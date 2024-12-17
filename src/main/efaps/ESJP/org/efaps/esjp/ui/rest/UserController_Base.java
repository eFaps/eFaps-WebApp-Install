/*
 * Copyright 2023 - 2020 The eFaps Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.efaps.esjp.ui.rest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.Company;
import org.efaps.admin.user.JAASSystem;
import org.efaps.admin.user.Person;
import org.efaps.admin.user.Person.AttrName;
import org.efaps.admin.user.Role;
import org.efaps.ci.CIAdmin;
import org.efaps.ci.CIAdminUser;
import org.efaps.db.Context;
import org.efaps.db.InstanceQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.eql.EQL;
import org.efaps.esjp.admin.common.systemconfiguration.KernelConfigurations;
import org.efaps.esjp.ui.rest.dto.CompanyDto;
import org.efaps.esjp.ui.rest.dto.UserDto;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

@EFapsUUID("001b5b92-6389-4a0b-9b57-0ee415f8bd8b")
@EFapsApplication("eFaps-WebApp")
public abstract class UserController_Base
{

    public static final String PREFERRED = "preferred_username";
    public static final String RESOURCE_ACCESS = "resource_access";
    public static final String ROLES = "roles";
    public static final String GIVEN_NAME = "given_name";
    public static final String FAMILY_NAME = "family_name";

    public static final String LOCALEKEY = "eFapsLocale";
    public static final String COMPANIES = "eFapsCompanies";
    public static final String TZKEY = "eFapsTimeZone";
    public static final String LANGKEY = "eFapsLanguage";

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    public Response getCurrent(final Application app,
                               final HttpHeaders headers,
                               final Boolean sync)
        throws EFapsException
    {
        if (sync != null && sync) {
            LOG.info("Received request to sync the User");
            syncUser(app, headers);
        }

        final var person = Context.getThreadContext().getPerson();
        final var currentCompanyId = Context.getThreadContext().getCompany().getId();

        final List<CompanyDto> companies = new ArrayList<>();

        for (final var companyId : person.getCompanies()) {
            final var oneCompany = Company.get(companyId);
            companies.add(CompanyDto.builder()
                            .withName(oneCompany.getName())
                            .withUuid(oneCompany.getUUID())
                            .withCurrent(currentCompanyId == companyId)
                            .build());
        }

        final var dto = UserDto.builder()
                        .withFirstName(person.getFirstName())
                        .withLastName(person.getLastName())
                        .withCompanies(companies)
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }

    public Response getCompanies()
        throws EFapsException
    {
        final var eval = EQL.builder().print().query(CIAdminUser.Company).select()
                        .attribute(CIAdminUser.Company.Name, CIAdminUser.Company.UUID)
                        .evaluate();
        final List<CompanyDto> companies = new ArrayList<>();
        while (eval.next()) {
            companies.add(CompanyDto.builder()
                            .withOid(eval.inst().getOid())
                            .withName(eval.get(CIAdminUser.Company.Name))
                            .withUuid(UUID.fromString(eval.get(CIAdminUser.Company.UUID)))
                            .build());
        }
        return Response.ok()
                        .entity(companies)
                        .build();
    }

    protected void syncUser(final Application app,
                            final HttpHeaders headers)
        throws EFapsException
    {
        final String audience = (String) app.getProperties().get("oidc.audience");
        if (audience == null) {
            LOG.warn("Cannot sync users due to missing 'oidc.audience'");
        } else {
            final var authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
            final var token = authHeader.replaceFirst("Bearer ", "");
            try {
                final var jwt = SignedJWT.parse(token);
                final var jwtClaimsSet = jwt.getJWTClaimsSet();
                if (validatePerson(jwtClaimsSet)) {
                    syncRoles(audience, jwtClaimsSet);
                    syncCompanies(jwtClaimsSet);
                    syncAttributes(jwtClaimsSet);
                }
            } catch (final ParseException e) {
                LOG.error("Could not parse token or claim", e);
            }
        }
    }

    /**
     * Validate if a person exists in the eFaps-Database. If it does not exists
     * and it is permitted via SytemConfiguration will create a basic Person.
     * Update of Attributes etc. must be done on syncAttributes.
     *
     * @param _userName the user name
     * @throws EFapsException on error
     * @throws ParseException
     */
    private boolean validatePerson(final JWTClaimsSet jwtClaimsSet)
        throws EFapsException, ParseException
    {
        LOG.trace("Steping into validatePerson");

        final Person person = getPerson(jwtClaimsSet.getSubject());
        boolean ret = false;
        if (person != null) {
            LOG.info("   existing person: {}", person.getName());
            ret = true;
        } else if (KernelConfigurations.SSO_PERMITCREATEPERSON.get()) {
            LOG.debug("{} is activated", KernelConfigurations.SSO_PERMITCREATEPERSON.getKey());
            final String userName = UUIDUtil.isUUID(jwtClaimsSet.getSubject())
                            ? jwtClaimsSet.getStringClaim(PREFERRED)
                            : jwtClaimsSet.getSubject();
            Person.createPerson(JAASSystem.getJAASSystem("eFaps"), userName, userName,
                            UUIDUtil.isUUID(jwtClaimsSet.getSubject()) ? jwtClaimsSet.getSubject() : null, true);
            ret = true;
            LOG.info("   newly created person: {}", userName);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private void syncRoles(final String audience,
                           final JWTClaimsSet jwtClaimsSet)
        throws EFapsException, ParseException
    {
        LOG.trace("Steping into syncRoles");
        if (KernelConfigurations.SSO_PERMITROLEUPDATE.get()) {
            LOG.debug("{} is activated", KernelConfigurations.SSO_PERMITROLEUPDATE.getKey());
            final var resource = jwtClaimsSet.getJSONObjectClaim(RESOURCE_ACCESS);
            LOG.info("   resource: {}", resource);
            final var resourceElement = resource.get(audience);
            if (resourceElement == null) {
                LOG.warn("Cannot sync roles due to missing 'resource_access' for {}", audience);
            } else {
                final var rolesList = ((Map<String, Object>) resourceElement).get(ROLES);
                if (rolesList != null && rolesList instanceof Collection) {
                    final Person person = getPerson(jwtClaimsSet.getSubject());
                    if (person != null) {
                        final Set<Role> roles = new HashSet<>();
                        for (final String roleStr : (Collection<String>) rolesList) {
                            final Role role;
                            if (UUIDUtil.isUUID(roleStr)) {
                                role = Role.get(UUID.fromString(roleStr));
                            } else {
                                role = Role.get(roleStr);
                            }
                            if (role != null) {
                                roles.add(role);
                            }
                        }
                        final JAASSystem jaasSystem = JAASSystem.getJAASSystem("eFaps");
                        person.setRoles(jaasSystem, roles);
                    }
                }
            }
        }
    }

    private void syncCompanies(final JWTClaimsSet jwtClaimsSet)
        throws EFapsException, ParseException
    {
        LOG.trace("Steping into syncCompanies");
        if (KernelConfigurations.SSO_PERMITCOMPANYUPDATE.get()) {
            LOG.debug("{} is activated", KernelConfigurations.SSO_PERMITCOMPANYUPDATE.getKey());

            final var companyList = jwtClaimsSet.getStringListClaim(COMPANIES);

            if (companyList != null) {

                final Person person = getPerson(jwtClaimsSet.getSubject());
                if (person != null) {
                    final Set<Company> companies = new HashSet<>();
                    for (final String companyStr : companyList) {
                        final Company company;
                        if (UUIDUtil.isUUID(companyStr)) {
                            company = Company.get(UUID.fromString(companyStr));
                        } else {
                            company = Company.get(companyStr);
                        }
                        if (company != null) {
                            companies.add(company);
                        }
                    }
                    final JAASSystem jaasSystem = JAASSystem.getJAASSystem("eFaps");
                    person.setCompanies(jaasSystem, companies);
                }
            }
        }
    }

    private void syncAttributes(final JWTClaimsSet jwtClaimsSet)
        throws EFapsException, ParseException
    {
        LOG.trace("Steping into syncAttributes");
        if (KernelConfigurations.SSO_PERMITATTRIBUTEUPDATE.get()) {
            LOG.debug("{} is activated", KernelConfigurations.SSO_PERMITATTRIBUTEUPDATE.getKey());
            final Person person = getPerson(jwtClaimsSet.getSubject());
            if (person != null) {
                boolean update = false;
                final var givenName = jwtClaimsSet.getStringClaim(GIVEN_NAME);
                if (StringUtils.isNotEmpty(givenName) && !person.getFirstName().equals(givenName)) {
                    person.updateAttrValue(AttrName.FIRSTNAME, givenName);
                    update = true;
                }

                final var familyName = jwtClaimsSet.getStringClaim(FAMILY_NAME);
                if (StringUtils.isNotEmpty(familyName) && !person.getLastName().equals(familyName)) {
                    person.updateAttrValue(AttrName.LASTNAME, familyName);
                    update = true;
                }

                final var localeTag = jwtClaimsSet.getStringClaim(LOCALEKEY);
                LOG.debug("{}: is is set with {}", LOCALEKEY, localeTag);
                if (StringUtils.isNotEmpty(localeTag) && !person.getLocale().toLanguageTag().equals(localeTag)
                                && Locale.forLanguageTag(localeTag) != null) {
                    person.updateAttrValue(AttrName.LOCALE, localeTag);
                    update = true;
                }
                final var tzStr = jwtClaimsSet.getStringClaim(TZKEY);
                if (StringUtils.isNotEmpty(tzStr)) {
                    LOG.debug("{}: is is set with {}", TZKEY, tzStr);
                    final TimeZone tz = TimeZone.getTimeZone(tzStr);
                    if (!person.getTimeZone().getID().equals(tzStr) && tz != null) {
                        person.updateAttrValue(AttrName.TIMZONE, tzStr);
                        update = true;
                    }
                }
                final String lang = jwtClaimsSet.getStringClaim(LANGKEY);
                if (StringUtils.isNotEmpty(lang) && !person.getLanguage().equals(lang)) {
                    LOG.debug("{}: is is set with {}", LANGKEY, lang);
                    final QueryBuilder queryBldr = new QueryBuilder(CIAdmin.Language);
                    queryBldr.addWhereAttrEqValue(CIAdmin.Language.Language, lang);
                    final InstanceQuery query = queryBldr.getQuery();
                    query.executeWithoutAccessCheck();
                    if (query.next()) {
                        person.updateAttrValue(AttrName.LANGUAGE, String.valueOf(query.getCurrentValue().getId()));
                        update = true;
                    }
                }
                if (update) {
                    person.commitAttrValuesInDB();
                }
            }
        }
    }

    private Person getPerson(final String _userName)
        throws EFapsException
    {
        final Person person;
        if (UUIDUtil.isUUID(_userName)) {
            person = Person.get(UUID.fromString(_userName));
        } else {
            person = Person.get(_userName);
        }
        return person;
    }
}

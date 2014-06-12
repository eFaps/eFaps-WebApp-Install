/*
 * Copyright 2003 - 2012 The eFaps Team
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
 * Author:          The eFaps Team
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

/*
 * @eFapsPackage  org.efaps.ui.wicket.behaviors
 * @eFapsUUID     9dae1678-6945-4141-bbe9-25d916250ae3
 * @eFapsRevision $Rev$
 */

function eFapsSetFieldValue(_referenceIdOrIdx, _fieldName, _fieldValue, _fieldLabel) {

    require([ 'dojo/query', 'dojo/dom', 'dijit/registry',"efaps/AutoComplete"], function(query, dom, registry, AutoComplete) {
        var pos = 0;
        if (typeof (_referenceIdOrIdx) == 'number') {
            pos = _referenceIdOrIdx;
        } else {
            // get the position in the field collection of the given reference field
            var refField = dom.byId(_referenceIdOrIdx);
            var name = refField.getAttribute('name');
            if (name == null) {
                var widget = registry.byId(_referenceIdOrIdx)
                if (typeof(widget) !== "undefined") {
                    name = widget.name;
                }
            }
            var i = 0;
            query("*[name=" + name + "]").forEach(function(node) {
                // if the node does not have an id check if his siblings has the one we search
                if (node.id ==='') {
                    var el = node;
                    while (el) {
                        if (el.id == _referenceIdOrIdx) {
                            pos = i;
                            break;
                        }
                        el = el.previousSibling;
                    }
                } else if (node.id == _referenceIdOrIdx) {
                    pos = i;
                }
                i++;
            });
        }
        // get the field collection
        var fields = query("*[name=" + _fieldName + "]")
        // only if the field exist go on
        if (fields.length > 0) {
            var cp = 0;
            if (fields.length > 1) {
                var cp = pos;
            }
            if (_fieldValue instanceof Array) {
                var sel = fields[cp];
                if (sel.nodeName == 'SELECT') {
                } else {
                    sel = document.createElement('SELECT');
                    for (i = 0; i < fields[cp].attributes.length; i++) {
                        if (sel.getAttributeNode(fields[cp].attributes[i].name) == null) {
                            sel.setAttribute(fields[cp].attributes[i].name, fields[cp].attributes[i].value);
                        }
                    }
                    sel.size = 1;
                    fields[cp].parentNode.replaceChild(sel, fields[cp]);
                }
                while (sel.options.length) {
                    sel.options[0] = null;
                }
                for (i = 1; i < _fieldValue.length; i = i + 2) {
                    option = new Option(_fieldValue[i + 1], _fieldValue[i], false, _fieldValue[i] == _fieldValue[0]);
                    sel.options[sel.length] = option;
                }
            } else if (typeof(_fieldLabel) !== "undefined") { // that means that a dojo widget must be search
                var parentWidget = registry.getEnclosingWidget(fields[cp]);
                if (parentWidget != null && typeof(parentWidget) !== "undefined") {
                    if (parentWidget.isInstanceOf(AutoComplete)) {
                        // deactivate the onchange event
                        parentWidget.set('_onChangeActive', false);
                        parentWidget.set("item",{id: _fieldValue, name:_fieldLabel, label: _fieldLabel});
                        // reactivate the onchange event
                        parentWidget.set('_onChangeActive',true);
                    }
                }
            } else {
                // if it is an input, the value can be set directly, else the DOM must be used
                if (fields[cp].nodeName == 'INPUT' || fields[cp].nodeName == 'TEXTAREA'
                        || fields[cp].nodeName == 'SELECT') {
                    fields[cp].value = _fieldValue;
                } else {
                    if (fields[cp].hasChildNodes()) {
                        fields[cp].firstChild.data = _fieldValue;
                    } else {
                        var n = document.createTextNode(_fieldValue);
                        fields[cp].appendChild(n);
                    }
                }
            }
        }
    });
}

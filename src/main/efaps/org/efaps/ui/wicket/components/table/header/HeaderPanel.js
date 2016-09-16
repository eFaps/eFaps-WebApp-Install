/*
 * Copyright 2003 - 2016 The eFaps Team
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

/*
 * @eFapsPackage  org.efaps.ui.wicket.components.table.header
 * @eFapsUUID      62934dc5-42b0-48eb-8e5a-57e6c41a77a8
 * @eFapsApplication eFaps-WebApp
 */

var minWidth = 20;
var seperatorWidth = 200;
var lastpos = 0;
var startpos = 0;
var seperatorOffset = 0;
var connections = [];
var seperator;

// value is used to reduce the evaluated table with to ensure that the columns will fit
var widthCorrection = 5;
// the with that will be substracted from the complete with in case that there is a scrollbar
var scrollBarWidth = 20;

function headerProperties() {
    this.headerID = "";
    this.bodyID = "";
    this.modelID = "";
    this.storeColumnWidths = "";
    this.storeColumnOrder = "";
    this.reloadTable = "";
    this.tableName = "";
}

//function used to retrieve a Style Rule from a StyleSheet
function getStyleRule(_styleIndex, _modelID) {
  var selectorName = ".eFapsCellWidth" + _styleIndex;
  for (i = 0; i < document.styleSheets.length; i++) {
    var find = document.styleSheets[i].cssRules[0].cssText.indexOf("eFapsCSSId"+ _modelID)
    if(find > -1){
      for (j = 0; j < document.styleSheets[i].cssRules.length; j++) {
        if (document.styleSheets[i].cssRules[j].selectorText == selectorName) {
          return document.styleSheets[i].cssRules[j];
        }
      }
    }
  }
}

function positionTableColumns(_props) {
    require(["dojo/query", "dojo/dom","dojo/dom-geometry","dojo/dom-style","dojo/NodeList-dom"], function(query,dom,domGeom,style){
        var header = dom.byId(_props.headerID);
        var table = dom.byId(_props.bodyID);
        var completeWidth = domGeom.position(table, false).w;
        // in rare cases the calculation of the browser is wrong, this solves the problem
        var parentCompleteWidth = domGeom.position(table.parentNode, false).w;
        if (completeWidth > parentCompleteWidth) {
            completeWidth = parentCompleteWidth;
        }
        var widthWeight = 0;
        var calcWidth = 0;
        // for a structurbrowser there will be a scrollbar in case that the content expands
        if (table.clientHeight < table.scrollHeight) {
            completeWidth = completeWidth - scrollBarWidth;
        }

        var nl = query("> div", header);
        nl.forEach(function(node){
            var fixed = node.className.indexOf("eFapsCellFixedWidth");
            var computedStyle = style.getComputedStyle(node);
            var output = domGeom.getContentBox(node, computedStyle).w;
            var marginBoxWidth = domGeom.getMarginSize(node, computedStyle).w;
            if(fixed == -1){
                calcWidth = calcWidth - Math.round(output) + Math.round(marginBoxWidth);
                widthWeight = widthWeight + Math.round(output);
            } else {
                calcWidth = calcWidth + Math.round(marginBoxWidth);
            }
        });
        var styleIndex = 0;
        nl.forEach(function(node){
            var fixed = node.className.indexOf("eFapsCellFixedWidth");
            var selectorName = ".eFapsCellWidth" + (styleIndex + _props.modelID);
            if(fixed == -1){
                var computedStyle = style.getComputedStyle(node);
                var output = domGeom.getContentBox(node, computedStyle).w;
                var rule = getStyleRule(styleIndex + _props.modelID, _props.modelID);
                cellWidth = Math.round((100/widthWeight * Math.round(output)/100)* (completeWidth - calcWidth - widthCorrection));
                rule.style.width= cellWidth + "px";
            }
            styleIndex++;
        })
        var k = 0;
        nl.forEach(function(node){
            var fixed = node.className.indexOf("eFapsCellFixedWidth");
            if(fixed == -1 && k+1 < nl.length){
                var xpos = domGeom.position(nl[k+1]).x;
                sep = dom.byId((k + _props.modelID) + "eFapsHeaderSeperator");
                if (sep != null) {
                    style.set(sep, "left", xpos + "px");
                }
            }
            k++;
        });
    });
}

function getColumnWidths(_props) {
    var widths = "";
    require([ "dojo/query", "dojo/dom", "dojo/dom-geometry", "dojo/dom-style" ],
            function(query, dom, domGeom, style) {
                var header = dom.byId(_props.headerID);
                var nl = query("> div", header);
                nl.forEach(function(node) {
                    computedStyle = style.getComputedStyle(node);
                    widths += domGeom.getContentBox(node, computedStyle).w + "px;";
                });
            });
    return widths;
}

function addMoveable(_sepId, _props) {
    require(["dojo/dnd/move","dojo/dom","dojo/dom-geometry","dojo/dom-style","dojo/dom-class"], function(move, dom, domGeom, domStyle, domClass) {
        var x = 0;
        //Create your "Moveable" as
        var ma = new move.constrainedMoveable(_sepId, {
            constraints: function() {
                            var n = dom.byId(_props.headerID),
                            s = domStyle.getComputedStyle(n),
                            mb = domGeom.getMarginBox(n, s);
                            mb.h=0;
                            return mb;
                        },
            within: false
        });

        ma.onMoveStart = function() {
            x = domGeom.position( this.node).x;
            domClass.add(this.node, "eFapsMove");
        }
        ma.onMoveStop = function() {
            dif = domGeom.position(this.node).x - x;
            domClass.remove(this.node, "eFapsMove");
            tableId = parseInt(this.node.id);
            leftrule = getStyleRule(tableId, _props.modelID);
            rightrule = getStyleRule(tableId+1, _props.modelID);
            leftWidth = parseInt(leftrule.style.width);
            rightWidth = parseInt(rightrule.style.width);
            move = 0;
            if (leftWidth + dif > minWidth && rightWidth - dif > minWidth) {
                leftrule.style.width = leftWidth + dif +"px";
                rightrule.style.width = rightWidth - dif +"px";
            } else {
                if (dif < 0) {
                    leftrule.style.width = minWidth +"px";
                    rightrule.style.width = rightWidth + leftWidth - minWidth + "px";
                    move = (leftWidth + dif-minWidth);
                } else {
                    rightrule.style.width = minWidth +"px";
                    leftrule.style.width = leftWidth + rightWidth - minWidth + "px";
                    move = -(rightWidth - dif - minWidth);
                }
                this.node.style.left = parseInt(this.node.style.left) - move +"px";
            }
            _props.storeColumnWidths(getColumnWidths(_props));
        }
    });
}

function getColumnOrder(_props) {
    var ids = "";
    require([ "dojo/query", "dojo/dom" ], function(query, dom) {
        var header = dom.byId(_props.headerID);
        var nl = query("div", header);
        nl.forEach(function(node) {
            ids += node.id + ";";
        });
    });
    return ids;
}

function addOnResizeEvent(func) {
  var oldonload = window.onresize;
  if (typeof window.onresize != 'function') {
    window.onresize = func;
  } else {
    window.onresize = function() {
      oldonload();
      func();
    }
  }
}

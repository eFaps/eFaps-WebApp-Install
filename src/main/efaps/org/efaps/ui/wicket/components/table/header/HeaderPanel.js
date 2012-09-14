/*
 * Copyright 22003 - 2012 The eFaps Team
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
 * Author:          jmox
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

/*
 * @eFapsPackage  org.efaps.ui.wicket.components.table.header
 * @eFapsUUID      62934dc5-42b0-48eb-8e5a-57e6c41a77a8
 * @eFapsRevision $Rev$
 */

var minWidth = 20;
var seperatorWidth = 200;
var lastpos = 0;
var startpos = 0;
var seperatorOffset = 0;
var connections = [];
var seperator;



function headerProperties(){
 this.headerID = "";
 this.bodyID = "";
 this.modelID = "";
 this.storeColumnWidths ="";
 this.storeColumnOrder = "";
 this.reloadTable = "";
}

function positionTableColumns(_props) {
    require([ "dojo/dom", "dojo/dom-geometry", "dojo/dom-style" ],
        function(dom, domGeom, style) {
            var header = dom.byId(_props.headerID);
            var cells = new Array();
            var widthWeight = 0;
            var celldivs = header.getElementsByTagName("div");
            for (i = 0; i < celldivs.length; i++) {
                var cell = celldivs[i];
                var cS = style.getComputedStyle(cell);
                width = domGeom.position(cell).w;
                widthWeight += domGeom.getMarginSize(cell).w + domGeom.getPadBorderExtents(cell).w;
                cells.push(new Array(cell, width, cell.className.indexOf("eFapsCellWidth") > -1));
            }
            var tablebody = dom.byId(_props.bodyID);
            var completeWidth = domGeom.position(tablebody.children[0]).w;

            if (completeWidth != 0) {
                header.style.width = completeWidth + "px";
                for (k = 0; k < cells.length; k++) {
                    if (cells[k][2] == true) {
                        var rule = getStyleRule(k + _props.modelID, _props.modelID);
                        var cellWidth = Math.round(((cells[k][1] / widthWeight) * completeWidth) - 1);
                        rule.style.width = cellWidth + "px";

                        if (k + 1 < cells.length) {
                            if (dom.byId((k + _props.modelID) + "eFapsHeaderSeperator")) {
                                var sep = dom.byId((k + _props.modelID) + "eFapsHeaderSeperator");
                                sep.style.left = domGeom.position(cells[k + 1][0]).x - domGeom.position(sep).w / 2 + "px";
                            }
                        }
                    }
                }
            }
        });
}


// function used to retrieve a Style Rule from a StyleSheet
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


function getColumnWidths(_props){
  var header = document.getElementById(_props.headerID);
  var celldivs = header.getElementsByTagName("div");
  var widths="";
  for(i = 0;i<celldivs.length;i++){
    if(celldivs[i].className.indexOf("eFapsCellFixedWidth") > -1 ||
         celldivs[i].className.indexOf("eFapsCellWidth") > -1){
      widths += window.getComputedStyle(celldivs[i],null).getPropertyValue("width") +";";
    }
  }
  return widths;
}

function beginColumnSize(_seperator,_event){
  lastpos = _event.screenX;
  startpos = lastpos;
  seperator = _seperator;
  seperatorOffset = _event.screenX - parseInt(_seperator.style.left);
  _seperator.style.width = parseInt(window.getComputedStyle(_seperator,null).getPropertyValue("width")) + seperatorWidth +"px";
  _seperator.style.left = parseInt(_seperator.style.left) - seperatorWidth/2 +"px";
  _seperator.style.backgroundPosition="top center";
  connections[0] = dojo.connect(_seperator,"onmousemove",this,  "doColumnSize" );
  connections[1] = dojo.connect(_seperator,"onmouseout",this,  "cancelColumnSize" );
}


function doColumnSize(_event){
  seperator.style.left= (_event.screenX-seperatorOffset) - seperatorWidth/2 +"px";
  lastpos=_event.screenX;
}

function endColumnSize(_seperator,_event,_props){
  dojo.forEach(connections,dojo.disconnect);
  var dif = lastpos - startpos;
  var i = parseInt(seperator.id);
  var leftrule = getStyleRule(i, _props.modelID);
  var rightrule = getStyleRule(i+1, _props.modelID);
  var leftWidth = parseInt(leftrule.style.width);
  var rightWidth = parseInt(rightrule.style.width);
  var move = 0;
  if(leftWidth + dif > minWidth && rightWidth - dif > minWidth){
    leftrule.style.width = leftWidth + dif +"px";
    rightrule.style.width = rightWidth - dif +"px";
  } else {
    if(dif < 0){
      leftrule.style.width = minWidth +"px";
      rightrule.style.width = rightWidth + leftWidth - minWidth + "px";
      move = (leftWidth + dif-minWidth);
    } else {
      rightrule.style.width = minWidth +"px";
      leftrule.style.width = leftWidth + rightWidth - minWidth + "px";
      move = -(rightWidth - dif - minWidth);
    }
  }
  _seperator.style.width = parseInt(_seperator.style.width) - seperatorWidth +"px";
  _seperator.style.left = parseInt(_seperator.style.left) - move + seperatorWidth/2 +"px";
  _seperator.style.backgroundPosition = "-200px 0";
  _props.storeColumnWidths(getColumnWidths(_props));
}


function cancelColumnSize(_event){
  endColumnSize(seperator,_event);
}

function getColumnOrder(props){
  var header = document.getElementById(props.headerID);
  var celldivs = header.getElementsByTagName("div");
  var ids="";
  for(i = 0;i<celldivs.length;i++){
    if(celldivs[i].className.indexOf("eFapsCellFixedWidth") > -1 ||
              celldivs[i].className.indexOf("eFapsCellWidth") > -1){
      ids += celldivs[i].id + ";";
    }
  }
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

function forEachIn(obj, callback) {
    for (key in obj) {
        if (obj.hasOwnProperty(key)) {
            callback(key, obj[key]);
        }
    }
}


var _pageItems = [];
var _selectedIds = [];
function removeItemFromSelectedList(id) {
    for (var i = 0; i < _selectedIds.length; i++) {
        if (_selectedIds[i] == id) {
            _selectedIds.splice(i, 1);
        }
    }
}

function distanceInsideRect(area, x, y) {
    if (x >= area[0] && x <= area[0] + area[2]
        && y >= area[1] && y <= area[1] + area[3]) {

        var distance = Math.min(x - area[0], area[0] + area[2] - x)
            + Math.min(y - area[1], area[1] + area[3] - y);
        return distance;
    }
    else return -1;
}
function sortItemsByName(items) {
    for (var i=0; i<items.length - 1; i++) {
        for(var j = i+1; j<items.length; j++) {
            if (items[i].name  > items[j].name) {
                var temp = items[i];
                items[i] = items[j];
                items[j] = temp;
            }
        }
    }
    return items;
}
function initGalenPageDump(pageData) {

    var canvas = $(".image .canvas");

    for (objectName in pageData.items) {
        if (pageData.items.hasOwnProperty(objectName)) {
            var item = pageData.items[objectName];
            item.name = objectName;
            item.id = _pageItems.length;
            item.selected = false;
            _pageItems.push(item);
        }
    }

    //sorting items by name
    var sortedItems = sortItemsByName(_pageItems);
    for (var i = 0; i<sortedItems.length; i++) {
        sortedItems[i].id = i;
        renderPageItem(canvas, sortedItems[i]); 
    }

    canvas.click(function (event) {
        var offset = $(this).offset();
        var x = event.pageX - offset.left;
        var y = event.pageY - offset.top;

        //Finding the closest to the rect

        var minDistance = 1000000;
        var selectedId = -1;
        for (var i = 0; i < _pageItems.length; i++) {
           var distance = distanceInsideRect(_pageItems[i].area, x, y);
           if (distance > -1 && distance < minDistance) {
               selectedId = i;
               minDistance = distance;
           }
        }

        if (selectedId >=0 ) {
            selectItem(selectedId);
        }
    });
}



function selectItem(id) {
    
    _pageItems[id].selected = !_pageItems[id].selected;
    var item = _pageItems[id];

    var $item = $("#canvas-brick-" + item.id);
    var $listItem = $("#object-list-item-" + item.id);

    if (item.selected) {
        _selectedIds.push(id);

        $item.addClass("selected");
        $listItem.addClass("selected");

    } else {
        removeItemFromSelectedList(id);
        $item.removeClass("selected");
        $listItem.removeClass("selected");
    }

    if (_selectedIds.length >= 2) {
        showSpecSuggestions(_selectedIds);
    }
    else if (_selectedIds.length == 1) {
        showObjectDetails(_pageItems[_selectedIds[0]]);
        hideSpecSuggestions();
    }
    else if (_selectedIds.length == 0) {
        hideObjectDetails();
        hideSpecSuggestions();
    }
}

function hideSpecSuggestions() {
    $("#object-suggestions").hide();
}

function Point(x, y) {
    this.x = x;
    this.y = y;
}
Point.prototype.isInsideArea = function (area) {
    return this.x >= area[0] && this.x < area[0] + area[2]
        && this.y >= area[1] && this.y < area[1] + area[3];
}
function areaToPoints(area) {
    return [
        new Point(area[0], area[1]),
        new Point(area[0] + area[2], area[1]),
        new Point(area[0] + area[2], area[1] + area[3]),
        new Point(area[0], area[1] + area[3])
    ];
}

function Spec(objectName, specText) {
    this.objectName = objectName;
    this.specText = specText;
}

function __galen_textForLocationsOfInsideSpec(itemA, itemB) {
    var sides = {
        top: itemA.area[1] - itemB.area[1],
        left: itemA.area[0] - itemB.area[0],
        right: itemB.area[0] + itemB.area[2] - itemA.area[0] - itemA.area[2],
        bottom: itemB.area[1] + itemB.area[3] - itemA.area[1] - itemA.area[3]
    }
    
    var text = "";
    var haveFirst = false;
    forEachIn(sides, function (sideName, value) {
        if (value > 0) {
            if (haveFirst) {
                text = text + ",";
            }
            haveFirst = true;

            text = text + " " + value + "px " + sideName;
        }
    });

    return text;
}
function Suggest() {
    this.specs = {};
}
Suggest.prototype.addSpec = function (spec) {
    if (this.specs[spec.objectName] == undefined) {
        this.specs[spec.objectName] = [spec];   
    }
    else this.specs[spec.objectName].push(spec);
};
Suggest.prototype.generateSuggestions = function (items) {
    var thisSuggest = this;
    var propose = function(spec) {
        if (spec != null) {
            thisSuggest.addSpec(spec);
        }
    };
    forEachIn(this._suggestions, function (name, suggestion) {
        for ( var i =0; i < items.length - 1; i++) {
            for (var j = i + 1; j < items.length; j++) {
                propose(suggestion(items[i], items[j]));
                propose(suggestion(items[j], items[i]));
            }
        }
    });

    var html = "";

    for (var objectName in this.specs) {
        if (this.specs.hasOwnProperty(objectName)) {
            html += "\n<b>" + objectName  + ":</b>\n";
            var specs = this.specs[objectName];
            for (var i = 0; i < specs.length; i++) {
                html += "    " + specs[i].specText + "\n";
            }
        }
    }

    return "<pre>" + html + "</pre>";
};
Suggest.prototype._suggestions = {};
Suggest.prototype._suggestions.width = function(a, b) {
           return new Spec(a.name, "width " + getPercentage(a.area[2], b.area[2]) + "% of " + b.name+"/width");
   };
Suggest.prototype._suggestions.height = function(a, b) {
          return new Spec(a.name, "height " + getPercentage(a.area[3], b.area[3]) + "% of " + b.name+"/height");
   };

function getPercentage(a, b) {
    var value = (a / b) * 100;
    return parseFloat(value.toPrecision(3));
}

Suggest.prototype._suggestions.inside = function (itemA, itemB){
    var points = areaToPoints(itemA.area); 
    for (var i = 0; i<points.length; i++) {
        if (!points[i].isInsideArea(itemB.area)) {
            return null;
        }
    }
    return new Spec(itemA.name, "inside " + itemB.name + " " + __galen_textForLocationsOfInsideSpec(itemA, itemB));
};
Suggest.prototype._suggestions.insidePartly = function (itemA, itemB){
    var points = areaToPoints(itemA.area); 

    var amountOfInsidePoints = 0;
    for (var i = 0; i<points.length; i++) {
        if (points[i].isInsideArea(itemB.area)) {
            amountOfInsidePoints++;
        }
    }

    if (amountOfInsidePoints > 0 && amountOfInsidePoints < 4) {
        return new Spec(itemA.name, "inside partly " + itemB.name + " " + __galen_textForLocationsOfInsideSpec(itemA, itemB));
    }
    return null;
};
Suggest.prototype._suggestions.alignedHorizontally = function (itemA, itemB) {
    var dTop = Math.abs(itemA.area[1] - itemB.area[1]); 
    var dBottom = Math.abs(itemA.area[1] + itemA.area[3] - itemB.area[1] - itemB.area[3]); 

    if (dTop < 5 && dBottom < 5) {
        var errorRate = "";
        if (dTop > 0 || dBottom > 0) {
            errorRate = " " + Math.max(dTop, dBottom) + "px";
        }
        return new Spec(itemA.name, "aligned horizontally all " + itemB.name + errorRate);
    }
    else if (dTop < 5) {
        var errorRate = "";
        if (dTop > 0) {
            errorRate = " " + dTop + "px";
        }
        return new Spec(itemA.name, "aligned horizontally top " + itemB.name + errorRate);
    }
    else if (dBottom < 5) {
        var errorRate = "";
        if (dBottom > 0) {
            errorRate = " " + dBottom + "px";
        }
        return new Spec(itemA.name, "aligned horizontally bottom " + itemB.name + errorRate);
    }
    return null;
};
Suggest.prototype._suggestions.alignedVertically = function (itemA, itemB) {
    var dLeft = Math.abs(itemA.area[0] - itemB.area[0]); 
    var dRight = Math.abs(itemA.area[0] + itemA.area[2] - itemB.area[0] - itemB.area[2]); 

    if (dLeft < 5 && dRight < 5) {
        var errorRate = "";
        if (dLeft > 0 || dRight > 0) {
            errorRate = " " + Math.max(dLeft, dRight) + "px";
        }
        return new Spec(itemA.name, "aligned vertically all " + itemB.name + errorRate);
    }
    else if (dLeft < 5) {
        var errorRate = "";
        if (dLeft > 0) {
            errorRate = " " + dLeft + "px";
        }
        return new Spec(itemA.name, "aligned vertically left " + itemB.name + errorRate);
    }
    else if (dRight < 5) {
        var errorRate = "";
        if (dRight > 0) {
            errorRate = " " + dRight + "px";
        }
        return new Spec(itemA.name, "aligned vertically right " + itemB.name + errorRate);
    }
    return null;
};
Suggest.prototype._suggestions.leftOf = function (a, b) {
    var diff = b.area[0] - a.area[0] - a.area[2];
    if (diff >= 0) {
        return new Spec(a.name, "left-of " + b.name + " " + diff + "px");
    }
    return null;
};
Suggest.prototype._suggestions.rightOf = function (a, b) {
    var diff = a.area[0] - b.area[0] - b.area[2];
    if (diff >= 0) {
        return new Spec(a.name, "right-of " + b.name + " " + diff + "px");
    }
    return null;
};
Suggest.prototype._suggestions.above = function (a, b) {
    var diff = b.area[1] - a.area[1] - a.area[3];
    if (diff >= 0) {
        return new Spec(a.name, "above " + b.name + " " + diff + "px");
    }
    return null;
};
Suggest.prototype._suggestions.below = function (a, b) {
    var diff = a.area[1] - b.area[1] - b.area[3];
    if (diff >= 0) {
        return new Spec(a.name, "below " + b.name + " " + diff + "px");
    }
    return null;
};

Suggest.prototype._suggestions.centered = function (a, b) {
    //centered all inside
    //centered hor in
    //cent v in
    //cent hor on
    //cent v on
    var dt = a.area[1] - b.area[1];
    var db = b.area[1] + b.area[3] - a.area[1] - a.area[3];

    var dl = a.area[0] - b.area[0];
    var dr = b.area[0] + b.area[2] - a.area[0] - a.area[2];

    var similar = function (value1, value2) {
        return Math.abs(value1 - value2) < 5;
    };

    var errorRate = function (diff) {
        var absDiff = Math.abs(diff);
        if (absDiff > 0) {
            return " " + absDiff + "px";
        }
        return "";
    };

    if (similar(dt, db) && dt > 0 ) {
        if (similar(dl, dr) && dr > 0) {
            return new Spec(a.name, "centered all inside " + b.name + errorRate(Math.max(Math.abs(dt - db), Math.abs(dl - dr))));
        }
        else {
            return new Spec(a.name, "centered vertically inside " + b.name + errorRate(dt - db));
        }
    }
    else if (similar(dr, dl) && dr > 0) {
        return new Spec(a.name, "centered horizontally inside " + b.name + errorRate(dl - dr));
    }
    else if (similar(dt, db) && dt < 0) {
        return new Spec(a.name, "centered vertically on " + b.name + errorRate(dt - db));
    }
    else if (similar(dr, dl) && dr < 0) {
        return new Spec(a.name, "centered horizontally on " + b.name + errorRate(dr - dl));
    }
    return null;
};

function showSpecSuggestions(selectedIds) {

    var suggest = new Suggest();

    var items = [];

    for (var i=0; i<selectedIds.length; i++) {
        items.push(_pageItems[selectedIds[i]]);
    }

    $("#object-suggestions .spec-list").html(suggest.generateSuggestions(items));
    $("#object-suggestions").show();
}

function showObjectDetails(item) {
    $("#object-details .xf-object-name").text(item.name);
    $("#object-details .xf-object-area").text(item.area);

    if (item.hasImage) {
        $("#object-details .image").html("<img src='objects/" + item.name + ".png'/>").show();
    }
    else $("#object-details .image").hide();

    $("#object-details").show();    
}

function hideObjectDetails() {
    $("#object-details").hide();    
}

function renderPageItem(canvas, item) {
    $("#object-list ul").append($("<li id='object-list-item-" + item.id + "' data-id='" + item.id + "'>" + item.name + "</li>").click(function () {
        var id = $(this).attr("data-id");
        selectItem(id);
    }));

    canvas.append("<div class='brick' id='canvas-brick-" + item.id + "' "
        + "style='"
        + "left:" + item.area[0] + "px;"
        + "top:" + item.area[1] + "px;"
        + "width:" + item.area[2] + "px;"
        + "height:" + item.area[3] + "px;"
        + "'>" 
        + "<span>" + item.name + "</span>"
        + "</div>");
}

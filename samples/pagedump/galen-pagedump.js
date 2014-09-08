
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
function initGalenPageDump(pageData) {

    var canvas = $(".image .canvas");
    for (objectName in pageData.items) {
        if (pageData.items.hasOwnProperty(objectName)) {
            var item = pageData.items[objectName];
            item.name = objectName;
            item.id = _pageItems.length;
            item.selected = false;
            _pageItems.push(item);
            renderPageItem(canvas, item); 
        }
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

    if (_selectedIds.length == 2) {
        showSpecSuggestions(_pageItems[_selectedIds[0]], _pageItems[_selectedIds[1]]);
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
function Suggest() {
    this.specs = [];
}
Suggest.prototype.addSpec = function (objectName, specText) {
    this.specs.push(new Spec(objectName, specText));
}
Suggest.prototype.inside = function (itemA, itemB){
    var points = areaToPoints(itemA.area); 
    for (var i = 0; i<points.length; i++) {
        if (!points[i].isInsideArea(itemB.area)) {
            return;
        }
    }
    var top = itemA.area[1] - itemB.area[1];
    var left = itemA.area[0] - itemB.area[0];
    var right = itemB.area[0] + itemB.area[2] - itemA.area[0] - itemA.area[2];
    var bottom = itemB.area[1] + itemB.area[3] - itemA.area[1] - itemA.area[3];

    this.addSpec(itemA.name, "inside: " + itemB.name + " " + top + "px top, " + left + "px left, " + right + "px right ", + bottom + "px bottom");
};
Suggest.prototype.generateSuggestions = function () {
    var html = "";

    for (var i = 0; i < this.specs.length; i++) {
        html += "<b>" + this.specs[i].objectName + "</b>";
        html += "<br/>" + this.specs[i].specText;
    }

    return html;
}

function showSpecSuggestions(itemA, itemB) {

    var suggest = new Suggest();
    suggest.inside(itemA, itemB);
    suggest.inside(itemB, itemA);
    /*suggest.insidePartly(itemA, itemB);
    suggest.insidePartly(itemB, itemA);
    suggest.aligned(itemA, itemB);
    suggest.nearLeft(itemA, itemB);
    suggest.nearRight(itemA, itemB);
    suggest.above(itemA, itemB);
    suggest.below(itemA, itemB);
    suggest.centeredInside(itemA, itemB);
    suggest.centeredInside(itemB, itemA);
    suggest.centeredOn(itemA, itemB);
    suggest.centeredOn(itemB, itemA);*/

    $("#object-suggestions .spec-list").html(suggest.generateSuggestions());
    $("#object-suggestions").show();
}

function showObjectDetails(item) {
    $("#object-details .xf-object-name").text(item.name);
    $("#object-details .xf-object-area").text(item.area);
    $("#object-details .xf-object-text").text(item.text);

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

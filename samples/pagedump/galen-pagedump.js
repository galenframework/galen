
var _pageItems = [];
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
            selectItem(_pageItems[selectedId]);
        }
    });
}

function selectItem(item) {
    $("#canvas-brick-" + item.id).addClass("selected");
}

function renderPageItem(canvas, item) {
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

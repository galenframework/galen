$.fn.center = function () {
    this.css("position","absolute");
    this.css("top", Math.max(0, (($(window).height() - $(this).outerHeight()) / 2) + $(window).scrollTop()) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + $(window).scrollLeft()) + "px");
    return this;
}


$(function() {
    $("body").append("<div id='tooltip'><a id='close-tooltip' href='#'>Close</a><div id='tooltip-body'></div></div>");

    $("#close-tooltip").click(function (){
        $("#tooltip").hide();
    });

    $("ul.test-specs li.fail").click(function () {
        var screenshot = $(this).attr("data-screenshot");
        var img = new Image();
        
        var areas = [];
        $(this).find("ul.areas li").each(function (){
            var areaText = $(this).attr("data-area");
            areas[areas.length] = {
                area: eval("[" + areaText + "]"),
                text: $(this).html()
            };
        });

        img.onload = function() {
            showScreenshot(img, this.width, this.height, areas);
        };
        img.src = screenshot;
    });
});

function showScreenshot(img, width, height, areas) {
    $("#tooltip-body").html("<div class='canvas'></div>").append(img);

    for (var i=0; i<areas.length; i++) {
        $("#tooltip-body .canvas").append("<div class='brick' style='left:" 
            + areas[i].area[0] + "px; top:" 
            + areas[i].area[1] + "px; width:" 
            + areas[i].area[2] + "px; height:" 
            + areas[i].area[3] + "px'><span>" 
            + areas[i].text + "</span></div>");
    }

    $("#tooltip").center();
    $("#tooltip").show();
}

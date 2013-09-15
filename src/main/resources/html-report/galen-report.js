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

    $(".global-error span").click(function (){
        $(this).next().slideToggle();
    });

    $("h2").click(function (){
        $(this).next().slideToggle();
    });

    $("h2").each(function (){
        var next = $(this).next();

        if (next.find("ul.test-specs li.fail").length > 0 || 
            next.find(".global-error").length > 0) {
            $(this).addClass("has-failures");
        }
    });
});

function showScreenshot(img, width, height, areas) {
    $("#tooltip-body").html("<div class='canvas'></div>").append(img);

    var delta = 2;
    for (var i=0; i<areas.length; i++) {
        var area = [areas[i].area[0] - delta, 
            areas[i].area[1] - delta,
            areas[i].area[2] + delta,
            areas[i].area[3] + delta
        ];
        $("#tooltip-body .canvas").append("<div class='brick' style='left:"
            + area[0] + "px; top:"
            + area[1] + "px; width:"
            + area[2] + "px; height:"
            + area[3] + "px'><span>"
            + areas[i].text + "</span></div>");
    }

    $("#tooltip").center();
    $("#tooltip").show();
}

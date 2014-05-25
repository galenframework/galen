$.fn.center = function () {
    this.css("position","absolute");
    this.css("top", ($(window).scrollTop() + 30) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + $(window).scrollLeft()) + "px");
    return this;
}


var Galen = {
    palette: {
        colors:["#E01B5D", "#1BBFE0", "#AB1BE0", "#2AA30B", "#C45D02", "#348072"],
        index: 0,
        start: function () {
            this.index = -1;
        },
        pick: function (){
            this.index++;
            if (this.index >= this.colors.length) {
                this.index = 0;
            }
            return this.colors[this.index];
        }
    },
    formatMainOverview: function () {
        $(".suites .suite").each(function (){
            var passed = parseInt($(this).find(".passed").text());
            var failed = parseInt($(this).find(".failed").text());
            var warnings = parseInt($(this).find(".warnings").text());
            var total = passed + failed + warnings;
            if (total > 0) {
                var passedPercent = Math.round(passed * 100 / total);
                var failedPercent = Math.round(failed * 100 / total);
                var warningPercent = Math.round(warnings * 100 / total);
                $(this).append("<table class='progress'><tr><td class='passed' style='width:" + passedPercent + "%;'></td><td style='width:" + failedPercent + "%;' class='failed'></td><td style='width:" + warningPercent + "%;' class='warning'></td></tr></tablet>");
            }
        });
    },
    formatSuiteReport: function () {
        $("body").append("<div id='tooltip'><a id='close-tooltip' href='#'>Close</a><div id='tooltip-body'></div></div>");

        $("#close-tooltip").click(function (){
            $("#tooltip").hide();
            return false;
        });

        $("ul.test-specs li.fail span").click(function () {
            var subObjects = $(this).parent().parent().find(".sub-objects");
            if (subObjects.length > 0) {
                subObjects.slideToggle();
            }
            else {
                var screenshot = $(this).closest("div.layout-report").attr("data-screenshot");
                var img = new Image();

                var areas = [];
                $(this).parent().find("ul.areas li").each(function (){
                    var areaText = $(this).attr("data-area");
                    areas[areas.length] = {
                        area: eval("[" + areaText + "]"),
                        text: $(this).html()
                    };
                });

                img.onload = function() {
                    Galen.showScreenshot(img, this.width, this.height, areas);
                };
                img.src = screenshot;
            }
        });

        $("h3.object").click(function (){
            var container = $(this).next();
            var screenshot = $(this).closest("div.layout-report").attr("data-screenshot");

            var areaText = container.attr("data-area");
            if (areaText != null) {
                var areas = [{
                    area: eval("[" + areaText + "]"),
                    text: container.attr("data-name")
                }];

                var img = new Image();
                img.onload = function() {
                    Galen.showScreenshot(img, this.width, this.height, areas);
                };
                img.src = screenshot;
            }
        });

        $("ul.report  li a.report-link").click(function () {
            var id = $(this).attr("data-report-id");

            $("#report-nodes-" + id).slideToggle(function () {
                var isCollapsed = !$(this).is(":visible");

                $(this).toggleClass("collapsed", isCollapsed);
                $("#report-link-" + id).toggleClass("collapsed", isCollapsed);
            });

            return false;
        });

        this.makeSliding(".layout-report h2");

        $("h2").each(function (){
            var next = $(this).next();

            if (next.find("ul.test-specs li.fail").length > 0 ||
                next.find(".global-error").length > 0) {
                $(this).addClass("has-failures");
            }
        });


        $("ul.report li.report-status-info > a.report-link").addClass("collapsed");
        $("ul.report li.report-status-info > div.report-container").addClass("collapsed").hide();

        $("ul.report li.report-status-error").each(function () {
            $(this).parents("div.report-container").removeClass("collapsed").show();
            $(this).parents("a.report-link").removeClass("collapsed");
        });


        var svgDownload = $("iconset .icon-download");

        $("ul.report ul.attachments a").each(function () {
            var el = $(this);
            var html = el.html();
            el.html("");
            svgDownload.clone().appendTo(el);
            el.append("<span>" + html + "</span>");
        });
    },
    makeSliding: function (selector) {
        $(selector).click(Galen.onSlidingElementClicked);
    },
    onSlidingElementClicked: function () {
        $(this).next().slideToggle();
    },
    showScreenshot: function (img, width, height, areas) {
        $("#tooltip-body").html("<div class='canvas'></div>").append(img);

        var delta = 2;
        Galen.palette.start();
        for (var i=0; i<areas.length; i++) {
            var area = [areas[i].area[0] - delta,
                areas[i].area[1] - delta,
                areas[i].area[2] + delta,
                areas[i].area[3] + delta
            ];
            var color = Galen.palette.pick();
            $("#tooltip-body .canvas").append("<div class='brick' style='left:"
                + area[0] + "px; top:"
                + area[1] + "px; width:"
                + area[2] + "px; height:"
                + area[3] + "px;"
                + "border: 1px solid " + color + ";'>"
                + "<span style='background:" + color + ";'>"
                + areas[i].text + "</span></div>");
        }

        $("#tooltip").center();
        $("#tooltip").show();
    }
};

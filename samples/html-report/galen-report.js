jQuery.fn.centerHorizontally = function () {
    this.css("position","absolute");
    this.css("top", Math.max(0, $(window).scrollTop() + 80) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + 
                                                $(window).scrollLeft()) + "px");
    return this;
}


var _GalenReport = {};

function setHtml(id, html) {
    document.getElementById(id).innerHTML = html;
}

function createTemplate(templateId) {
    var source = document.getElementById(templateId).innerHTML;
    return Handlebars.compile(source);
}

function safeHtml(html) {
    return new Handlebars.SafeString(html);
}

function hasChildElements(items) {
    return items !== null && items !== undefined && Array.isArray(items) && items.length > 0;
}


function onExpandNodeClick() {
    var expandLink = this;

    var container = $(this).next(".expand-container");
    if (container.length === 0) {
        container = $(this).next().next(".expand-container");
    }
    container.slideToggle({
        duration: "fast",
        complete: function () {
            if ($(this).is(":visible")) {
                $(expandLink).removeClass("collapsed").addClass("expanded");
            } else {
                $(expandLink).removeClass("expanded").addClass("collapsed");
            }
        }
    });

    return false;
}

function ColorPatternPicker() {
    this.colors = [
        "#B55CFF", "#FF5C98", "#5C9AFF", "#5CE9FF", "#5CFFA3", "#98FF5C", "#FFE95C", "#FFA05C"
    ];
}
ColorPatternPicker.prototype.index = -1;
ColorPatternPicker.prototype.pickColor = function () {
    this.index += 1;

    if (this.index >= this.colors.length) {
        this.index = 0;
    }

    return this.colors[this.index];
};

function collectObjectsToHighlight(objects, objectNames) {
    var collected = {},
        colorPicker = new ColorPatternPicker();

    for (var i = 0; i < objectNames.length; i++) {
        var objectName = objectNames[i];

        if (objectName in objects) {
            var area = objects[objectName].area;

            collected[objectName] = {
                area: {
                    left: area[0] - 1,
                    top: area[1] - 1,
                    width: area[2] + 2,
                    height: area[3] + 2
                },
                color: colorPicker.pickColor()
            };
        }
    }
    return collected;
}

function onLayoutCheckClick() {
    $this = $(this);
    var objectNames = $this.attr("data-highlight-objects").split(",");

    var checkText = $this.text();
    var errorText = $this.next(".layout-check-error-message").text();

    var layoutId = $this.closest(".layout-report").attr("data-layout-id");

    if (layoutId !== "" && layoutId >= 0) {
        var layout = _GalenReport.layouts[layoutId];

        if (layout !== null) {
            var img = new Image();
            var objects = collectObjectsToHighlight(layout.objects, objectNames);

            img.onload = function() {
                _GalenReport.showNotification(checkText, errorText);
                _GalenReport.showScreenshotWithObjects(layout.screenshot, this.width, this.height, objects);
            };
            img.onerror = function() {
                _GalenReport.showNotification("Cannot load screenshot", "Can't load screenshot: " + layout.screenshot);
            };

            img.src = layout.screenshot;
        } else {
            _GalenReport.showErrorNotification("Couldn't find layout data");
        }
    } else {
        _GalenReport.showErrorNotification("Couldn't find layout data");
    }

    return false;
}

function onImageComparisonClick() {
    var $this = $(this);
    return false;
}

function onNotificationCloseClick() {
    $(this).closest(".notification").fadeOut("fast");
    return false;
}

function hideNotification() {
    $(".notification").fadeOut();
}


function showShadow() {
    $("#screen-shadow").fadeIn();
}
function hideShadow() {
    $("#screen-shadow").fadeOut();
}
function showPopup(html) {
    showShadow();
    $("#popup .popup-content").html(html);
    $("#popup").centerHorizontally().fadeIn('fast');
}

function hidePopup() {
    hideShadow();
    $("#popup").fadeOut();
}

function onPopupCloseClick() {
    hideShadow();
    $(this).closest(".popup").fadeOut();
    return false;
}

function expandAllNodes() {
    $(".expand-container").show();
    $("a.expand-link.contains-children-true").removeClass("collapsed").removeClass("expanded").addClass("expanded");
}

function expandErrorNodes() {
    collapseAllNodes();

    $(".node-status-error, .layout-check-status-error").parents(".expand-container").show();
}
function collapseAllNodes() {
    $(".expand-container").hide();
    $("a.expand-link.contains-children-true").removeClass("expanded").removeClass("collapsed").addClass("collapsed");
}


function createGalenReport() {

    _GalenReport = {
        layouts: [],

        registerLayout: function (layout) {
            var id = this.layouts.push({
                objects: layout.objects,
                screenshot: layout.screenshot
            }) - 1;
            return id;
        },

        tpl: {
           main: createTemplate("report-tpl"),
           reportNode: createTemplate("report-node-tpl"),
           reportNodeText: createTemplate("report-node-text-tpl"),
           layout: createTemplate("report-layout-tpl"),
           layoutSection: createTemplate("report-layout-section-tpl"),
           layoutObject: createTemplate("report-layout-object-tpl"),
           layoutCheck: createTemplate("report-layout-check-tpl"),
           sublayout: createTemplate("report-layout-sublayout-tpl"),
           screenshotPopup: createTemplate("screenshot-popup-tpl")
        },

        render: function (id, reportData) {
            setHtml(id, this.tpl.main(reportData));
            
            $("a.expand-link.contains-children-true").click(onExpandNodeClick);
            $("a.layout-check").click(onLayoutCheckClick);
            $("a.image-comparison-link").click(onImageComparisonClick);

            expandErrorNodes();
        },

        showNotification: function (summary, message) {
            $("#notification .notification-summary").text(summary);
            $("#notification .notification-message").text(message);
            $("#notification").fadeIn("fast");
        },

        showErrorNotification: function (summary, message) {
            this.showNotification(summary, message);
        },

        showScreenshotWithObjects: function (screenshotPath, width, height, objects) {
            showPopup(_GalenReport.tpl.screenshotPopup({
                screenshot: screenshotPath,
                objects: objects
            })); 
        }
    };


    $(document).keydown(function(e) {
        if (e.keyCode == 27) {
            hidePopup();
            hideNotification();
        }
    });

    $(".notification-close-link").click(onNotificationCloseClick);
    $(".popup-close-link").click(onPopupCloseClick);

    $(".menu-op-expand-all").click(expandAllNodes);
    $(".menu-op-collapse-all").click(collapseAllNodes);
    $(".menu-op-expand-errors").click(expandErrorNodes);

    return _GalenReport;
}

Handlebars.registerHelper("renderNode", function (node) {
    if (node !== null && node !== undefined) {
        if (node.type === "node")  {
            return safeHtml(_GalenReport.tpl.reportNode(node));
        } else if (node.type === "text") {
            return safeHtml(_GalenReport.tpl.reportNodeText(node));
        } else if (node.type === "layout") {
            return safeHtml(_GalenReport.tpl.layout(node));
        }
    }
    return "";
});

Handlebars.registerHelper("renderLayoutSection", function (section) {
    if (section !== null && section !== undefined) {
        return safeHtml(_GalenReport.tpl.layoutSection(section));
    }
});

Handlebars.registerHelper("renderLayoutObject", function (object) {
    if (object !== null && object !== undefined) {
        return safeHtml(_GalenReport.tpl.layoutObject(object));
    }
});

Handlebars.registerHelper("renderLayoutCheck", function (check) {
    if (check !== null && check !== undefined) {
        return safeHtml(_GalenReport.tpl.layoutCheck(check));
    }
});

Handlebars.registerHelper("renderSublayout", function (sublayout) {
    if (sublayout !== null && sublayout !== undefined) {
        var layoutId = _GalenReport.registerLayout(sublayout);
        sublayout.layoutId = layoutId;
        return safeHtml(_GalenReport.tpl.sublayout(sublayout));
    }
});

Handlebars.registerHelper("hasChildElements", function (items1, items2) {
    if (hasChildElements(items1) || hasChildElements(items2)) {
        return "true"
    }
    return "false";
});

Handlebars.registerHelper("formatReportTime", function (time) {
    if (time !== null && time !== undefined) {
    var date = new Date(time * 1000);
        var hh = date.getUTCHours();
        var mm = date.getUTCMinutes();
        var ss = date.getSeconds();
        if (hh < 10) {hh = "0"+hh;}
        if (mm < 10) {mm = "0"+mm;}
        if (ss < 10) {ss = "0"+ss;}
        return hh + ":" + mm + ":" + ss;
    }
    return "";
});


Handlebars.registerHelper("commaSeparated", function (items) {
    if (items !== null && items !== undefined && items.length > 0) {
        var text = "";
        for (var i = 0; i < items.length; i++) {
            if (i > 0) {
                text += ",";
            }
            text += items[i];
        }
        return text;
    }

    return "";
});









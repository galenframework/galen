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
                    left: area[0] - 3,
                    top: area[1] - 3,
                    width: area[2],
                    height: area[3]
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
    var errorText = $this.next(".layout-check-error-message").find(".layout-check-error-message-text").text();

    var layoutId = $this.closest(".layout-report").attr("data-layout-id");

    if (layoutId !== "" && layoutId >= 0) {
        var layout = _GalenReport.layouts[layoutId];

        if (layout !== null) {
            var objects = collectObjectsToHighlight(layout.objects, objectNames);

            loadImage(layout.screenshot, function () {
                _GalenReport.showNotification(checkText, errorText);
                _GalenReport.showScreenshotWithObjects(layout.screenshot, this.width, this.height, objects);
            });

        } else {
            _GalenReport.showErrorNotification("Couldn't find layout data");
        }
    } else {
        _GalenReport.showErrorNotification("Couldn't find layout data");
    }

    return false;
}

function loadImage(imagePath, callback) {
    var img = new Image();
    img.onload = function () {
        callback(this, this.width, this.height);
    };
    img.onerror = function() {
        _GalenReport.showNotification("Cannot load image", "Path: " + imagePath);
    };
    img.src = imagePath;
}

function onImageComparisonClick() {
    var $this = $(this);
    var actualImagePath = $this.attr("data-actual-image");
    var expectedImagePath = $this.attr("data-expected-image");
    var mapImagePath = $this.attr("data-map-image");

    loadImage(actualImagePath, function (actualImage, actualImageWidth, actualImageHeight) {
        loadImage(expectedImagePath, function (expectedImage, expectedImageWidth, expectedImageHeight) {
            loadImage(mapImagePath, function (mapImage, mapImageWidth, mapImageHeight) {
                showPopup(_GalenReport.tpl.imageComparison({
                    actual: actualImagePath,
                    expected: expectedImagePath,
                    map: mapImagePath
                }));
            });
        });
    });
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
           screenshotPopup: createTemplate("screenshot-popup-tpl"),
           imageComparison: createTemplate("image-comparison-tpl")
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
    $("#screen-shadow").click(function () {
        hidePopup();
        hideNotification();
    });

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
    var date = new Date(time);
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

function toStringWithLeadingZero(number) {
    if (number < 10) {
        return "0" + number;
    }
    return number;
}

Handlebars.registerHelper("formatDateTime", function (time) {
    if (time !== null && time !== undefined) {
    var d = new Date(time);
        var date = toStringWithLeadingZero(d.getDate());
        var month = toStringWithLeadingZero(d.getMonth() + 1);
        var year = d.getFullYear();

        var hh = toStringWithLeadingZero(d.getUTCHours());
        var mm = toStringWithLeadingZero(d.getUTCMinutes());
        var ss = toStringWithLeadingZero(d.getSeconds());
        return date + "-" + month + "-" + year + " " + hh + ":" + mm + ":" + ss;
    }
    return "";
});

Handlebars.registerHelper("renderProgressBar", function (statistic) {
    var total = statistic.passed + statistic.errors + statistic.warnings;
    var passedPercent = Math.round(statistic.passed * 100 / total);
    var failedPercent = Math.round(statistic.errors * 100 / total);
    var warningPercent = Math.round(statistic.warnings * 100 / total);

    return new Handlebars.SafeString("<table class='progress'><tr><td class='passed' style='width:" + passedPercent + "%;'></td><td style='width:" + failedPercent + "%;' class='failed'></td><td                 style='width:" + warningPercent + "%;' class='warning'></td></tr></table>");
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



function createGalenTestOverview() {
    return  {
        tpl: {
            main: createTemplate("main-overview")
        },

        render: function (id, data) {
            setHtml(id, this.tpl.main(data));

            $('table.tests').tablesorter({
                theme: 'default',
                widthFixed: false,
                showProcessing: false,
                headerTemplate: '{content}',
                onRenderTemplate: null, 
                onRenderHeader: function (index) {
                    $(this).find('div.tablesorter-header-inner').addClass('roundedCorners');
                },
                cancelSelection: true,
                dateFormat: "mmddyyyy",
                sortMultiSortKey: "shiftKey",
                sortResetKey: 'ctrlKey',
                usNumberFormat: true,
                delayInit: false,
                serverSideSorting: false,
                headers: {
                    0: {
                        sorter: "text"
                    },
                    1: {
                        sorter: "digit"
                    },
                    2: {
                        sorter: "text"
                    },
                    3: {
                        sorter: "url"
                    }
                },
                ignoreCase: true,
                sortForce: null,
                sortList: [
                    [0, 0],
                    [1, 0],
                    [2, 0]
                ],
                sortAppend: null,
                sortInitialOrder: "asc",
                sortLocaleCompare: false,
                sortReset: false,
                sortRestart: false,
                emptyTo: "bottom",
                stringTo: "max",
                textExtraction: {
                    0: function (node) {
                        return $(node).text();
                    },
                    1: function (node) {
                        return $(node).text();
                    }
                },
                textSorter: null,
                initWidgets: true,
                widgets: ['zebra', 'columns'],
                widgetOptions: {
                    zebra: [
                        "ui-widget-content even",
                        "ui-state-default odd"],
                    uitheme: 'jui',
                    columns: [
                        "primary",
                        "secondary",
                        "tertiary"],
                    columns_tfoot: true,
                    columns_thead: true,
                    filter_childRows: false,
                    filter_columnFilters: true,
                    filter_cssFilter: "tablesorter-filter",
                    filter_functions: null,
                    filter_hideFilters: false,
                    filter_ignoreCase: true,
                    filter_reset: null,
                    filter_searchDelay: 300,
                    filter_serversideFiltering: false,
                    filter_startsWith: false,
                    filter_useParsedData: false,
                    resizable: true,
                    saveSort: true,
                    stickyHeaders: "tablesorter-stickyHeader"
                },
                initialized: function (table) {},
                tableClass: 'tablesorter',
                cssAsc: "tablesorter-headerSortUp",
                cssDesc: "tablesorter-headerSortDown",
                cssHeader: "tablesorter-header",
                cssHeaderRow: "tablesorter-headerRow",
                cssIcon: "tablesorter-icon",
                cssChildRow: "tablesorter-childRow",
                cssInfoBlock: "tablesorter-infoOnly",
                cssProcessing: "tablesorter-processing",
                selectorHeaders: '> thead th, > thead td',
                selectorSort: "th, td",
                selectorRemove: "tr.remove-me",
                debug: false

            });

            $.extend($.tablesorter.themes.jui, {
                table: 'ui-widget ui-widget-content ui-corner-all', 
                header: 'ui-widget-header ui-corner-all ui-state-default',
                icons: 'ui-icon', 
                sortNone: 'ui-icon-carat-2-n-s',
                sortAsc: 'ui-icon-carat-1-n',
                sortDesc: 'ui-icon-carat-1-s',
                active: 'ui-state-active',
                hover: 'ui-state-hover', 
                filterRow: '',
                even: 'ui-widget-content',
                odd: 'ui-state-default'
            });
        }
    };
}






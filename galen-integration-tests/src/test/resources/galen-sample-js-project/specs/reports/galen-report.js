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

function haveSimilarElements(array1, array2) {
    return $(array1).filter(array2).length > 0;
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

    var container = $(this).siblings(".expand-container");
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

function collectObjectsToHighlight(objects, filterFunction) {
    var collected = [],
        colorPicker = new ColorPatternPicker();

    for (var objectName in objects) {
        if (objects.hasOwnProperty(objectName)) {

            if (filterFunction(objectName, objects[objectName])) {
                var area = objects[objectName].area;

                collected.push({
                    name: objectName,
                    area: {
                        left: area[0] - 3,
                        top: area[1] - 3,
                        width: area[2],
                        height: area[3]
                    },
                    color: colorPicker.pickColor(),
                    drawBorder: true,
                    fillBackground: false
                });
            }
        }
    }

    return sortByArea(collected);
}

function sortByArea(objects) {
    return objects.sort(function (a, b) {
        return b.area.width*b.area.height - a.area.width*a.area.height;
    });
}

function findScreenSize(objects) {
    var maxWidth = 0;
    var maxHeight = 0;
    for (var objectName in objects) {
        if (objectName === "screen") {
            return {
                width: objects[objectName].area[2],
                height: objects[objectName].area[3]
            };
        } else {
            if (maxWidth < objects[objectName].area[2]) {
                maxWidth = objects[objectName].area[2];
            }
            if (maxHeight < objects[objectName].area[3]) {
                maxHeight = objects[objectName].area[3];
            }
        }
    }

    return {
        width: maxWidth,
        height: maxHeight
    };
}

function findClosestScreenshotData($element) {
    var $closest = $element.closest("[data-screenshot]");
    if ($closest.length > 0) {
        return $closest.attr("data-screenshot");
    } else {
        return null;
    }
}

function onLayoutCheckClick() {
    $this = $(this);
    var objectNames = $this.attr("data-highlight-objects").split(",");

    var checkText = $this.text();
    var errorText = $this.next(".layout-check-error-message").find(".layout-check-error-message-text").text();

    var layoutId = $this.closest(".layout-report").attr("data-layout-id");
    var screenshot = findClosestScreenshotData($this);

    if (layoutId !== "" && layoutId >= 0) {
        var layout = _GalenReport.layouts[layoutId];

        if (layout !== null) {
            var objects = collectObjectsToHighlight(layout.objects, function (objectName, object) {
                return objectNames.indexOf(objectName) > -1;
            });

            if (screenshot === null || screenshot === undefined) {
                screenshot = _GalenReport.layouts[0].screenshot;
            }

            showShadow();
            showPopup("Loading ...");

            loadImage(screenshot, function () {
                _GalenReport.showNotification(checkText, errorText);
                _GalenReport.showScreenshotWithObjects(screenshot, this.width, this.height, objects);
            }, function () {
                var screenSize = findScreenSize(layout.objects);
                _GalenReport.showNotification(checkText, errorText);
                _GalenReport.showScreenshotWithObjects(null, screenSize.width, screenSize.height, objects);
            });

        } else {
            _GalenReport.showErrorNotification("Couldn't find layout data");
        }
    } else {
        _GalenReport.showErrorNotification("Couldn't find layout data");
    }

    return false;
}

function loadImage(imagePath, callback, errorCallback) {
    var img = new Image();
    img.onload = function () {
        callback(this, this.width, this.height);
    };
    img.onerror = function() {
        errorCallback(this);
    };
    img.src = imagePath;
}

function onImageComparisonClick() {
    var $this = $(this);
    var actualImagePath = $this.attr("data-actual-image");
    var expectedImagePath = $this.attr("data-expected-image");
    var mapImagePath = $this.attr("data-map-image");

    showShadow();
    showPopup("Loading ...");

    var windowWidth = $(window).width();

    loadImage(actualImagePath, function (actualImage, actualImageWidth, actualImageHeight) {
        loadImage(expectedImagePath, function (expectedImage, expectedImageWidth, expectedImageHeight) {
            loadImage(mapImagePath, function (mapImage, mapImageWidth, mapImageHeight) {
                var layout = "vertical";
                if (windowWidth - actualImageWidth - expectedImageWidth > 100) {
                    layout = "horizontal";
                }

                showPopup(_GalenReport.tpl.imageComparison({
                    actual: actualImagePath,
                    expected: expectedImagePath,
                    map: mapImagePath,
                    layout: layout
                }));
            });
        });
    });

    return false;
}

function visitEachSpec(sections, callback) {
    if (sections !== null && sections !== undefined) {
        for (var i = 0; i < sections.length; i++) {
            if (sections[i].sections != undefined && sections[i].sections != null) {
                visitEachSpec(sections[i].sections, callback);
            }

            for (var j = 0; j < sections[i].objects.length; j++) {
                for (var k = 0; k < sections[i].objects[j].specs.length; k++) {
                    callback(sections[i].objects[j].specs[k]);
                }
            }
        }
    }
}

function rgb2hex(r,g,b){
    return "#" +
        ("0" + r.toString(16)).slice(-2) +
        ("0" + g.toString(16)).slice(-2) +
        ("0" + b.toString(16)).slice(-2);
}


function pickHeatColor(value) {
    var max = 6;
    var _t = Math.min(value/max, 1.0);

    if (_t < 0.5) {
        var t = _t*2;
        var red = Math.min(Math.floor(255.0 * t), 255);
        return rgb2hex(red, 255, 0);
    } else {
        var t = (_t - 0.5) * 2;
        var green = Math.min(Math.floor(255.0 * (1.0 - t)), 255);
        return rgb2hex(255, green, 0);
    }

}

function collectObjectsForHeatmap(layout) {
    var objectsHeatMap = {
    };

    var collected = [];

    visitEachSpec(layout.sections, function (spec) {
        for (var i = 0; i < spec.highlight.length; i++) {
            var name = spec.highlight[i];
            if (name != "screen" && name != "self" && name != "viewport" && name != "parent") {
                if (objectsHeatMap[name] !== undefined) {
                    objectsHeatMap[name] += 1;
                } else {
                    objectsHeatMap[name] = 1;
                }
            }
            if (spec.hasOwnProperty("subLayout")) {
                var collectedFromSubLayout = collectObjectsForHeatmap(spec.subLayout);
                for (var k = 0; k < collectedFromSubLayout.length; k++) {
                    collected.push(collectedFromSubLayout[k]);
                }
            }
        }
    });

    for (objectName in objectsHeatMap) {
        if (objectsHeatMap.hasOwnProperty(objectName)) {
            var count = objectsHeatMap[objectName];

            if (layout.objects.hasOwnProperty(objectName)) {
                
                var area = layout.objects[objectName].area;

                collected.push({
                    name: objectName,
                    area: {
                        left: area[0],
                        top: area[1],
                        width: area[2],
                        height: area[3]
                    },
                    color: pickHeatColor(count),
                    drawBorder: false,
                    fillBackground: true
                });
            }
        }
    }

    return sortByArea(collected);
}

function onLayoutHeatmapClick() {
    $this = $(this);
    var layoutId = $this.closest(".node-horizontal-menu").attr("data-layout-id");

    if (layoutId !== "" && layoutId >= 0) {
        var layout = _GalenReport.layouts[layoutId];

        if (layout !== null) {
            var objects = collectObjectsForHeatmap(layout);
            var screenshot = layout.screenshot;

            if (screenshot === null || screenshot === undefined) {
                screenshot = _GalenReport.layouts[0].screenshot;
            }

            showShadow();
            showPopup("Loading ...");

            loadImage(screenshot, function () {
                _GalenReport.showScreenshotWithObjects(screenshot, this.width, this.height, objects);
            }, function () {
                var screenSize = findScreenSize(layout.objects);
                _GalenReport.showNotification("Couldn't load screenshot: " + screenshot, "");
                _GalenReport.showScreenshotWithObjects(null, screenSize.width, screenSize.height, objects);
            });

        } else {
            _GalenReport.showErrorNotification("Couldn't find layout data");
        }
    } else {
        _GalenReport.showErrorNotification("Couldn't find layout data");
    }

    return false;
}


function onNodeExtrasClick() {
    var html = $(this).next(".node-extras-content").html();
    showPopup(html);
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
                screenshot: layout.screenshot,
                sections: layout.sections
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
           imageComparison: createTemplate("image-comparison-tpl"),
           nodeExtras: createTemplate("node-extras-tpl")
        },

        render: function (id, reportData) {
            setHtml(id, this.tpl.main(reportData));
            
            $("a.expand-link.contains-children-true").click(onExpandNodeClick);
            $("a.layout-check").click(onLayoutCheckClick);
            $("a.image-comparison-link").click(onImageComparisonClick);
            $("a.layout-heatmap-link").click(onLayoutHeatmapClick);
            $("a.node-extras").click(onNodeExtrasClick);

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
                objects: objects,
                width: width,
                height: height
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
            if (node.layoutId === undefined || node.layoutId === null) {
                node.layoutId = _GalenReport.registerLayout(node);
            }
            return safeHtml(_GalenReport.tpl.layout(node));
        }
    }
    return "";
});

Handlebars.registerHelper("renderNodeExtras", function (extras) {
    if (extras !== null && extras !== undefined) {
        return safeHtml(_GalenReport.tpl.nodeExtras(extras));
    }
    return "";
});

Handlebars.registerHelper('ifCond', function(v1, v2, options) {
    if(v1 === v2) {
        return options.fn(this);
    }
    return options.inverse(this);
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
        if (sublayout.layoutId === undefined || sublayout.layoutId === null) {
            sublayout.layoutId = _GalenReport.registerLayout(sublayout);
        }
        
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
        var hh = date.getHours();
        var mm = date.getMinutes();
        var ss = date.getSeconds();
        if (hh < 10) {hh = "0"+hh;}
        if (mm < 10) {mm = "0"+mm;}
        if (ss < 10) {ss = "0"+ss;}
        return hh + ":" + mm + ":" + ss;
    }
    return "";
});

Handlebars.registerHelper("formatGroupsPretty", function (groups) {
    if (groups !== null && groups !== undefined) {
        var text = "";

        for (var i = 0; i < groups.length; i++) {
            if (i > 0) {
                text += ", ";
            }
            text = text + '<a href="#tests|grouped|' + groups[i] + '">' + groups[i] + '</a>';
        }
        return safeHtml(text);
    }
    return "";
});

Handlebars.registerHelper("formatDurationHumanReadable", function (durationInMillis) {
    var durationInSeconds = Math.floor(durationInMillis / 1000);
    if (durationInSeconds > 0) {
        var hours = Math.floor(durationInSeconds / 3600);
        var minutes = Math.floor((durationInSeconds - hours * 3600) / 60);
        var seconds = Math.floor(durationInSeconds - hours * 3600 - minutes * 60);

        var text = "";
        if (hours > 0) {
            text += hours + "h";
        }

        if (minutes > 0 || hours > 0) {
            if (hours > 0) {
                text += " ";
            }
            text += minutes;
            text += "m";
        }

        if (seconds > 0) {
            if (hours > 0 || minutes > 0) {
                text += " ";
            }
            text += seconds;
            text += "s";
        }

        return text;
    }

    else return "0";
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

        var hh = toStringWithLeadingZero(d.getHours());
        var mm = toStringWithLeadingZero(d.getMinutes());
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

Handlebars.registerHelper("renderGroupsProgressBar", function (group) {
    var total = group.tests;
    var passedPercent = Math.round(group.passed * 100 / total);
    var failedPercent = Math.round(group.failed * 100 / total);

    return new Handlebars.SafeString("<table class='progress'><tr><td class='passed' style='width:" + passedPercent + "%;'></td><td style='width:" + failedPercent + "%;' class='failed'></td></tr></table>");
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

    return  {
        testsTableId: null,
        groupsTableId: null,
        tpl: {
            tests: createTemplate("tests-table-tpl"),
            groups: createTemplate("groups-table-tpl")
        },


        handleHash: function (hash) {
            var hashParameters = hash.split("|");
            var view = hashParameters[0];

            $(".tabs .tab-selected").each(function () {
                $(this).removeClass("tab-selected");
            });

            if (view === "groups") {
                $("#" + this.testsTableId).hide();
                $("#" + this.groupsTableId).show();
                $(".tabs .tab-groups").addClass("tab-selected");

                this.handleGroupsHash(hash);
            } else {
                $("#" + this.groupsTableId).hide();
                $("#" + this.testsTableId).show();

                $(".tabs .tab-tests").addClass("tab-selected");
                this.handleTestsHash(hash);
            }
        },


        handleGroupsHash: function (hash) {
        },
        handleTestsHash: function (hash) {
            var arguments = hash.split("|");
            if (arguments.length > 1 && arguments[1] === "grouped") {
                var selectedGroups = arguments[2].split(",");
                if (selectedGroups.length > 0) {
                    $(".tests.tablesorter > tbody > tr").each(function () {
                        var $this = $(this);
                        var groupsAttr = $this.attr("data-groups");
                        var groups = groupsAttr.split(",");
                        if(haveSimilarElements(groups, selectedGroups)) {
                            $this.show();
                        } else {
                            $this.hide();
                        }
                    });

                    return;
                }
            }
            $(".tests.tablesorter tbody tr").each(function () {
                $(this).show();
            });
        },

        renderGroupsTable: function (id, testData) {
            this.groupsTableId = id;
            var tests = testData.tests;
            var groups = {};

            for (var i = 0; i < tests.length; i++) {
                var test = tests[i];
                var testGroups = tests[i].groups;

                if (testGroups !== null && testGroups !== undefined && Array.isArray(testGroups)) {
                    for (var j = 0; j < testGroups.length; j++) {
                        var groupName = testGroups[j];
                        if (groupName in groups) {
                            groups[groupName].tests += 1;
                            groups[groupName].failed += test.failed ? 1 : 0;
                            groups[groupName].passed += test.failed ? 0 : 1;
                        }
                        else {
                            groups[groupName] = {
                                name: groupName,
                                tests: 1,
                                failed: test.failed ? 1 : 0,
                                passed: test.failed ? 0 : 1
                            };
                        }
                    }
                }
            }

            var groupsArray = [];

            for (name in groups) {
                if (groups.hasOwnProperty(name)) {
                    groupsArray.push(groups[name]);
                }
            }

            setHtml(id, this.tpl.groups(groupsArray));
            this.createTableSorter("#" + id + " table");
        },

        renderTestsTable: function (id, data) {
            this.testsTableId = id;
            setHtml(id, this.tpl.tests(data));

            this.createTableSorter('#' + id + " table");
        },

        initialSorting: function () {
            return [
                [2, 1],
                [0, 0],
                [1, 0]
            ];
        },

        createTableSorter: function (selector) {
            $(selector).tablesorter({
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
                ignoreCase: true,
                sortForce: null,
                sortList: this.initialSorting(),
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

        }
    };
}

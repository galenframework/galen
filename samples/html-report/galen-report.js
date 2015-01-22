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
        // table sorting


        $('table.suites').tablesorter({

            // *** APPEARANCE ***
            // Add a theme - try 'blackice', 'blue', 'dark', 'default'
            //  'dropbox', 'green', 'grey' or 'ice'
            // to use 'bootstrap' or 'jui', you'll need to add the "uitheme"
            // widget and also set it to the same name
            // this option only adds a table class name "tablesorter-{theme}"
            theme: 'default',

            // fix the column widths
            widthFixed: false,

            // Show an indeterminate timer icon in the header when the table
            // is sorted or filtered
            showProcessing: false,

            // header layout template (HTML ok); {content} = innerHTML,
            // {icon} = <i/> (class from cssIcon)
            headerTemplate: '{content}',

            // return the modified template string
            onRenderTemplate: null, // function(index, template){ return template; },

            // called after each header cell is rendered, use index to target the column
            // customize header HTML
            onRenderHeader: function (index) {
                // the span wrapper is added by default
                $(this).find('div.tablesorter-header-inner').addClass('roundedCorners');
            },

            // *** FUNCTIONALITY ***
            // prevent text selection in header
            cancelSelection: true,

            // other options: "ddmmyyyy" & "yyyymmdd"
            dateFormat: "mmddyyyy",

            // The key used to select more than one column for multi-column
            // sorting.
            sortMultiSortKey: "shiftKey",

            // key used to remove sorting on a column
            sortResetKey: 'ctrlKey',

            // false for German "1.234.567,89" or French "1 234 567,89"
            usNumberFormat: true,

            // If true, parsing of all table cell data will be delayed
            // until the user initializes a sort
            delayInit: false,

            // if true, server-side sorting should be performed because
            // client-side sorting will be disabled, but the ui and events
            // will still be used.
            serverSideSorting: false,

            // *** SORT OPTIONS ***
            // These are detected by default,
            // but you can change or disable them
            // these can also be set using data-attributes or class names
            headers: {
                // set "sorter : false" (no quotes) to disable the column
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

            // ignore case while sorting
            ignoreCase: true,

            // forces the user to have this/these column(s) sorted first
            sortForce: null,
            // initial sort order of the columns, example sortList: [[0,0],[1,0]],
            // [[columnIndex, sortDirection], ... ]
            sortList: [
                [0, 0],
                [1, 0],
                [2, 0]
            ],
            // default sort that is added to the end of the users sort
            // selection.
            sortAppend: null,

            // starting sort direction "asc" or "desc"
            sortInitialOrder: "asc",

            // Replace equivalent character (accented characters) to allow
            // for alphanumeric sorting
            sortLocaleCompare: false,

            // third click on the header will reset column to default - unsorted
            sortReset: false,

            // restart sort to "sortInitialOrder" when clicking on previously
            // unsorted columns
            sortRestart: false,

            // sort empty cell to bottom, top, none, zero
            emptyTo: "bottom",

            // sort strings in numerical column as max, min, top, bottom, zero
            stringTo: "max",

            // extract text from the table - this is how is
            // it done by default
            textExtraction: {
                0: function (node) {
                    return $(node).text();
                },
                1: function (node) {
                    return $(node).text();
                }
            },

            // use custom text sorter
            // function(a,b){ return a.sort(b); } // basic sort
            textSorter: null,

            // *** WIDGETS ***

            // apply widgets on tablesorter initialization
            initWidgets: true,

            // include zebra and any other widgets, options:
            // 'columns', 'filter', 'stickyHeaders' & 'resizable'
            // 'uitheme' is another widget, but requires loading
            // a different skin and a jQuery UI theme.
            widgets: ['zebra', 'columns'],

            widgetOptions: {

                // zebra widget: adding zebra striping, using content and
                // default styles - the ui css removes the background
                // from default even and odd class names included for this
                // demo to allow switching themes
                // [ "even", "odd" ]
                zebra: [
                    "ui-widget-content even",
                    "ui-state-default odd"],

                // uitheme widget: * Updated! in tablesorter v2.4 **
                // Instead of the array of icon class names, this option now
                // contains the name of the theme. Currently jQuery UI ("jui")
                // and Bootstrap ("bootstrap") themes are supported. To modify
                // the class names used, extend from the themes variable
                // look for the "$.extend($.tablesorter.themes.jui" code below
                uitheme: 'jui',

                // columns widget: change the default column class names
                // primary is the 1st column sorted, secondary is the 2nd, etc
                columns: [
                    "primary",
                    "secondary",
                    "tertiary"],

                // columns widget: If true, the class names from the columns
                // option will also be added to the table tfoot.
                columns_tfoot: true,

                // columns widget: If true, the class names from the columns
                // option will also be added to the table thead.
                columns_thead: true,

                // filter widget: If there are child rows in the table (rows with
                // class name from "cssChildRow" option) and this option is true
                // and a match is found anywhere in the child row, then it will make
                // that row visible; default is false
                filter_childRows: false,

                // filter widget: If true, a filter will be added to the top of
                // each table column.
                filter_columnFilters: true,

                // filter widget: css class applied to the table row containing the
                // filters & the inputs within that row
                filter_cssFilter: "tablesorter-filter",

                // filter widget: Customize the filter widget by adding a select
                // dropdown with content, custom options or custom filter functions
                // see http://goo.gl/HQQLW for more details
                filter_functions: null,

                // filter widget: Set this option to true to hide the filter row
                // initially. The rows is revealed by hovering over the filter
                // row or giving any filter input/select focus.
                filter_hideFilters: false,

                // filter widget: Set this option to false to keep the searches
                // case sensitive
                filter_ignoreCase: true,

                // filter widget: jQuery selector string of an element used to
                // reset the filters.
                filter_reset: null,

                // Delay in milliseconds before the filter widget starts searching;
                // This option prevents searching for every character while typing
                // and should make searching large tables faster.
                filter_searchDelay: 300,

                // Set this option to true if filtering is performed on the server-side.
                filter_serversideFiltering: false,

                // filter widget: Set this option to true to use the filter to find
                // text from the start of the column. So typing in "a" will find
                // "albert" but not "frank", both have a's; default is false
                filter_startsWith: false,

                // filter widget: If true, ALL filter searches will only use parsed
                // data. To only use parsed data in specific columns, set this option
                // to false and add class name "filter-parsed" to the header
                filter_useParsedData: false,

                // Resizable widget: If this option is set to false, resized column
                // widths will not be saved. Previous saved values will be restored
                // on page reload
                resizable: true,

                // saveSort widget: If this option is set to false, new sorts will
                // not be saved. Any previous saved sort will be restored on page
                // reload.
                saveSort: true,

                // stickyHeaders widget: css class name applied to the sticky header
                stickyHeaders: "tablesorter-stickyHeader"

            },

            // *** CALLBACKS ***
            // function called after tablesorter has completed initialization
            initialized: function (table) {},

            // *** CSS CLASS NAMES ***
            tableClass: 'tablesorter',
            cssAsc: "tablesorter-headerSortUp",
            cssDesc: "tablesorter-headerSortDown",
            cssHeader: "tablesorter-header",
            cssHeaderRow: "tablesorter-headerRow",
            cssIcon: "tablesorter-icon",
            cssChildRow: "tablesorter-childRow",
            cssInfoBlock: "tablesorter-infoOnly",
            cssProcessing: "tablesorter-processing",

            // *** SELECTORS ***
            // jQuery selectors used to find the header cells.
            selectorHeaders: '> thead th, > thead td',

            // jQuery selector of content within selectorHeaders
            // that is clickable to trigger a sort.
            selectorSort: "th, td",

            // rows with this class name will be removed automatically
            // before updating the table cache - used by "update",
            // "addRows" and "appendCache"
            selectorRemove: "tr.remove-me",

            // *** DEBUGING ***
            // send messages to console
            debug: false

        });

        // Extend the themes to change any of the default class names ** NEW **
        $.extend($.tablesorter.themes.jui, {
            // change default jQuery uitheme icons - find the full list of icons
            // here: http://jqueryui.com/themeroller/ (hover over them for their name)
            table: 'ui-widget ui-widget-content ui-corner-all', // table classes
            header: 'ui-widget-header ui-corner-all ui-state-default', // header classes
            icons: 'ui-icon', // icon class added to the <i> in the header
            sortNone: 'ui-icon-carat-2-n-s',
            sortAsc: 'ui-icon-carat-1-n',
            sortDesc: 'ui-icon-carat-1-s',
            active: 'ui-state-active', // applied when column is sorted
            hover: 'ui-state-hover', // hover class
            filterRow: '',
            even: 'ui-widget-content', // even row zebra striping
            odd: 'ui-state-default' // odd row zebra striping
        });
    },
    formatSuiteReport: function () {
        $("body").append("<div id='tooltip'><a id='close-tooltip' href='#'>Close</a><div id='tooltip-body'></div></div>");

        $("#close-tooltip").click(function (){
            $("#tooltip").hide();
            return false;
        });
        $(document).keydown(function(e) {
            if (e.keyCode == 27) {
                $("#tooltip").hide();
            }
        });

        $("ul.test-specs li.fail span, ul.test-specs li.warn span").click(function () {
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


        $("a.link-show-differences").click(function () {
            var sampleArea = $(this).attr("data-area");
            var sampleImage = $(this).attr("data-imagesource");
            var originalArea = $(this).closest("div.object").attr("data-area");
            var originalImage = $(this).closest("div.layout-report").attr("data-screenshot");
            var comparisonMapPath = $(this).attr("data-comparisonmap");
            
            Galen.showImageComparison(originalImage, originalArea, sampleImage, sampleArea, comparisonMapPath);
            return false;
        });
    },
    makeSliding: function (selector) {
        $(selector).click(Galen.onSlidingElementClicked);
    },
    onSlidingElementClicked: function () {
        $(this).next().slideToggle();
    },
    loadImage: function (path, callback) {
        var img = new Image();
        img.onload = function() {
            callback(this);
        };
        img.src = path;
    },
    readAreaFromText: function (text, defaultWidth, defaultHeight) {
        if (text != null && text.length > 0) {
            var area = eval("[" + text + "]");
            if (area.length == 4) {
                return area;
            }
        }
        return [0,0, defaultWidth, defaultHeight];
    },

    showImageComparison: function (originalImagePath, originalArea, sampleImagePath, sampleArea, comparisonMapPath) {
        Galen.loadImage(originalImagePath, function (originalImage) {
            Galen.loadImage(sampleImagePath, function (sampleImage) {
                $("#tooltip-body").html(
                "<input id='image-comparison-toggle-state' type='checkbox'/> <label for='image-comparison-toggle-state'>Put images on top of each other</label>" 
                + "<div class='image-comparison'>" 
                + "<b>Actual Image</b>" 
                + "<div class='image original'></div>"  
                + "<b>Expected</b>" 
                + "<div class='image sample'></div>"
                + "<b>Comparison Map</b>" 
                + "<div class='image comparisonmap'><img src='" + comparisonMapPath + "'/></div>"
                + "</div>");

                originalArea = Galen.readAreaFromText(originalArea, originalImage.width, originalImage.height); 
                sampleArea = Galen.readAreaFromText(sampleArea, sampleImage.width, sampleImage.height);

                renderImage = function (locator, imagePath, area) {
                    $(locator)
                    .css("background-image", "url(" + imagePath + ")")
                    .css("background-repeat", "no-repeat")
                    .css("background-position", (-area[0]) + "px " + (-area[1]) + "px")
                    .css("width", area[2] + "px")
                    .css("height", area[3] + "px");
                };

                renderImage("#tooltip-body .image-comparison .image.original", originalImagePath, originalArea);
                renderImage("#tooltip-body .image-comparison .image.sample", sampleImagePath, sampleArea);

                $("#tooltip-body .image-comparison .image.comparisonmap").css("width", originalArea[2]);


                $("#image-comparison-toggle-state").click(function () {
                    if ($(this).is(":checked")) {
                        $("#tooltip-body b").hide();

                        $("#tooltip-body .image-comparison .image.sample").addClass("layed-on-top");
                    }
                    else {
                        $("#tooltip-body b").show();
                        $("#tooltip-body .image-comparison .image.sample").removeClass("layed-on-top");
                    }
                });


                $("#tooltip").center();
                $("#tooltip").show();
            });
        });
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

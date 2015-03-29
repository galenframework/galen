




function createTemplate(templateId) {
    var source = document.getElementById(templateId).innerHTML;
    return Handlebars.compile(source);
}


function setHtml(id, html) {
    document.getElementById(id).innerHTML = html;
}

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

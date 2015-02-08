
function setHtml(id, html) {
    document.getElementById(id).innerHTML = html;
}



function createTemplate(templateId) {
    var source = document.getElementById(templateId).innerHTML;
    return Handlebars.compile(source);
}



var _GalenReport = {};

function safeHtml(html) {
    return new Handlebars.SafeString(html);
}
function createGalenReport() {
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
            return safeHtml(_GalenReport.tpl.sublayout(sublayout));
        }
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

    _GalenReport = {
        tpl: {
           main: createTemplate("report-tpl"),
           reportNode: createTemplate("report-node-tpl"),
           reportNodeText: createTemplate("report-node-text-tpl"),
           layout: createTemplate("report-layout-tpl"),
           layoutSection: createTemplate("report-layout-section-tpl"),
           layoutObject: createTemplate("report-layout-object-tpl"),
           layoutCheck: createTemplate("report-layout-check-tpl"),
           sublayout: createTemplate("report-layout-sublayout-tpl")
        },

        render: function (id, reportData) {
            setHtml(id, this.tpl.main(reportData));

            $("a.expand-link").click(function () {

                var container = $(this).next(".expand-container");
                if (container.length === 0) {
                    container = $(this).next().next(".expand-container");
                }
                container.slideToggle('fast');
            });
        }
    };

    return _GalenReport;
}

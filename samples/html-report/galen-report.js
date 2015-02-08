
function setHtml(id, html) {
    document.getElementById(id).innerHTML = html;
}



function createTemplate(templateId) {
    var source = document.getElementById(templateId).innerHTML;
    return Handlebars.compile(source);
}



var _GalenReport = {};
function createGalenReport() {
    Handlebars.registerHelper("renderNode", function (node) {
        if (node !== null && node !== undefined) {
            return new Handlebars.SafeString(_GalenReport.tpl.reportNode(node));
        }
        return "";
    });

    _GalenReport = {
        tpl: {
           main: createTemplate("report-tpl"),
           reportNode: createTemplate("report-node-tpl")
        },

        render: function (id, reportData) {
            setHtml(id, this.tpl.main(reportData));
        }
    };

    return _GalenReport;
}

/** This script is used in PageSpecReader **/
/*jslint nomen: true*/
/*global importClass, com, PageRule, StructNode, _pageSpecHandler*/

importClass(com.galenframework.speclang2.reader.pagespec.PageRule);
importClass(com.galenframework.parser.StructNode);

function _readDataFromProperties(properties) {
    "use strict";
    var data = {},
        it,
        entry,
        value;

    if (properties !== null) {
        it = properties.entrySet().iterator();

        while (it.hasNext()) {
            entry = it.next();
            if (entry.getValue() !== null) {
                value = entry.getValue().toString();
            } else {
                value = null;
            }
            data[entry.getKey()] = value;
        }
    }

    return data;
}

/*jslint unparam: true*/
this.rule = function (ruleExpression, callback) {
    "use strict";

    _pageSpecHandler.addRule(ruleExpression, new PageRule({
        callback: callback,

        apply: function (pageSpecHandler, ruleText, currentObjectName, properties) {

            var resultingNodes = [],
                data = _readDataFromProperties(properties),
                processor = {
                    addSpecs: function (specs) {
                        var i;

                        if (currentObjectName === null) {
                            throw new Error("The rule was used not on the object level");
                        }

                        for (i = 0; i < specs.length; i += 1) {
                            resultingNodes.push(new StructNode(specs[i]));
                        }
                    },
                    addObjectSpecs: function (objectName, specs) {
                        if (currentObjectName !== null) {
                            throw new Error("The rule was used on object level (" + currentObjectName + ")");
                        }

                        var objectNode = new StructNode(objectName + ":"),
                            i;

                        for (i = 0; i < specs.length; i += 1) {
                            objectNode.addChildNode(new StructNode(specs[i]));
                        }

                        resultingNodes.push(objectNode);
                    }
                };

            callback.call(processor, currentObjectName, data);

            return resultingNodes;
        }
    }));
};

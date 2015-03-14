/** This script is used in PageSpecReader **/
/*jslint nomen: true*/
/*global importClass, net, java, SpecReader, RuleProcessor, PageSection, ObjectSpecs, Properties, SpecGroup, _pageSpec*/

importClass(net.mindengine.galen.specs.reader.page.RuleProcessor);
importClass(net.mindengine.galen.specs.reader.SpecReader);
importClass(net.mindengine.galen.specs.page.PageSection);
importClass(net.mindengine.galen.specs.page.ObjectSpecs);
importClass(java.util.Properties);
importClass(net.mindengine.galen.specs.page.SpecGroup);

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

function _readSpec(specText) {
    "use strict";
    return new SpecReader(new Properties()).read(specText);
}

/*jslint unparam: true*/
this.rule = function (ruleExpression, callback) {
    "use strict";

    _pageSpec.addRuleProcessor(ruleExpression, new RuleProcessor({
        callback: callback,

        processRule: function (object, ruleText, varsContext, section, properties, contextPath, pageSpecReader) {

            var addedObjectSpecs = {
                    objects: [],
                    add: function (name, specs) {
                        var i, j;
                        for (i = 0; i < this.objects.length; i += 1) {
                            if (this.objects[i].name === name) {
                                for (j = 0; j < specs.length; j += 1) {
                                    this.objects[i].specs.push(specs[j]);
                                }
                                return;
                            }
                        }

                        this.objects.push({
                            name: name,
                            specs: specs
                        });
                    }
                },
                currentObjectName = object !== null ? object.getObjectName() : null,
                data = _readDataFromProperties(varsContext.getProperties()),
                processor = {
                    addSpecs: function (specs) {
                        var i, specGroup;

                        if (object === null) {
                            throw new Error("The rule was used not on the object level");
                        }

                        specGroup = new SpecGroup();
                        specGroup.setName(ruleText);
                        object.getSpecGroups().add(specGroup);

                        for (i = 0; i < specs.length; i += 1) {
                            specGroup.getSpecs().add(_readSpec(specs[i]));
                        }
                    },
                    addObjectSpecs: function (objectName, specs) {
                        addedObjectSpecs.add(objectName, specs);
                    }
                },
                ruleSection,
                i,
                j,
                objectSpecs;

            callback.call(processor, currentObjectName, data);


            if (addedObjectSpecs.objects.length > 0) {
                ruleSection = new PageSection();
                ruleSection.setName(ruleText);

                for (i = 0; i < addedObjectSpecs.objects.length; i += 1) {
                    objectSpecs = new ObjectSpecs(addedObjectSpecs.objects[i].name);

                    for (j = 0; j < addedObjectSpecs.objects[i].specs.length; j += 1) {
                        objectSpecs.getSpecs().add(_readSpec(addedObjectSpecs.objects[i].specs[j]));
                    }

                    ruleSection.getObjects().add(objectSpecs);
                }

                section.addSubSection(ruleSection);
            }
        }
    }));
};


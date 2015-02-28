/** This script is used in PageSpecReader **/
importClass(net.mindengine.galen.specs.reader.page.RuleProcessor);
importClass(java.lang.System);
importClass(net.mindengine.galen.specs.reader.SpecReader);
importClass(net.mindengine.galen.specs.page.PageSection);
importClass(net.mindengine.galen.specs.page.ObjectSpecs);
importClass(java.util.Properties);
importClass(net.mindengine.galen.specs.page.SpecGroup);

function _readDataFromProperties(properties) {
    var data = {},
        it,
        entry;

    if (properties != null) {
        it = properties.entrySet().iterator();

        while (it.hasNext()) {
            entry = it.next();
            data[entry.getKey()] = entry.getValue();
        }
    }


    return data;
}

function _readSpec(specText) {
    return new SpecReader(new Properties()).read(specText);
}

this.rule = function (ruleExpression, callback) {

    _pageSpec.addRuleProcessor(ruleExpression, new RuleProcessor({
        callback: callback,

        processRule: function (object, ruleText, varsContext, section, properties, contextPath, pageSpecReader) {

            var addedObjectSpecs = {
                objects: [],
                add: function (name, specs) {
                    for (var i = 0; i < this.objects.length; i += 1) {
                        if (this.objects[i].name === name) {
                            for (var j = 0; j < specs.length; j += 1) {
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
            };

            var currentObjectName = object != null? object.getObjectName() : null,
                data = _readDataFromProperties(varsContext.getProperties()),
                processor = {
                    addSpecs: function (specs) {
                        if (object === null) {
                            throw new Error("The rule was used not on the object level");
                        }

                        var specGroup = new SpecGroup();
                        specGroup.setName(ruleText);
                        object.getSpecGroups().add(specGroup);

                        for (var i = 0; i < specs.length; i += 1) {
                            specGroup.getSpecs().add(_readSpec(specs[i]));
                        }
                    },
                    addObjectSpecs: function (objectName, specs) {
                        addedObjectSpecs.add(objectName, specs);
                    }
                };

            callback.call(processor, currentObjectName, data);


            if (addedObjectSpecs.objects.length > 0) {
                var ruleSection = new PageSection();
                ruleSection.setName(ruleText);

                for (var i = 0; i < addedObjectSpecs.objects.length; i += 1) {
                    var object = new ObjectSpecs(addedObjectSpecs.objects[i].name);

                    for (var j = 0; j < addedObjectSpecs.objects[i].specs.length; j += 1) {
                        object.getSpecs().add(_readSpec(addedObjectSpecs.objects[i].specs[j]));
                    }

                    ruleSection.getObjects().add(object);
                }

                section.addSubSection(ruleSection);
            }
        }
    }));
};


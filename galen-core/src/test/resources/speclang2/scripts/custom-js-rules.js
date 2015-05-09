

rule("%{objectPattern} should be aligned horizontally", function (objectName, parameters) {
    var allObjects = findAll(parameters.objectPattern);

    for (var i = 1; i < allObjects.length; i++) {
        this.addObjectSpecs(allObjects[i].name, [
            "aligned horizontally all " + allObjects[i-1].name
        ]);
    }
});


rule("squared", function (objectName, parameters) {
    this.addSpecs([
        "width 100 % of " + objectName + "/height"
    ]);
});



rule("%{firstObject} and %{secondObject} should be aligned %{directionAndSides}", function (objectName, data) {

    this.addObjectSpecs(data.firstObject, [
        "aligned " + data.directionAndSides + ": " + data.secondObject
    ]);

});

rule("squared", function (objectName, data) {
    this.addSpecs([
        "width: 100% of " + objectName + "/height"
    ]);
});

importClass(net.mindengine.galen.components.JsTestRegistry);


JsTestRegistry.get().registerEvent("included.js was loaded");


this.includedObject = {
    name: "Included object"
};
importClass(com.galenframework.components.JsTestRegistry);
JsTestRegistry.get().registerEvent("included.js was loaded");


this.includedObject = {
    name: "Included object"
};
importClass(com.galenframework.components.JsTestRegistry);
load("included.js");


JsTestRegistry.get().registerEvent("From main name is visible as " + includedObject.name);

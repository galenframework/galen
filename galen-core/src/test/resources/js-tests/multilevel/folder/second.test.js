importClass(com.galenframework.components.JsTestRegistry);

// At this point the following script should be ignored
// as it was already loaded previously
load("../included.js");

JsTestRegistry.get().registerEvent("From second name is visible as " + includedObject.name);

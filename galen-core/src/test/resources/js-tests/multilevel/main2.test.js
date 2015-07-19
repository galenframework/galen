importClass(com.galenframework.components.JsTestRegistry);
load("/_test-js-multilevel/included.js");

JsTestRegistry.get().registerEvent("From main name is visible as " + includedObject.name);

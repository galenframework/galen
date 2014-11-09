importClass(net.mindengine.galen.components.JsTestRegistry);
JsTestRegistry.get().registerEvent("script is loaded");



this.customName = function () {
    return "name from script";
};
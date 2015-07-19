importClass(com.galenframework.components.DummyDriver);

var pageUrl = args[0];
var pageSize = args[1];


var driver = new DummyDriver();
driver.get(pageUrl);


driver;
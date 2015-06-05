
importClass(org.openqa.selenium.By);
importClass(org.openqa.selenium.remote.RemoteWebElement);

this.doubleLoadCheck = 0;

load("to-import-script.js");



load("to-import-script.js");

var text = arg.prefix + " typed by a selenium from javascript " + varFromImportedScript;

var inputField = driver.findElement(By.id("search-query"));

inputField.sendKeys(text);


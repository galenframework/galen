
importClass(org.openqa.selenium.By);
importClass(org.openqa.selenium.remote.RemoteWebElement);
importClass(net.mindengine.galen.browser.WebDriverWrapper);


var text = arg.prefix + " typed by a selenium from javascript";

var driver = new WebDriverWrapper(browser.getDriver())

var inputField = driver.findElement(By.id("search-query"));

inputField.sendKeys(text);


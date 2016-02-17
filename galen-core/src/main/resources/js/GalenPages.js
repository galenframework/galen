/*******************************************************************************
 * Copyright 2014 Ivan Shubin http://galenframework.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ******************************************************************************/

/*global GalenUtils, TestSession, By, Thread, Actions*/
/*jslint nomen: true*/
/*jslint newcap: true*/

(function (exports) {
    "use strict";

    String.prototype.trim = function () {
        return this.replace(/^\s+|\s+$/g, '');
    };


    var GalenPages = {
        settings: {
            cacheWebElements: true,
            allowReporting: true
        },
        Types: {
            List: "__GALEN_PAGES_TYPE_LIST__",
            Component: "__GALEN_PAGES_TYPE_COMPONENT__"
        },
        report: function (name, details) {
            if (GalenPages.settings.allowReporting) {
                var testSession = TestSession.current(),
                    node;
                if (testSession !== null) {
                    node = testSession.getReport().info(name);

                    if (details !== undefined && details !== null) {
                        node.withDetails(details);
                    }
                }
            }
        },
        Locator: function (type, value) {
            this.type = type;
            this.value = value;
        },
        identifyLocatorType: function (locatorText) {
            var symbol = locatorText.trim().charAt(0);

            if (symbol === "/") {
                return "xpath";
            }
            return "css";
        },
        parseLocator: function (locatorText) {
            var index = locatorText.indexOf(":"),
                typeText,
                value,
                type;
            if (index > 0) {
                typeText = locatorText.substr(0, index).trim();
                value = locatorText.substr(index + 1, locatorText.length - 1 - index).trim();
                type = "css";
                if (typeText === "id") {
                    type = typeText;
                } else if (typeText === "xpath") {
                    type = typeText;
                } else if (typeText === "css") {
                    type = typeText;
                } else {
                    throw new Error("Unknown locator type: " + typeText);
                }

                return new this.Locator(type, value);
            }
            return new this.Locator(this.identifyLocatorType(locatorText), locatorText);
        },
        toStringLocator: function (locator) {
            return locator.type + ": " + locator.value;
        },
        extendPage: function (page, driver, name, mainFields, secondaryFields) {
            var obj, key;
            /* Adding all the page functions defined in prototype */
            for (key in GalenPages.Page.prototype) {
                if (GalenPages.Page.prototype.hasOwnProperty(key)) {
                    page[key] = GalenPages.Page.prototype[key];
                }
            }

            obj = new GalenPages.Page(driver, name, mainFields, secondaryFields);

            for (key in obj) {
                if (obj.hasOwnProperty(key)) {
                    page[key] = obj[key];
                }
            }
        },

        convertLocator: function (galenLocator) {
            if (galenLocator.type === "id") {
                return By.id(galenLocator.value);
            }
            if (galenLocator.type === "css") {
                return By.cssSelector(galenLocator.value);
            }
            if (galenLocator.type === "xpath") {
                return By.xpath(galenLocator.value);
            }

            throw new Error("Unknown locator type: " + galenLocator.type);
        },

        convertTimeToMillis: function (userTime) {
            if (typeof userTime === "string") {
                var number = parseInt(userTime, 10),
                    type = userTime.replace(new RegExp("([0-9]| )", "g"), "");
                if (type === "") {
                    return number;
                }
                if (type === "m") {
                    return number * 60000;
                }
                if (type === "s") {
                    return number * 1000;
                }
                throw new Error("Cannot convert time. Unknown metric: " + type);
            }
            return userTime;
        },

        Wait: function (settings) {
            this.settings = settings;

            if (settings.time === undefined) {
                throw new Error("time was not defined");
            }

            var period = settings.period;
            if (period === undefined) {
                period = 1000;
            }

            this.message = null;
            if (typeof settings.message === "string") {
                this.message = settings.message;
            } else {
                this.message = "timeout error waiting for:";
            }

            this.time = GalenPages.convertTimeToMillis(settings.time);
            this.period = GalenPages.convertTimeToMillis(period);

            //conditions is a map of functions which should return boolean
            this.untilAll = function (conditions) {
                var waitFuncs = [],
                    property,
                    value,
                    t,
                    errors,
                    i;
                for (property in conditions) {
                    if (conditions.hasOwnProperty(property)) {
                        value = conditions[property];

                        waitFuncs[waitFuncs.length] = {
                            message: property,
                            func: value
                        };
                    }
                }

                if (waitFuncs.length > 0) {
                    t = 0;
                    while (t < this.time) {
                        t = t + this.period;
                        if (this._checkAll(waitFuncs)) {
                            return;
                        }
                        GalenPages.sleep(this.period);
                    }

                    errors = "";
                    for (i = 0; i < waitFuncs.length; i += 1) {
                        if (!this._applyConditionFunc(waitFuncs[i].func)) {
                            errors = errors + "\n  - " + waitFuncs[i].message;
                        }
                    }

                    if (errors.length > 0) {
                        throw new Error(this.message + errors);
                    }
                } else {
                    throw new Error("You are waiting for nothing");
                }
            };
            this.forEach = function (items, itemConditionName, conditionFunc) {
                var conditions = {},
                    i,
                    name,
                    applyConditionFunc = function () {
                        return this.conditionFunc(this.element);
                    };


                for (i = 0; i < items.length; i += 1) {
                    name = "#" + (i + 1) + " " + itemConditionName;
                    conditions[name] = {
                        element: items[i],
                        conditionFunc: conditionFunc,
                        apply: applyConditionFunc
                    };
                }

                this.untilAll(conditions);
            };

            this._checkAll = function (waitFuncs) {
                var i;
                for (i = 0; i < waitFuncs.length; i += 1) {
                    if (!this._applyConditionFunc(waitFuncs[i].func)) {
                        return false;
                    }
                }
                return true;
            };

            //Need this hack since sometimes it could be function and sometimes it could be an object with apply function inside
            this._applyConditionFunc = function (conditionFunc) {
                if (typeof conditionFunc === "function") {
                    return conditionFunc();
                }
                return conditionFunc.apply();
            };
        },
        wait: function (settings) {
            return new this.Wait(settings);
        },
        sleep: function (timeInMillis) {
            Thread.sleep(timeInMillis);
        }
    };

    GalenPages.Page = function (driver, name, mainFields, secondaryFields) {
        this.driver = driver;
        this.name = name;
        var thisPage = this,
            suffix = "";

        if (name !== undefined && name !== null && name.length > 0) {
            suffix = " on " + this.name;
        }

        this.initPageElements(mainFields, suffix, function (fieldNames) {
            thisPage._primaryFields = fieldNames;
        });
        this.initPageElements(secondaryFields, suffix, function (fieldNames) {
            thisPage._secondaryFields = fieldNames;
        });
    };
    GalenPages.Page.prototype.initPageElements = function (elementsMap, nameSuffix, elementsCollectedCallback) {
        var fieldNames = [],
            property,
            value;

        for (property in elementsMap) {
            if (elementsMap.hasOwnProperty(property)) {
                value = elementsMap[property];
                if (typeof value === "string") {
                    this[property] = new GalenPages.PageElement(property + nameSuffix, GalenPages.parseLocator(value), this);
                    fieldNames.push(property);
                } else if (value !== null && value.hasOwnProperty("__type__")) {
                    if (value.__type__ === GalenPages.Types.List) {
                        this[property] = new GalenPages.PageElementList(property + nameSuffix, GalenPages.parseLocator(value.locator), value.elementConstructor, this);
                    } else if (value.__type__ === GalenPages.Types.Component) {
                        this[property] = GalenPages.initPageElementComponent(property + nameSuffix, GalenPages.parseLocator(value.locator), value.elementConstructor, this, this.driver);
                    } else {
                        throw new Error("Unrecognized type: " + value.__type__);
                    }
                } else {
                    this[property] = value;
                }
            }
        }

        if (elementsCollectedCallback !== null && typeof elementsCollectedCallback  === "function") {
            elementsCollectedCallback(fieldNames);
        }
    };
    GalenPages.Page.prototype.getAllLocators = function () {
        var fieldNames = this._primaryFields.concat(this._secondaryFields),
            i,
            field,
            allFieldLocators = {};

        for (i = 0; i < fieldNames.length; i += 1) {
            field = this[fieldNames[i]];
            allFieldLocators[fieldNames[i]] = GalenPages.toStringLocator(field.locator);
        }

        return allFieldLocators;
    };
    GalenPages.Page.prototype.waitTimeout = "10s";
    GalenPages.Page.prototype.waitPeriod = "1s";
    GalenPages.Page.prototype._report = function (name) {
        try {
            GalenPages.report(name);
        } catch (err) {
            return;
        }
    };
    GalenPages.Page.prototype.open = function (url) {
        this._report("Open " + url);
        this.driver.get(url);
    };
    GalenPages.Page.prototype.findChild = function (locator) {
        if (typeof locator === "string") {
            locator = GalenPages.parseLocator(locator);
        }

        if (this.parent !== undefined) {
            return this.parent.findChild(locator);
        }

        try {
            var element = this.driver.findElement(GalenPages.convertLocator(locator));
            if (element === null) {
                throw new Error("No such element: " + locator.type + " " + locator.value);
            }
            return element;
        } catch (error) {
            throw new Error("No such element: " + locator.type + " " + locator.value);
        }
    };
    GalenPages.Page.prototype.findChildren = function (locator) {
        if (typeof locator === "string") {
            locator = GalenPages.parseLocator(locator);
        }

        if (this.parent !== undefined) {
            return this.parent.findChildren(locator);
        }

        var list = this.driver.findElements(GalenPages.convertLocator(locator));
        return list;
    };
    GalenPages.Page.prototype.set = function (props) {
        var property;
        for (property in props) {
            if (props.hasOwnProperty(property)) {
                this[property] = props[property];
            }
        }
        return this;
    };
    GalenPages.Page.prototype.waitForIt = function () {
        if (this._primaryFields.length > 0) {
            var conditions = {},
                i,
                pageName = this.name,
                applyConditionFunc = function () {
                    return this.field.exists() && this.field.isDisplayed();
                };

            for (i = 0; i < this._primaryFields.length; i += 1) {
                conditions[this._primaryFields[i] + " to be displayed"] = {
                    field: this[this._primaryFields[i]],
                    apply: applyConditionFunc
                };
            }

            if (pageName === null || pageName === undefined) {
                pageName = "page";
            }

            GalenPages.wait({time: this.waitTimeout, period: this.waitPeriod, message: "timeout waiting for " + pageName + " elements:"}).untilAll(conditions);
        } else {
            throw new Error("You can't wait for page as it does not have any fields defined");
        }
        return this;
    };

    GalenPages.PageElement = function (name, locator, parent) {
        this.name = name;
        this.cachedWebElement = null;
        this.locator = locator;
        this.parent = parent;
    };
    GalenPages.PageElement.prototype.isEnabled = function () {
        return this.getWebElement().isEnabled();
    };
    GalenPages.PageElement.prototype.attribute = function (attrName) {
        return this.getWebElement().getAttribute(attrName);
    };
    GalenPages.PageElement.prototype._report = function (name) {
        try {
            GalenPages.report(name, this.locator.type + ": " + this.locator.value);
        } catch (err) {
            return;
        }
    };
    GalenPages.PageElement.prototype.getDriver = function () {
        return this.parent.driver;
    };
    GalenPages.PageElement.prototype.hover = function () {
        var actions = new Actions(this.getDriver());
        actions.moveToElement(this.getWebElement()).perform();
    };
    GalenPages.PageElement.prototype.cssValue = function (cssProperty) {
        return this.getWebElement().getCssValue(cssProperty);
    };
    GalenPages.PageElement.prototype.click = function () {
        this._report("Click " + this.name);
        this.getWebElement().click();
    };
    GalenPages.PageElement.prototype.typeText = function (text) {
        this._report("Type \"" + text + "\" to " + this.name);
        this.getWebElement().sendKeys(text);
    };
    GalenPages.PageElement.prototype.getText = function () {
        return this.getWebElement().getText();
    };
    GalenPages.PageElement.prototype.clear = function () {
        this._report("Clear " + this.name);
        this.getWebElement().clear();
    };
    GalenPages.PageElement.prototype.getWebElement = function () {
        if (GalenPages.settings.cacheWebElements) {
            if (this.cachedWebElement === null) {
                this.cachedWebElement = this.parent.findChild(this.locator);
            }
            return this.cachedWebElement;
        }
        return this.parent.findChild(this.locator);
    };
    GalenPages.PageElement.prototype.isDisplayed = function () {
        return this.getWebElement().isDisplayed();
    };
    GalenPages.PageElement.prototype.selectByValue = function (value) {
        this._report("Select by value \"" + value + "\" in " + this.name);
        var option = this.getWebElement().findElement(By.xpath(".//option[@value=\"" + value + "\"]"));
        if (option !== null) {
            option.click();
        } else {
            throw new Error("Cannot find option with value \"" + value + "\"");
        }
    };
    GalenPages.PageElement.prototype.selectByText = function (text) {
        this._report("Select by text \"" + text + "\" in " + this.name);
        var option = this.getWebElement().findElement(By.xpath(".//option[normalize-space(.)=\"" + text + "\"]"));
        if (option !== null) {
            option.click();
        } else {
            throw new Error("Cannot find option with text \"" + text + "\"");
        }
    };
    GalenPages.PageElement.prototype.waitFor = function (func, messageSuffix, time) {
        time = time !== undefined ? time : "10s";
        var name = this.name !== undefined ? this.name : "",
            msg =  name + " " + messageSuffix,
            thisElement = this,
            conditions = {};
        conditions[msg] = function () {
            return func(thisElement);
        };
        GalenPages.wait({time: time, period: 200}).untilAll(conditions);
    };
    GalenPages.PageElement.prototype.waitToBeShown = function (time) {
        this.waitFor(function (thisElement) {
            return thisElement.exists() && thisElement.isDisplayed();
        }, "should be shown", time);
    };
    GalenPages.PageElement.prototype.waitToBeHidden = function (time) {
        this.waitFor(function (thisElement) {
            return !thisElement.exists() || !thisElement.isDisplayed();
        }, "should be hidden", time);
    };
    GalenPages.PageElement.prototype.waitUntilExists = function (time) {
        this.waitFor(function (thisElement) {
            return thisElement.exists();
        }, "should exist", time);
    };
    GalenPages.PageElement.prototype.waitUntilGone = function (time) {
        this.waitFor(function (thisElement) {
            return !thisElement.exists();
        }, "should not exist", time);
    };
    GalenPages.PageElement.prototype.exists = function () {
        try {
            this.getWebElement();
            return true;
        } catch (error) {
            return false;
        }
    };
    GalenPages.PageElement.prototype.findChild = function (locator) {
        if (typeof locator === "string") {
            locator = GalenPages.parseLocator(locator);
        }

        if (this.parent !== undefined) {
            return this.parent.findChild(locator);
        }
        try {
            var element = this.driver.findElement(GalenPages.convertLocator(locator));
            if (element === null) {
                throw new Error("No such element: " + locator.type + " " + locator.value);
            }
            return element;
        } catch (error) {
            throw new Error("No such element: " + locator.type + " " + locator.value);
        }
    };
    GalenPages.PageElement.prototype.findChildren = function (locator) {
        if (typeof locator === "string") {
            locator = GalenPages.parseLocator(locator);
        }

        if (this.parent !== undefined) {
            return this.parent.findChildren(locator);
        }

        var list = this.driver.findElements(GalenPages.convertLocator(locator));
        return list;
    };
    GalenPages.PageElement.prototype.insideFrame = function (callback) {
        var webElement = this.getWebElement(),
            driver = this.getDriver();
        driver.switchTo().frame(webElement);
        callback(this, driver);
        driver.switchTo().parentFrame();
    };
    GalenPages.PageElement.prototype.dragAndDropTo = function (dropField) {
        this._report("Drag  \"" + this.name + "\" and drop on \"" + dropField.name + "\"");
        var builder = new Actions(this.getDriver()),
            dropWebElement = dropField.getWebElement(),
            dragAndDrop = builder.clickAndHold(this.getWebElement())
                .moveToElement(dropWebElement)
                .release(dropWebElement)
                .build();

        dragAndDrop.perform();
    };

    GalenPages.PageElementList = function (name, locator, elementConstructor, parent) {
        this.name = name;
        this.locator = locator;
        this.elementConstructor = elementConstructor;
        this.parent = parent;
        this.cachedWebElements = null;
    };
    GalenPages.PageElementList.prototype.getWebElements = function () {
        if (this.cachedWebElements === null) {
            this.cachedWebElements = this.parent.findChildren(this.locator);
        }
        return this.cachedWebElements;
    };
    GalenPages.PageElementList.prototype.size = function () {
        return this.getWebElements().size();
    };
    GalenPages.PageElementList.prototype.get = function (index) {
        var elements = this.getWebElements(),
            pageElement,
            subPage,
            listElementName;

        if (index >= 0 && index < elements.size()) {
            pageElement = elements.get(index);

            listElementName = "#" + index + " of " + this.name;

            subPage = new this.elementConstructor(pageElement, {
                name: listElementName
            });
            subPage.name = listElementName;
            return subPage;
        }
        throw new Error("Index out of bounds: " + index);
    };

    GalenPages.initPageElementComponent = function (name, locator, elementConstructor, parent, driver) {

        var subDriver = {
                locator: locator,
                webElement: null,
                driver: driver,
                getWebElement: function () {
                    if (this.webElement === null) {
                        this.webElement = this.driver.findElement(GalenPages.convertLocator(locator));
                    }
                    return this.webElement;
                },
                findElement: function (by) {
                    return this.getWebElement().findElement(by);
                },
                findElements: function (by) {
                    return this.getWebElement().findElements(by);
                }
            },
            component = new elementConstructor(subDriver, {
                name: name
            }),
            pageElement = new GalenPages.PageElement(name, locator, parent),
            property;

        for (property in pageElement) {
            if (pageElement.hasOwnProperty(property)) {
                component[property] = pageElement[property];
            }
        }

        for (property in GalenPages.PageElement.prototype) {
            if (GalenPages.PageElement.prototype.hasOwnProperty(property)) {
                component[property] = GalenPages.PageElement.prototype[property];
            }
        }
        return component;
    };

    function insideFrame(driver, locator, callback) {
        var frameElement = driver.findElement(GalenPages.convertLocator(GalenPages.parseLocator(locator)));
        driver.switchTo().frame(frameElement);
        callback(driver);
        driver.switchTo().parentFrame();
    }


    function $page(name, mainFields, secondaryFields) {
        return function (driver, parent) {
            var fullName = name;
            if (parent !== undefined && parent !== null && parent.hasOwnProperty("name")) {
                fullName = name + " on " + parent.name;
            }
            GalenPages.extendPage(this, driver, fullName, mainFields, secondaryFields);
        };
    }

    function $list(elementConstructor, locator) {
        if (typeof elementConstructor !== "function") {
            throw new Error("First argument is not a function");
        }

        if (typeof locator !== "string") {
            throw new Error("Second argument is not a string");
        }

        return {
            __type__: GalenPages.Types.List,
            elementConstructor: elementConstructor,
            locator: locator
        };
    }

    function $component(elementConstructor, locator) {
        return {
            __type__: GalenPages.Types.Component,
            elementConstructor: elementConstructor,
            locator: locator
        };
    }

    exports.GalenPages = GalenPages;
    exports.insideFrame = insideFrame;
    exports.$page = $page;
    exports.$list = $list;
    exports.$component = $component;
}(this));

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



/*jslint nomen: true*/
/*global _galenCore, GalenTest, TestSuiteEvent, TestEvent, TestFilterEvent, TestRetryEvent, TestSession*/

(function (exports) {
    "use strict";
    function invokeFunc(object, args, callback) {
        if (args === undefined || args === null) {
            return callback.call(object);
        }

        switch (args.length) {
        case 0:
            return callback.call(object);
        case 1:
            return callback.call(object, args[0]);
        case 2:
            return callback.call(object, args[0], args[1]);
        case 3:
            return callback.call(object, args[0], args[1], args[2]);
        case 4:
            return callback.call(object, args[0], args[1], args[2], args[3]);
        case 5:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4]);
        case 6:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5]);
        case 7:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        case 8:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        case 9:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        case 10:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
        case 11:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
        case 12:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);
        case 13:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12]);
        case 14:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13]);
        case 15:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14]);
        case 16:
            return callback.call(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
        }
    }

    var GalenCore = {
        _invokeFunc: invokeFunc,

        settings: {
            parameterization: {
                stackBackwards: false
            }
        },
        futureGroups: {
            stack: [],
            push: function (value) {
                if (Array.isArray(value)) {
                    this.stack.push(value);
                } else {
                    this.stack.push([value]);
                }
            },
            pop: function () {
                if (this.stack.length > 0) {
                    this.stack.pop();
                }
            },
            fetchAll: function () {
                var groups = [],
                    i,
                    k;

                for (i = 0; i < this.stack.length; i += 1) {
                    for (k = 0; k < this.stack[i].length; k += 1) {
                        groups.push(this.stack[i][k]);
                    }
                }

                return groups;
            }
        },
        futureData: {
            stack: [],
            push: function (name, value) {
                this.stack.push({
                    name: name,
                    value: value
                });
            },
            pop: function () {
                if (this.stack.length > 0) {
                    this.stack.pop();
                }
            },
            fetchAll: function () {
                var data = {},
                    i,
                    name,
                    value,
                    k;
                for (i = 0; i < this.stack.length; i += 1) {
                    name = this.stack[i].name;
                    value = this.stack[i].value;

                    if (data[name] === null || data[name] === undefined) {
                        if (Array.isArray(value)) {
                            data[name] = value;
                        } else {
                            data[name] = [value];
                        }
                    } else {
                        if (Array.isArray(value)) {
                            for (k = 0; k < value.length; k += 1) {
                                data[name].push(value[k]);
                            }
                        } else {
                            data[name].push(value);
                        }
                    }
                }
                return data;
            }
        },
        parametersStack: {
            stack: [],
            add: function (parameters) {
                this.stack.push(parameters);
            },
            pop: function () {
                if (this.stack.length > 0) {
                    this.stack.pop();
                }
            },
            last: function () {
                if (this.stack.length > 0) {
                    return this.stack[this.stack.length - 1];
                }
                return null;
            },
            fetchAll: function () {
                var args = [],
                    i,
                    j;

                if (GalenCore.settings.parameterization.stackBackwards) {
                    for (i = this.stack.length - 1; i >= 0; i -= 1) {
                        for (j = 0; j < this.stack[i].length; j += 1) {
                            args.push(this.stack[i][j]);
                        }
                    }
                } else {
                    for (i = 0; i < this.stack.length; i += 1) {
                        for (j = 0; j < this.stack[i].length; j += 1) {
                            args.push(this.stack[i][j]);
                        }
                    }
                }
                return args;
            }
        },
        processVariable: function (varName) {
            var args = GalenCore.parametersStack.fetchAll(),
                i,
                value;

            if (args !== null) {
                for (i = 0; i < args.length; i += 1) {
                    if (args[i] !== null && typeof args[i] === "object") {
                        /*jslint evil:true */
                        value = eval("args[i]." + varName);
                        if (value !== undefined) {
                            return value;
                        }
                    }
                }
            }
            return "";
        },
        processExpression: function (name, expressionCallback) {
            var text = "",
                processing = true,
                id = 0,
                varName = "",
                // 0 - appending to text, 1 - appending to varName
                state = 0,
                sym;
            while (processing) {
                sym = name.charAt(id);
                if (sym === "$" && id < name.length - 1 && name.charAt(id + 1) === "{") {
                    varName = "";
                    id += 1;
                    state = 1;
                } else if (state === 1 && name.charAt(id) === "}") {
                    state = 0;
                    if (varName.length > 0) {
                        text = text + expressionCallback(varName);
                    }
                } else {
                    if (state === 0) {
                        text = text + sym;
                    } else {
                        varName = varName + sym;
                    }
                }

                id += 1;
                processing = (id < name.length);
            }
            return text;
        }
    };

    function test(name, callback) {
        var callbacks = [],
            aTest;
        if (typeof callback === "function") {
            callbacks = [callback];
        }
        aTest = {
            testName: GalenCore.processExpression(name, GalenCore.processVariable),
            callbacks: callbacks,
            arguments: GalenCore.parametersStack.fetchAll(),
            data: GalenCore.futureData.fetchAll(),
            testGroups: GalenCore.futureGroups.fetchAll(),
            on: function (args, callback) {
                if (Array.isArray(args)) {
                    this.arguments = args;
                } else {
                    this.arguments = [args];
                }

                return this.do(callback);
            },
            do: function (callback) {
                if (typeof callback === "function") {
                    this.callbacks.push(callback);
                }
                return this;
            },
            /* All the following functions are implementations of GalenTest interface */
            getName: function () {
                return this.testName;
            },
            getGroups: function () {
                return this.testGroups;
            },
            beforeTest: function () {
                return;
            },
            afterTest: function () {
                return;
            },
            execute: function (report, listener) {
                this.report = report || null;
                this.listener = listener || null;
                if (this.callbacks !== null) {
                    var i = 0;
                    for (i = 0; i < this.callbacks.length; i += 1) {
                        invokeFunc(this, this.arguments, this.callbacks[i]);
                    }
                }
            }
        };

        // A GalenTest is a Java interface which will be used by Galen Framework in order to execute js-based tests
        _galenCore.addTest(new GalenTest(aTest));
        return aTest;
    }

    function parameterizeByArray(rows, callback) {
        var i = 0;
        for (i = 0; i < rows.length; i += 1) {
            GalenCore.parametersStack.add(rows[i]);
            invokeFunc(this, rows[i], callback);
            GalenCore.parametersStack.pop();
        }
    }

    function parameterizeByMap(map, callback) {
        var key;
        for (key in map) {
            if (map.hasOwnProperty(key)) {

                GalenCore.parametersStack.add([map[key]]);
                callback(map[key]);
                GalenCore.parametersStack.pop();
            }
        }
    }

    function forAll(data, callback) {
        if (Array.isArray(data)) {
            return parameterizeByArray(data, callback);
        }
        return parameterizeByMap(data, callback);
    }

    function forOnly(data, callback) {
        if (Array.isArray(data)) {
            return parameterizeByArray([data], callback);
        }
        return parameterizeByArray([[data]], callback);
    }

    function beforeTestSuite(callback) {
        _galenCore.addBeforeTestSuiteEvent(new TestSuiteEvent({
            callback: {
                func: callback
            },
            execute: function () {
                this.callback.func();
            }
        }));
    }

    function afterTestSuite(callback) {
        _galenCore.addAfterTestSuiteEvent(new TestSuiteEvent({
            callback: {
                func: callback
            },
            execute: function () {
                this.callback.func();
            }
        }));
    }

    function beforeTest(callback) {
        _galenCore.addBeforeTestEvent(new TestEvent({
            callback: {
                func: callback
            },
            execute: function (test) {
                this.callback.func(test);
            }
        }));
    }

    function afterTest(callback) {
        _galenCore.addAfterTestEvent(new TestEvent({
            callback: {
                func: callback
            },
            execute: function (test) {
                this.callback.func(test);
            }
        }));
    }

    function retry(times, callback) {
        if (times > 0) {
            try {
                return callback(times);
            } catch (ex) {
                return retry(times - 1, callback);
            }
        }
        return callback(times);
    }

    function createTestDataProvider(varName) {
        return function (varValue, callback) {

            GalenCore.futureData.push(varName, varValue);
            callback();
            GalenCore.futureData.pop();
        };
    }

    function grouped(groups, callback) {
        GalenCore.futureGroups.push(groups);
        callback();
        GalenCore.futureGroups.pop();
    }

    function testFilter(callback) {
        _galenCore.addTestFilterEvent(new TestFilterEvent({
            callback: {
                func: callback
            },
            execute: function (tests) {
                return this.callback.func(tests);
            }
        }));
    }

    function testRetry(callback) {
        _galenCore.addTestRetryEvent(new TestRetryEvent({
            callback: {
                func: callback
            },
            shouldRetry: function (testName, retryCount) {
                return this.callback.func(testName, retryCount);
            }
        }));
    }

    function logged(title, callback) {
        var report = TestSession.current().getReport(),
            result;
        report.sectionStart(title);
        result = callback(report);
        report.sectionEnd();
        return result;
    }

    /*jslint unparam: true*/
    function loggedFunction(title, callback) {
        return function (_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16) {
            var args = arguments,
                that = this,
                convertedTitle = GalenCore.processExpression(title, function (expressionText) {
                    try {
                        /*jslint evil:true */
                        return eval(expressionText);
                    } catch (ex) {
                        return "";
                    }
                });

            logged(convertedTitle, function () {
                GalenCore._invokeFunc(that, args, callback);
            });
        };
    }
    /*jslint unparam: false*/

    exports.test = test;
    exports.forAll = forAll;
    exports.forOnly = forOnly;
    exports.beforeTestSuite = beforeTestSuite;
    exports.afterTestSuite = afterTestSuite;
    exports.beforeTest = beforeTest;
    exports.afterTest = afterTest;
    exports.retry = retry;
    exports.createTestDataProvider = createTestDataProvider;
    exports.GalenCore = GalenCore;
    exports.testFilter = testFilter;
    exports.testRetry = testRetry;
    exports.grouped = grouped;
    exports.logged = logged;
    exports.loggedFunction = loggedFunction;
}(this));

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

var GalenCore = {
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
        last: function() {
            if (this.stack.length > 0) {
                return this.stack[this.stack.length - 1];
            }
            else return null;
        }
    },
    processVariable: function (varName) {
        var args = GalenCore.parametersStack.last();

        if (args != null) {
            if (args.length == 1) {
                if (args[0] != null && typeof args[0] == "object") {
                    var argObject = args[0];

                    var value = eval("argObject." + varName);
                    return value;
                }
            }
        }
        return "";
    },
    processTestName: function (name) {
        var parameters = this.parametersStack.last();
        if (parameters != null) {

            var text = "";

            var processing = true;

            var id = 0;

            var varName = "";
            // 0 - appending to text, 1 - appending to varName
            var state = 0;
            while(processing) {
                var sym = name.charAt(id);
                if (sym == "$" && id < name.length - 1 && name.charAt(id + 1) == "{") {
                    varName = "";
                    id++;
                    state = 1;
                }
                else if (state == 1 && name.charAt(id) == "}") {
                    state = 0;
                    if (varName.length > 0) {
                        text = text + GalenCore.processVariable(varName);
                    }
                }
                else {
                    if (state == 0) {
                        text = text + sym;
                    }
                    else {
                        varName = varName + sym;
                    }

                }

                id++;
                processing = (id < name.length);
            }

            return text;
        }
        else return name;
    }
};

function test(name, callback) {
    var callbacks = [];
    if (typeof callback == "function") {
        callbacks = [callback];
    }
    var aTest = {
        testName: GalenCore.processTestName(name),
        callbacks: callbacks,
        arguments: GalenCore.parametersStack.last(),
        on: function (arguments, callback) {
            if (Array.isArray(arguments)){
                this.arguments = arguments;
            } else {
                this.arguments = [arguments];
            }

            return this.do(callback);
        },
        do: function (callback) {
            if (typeof callback == "function") {
                this.callbacks.push(callback);
            }
            return this;
        },
        /* All the following functions are implementations of GalenTest interface */
        getName: function () {
            return this.testName;
        },
        beforeTest: function () {
        },
        afterTest: function ()  {
        },
        checkLayout: function (driver, specFile, includedTags, excludedTags) {
            return _galenCore.checkLayout(this.report, driver, includedTags, excludedTags);
        },
        execute: function (report, listener) {
            this.report = report || null;
            this.listener = listener || null;
            if (this.callbacks != null) {
                for (var i = 0; i < this.callbacks.length; i++) {
                    invokeFunc(this.arguments, this.callbacks[i]);
                }
            }
        }
    };

    // A GalenTest is a Java interface which will be used by Galen Framework in order to execute js-based tests
    _galenCore.addTest(new GalenTest(aTest));
    return aTest;
};

function parameterizeByArray(rows, callback) {
    for (var i = 0; i<rows.length; i++) {
        GalenCore.parametersStack.add(rows[i]);
        invokeFunc(rows[i], callback);
        GalenCore.parametersStack.pop();
    }
};

function parameterizeByMap(map, callback) {
    for (key in map) {
        if (map.hasOwnProperty(key)) {

            GalenCore.parametersStack.add([map[key]]);
            callback(map[key]);
            GalenCore.parametersStack.pop();
        }
    }
}

function invokeFunc(args, callback) {
    if (args == undefined || args == null) {
        return callback();
    }

    switch(args.length) {
    case 0: return callback();
    case 1: return callback(args[0]);
    case 2: return callback(args[0], args[1]);
    case 3: return callback(args[0], args[1], args[2]);
    case 4: return callback(args[0], args[1], args[2], args[3]);
    case 5: return callback(args[0], args[1], args[2], args[3], args[4]);
    case 6: return callback(args[0], args[1], args[2], args[3], args[4], args[5]);
    case 7: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
    case 8: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
    case 9: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
    case 10: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
    case 11: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
    case 12: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);
    case 13: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12]);
    case 14: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13]);
    case 15: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14]);
    case 16: return callback(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
    }
}

function forAll (data, callback) {
    if (Array.isArray(data)) {
        return parameterizeByArray(data, callback);
    }
    else {
        return parameterizeByMap(data, callback);
    }
};

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

function beforeTest(callback)  {
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
            return callback();
        }
        catch (ex) {
            return retry(times - 1, callback);
        }
    }
    else return callback();
}



(function (exports) {
    exports.test = test;
    exports.forAll = forAll;
    exports.beforeTestSuite = beforeTestSuite;
    exports.afterTestSuite = afterTestSuite;
    exports.beforeTest = beforeTest;
    exports.afterTest = afterTest;
    exports.retry = retry;
})(this);

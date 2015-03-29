
function assertThat(message, obj) {
    return {
        is: function (anotherObject) {
            var real = JSON.stringify(obj);
            var expected = JSON.stringify(anotherObject);
            if (real != expected) {
                throw new Error("asserting: " + message + "\n real is    : " + real + "\n expected is: " + expected);
            }
        },
        typeIs: function (expectedType) {
            var realType = typeof obj;
            if (realType != expectedType) {
                throw new Error("asserting: " + message + "\n real type is: " + realType + "\n expected type is: " + expectedType);
            }
        },
        hasFields: function (fields) {
            for (var i=0; i<fields.length; i++) {
                if (obj[fields[i]] == undefined) {
                    throw new Error("asserting: " + message + "\n object does not have a field: " + fields[i]);
                }
            }
        }
    }
}

function assertError(func) {
    return {
        is: function (expectedErrorMessage) {
            var catchedError = null;
            try {
                func();
            }
            catch(error) {
                catchedErrorMessage = error.message;
            }

            assertThat("Should throw error", catchedErrorMessage).is(expectedErrorMessage);
        }
    }
}

(function (exports) {
    exports.assertThat = assertThat;
    exports.assertError = assertError;
})(this);

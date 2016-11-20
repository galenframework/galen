function forMap(map, callback) {
    if (map !== undefined && map !== null) {
        for (var key in map) {
            if (map.hasOwnProperty(key)) {
                callback(key, map[key]);
            }
        }
    }
}

function forArray(array, callback) {
    if (array !== undefined && array !== null) {
        for (var i = 0; i < array.length; i += 1) {
            callback(array[i], i);
        }
    }
}

function copyProperties(dest, source) {
    forMap(source, function (name, value) {
        dest[name] = value;
    });
}

(function (export) {
    export.forMap = forMap;
    export.forArray = forArray;
    export.copyProperties = copyProperties;
})(this);

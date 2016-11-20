load("basics.js");

function Device(settings) {
    this.deviceName = settings.deviceName;
    this.tags = settings.tags;
    this.excludedTags = settings.excludedTags;
    this.initDriver = settings.initDriver;
    this.quit = settings.quit;
}
Device.prototype.withProperty = function (propName, value) {
    this[propName] = value;
    return this;
};

function inLocalBrowser(name, size, tags, browserType) {
    return new Device({
        deviceName: name,
        tags: tags,
        size: size,
        initDriver: function (url) {
            this.driver = createDriver(url, size, browserType);
            return this.driver;
        },
        quit: function () {
            this.driver.quit();
        }
    });
}

function inSeleniumGrid(gridUrl, deviceName, tags, gridSettings) {
    return new Device({
        deviceName: deviceName,
        tags: tags,
        initDriver: function (url) {
            this.driver = createGridDriver(gridUrl, gridSettings);
            return this.driver;
        },
        quit: function () {
            this.driver.quit();
        }
    });
}

function convertGridDevices(devicesJson, gridUrl) {
    if (gridUrl === undefined || gridUrl === null) {
        throw new Error("Missing gridUrl argument");
    }
    var devices = {};
    forMap(devicesJson, function (deviceName, deviceSettings) {
        devices[deviceName] = inSeleniumGrid(gridUrl, deviceSettings.deviceName, deviceSettings.tags, deviceSettings.gridSettings);
    });
    return devices;
}

function loadGridDevices(configPath, gridUrl) {
    if (gridUrl === undefined || gridUrl === null) {
        throw new Error("Missing gridUrl argument");
    }
    if (fileExists(configPath)) {
        var devicesText = readFile(configPath);
        var devicesJson = JSON.parse(devicesText);
        return convertGridDevices(devicesJson, gridUrl);
    } else {
        throw new Error("Devices file not found: " + configPath);
    }
}



var _globalSingleDriver = null;
function inSingleBrowser(name, size, tags) {
    return new Device({
        deviceName: name,
        tags: tags,
        size: size,
        initDriver: function (url) {
            if (_globalSingleDriver === null) {
                _globalSingleDriver = createDriver(url, size);
            }
            this.driver = _globalSingleDriver;

            if (url !== null) {
                this.driver.get(url);
            }
            
            if (size !== null) {
                resize(this.driver, size);
            }

            return this.driver;
        },
        quit: function () {
        }
    });
}
afterTestSuite(function () {
    if (_globalSingleDriver !== null) {
        _globalSingleDriver.quit();
        _globalSingleDriver = null;
    }
});


(function (export) {
    export.inLocalBrowser = inLocalBrowser;
    export.inSeleniumGrid = inSeleniumGrid;
    export.inSingleBrowser = inSingleBrowser;
    export.Device = Device;
    export.loadGridDevices = loadGridDevices;
})(this);

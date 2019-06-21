Array.prototype.pushAll = function (arr) {
    if (arr && arr.length > 0) {
        for (var i = 0; i < arr.length; i++) {
            this.push(arr[i]);
        }
    }
};
var ColorPicker = {
    palette: [
        "#B55CFF", "#FF5C98", "#5C9AFF", "#5CE9FF", "#5CFFA3", "#98FF5C", "#FFE95C", "#FFA05C"
    ],
    pickColor: function(index) {
        return this.palette[index % this.palette.length];
    }
};

var MetaUtils = {
    isEdgeVertical: function(edgeName) {
        edgeName = edgeName.toLowerCase();
        return (edgeName === 'left' || edgeName === 'right');
    },
    isEdgeHorizontal: function(edgeName) {
        edgeName = edgeName.toLowerCase();
        return (edgeName === 'top' || edgeName === 'bottom');
    },
    fetchEdge: function(area, edgeName) {
        edgeName = edgeName.toLowerCase();
        if (edgeName === 'left') {
            return {x1: area[0], y1: area[1], x2: area[0], y2: area[1] + area[3]};
        } else if (edgeName === 'right') {
            return {x1: area[0] + area[2], y1: area[1], x2: area[0] + area[2], y2: area[1] + area[3]};
        } else if (edgeName === 'top') {
            return {x1: area[0], y1: area[1], x2: area[0] + area[2], y2: area[1]};
        } else if (edgeName === 'bottom') {
            return {x1: area[0], y1: area[1] + area[3], x2: area[0] + area[2], y2: area[1] + area[3]};
        } else {
            console.log('Unknown edge: ' + edgeName);
            return null;
        }
    },
    horizontalArrowGuide: function (x, y, direction) {
        var arrowLength = 10;
        var arrowWidth = 3;
        return [{
            type: 'line',
            x1: x, y1: y,
            x2: x + arrowLength * direction, y2: y + arrowWidth
        },{
            type: 'line',
            x1: x, y1: y,
            x2: x + arrowLength * direction, y2: y - arrowWidth
        }];
    },
    verticalArrowGuide: function (x, y, direction) {
        var arrowLength = 10;
        var arrowWidth = 3;
        return [{
            type: 'line',
            x1: x, y1: y,
            x2: x + arrowWidth, y2: y + arrowLength * direction
        },{
            type: 'line',
            x1: x, y1: y,
            x2: x - arrowWidth, y2: y + arrowLength * direction
        }];
    },
    createVerticalDistanceGuides: function (edgeA, edgeB, expectedText, realText) {
        var distance = Math.abs(edgeA.x1 - edgeB.x1);

        var midTop = 0;
        if (edgeA.y1 >= edgeB.y1 && edgeA.y2 <= edgeB.y2) { // edgeA is inside edgeB
            midTop = Math.abs(edgeA.y1 + edgeA.y2) / 2;
        } else if (edgeB.y1 >= edgeA.y1 && edgeB.y2 <= edgeA.y2) { // edgeB is inside edgeA
            midTop = Math.abs(edgeB.y1 + edgeB.y2) / 2;
        } else {
            var midTop1 = Math.round((edgeA.y1 + edgeB.y2) / 2.0);
            var midTop2 = Math.round((edgeA.y2 + edgeB.y1) / 2.0);
            if (Math.abs(midTop1 - edgeA.y1) > Math.abs(midTop2 - edgeA.y2)) {
                midTop = midTop2;
            } else {
                midTop = midTop1;
            }
        }

        var guides = [];
        if (distance > 30) {
            guides.push({
                type: 'line',
                x1: edgeA.x1, y1: midTop,
                x2: edgeB.x1, y2: midTop
            });
            guides.push({
                type: 'text',
                text: realText,
                x: Math.round((edgeA.x1 + edgeB.x1) / 2 - (realText.length * 7) / 2),
                y: midTop - 5,
                vertical: false
            });
            guides.pushAll(this.horizontalArrowGuide(edgeA.x1, midTop, 1));
            guides.pushAll(this.horizontalArrowGuide(edgeB.x1, midTop, -1));
        } else {
            guides.push({
                type: 'line',
                x1: edgeA.x1 - 30, y1: midTop,
                x2: edgeB.x1 + 30, y2: midTop
            });
            guides.push({
                type: 'text',
                text: realText,
                x: edgeB.x1 + 4,
                y: midTop - 5,
                vertical: false
            });
            guides.pushAll(this.horizontalArrowGuide(edgeA.x1, midTop, -1));
            guides.pushAll(this.horizontalArrowGuide(edgeB.x1, midTop, 1));
        }
        return guides;
    },
    createHorizontalDistanceGuides: function (edgeA, edgeB, expectedText, realText) {
        var distance = Math.abs(edgeA.y1 - edgeB.y1);
        var midLeft = 0;
        if (edgeA.x1 >= edgeB.x1 && edgeA.x2 <= edgeB.x2) { // edgeA is inside edgeB
            midLeft = Math.abs(edgeA.x1 + edgeA.x2) / 2;
        } else if (edgeB.x1 >= edgeA.x1 && edgeB.x2 <= edgeA.x2) { // edgeB is inside edgeA
            midLeft = Math.abs(edgeB.x1 + edgeB.x2) / 2;
        } else {
            var midLeft1 = Math.round((edgeA.x1 + edgeB.x2) / 2.0);
            var midLeft2 = Math.round((edgeA.x2 + edgeB.x1) / 2.0);
            if (Math.abs(midLeft1 - edgeA.x1) > Math.abs(midLeft2 - edgeA.x2)) {
                midLeft = midLeft2;
            } else {
                midLeft = midLeft1;
            }
        }

        var guides = [];
        if (distance > 30) {
            guides.push({
                type: 'line',
                x1: midLeft, y1: edgeA.y1,
                x2: midLeft, y2: edgeB.y1
            });
            guides.push({
                type: 'text',
                text: realText,
                x: midLeft - 8,
                y: Math.round((edgeA.y1 + edgeB.y1) / 2 + (realText.length * 7) / 2),
                vertical: true
            });
            guides.pushAll(this.verticalArrowGuide(midLeft, edgeA.y1, 1));
            guides.pushAll(this.verticalArrowGuide(midLeft, edgeB.y1, -1));
        } else {
            guides.push({
                type: 'line',
                x1: midLeft, y1: edgeA.y1 - 30,
                x2: midLeft, y2: edgeB.y1 + 30
            });
            guides.push({
                type: 'text',
                text: realText,
                x: midLeft - 8,
                y: edgeB.y1 - 4,
                vertical: true
            });
            guides.pushAll(this.verticalArrowGuide(midLeft, edgeA.y1, -1));
            guides.pushAll(this.verticalArrowGuide(midLeft, edgeB.y1, 1));
        }
        return guides;
    }
};

var _ = {
    map: function (arr, func) {
        var newArray = [];
        for (var i = 0; i < arr.length; i++) {
            newArray.push(func(arr[i], i));
        }
        return newArray;
    },

    mapNonNull: function (arr, func) {
        var newArray = [];
        for (var i = 0; i < arr.length; i++) {
            var item = func(arr[i], i);
            if (item !== null && item !== undefined) {
                newArray.push(item);
            }
        }
        return newArray;
    },

    forEachInObject: function (obj, func) {
        if (obj) {
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    func(obj[key], key);
                }
            }
        }
    },

    forEach: function (arr, callback) {
        if (arr && arr.length > 0) {
            for (var i = 0; i < arr.length; i++) {
                callback(arr[i], i);
            }
        }
    },

    contains: function (arr, value) {
        if (arr && arr.length > 0) {
            for (var i = 0; i < arr.length; i++) {
                if (arr[i] === value) {
                    return true;
                }
            }
        }
        return false;
    }
};


function formatTime(timeInMillis) {
    if (timeInMillis !== null && timeInMillis !== undefined) {
    var date = new Date(timeInMillis);
        var hh = date.getHours();
        var mm = date.getMinutes();
        var ss = date.getSeconds();
        if (hh < 10) {hh = "0"+hh;}
        if (mm < 10) {mm = "0"+mm;}
        if (ss < 10) {ss = "0"+ss;}
        return hh + ":" + mm + ":" + ss;
    }
    return "";
}
function convertToPlusMinus(expanded) {
    if (expanded) {
        return '-';
    } else {
        return '+';
    }
}

function toggleReportNode(node) {
    node.expanded = !node.expanded;
}

Vue.component('image-comparison-popup', {
    props: ['imagedata'],
    template: '#tpl-image-comparison-popup',
    mounted() {
        document.addEventListener('keydown', this.onKeyPress);
    },
    beforeDestroy() {
        document.removeEventListener('keydown', this.onKeyPress);
    },
    data: function () {
        var clientWidth = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
        var clientHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
        return {
            position: {
                top: 5,
                left: 50,
                width: clientWidth - 100,
                height: clientHeight - 10
            }
        };
    },
    methods: {
        onKeyPress: function (event) {
            if (event.key === 'Escape') {
                this.$emit('close');
            }
        }
    }
});

Vue.component('screenshot-popup', {
    props: ['screenshot', 'highlight', 'guides', 'spec'],
    template: '#tpl-screenshot-popup',
    mounted() {
        document.addEventListener('keydown', this.onKeyPress);
    },
    beforeDestroy() {
        document.removeEventListener('keydown', this.onKeyPress);
    },
    data: function () {
        var clientWidth = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
        var clientHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);

        return {
            position: {
                top: 5,
                left: 50,
                width: clientWidth - 100,
                height: clientHeight - 10
            },
            plot: {
                width: 100,
                height: 100
            },
            crop: {
                width: Math.max(400, this.highlight.boundaryBox.width) + 60,
                height: Math.max(400, this.highlight.boundaryBox.height) + 60,
                offsetLeft: 30 - this.highlight.boundaryBox.min.x,
                offsetTop: 30 - this.highlight.boundaryBox.min.y,
            }
        };
    },
    methods: {
        onKeyPress: function (event) {
            if (event.key === 'Escape') {
                this.$emit('close');
            }
        }
    }
});

Vue.component('mutation-report', {
    props: ['report'],
    template: '#tpl-mutation-report',
    data: function () {
        var mutations = [];
        _.forEachInObject(this.report.mutationReport.objectMutationStatistics, function (objectStats, objectName) {
            mutations.push({
                name: objectName,
                passed: objectStats.passed,
                failed: objectStats.failed,
                failedRatio: Math.round(objectStats.failed * 1000.0 / Math.max(1, objectStats.passed + objectStats.failed)) / 10,
                failedMutations: objectStats.failedMutations
            });
        });
        return {
            tableColumns: [{
                name: 'Object', field: 'name',
            },{
                name: 'Passed', field: 'passed'
            },{
                name: 'Failed', field: 'failed'
            },{
                name: 'Ratio', field: 'failedRatio'
            }],
            sorting: {
                columnField: '',
                order: 1
            },
            showFailedMutationsFor: '',
            mutations: mutations
        };
    },
    methods: {
        toggleReportNode: toggleReportNode,
        sortTable: function (column) {
            var self = this;
            if (this.sorting.columnField === column.field) {
                this.sorting.order = -this.sorting.order;
            } else {
                this.sorting.columnField = column.field;
                this.sorting.order = 1;
            }
            this.mutations.sort(function (a, b) {
                var valueA = a[column.field];
                var valueB = b[column.field];
                var diff = valueA > valueB ? 1: -1;
                return diff * self.sorting.order;
            });
        },
        toggleMutationFor: function (objectName) {
            if (this.showFailedMutationsFor === objectName) {
                this.showFailedMutationsFor = '';
            } else {
                this.showFailedMutationsFor = objectName;
            }
        }
    },
    filters: {
        formatTime: formatTime,
        roundFailedRatio: function (value) {
            return Math.round(value * 100) / 100;
        }
    }
});

Vue.component('layout-section', {
    props: ['layout', 'section', 'bus'],
    template: '#tpl-layout-section',
    methods: {
        toggleReportNode: toggleReportNode
    }
});

Vue.component('object-node', {
    props: ['layout', 'object', 'bus'],
    template: '#tpl-object-node',
    methods: {
        toggleReportNode: toggleReportNode
    }
});

Vue.component('layout-spec', {
    props: ['layout', 'spec', 'bus'],
    template: '#tpl-layout-spec',
    data: function () {
        return {
            isFailed: this.spec.errors && this.spec.errors.length > 0,
            imageComparisonShown: false
        };
    },
    methods: {
        showSpec: function() {
            this.bus.$emit('spec-clicked', this.spec, this.layout);
        }
    }
});

Vue.component('layout-spec-group', {
    props: ['layout', 'specgroup', 'bus'],
    template: '#tpl-spec-group',
    methods: {
        toggleReportNode: toggleReportNode
    }
});

Vue.component('layout-report', {
    props: ['layout'],
    template: '#tpl-layout-report',

    data: function () {
        return {
            bus: new Vue(),
            screenshotPopup: {
                shown: false,
                spec: null,
                screenshotFile: null
            }
        };
    },
    created: function() {
        this.bus.$on('spec-clicked', this.specClicked)
    },
    methods: {
        toggleReportNode: toggleReportNode,
        collectHighlightAreas: function (objectNames, layout) {
            var self = this;
            var boundaryBox = {
                min: {x: 1000000, y: 1000000},
                max: {x: 0, y: 0}
            };
            var objects = _.mapNonNull(objectNames, function (objectName, index) {
                if (layout.objects.hasOwnProperty(objectName)) {
                    var area = layout.objects[objectName].area;
                    if (area) {
                        var item = {
                            name: objectName,
                            area: {x: area[0], y: area[1], width: area[2], height: area[3]},
                            color: ColorPicker.pickColor(index),
                            border: true,
                            fill: false,
                            caption: true
                        };

                        boundaryBox.min.x = Math.min(boundaryBox.min.x, item.area.x);
                        boundaryBox.min.y = Math.min(boundaryBox.min.y, item.area.y);
                        boundaryBox.max.x = Math.max(boundaryBox.max.x, item.area.x + item.area.width);
                        boundaryBox.max.y = Math.max(boundaryBox.max.y, item.area.y + item.area.height);
                        return item;
                    }
                }
                return null;
            });

            boundaryBox.width = boundaryBox.max.x - boundaryBox.min.x;
            boundaryBox.height = boundaryBox.max.y - boundaryBox.min.y;
            return {
                objects: objects,
                boundaryBox: boundaryBox
            };
        },
        findObjectArea: function(objectName, layout) {
            if (layout.objects.hasOwnProperty(objectName)) {
                return layout.objects[objectName].area;
            }
            return null;
        },
        collectMetaGuides: function(meta, layout) {
            var self = this;
            return _.mapNonNull(meta, function (metaEntry) {
                var fromArea = self.findObjectArea(metaEntry.from.object, layout);
                var toArea = self.findObjectArea(metaEntry.to.object, layout);
                if (fromArea && toArea) {
                    var fromEdge = MetaUtils.fetchEdge(fromArea, metaEntry.from.edge);
                    var toEdge = MetaUtils.fetchEdge(toArea, metaEntry.to.edge);
                    if (MetaUtils.isEdgeVertical(metaEntry.from.edge) && MetaUtils.isEdgeVertical(metaEntry.to.edge)) {
                        return MetaUtils.createVerticalDistanceGuides(fromEdge, toEdge, metaEntry.expectedDistance, metaEntry.realDistance);
                    } else if (MetaUtils.isEdgeHorizontal(metaEntry.from.edge) && MetaUtils.isEdgeHorizontal(metaEntry.to.edge)) {
                        return MetaUtils.createHorizontalDistanceGuides(fromEdge, toEdge, metaEntry.expectedDistance, metaEntry.realDistance);
                    }
                }
                return null;
            });
        },
        specClicked: function(spec, layout) {
            this.screenshotPopup.metaGuides = [];
            if (spec.highlight && spec.highlight.length > 0) {
                this.screenshotPopup.highlightAreas = this.collectHighlightAreas(spec.highlight, layout);
            }
            if (spec.meta && spec.meta.length > 0) {
                this.screenshotPopup.metaGuides = this.collectMetaGuides(spec.meta, layout);
            }
            this.screenshotPopup.spec = spec;
            this.screenshotPopup.shown = true;
            this.screenshotPopup.screenshotFile = this.layout.screenshot;
        },
        showHeatMap: function() {
            this.screenshotPopup.spec = null;
            this.screenshotPopup.metaGuides = [];
            this.screenshotPopup.highlightAreas = this.collectObjectsForHeatmap(this.layout);
            this.screenshotPopup.screenshotFile = this.layout.screenshot;
            this.screenshotPopup.shown = true;
        },
        showFailureMap: function() {
            this.screenshotPopup.spec = null;
            this.screenshotPopup.metaGuides = [];
            this.screenshotPopup.highlightAreas = this.collectObjectsForFailureMap(this.layout);
            this.screenshotPopup.screenshotFile = this.layout.screenshot;
            this.screenshotPopup.shown = true;
        },
        visitEachSpec: function(sections, callback) {
            if (sections !== null && sections !== undefined) {
                for (var i = 0; i < sections.length; i++) {
                    if (sections[i].sections != undefined && sections[i].sections != null) {
                        this.visitEachSpec(sections[i].sections, callback);
                    }

                    for (var j = 0; j < sections[i].objects.length; j++) {
                        for (var k = 0; k < sections[i].objects[j].specs.length; k++) {
                            callback(sections[i].objects[j].specs[k]);
                        }
                    }
                }
            }
        },
        sortByArea: function(objects) {
            return objects.sort(function (a, b) {
                return b.area.width*b.area.height - a.area.width*a.area.height;
            });
        },
        rgb2hex: function(colorArray) {
            return "#" +
                ("0" + colorArray[0].toString(16)).slice(-2) +
                ("0" + colorArray[1].toString(16)).slice(-2) +
                ("0" + colorArray[2].toString(16)).slice(-2);
        },
        interpolateArray: function(t, arr1, arr2) {
            var result = [];
            for (var i = 0; i < arr1.length && i < arr2.length; i++) {
                result.push((1.0 - t) * arr1[i]  + t * arr2[i]);
            }
            return result;
        },
        pickHeatColor: function(value) {
            var max = 6;
            var _t = Math.min(value/max, 1.0);
            var lowColor = [0, 215, 252];
            var midColor = [252, 206, 0];
            var highColor = [255, 0, 0];


            if (_t < 0.5) {
                return this.rgb2hex(this.interpolateArray(_t*2, lowColor, midColor));
            } else {
                return this.rgb2hex(this.interpolateArray((_t - 0.5)*2, midColor, highColor));
            }
        },
        collectObjectsForHeatmap: function (layout) {
            var self = this;
            return this.collectObjectsStatistics(layout, function (object) {
                object.color = self.pickHeatColor(object.specCount);
            });
        },
        collectObjectsForFailureMap: function (layout) {
            var self = this;
            return this.collectObjectsStatistics(layout, function (object) {
                object.color = '#69ee58';
                if (object.errorCount == 1) {
                    object.color = '#ffa100';
                } else  if (object.errorCount >= 2) {
                    object.color = '#ff3600';
                }
                //object.color = self.pickHeatColor(object.specCount);
            });
        },

        collectObjectsStatistics: function (layout, objectModifierCallback) {
            var objects = this._collectObjectsStatistics(layout);
            var boundaryBox = {
                min: {x: 1000000, y: 1000000},
                max: {x: 0, y: 0}
            };
            var self = this;
            _.forEach(objects, function (object) {
                boundaryBox.min.x = Math.min(boundaryBox.min.x, object.area.x);
                boundaryBox.min.y = Math.min(boundaryBox.min.y, object.area.y);
                boundaryBox.max.x = Math.max(boundaryBox.max.x, object.area.x + object.area.width);
                boundaryBox.max.y = Math.max(boundaryBox.max.y, object.area.y + object.area.height);
                objectModifierCallback(object);
            });

            boundaryBox.width = boundaryBox.max.x - boundaryBox.min.x;
            boundaryBox.height = boundaryBox.max.y - boundaryBox.min.y;
            return {
                boundaryBox,
                objects: this.sortByArea(objects)
            };
        },
        _collectObjectsStatistics: function (layout) {
            var objectsMap = {};
            var collectedObjects = [];
            var self = this;
            this.visitEachSpec(layout.sections, function (spec) {
                for (var i = 0; i < spec.highlight.length; i++) {
                    var name = spec.highlight[i];
                    if (name !== 'screen' && name !== 'self' && name !== 'viewport' && name !== 'parent') {
                        if (!objectsMap.hasOwnProperty(name)) {
                            var area = layout.objects[name].area;
                            objectsMap[name] = {
                                name: name,
                                area: {
                                    x: area[0],
                                    y: area[1],
                                    width: area[2],
                                    height: area[3]
                                },
                                color: '#ffffff',
                                drawBorder: false,
                                fill: true,
                                fillBackground: true,
                                specCount: 0,
                                errorCount: 0
                            };
                        }
                        objectsMap[name].specCount += 1;
                        if (spec.errors && spec.errors.length > 0) {
                            objectsMap[name].errorCount += 1;
                        }
                    }
                    if (spec.hasOwnProperty('subLayout')) {
                        var collectedFromSubLayout = self._collectObjectsStatistics(spec.subLayout);
                        for (var k = 0; k < collectedFromSubLayout.length; k++) {
                            collectedObjects.push(collectedFromSubLayout[k]);
                        }
                    }
                }
            });
            for (var objectName in objectsMap) {
                if (objectsMap.hasOwnProperty(objectName)) {
                    collectedObjects.push(objectsMap[objectName])
                }
            }
            return collectedObjects;
        }
    },
    filters: {
        convertToPlusMinus: convertToPlusMinus,
        formatTime: formatTime
    }
});

Vue.component('report-node', {
    props: ['node'],
    template: '#tpl-report-node',
    data: function () {
        return {
            extrasPopup: {
                show: false,
                extras: []
            }
        };
    },
    methods: {
        toggleReportNode: toggleReportNode,
        showNodeExtrasPopup: function(extras) {
            this.extrasPopup.extras = [];
            for (var key in extras) {
                if (extras.hasOwnProperty(key)) {
                    this.extrasPopup.extras.push({
                        name: key,
                        value: extras[key]
                    });
                }
            }
            this.extrasPopup.extras.sort(function (a, b) {
                return a.name > b.name;
            });
            this.extrasPopup.show = true;
        }
    },
    filters: {
        convertToPlusMinus: convertToPlusMinus,
        formatTime: formatTime
    }
});



function enrichObjectAndReturnHasFailure(object) {
    object.expanded = false;
    object.hasFailure = false;

    var enrichSpec = function(parent) {
        return function (spec) {
            if (spec.errors && spec.errors.length > 0) {
                parent.hasFailure = true;
            }
            if (spec.subLayout && spec.subLayout.sections) {
                _.forEach(spec.subLayout.sections, function (subSection) {
                    if (enrichSectionAndReturnHasFailure(subSection)) {
                        parent.hasFailure = true;
                    }
                });
            }
        };
    };

    _.forEach(object.specs, enrichSpec(object));
    if (object.specGroups) {
        _.forEach(object.specGroups, function (specGroup) {
            specGroup.expanded = false;
            _.forEach(specGroup.specs, enrichSpec(specGroup));
            if (specGroup.hasFailure) {
                object.hasFailure = true;
            }
        });
    }
    return object.hasFailure;
}

function enrichSectionAndReturnHasFailure(section) {
    section.expanded = false;
    section.hasFailure = false;
    var thatSection = section;
    _.forEach(section.sections, function (section) {
        if (enrichSectionAndReturnHasFailure(section)) {
            thatSection.hasFailure = true;
        }
    });
    _.forEach(section.objects, function (object) {
        if (enrichObjectAndReturnHasFailure(object)) {
            thatSection.hasFailure = true;
        }
    });
    return section.hasFailure;
}

function enrichReportNodeAndReturnHasFailure(node) {
    node.expanded = false;
    node.hasFailure = node.status === 'error';
    node.hasChildren = false;
    if ((node.nodes && node.nodes.length > 0) || (node.sections && node.sections.length > 0)) {
        node.hasChildren = true;
    }
    if (node.type === 'node') {
        _.forEach(node.nodes, function (subNode) {
            if (enrichReportNodeAndReturnHasFailure(subNode)) {
                node.hasFailure = true;
            }
        });
    } else if (node.type === 'layout') {
        _.forEach(node.sections, function (section) {
            if (enrichSectionAndReturnHasFailure(section)) {
                node.hasFailure = true;
            }
        });
    } else if (node.type === 'mutation') {
        if (node.status === 'error') {
            node.hasFailure = true;
        }
    }

    return node.hasFailure;
}

function enrichReportData (reportData) {
    _.forEach(reportData.report.nodes, enrichReportNodeAndReturnHasFailure);
    return reportData;
}

function expandOnlyErrorsInSection (section) {
    section.expanded = section.hasFailure;
    _.forEach(section.sections, expandOnlyErrorsInSection);
    _.forEach(section.objects, function (object) {
        object.expanded = object.hasFailure;
        if (object.specs) {
            _.forEach(object.specs, function (spec) {
                if (spec.subLayout && spec.subLayout.sections) {
                    _.forEach(spec.subLayout.sections, expandOnlyErrorsInSection);
                }
            });
        }
        _.forEach(object.specGroups, function (specGroup) {
            specGroup.expanded = specGroup.hasFailure;
            _.forEach(specGroup.specs, function (spec) {
                if (spec.subLayout && spec.subLayout.sections) {
                    _.forEach(spec.subLayout.sections, expandOnlyErrorsInSection);
                }
            });
        });
    });
}

function expandOnlyErrorsInNode (node) {
    node.expanded = node.hasFailure;

    if (node.type === 'layout') {
        _.forEach(node.sections, function (section) {
            expandOnlyErrorsInSection(section);
        });
    } else {
        _.forEach(node.nodes, function(childNode) {
            expandOnlyErrorsInNode(childNode);
        });
    }

}

function visitEachSection(section, callback) {
    callback(section, 'section');
    _.forEach(section.sections, function (subSection) {
        visitEachSection(subSection, callback);
    });
    _.forEach(section.objects, function (object) {
        callback(object, 'object');

        _.forEach(object.specs, function (spec) {
            if (spec.subLayout) {
                _.forEach(spec.subLayout.sections, function (subSection) {
                    visitEachSection(subSection, callback);
                });
            }
        });
        _.forEach(object.specGroups, function (specGroup) {
            callback(specGroup);
        });
    });
}

function visitEachNode(node, callback) {
    callback(node, 'node');
    _.forEach(node.nodes, function(childNode) {
        if (childNode.type === 'layout') {
            callback(childNode);
            _.forEach(childNode.sections, function (section) {
                visitEachSection(section, callback);
            });
        } else {
            visitEachNode(childNode, callback);
        }
    });
}

function renderTestReport(reportData) {
    var app = new Vue({
        el: '#app',
        mounted: function () {
            this.expandOnlyErrors();
        },
        data: {
            reportData: enrichReportData(reportData)
        },
        methods: {
            expandOnlyErrors: function() {
                _.forEach(this.reportData.report.nodes, expandOnlyErrorsInNode);
            },
            expandAll: function () {
                visitEachNode(this.reportData.report, function (node) {
                    node.expanded = true;
                });
            },
            collapseAll: function () {
                visitEachNode(this.reportData.report, function (node) {
                    node.expanded = false;
                });
            }
        }
    });
}

function collectTestGroups(tests) {
    var groups = {};
    _.forEach(tests, function (test) {
        _.forEach(test.groups, function (group) {
            if (!groups.hasOwnProperty(group)) {
                groups[group] = '';
            }
        });
    });

    var result = [];
    for (var group in groups) {
        if (groups.hasOwnProperty(group)) {
            result.push(group);
        }
    }
    result.sort();
    return result;
}

function renderTestOverviewReport(reportData) {
    var app = new Vue({
        el: '#app',
        data: {
            tableColumns: [{
                name: 'Test', field: 'name',
            },{
                name: 'Passed', field: 'passed'
            },{
                name: 'Failed', field: 'failed'
            },{
                name: 'Warning', field: 'warning'
            },{
                name: 'Total', field: 'total'
            },{
                name: 'Started', field: 'started'
            },{
                name: 'Duration', field: 'duration'
            }],
            sorting: {
                columnField: '',
                order: 1
            },
            selectedGroup: null,
            groups: _.map(collectTestGroups(reportData.tests), function (group) {
                return {name: group, selected: false};
            }),
            tests: _.map(reportData.tests, function (test) {
                return {
                    testId: test.testId,
                    show: true,
                    groups: test.groups,
                    name: {value: test.name, index: test.name},
                    passed: {value: test.statistic.passed, index: test.statistic.passed},
                    failed: {value: test.statistic.errors, index: test.statistic.errors},
                    warning: {value: test.statistic.warnings, index: test.statistic.warnings},
                    total: {value: test.statistic.total, index: test.statistic.total},
                    started: {value: formatTime(test.startedAt), index: test.startedAt},
                    duration: {value: Math.round((test.endedAt - test.startedAt)/100)/10 + "s", index: test.endedAt - test.startedAt},
                    progress: {
                        passed: test.statistic.passed * 100 / Math.max(1, test.statistic.total),
                        failed: test.statistic.errors * 100 / Math.max(1, test.statistic.total),
                        warning: test.statistic.warnings * 100 / Math.max(1, test.statistic.total)
                    }
                };
            })
        },
        methods: {
            sortTable: function (column) {
                var self = this;
                if (this.sorting.columnField === column.field) {
                    this.sorting.order = -this.sorting.order;
                } else {
                    this.sorting.columnField = column.field;
                    this.sorting.order = 1;
                }
                this.tests.sort(function (a, b) {
                    var valueA = a[column.field].index;
                    var valueB = b[column.field].index;
                    var diff = valueA > valueB ? 1: -1;
                    return diff * self.sorting.order;
                });
            },
            toggleAllGroups: function () {
                this.selectedGroup = null;
                _.forEach(this.tests, function (test) {
                    test.show = true;
                });
            },
            toggleGroup: function (group) {
                this.selectedGroup = group;
                _.forEach(this.tests, function (test) {
                    test.show = _.contains(test.groups, group);
                });
            }
        }
    });
}

function renderPageDump(pageData) {
    var objects = [];
    var objectNames = [];
    var maxX2 = 0, maxY2 = 0;
    var index = 0;
    var objectsMap = {};
    _.forEachInObject(pageData.items, function (object, objectName) {
        index += 1;
        objectNames.push(objectName);
        var obj = {
            name: objectName,
            caption: false,
            selected: false,
            hasImage: object.hasImage,
            area: {
                x: object.area[0],
                y: object.area[1],
                x2: object.area[0] + object.area[2],
                y2: object.area[1] + object.area[3],
                width: object.area[2],
                height: object.area[3]
            },
            color: '#ff9100'
        };
        objects.push(obj);
        objectsMap[objectName] = obj;

        maxX2 = Math.max(maxX2, obj.area.x2);
        maxY2 = Math.max(maxY2, obj.area.y2);
    });

    objects.sort(function (a, b) {
        return b.area.width * b.area.height - a.area.width * a.area.height;
    });

    objectNames.sort();

    var app = new Vue({
        el: '#app',
        data: {
            title: pageData.title,
            pageName: pageData.pageName,
            objectNames: objectNames,
            objects: objects,
            objectsMap: objectsMap,
            offsetLeft: 60,
            offsetTop: 60,
            canvasWidth: maxX2 + 120,
            canvasHeight: maxY2 + 120,
            selection: [],
            searchFilter: '',
            guides: []
        },
        methods: {
            onObjectClick: function (object) {
                if (this.guides.length > 0) {
                    this.guides = [];
                }

                if (object.selected) {
                    if (this.selection[0].name === object.name) {
                        this.selection.splice(0, 1);
                    } else {
                        this.selection.splice(1, 1);
                    }

                    object.selected = false;
                } else {
                    object.selected = true;
                    if (this.selection.length === 0) {
                        this.selection.push(object);
                    } else if (this.selection.length === 1) {
                        this.selection.push(object);
                        this.guides = this.generateGuides(this.selection[0], this.selection[1]);
                    } else {
                        this.selection[0].selected = false;
                        this.selection[1].selected = false;
                        this.selection = [object];
                    }
                }
            },
            onObjectMouseOver: function (object) {
                object.caption = true;
            },
            onObjectMouseOut: function (object) {
                object.caption = false;
            },
            isInside: function (parentObject, childObject) {
                return childObject.area.x >= parentObject.area.x
                    && childObject.area.x2 <= parentObject.area.x2
                    && childObject.area.y >= parentObject.area.y
                    && childObject.area.y2 <= parentObject.area.y2
            },
            isFromLeft: function (object1, object2) {
                return object2.area.x >= object1.area.x2;
            },
            isFromAbove: function (object1, object2) {
                return object2.area.y >= object1.area.y2;
            },
            generateGuides: function (object1, object2) {
                if (this.isInside(object1, object2)) {
                    return this.generateInsideGuides(object1, object2);
                } else if (this.isInside(object2, object1)) {
                    return this.generateInsideGuides(object2, object1);
                } else if (this.isFromLeft(object1, object2)) {
                    return [
                        MetaUtils.createVerticalDistanceGuides(this.rightEdge(object1), this.leftEdge(object2), '', object2.area.x - object1.area.x2 + 'px')
                    ]
                } else if (this.isFromLeft(object2, object1)) {
                    return [
                        MetaUtils.createVerticalDistanceGuides(this.rightEdge(object2), this.leftEdge(object1), '', object1.area.x - object2.area.x2 + 'px')
                    ]
                } else if (this.isFromAbove(object1, object2)) {
                    return [
                        MetaUtils.createHorizontalDistanceGuides(this.bottomEdge(object1), this.topEdge(object2), '', object2.area.y - object1.area.y2 + 'px')
                    ]
                } else if (this.isFromAbove(object2, object1)) {
                    return [
                        MetaUtils.createHorizontalDistanceGuides(this.bottomEdge(object2), this.topEdge(object1), '', object1.area.y - object2.area.y2 + 'px')
                    ]
                } else {
                    return [];
                }
            },
            generateInsideGuides: function (parentObject, childObject) {
                return [
                    MetaUtils.createVerticalDistanceGuides(this.leftEdge(parentObject), this.leftEdge(childObject), '', childObject.area.x - parentObject.area.x + 'px'),
                    MetaUtils.createVerticalDistanceGuides(this.rightEdge(childObject), this.rightEdge(parentObject), '', parentObject.area.x2 - childObject.area.x2 + 'px'),
                    MetaUtils.createHorizontalDistanceGuides(this.topEdge(parentObject), this.topEdge(childObject), '', childObject.area.y - parentObject.area.y + 'px'),
                    MetaUtils.createHorizontalDistanceGuides(this.bottomEdge(childObject), this.bottomEdge(parentObject), '', parentObject.area.y2 - childObject.area.y2 + 'px')
                ]
            },
            leftEdge: function (object) {
                return {x1: object.area.x, y1: object.area.y, x2: object.area.x, y2: object.area.y2};
            },
            rightEdge: function (object) {
                return {x1: object.area.x2, y1: object.area.y, x2: object.area.x2, y2: object.area.y2};
            },
            topEdge: function (object) {
                return {x1: object.area.x, y1: object.area.y, x2: object.area.x2, y2: object.area.y};
            },
            bottomEdge: function (object) {
                return {x1: object.area.x, y1: object.area.y2, x2: object.area.x2, y2: object.area.y2};
            },
            canvasMouseOver: function (event) {
                var svg = document.getElementById('canvas');
            }
        }
    });
}

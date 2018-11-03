# Change log
All changes to Galen Framework project will be documented in this file

## [2.4.0][2018-11-03]
### Added
- 'edges' filter in image spec
- Specs meta in json and html reports
- Failure map popup in html report
- Validations highlight in HTML report
- Spec generator
- 'mutate' action for galen specs mutation testing
- Embedded galen-extras lib
- Section filtering
- Google Vision integration for test recognition

### Changed
- Reimplemented all html reports





## [2.3.0][2016-07-13]
### Added
- 'replace-colors' filter in 'image' spec
- Feature to ignore regions in 'image' spec
- Wildcard character for matching multiple files in image spec
- Index mapping in 'forEach' loop
- 'clickAt' function in GalenPages for PageElement object
- 'dragByOffset' function in GalenPages for PageElement object
- '@die' operator for failure support in Galen Specs language
- '>=' and '<=' operators in pixel validations
- Support for custom name in TestNG and JUnit tests
- Gradient color support in 'color-scheme' spec
- Added support for object groups in spec 'contains'
- 'singleline' transformation in 'text' spec


### Changed
- Exceptions in JavaScript expressions in Galen Specs will crash parser


### Fixed
- Fixed test report duration reporting for TestNG and JUnit tests
- Added file and line number to all syntax exceptions
- Fixed locator parser in GalenPages for advanced CSS expressions
- Fixed warnings rendering in Html report for 'component' specs



## [2.2.5][2015-04-14]
- Fixed #318 Path was incorrectly resolved when loading js scripts

## [2.2.4][2015-03-31]
- Updated selenium version to 2.53.0

## [2.2.2][2015-02-28]

### Fixed
- Updated selenium version to 2.52.0
- Fixed rules overriding and added more informative error message in case there was error inside rule
- Installation script for Mac. Now it creates a symlink in /usr/local/bin/galen
- Fixed default functions replacement when using $page in GalenPages
- Added 'singleline' text operation for text validation
- Added 2 pixels error rate in spec 'inside' when checking that the element is not completely inside another element


## [2.2.1][2015-12-08]

### Fixed
- Spec parser now gives error when there is incorrect text in the end of spec
- Added missing file js api: createFile, writeFile, appendFile, deleteFile
- Fixed report css for attachments and extras link


## [2.2.0][2015-12-05]

### Added
- Object groups in page spec
- Arguments for component spec
- Code blocks processing for custom rules
- Alpha channel for area avoidance in image comparison
- "mask" filter in image spec
- An error handling when specs are indented below each other
- "viewport" and "screen" objects in page spec JS API
- Support for JUnit report
- New function in JS API
    - parsePageSpec
    - checkPageSpecLayout
    - makeDirectory
    - listDirectory
    - fileExists
    - isDirectory
- "screen" and "viewport" objects in page dump

### Changed
- Using galen.config file by default for configuration
- All object statements in spec files are now strict
- Improved denoise filter in image spec
- Removed objects text properties from page dump
- Removed visiblity caching for web elements when checking layout
- Added sorting objects by size in heatmap and screenshot popup in html report

### Fixed
- Second level components had broken scope
- Tabs indentation in test suites
- dumpPage js function could not work with undefined excludedObjects
- Fixed error of 1 extra pixel in image spec, which was caused due to pixel rounding 


## [2.1.3][2015-11-11]
### Fixed
- Viewport calculation. Now it ignores scrollbars
- console.log is able to print Java objects

## [2.1.2][2015-09-29]
### Fixed
- LayoutReport.errors() was not taking into account errors within object-based custom rules
- Exception within tests are now printed to console

## [2.1.1][2015-09-29]
### Fixed
- Blur for edges of page element image
- Removed default ./ prefix to all spec paths
- Screen identification on some websites

### Changed 
- Exiting with error code by default in case there were failed tests


## [2.1.0][2015-09-21]
### Changed
- Java API:
    - Changed signature of Galen.checkLayout method
    - Removed Galen.dumpPage method.
    - Introduced GalenPageDump class for making page dumps

### Added
- insideFrame function to PageElement prototype in GalenPages
- Heatmap for layout reports in html report
- "self" special object in specs parser
- Custom information table in html report
- Option for object to be omitted in page dump
- "load" function accept an array of strings
- Support for float percentages in color-scheme validation
- "count" spec for validating the amount of specific objects on page
- Providing object locators to spec parser from Java and JavaScript
- Switch for page elements area finding method. This makes it possible to tests on iOS devices 7.0 and greater

### Fixed
- String concatenation in Galen Specs parser
- RasterFormatException in page dump in case the screenshot is smaller than the element area 
- Improved layout for image comparison popup in HTML report




## [2.0.10][2015-09-08]
## Fixed
- Multi objects matching inside parent object



## [2.0.9][2015-09-03]
### Fixed
- Count function in page spec reader 

### Added 
- Alphanumeric sorting of page elements



## [2.0.8][2015-08-25]
### Added
- Edge browser support
- Text file attachment support in reports

### Fixed
- insideFrame function



## [2.0.7][2015-08-13]
### Fixed
- Fixed cleaning of empty sections. It was incidentally removing non-empty sections in page spec



## [2.0.5][2015-08-03]
### Fixed
- Fixed cleaning of empty sections. It was incidentally removing non-empty sections in page spec



## [2.0.3][2015-07-27]
### Changed
- Galen Specs Lang v2.

### Added
- Tabs indentation for test suite parser
- WebDriver instance is incorrectly provided to JavaScript executor from GalenPageActionRunJavascript 
- Shorthand color notation in color-scheme spec
- Passing JavaScript variables from test suite into checkLayout 
- GalenConfig should also load config from resources
- Relative width as pecentage instead of px
- URL-Parameter in Report for sorting
- Frame support in GalenPages
- Made reporting optional for dumps with onlyImages flage.
- JUnit/TestNG runners in galen-java-support
- Offset analyzer in image comparison spec
- List elements in GalenPages

### Fixed
- Fails to identify devicePixelRatio and make a screenshot on IE
- Spec 'absent' should pass in case locator is not found for object name


### Remove
- Old Galen Specs Language parser


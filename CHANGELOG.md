# Change log
All changes to Galen Framework project will be documented in this file

## [Unreleased][unreleased]
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




## [2.0.10] - 2015-09-08
## Fixed
- Multi objects matching inside parent object



## [2.0.9] - 2015-09-03
### Fixed
- Count function in page spec reader 

### Added 
- Alphanumeric sorting of page elements



## [2.0.8] - 2015-08-25
### Added
- Edge browser support
- Text file attachment support in reports

### Fixed
- insideFrame function



## [2.0.7] - 2015-08-13
### Fixed
- Fixed cleaning of empty sections. It was incidentally removing non-empty sections in page spec



## [2.0.5] - 2015-08-03
### Fixed
- Fixed cleaning of empty sections. It was incidentally removing non-empty sections in page spec



## [2.0.3] - 2015-07-27
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


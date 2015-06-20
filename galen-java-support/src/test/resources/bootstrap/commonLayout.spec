objects
	navbar				.navbar-header
	navbar-item-*		.navbar-collapse .nav li 
	menubar-left		.sidebar-left
	header				.bs-docs-header
	content				.bs-docs-container
	header-container	.bs-docs-header .container

- Overall layout -
    - @( mobile, tablet) -
		content:
   			visible
		navbar:
   			visible
		content:
   			below navbar 370 to 410 px
   
- navigation shown on desktop -
    - @(mobile) -
		navbar-item-*:
   			"hidden on mobile" absent   
    - @(desktop) -
		navbar-item-*:
   			"shown on desktop" visible   

-Content  size -
    - @( mobile) -
		content:
   			"should fit to screen size" width 100% of screen/width
    - @(desktop) -
		content:
   			"should fit to screen size with margin" width  80 to 90% of screen/width
    
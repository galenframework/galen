
@objects
    main-header     h1

    frame           #some-frame


= Main section =
    @on desktop
        main-header:
            above frame


        frame:
            component frame failed-component.spec

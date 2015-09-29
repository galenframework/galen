
@objects
    save-button     .save-button

@rule squared
    width 100% of ${objectName}/height

@rule %{objectName} should be standard button
    ${objectName}:
        width 140px

= Main =

    | save-button should be standard button

    save-button:
        | squared


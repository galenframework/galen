
@objects
    save-button     css .save-button
    name-textfield  id  name-textfield

= Main section =
    @on *
        save-button :
            near name-textfield 50 px left
            text is "Store"
            aligned horizontally all name-textfield
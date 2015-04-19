Notes = {
    notes: [
        {
            id: 1,
            title: "Simple note",
            description: "asfafasf sf asf asa as"
        },{
            id: 2, 
            title: "Another note",
            description: "asfafasf sf asf asa aasdssad dasdsad sad sad sadas das d sd sasa ds adas asd adss"
        }
    ],
    getAll: function () {
        return this.notes;
    },
    add: function (note) {
        note.id = new Date().getTime();
        this.notes.push(note);
    },
    findById: function (id) {
        for (var i=0; i<this.notes.length; i++) {
            if (this.notes[i].id == id) {
                return this.notes[i];
            }
        }

        return null;
    }
};

App = {
    templates: {
    },
    compileTemplate: function (id) {
        var source = $("#" + id).html();
        return Handlebars.compile(source);
    },
    initTemplates: function (pages) {
        for (name in pages) {
            if (pages.hasOwnProperty(name)) {
                this.templates[name] = this.compileTemplate(pages[name]);
            }
        }
    },
    init: function () {

        Handlebars.registerHelper('shortText', function(text) {
            text = Handlebars.Utils.escapeExpression(text);
            
            if (text.length > 50) {
                text = text.substring(0, 50) + "...";
            }
            return new Handlebars.SafeString(text);
        });


        this.initTemplates({
            welcomePage: "tpl-welcome-page",
            loginPage: "tpl-login-page",
            myNotesPage: "tpl-my-notes-page",
            addNotePage: "tpl-add-note-page"
        });
    },
    showLoginPage: function () {
        this.render(this.templates.loginPage());
    },
    showWelcomePage: function () {
        this.render(this.templates.welcomePage());
    },
    showMyNotesPage: function () {
        this.render(this.templates.myNotesPage({
            notes: Notes.getAll()
        }));
    },
    showAddNotePage: function () {
        this.render(this.templates.addNotePage({
            heading: "Add note",
            title: "",
            description: "",
            operation: "Add Note"
        }));
    },
    showMyNote: function (id) {
        var note = Notes.findById(id);
        if (note != null) {
            this.render(this.templates.addNotePage({
                heading: note.title,
                title: note.title,
                description: note.description,
                operation: "Save"
            }));
        }
    },
    render: function (html) {
        $("#content").html(html);
    },
    login: function () {
        var username = $("input[name='login.username']").val();
        var password = $("input[name='login.password']").val();

        if (username == "testuser@example.com" && password == "test123") {
            this.loggedUser = {
                name: "John",
                email: "testuser@example.com"
            };

            this.showMyNotesPage();
        }
        else {
            $("#login-error-message").html("The username or password are incorrect").show();
        }
    },
    addNote: function () {
        var title = $("input[name='note.title']").val();
        var description = $("textarea[name='note.description']").val();

        if (title != null && title.length > 0 ) {
            Notes.add({
                heading: "Add Note",
                title: title,
                description: description
            });
            App.showMyNotesPage();
        }
        else {
            $("#note-error-message").html("Title should not be empty").show();
        }
    }
};

importClass(com.galenframework.tests.integration.GalenPagesIT);

var CommentComponent = $page("Comment", {
    userLink: "a.user",
    message: ".message"
});

var CommentsPage = $page("Comments page", {
    comments: $list(CommentComponent, ".comments > li")
});


var commentsPage = new  CommentsPage(driver);

var secondUserName = commentsPage.comments.get(1).userLink.getText();
var secondMessage = commentsPage.comments.get(1).message.getText();


GalenPagesIT._callbacks.put("list-test", "Amount of comments is " + commentsPage.comments.size()
    + "\n2nd user name is: " + secondUserName
    + "\n2nd message is: " + secondMessage);

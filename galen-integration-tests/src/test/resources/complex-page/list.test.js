importClass(net.mindengine.galen.tests.integration.GalenPagesIT);

var CommentComponent = $page({
    userLink: "a.user",
    message: ".message"
});

var CommentsPage = $page({
    comments: $list(CommentComponent, ".comments > li")
});


var commentsPage = new  CommentsPage(driver);

var secondUserName = commentsPage.comments.get(1).userLink.getText();
var secondMessage = commentsPage.comments.get(1).message.getText();


GalenPagesIT._callbacks.add("Amount of comments is " + commentsPage.comments.size());
GalenPagesIT._callbacks.add("2nd user name is: " + secondUserName);
GalenPagesIT._callbacks.add("2nd message is: " + secondMessage);

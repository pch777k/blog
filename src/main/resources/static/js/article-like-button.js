$(document).ready(function () {
    $(".likeButton").click(function () {
        let articleId = $(this).data("article-id");
        let likesElement = $('#likesCount-' + articleId);
        $.ajax({
            type: "POST",
            url: "/api/v1/articles/" + articleId + "/like",
            success: function () {
                let currentLikes = parseInt(likesElement.text());
                likesElement.text(currentLikes + 1);
                console.log("Likes increased successfully!");
            },
            error: function (xhr, status, error) {
                console.error("Error while increasing likes:", error);
            }
        });
    });
});


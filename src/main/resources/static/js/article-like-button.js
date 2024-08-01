$(document).on('click', '.likeButton', function () {
    let articleId = $(this).data("article-id");
    let likesElement = $('#likesCount-' + articleId);
    $.ajax({
        type: "POST",
        url: "/api/v1/articles/" + articleId + "/like",
        success: function (response) {
            console.log("Server response:", response);
            likesElement.text(response.likesCount);
            console.log("Likes increased successfully!");
        },
        error: function (xhr, status, error) {
            console.error("Error while increasing likes:", error);
            console.error("Response text:", xhr.responseText);
        }
    });
});


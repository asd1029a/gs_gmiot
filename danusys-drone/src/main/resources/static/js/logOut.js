function logout() {
    const domain = document.domain;
    document.cookie = 'accessToken' + '=; expires=Thu, 01 Jan 1999 00:00:10 GMT;domain=' + domain + ';path=/';
    document.location.href = "/";



}

$(".logout").on("click", function () {

    logout();

});
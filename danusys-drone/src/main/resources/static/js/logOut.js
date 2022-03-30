function logout() {
    document.location.href="/";
    document.cookie = 'accessToken' + '=; expires=Thu, 01 Jan 1999 00:00:10 GMT;domain='+ document.domain+'path=/';



}

$(".logout").on("click", function () {

    logout();

});
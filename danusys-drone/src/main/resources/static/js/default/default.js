
document.addEventListener("DOMContentLoaded",function() {
    document.getElementsByClassName("gnm")[0].addEventListener("click",function() {
        let focusHeader = document.getElementsByClassName("gnm")[0].children[0].className.substr(1);
        document.getElementById(focusHeader).className = "on";
    })
})
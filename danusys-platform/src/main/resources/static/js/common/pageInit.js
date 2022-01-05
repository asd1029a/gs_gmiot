$(function () {
    let path = "[[${#httpServletRequest.getRequestURI()}]]";
    $(".gnm li").map((i, el) => {
        if(path.indexOf(el.id) > -1) $(el).addClass("active");
    });

    /* let path = "[[${#httpServletRequest.getRequestURI()}]]";
        $(".accordion li").map((i, el) => {
            if(path.indexOf(el.id) > -1) $(el).addClass("active");
        })*/
    debugger;
    $(".accordion > li").click(function(e) {
        $(".accordion > li").removeClass('on');
        $(e.currentTarget).addClass('on');
    })
    /*$(".sub-menu dd").click(function(e) {

        //$(".sub-menu dd").removeClass('on');
        $(e.target).addClass('on');
    })*/

    $(".sub-menu > li").map((i, el) => {
        if(path.indexOf(el.id) > -1) $(el).addClass("on");
    })

})
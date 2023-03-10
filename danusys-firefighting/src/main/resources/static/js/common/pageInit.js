/* document 공통 기능 */
$(document).ready(() => {
    comm.initModal();
    $("#searchForm form").on('submit', () => {return false;});
    const path = location.pathname;
    const pathArr = path.split("/");

    /* 탑메뉴 및 lnb 메뉴 관련 */
    $("#"+pathArr[2]).addClass("active");
    $(".gnm_parent").on("mouseover", e => {
        $(e.currentTarget).addClass("hover");
    })

    $(".gnm_parent").on("mouseout", e => {
        $(e.currentTarget).removeClass("hover");
    })

    $("#"+pathArr[3]).parents("li").hasClass("multi")
        ? $("#"+pathArr[3]).parents("li").addClass("on")
        : $("#"+pathArr[3]).addClass("on");

    $(".multi").on("click", (e)=> {
        $(".accordion > li").removeClass("on");
        $(e.currentTarget).addClass("on");
    });

    $('.dashboard_snb dd.dashboardType').on("click", e => {
        const $target = $(e.currentTarget);
        $target.siblings('dd').removeClass('active');
    });

    $(".accordion_sub_menu > li").map((i, el) => {
        if(path.indexOf(el.id) > -1) $(el).addClass("on");
    });


    $(".dashboard_snb dd.dashboardType").map((i, el) => {
        if(path.indexOf(el.dataset.value) > -1) $(el).addClass("active");
    });

    /* 검색조건 초기화 버튼 */
    $("#resetFormBtn").on("click", (e) => {
        $("#searchForm form").initForm();
    });

    /* 로그아웃 */
    $("#logoutBtn").on('click', () => {
        account.user.logout();
    })

    /* 다중 셀렉트 박스 */
    $.each($(".dropdown_checkbox[data-selectbox-delay != 'true']"), (idx, item) => {
        comm.createMultiSelectBox(item);
    });

    /* date picker */
    $("input.input_date").attr("autocomplete", "off");
    $('input[name=startDt]').each((idx, item) => {
        const $startDt = $(item);
        const $endDt = $(item).parents('form').find('input[name=endDt]');
        dateFunc.datePickerSet($startDt, $endDt, true);
        //초기값
        const now = new Date();
        const ago = new Date();
        ago.setDate(now.getDate() - 30);
        $startDt.datepicker().data('datepicker').selectDate(ago);
        $endDt.datepicker().data('datepicker').selectDate(now);
    });

    /* 페이지별 공통 기능 */
    // 조회/관리
    if(pathArr[2] === "inqry") {
    // 환경설정
    } else if(path === "/pages/config/userAccount") {
        account.user.eventHandler();
        account.user.create();
    } else if(path === "/pages/config/userGroup") {
        account.group.eventHandler();
        account.group.create();
    }

    // 관제
    else if(path === "/pages/mntr") {
        svgToImage.init();
        mntr.init();
        mntr.eventHandler();
    }
});
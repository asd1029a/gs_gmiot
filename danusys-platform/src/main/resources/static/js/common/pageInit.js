/* document 공통 기능 */
$(document).ready(() => {
    const path = location.pathname;
    const pathArr = path.split("/");
    
    /* 탑메뉴 및 lnb 메뉴 관련 */
    $("#"+pathArr[2]).addClass("active");

    $("#"+pathArr[3]).parents("li").hasClass("multi")
        ? $("#"+pathArr[3]).parents("li").addClass("on")
        : $("#"+pathArr[3]).addClass("on");

    $(".multi").on("click", (e)=> {
        $(".accordion > li").removeClass("on");
        $(e.currentTarget).addClass("on");
    });

    $(".accordion_sub_menu > li").map((i, el) => {
        if(path.indexOf(el.id) > -1) $(el).addClass("on");
    });

    /* 다중 셀렉트 박스 */
    $.each($(".dropdown_checkbox"), (idx, item) => {
        comm.createMultiSelectBox(item)
    });


    /* 검색조건 초기화 버튼 */
    $("#resetFormBtn").on("click", (e) => {
        $("#searchForm form").initForm();
    });

    /* date picker */
    if($("#startDt").length > 0 && $("#endDt").length > 0) {
        $("#startDt, #endDt").attr("autocomplete", "off");
        dateFunc.datePickerSet($("#startDt"), $("#endDt"), true);
    } else if($(".input_date").length > 0) {
        dateFunc.datePickerSet($(".input_date"));
    }

    /* 페이지별 공통 기능 */
    // 조회/관리
    if(path === "/pages/inqry/event1") {
        event.create($('#cityEventTable'),"city");
    } else if(path === "/pages/inqry/event2") {
        event.create($('#troubleEventTable'),"trouble");
    } else if(path === "/pages/inqry/station") {

    } else if(path === "/pages/inqry/facilities") {

    }
    // 환경설정
    else if(path === "/pages/config/dimmingGroup") {

    } else if(path === "/pages/config/userAccount") {
        account.user.create();
    } else if(path === "/pages/config/userGroup") {

    } else if(path === "/pages/config/commonCode") {
        commonCode.create($('#parentCodeTable'),0);
    } else if(path === "/pages/config/notice") {
        notice.create();
    }
    // 관제
    else if(path === "/pages/mntr") {
        mntr.init();
        mntr.eventHandler();
    }
})
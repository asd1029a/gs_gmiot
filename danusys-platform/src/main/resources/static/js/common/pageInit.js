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

    $(".sub-menu > li").map((i, el) => {
        if(path.indexOf(el.id) > -1) $(el).addClass("on");
    });

    /* 셀렉트 박스 */
    comm.customSelectBox('.dropdownCheckbox');

    /* 행정구역 선택 셀렉트 박스 */
    $(".checkboxTitle_district").click(function(){
        $(".checkboxList_district").toggle();
    });
    $("#districtALL").change(function () {
        $(".checkboxList_district input[type=checkbox]").prop('checked', $(this).prop('checked'));
    });

    /* 페이지별 공통 기능 */
    // 조회/관리
    if(path === "/pages/inqry/event1") {

    } else if(path === "/pages/inqry/event2") {

    } else if(path === "/pages/inqry/station") {

    } else if(path === "/pages/inqry/facilities") {
        $(".checkboxTitle_facilities").click(function(){
            $(".checkboxList_facilities").show();
        });
    }
    // 환경설정
    else if(path === "/pages/config/district") {

    } else if(path === "/pages/config/userAccount") {

    } else if(path === "/pages/config/userGroup") {

    } else if(path === "/pages/config/commonCode") {

    } else if(path === "/pages/config/notice") {

    }
})
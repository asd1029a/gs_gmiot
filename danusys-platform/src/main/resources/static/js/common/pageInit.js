/* document 공통 기능 */
$(document).ready(() => {
    comm.initModal();
    $("#searchForm form").on('submit', () => {return false;});
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
        comm.createMultiSelectBox(item);
    });

    /* 검색조건 초기화 버튼 */
    $("#resetFormBtn").on("click", (e) => {
        $("#searchForm form").initForm();
    });

    /* 로그아웃 */
    $("#logoutBtn").on('click', () => {
        account.user.logout();
    })

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
        // event.eventHandler($('#cityEventTable'),"city");
        // event.create($('#cityEventTable'),"city");
    } else if(path === "/pages/inqry/event2") {
        // event.eventHandler($('#troubleEventTable'),"trouble");
        // event.create($('#troubleEventTable'),"trouble");
    } else if(path === "/pages/inqry/station") {
        // station.create();
    } else if(path === "/pages/inqry/facilities") {
        facility.eventHandler();
        facility.create();
    } else if(path === "/pages/inqry/eventCabinet") {
        event.eventHandler($('#cabinetEventTable'), 'cabinet');
        event.create($('#cabinetEventTable'), 'cabinet');
    } else if(path === "/pages/inqry/eventDron") {
        event.create($('#dronEventTable'), 'dron');
    }
    // 환경설정
    else if(path === "/pages/config/dimmingSet") {
        dimming.eventHandler();
        comm.checkAuthority("/user/check/authority", "config", "rw")
            .then(
                (result) => {
                    dimming.createGroup(result);
                }
            );
        dimming.initMap();
    } else if(path === "/pages/config/userAccount") {
        account.user.eventHandler();
        account.user.create();
    } else if(path === "/pages/config/userGroup") {
        account.group.eventHandler();
        account.group.create();
    } else if(path === "/pages/config/commonCode") {
        commonCode.eventHandler($("[data-table-seq='0']"),0);
        commonCode.create($("[data-table-seq='0']"),0);
    } else if(path === "/pages/config/notice") {
        notice.eventHandler();
        comm.checkAuthority("/user/check/authority", "config", "rw")
            .then(
                (result) => {
                    notice.create(result);
                }
            );
    } else if(path === "/pages/config/signage") {
        signage.eventHandler();
    }
    // 관제
    else if(path === "/pages/mntr") {
        svgToImage.init();
        mntr.init();
        mntr.eventHandler();
        /* date picker */
        $("input.input_date").attr("autocomplete", "off");
        $('input[name=startDt]').each((idx, item) => {
            const $startDt = $(item);
            const $endDt = $(item).parents('form').find('input[name=endDt]');
            dateFunc.datePickerSet($startDt, $endDt, true);
            //초기값
            const now = new Date();
            const ago = new Date();
            ago.setDate(now.getDate() - 2);
            $startDt.datepicker().data('datepicker').selectDate(ago);
            $endDt.datepicker().data('datepicker').selectDate(now);
        });
    }
    // 대시보드 (임시)
    else if(path === "/pages/dashboard") {
        //임시 이벤트 핸들러 처리 -> TODO dashboard.js 생성?
        $('.dashboard_snb dd.dashboardType').on("click", e => {
            const $target = $(e.currentTarget);
            const theme = $target.attr('data-value');
            //선택 css 처리
            $target.siblings('dd').removeClass('active');
            $target.addClass('active');
            //이미지 변경
            $('.empty_area div img').attr('src','/images/sample/dashboard_' + theme + '.png');
        });
    }
})
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
        if(path === "/pages/inqry/station") {
            station.eventHandler();
            station.create();
        } else if(path === "/pages/inqry/facilities") {
            facility.eventHandler();
            facility.create();
        } else if(path === "/pages/inqry/eventFaceDetection") {
            faceDetection.eventHandler();
            faceDetection.create();
        } else if(path === "/pages/inqry/event2") {
            // event.eventHandler($('#troubleEventTable'),"trouble");
            // event.create($('#troubleEventTable'),"trouble");
        } else {
            const targetName = pathArr[3];
            event.init(targetName);
            comm.createMultiSelectBox($(".dropdown_checkbox[data-selectbox-delay = 'true']")[0]);

            const $target = $('#'+targetName+'Table');
            event.eventHandler($target, targetName);
            event.create($target, targetName);
        }
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

        //추후 properties 혹은 디비로 관리?
        const initMenu = {
            '41210':"smartPole", //광명
            '47210':"smartBusStop", //영주
            '45210':"smartPower", //김제
            '26290':"smartPole" //부산남구
        }
        let type = initMenu[window.siGunCode];
        console.log(type);
        $('.mntr_container .lnb ul li[data-value='+type+']').trigger("click");
    }
    // 대시보드 (임시)
    else if(path === "/pages/dashboard/dashboard_facility") {
        dashboard.init();
    }

    //통계
    else if(path === "/pages/stats/statistics"){
        const url = new URL(location.href);
        const type = url.searchParams.get("type");
        stats.getEventKind(type, (data) => {
            $(".event_type").text(data.codeName);
        });

        stats.eventHandler();
        stats.create();
        stats.setChart();
    }
})
//setting data array list
let setting_list = [];
let weekday_list = [];
let weekend_list = [];

let settingOk = false;

const setSetting = {
    eventHandler : () => {

        $(".cycle_day_dt").on("click", function () {
            $(".cycle_day_dt").removeClass("active");
            $(this).addClass("active");
            $(".cycle_day").val($(this).text());
        });
        $(".air_con_fan").off('click').on("click", function () {
            if (settingOk) {
                $(".air_con_fan").removeClass("active");
                $(this).addClass("active");
                $("#fan").val($(this).text());
            } else {
                $("#fan").val("");
                comm.showAlert("설정 변경을 눌러야 가능한 기능입니다.");
                return false;
            }

        });
        $(".air_con_mode").off('click').on("click", function () {
            if (settingOk) {
                $(".air_con_mode").removeClass("active");
                $(this).addClass("active");
                $("#mode").val($(this).text());
            } else {
                $("#mode").val("");
                comm.showAlert("설정 변경을 눌러야 가능한 기능입니다.");
                return false;
            }
        });
        $(".power_list").on("click", function () {
            settingOk = false;
            $(".air_con_mode").removeClass("active");
            $(".air_con_fan").removeClass("active");
            $(".power_list").removeClass("active");
            $(this).addClass("active");
            if (this.classList.contains('setting_controller')) {
                settingOk = true;
            } else {
                $("#fan").val("");
                $("#mode").val("");
            };
            $(".power").val($(this).text());
        });

        $(".setting_save").on("click", () => {
            if (setting_list.length === 0) {
                deleteSetting("/facilitySetting/",$(".facility_seq").val());
            } else {
                setting_list.filter(
                    (element) => element !== []
                );
                addSetting("/facilitySetting",setting_list);
            }
            cancelChecks();
            $("#popup_controls").hide();
        })


        $(".setting_close").off("click").on("click", () => {
            comm.confirm("취소하시겠습니까?"
                , {}
                , () => {
                    $(".popup_controls").hide();
                    $(".insert_data").remove();
                    setting_list = [];
                    weekday_list = [];
                    weekend_list = [];
                }
                , () => {
                    return false;
                });
        });

        $('.start_time').timepicker({
            timeFormat : 'H:i',
            defaultTime : '00:00',
            step : 30,
            dynamic : false,
            dropdown : true,
            scrollbar : true
        });

        $(".popup_close").on("click", function () {
            $(".popup_controls").hide();
            $(".insert_data").remove();
            cancelChecks();
            setting_list = [];
            weekday_list = [];
            weekend_list = [];
        });

    }
}
function gmSeqCheck(seq) {
    if (seq == '6366' || seq == '6379' || seq == '6353' || seq == '6418' || seq == '6340' || seq == '6405' || seq == '6392') {
        return true;
    }
    return false;
}

function cancelChecks() {
    $(".cycle_day").val("");
    $("#fan").val("");
    $("#mode").val("");
    $(".power").val("");
    $(".cycle_day_dt").removeClass("active");
    $(".air_con_fan").removeClass("active");
    $(".air_con_mode").removeClass("active");
    $(".power_list").removeClass("active");
}

function deleteControl(obj, idx, airChk, seq) {
    let chk = obj.className;
    comm.confirm("삭제하시겠습니까?", {}
        , () => {
            if (chk.includes("weekday")) {
                let set_filter = setting_list.filter(f => f.facilitySettingTime !== weekday_list[idx].start_time && f.facilitySettingDay !== weekday_list[idx].cycle_day);
                let day_filter = weekday_list.filter(f => f !== weekday_list[idx]);
                let day_tr = $(obj).parent().parent();

                setting_list = set_filter;
                weekday_list = day_filter;
                weekday_list = startTimeSort(weekday_list);

                day_tr.remove();
            } else if (chk.includes("weekend")) {
                let set_filter = setting_list.filter(f => f.facilitySettingTime !== weekend_list[idx].start_time && f.facilitySettingDay !== weekend_list[idx].cycle_day);
                let end_filter = weekend_list.filter(f => f !== weekend_list[idx]);
                let end_tr = $(obj).parent().parent();

                setting_list = set_filter;
                weekend_list = end_filter;
                weekend_list = startTimeSort(weekend_list);

                end_tr.remove();
            }
            $(".insert_data").remove();
            createControlAllV2(airChk, seq);
        }
        , () => {
            return false;
    });
}

function startTimeSort(ary) {
    if (ary != null) {
        ary.sort((a, b) => {
            let as = a.start_time;
            let bs = b.start_time;
            if(as < bs){
                return -1;
            }
            if(as > bs){
                return 1;
            }
            return 0;
        })
        return ary;
    }
}

// function startTimeSortV2(ary){
//     if(ary != null) {
//         ary.sort((a,b) =>{
//             let as = a.facilitySettingTime;
//             let bs = b.facilitySettingTime;
//             if(as < bs){
//                 return -1;
//             }
//             if(as > bs){
//                 return 1;
//             }
//             return 0;
//         })
//         return ary;
//         console.log(ary);
//     }
// }

function arrayDuplicateCheck(type,seq,ary,data,administZone){
    let chk = true;
    for(let i=0; i<ary.length; i++){
        if(ary[i].facilitySettingTime === data.start_time && changeToFormStyle("cycle_day", ary[i].facilitySettingDay) === data.cycle_day){
            comm.showAlert("중복된 시간은 입력할 수 없습니다.");
            chk = false;
            break;
        }
    }

    if(chk){
        changeToDBAry(seq, data, type, administZone);
        if(data.cycle_day == "평일") {
            weekday_list.push(data);
        }else{
            weekend_list.push(data);
        }
    }
}

async function settingAdd(type,seq, administZone){
    if(setting_list == null){
        setting_list = [];
        weekday_list = [];
        weekend_list = [];
    }

    if($(".power").val() === "change"){
        if ($('.cycle_day').val() == "") {
            comm.showAlert("주기를 선택하세요");
            return false;
        } else if ($('.power').val() == "") {
            comm.showAlert("ON/OFF를 선택하세요");
            return false;
        } else if ($('.start_time').val() == "") {
            comm.showAlert("작동 시간을 선택하세요");
            return false;
        } else if ($('#mode').val() == "") {
            comm.showAlert("운전모드를 선택하세요");
            return false;
        } else if ($('#temp').val() == "") {
            comm.showAlert("온도를 선택하세요");
            return false;
        } else if ($('#fan').val() == "") {
            comm.showAlert("바람세기를 선택하세요");
            return false;
        }
    }else{
        if ($('.cycle_day').val() == "") {
            comm.showAlert("주기를 선택하세요");
            return false;
        } else if ($('.power').val() == "") {
            comm.showAlert("켜짐/꺼짐을 선택하세요");
            return false;
        } else if ($('.start_time').val() == "") {
            comm.showAlert("작동 시간을 선택하세요");
            return false;
        }
    }

    $(".insert_data").remove();

    let air_form = $(".air_con_form").serializeJSON();

    arrayDuplicateCheck(type,seq,setting_list,air_form,administZone);

    weekday_list = startTimeSort(weekday_list);
    weekend_list = startTimeSort(weekend_list);
    createControlAllV2(type,seq);
}


async function setFacilityAppoint(index,id,seq,name,administZone){
    $(".insert_data").remove();
    setting_list = [];
    weekday_list = [];
    weekend_list = [];

    setting_list.push(await getSettingList("/facilitySetting/",seq));
    weekday_list.push(await getSettingList("/facilitySetting/weekday/",seq));
    weekend_list.push(await getSettingList("/facilitySetting/weekend/",seq));

    let idCheck = id.split("/");
    let setTag = '<li class="add" id="setting_add" onclick="settingAdd(\''+idCheck[1]+'\','+seq+','+administZone+')">추가</li>';
    $(".ano_add").html("");
    $(".air_add").html("");
    $(".facility_seq").val(seq);

    setAppointList(idCheck[1],setTag,seq,administZone);
}
// function setTwoAryOneAry(ary) {
//     if(ary != null) {
//         let chg_list = [];
//         for (let i = 0; i < ary.length; i++) {
//             chg_list.push(ary[i]);
//         }
//         return startTimeSortV2(chg_list);
//     }
// }
function setAppointList(idCheck,setTag,seq,administZone) {

    setting_list = setting_list[0];
    weekday_list = startTimeSort(changeToSameAry(weekday_list[0],idCheck,seq,administZone));
    weekend_list = startTimeSort(changeToSameAry(weekend_list[0],idCheck,seq,administZone));

    if(setting_list.length === 0 || setting_list === undefined) {
        setting_list = [];
    }
    if(weekday_list === undefined) {
        weekday_list = [];
    }
    if(weekend_list === undefined) {
        weekend_list = [];
    }

    if(idCheck == "air_con" || gmSeqCheck(seq)) {
        $(".air_add").append(setTag)
        if(weekend_list != null || weekday_list != null ) {
            createControlAllV2(idCheck, seq);
        }


        $(".setting_area").show();
        $(".setting_controller").show();

    } else {
        $(".ano_add").append(setTag)
        if(weekend_list != null  || weekday_list != null  ) {
            createControlAllV2("another",seq);
        }


        $(".setting_area").hide();
        $(".setting_controller").hide();

    }
    if($("#popup_controls").css("display") == "none") {
        $("#popup_controls").show();
    } else {
        // $("form").each(function (){
        //     this.reset();
        // })
        cancelChecks();
        $("#popup_controls").hide();
    }
}

function createControlAllV2(type, seq) {
    let tag = "";
    for (let i = 0; i < weekday_list.length; i++) {
        if(weekday_list[i] === weekday_list[weekday_list.length-1]) {
            if(weekday_list[i].power !== "OFF" && weekday_list[i].mode != null && weekday_list[i].mode != undefined && (type === "air_con" || gmSeqCheck(seq))) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekday_list[i].cycle_day+"</td>"+
                    "<td>"+weekday_list[i].power+"</td>"+
                    "<td>"+weekday_list[i].start_time+" ~ 설정 전까지</td>" +
                    "<td>"+weekday_list[i].mode+"</td>" +
                    "<td>"+weekday_list[i].temp+"°C</td>" +
                    "<td>"+weekday_list[i].fan+"</td>" +
                    "<td><span class=\"button setting_delete weekday\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            } else if (weekday_list[i].power !== "OFF" && (type === "air_con" || gmSeqCheck(seq))) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekday_list[i].cycle_day+"</td>"+
                    "<td>"+weekday_list[i].power+"</td>"+
                    "<td>"+weekday_list[i].start_time+" ~ 설정 전까지</td>" +
                    "<td></td>" +
                    "<td>"+weekday_list[i].temp+"°C</td>" +
                    "<td></td>" +
                    "<td><span class=\"button setting_delete weekday\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            } else if (type === "air_con" || gmSeqCheck(seq)) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekday_list[i].cycle_day+"</td>"+
                    "<td>"+weekday_list[i].power+"</td>"+
                    "<td>"+weekday_list[i].start_time+" ~ 설정 전까지</td>" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td><span class=\"button setting_delete weekday\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            } else {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekday_list[i].cycle_day+"</td>"+
                    "<td>"+weekday_list[i].power+"</td>"+
                    "<td>"+weekday_list[i].start_time+" ~ 설정 전까지</td>" +
                    "<td style='display: none'></td>" +
                    "<td style='display: none'></td>" +
                    "<td style='display: none'></td>" +
                    "<td><span class=\"button setting_delete weekday\" onclick='deleteControl(this,"+i+",\"another\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            }

        } else {
            if(weekday_list[i].power !== "OFF" && weekday_list[i].mode != null && weekday_list[i].mode != undefined && (type === "air_con" || gmSeqCheck(seq))) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekday_list[i].cycle_day+"</td>"+
                    "<td>"+weekday_list[i].power+"</td>"+
                    "<td>"+weekday_list[i].start_time+" ~ "+weekday_list[i+1].start_time+"</td>" +
                    "<td>"+weekday_list[i].mode+"</td>" +
                    "<td>"+weekday_list[i].temp+"°C</td>" +
                    "<td>"+weekday_list[i].fan+"</td>" +
                    "<td><span class=\"button setting_delete weekday\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            } else if (weekday_list[i].power !== "OFF"  && (type === "air_con" || gmSeqCheck(seq))) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekday_list[i].cycle_day+"</td>"+
                    "<td>"+weekday_list[i].power+"</td>"+
                    "<td>"+weekday_list[i].start_time+" ~ "+weekday_list[i+1].start_time+"</td>" +
                    "<td></td>" +
                    "<td>"+weekday_list[i].temp+"°C</td>" +
                    "<td></td>" +
                    "<td><span class=\"button setting_delete weekday\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            } else if (type === "air_con" || gmSeqCheck(seq)) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekday_list[i].cycle_day+"</td>"+
                    "<td>"+weekday_list[i].power+"</td>"+
                    "<td>"+weekday_list[i].start_time+" ~ "+weekday_list[i+1].start_time+"</td>" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td><span class=\"button setting_delete weekday\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            }
            else {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekday_list[i].cycle_day+"</td>"+
                    "<td>"+weekday_list[i].power+"</td>"+
                    "<td>"+weekday_list[i].start_time+" ~ "+weekday_list[i+1].start_time+"</td>" +
                    "<td style='display: none'></td>" +
                    "<td style='display: none'></td>" +
                    "<td style='display: none'></td>" +
                    "<td><span class=\"button setting_delete weekday\" onclick='deleteControl(this,"+i+",\"another\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            }
        }
    }

    for (let i = 0; i < weekend_list.length; i++) {
        if(weekend_list[i] === weekend_list[weekend_list.length-1]) {
            if(weekend_list[i].power !== "OFF" && weekend_list[i].mode != null && weekend_list[i].mode != undefined && (type === "air_con" || gmSeqCheck(seq))) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekend_list[i].cycle_day+"</td>"+
                    "<td>"+weekend_list[i].power+"</td>"+
                    "<td>"+weekend_list[i].start_time+" ~ 설정 전까지</td>" +
                    "<td>"+weekend_list[i].mode+"</td>" +
                    "<td>"+weekend_list[i].temp+"°C</td>" +
                    "<td>"+weekend_list[i].fan+"</td>" +
                    "<td><span class=\"button setting_delete weekend\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            } else if (weekend_list[i].power !== "OFF"  && (type === "air_con" || gmSeqCheck(seq))) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekend_list[i].cycle_day+"</td>"+
                    "<td>"+weekend_list[i].power+"</td>"+
                    "<td>"+weekend_list[i].start_time+" ~ 설정 전까지</td>" +
                    "<td></td>" +
                    "<td>"+weekend_list[i].temp+"°C</td>" +
                    "<td></td>" +
                    "<td><span class=\"button setting_delete weekend\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            } else if (type === "air_con" || gmSeqCheck(seq)) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekend_list[i].cycle_day+"</td>"+
                    "<td>"+weekend_list[i].power+"</td>"+
                    "<td>"+weekend_list[i].start_time+" ~ 설정 전까지</td>" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td><span class=\"button setting_delete weekend\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"

            }else{
                tag += "<tr class='insert_data'>"+
                    "<td>"+weekend_list[i].cycle_day+"</td>"+
                    "<td>"+weekend_list[i].power+"</td>"+
                    "<td>"+weekend_list[i].start_time+" ~ 설정 전까지</td>" +
                    "<td style='display: none'></td>" +
                    "<td style='display: none'></td>" +
                    "<td style='display: none'></td>" +
                    "<td><span class=\"button setting_delete weekend\" onclick='deleteControl(this,"+i+",\"another\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            }
        }else {
            if(weekend_list[i].power !== "OFF"  && weekend_list[i].mode != null && weekend_list[i].mode != undefined && (type === "air_con" || gmSeqCheck(seq))){
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekend_list[i].cycle_day+"</td>"+
                    "<td>"+weekend_list[i].power+"</td>"+
                    "<td>"+weekend_list[i].start_time+" ~ "+weekend_list[i+1].start_time+"</td>" +
                    "<td>"+weekend_list[i].mode+"</td>" +
                    "<td>"+weekend_list[i].temp+"°C</td>" +
                    "<td>"+weekend_list[i].fan+"</td>" +
                    "<td><span class=\"button setting_delete weekend\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            }else if(weekend_list[i].power !== "OFF"  && (type === "air_con" || gmSeqCheck(seq))){
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekend_list[i].cycle_day+"</td>"+
                    "<td>"+weekend_list[i].power+"</td>"+
                    "<td>"+weekend_list[i].start_time+" ~ "+weekend_list[i+1].start_time+"</td>" +
                    "<td></td>" +
                    "<td>"+weekend_list[i].temp+"°C</td>" +
                    "<td></td>" +
                    "<td><span class=\"button setting_delete weekend\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            }else if(type === "air_con" || gmSeqCheck(seq)) {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekend_list[i].cycle_day+"</td>"+
                    "<td>"+weekend_list[i].power+"</td>"+
                    "<td>"+weekend_list[i].start_time+" ~ "+weekend_list[i+1].start_time+"</td>" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td><span class=\"button setting_delete weekend\" onclick='deleteControl(this,"+i+",\"air_con\","+seq+")'>삭제</span></td>" +
                    "</tr>"

            }else {
                tag +=  "<tr class='insert_data'>"+
                    "<td>"+weekend_list[i].cycle_day+"</td>"+
                    "<td>"+weekend_list[i].power+"</td>"+
                    "<td>"+weekend_list[i].start_time+" ~ "+weekend_list[i+1].start_time+"</td>" +
                    "<td style='display: none'></td>" +
                    "<td style='display: none'></td>" +
                    "<td style='display: none'></td>" +
                    "<td><span class=\"button setting_delete weekend\" onclick='deleteControl(this,"+i+",\"another\","+seq+")'>삭제</span></td>" +
                    "</tr>"
            }

        }
    }
    $(".control_area").append(tag);
}

function changeToSameAry(chk,type,seq,administZone) {
    let ary = [];
    let resultAry = [];
    if((type == "air_con" || gmSeqCheck(seq)) && chk[0] != null) {
        for (let i = 0; i < chk.length; i++) {
            let name = "";
            let transAry = [];
            transAry.push({
                "cycle_day":changeToFormStyle("cycle_day", chk[i][0].facilitySettingDay, administZone),
                "start_time":chk[i][0].facilitySettingTime
            })
            for (let j = 0; j < chk[i].length; j++) {
                name = chk[i][j].facilitySettingName;
                if(name == "temp") {
                    transAry.push({
                        "temp" : chk[i][j].facilitySettingValue
                    });
                }else if(name == "fan"){
                    transAry.push({
                        "fan" : changeToFormStyle(chk[i][j].facilitySettingName, chk[i][j].facilitySettingValue, administZone)
                    });
                }else if(name == "mode"){
                    transAry.push({
                        "mode" : changeToFormStyle(chk[i][j].facilitySettingName,chk[i][j].facilitySettingValue, administZone)
                    });
                }else if(name == "power"){
                    transAry.push({
                        "power" : changeToFormStyle(chk[i][j].facilitySettingName,chk[i][j].facilitySettingValue, administZone)
                    });
                }
            }
            resultAry.push(transAry);
        }
        for (let i = 0; i < resultAry.length; i++) {
            ary.push(Object.assign(...resultAry[i]));
        }
        return ary;
    } else if(chk[0] != null){
        // chk[0] != undefined
        for (let i = 0; i < chk.length; i++) {
            let name = "";
            let transAry = [];
            transAry.push({
                "cycle_day":changeToFormStyle("cycle_day", chk[i][0].facilitySettingDay, administZone),
                "start_time":chk[i][0].facilitySettingTime
            })
            for (let j = 0; j < chk[i].length; j++) {
                name = chk[i][j].facilitySettingName;
                if(name == "power"){
                    transAry.push({
                        "power" : changeToFormStyle(chk[i][j].facilitySettingName,chk[i][j].facilitySettingValue, administZone)
                    });
                }
            }
            resultAry.push(transAry);
        }
        for (let i = 0; i < resultAry.length; i++) {
            ary.push(Object.assign(...resultAry[i]));
        }
        return ary;
    }
}


function changeToDBAry (seq, data, type, administZone) {
    setting_list.push({
            "facilitySeq" : seq,
            "facilitySettingType" : 189,
            "facilitySettingDay" : changeToDBStyle("cycle_day", data.cycle_day, administZone),
            "facilitySettingTime" : data.start_time,
            "facilitySettingName" : "power",
            "facilitySettingValue" : changeToDBStyle("power", data.power, administZone)
        });
    if (data.mode != "") {
        setting_list.push({
            "facilitySeq" : seq,
            "facilitySettingType" : 189,
            "facilitySettingDay" : changeToDBStyle("cycle_day", data.cycle_day, administZone),
            "facilitySettingTime" : data.start_time,
            "facilitySettingName" : "mode",
            "facilitySettingValue" : changeToDBStyle("mode", data.mode, administZone)
        });
    }
    if (data.fan != "") {
        setting_list.push({
            "facilitySeq" : seq,
            "facilitySettingType" : 189,
            "facilitySettingDay" : changeToDBStyle("cycle_day", data.cycle_day, administZone),
            "facilitySettingTime" : data.start_time,
            "facilitySettingName" : "fan",
            "facilitySettingValue" : changeToDBStyle("fan", data.fan, administZone)
        });
    }
    if (data.temp != "" && data.power !== "OFF" && (type === "air_con" || gmSeqCheck(seq))) {
        setting_list.push({
            "facilitySeq" : seq,
            "facilitySettingType" : 189,
            "facilitySettingDay" : changeToDBStyle("cycle_day", data.cycle_day, administZone),
            "facilitySettingTime" : data.start_time,
            "facilitySettingName" : "temp",
            "facilitySettingValue" : data.temp
        });
    }
}



function changeToFormStyle(chk, val, administZone) {
    let obj = "";
    if(chk === "power") {
        obj = (administZone == "41210") ?
            {
                "On" : {
                    "value" : "ON"
                }
                , "Off" : {
                    "value" : "OFF"
                }
            } :
            {
                "true" : {
                    "value" : "ON"
                }
                , "false" : {
                    "value" : "OFF"
                }
            }
    }

    if(chk === "cycle_day") {
        obj = {
            "weekday" : {
                "value" : "평일"
            }
            , "weekend" : {
                "value" : "주말"
            }
        }
    }

    if(chk === "mode") {
        obj = {
            "1" : {
                "value" : "냉방"
            }
            , "2" : {
                "value" : "송풍"
            }
            , "3" : {
                "value" : "자동"
            }
            , "4" : {
                "value" : "난방"
            }
        }
    }
    if(chk === "fan") {
        obj = {
            "1" : {
                "value" : "약풍"
            }
            , "2" : {
                "value" : "중풍"
            }
            , "3" : {
                "value" : "강풍"
            }
            , "4" : {
                "value" : "자동풍"
            }
        }
    }
    return obj[val].value;
}

function changeToDBStyle(chk,val, administZone) {
    let obj = "";
    if(chk === "power") {
        obj = (administZone == "41210") ?
            {
                "ON" : {
                    "value" : "On"
                }
                , "OFF" : {
                    "value" : "Off"
                }
                , "change" : {
                    "value" : "On"
                }
            } :
            {
                "ON" : {
                    "value" : "true"
                }
                , "OFF" : {
                    "value" : "false"
                }
                , "change" : {
                    "value" : "true"
                }
            }
    }

    if(chk === "cycle_day") {
        obj = {
            "평일" : {
                "value" : "weekday"
            }
            , "주말" : {
                "value" : "weekend"
            }
        }
    }

    if(chk === "mode") {
        obj = {
            "냉방" : {
                "value" : "1"
            }
            , "송풍" : {
                "value" : "2"
            }
            , "자동" : {
                "value" : "3"
            }
            , "난방" : {
                "value" : "4"
            }
        }
    }

    if(chk === "fan") {
        obj = {
            "약풍" : {
                "value" : "1"
            }
            , "중풍" : {
                "value" : "2"
            }
            , "강풍" : {
                "value" : "3"
            }
            , "자동풍" : {
                "value" : "4"
            }
        }
    }
    return obj[val].value;
}


function getSettingList(url, seq) {
    return new Promise(function (resolve, reject){
        $.ajax({
            url : url + seq,
            type : "POST",
            async : true,
            dataType : 'json',
            success: function (response) {
                resolve(response);
            },
            error: function (xhr) {
                console.log(xhr);
                reject(xhr);
            }
        });
    });
};

function addSetting(url, obj) {
    $.ajax({
        url : url,
        type : "POST",
        data : JSON.stringify(obj),
        dataType : 'json',
        contentType : "application/json; UTF-8;",
        // error: function (error) {
        //     console.log("error: " + error);
        // }
    })
}

function deleteSetting(url, seq) {
    $.ajax({
        url : url + seq,
        type : "DELETE",
        // error: function (error) {
        //     console.log("error: " + error);
        // }
    })
}
$(document).ready(function () {
    // console.log(window.location.pathname);
    let param = {"droneDeviceName": ""}
    loadDroneList(param);
    loadAllSocketList();

    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "POST",
        data: JSON.stringify(param),
        async: false,
        success: function (resultData) {

        }
    })
    getListMission();

});

function loadDroneList(param) {
    let drone_total_count = 0;
    let deviceName = $(".add_drone_device_name").val();
    let countMissionList = 0;

    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "POST",
        data: JSON.stringify(param),
        async: false,
        success: function (resultData) {
            //   console.log(resultData);
            let droneUserId = null;
            $(".listScroll ul").html("");
            $.each(resultData, function (i, item) {


                //     console.log("i=", i);
                if (item.userId != null)
                    droneUserId = item.userId;
                else
                    droneUserId = "";

                if (item.id !== 0) {
                    drone_total_count++;
                    $(".listScroll ul").append(`
                        <li class="drone_info" data-drone-id="${item.id}">
                            <dl>
                                <dt><span class="green">${item.status}</span>${item.droneDeviceName}</dt>
                                <dd><i><img src="images/um/listMore.svg"></i></dd>
                            </dl>
                            <p>ID : ${droneUserId}</p>
                            <p>${item.updateDt}</p>
                        </li>
                    `)
                }
            });
            $('.drone_info').on('click', function (e) {
                let id = $(e.currentTarget).data("droneId");
                //     console.log(id);
                $("#file").val("");
                getDroneDetails(id);
            });

            //   console.log(drone_total_count);
            $(".count_mission_list").text(drone_total_count);
        }
    });
}

$(".add_drone").on("click", function () {
    let deviceName = $(".add_drone_device_name").val();
    let param = {"droneDeviceName": deviceName};
    let flag = false;
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "PUT",
        data: JSON.stringify(param),
        async: false,
        success: function (resultData) {
            $(".drone_id").val(resultData.id);
            $(".drone_status").val(resultData.status);
            flag = true;

        }

    });
    let droneUserId = null;
    param = {"droneDeviceName": ""};
    if (flag) {

        loadDroneList(param);
    }
//    console.log(flag);


});

$(".update_drone_detail_button").on("click", function () {
    let isSuccess =0;
    // console.log($(".location_select option:selected").data("id"));
    let param = {
        "droneId": $(".drone_id").val(),
        "droneDetails": {
            "location": $(".drone_location").val(),
            "masterManager": $(".drone_master_manager").val(),
            "subManager": $(".drone_sub_manager").val(),
            "manufacturer": $(".drone_manufacturer").val(),
            "type": $(".drone_type").val(),
            "weight": $(".drone_weight").val(),
            "maximumOperatingDistance": $(".drone_maximum_operating_distance").val(),
            "maximumManagementAltitude": $(".drone_maximum_management_altitude").val(),
            "maximumOperatingSpeed": $(".drone_maximum_operating_speed").val(),
            "simNumber": $(".drone_sim_number").val(),
            "maximumSpeed": $(".drone_maximum_speed").val(),
            "size1": $(".size1").val(),
            "size2": $(".size2").val(),
            "size3": $(".size3").val(),
            "maximumOperatingWeight": $(".maximum_operating_weight").val(),
            "operationTemperatureRangeMin": $(".drone_operating_temperature_min").val(),
            "operationTemperatureRangeMax": $(".drone_operating_temperature_max").val(),


        },
        "droneSocket": $(".socket_list option:selected").data("id"),
        "droneStatus": "대기중",
        "droneBase": $(".location_select option:selected").data("id"),
        "droneMission": $(".mission_list option:selected").data("id")
    }
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/dronedetails",
        type: "PATCH",
        async: false,
        data: JSON.stringify(param),
        success: function (resultData) {
            //    console.log(resultData);
            isSuccess=1;
        }
    });

    let formData = new FormData();
    formData.append('uploadFile', $('#file')[0].files[0]);
    //imgCheck($('#file')[0].files[0]);
    let droneId = 0;
    if ($(".drone_id").val() != "")
        droneId = $(".drone_id").val();
    formData.append("droneId", droneId);
    $.ajax({
        url: "/file/upload/drone",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        success: function (resultData) {
            //console.log("resultData", resultData);
            $(".uploadName").val(resultData);
            if(isSuccess ==1){
                alert("저장되었습니다.");
            }
            else{
                alert("중복된 드론 입니다.다시　드론을　선택해주세요．");
            }


        }
    })


});

function getDroneDetails(id) {
    //loadAllSocketList();
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: `/drone/api/dronebase`,
        type: "POST",
        data: JSON.stringify({}),
        async: false,
        success: function (resultData) {
            // console.log(resultData);
            $(".location_select").html(``);
            $.each(resultData, function (i, item) {
                //   console.log("item", item.baseName);
                $(".location_select").append(`<option data-id="${item.id}">${item.baseName}</option>`);
            })
            $.ajax({
                contentType: "application/json; charset=utf-8",
                url: `/drone/api/drone/${id}`,
                type: "GET",
                async: false,
                success: function (result) {
                    let isIncludeData = false;
                    console.log(result);


                    //   console.log("result",$(`.mission_list option[data-id='${result.mission.id}']`)[0]);
                    //  console.log("resultMissionId", result.droneInmission.mission);
                    if (result.droneInmission.mission != null)
                        document.querySelectorAll(`.mission_list option[data-id='${result.droneInmission.mission.id}']`)[0].selected = true;
                    document.querySelectorAll(`.location_select option[data-id='${result.droneBase.id}']`)[0].selected = true;
                    let querySelectorSocketList = document.querySelectorAll(`.socket_list [data-id]`);

                    querySelectorSocketList.forEach(function (value, key, parent) {
                        let dataId = value.dataset.id;
                        if (dataId == result.droneSocket) {
                            isIncludeData = true;
                        }
                    })
                    if (isIncludeData)
                        document.querySelectorAll(`.socket_list option[data-id='${result.droneSocket}']`)[0].selected = true;
                    let droneDetails = result.droneDetails;
                    $(".drone_status").val(result.status);
                    $(".drone_master_manager").val(droneDetails.masterManager);
                    $(".drone_id").val(result.id);
                    $(".drone_sub_manager").val(droneDetails.subManager);
                    $(".drone_name").val(result.droneDeviceName);
                    $(".drone_manufacturer").val(droneDetails.manufacturer);
                    $(".drone_insert_dt").val(droneDetails.insertDt);
                    $(".drone_type").val(droneDetails.type);
                    $(".drone_weight").val(droneDetails.weight);
                    $(".drone_maximum_operating_distance").val(droneDetails.maximumOperatingDistance);
                    $(".drone_maximum_management_altitude").val(droneDetails.maximumManagementAltitude);
                    //$(".drone_operating_temperature_range").val("");
                    $(".drone_maximum_operating_speed").val(droneDetails.maximumOperatingSpeed);
                    $(".drone_sim_number").val(droneDetails.simNumber);
                    $(".drone_maximum_speed").val(droneDetails.maximumSpeed);
                    $(".uploadName").val(droneDetails.thumbnailImg);
                    $(".picture img").attr("src", "/file/image/" + droneDetails.thumbnailImg);

                    $(".size1").val(droneDetails.size1);
                    $(".size2").val(droneDetails.size2);
                    $(".size3").val(droneDetails.size3);
                    $(".maximum_operating_weight").val(droneDetails.maximumOperatingWeight);
                    $(".drone_operating_temperature_min").val(droneDetails.operationTemperatureRangeMin);
                    $(".drone_operating_temperature_max").val(droneDetails.operationTemperatureRangeMax);


                }
            });

        }

    });


}

function delete_button_event() {
    if (confirm("정말 삭제하시겠습니까??") == true) {    //확인
        //   console.log($(".drone_id").val());
        let param = {"id": $(".drone_id").val()};

        $.ajax({
            contentType: "application/json; charset=utf-8",
            url: '/drone/api/drone/',
            type: "DELETE",
            async: false,
            data: JSON.stringify(param),
            success: function (resultData) {
                //    console.log(resultData);
                if (resultData === "success") {
                    $(".drone_location").val("");
                    $(".drone_status").val("");
                    $(".drone_master_manager").val("");
                    $(".drone_id").val("");
                    $(".drone_sub_manager").val("");
                    $(".drone_name").val("");
                    $(".drone_manufacturer").val("");
                    $(".drone_insert_dt").val("");
                    $(".drone_type").val("");
                    $(".drone_weight").val("");
                    $(".drone_maximum_operating_distance").val("");
                    $(".drone_maximum_management_altitude").val("");
                    //$(".drone_operating_temperature_range").val("");
                    $(".drone_maximum_operating_speed").val("");
                    $(".drone_sim_number").val("");
                    $(".drone_maximum_speed").val("");
                    $(".uploadName").val("");
                    param = {"droneDeviceName": ""};
                    loadDroneList(param);
                }

            }
        });
    } else {   //취소
        return;
    }
}

$(".delete_drone_detail_button").on("click", delete_button_event);

function getListMission() {
    let param = {"name": "", "droneId": ""};
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/mission",
        type: "POST",
        data: JSON.stringify(param),
        success: function (resultData) {
            //  console.log(resultData);
            $(".droneFrameLeft dl dt select").html("");
            $.each(resultData, function (i, item) {
                $(".droneFrameLeft dl dt select").append(`
                <option data-id=${item.id}>${item.name}</option>
                `)
            });

        }
    });
}

//
function readImage(input) {
    // 인풋 태그에 파일이 있는 경우
    if (input.files && input.files[0]) {
        // 이미지 파일인지 검사 (생략)
        // FileReader 인스턴스 생성
        const reader = new FileReader()
        // 이미지가 로드가 된 경우
        reader.onload = e => {
            const previewImage = document.getElementById("preview-image")
            previewImage.src = e.target.result
        }
        // reader가 이미지 읽도록 하기
        reader.readAsDataURL(input.files[0])
    }
}

$("#file").on("change", function (e) {

    readImage(e.target);


});


function imgCheck(img) {
    alert(img.clientWidth);
}

const pageDrone = {
    createMissionPopup: function (data) {
        const popup = common.getQs(".popup");
        popup.style.display = "block";
        if (!data) {
            return false;
        }
        const name = popup.querySelector("#mission-name");
        const id = popup.querySelector("#mission-id");
        name.value = data.name;
        id.value = data.userId;
        popup.dataset.id = data.id;
    },
    closePopup() {
        const deviceName = common.getQs("#device-name");
        //login 로그인 보류
        //const id = common.getQs("#mission-userId");
        const popup = common.getQs(".popup");
        const listMore = common.getQs(".listMore");

        deviceName.value = "";
        popup.style.display = "none";
        // listMore.classList.remove("on");

    },
    saveMission: async function () {
        const userId = common.getQs("#mission-id").value;
        const name = common.getQs("#device-name").value;
        const popup = common.getQs(".popup");
        let id = popup.dataset.id;

        if (id) {
            id = Number.parseInt(popup.dataset.id);
        }

        if (!name) {
            alert("미션 명을 입력하세요.");
            return false;
        }
        ;

//        let deviceName = $(".add_drone_device_name").val();
        let param = {
            "droneDeviceName": name,
            "userId": userId,

        };
        let flag = false;

        $.ajax({
            contentType: "application/json; charset=utf-8",
            url: "/drone/api/drone",
            type: "PUT",
            data: JSON.stringify(param),
            async: false,
            success: function (resultData) {
                $(".drone_id").val(resultData.id);
                $(".drone_status").val(resultData.status);
                flag = true;

            }

        });
        param = {"droneDeviceName": ""};
        if (flag) {

            loadDroneList(param);
        }


        pageDrone.closePopup();


    }
}

$(".add_drone_device_name").on("input", function () {
    // console.log($("input:radio[name=drone]:checked").val());
    let checkedComboBoxName = $(".selectBox .box .list .selected").data("id");

    let checkedRadioName = $("input:radio[name=drone]:checked").val();
    let param = {};
    if (checkedRadioName === "droneName") {
        param = {
            "droneDeviceName": $(".add_drone_device_name").val(),
            "droneStatus": checkedComboBoxName
        }
        loadDroneList(param);
    } else if (checkedRadioName == "id") {
        param = {
            "userId": $(".add_drone_device_name").val(),
            "droneStatus": checkedComboBoxName
        }
        loadDroneList(param);

    }


});


function loadSocketList() {
    let param = {};
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/droneSocket",
        type: "POST",
        data: JSON.stringify(param),
        success: function (resultData) {
            console.log(resultData);
            $(".socket_list").html(``);
            $.each(resultData, function (i, item) {
                //   console.log("item", item.baseName);
                $(".socket_list").append(`<option data-id="${item.index}">address:${item.address},port:${item.port}</option>`);
            })
        }

    });
}

// $(".sync_button").on("click", function () {
//     saveSocketList();
// })

function loadAllSocketList() {
    let param = {};
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/socket",
        type: "POST",
        data: JSON.stringify(param),
        success: function (resultData) {

            console.log(resultData);
            $(".socket_list").html(``);
            $.each(resultData, function (i, item) {
                console.log(resultData);
                //   console.log("item", item.baseName);
                $(".socket_list").append(`<option data-id="${item.systemId}">${item.systemId} : address:${item.ip},port:${item.port}</option>`);
            })
        }
    });

}

function saveSocketList() {
    let param = {};
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/socket",
        type: "PUT",
        data: JSON.stringify(param),
        success: function (resultData) {
            alert("동기화 완료");
            loadAllSocketList();
        }
    });

}
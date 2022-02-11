$(document).ready(function () {

    let param = {"droneDeviceName": ""}
    loadDroneList(param);

});

function loadDroneList(param){
    let deviceName = $(".add_drone_device_name").val();
    let countMissionList = 0;
    let droneUserId=null;
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "POST",
        data: JSON.stringify(param),
        success: function (resultData) {
            console.log(resultData);
            $(".listScroll ul").html("");
            $.each(resultData, function (i, item) {
                countMissionList = i;
                if(item.userId!=null)
                    droneUserId=item.userId;
                else
                    droneUserId="";
                if (item.id !== 0) {
                    $(".listScroll ul").append(`
                        <li>
                            <dl>
                                <dt><span class="green">${item.droneDetails.status}</span>${item.droneDeviceName}</dt>
                                <dd><i><img src="images/um/listMore.svg"></i></dd>
                            </dl>
                            <p>ID : ${droneUserId}</p>
                            <p>${item.updateDt}</p>
                        </li>
            `)
                }

            });
            console.log(countMissionList);
            $(".count_mission_list").text(countMissionList);
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
    let droneUserId=null;
    param = {"droneDeviceName": ""};
    if (flag) {


    }
    console.log(flag);
    loadDroneList(param);

});

$(".update_drone_detail_button").on("click", function () {
    let param = {
        "droneId": $(".drone_id").val(),
        "droneDetails": {
            "location": $(".drone_location").val(),
            "status": $(".drone_status").val(),
            "masterManager": $(".drone_master_manager").val(),
            "subManager": $(".drone_sub_manager").val(),
            "manufacturer": $(".drone_manufacturer").val(),
            "type": $(".drone_type").val(),
            "weight": $(".drone_weight").val(),
            "maximumOperatingDistance": $(".drone_maximum_operating_distance").val(),
            "maximumManagementAltitude": $(".drone_maximum_management_altitude").val(),
            "maximumOperatingSpeed": $(".drone_maximum_operating_speed").val(),
            "simNumber": $(".drone_sim_number").val(),
            "maximumSpeed": $(".drone_maximum_speed").val()


        }
    }
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/dronedetails",
        type: "PUT",
        data: JSON.stringify(param),
        success: function (resultData) {
            console.log(resultData);
        }
    });
});
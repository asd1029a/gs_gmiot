let drone_total_count = 0;

$(document).ready(function () {

    let param = {"droneDeviceName": ""}
    loadDroneList(param);
    getListMission();

});

function loadDroneList(param) {
    let deviceName = $(".add_drone_device_name").val();
    let countMissionList = 0;
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "POST",
        data: JSON.stringify(param),
        async: false,
        success: function (resultData) {
            console.log(resultData);
            let droneUserId = null;
            $(".listScroll ul").html("");
            $.each(resultData, function (i, item) {
                drone_total_count = i;

                if (item.userId != null)
                    droneUserId = item.userId;
                else
                    droneUserId = "";

                if (item.id !== 0) {
                    $(".listScroll ul").append(`
                        <li class="drone_info" data-drone-id="${item.id}">
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
            $('.drone_info').on('click', function (e) {
                let id = $(e.currentTarget).data("droneId");
                console.log(id);
                getDroneDetails(id);
            });

            console.log(drone_total_count);
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
        type: "PATCH",
        data: JSON.stringify(param),
        success: function (resultData) {
            console.log(resultData);
        }
    });


});

function getDroneDetails(id) {
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: `/drone/api/drone/${id}`,
        type: "GET",

        success: function (resultData) {
            console.log(resultData);
            let droneDetails = resultData.droneDetails;
            $(".drone_location").val(droneDetails.location);
            $(".drone_status").val(droneDetails.status);
            $(".drone_master_manager").val(droneDetails.masterManager);
            $(".drone_id").val(resultData.id);
            $(".drone_sub_manager").val(droneDetails.subManager);
            $(".drone_name").val(resultData.droneDeviceName);
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


        }
    });
}

$(".delete_drone_detail_button").on("click", function () {
    console.log($(".drone_id").val());
    let param = {"id": $(".drone_id").val()};

    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: '/drone/api/drone/',
        type: "DELETE",
        async: false,
        data: JSON.stringify(param),
        success: function (resultData) {
            console.log(resultData);
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
                param = {"droneDeviceName": ""};
                loadDroneList(param);
            }

        }
    });


});

function getListMission() {
    let param = {"name": ""};
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/mission",
        type: "POST",
        data: JSON.stringify(param),
        success: function (resultData) {
            console.log(resultData);
            $(".droneFrameLeft dl dt select").html("");
            $.each(resultData, function (i, item) {
                $(".droneFrameLeft dl dt select").append(`
                <option>${item.name}</option>
                `)
            });

        }
     });
}



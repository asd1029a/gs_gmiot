$(document).ready(function () {

    let deviceName = $(".add_drone_device_name").val();
    let param = {"droneDeviceName": ""}
    let countMissionList = 0;
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "POST",
        data: JSON.stringify(param),
        success: function (resultData) {
            console.log(resultData);
            $.each(resultData, function (i, item) {
                countMissionList = i;

                if (item.id !== 0) {
                    $(".listScroll ul").append(`
                        <li>
                            <dl>
                                <dt><span class="green">${item.droneDetails.status}</span>${item.droneDeviceName}</dt>
                                <dd><i><img src="images/um/listMore.svg"></i></dd>
                            </dl>
                            <p>ID : ${item.userId}</p>
                            <p>${item.updateDt}</p>
                        </li>
            `)
                }

            });
            console.log(countMissionList);
            $(".count_mission_list").text(countMissionList);
        }
    });


});


$(".add_drone").on("click", function () {
    let deviceName = $(".add_drone_device_name").val();
    let param = {"droneDeviceName": deviceName};
    let flag = false;
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "PUT",
        data: JSON.stringify(param),
        async : false,
        success: function (resultData) {
            $(".drone_id").val(resultData.id);
            $(".drone_status").val(resultData.status);
            flag = true;

        }

    });

    if (flag) {
        param = {"droneDeviceName": ""};
        $.ajax({
            contentType: "application/json; charset=utf-8",
            url: "/drone/api/drone",
            type: "POST",
            data: JSON.stringify(param),
            success: function (postResultData) {
                console.log(postResultData);
                $(".listScroll ul").html("");
                $.each(postResultData, function (i, item) {
                    countMissionList = i;

                    if (item.id !== 0) {
                        $(".listScroll ul").append(`
                        <li>
                            <dl>
                                <dt><span class="green">${item.droneDetails.status}</span>${item.droneDeviceName}</dt>
                                <dd><i><img src="images/um/listMore.svg"></i></dd>
                            </dl>
                            <p>ID : ${item.userId}</p>
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
    console.log(flag);


});

$(".update_drone_detail_button").on("click",function (){
    let param={
        "droneId":$(".drone_id").val(),
        "droneDetails":{
            "location":$(".drone_location").val(),
            "status":$(".drone_status").val(),
            "masterManager":$(".drone_master_manager").val(),
            "subManager":$(".drone_sub_manager").val(),
            "manufacturer":$(".drone_manufacturer").val(),
            "type":$(".drone_type").val(),
            "weight":$(".drone_weight").val(),
            "maximumOperatingDistance":$(".drone_maximum_operating_distance").val(),
            "maximumManagementAltitude":$(".drone_maximum_management_altitude").val(),
            "maximumOperatingSpeed":$(".drone_maximum_operating_speed").val(),
            "simNumber":$(".drone_sim_number").val(),
            "maximumSpeed":$(".drone_maximum_speed").val()


        }
    }
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/dronedetails",
        type: "PUT",
        data: JSON.stringify(param),
        success: function (resultData){
            console.log(resultData);
        }
    });
});
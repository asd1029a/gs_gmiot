$(document).ready(function () {

    let deviceName = $(".add_drone_device_name").val();
    let param = {"droneDeviceName": ""}
    let countMissionList=0;
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "POST",
        data: JSON.stringify(param),
        success: function (resultData) {
            console.log(resultData);
            $.each(resultData, function (i, item) {
                countMissionList=i;
                if(item.id!==0){
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
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "PUT",
        data: JSON.stringify(param),
        success: function (resultData) {
            $(".drone_id").val(resultData.id);
            $(".drone_status").val(resultData.status);

        }

    });
});
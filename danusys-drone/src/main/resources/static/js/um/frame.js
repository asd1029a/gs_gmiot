$(document).ready(function () {

    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/drone/api/drone",
        type: "PUT",
        data: JSON.stringify(param),
        success: function (resultData) {

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
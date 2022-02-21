$(document).ready(function () {

    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "drone/api/dronelog",
        type: "POST",
        success: function (resultData) {
            console.log(resultData);
            $(".search_body").html("");
            $.each(resultData, function (i, item) {
                $(".search_body").append(`
           <tr>
                <td>${item.id}</td>
                <td>${item.droneDeviceName}</td>
                <td>${item.missionName}</td>
                <td>${item.insertDt}</td>
                <td><span className="button">다운로드</span></td>
            </tr>
         
          `);
            });


        }

    });

})
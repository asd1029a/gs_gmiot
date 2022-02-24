$(document).ready(function () {
    let paramMap = {
        "start": 0,
        "length": 15

    }

    ajaxLog(paramMap);
})

$(".excel_download").on("click", function () {
    let paramMap = {
        "start": 0,
        "length": 1
    }
    let resultData = ajaxLog(paramMap);


    console.log(resultData);
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "/file/excel/download",
        data: JSON.stringify(resultData),
        type: "POST",
        success:function (){
            window.location.href="/file/excel.xlsx";
        }

    })

})


function ajaxLog(paramMap) {
    let pagePerCount = 3;
    let returnValue = null;
    $.ajax({
        contentType: "application/json; charset=utf-8",
        url: "drone/api/dronelog",
        type: "POST",
        async: false,
        data: JSON.stringify(paramMap),
        success: function (resultData) {
            returnValue = resultData.data;
            console.log(resultData);
            $(".search_body").html("");
            $(".pageNav").html("");
            $(".count").html(resultData.count);

            $.each(resultData.data, function (i, item) {
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
            $(".pageNav").append(`<li class="prev" data-id=${resultData.startPage - 1} ><i><img src="images/um/navPrev.svg"></i></li>`);
            for (let i = resultData.startPage; i <= resultData.endPage; i++) {
                if (i === (paramMap.start) + 1) {
                    $(".pageNav").append(`<li class="page_button on" data-id="${i}">${i}</li>`);
                } else {
                    $(".pageNav").append(`<li class="page_button" data-id="${i}">${i}</li>`);
                }

            }
            $(".pageNav").append(`<li class="next" data-id=${resultData.endPage + 1}><i><img src="images/um/navNext.svg"></i></li>`);

            $(".pageNav li").on("click", function (e) {
                let id = $(e.currentTarget).data("id");
                console.log(id);
                let paramMap = {"start": id - 1, "length": 15};
                if (id <= resultData.pages)
                    ajaxLog(paramMap);
            })

        }

    });
    return returnValue;
}


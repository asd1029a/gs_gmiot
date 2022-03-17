$(document).ready(function () {
    let paramMap = {
        "start": 0,
        "length": 15

    }

    ajaxLog(paramMap);
});

$(".excel_download").on("click", function () {
        let beforeDate = $(".before_date").val();
        let afterDate = $(".after_date").val();
        let selectType = $("#selectType option:selected").val();
        let searchWord = $(".search_word").val();


        let paramMap;


        if (selectType === "all") {
            paramMap = {
                "start": 0,
                "length": 1,
                "searchType": 1,
                "deviceName": searchWord,
                "beforeDate": beforeDate,
                "afterDate": afterDate
            }

        } else if (selectType == "device_name") {
            paramMap = {
                "start": 0,
                "length": 1,
                "deviceName": searchWord,
                "beforeDate": beforeDate,
                "afterDate": afterDate
            }

        } else if (selectType == "mission_name") {
            paramMap = {
                "start": 0,
                "length": 1,
                "missionName": searchWord,
                "beforeDate": beforeDate,
                "afterDate": afterDate
            }
        }

        let resultData = null;
        $.ajax({
            contentType: "application/json; charset=utf-8",
            url: "drone/api/dronelog",
            type: "POST",
            async: false,
            data: JSON.stringify(paramMap),
            success: function (result) {

                resultData = result.data;
            }
        });
        paramMap = {
            dataMap: resultData,
            fileName: "Log.xlsx",
            headerList: ["아이디", "드론이름", "미션이름"]
        };

        excelDownload(paramMap);
    }
)


$(".search_button").on("click", function () {
    let beforeDate = $(".before_date").val();
    let afterDate = $(".after_date").val();
  //  console.log(beforeDate, afterDate);
    let selectType = $("#selectType option:selected").val();
    let searchWord = $(".search_word").val();
    if (selectType === "all") {
        let paramMap = {
            "start": 0,
            "length": 15,
            "searchType": 1,
            "deviceName": searchWord,
            "beforeDate": beforeDate,
            "afterDate": afterDate
        }
        ajaxLog(paramMap);
    } else if (selectType == "device_name") {
        let paramMap = {
            "start": 0,
            "length": 15,
            "deviceName": searchWord,
            "beforeDate": beforeDate,
            "afterDate": afterDate
        }
        ajaxLog(paramMap);
    } else if (selectType == "mission_name") {
        let paramMap = {
            "start": 0,
            "length": 15,
            "missionName": searchWord,
            "beforeDate": beforeDate,
            "afterDate": afterDate
        }
        ajaxLog(paramMap);
    }
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
            // console.log(resultData);
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
                <td><span class="button download_button" data-id="${i}">다운로드</span></td>
            </tr>
         
          `);


            });
            $(".download_button").on("click", function (e) {
                let id = $(e.currentTarget).data("id");
                //console.log(id);
                // console.log("data", resultData.data[id].droneLogDetails);
          //          console.log(resultData.data);
                let paramMap = {
                    dataMap: resultData.data[id].droneLogDetails,
                    fileName: "detailLog.xlsx"
                };
                excelDownload(paramMap);


            });
            if (resultData.startPage != 1)
                $(".pageNav").append(`<li class="prev" data-id=${resultData.startPage - 1} ><i><img src="images/um/navPrev.svg"></i></li>`);

            for (let i = resultData.startPage; i <= resultData.endPage; i++) {
                if (i === (paramMap.start) + 1) {
                    $(".pageNav").append(`<li class="page_button on" data-id="${i}">${i}</li>`);
                } else {
                    $(".pageNav").append(`<li class="page_button" data-id="${i}">${i}</li>`);
                }

            }

            if (resultData.endPage < resultData.pages)
                $(".pageNav").append(`<li class="next" data-id=${resultData.endPage + 1}><i><img src="images/um/navNext.svg"></i></li>`);

            $(".pageNav li").on("click", function (e) {
                let beforeDate = $(".before_date").val();
                let afterDate = $(".after_date").val();
                let selectType = $("#selectType option:selected").val();
                let searchWord = $(".search_word").val();
                let id = $(e.currentTarget).data("id");
                //  console.log(id);
                if (selectType === "all") {
                    let paramMap = {
                        "start": id - 1,
                        "length": 15,
                        "searchType": 1,
                        "deviceName": searchWord,
                        "beforeDate": beforeDate,
                        "afterDate": afterDate
                    }
                    ajaxLog(paramMap);
                } else if (selectType == "device_name") {
                    let paramMap = {
                        "start": id - 1, "length": 15,
                        "deviceName": searchWord,
                        "beforeDate": beforeDate,
                        "afterDate": afterDate
                    }
                    if (id <= resultData.pages)
                        ajaxLog(paramMap);

                } else if (selectType == "mission_name") {
                    let paramMap = {
                        "start": id - 1, "length": 15,
                        "missionName": searchWord,
                        "beforeDate": beforeDate,
                        "afterDate": afterDate
                    }
                    if (id <= resultData.pages)
                        ajaxLog(paramMap);

                }
            })

        }

    });
    return returnValue;
}

$(".search_reset").on("click", function () {
    let paramMap = {
        "start": 0,
        "length": 15

    }
    $(".before_date").val("");
    $(".after_date").val("");
    $("#selectType option:eq(0)").prop("selected", true);
    ajaxLog(paramMap);
});



function excelDownload(resultData) {
    axios({
        method: 'POST',
        url: '/file/excel/download',
        responseType: 'blob',
        data: resultData
    })
        .then(response => {
            // console.log(response);
            const url
                = window.URL.createObjectURL(new Blob([response.data],
                {type: "application/vnd.ms-excel"}));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', resultData.fileName);

            document.body.appendChild(link);
            link.click();
        });

}


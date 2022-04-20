/*
* 시설물 관련 JS
*/

const facility = {
    create : () => {
        const $target = $('#facilityTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            ajax :
                {
                    'url' : "/facility",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'data' : function ( d ) {
                        console.log(d);
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        console.log(result);
                        $('.title dd .count').text(result.recordsTotal);
                        return result.data;
                    }
                },
            select: {
                toggleable: false
            },
            columns : [
                {data: "facilitySeq", className: "alignLeft"},
                {data: "facilityKind"},
                {data: "facilityStatus"},
                {data: "stationKind"},
                {data: "stationName"},
                {data: "administZone"},
                {data: "address"},
                {data: null}
            ],
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button">상세보기</span>'
            }
            , {
                targets: 3,
                createdCell: function (td, cellData) {
                    if ( cellData !== null ) {
                        $(td).text("");
                        $(td).append("<i><img src='/images/default/clipboard.svg'></i>");
                    } else {
                        $(td).text("없음");
                    }
                }
            }
            , {
                targets: 6,
                render: $.fn.dataTable.render.ellipsis( 50, true )
            }]
            , excelDownload : true
        }

        const evt = {
            click : function(e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    //commonCode.showPopup('mod');
                    //$('#commonCodeForm').setItemValue(rowData);
                    event.get(rowData.facilitySeq, (result) => console.log(result));
                }
            }
        }
        comm.createTable($target ,optionObj, evt);
    }
    , getList : (param, pCallback) => {
        $.ajax({
            url : "/facility"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    }
    , getListGeoJson : (param, pCallback) => {
        $.ajax({
            url : "/facility/geojson"
            , type : "POST"
            , data : JSON.stringify(param)
            , contentType : "application/json; charset=utf-8"
        }).done((result) => {
            pCallback(result);
        });
    }
    , get : (pSeq, pCallback) => {
        $.ajax({
            url : "/facility/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    }
    , addProc : () => {
        const formObj = $('#facilityForm').serializeJSON();

        $.ajax({
            url : "/facility"
            , type: "PUT"
            , data : formObj
        }).done((result) => {
            comm.showAlert("시설물이 등록되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
    , modProc : () => {
        const formObj = $('#facilityForm').serializeJSON();

        $.ajax({
            url : "/facility"
            , type: "PATCH"
            , data : formObj
        }).done((result) => {
            comm.showAlert("시설물이 수정되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
    , modOptProc : (pData, pCallback) => {
        $.ajax({
            url : "/facility/opt"
            , type: "PATCH"
            , data : pData
        }).done((result) => {
            pCallback(result);
        });
    }
    , delProc : (pSeq) => {
        $.ajax({
            url : "/facility/"+pSeq
            , type: "DELETE"
        }).done((result) => {
            comm.showAlert("시설물이 삭제되었습니다");
            facility.create($('#facilityTable'));
            facility.hidePopup();
        });
    }
}

const dimming = {
    eventHandler : () => {
        $("#searchBtn").on('click', () => {
            comm.checkAuthority("/user/check/authority", "config", "rw")
                .then(
                    (result) => {
                        dimming.create(result);
                    }
                );
        });
    }
    , create : (pPermit) => {
        const $target = $('#dimmingGroupTable');
        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            security : true,
            autoWidth: true,
            ajax :
                {
                    'url' : "/facility/dimmingGroup",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'async' : false,
                    'data' : function ( d ) {
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        $('.title dd .count').text(result.recordsTotal);
                        return result.data;
                    }
                },
            select: true,
            columns : [
                {data: "dimmingGroupName"},
                {data: null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    render: $.fn.dataTable.render.ellipsis( 50, true )
                }
            ]
        }
        if(pPermit !== "none") {
            optionObj.columnDefs.push({
                "targets": 1,
                "data": null,
                "defaultContent": '<span class="button mod">수정</span>'
            });
        }
        const evt = {
            click : function(e) {
                const $form = $('#dimmingGroupForm');
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    /* TODO : 팝업 적용 후 작동 예정*/
                    dimming.showPopup();
                    dimming.get(rowData.dimmingGroupSeq ,(result) => {
                        $form.data("dimmingGroupSeq", rowData.dimmingGroupSeq);
                        $form.setItemValue(result);
                    });
                } else {
                    const param = {
                        'optType' : "dimming"
                        , 'dimmingGroupSeq' : rowData.dimmingGroupSeq
                    };
                    facility.getList(param, (result)=> {

                        //디밍 그룹별 맵 제어
                        const dimmingLayer = new dataLayer('dimmMap').fromRaw(
                            result.data, 'dimmingLayer', true, layerStyle.facility()
                        );

                        let dimmControl = new layerControl('dimmMap', 'title');
                        window.dimmControl = dimmControl;
                        window.dimmControl.remove('dimmingLayer');
                        window.dimmMap.addLayer(dimmingLayer);

                        let fitExtent = window.dimmControl.find('dimmingLayer').getSource().getExtent();
                        // TODO 데이터보고 조정하거나 삭제하거나
                        // fitExtent.forEach((ind, v) => {
                        //     console.log(ind, v);
                        // });

                        dimmMap.map.getView().fit(fitExtent,dimmMap.map.getSize());
                    });

                }
            }
        }
        comm.createTable($target ,optionObj, evt);
    }
    , init : () => {
        let dimmMap = new mapCreater('dimmMap', 0);
        window.dimmMap = dimmMap;
        window.dimmMap.setCenter(new ol.proj.fromLonLat(['126.8646558753815' ,'37.47857596680809'], 'EPSG:5181'));
        window.dimmMap.setZoom(11);

        //디밍 그룹별맵 제어 ban
        $('#dimmMap').prepend('<canvas style="position: absolute;background: #ff000000;width: 100%;height: 100%;z-index: 1;"></canvas>');
    }
    , get : (pSeq, pCallback) => {
        $.ajax({
            url : "/facility/dimmingGroup/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    }
    , addGroup : () => {

    }
    , modGroup : () => {

    }
    , delGroup : () => {

    }
    , getDimming : () => {

    }
    , addDimming : () => {

    }
    , showPopup : () => {
        $("dimmingGroupPopup").css('display', 'flex');
    }
    , hidePopup : () => {
        $("dimmingGroupPopup").hide();
    }
}
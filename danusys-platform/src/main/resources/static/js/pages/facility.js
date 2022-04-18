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
                        dimming.createGroup(result);
                    }
                );
        });
        $("#dimmingGroupPopup .title dd").on('click', () => {
            dimming.hidePopup();
        });
        $("#addDimmingGroupBtn").on('click', () => {
            dimming.showPopup("add");
        });
    }
    , createGroup : (pPermit) => {
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
                const param = {
                    'dimmingGroupSeq' : rowData.dimmingGroupSeq
                };
                if($(e.target).hasClass('button')) {
                    dimming.showPopup("mod");
                    dimming.getListLampRoadInGroup(param ,(result) => {

                    });
                } else {
                    dimming.getListLampRoadInGroup(param, (result)=> {
                        dimming.setData(result.data[0]);
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
        //디밍 그룹별 맵 초기화
        dimming.init();
    }
    , createLampRoad : () => {
        const $target = $('#lampRoadInGroupTable');
        const optionObj = {
            dom: '<"table_body"rt>',
            destroy: true,
            bPaginate: false,
            bServerSide: false,
            scrollY: "calc(100% - 50px)",
            ajax:
                {
                    'url': "/facility",
                    'contentType': "application/json; charset=utf-8",
                    'type': "POST",
                    'data' : function ( ) {
                        return JSON.stringify( {"facilityKind" : "lamp_road"} );
                    }
                }
            ,
            select: {
                toggleable: false
            }
            , columns: [
                {data: "facilitySeq"},
                {data: "facilityId"},
                {data: null}
            ]
            , columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span><input type="checkbox"/><label><span></span></label></span>'
            }]
            , fnCreatedRow: (nRow, aaData, iDataIndex) => {
                const facilitySeq = aaData.facilitySeq;
                $(nRow).find('input').prop('id', "check" + facilitySeq);
                $(nRow).find('input').prop('value', facilitySeq);
                $(nRow).find('label').prop('for', "check" + facilitySeq);
            }
            , excelDownload: false
            , search: {
                "search": "Fred"
            }
        }

        const evt = {
            click: function (e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
            }
        }
        comm.createTable($target, optionObj, evt);
    }
    , init : () => {
        let dimmMap = new mapCreater('dimmMap', 0);
        window.dimmMap = dimmMap;
        window.dimmMap.setCenter(new ol.proj.fromLonLat(['126.8646558753815' ,'37.47857596680809'], 'EPSG:5181'));
        window.dimmMap.setZoom(11);

        //디밍 그룹별맵 제어 ban
        $('#dimmMap').prepend('<canvas style="position: absolute;background: #ff000000;width: 100%;height: 100%;z-index: 1;"></canvas>');
    }
    , getListLampRoadInGroup : (param, pCallback) => {
        $.ajax({
            url : "/facility/lampRoadInGroup"
            , type : "POST"
            , contentType : "application/json; charset=utf-8"
            , data : JSON.stringify(param)
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
    , setData : (pData) => {
        $.each(pData, (name, value)=> {
            $(".setting_dimming #"+name).val(value);
            if(name === "dimmingTimeZone") {
                const dimmingTimeZone = JSON.parse(value.replaceAll('\n', ''));
                $.each(dimmingTimeZone, (time, setValue) => {
                    $("#dimmingTimeZoneTable input[name="+time+"_max]").val(setValue.max);
                    $("#dimmingTimeZoneTable input[name="+time+"_min]").val(setValue.min);
                })
            }
        })
    }
    , add : () => {

    }
    , showPopup : (type) => {
        comm.showModal($('#dimmingGroupPopup'));
        $("#dimmingGroupPopup").css('display', 'flex');
        $("#dimmingGroupPopup #dimmingGroupName").val("");
        dimming.createLampRoad();
        $('#dimmingGroupPopup [data-add], [data-mod]').hide();
        $('#dimmingGroupPopup [data-'+type+'="true"]').show();
    }
    , hidePopup : () => {
        comm.hideModal($('#dimmingGroupPopup'));
        $("#dimmingGroupPopup").hide();
    }
}
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
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
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

/* 디밍 관련 JS*/
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
            window.dimmGroupMap.updateSize();
            //TODO 그룹이 없는 나머지 시설물이 전부 보이는 맵 view move
        });
        $("#addDimmingGroupProcBtn").on('click', () => {
            const dimmingGroupName = $("#dimmingGroupName").val();
            const $checkedLampRoads = $("#lampRoadInGroupTable input[type='checkbox']:checked");
            const facilitySeqList = [];
            let obj = {};
            if(dimmingGroupName === "" || typeof dimmingGroupName === "undefined") {
                comm.showAlert("그룹이름이 작성되지 않았습니다.");
            } else {
                $checkedLampRoads.each((i, e)=> {
                    facilitySeqList.push($(e).val());
                });
                if(facilitySeqList.length === 0) {
                    comm.showAlert("시설물을 선택해주십시오.");
                } else {
                    obj.facilitySeqList = facilitySeqList;
                    obj.dimmingGroupName = dimmingGroupName;
                    obj.facilityOptType = "dimming";
                    dimming.addGroupProc(obj);
                }
            }
        });
        $("#modDimmingGroupProcBtn").on('click', () => {
            const dimmingGroupName = $("#dimmingGroupName").val();
            const dimmingGroupSeq = $("#dimmingGroupPopup").data("dimmingGroupSeq");
            const $checkedLampRoads = $("#lampRoadInGroupTable input[type='checkbox']:checked");

            if(dimmingGroupName === "" || typeof dimmingGroupName === "undefined") {
                comm.showAlert("그룹이름이 작성되지 않았습니다.");
            } else {
                if($checkedLampRoads.length === 0) {
                    comm.showAlert("시설물을 선택해주십시오.");
                } else {
                    const obj = {};
                    dimming.getListLampRoadInGroup({
                            'dimmingGroupSeq' : dimmingGroupSeq
                        },(result) => {
                            debugger;
                            const delFacilitySeqList = [];
                            $.each(result.data, (i, e) => {
                                delFacilitySeqList.push(Number(e.facilitySeq));
                            })
                            const newFacilitySeqList = [];
                            $checkedLampRoads.each((i, e)=> {
                                newFacilitySeqList.push(Number($(e).val()));
                            });
                            obj.dimmingGroupSeq = dimmingGroupSeq;
                            obj.delFacilitySeqList = delFacilitySeqList;
                            obj.facilitySeqList = newFacilitySeqList;
                            obj.dimmingGroupName = dimmingGroupName;
                            obj.facilityOptType = "dimming";
                            dimming.modGroupProc(obj);
                        }
                    );
                }
            }
        });
        $("#delDimmingGroupProcBtn").on('click', () => {
            const dimmingGroupSeq = $("#dimmingGroupPopup").data("dimmingGroupSeq");
            comm.confirm("해당 그룹을 삭제하시겠습니까?",
                {},
            () => {
                    dimming.getListLampRoadInGroup({"dimmingGroupSeq" : dimmingGroupSeq}
                    ,(result) => {
                        const delFacilitySeqList = [];
                        const obj = {};
                        $.each(result.data, (i, e) => {
                            delFacilitySeqList.push(e.facilitySeq);
                        });
                        obj.delFacilitySeqList = delFacilitySeqList;
                        obj.facilityOptType = "dimming"
                        dimming.delGroupProc(obj);
                    });
                },
                () => {return false;}
                )
        });
        $("#addDimmingSetBtn").on('click', () => {

        });
        $("#defaultDimmingSetBtn").on('click', () => {
            comm.confirm("기본값으로 설정하시겠습니까?",
                {text : "작성한 설정값이 사라집니다."},
                () => {
                    $(".setting_dimming ul li input").val("");
                    $("#dimmingTimeZoneTable tbody tr:nth-child(1) td input").val("100");
                    $("#dimmingTimeZoneTable tbody tr:nth-child(2) td input").val("50");
                },
                () => {return false;});
        });
    }
    , createGroup : (pPermit) => {
        const $target = $('#dimmingGroupTable');
        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "100px",
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
                const rowData = $target.DataTable().row($(e.currentTarget)).data();

                if($(e.target).hasClass('button')) {
                    dimming.showPopup("mod", rowData);
                } else {
                    dimming.getListLampRoadInGroup({
                        'dimmingGroupSeq' : rowData.dimmingGroupSeq
                    }, (result)=> {
                        dimming.setData(result.data[0]);
                        //디밍 그룹별 맵 제어
                        const dimmLayer = new dataLayer('dimmMap').fromRaw(
                            result.data, 'dimmLayer', true, layerStyle.facility()
                        );

                        let dimmControl = new layerControl('dimmMap', 'title');
                        window.dimmControl = dimmControl;
                        window.dimmControl.remove('dimmLayer');
                        window.dimmMap.addLayer(dimmLayer);

                        let fitExtent = window.dimmControl.find('dimmLayer').getSource().getExtent();
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
        window.dimmMap.updateSize();
    }
    , createLampRoad : (pObj) => {
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
                    'async': false,
                    'data' : function ( ) {
                        return JSON.stringify( pObj );
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
                const coordinates = new ol.proj.transform([Number(aaData.longitude), Number(aaData.latitude)], 'EPSG:4326', 'EPSG:5181');
                const feature = new ol.Feature({
                    geometry : new ol.geom.Point(coordinates),
                    properties : Object.assign(aaData, {"selected": false})
                })
                const facilitySeq = aaData.facilitySeq;
                $(nRow).find('input').prop('id', "check" + facilitySeq);
                $(nRow).find('input').prop('value', facilitySeq);
                $(nRow).find('label').prop('for', "check" + facilitySeq);
                $(nRow).data('feature', feature);
            }
            , excelDownload: false
            , search: {
                "search": "Fred"
            }
        }

        const evt = {
            click: function (e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                //해당 시설물로 이동 및 펄스
                const feature = $(e.currentTarget).data('feature');
                const coordinates = feature.getGeometry().getCoordinates();
                window.dimmGroupMap.setCenter(coordinates);
                window.dimmGroupMap.setZoom(13);

                window.dimmGroupMap.setPulse(coordinates);
                window.dimmGroupMap.removePulse();

                window.dimmGroupMap.map.render();
            }
        }
        comm.createTable($target, optionObj, evt);

        //시설물 표출
        window.dimmGroupMap.updateSize();
        const features = new Array();
        $('#lampRoadInGroupTable tbody tr').each((idx, item) => {
            features.push($(item).data('feature'));
        });
        const source =  window.dimmGroupControl.find('dimmGroupLayer').getSource();
        source.clear();
        source.addFeatures(features);
        source.changed();
        const fitExtent = source.getExtent();
        window.dimmGroupMap.map.getView().fit(fitExtent, window.dimmGroupMap.map.getSize());

    }
    , init : () => {
        const baseCenter = new ol.proj.fromLonLat(['126.8646558753815' ,'37.47857596680809'], 'EPSG:5181');

        //디밍 그룹별맵 초기설정
        let dimmMap = new mapCreater('dimmMap', 0);
        window.dimmMap = dimmMap;
        window.dimmMap.setCenter(baseCenter);
        window.dimmMap.setZoom(11);
        //디밍 그룹별맵 제어 ban
        $('#dimmMap').prepend('<canvas style="position: absolute;background: #ff000000;width: 100%;height: 100%;z-index: 1;"></canvas>');


        //디밍 그룹 설정맵 초기설정
        let dimmGroupMap = new mapCreater('dimmGroupMap', 0);
        dimmGroupMap.createMousePosition('mousePosition');
        // window.dimmGroupSelected = [];
        window.dimmGroupMap = dimmGroupMap;
        //select 처리
        dimmGroupMap.setMapEventListener('singleclick', e => {
            window.dimmGroupMap.map.forEachFeatureAtPixel(e.pixel, f => {
                const selectFlag = f.getProperties().properties.selected;
                if(!selectFlag) {
                    f.getProperties().properties.selected = true;
                    debugger;
                } else {
                    f.getProperties().properties.selected = false;
                }
                window.dimmGroupControl.find('dimmGroupLayer').getSource().changed();
                window.dimmGroupMap.map.render();
            });
        });
        const dimmGroupLayer = new dataLayer('dimmGroupMap').fromRaw(
            {}, 'dimmGroupLayer', true, layerStyle.facility()
        );
        window.dimmGroupMap.addLayer(dimmGroupLayer);

        const dimmGroupControl = new layerControl('dimmGroupMap', 'title');
        window.dimmGroupControl = dimmGroupControl;
        dimmGroupControl.remove(dimmGroupLayer);
        window.dimmGroupMap.setCenter(baseCenter);
        window.dimmGroupMap.setZoom(11);
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
    , addGroupProc : (pObj) => {
        $.ajax({
            url : "/facility/opt"
            , type: "PUT"
            , data :  JSON.strddddddddingify(pObj)
            , contentType : "application/json; charset=utf-8",
        }).done(() => {
            comm.showAlert("디밍 그룹이 등록되었습니다. <br/> 디밍설정을 설정해주십시오.");
            dimming.createGroup();
            dimming.hidePopup();
        }).fail(() => {
            comm.showAlert("디밍 그룹 등록에 실패했습니다.");
        });
    }
    , modGroupProc : (pObj) => {
        $.ajax({
            url : "/facility/opt"
            , type: "PATCH"
            , data :  JSON.stringify(pObj)
            , contentType : "application/json; charset=utf-8",
        }).done(() => {
            comm.showAlert("디밍 그룹이 수정되었습니다. <br/> 디밍설정을 재설정해주십시오.");
            dimming.createGroup();
            dimming.hidePopup();
        }).fail(() => {
            comm.showAlert("디밍 그룹 수정에 실패했습니다.");
        });
    }
    , delGroupProc : (pObj) => {
        $.ajax({
            url : "/facility/opt"
            , type: "DELETE"
            , data :  JSON.stringify(pObj)
            , contentType : "application/json; charset=utf-8",
        }).done(() => {
            comm.showAlert("디밍 그룹이 삭제되었습니다.");
            dimming.createGroup();
            dimming.hidePopup();
        }).fail(() => {
            comm.showAlert("디밍 그룹 삭제에 실패했습니다.");
        });
    }
    , setData : (pData) => {
        if(pData.maxBrightTime === null || pData.minBrightTime === null
            || pData.keepBrightTime === null || pData.dimmingTimeZone === null) {
            comm.showAlert("디밍 설정을 진행해주십시오.");
        } else {
            $.each(pData, (name, value)=> {
                $(".setting_dimming #"+name).val(value);
                if(name === "dimmingTimeZone") {
                    const dimmingTimeZone = value.split(',');
                    $.each(dimmingTimeZone, (idx, time) => {
                        const setValue = time.split('/');
                        $("#dimmingTimeZoneTable input[name=time_" + String(idx + 1)+"_max]").val(setValue[1]);
                        $("#dimmingTimeZoneTable input[name=time_" + String(idx + 1)+"_min]").val(setValue[0]);
                    })
                }
            })
        }
    }
    , addProc : () => {

    }
    , showPopup : (type, pRowData) => {
        const createObj = {
            facilityKind : "lamp_road"
            , createType : type
        }

        comm.showModal($('#dimmingGroupPopup'));
        $("#dimmingGroupPopup").css('display', 'flex');
        $("#dimmingGroupPopup #dimmingGroupName").val("");
        $('#dimmingGroupPopup [data-add], [data-mod]').hide();
        $('#dimmingGroupPopup [data-'+type+'="true"]').show();

        if(type === "add") {
            dimming.createLampRoad(createObj);
        } else if(type === "mod") {
            const dimmingGroupSeq = pRowData.dimmingGroupSeq;
            $('#dimmingGroupPopup').data("dimmingGroupSeq", dimmingGroupSeq);
            createObj.dimmingGroupSeq = dimmingGroupSeq

            dimming.createLampRoad(createObj);
            dimming.getListLampRoadInGroup({
                'dimmingGroupSeq' : dimmingGroupSeq
            } ,(result) => {
                $("#dimmingGroupPopup #dimmingGroupName").val(result.data[0].dimmingGroupName);
                $.each(result.data, (idx, obj) => {
                    $("#lampRoadInGroupTable #check"+obj.facilitySeq).prop("checked", true);
                })
                //
                // //TODO 누른 행 그룹에 속하는 모든 시설물(선택)
                // const $checkRows = $("#lampRoadInGroupTable input[type=checkbox]:checked");
                // $.each($checkRows, (idx, obj) => {
                //     const selectFeature = $(obj).parents('tr').data('feature');
                //     window.dimmGroupControl.find('dimmGroupLayer').getSource().forEachFeature(f => {
                //         if(f == selectFeature){
                //             f.getProperties().properties.selected = true;
                //         }
                //     })
                // });
                // window.dimmGroupControl.find('dimmGroupLayer').getSource().changed();
                // window.dimmGroupMap.map.render();
            });
        }
    }
    , hidePopup : () => {
        comm.hideModal($('#dimmingGroupPopup'));
        $('#dimmingGroupPopup').removeData("dimmingGroupSeq");
        $("#dimmingGroupPopup").hide();
    }
}
/*
* 디밍관련 JS
*/

const dimming = {
    /*
     * 디밍 페이지 이벤트 핸들러
     */
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
        });
        $("#addDimmingGroupProcBtn").on('click', () => {
            const dimmingGroupName = $("#dimmingGroupName").val();
            const $checkedLampRoads = $("#lampRoadInGroupTable input[type='checkbox']:checked");
            if(dimmingGroupName === "" || typeof dimmingGroupName === "undefined") {
                comm.showAlert("그룹이름이 작성되지 않았습니다.");
            } else {
                if($checkedLampRoads.length === 0) {
                    comm.showAlert("시설물을 선택해주십시오.");
                } else {
                    const facilitySeqList = [];
                    $checkedLampRoads.each((idx, ele)=> {
                        facilitySeqList.push($(ele).val());
                    });
                    let dimmingGroupSeq = Number(dimming.getLastGroupSeq().dimmingGroupSeq);
                    if(typeof dimmingGroupSeq === "undefined" || dimmingGroupSeq == null) {
                        dimmingGroupSeq = 1;
                    } else {
                        dimmingGroupSeq ++;
                    }
                    const optObj = {
                        "dimming_group_seq" : String(dimmingGroupSeq)
                        , "dimming_group_name" : dimmingGroupName
                    }
                    facility.addOptProc(facility.createFacilityOptList("1", facilitySeqList, optObj)
                        , () => {
                            comm.showAlert("디밍 그룹이 등록되었습니다. <br/> 디밍설정을 설정해주십시오.");
                            dimming.createGroup();
                            dimming.hidePopup();
                            dimming.setDefault();
                        }
                        , () => {
                            comm.showAlert("디밍 그룹 등록에 실패했습니다.");
                        });
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
                    dimming.getListLampRoadInGroup({
                            'dimmingGroupSeq' : dimmingGroupSeq
                        },(result) => {
                            const delFacilitySeqList = [];
                            $.each(result.data, (idx, value) => {
                                delFacilitySeqList.push(value.facilitySeq);
                            })
                            const facilitySeqList = [];
                            $checkedLampRoads.each((i, e)=> {
                                facilitySeqList.push($(e).val());
                            });
                            const optObj = {
                                "dimming_group_seq" : dimmingGroupSeq
                                , "dimming_group_name" : dimmingGroupName
                            }
                            let modDimmingGroupObj = facility.createFacilityOptList("1", facilitySeqList, optObj);
                            modDimmingGroupObj.delFacilitySeqList = delFacilitySeqList;
                            modDimmingGroupObj.ignoreDeleteOptList = ["dimming_time_zone"
                                , "keep_bright_time"
                                , "max_bright_time"
                                , "min_bright_time"];
                            modDimmingGroupObj.facilityOptType = "dimming";
                            facility.modOptProc(modDimmingGroupObj
                                ,() => {
                                    comm.showAlert("디밍 그룹이 수정되었습니다. <br/> 디밍설정을 재설정해주십시오.");
                                    dimming.createGroup();
                                    dimming.hidePopup();
                                    dimming.setDefault();
                                }
                                , () => {
                                    comm.showAlert("디밍 그룹 수정에 실패했습니다.");
                                });
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
                    dimming.getListLampRoadInGroup({
                            "dimmingGroupSeq" : dimmingGroupSeq}
                        ,(result) => {
                            const delFacilitySeqList = [];
                            const delDimmingGroupObj = {};
                            $.each(result.data, (i, e) => {
                                delFacilitySeqList.push(e.facilitySeq);
                            });
                            delDimmingGroupObj.delFacilitySeqList = delFacilitySeqList;
                            delDimmingGroupObj.facilityOptType = "dimming"
                            facility.delOptProc(delDimmingGroupObj
                                , () => {
                                    comm.showAlert("디밍 그룹이 삭제되었습니다.");
                                    dimming.createGroup();
                                    dimming.hidePopup();
                                    dimming.setDefault();
                                }
                                , () => {
                                    comm.showAlert("디밍 그룹 삭제에 실패했습니다.");
                                });
                        });
                },
                () => {return false;}
            )
        });
        $("#addDimmingSetBtn").on('click', () => {
            const dimmingGroupData = $("#dimmingGroupTable").DataTable().row(".selected").data();
            if(typeof dimmingGroupData === "undefined" || dimmingGroupData === null) {
                comm.showAlert("설정할 디밍 그룹을 선택 후 진행해주십시오.");
            } else {
                dimming.getListLampRoadInGroup({
                        'dimmingGroupSeq' : dimmingGroupData.dimmingGroupSeq
                    },(result) => {
                        const facilityIdList = [];
                        const facilitySeqList = [];
                        const timeList = [];
                        const dimmingSetObj = {};

                        $.each(result.data, (i, e) => {
                            facilitySeqList.push(e.facilitySeq);
                            facilityIdList.push(e.facilityId);
                        });
                        for (let i = 0; i < 24; i++) {
                            const max = $("#dimmingTimeZoneTable tbody tr:nth-child(1) td input")[i].value;
                            const min = $("#dimmingTimeZoneTable tbody tr:nth-child(2) td input")[i].value;
                            timeList.push(min + "/" + max);
                        }

                        dimmingSetObj.callUrl = "/add/dimmingUpdate";
                        dimmingSetObj.maxBrightTime = $("#maxBrightTime").val();
                        dimmingSetObj.keepBrightTime = $("#keepBrightTime").val();
                        dimmingSetObj.minBrightTime = $("#minBrightTime").val();
                        dimmingSetObj.deviceIds = facilityIdList.join(',');
                        dimmingSetObj.time = timeList.join(', ');

                        console.log(dimmingSetObj);
                        dimming.sendDimmingProc(dimmingSetObj, () => {
                        const optObj = {
                            "dimming_time_zone" : timeList.join(',')
                            , "keep_bright_time" : $("#keepBrightTime").val()
                            , "max_bright_time" : $("#maxBrightTime").val()
                            , "min_bright_time" : $("#minBrightTime").val()
                        }
                        let modDimmingSetObj = facility.createFacilityOptList("1", facilitySeqList, optObj);
                        modDimmingSetObj.delFacilitySeqList = facilitySeqList;
                        modDimmingSetObj.ignoreDeleteOptList = ["dimming_group_name", "dimming_group_seq"];
                        modDimmingSetObj.facilityOptType = "dimming";

                        facility.modOptProc(modDimmingSetObj
                            , () => {
                                comm.showAlert("디밍 설정이 저장되었습니다.");
                                dimming.createGroup();
                            }
                            , () => {
                                comm.showAlert("디밍 설정을 실패했습니다.");
                            });
                        });
                    }
                );
            }
        });
        $("#defaultDimmingSetBtn").on('click', () => {
            comm.confirm("기본값으로 설정하시겠습니까?",
                {text : "작성한 설정값이 사라지며, 재적용 해야합니다."},
                () => {
                    dimming.setDefault();
                },
                () => {return false;});
        });
    }
    /*
     * 디밍 그룹 테이블 생성
     * @param 권한 존재 유무 (없음 : 'none')
     */
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
            select: {
                toggleable: false
            },
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
                            result.data, 'dimmLayer', true, layerStyle.dimming()
                        );

                        let dimmControl = new layerControl('dimmMap', 'title');
                        window.dimmControl = dimmControl;
                        window.dimmControl.remove('dimmLayer');
                        window.dimmMap.addLayer(dimmLayer);

                        let fitExtent = window.dimmControl.find('dimmLayer').getSource().getExtent();
                        dimmMap.map.getView().fit(fitExtent,dimmMap.map.getSize());
                        dimmMap.map.getView().setZoom(dimmMap.map.getView().getZoom() - 0.5);
                    });
                }
            }
        }
        comm.createTable($target ,optionObj, evt);
        window.dimmMap.updateSize();
    }
    /*
     * 가로등 테이블 생성
     * @param1 : pObj = {디밍 그룹 정보}
     * @param2 : type = ( 추가 = 'add' || 수정 = 'mod')
     */
    , createLampRoad : (pObj) => {
        console.log(pObj);
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
                    'data' : function () {
                        return JSON.stringify( pObj );
                    },
                    'dataSrc' : function(result) {
                        if(pObj.activeType === "add") {
                            return result.data;
                        } else {
                            let selectedFacilityList = null;

                            dimming.getListLampRoadInGroup({"dimmingGroupSeq": pObj.dimmingGroupSeq},
                                (lampRoadInGroup) => {
                                    let facilityAll = result.data;
                                    let facilitySel = lampRoadInGroup.data;

                                    facilityAll.filter(facility => {
                                        let flag = false;
                                        for (let obj of facilitySel) {
                                            if (Number(obj.facilitySeq) == Number(facility.facilitySeq)) {
                                                flag = true;
                                                break;
                                            }
                                        }
                                        facility.selected = flag;
                                        return facility;
                                    });
                                    selectedFacilityList = facilityAll;
                                });
                            return selectedFacilityList;
                        }
                    }
                }
            ,
            select: {
                toggleable: false
            }
            , columns: [
                {data: "facilityId"},
                {data: "facilityKindName"},
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
                    properties : aaData
                });
                const facilitySeq = aaData.facilitySeq;
                const selected = aaData.selected;
                $(nRow).find('input').prop('id', "check" + facilitySeq);
                $(nRow).find('input').prop('value', facilitySeq);
                $(nRow).find('label').prop('for', "check" + facilitySeq);
                if(selected) {
                    $(nRow).find('input').prop("checked", true);
                }
                $(nRow).data('feature', feature);
            }
            , excelDownload: false
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

                //체크박스 선택시 시설물 선택
                if($(e.target).prop('tagName') == "INPUT"){
                    window.dimmGroupControl.find('dimmGroupLayer').getSource().forEachFeature(f => {
                        if(feature == f){
                            const flag = f.getProperties().properties.selected;
                            if(flag){
                                //시설물 해제
                                f.getProperties().properties.selected = false;
                            } else {
                                //시설물 선택
                                f.getProperties().properties.selected = true;
                            }
                        }
                    });
                    window.dimmGroupControl.find('dimmGroupLayer').getSource().changed();
                }
                window.dimmGroupMap.map.render();
            }
            , keyup : function() {
                $("#lampRoadKeyword").off("keyup");
                $("#lampRoadKeyword").on("keyup", function (input) {
                    const keyword = $(input.currentTarget).val();
                    if(keyword === "") {
                        $('#lampRoadInGroupTable tbody tr').show();
                    } else {
                        $('#lampRoadInGroupTable').DataTable().rows().data().each((data, idx)=> {
                            const rowHtml = $('#lampRoadInGroupTable').DataTable().row(idx).node();
                            if(data.facilityId.includes(keyword)) {
                                $(rowHtml).show();
                            } else {
                                $(rowHtml).hide();
                            }
                        });
                    }
                });
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
        source.forEachFeature(f => {
            f.setStyle(layerStyle.dimming(f.getProperties().properties.selected));
        });
        source.changed();
        const fitExtent = source.getExtent();
        window.dimmGroupMap.map.getView().fit(fitExtent, window.dimmGroupMap.map.getSize());
        window.dimmGroupMap.map.getView().setZoom(window.dimmGroupMap.map.getView().getZoom() - 0.5);
    }
    /*
     * 디밍 페이지 초기 지도 생성
     */
    , initMap : () => {
        const baseCenter = new ol.proj.fromLonLat(['126.8646558753815' ,'37.47857596680809'], 'EPSG:5181');

        //디밍 그룹별맵 초기설정
        let dimmMap = new mapCreater('dimmMap', 0);
        window.dimmMap = dimmMap;
        window.dimmMap.setCenter(baseCenter);
        window.dimmMap.setZoom(11);
        //디밍 그룹별맵 제어 ban
        $('#dimmMap').prepend('<canvas class="map_mouse_ban"></canvas>');

        //디밍 그룹 설정맵 초기설정
        let dimmGroupMap = new mapCreater('dimmGroupMap', 0);
        dimmGroupMap.createMousePosition('mousePosition');
        // window.dimmGroupSelected = [];
        window.dimmGroupMap = dimmGroupMap;
        //select 처리
        dimmGroupMap.setMapEventListener('singleclick', e => {
            window.dimmGroupMap.map.forEachFeatureAtPixel(e.pixel, f => {
                const selectFlag = f.getProperties().properties.selected;
                const selectSeq = f.getProperties().properties.facilitySeq;
                const $checkInput = $('#lampRoadInGroupTable tbody input#check'+selectSeq);
                //시설물 선택 토글
                f.getProperties().properties.selected = !selectFlag;
                //체크 박스 토글
                $checkInput.prop('checked', !selectFlag);
                //리스트 스크롤
                const checkPosition = $checkInput.parents('tr').position().top;
                $('#lampRoadInGroupTable').parents('.dataTables_scrollBody').scrollTop(checkPosition);

                window.dimmGroupControl.find('dimmGroupLayer').getSource().changed();
                window.dimmGroupMap.map.render();
            });
        });
        const dimmGroupLayer = new dataLayer('dimmGroupMap').fromRaw(
            {}, 'dimmGroupLayer', true, layerStyle.dimming(false)
        );
        window.dimmGroupMap.addLayer(dimmGroupLayer);

        const dimmGroupControl = new layerControl('dimmGroupMap', 'title');
        window.dimmGroupControl = dimmGroupControl;
        dimmGroupControl.remove(dimmGroupLayer);
        window.dimmGroupMap.setCenter(baseCenter);
        window.dimmGroupMap.setZoom(11);

        //map mouse over event
        let target = dimmGroupMap.map.getTarget();
        let jTarget = typeof target === "string" ? $("#" + target) : $(target);
        $(dimmGroupMap.map.getViewport()).on('mousemove', e => {
            const pixel = dimmGroupMap.map.getEventPixel(e.originalEvent);
            const hit = dimmGroupMap.map.forEachFeatureAtPixel(pixel, (feature, layer) => true);
            const cTarget = $(e.target);
            let popup = new mapPopup('dimmGroupMap');

            if (hit) {
                if(cTarget[0].tagName=="CANVAS") {
                    jTarget.css("cursor", "pointer");
                    dimmGroupMap.map.forEachFeatureAtPixel(pixel, (feature, layer) => {
                        if (layer) {
                            let position = feature.getGeometry().getCoordinates();
                            let content = mapPopupContent.dimming(feature.getProperties().properties);

                            popup.create('mouseOverPopup');
                            popup.content('mouseOverPopup', content);
                            popup.move('mouseOverPopup', position);
                        }
                    });
                }
            } else {
                jTarget.css("cursor", "all-scroll");
                popup.remove('mouseOverPopup');
            }
        });
    }
    /*
     * 디밍그룹 소속 가로등 리스트 조회
     * @param1 : 가로등 param
     * @param2 : callback function
     * @returns : callback function
     */
    , getListLampRoadInGroup : (param, pCallback) => {
        $.ajax({
            url : "/facility/lampRoadInGroup"
            , type : "POST"
            , contentType : "application/json; charset=utf-8"
            , data : JSON.stringify(param)
            , async : false
        }).done((result) => {
            pCallback(result);
        });
    }
    /*
    * 디밍 그룹 마지막 시퀀스 조회
    * @returns : dimmingGroupSeq
    */
    , getLastGroupSeq : () => {
        let returnValue = "";
        $.ajax({
            url : "/facility/lastDimmingGroupSeq"
            , type : "GET"
            , contentType : "application/json; charset=utf-8"
            , async : false
        }).done((result) => {
            returnValue = result;
        });
        return returnValue;
    }
    /*
    * 디밍 그룹 디밍설정 데이터 세팅
    * @param : pData = 세팅 데이터 (JSON 형식)
    */
    , setData : (pData) => {
        if(typeof pData.maxBrightTime === "undefined" || pData.maxBrightTime === null
            || typeof pData.minBrightTime === "undefined" || pData.minBrightTime === null
            || typeof pData.keepBrightTime === "undefined" || pData.keepBrightTime === null
            || typeof pData.dimmingTimeZone === "undefined" || pData.dimmingTimeZone === null) {
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
    /*
    * 디밍 그룹 디밍설정 데이터 초기화
    */
    , setDefault : () => {
        $(".setting_dimming ul li input").val("");
        $(".setting_dimming table tbody tr:nth-child(1) td input").val("100");
        $(".setting_dimming table tbody tr:nth-child(2) td input").val("50");
    }
    /*
    * 디밍설정 외부 연계 전송
    * param1 : pObj = 디밍 설정 정보(JSON 형식)
    * param2 : callback function
    * returns : callback function
    */
    , sendDimmingProc : (pObj, pCallback) => {
        $.ajax({
            url : "/api/call"
            , type : "POST"
            , contentType : "application/json; charset=utf-8"
            , data : JSON.stringify(pObj)
        }).done((result) => {
            pCallback(result);
        });
    }
    /*
    * 디밍 페이지 팝업 열기
    * param1 : type (추가 : 'add', 수정 : 'mod')
    * param2 : pRowData (디밍 그룹 row data)
    */
    , showPopup : (type, pRowData) => {
        const createObj = {
            facilityKind : ["lamp_road", "lamp_walk"]
            , activeType : type
            , popupType : "dimming"
        }

        comm.showModal($('#dimmingGroupPopup'));
        $("#dimmingGroupPopup").css('display', 'flex');
        $("#dimmingGroupPopup #dimmingGroupName").val("");
        $('#dimmingGroupPopup [data-add], [data-mod]').hide();
        $('#dimmingGroupPopup [data-'+type+'="true"]').show();

        if(type === "add") {
            dimming.createLampRoad(createObj);
        } else if(type === "mod") {
            debugger;
            $("#dimmingGroupPopup #dimmingGroupName").val(pRowData.dimmingGroupName);
            const dimmingGroupSeq = pRowData.dimmingGroupSeq;
            $('#dimmingGroupPopup').data("dimmingGroupSeq", dimmingGroupSeq);
            createObj.dimmingGroupSeq = dimmingGroupSeq

            dimming.createLampRoad(createObj);
        }
    }
    /*
    * 디밍 페이지 팝업 닫기 
    */
    , hidePopup : () => {
        comm.hideModal($('#dimmingGroupPopup'));
        $('#dimmingGroupPopup').removeData("dimmingGroupSeq");
        $("#dimmingGroupPopup").hide();
    }
}
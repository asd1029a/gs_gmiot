/**
 * 공통 코드 관리
 */

const commonCode = {
    eventHandler : ($target, pParentCode) => {
        $("#searchBtn").on('let', (e) => {
            commonCode.create($target, pParentCode);
        });

        $("[data-add-popup]").on('click', (e) => {
            commonCode.showPopup('add',$(e.target).data('addPopup'));
        });
        $("[data-add-popup]").hide();
        $("[data-add-popup='0']").show();

        $("#commonCodePopup .title dd").on('click', () => {
            commonCode.hidePopup();
        });

        $("#commonCodePopup #addProcBtn").on('click', () => {
            const depth = $('#commonCodeForm').data('depth');
            commonCode.addProc(depth);
        });

        $("#commonCodePopup #modProcBtn").on('click', () => {
            const depth = $('#commonCodeForm').data('depth');
            commonCode.modProc(depth);
        });

        $("#commonCodePopup #delProcBtn").on('click', () => {
            const depth = $('#commonCodeForm').data('depth');
            commonCode.delProc(depth);
        });
    },
    create : ($target, pParentCodeSeq) => {
        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 40px)",
            ajax :
                {
                    'url' : "/config/commonCode",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'data' : function ( d ) {
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        pParentCodeSeq!==0?param.keyword='':'';
                        param.parentCodeSeq = pParentCodeSeq;
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        const targetSeq = Number($target.data('tableSeq'));
                        $('.search_list:nth-of-type('+(targetSeq+1)+') .title dd .count').text(result.recordsTotal);
                        return result.data;
                    }
                },
            select: {
                toggleable: false,
                style: "single"
            },
            columns : [
                {data: "codeId", className: "alignLeft"},
                {data: "codeName"},
                {data: "useKind"},
                {data: null}
            ],
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button mod" data-add-popup="'+Number($target.data('tableSeq'))+'">상세</span>'
            }
            , {
                targets: 2,
                    createdCell: function (td, cellData) {
                    $(td).text("");
                    if ( cellData === 'Y' ) {
                        $(td).append("<span class=\"status green\"></span>사용");
                    } else {
                        $(td).append("<span class=\"status \"></span>사용 안함");
                    }
                }
            }]
            , excelDownload : false
        }
        const evt = {
            click : function(e) {
                const targetSeq = Number($target.data('tableSeq'));
                const $form = $('#commonCodeForm');
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    const buttonType = $(e.target).attr("class").split(' ')[1];
                    commonCode.showPopup(buttonType, $(e.target).data('addPopup'));
                    commonCode.get(rowData.codeSeq ,(result) => {
                        $form.data("codeSeq", rowData.codeSeq);
                        if(targetSeq !== 0) {
                            $form.data("parentCodeSeq", rowData.parentCodeSeq);
                        }
                        if(typeof result.insertDt !== "undefined" || typeof result.updateDt !== "undefined") {

                            result.insertDt = dateFunc.getUnixToDateYyyyMmDd(new Date(result.insertDt), "-");
                            result.updateDt = dateFunc.getUnixToDateYyyyMmDd(new Date(result.updateDt), "-");
                        }
                        $form.setItemValue(result);
                    });
                }
                if(targetSeq !== 2) {
                    $(".search_list:nth-of-type("+(targetSeq+2)+") .title dt span").text("["+rowData.codeName+"]");
                    commonCode.create($("[data-table-seq='"+(targetSeq+1)+"']"),rowData.codeSeq);
                    $("[data-add-popup='"+(targetSeq+1)+"']").show();
                }
            }
            // 클릭시 디테일 select 추가해야함
        }
        comm.createTable($target ,optionObj, evt);
    },
    /**
     * commonCode 조회
     * @param {object} pObj - {
     *     draw: null일시 View 조회
     *     ,type: 데이터종류(stationKind, district, facilityKind, null)
     *     ,start: paging 시작 idx
     *     ,length: paging row 갯수
     *     ,pParentCode: 조회할 parent_code_seq
     * }
     * @param {function} pCallback - callback function
     * */
    getList : (pObj, pCallback) => {
        $.ajax({
            url : "/config/commonCode"
            , type : "POST"
            , data : JSON.stringify(pObj)
            , contentType : "application/json; charset=utf-8"
            , async: false
        }).done((result) => {
            pCallback(result);
        })
    },
    get : (pSeq, pCallback) =>{
        $.ajax({
            url : "/config/commonCode/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    },
    showPopup : (type, pDepth) => {
        comm.showModal($('#commonCodePopup'));
        $('#commonCodePopup').css("display", "flex");
        $('#commonCodeForm').initForm();
        $('#commonCodePopup [data-add], [data-mod]').hide();
        $('#commonCodePopup').data("depth", pDepth);

        let title = "";
        title = pDepth !== 0 ? $('.search_list:nth-of-type('+(pDepth+1)+') [data-popup-title]').text() : "";
        $('#commonCodePopup [data-'+type+'="true"]').show();
        $('#commonCodeForm').data('depth', pDepth);
        if(type === "add") {
            $('#commonCodeForm').data('codeSeq', '');
            if(pDepth === 0) {
                $('#commonCodeForm').data('parentCodeSeq', 0);
            } else {
                const parentData = $(".search_table [data-table-seq="+Number(pDepth-1)+"]").DataTable().row(".selected").data();
                $('#commonCodeForm').data('parentCodeSeq', parentData.codeSeq);
            }
            $('#commonCodePopup .title dt').text(title+"코드 등록");
        } else if(type === "mod") {
            $('#commonCodePopup .title dt').text(title+'코드 수정');
        }
    },
    hidePopup : () => {
        $('#commonCodePopup .popupContents').scrollTop(0);
        comm.hideModal($('#commonCodePopup'));
        $('#commonCodePopup').hide();
    },
    addProc : (pDepth) => {
        const $form = $('#commonCodeForm');
        const formObj = $form.serializeJSON();
        let parentCodeSeq = $form.data('parentCodeSeq');
        formObj.parentCodeSeq = parentCodeSeq;

        if($form.doValidation()) {
            $.ajax({
                url : "/config/commonCode"
                , type : "PUT"
                , data : JSON.stringify(formObj)
                , contentType : "application/json; charset=utf-8",
            }).done(() => {
                comm.showAlert("코드가 등록되었습니다");
                commonCode.clearDepthTable();
                if(pDepth === 0) {
                    parentCodeSeq = 0;
                }
                commonCode.create($(".search_table [data-table-seq="+Number(pDepth)+"]"), parentCodeSeq);
                commonCode.hidePopup();
            }).fail(() => {
                comm.showAlert("코드 등록에 실패했습니다.");
            });
        } else {
            return false;
        }
    },
    modProc : (pDepth) => {
        const $form = $('#commonCodeForm');
        const formObj = $form.serializeJSON();
        let parentCodeSeq = $form.data('parentCodeSeq');
        formObj.codeSeq = $form.data('codeSeq');

        if($form.doValidation()) {
            $.ajax({
                url : "/config/commonCode"
                , type : "PATCH"
                , data : JSON.stringify(formObj)
                , contentType : "application/json; charset=utf-8",
            }).done(() => {
                comm.showAlert("코드가 수정되었습니다");
                commonCode.clearDepthTable();
                if(pDepth === 0) {
                    parentCodeSeq = 0;
                }
                commonCode.create($(".search_table [data-table-seq="+Number(pDepth)+"]"), parentCodeSeq);
                commonCode.hidePopup();
            }).fail(() => {
                comm.showAlert("코드 수정에 실패했습니다.");
            });
        } else {
            return false;
        }
    },
    delProc : (pDepth) => {
        const $form = $('#commonCodeForm');
        const codeSeq = $form.data('codeSeq');
        let parentCodeSeq = $form.data('parentCodeSeq');

        $.ajax({
                url : "/config/commonCode/" + codeSeq
                , type : "DELETE"
            }).done(() => {
                comm.showAlert("코드가 삭제되었습니다");
                commonCode.clearDepthTable();
                if(pDepth === 0) {
                    parentCodeSeq = 0;
                }
                commonCode.create($(".search_table [data-table-seq="+Number(pDepth)+"]"), parentCodeSeq);
                commonCode.hidePopup();
            }).fail(() => {
            comm.showAlert("코드 삭제에 실패했습니다.");
        });
    },
    clearDepthTable : () => {
        const $secondCodeTable = $("#secondCodeTable");
        const $thirdCodeTable = $("#thirdCodeTable");
        $(".search_list:nth-child(2) .article_title [data-popup-title=true]").text("");
        $(".search_list:nth-child(3) .article_title [data-popup-title=true]").text("");
        $(".search_list:nth-child(2) .article_title .count").text("0");
        $(".search_list:nth-child(3) .article_title .count").text("0");
        $(".search_list:nth-child(2) .article_title ul li").hide();
        $(".search_list:nth-child(3) .article_title ul li").hide();
        $secondCodeTable.DataTable().destroy();
        $secondCodeTable.find("tbody").empty()
        $thirdCodeTable.DataTable().destroy();
        $thirdCodeTable.find("tbody").empty();
    }
}
/**
 * 공통 코드 관리
 */

const commonCode = {
    eventHandler : ($target, pParentCode) => {
        $("#searchBtn").on('click', (e) => {
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
    },
    create : ($target, pParentCode) => {
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
                        pParentCode!==0?param.keyword='':'';
                        param.pParentCode = pParentCode;
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
                {data: "codeSeq", className: "alignLeft"},
                {data: "codeName"},
                {data: "useKind"},
                {data: null}
            ],
            columnDefs: [{
                "targets": -1,
                "data": null,
                "defaultContent": '<span class="button mod" data-add-popup="'+Number($target.data('tableSeq'))+'">상세보기</span>'
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
                    commonCode.showPopup(buttonType,$(e.target).data('addPopup'));
                    commonCode.get(rowData.codeSeq ,(result) => {
                        $form.data("codeSeq", rowData.codeSeq);
                        $form.setItemValue(result);
                    });
                }
                else if(targetSeq !== 2) {
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
    showPopup : (type, seq) => {
        //$('#commonCodePopup .popupContents').scrollTop(0);
        comm.showModal($('#commonCodePopup'));
        $('#commonCodePopup').css("display", "flex");
        $('#commonCodeForm').initForm();
        $('#commonCodePopup [data-add], [data-mod]').hide();

        /*/!*$('#commonCodePopup [data-popup]').hide();
        $('#commonCodePopup [data-popup] input').attr('disabled',true);*!/
        $('#commonCodePopup [data-popup="'+$targetId+'"] input').attr('disabled',false);*/
        let title = "";
        title = seq !== 0 ? $('.search_list:nth-of-type('+(seq+1)+') [data-popup-title]').text() : "";
        $('#commonCodePopup [data-'+type+'="true"]').show();
        if(type === "add") {
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
    addProc : () => {
        const formObj = $('#commonCodeForm').serializeJSON();

        comm.ajaxPost({
                url : "/common/addCommonCodeProc"
                , type : "PUT"
                , data : formObj
            },
            (result) => {
                comm.showAlert("등록되었습니다");
                commonCode.create($('#parentCodeTable'));
                commonCode.hidePopup();
            });
    },
    modProc : () => {
        const formObj = $('#commonCodeForm').serializeJSON();

        comm.ajaxPost({
                url : "/common/modCommonCodeProc"
                , type : "PATCH"
                , data : formObj
            },
            (result) => {
                comm.showAlert("수정되었습니다");
                commonCode.create($('#parentCodeTable'));
                commonCode.hidePopup();
            });
    },
    delProc : (pSeq) => {
        comm.ajaxPost({
                url : "/common/delCommonCodeProc"
                , type : "DELETE"
                , data : {commonCodeSeq : pSeq}
            },
            (result) => {
                comm.showAlert("삭제되었습니다");
                commonCode.create($('#parentCodeTable'));
                commonCode.hidePopup();
            });
    }
}
/**
 * 공통 코드 관리
 */

const commonCode = {
    create : ($target, pParentCode) => {
        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 45px)",
            ajax :
                {
                    'url' : "/config/commonCode",
                    'contentType' : "application/json; charset=utf-8",
                    'type' : "POST",
                    'data' : function ( d ) {
                        console.log(d);
                        const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                        param.pParentCode = pParentCode;
                        return JSON.stringify( param );
                    },
                    'dataSrc' : function (result) {
                        console.log(result);
                        const targetSeq = Number($target.attr('data-table-seq'));
                        $('.search_list:nth-of-type('+(targetSeq+1)+') .title dd .count').text(result.recordsTotal);
                        return result.data;
                    }
                },
            select: {
                toggleable: false
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
                "defaultContent": '<span class="button">상세보기</span>'
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
                const targetSeq = Number($target.attr('data-table-seq'));

                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('button')) {
                    //commonCode.showPopup('mod');
                    //$('#commonCodeForm').setItemValue(rowData);
                    commonCode.get(rowData.codeSeq, (result) => console.log(result));
                }
                else if(targetSeq !== 2) {
                    commonCode.create($("[data-table-seq='"+(targetSeq+1)+"']"),rowData.codeSeq);
                }
            }
            // 클릭시 디테일 select 추가해야함
        }
        comm.createTable($target ,optionObj, evt);
    },
    getList : (param, pCallback) => {
        comm.ajaxPost({
            url : "/config/commonCode"
            , data : {}
        }, (result) => {
            pCallback(result);
        });
    },
    get : (pSeq, pCallback) =>{
        $.ajax({
            url : "/config/commonCode/" + pSeq
            , type : "GET"
        }).done((result) => {
            pCallback(result);
        });
    },
    showPopup : (type) => {
        $('#commonCodePopup .popupContents').scrollTop(0);
        comm.showModal($('#commonCodePopup'));
        $('#commonCodePopup').css("display", "flex");
        $('#commonCodeForm').initForm();
        $('#commonCodePopup [data-mode]').hide();
        if(type === "add") {
            $('#commonCodePopup .popupTitle h4').text("게시글 등록");
            $('#commonCodePopup').css('height', '480px');
            $('#commonCodePopup [data-mode="'+type+'"]').show();
        } else if(type === "mod") {
            $('#commonCodePopup .popupTitle h4').text('게시글 수정');
            $('#commonCodePopup').css('height', '780px');
            $('#commonCodePopup [data-mode="'+type+'"]').show();
        } else if(type === "detail") {
            $('#commonCodePopup .popupTitle h4').text("상세");
            $('#commonCodePopup').css('height', '610px');
            $('#commonCodePopup [data-mode="'+type+'"]').show();
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
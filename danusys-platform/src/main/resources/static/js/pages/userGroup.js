/**
 * 사용자 그룹 관리
 */

const userGroup = {
    create : () => {
        const $target = $('#userAccountTable');

        const optionObj = {
            dom: '<"table_body"rt><"table_bottom"p>',
            destroy: true,
            pageLength: 15,
            scrollY: "calc(100% - 45px)",
            ajax :
                {
                    'url' : "/user/getListUserGroup",
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
                {data: "title", className: "alignLeft"},
                {data: "content", className: "alignLeft"},
                {data: "insertAdminId"},
                {data: "insertDt"},
                {data: null}
            ]
            // "columnDefs": [{
            //     "targets": -1,
            //     "data": null,
            //     "defaultContent": '<span class="writeButton"><i></i></span>'
            // }
            //     , {
            //         targets: 0,
            //         render: $.fn.dataTable.render.ellipsis( 30, true )
            //     }
            //     , {
            //         targets: 1,
            //         render: $.fn.dataTable.render.ellipsis( 50, true )
            //     }]
            , excelDownload : true
        }

        const evt = {
            click : function(e) {
                const rowData = $target.DataTable().row($(e.currentTarget)).data();
                if($(e.target).hasClass('writeButton') || $(e.target).prop('tagName') === "I") {
                    userGroup.showPopup('mod');
                    $('#userGrouptForm').setItemValue(rowData);
                }
            }
        }
        comm.createTable($target ,optionObj, evt);
    },
    getList : (pCallback) => {
        comm.ajaxPost({
            url : "/user/getListUserGroup"
            , data : {}
        }, (result) => {
            pCallback(result);
        });
    },
    showPopup : (type) => {
        $('#userGroupPopup .popupContents').scrollTop(0);
        comm.showModal($('#userGroupPopup'));
        $('#userGroupPopup').css("display", "flex");
        $('#userGroupForm').initForm();
        $('#userGroupPopup [data-mode]').hide();
        if(type === "add") {
            $('#userGroupnPopup .popupTitle h4').text("사용자 그룹 등록");
            $('#userGroupPopup').css('height', '480px');
            $('#userGroupPopup [data-mode="'+type+'"]').show();
        } else if(type === "mod") {
            $('#userGroupPopup .popupTitle h4').text('사용자 그룹 수정');
            $('#userGroupPopup').css('height', '780px');
            $('#userGroupPopup [data-mode="'+type+'"]').show();
        } else if(type === "detail") {
            $('#userGroupPopup .popupTitle h4').text("사용자 그룹 상세");
            $('#userGroupPopup').css('height', '610px');
            $('#userGroupPopup [data-mode="'+type+'"]').show();
        }
    },
    hidePopup : () => {
        $('#userGroupPopup .popupContents').scrollTop(0);
        comm.hideModal($('#userGroupPopup'));
        $('#userGroupPopup').hide();
    },
    addProc : () => {
        const formObj = $('#userGroupForm').serializeJSON();

        comm.ajaxPost({
                url : "/user/addUserGroupProc"
                , type : "PUT"
                , data : formObj
            },
            (result) => {
                comm.showAlert("사용자 그룹이 등록되었습니다");
                userGroup.create($('#userGroupTable'));
                userGroup.hidePopup();
            });
    },
    modProc : () => {
        const formObj = $('#userGroupForm').serializeJSON();

        comm.ajaxPost({
                url : "/user/modUserGroupProc"
                , type : "PATCH"
                , data : formObj
            },
            (result) => {
                comm.showAlert("사용자 그룹이 수정되었습니다");
                userGroup.create($('#userGroupTable'));
                userGroup.hidePopup();
            });
    },
    delProc : (pSeq) => {
        comm.ajaxPost({
                url : "/user/delUserGroupProc"
                , type : "DELETE"
                , data : {pSeq : pSeq}
            },
            (result) => {
                comm.showAlert("사용자 그룹이 삭제되었습니다");
                userGroup.create($('#userGroupTable'));
                userGroup.hidePopup();
            });
    }
}
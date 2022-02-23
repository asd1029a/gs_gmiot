/*
* 사용자 관련 JS
*/

const account = {
    user : {
        eventHandler : () => {
            $("#searchBtn").on('click', (e) => {
                account.user.create();
            });
            $("#addUserAccountBtn").on('click', () => {
                account.user.showPopup("add");
            });
            $("#addUserAccountProcBtn").on('click', () => {
                account.user.addProc();
            });
            $("#modUserAccountProcBtn").on('click', () => {
                account.user.modProc($("userAccountForm").data("userSeq"));
            });
            $("#delUserAccountProcBtn").on('click', () => {
                account.user.delProc($("userAccountForm").data("userSeq"));
            });
            $("#userAccountPopup .title dd").on('click', () => {
                account.user.hidePopup();
            });
        },
        create : () => {
            const $target = $('#userAccountTable');

            const optionObj = {
                dom: '<"table_body"rt><"table_bottom"p>',
                destroy: true,
                pageLength: 15,
                scrollY: "calc(100% - 40px)",
                ajax :
                    {
                        'url' : "/user",
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
                    }
                    , select: {
                    toggleable: false
                }
                , columns : [
                    {data: "userId", className: "alignLeft"},
                    {data: "userName"},
                    {data: "tel"},
                    {data: "email", className: "alignLeft"},
                    {data: "status"},
                    {data: "insertDt"},
                    {data: "lastLoginDt"},
                    {data: null}
                ]
                , "columnDefs": [{
                    "targets": -1,
                    "data": null,
                    "defaultContent": '<span class="button">상세보기</span>'
                }]
                , excelDownload : true
            }

            const evt = {
                click : function(e) {
                    const $form = $('#userAccountForm');
                    const rowData = $target.DataTable().row($(e.currentTarget)).data();
                    if($(e.target).hasClass('button')) {
                        account.user.showPopup('mod');
                        $('#userAccountForm').setItemValue(rowData);
                        account.user.get(rowData.userSeq ,(result) => {
                            $form.data("userSeq", rowData.userSeq);
                            $form.setItemValue(result);
                        });
                    }
                }
            }
            comm.createTable($target ,optionObj, evt);
        },
        getList : (pCallback) => {
            comm.ajaxPost({
                url : "/user"
                , data : {}
            }, (result) => {
                pCallback(result);
            });
        },
        get : (pSeq, pCallback) => {
            $.ajax({
                url : "/user/" + pSeq
                , type : "GET"
            }).done((result) => {
                pCallback(result);
            });
        },
        showPopup : (type) => {
            const $popup = $("#userAccountPopup");

            $('#userAccountPopup .popupContents').scrollTop(0);
            comm.showModal($('#userAccountPopup'));
            $popup.css("display", "flex");
            $('#userAccountForm').initForm();
            $('#userAccountPopup [data-mode]').hide();
            $popup.find("#userId").data("required", true).data("regex", "loginId");
            $popup.find("#password").data("required", true).data("regex", "sPassword");
            $popup.find("#userName").data("required", true).data("regex", "name");
            $popup.find("#tel").data("required", true).data("regex", "loginId");
            $popup.find("#email").data("required", true).data("regex", "email");
            if(type === "add") {
                $('#userAccountPopup .title dt').text("사용자 계정 등록");
                $('#userAccountPopup [data-mode="'+type+'"]').show();
            } else if(type === "mod") {
                $('#userAccountPopup .title dt').text('사용자 계정 수정');
                $('#userAccountPopup [data-mode="'+type+'"]').show();
            } else if(type === "detail") {
                $('#userAccountPopup .title dt').text("사용자 계정 상세");
                $('#userAccountPopup [data-mode="'+type+'"]').show();
            }
        },
        hidePopup : () => {
            $('#userAccountPopup .popupContents').scrollTop(0);
            comm.hideModal($('#userAccountPopup'));
            $('#userAccountPopup').hide();
        },
        addProc : () => {
            const formObj = $('#userAccountForm').serializeJSON();
            const $checkPassword = $("#checkPassword");

            if(formObj.password === $checkPassword.val()) {
                if($('#userAccountForm').doValidation()) {
                    $.ajax({
                        url: "/user"
                        , type: "PUT"
                        , contentType : "application/json; charset=utf-8"
                        , data : JSON.stringify(formObj)
                    }).done((result) => {
                        comm.showAlert("사용자 계정이 등록되었습니다");
                        account.user.create($('#userAccountTable'));
                        account.user.hidePopup();
                    });
                } else {
                    return false;
                }
            } else {
                $checkPassword.focus();
                comm.showAlert("비밀번호 확인이 일치하지 않습니다.")
            }
        },
        modProc : (pSeq) => {
            const formObj = $('#userAccountForm').serializeJSON();
            formObj.userSeq = pSeq;
            const $checkPassword = $("#checkPassword");

            if(formObj.password === $checkPassword.val()) {
                if(formObj.password === ""
                    && $checkPassword.val() === "") {
                    delete formObj.password;
                    $('#userAccountForm').find("#password").removeData("required", true).removeData("regex", "sPassword");
                }
            } else {
                $checkPassword.focus();
                comm.showAlert("비밀번호 확인이 일치하지 않습니다.");
                return false;
            }

            if($('#userAccountForm').doValidation()) {
                $.ajax({
                    url : "/user"
                    , type : "PATCH"
                    , contentType : "application/json; charset=utf-8"
                    , data : JSON.stringify(formObj)
                }).done((result) => {
                    comm.showAlert("사용자 계정이 수정되었습니다");
                    account.user.create($('#userAccountTable'));
                    account.user.hidePopup();
                });
            } else {
                return false;
            }
        },
        delProc : (pSeq) => {
            $.ajax({
                url : "/user/"+pSeq
                , type : "DELETE"
            }).done((result) => {
                comm.showAlert("사용자 계정이 삭제되었습니다");
                account.user.create($('#userAccountTable'));
                account.user.hidePopup();
            });
        }
    }
    , group : {
        eventHandler : () => {
            $("#searchBtn").on('click', (e) => {
                account.group.create();
            });
            $("#addUserGroupBtn").on('click', () => {
                account.group.showPopup("add");
            });
            $("#userGroupPopup .title dd").on('click', () => {
                account.group.hidePopup();
            });
            $("#addUserGroupProcBtn").on('click', () => {
                account.group.addProc();
            });
            $("#modUserGroupProcBtn").on('click', () => {
                account.group.modProc($("#userGroupForm").data("userGroupSeq"));
            });
            $("#delUserGroupProcBtn").on('click', () => {
                account.group.delProc($("#userGroupForm").data("userGroupSeq"));
            });
        },
        create : () => {
            const $target = $('#userAccountTable');

            const optionObj = {
                dom: '<"table_body"rt><"table_bottom"p>',
                destroy: true,
                pageLength: 15,
                scrollY: "calc(100% - 40px)",
                ajax :
                    {
                        'url' : "/user/group",
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
                    {data: "userGroupSeq", className: "alignLeft"},
                    {data: "groupName"},
                    {data: "groupName"},
                    {data: "groupDesc"},
                    {data: null}
                ]
                , "columnDefs": [{
                    "targets": -1,
                    "data": null,
                    "defaultContent": '<span class="button">상세보기</span>'
                }]
                , excelDownload : true
            }

            const evt = {
                click : function(e) {
                    const $form = $('#userGroupForm');
                    const rowData = $target.DataTable().row($(e.currentTarget)).data();
                    if($(e.target).hasClass('button')) {
                        account.group.showPopup('mod');
                        $('#userAccountForm').setItemValue(rowData);
                        account.group.get(rowData.userGroupSeq ,(result) => {
                            $form.data("userGroupSeq", rowData.userGroupSeq);
                            $form.setItemValue(result);
                        });
                    }

                }
            }
            comm.createTable($target ,optionObj, evt);
        },
        getList : (pCallback) => {
            comm.ajaxPost({
                url : "/user/group"
                , data : {}
            }, (result) => {
                pCallback(result);
            });
        },
        get : (pSeq, pCallback) => {
            $.ajax({
                url : "/user/group/" + pSeq
                , type : "GET"
            }).done((result) => {
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
                $('#userGroupPopup .title dt').text("사용자 그룹 등록");
                $('#userGroupPopup [data-mode="'+type+'"]').show();
            } else if(type === "mod") {
                $('#userGroupPopup .title dt').text('사용자 그룹 수정');
                $('#userGroupPopup [data-mode="'+type+'"]').show();
            } else if(type === "detail") {
                $('#userGroupPopup .title dt').text("사용자 그룹 상세");
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

            $.ajax({
                url : "/user/group"
                , type : "PUT"
                , data : JSON.stringify(formObj)
                , contentType : "application/json; charset=utf-8"
                , dataType : "json"
            }).done((result) => {
                comm.showAlert("사용자 그룹이 등록되었습니다");
                account.group.create($('#userGroupTable'));
                account.group.hidePopup();
            });
        },
        modProc : () => {
            const formObj = $('#userGroupForm').serializeJSON();

            $.ajax({
                url : "/user/group"
                , type : "PATCH"
                , data : JSON.stringify(formObj)
                , contentType : "application/json; charset=utf-8"
                , dataType : "json"
            }).done((result) => {
                comm.showAlert("사용자 그룹이 수정되었습니다");
                account.group.create($('#userGroupTable'));
                account.group.hidePopup();
            });
        },
        delProc : (pSeq) => {
            $.ajax({
                url : "/user/group/"+pSeq
                , type : "DELETE"
            }).done((result) => {
                comm.showAlert("사용자 그룹이 삭제되었습니다");
                account.group.create($('#userGroupTable'));
                account.group.hidePopup();
            });
        }
    }
}
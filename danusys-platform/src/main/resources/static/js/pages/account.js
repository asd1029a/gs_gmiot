/*
* 사용자 관련 JS
*/

const account = {
    user: {
        logout: () => {
            const domain = document.domain;
            document.cookie = 'accessToken' + '=; expires=Thu, 01 Jan 1999 00:00:10 GMT;domain=' + domain + ';path=/';
            document.location.href = "/";
        },
        eventHandler: () => {
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
                account.user.modProc($("#userAccountForm").data("userSeq"));
            });
            $("#delUserAccountProcBtn").on('click', () => {
                account.user.delProc($("#userAccountForm").data("userSeq"));
            });
            $("#userAccountPopup .title dd").on('click', () => {
                account.user.hidePopup();
            });
        },
        create: () => {
            const $target = $('#userAccountTable');

            const optionObj = {
                dom: '<"table_body"rt><"table_bottom"p>',
                destroy: true,
                pageLength: 15,
                scrollY: "calc(100% - 40px)",
                ajax:
                    {
                        'url': "/user/paging",
                        'contentType': "application/json; charset=utf-8",
                        'type': "POST",
                        'data': function (d) {
                            const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                            return JSON.stringify(param);
                        },
                        'dataSrc': function (result) {
                            $('.title dd .count').text(result.recordsTotal);
                            return result.data;
                        }
                    }
                , select: {
                    toggleable: false,
                    style: "single"
                }
                , columns: [
                    {data: "userId", className: "alignLeft"},
                    {data: "userName"},
                    {data: "tel"},
                    {data: "email", className: "alignLeft"},
                    {data: "userStatus.codeName"},
                    {data: "insertDt"},
                    {data: "lastLoginDt"},
                    {data: null}
                ]
                , "columnDefs": [{
                    "targets": -1,
                    "data": null,
                    "defaultContent": '<span class="button">상세보기</span>'
                }]
                , excelDownload: {
                    url : "/user/excel/download"
                    , fileName : "사용자 목록_"+ dateFunc.getCurrentDateYyyyMmDd(0, '') +".xlsx"
                    , search : $("#searchForm form").serializeJSON()
                    , headerList : ["고유번호|userSeq"
                        , "ID|userId"
                        , "이름|userName"
                        // , "사용여부|userStatus.codeName"
                        , "마지막 접속|lastLoginDt"
                        , "생성자|insertUserId"
                        , "생성일|insertDt"
                        , "수정자|updateUserId"
                        , "수정일|updateDt"]
                }
            }

            const evt = {
                click: function (e) {
                    const $form = $('#userAccountForm');
                    const rowData = $target.DataTable().row($(e.currentTarget)).data();
                    if ($(e.target).hasClass('button')) {
                        $('#userAccountForm').setItemValue(rowData);
                        account.user.get(rowData.userSeq, (result) => {
                            $form.data("userSeq", rowData.userSeq);
                            account.user.showPopup('mod');
                            $form.setItemValue(result);
                        });
                    }
                }
            }
            comm.createTable($target, optionObj, evt);
        },
        createUserInGroup: (type) => {
            const $target = $('#userInGroupTable');
            const url = type === "mod" ? "/user/userInGroup" : "/user";
            const optionObj = {
                dom: '<"table_body"rt>',
                destroy: true,
                bPaginate: false,
                bServerSide: false,
                scrollY: "calc(100% - 50px)",
                ajax:
                    {
                        'url': url,
                        'contentType': "application/json; charset=utf-8",
                        'type': "POST",
                        'data': function () {
                            return JSON.stringify({
                                userGroupSeq: $("#userGroupForm").data("userGroupSeq"),
                                status: [0,1] //삭제 된 사용자 제외
                            });
                        },
                    }
                ,
                select: {
                    toggleable: false,
                    style: "single"
                }
                , columns: [
                    {data: "userSeq", className: "alignLeft"},
                    {data: "userName"},
                    {data: "tel"},
                    {data: null}
                ]
                , columnDefs: [{
                    "targets": -1,
                    "data": null,
                    "defaultContent": '<span><input type="checkbox"/><label><span></span></label></span>'
                }]
                , fnCreatedRow: (nRow, aaData, iDataIndex) => {
                    const userSeq = aaData.userSeq;
                    $(nRow).find('input').prop('id', "check" + userSeq);
                    $(nRow).find('input').prop('value', userSeq);
                    $(nRow).find('label').prop('for', "check" + userSeq);
                    if (aaData.checked === "checked") {
                        $(nRow).find('input').prop('checked', true);
                    }
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
        },
        getList: (pCallback) => {
            comm.ajaxPost({
                url: "/user"
                , data: {}
            }, (result) => {
                pCallback(result);
            });
        },
        get: (pSeq, pCallback) => {
            $.ajax({
                url: "/user/" + pSeq
                , type: "GET"
            }).done((result) => {
                pCallback(result);
            });
        },
        checkId: (pId, pCallback) => {
            $.ajax({
                url: "/user/checkId/" + pId
                , type: "GET"
            }).done((result) => {
                pCallback(result);
            });
        },
        showPopup: (type) => {
            const $popup = $("#userAccountPopup");

            $('#userAccountPopup .popupContents').scrollTop(0);
            comm.showModal($('#userAccountPopup'));
            account.group.createUserInGroup(type);
            $('#userAccountForm').initForm();
            $('#userAccountPopup [data-mode]').hide();

            $popup.css("display", "flex");
            $popup.find("#userId").data("required", true).data("regex", "loginId");
            $popup.find("#password").data("required", true).data("regex", "sPassword");
            $popup.find("#userName").data("required", true).data("regex", "name");
            $popup.find("#tel").data("required", true).data("regex", "loginId");
            $popup.find("#email").data("required", true).data("regex", "email");
            $popup.find("#userId").prop("disabled", false);
            $("#checkIdBtn").remove();
            if (type === "add") {
                $('#userAccountPopup .title dt').text("사용자 계정 등록");
                $('#userAccountPopup [data-mode="' + type + '"]').show();
                $("#userId").parent().append('<span class="button" id="checkIdBtn">중복 확인</span>');
                $("#userId").on('change', (e) => {
                    $("#checkIdBtn").data("duplCheck", false);
                });
                $("#checkIdBtn").on('click', (e) => {
                    const userId = $("#userId").val();
                    if (stringFunc.validRegex(userId, "loginId")) {
                        account.user.checkId(userId, (result) => {
                            if (result === 0) {
                                comm.showAlert("사용중인 아이디입니다.<br/> 다른 아이디를 입력해주십시오.");
                                $(e.currentTarget).data("duplCheck", false);
                            } else {
                                comm.showAlert("사용가능한 아이디입니다.");
                                $(e.currentTarget).data("duplCheck", true);
                            }
                        });
                    } else {
                        comm.showAlert("[ID] <br/> * 형식에 맞지 않는 문자 * </br> 다시 입력해 주십시오.");
                        $(e.currentTarget).data("duplCheck", false);
                    }
                });
            } else if (type === "mod") {
                $('#userAccountPopup .title dt').text('사용자 계정 수정');
                $popup.find("#userId").removeData("required").removeData("regex");
                $popup.find("#userId").prop("disabled", true);
                $('#userAccountPopup [data-mode="' + type + '"]').show();
            }
        },
        hidePopup: () => {
            $('#userAccountPopup .popupContents').scrollTop(0);
            comm.hideModal($('#userAccountPopup'));
            $('#userAccountPopup').hide();
        },
        addProc: () => {
            const formObj = $('#userAccountForm').serializeJSON();
            const $checkPassword = $("#checkPassword");
            formObj.userGroupSeqList = [];
            $('#userInGroupTable tbody tr').each((i, e) => {
                if ($(e).find("input").prop('checked')) {
                    formObj.userGroupSeqList.push(Number($(e).find("input").val()));
                }
            })

            if (formObj.password === $checkPassword.val() && $("#checkIdBtn").data("duplCheck") === true) {
                if ($('#userAccountForm').doValidation()) {
                    $.ajax({
                        url: "/user"
                        , type: "PUT"
                        , contentType: "application/json; charset=utf-8"
                        , data: JSON.stringify(formObj)
                    }).done((result) => {
                        comm.showAlert("사용자 계정이 등록되었습니다");
                        account.user.create($('#userAccountTable'));
                        account.user.hidePopup();
                    });
                } else {
                    return false;
                }
            } else if (formObj.password !== $checkPassword.val()) {
                $checkPassword.focus();
                comm.showAlert("비밀번호 확인이 일치하지 않습니다.");
            } else if ($("#checkIdBtn").data("duplCheck") === false) {
                comm.showAlert("아이디 중복확인이 필요합니다.");
            }
        },
        modProc: (pSeq) => {
            const formObj = $('#userAccountForm').serializeJSON();
            const $checkPassword = $("#checkPassword");
            formObj.userSeq = pSeq;
            formObj.userSeqList = [pSeq];
            formObj.userGroupSeqList = [];
            $('#userInGroupTable tbody tr').each((i, e) => {
                if ($(e).find("input").prop('checked')) {
                    formObj.userGroupSeqList.push(Number($(e).find("input").val()));
                }
            })
            if (formObj.password === $checkPassword.val()) {
                if (formObj.password === ""
                    && $checkPassword.val() === "") {
                    delete formObj.password;
                    $('#userAccountForm').find("#password").removeData("required", true).removeData("regex", "sPassword");
                }
            } else {
                $checkPassword.focus();
                comm.showAlert("비밀번호 확인이 일치하지 않습니다.");
                return false;
            }

            if ($('#userAccountForm').doValidation()) {
                $.ajax({
                    url: "/user"
                    , type: "PATCH"
                    , contentType: "application/json; charset=utf-8"
                    , data: JSON.stringify(formObj)
                }).done((result) => {
                    comm.showAlert("사용자 계정이 수정되었습니다");
                    account.user.create($('#userAccountTable'));
                    account.user.hidePopup();
                });
            } else {
                return false;
            }
        },
        delProc: (pSeq) => {
            $.ajax({
                url: "/user/" + pSeq
                , type: "DELETE"
            }).done((result) => {
                comm.showAlert("사용자 계정이 삭제되었습니다");
                account.user.create($('#userAccountTable'));
                account.user.hidePopup();
            });
        }
    }
    , group: {
        eventHandler: () => {
            $("#searchBtn").on('click', (e) => {
                account.group.create();
            });
            $("#addUserGroupBtn").on('click', () => {
                $('#userGroupForm').data("userGroupSeq", "");
                account.group.showPopup("add");
            });
            $("#userGroupPopup .title dd").on('click', () => {
                account.group.hidePopup();
            });
            $("#addUserGroupProcBtn").on('click', () => {
                const groupName = $("#groupName").val();

                if (groupName === ""){
                    comm.showAlert("그룹 이름을 입력하세요.");
                } else{
                    account.group.checkGroupName(
                        groupName
                        , (result) => {
                            if(result === 1) {
                                account.group.addProc();
                            } else {
                                $("#groupName").focus();
                                comm.showAlert("중복된 그룹 이름이 존재합니다.");
                            }
                        }
                    );
                }
            });
            $("#modUserGroupProcBtn").on('click', () => {
                const groupName = $("#groupName").val();

                if (groupName === ""){
                    comm.showAlert("그룹 이름을 입력하세요.");
                } else{
                    account.group.modProc($("#userGroupForm").data("userGroupSeq"));
                }
            });
            $("#delUserGroupProcBtn").on('click', () => {
                account.group.delProc($("#userGroupForm").data("userGroupSeq"));
            });
        },
        create: () => {
            const $target = $('#userAccountTable');

            const optionObj = {
                dom: '<"table_body"rt><"table_bottom"p>',
                destroy: true,
                pageLength: 15,
                scrollY: "calc(100% - 40px)",
                ajax:
                    {
                        'url': "/userGroup/paging",
                        'contentType': "application/json; charset=utf-8",
                        'type': "POST",
                        'data': function (d) {
                            const param = $.extend({}, d, $("#searchForm form").serializeJSON());
                            return JSON.stringify(param);
                        },
                        'dataSrc': function (result) {
                            $('.title dd .count').text(result.recordsTotal);
                            return result.data;
                        }
                    },
                select: {
                    toggleable: false,
                    style: "single"
                },
                columns: [
                    {data: "groupName"},
                    {data: "groupDesc"},
                    {data: "inUserId"},
                    {data: null}
                ]
                , "columnDefs": [{
                    "targets": -1,
                    "data": null,
                    "defaultContent": '<span class="button">상세보기</span>'
                }]
                , excelDownload: {
                    url : "/userGroup/excel/download"
                    , fileName : "사용자 그룹 목록_"+ dateFunc.getCurrentDateYyyyMmDd(0, '') +".xlsx"
                    , search : $("#searchForm form").serializeJSON()
                    , headerList : ["고유번호|userGroupSeq"
                        , "이름|groupName"
                        , "설명|groupDesc"
                        // , "사용여부|user"
                        , "생성자|insertUserId"
                        , "생성일|insertDt"
                        , "수정자|updateUserId"
                        , "수정일|updateDt"]
                }
            }

            const evt = {
                click: function (e) {
                    const $form = $('#userGroupForm');
                    const rowData = $target.DataTable().row($(e.currentTarget)).data();
                    if ($(e.target).hasClass('button')) {
                        account.group.get(rowData.userGroupSeq, (result) => {
                            $form.data("userGroupSeq", rowData.userGroupSeq);
                            account.group.showPopup('mod');
                            account.group.setPermitItemValue(result);
                            $form.setItemValue(result);
                        });
                    }
                }
            }
            comm.createTable($target, optionObj, evt);
        },
        createUserInGroup: (type) => {
            const $target = $('#userInGroupTable');

            const url = type === "mod" ? "/userGroup/userInGroup" : "/userGroup"

            const optionObj = {
                dom: '<"table_body"rt>',
                destroy: true,
                bPaginate: false,
                bServerSide: false,
                scrollY: "calc(100% - 40px)",
                ajax:
                    {
                        'url': url,
                        'contentType': "application/json; charset=utf-8",
                        'type': "POST",
                        'data': function () {
                            return JSON.stringify({
                                userSeq: $("#userAccountForm").data("userSeq")
                            });
                        },
                    },
                select: {
                    toggleable: false,
                    style: "single"
                },
                columns: [
                    {data: "userGroupSeq", className: "alignLeft"},
                    {data: "groupName"},
                    {data: "groupDesc"},
                    {data: null}
                ]
                , columnDefs: [{
                    "targets": -1,
                    "data": null,
                    "defaultContent": '<span><input type="checkbox"/><label><span></span></label></span>'
                }]
                ,
                fnCreatedRow: (nRow, aaData, iDataIndex) => {
                    const userGroupSeq = aaData.userGroupSeq;
                    $(nRow).find('input').prop('id', "check" + userGroupSeq);
                    $(nRow).find('input').prop('value', userGroupSeq);
                    $(nRow).find('label').prop('for', "check" + userGroupSeq);
                    if (aaData.checked === "checked") {
                        $(nRow).find('input').prop('checked', true);
                    }
                }
                , excelDownload: false
            }

            const evt = {
                click: function (e) {
                    const rowData = $target.DataTable().row($(e.currentTarget)).data();
                }
            }
            comm.createTable($target, optionObj, evt);
        },
        setPermitItemValue : (pData) => {
            const $permitTable = $("#permitTable");
            if(pData.userGroupPermit.length !== 0) {
                $permitTable.find("tbody tr").each((index, item) => {
                    const permitName = pData.userGroupPermit[index].permitMenu['codeId'];
                    const permitValue = pData.userGroupPermit[index].permit['codeValue'];
                    $permitTable.find('span [name="'+permitName+'"][ data-value="'+permitValue+'"]').prop("checked", true);
                });
            }
        },
        checkGroupName: (pId, pCallback) => {
            $.ajax({
                url: "/userGroup/checkGroupName/" + pId
                , type: "GET"
            }).done((result) => {
                pCallback(result);
            });
        },
        getList: (pCallback) => {
            comm.ajaxPost({
                url: "/userGroup"
                , data: {}
            }, (result) => {
                pCallback(result);
            });
        },
        get: (pSeq, pCallback) => {
            $.ajax({
                url: "/userGroup/" + pSeq
                , type: "GET"
            }).done((result) => {
                pCallback(result);
            });
        },
        showPopup: (type) => {
            $('#userGroupPopup .popupContents').scrollTop(0);
            $("#permitTable").find('input').prop('checked', false);
            comm.showModal($('#userGroupPopup'));
            account.user.createUserInGroup(type);
            $('#userGroupPopup').css("display", "flex");
            $('#userGroupForm').initForm();
            $('#userGroupPopup [data-mode]').hide();
            if (type === "add") {
                $('#userGroupPopup .title dt').text("사용자 그룹 등록");
                $('#userGroupPopup [data-mode="' + type + '"]').show();
            } else if (type === "mod") {
                $('#userGroupPopup .title dt').text('사용자 그룹 수정');
                $('#userGroupPopup [data-mode="' + type + '"]').show();
            } else if (type === "detail") {
                $('#userGroupPopup .title dt').text("사용자 그룹 상세");
                $('#userGroupPopup [data-mode="' + type + '"]').show();
            }
        },
        hidePopup: () => {
            $('#userGroupPopup .popupContents').scrollTop(0);
            comm.hideModal($('#userGroupPopup'));
            $('#userGroupPopup').hide();
        },
        addProc: () => {
            const formObj = $('#userGroupForm').serializeJSON();

            formObj.userSeqList = [];
            $('#userInGroupTable tbody tr').each((i, e) => {
                if ($(e).find("input").prop('checked')) {
                    formObj.userSeqList.push(Number($(e).find("input").val()));
                }
            });

            let permit = {};
            let isEmptyPermit = false;
            $('#permitTable tbody tr').each((i, element) => {
                let name = $(element).find("input").eq(0).attr("name");
                permit[name] = $("[name=" + name + "]:checked").data("value");
                if(!permit[name] === undefined){
                    isEmptyPermit = true;
                    return 0;
                }
            });
            formObj.permitList = permit;

            if(isEmptyPermit){
                comm.showAlert("권한을 모두 체크하세요.");
            } else if(!$("[name=userGroupStatus]:checked").data("value") === undefined) {
                comm.showAlert("사용 여부를 체크하세요.");
            } else{
                $.ajax({
                    url: "/userGroup"
                    , type: "PUT"
                    , data: JSON.stringify(formObj)
                    , contentType: "application/json; charset=utf-8"
                    , dataType: "json"
                }).done((result) => {
                    comm.showAlert("사용자 그룹이 등록되었습니다");
                    account.group.create($('#userGroupTable'));
                    account.group.hidePopup();
                });
            }
        },
        modProc: (pSeq) => {
            const formObj = $('#userGroupForm').serializeJSON();

            formObj.userGroupSeq = pSeq;
            formObj.userGroupSeqList = [pSeq];
            formObj.userSeqList = [];
            $('#userInGroupTable tbody tr').each((i, e) => {
                if ($(e).find("input").prop('checked')) {
                    formObj.userSeqList.push(Number($(e).find("input").val()));
                }
            });

            let permit = {};
            let isEmptyPermit = false;
            $('#permitTable tbody tr').each((i, element) => {
                let name = $(element).find("input").eq(0).attr("name");
                permit[name] = $("[name=" + name + "]:checked").data("value");
                if(!permit[name] === undefined){
                    isEmptyPermit = true;
                    return 0;
                }
            });
            formObj.permitList = permit;

            if(isEmptyPermit){
                comm.showAlert("권한을 모두 체크하세요.");
            } else if($("[name=userGroupStatus]:checked").data("value") === undefined) {
                comm.showAlert("사용 여부를 체크하세요.");
            } else{
                $.ajax({
                    url: "/userGroup"
                    , type: "PATCH"
                    , data: JSON.stringify(formObj)
                    , contentType: "application/json; charset=utf-8"
                    , dataType: "json"
                }).done((result) => {
                    comm.showAlert("사용자 그룹이 수정되었습니다");
                    account.group.create($('#userGroupTable'));
                    account.group.hidePopup();
                });
            }
        },
        delProc: (pSeq) => {
            $.ajax({
                url: "/userGroup/" + pSeq
                , type: "DELETE"
            }).done((result) => {
                comm.showAlert("사용자 그룹이 삭제되었습니다");
                account.group.create($('#userGroupTable'));
                account.group.hidePopup();
            });
        }
    }
}
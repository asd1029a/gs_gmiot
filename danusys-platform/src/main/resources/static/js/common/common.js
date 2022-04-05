/*
 *  JQuery 확장 공통 정의 함수
 */
$.fn.extend({
    /**
     * Modal
     * @param flag (true , false)
     */
    modal: function(flag) {
        var curThis = this;

        if (flag) {
            curThis.show();
        } else {
            curThis.hide();
        }
    },
    serializeJSON: function() {
        var obj = {};
        try {
            var curThis = this;

            if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
                const arr = this.serializeArray();
                if (arr) {
                    jQuery.each(arr, function() {
                        obj[this.name] = this.value;
                    });
                }
                $.each(this[0], function(idx, element) {
                    if (element.tagName == "SELECT" && $(element).attr("multiple") == "multiple") {
                        obj[$(element).attr("id")] = $(element).selectpicker("val");
                    } else if (element.type == "radio" && $(element).prop('checked') === true) {
                        obj[$(element).attr("name")] = $(element).data('value');
                    } else if(element.type == "checkbox" && $(element).prop('checked') === true){
                        let name = $(element).attr("name");
                        let val = $(element).data('value');

                        if(val !== "all"){
                            // 기본으로 위의 포문으로 값이 들어있어서 검증해야 함
                            Array.isArray(obj[name])
                                ? obj[name].push(val)
                                : obj[name] = Array.of(val);
                        }
                    }
                });
            }
        } catch (e) {
            console.log("serializeJSON Func Error : " + e.message);
        } finally {}
        return obj;
    },
    doRequired: function() {
        var curThis = this,
            returnVal = true,
            txtLabel = "";

        $.each(curThis[0], function(idx, element) {
            if ($(element).attr("required") == "required") {
                //$(element).parents(".form-group").find("label .label_span").empty();
                //$(element).parents(".form-group").find("label .label_span").append("<i class='fas fa-check mr-2'></i>");

                if (element.tagName == "INPUT" || element.tagName == "TEXTAREA") {
                    $(element).prev("label").find(".label_span").empty();
                    //$(element).prev("label").find(".label_span").append("<i class='fas fa-check mr-2'></i>");
                    $(element).prev("label").find(".label_span").append("● ").css("color", "purple");
                } else if (element.tagName == "SELECT") {
                    $(element).parent().parent().find(".label_span").empty();
                    //$(element).parent().parent().find(".label_span").append("<i class='fas fa-check mr-2'></i>");
                    $(element).parent().parent().find(".label_span").append("● ").css("color", "purple");
                }
            }
        });
    },
    doSearchValidation: function() {
        var curThis = this;
        var result = true;

        $.each(curThis[0], function(idx, element) {
            if ($(element).attr("id") == "startDt") {
                var startDtDateArr = $(element).val().split("-");
                var startDtDate = new Date(startDtDateArr[0], Number(startDtDateArr[1]) - 1, startDtDateArr[2]);

                var endDtDateArr = $("#searchForm").find("#endDt").val().split("-");
                var endDtDate = new Date(endDtDateArr[0], Number(endDtDateArr[1]) - 1, endDtDateArr[2]);
                var betweenDay = (startDtDate.getTime() - endDtDate.getTime()) / 1000 / 60 / 60 / 24;

                if (betweenDay > 0) {
                    comm.showAlert("종료날짜보다 시작날짜가 더 큽니다.");
                    $(element).val($("#searchForm").find("#endDt").val());
                    result = false;
                }
            } else if ($(element).attr("id") == "searchFullKeyword") {
                var searchKeyword = $.trim($(element).val());
                var regex = /^[A-Za-z가-헿0-9_\s]+$/;

                if (searchKeyword.length > 0) {
                    if (!searchKeyword.match(regex)) {
                        $(element).val("");
                        comm.showAlert("적합하지 않은 문자가 포함되었습니다.");
                        result = false;
                    }
                } else {
                    $(element).val("");
                }
            }
        });
        return result;
    },
    doValidation: function() {
        var curThis = this,
            returnVal = true,
            txtLabel = "";
        var validType = 1;
        var $labelElement;

        $.each(curThis[0], function(idx, element) {
            $labelElement = curThis.find("label[for=" + ($(element).attr("id")) + "]");

            if (typeof($labelElement.attr("data-hidden-value")) != "undefined") {
                txtLabel = $labelElement.attr("data-hidden-value");
            } else if ($labelElement.length === 0) {
                txtLabel = $(element).parent().parent().find('dt').text();
            } else {
                txtLabel = $labelElement.text();
            }

            if (typeof(txtLabel) != "undefined") {
                txtLabel = txtLabel.replace(" :", "");
                txtLabel = txtLabel.replace("* ", "");
            }

            if (!$(element).is(":disabled")) {
                if ($(element).data("required") &&
                    ((element.tagName === "INPUT" && $.trim($(element).val()) === "") ||
                        (element.tagName === "SELECT" && ($(element).val() == null || $.trim($(element).val()) === "")))) {
                    $(element).focus();
                    validType = 1;
                    returnVal = false;
                    return false;

                } else if ($(element).val() !== "" && typeof($(element).data("regex")) !== "undefined") {
                    if (!stringFunc.validRegex($(element).val(), $(element).data("regex"))) {
                        $(element).focus();
                        validType = 2;
                        returnVal = false;
                        return false;
                    }
                }
            }
        });

        if (!returnVal) {
            let resultStr = "";
            resultStr = "[" + txtLabel + "]";
            resultStr += "<br />";

            if (validType === 1) {
                resultStr += "* 필수항목 미입력 *";
            } else {
                resultStr += "* 형식에 맞지 않는 문자 *";
            }
            resultStr += "<br />다시 입력해 주십시오.";
            comm.showAlert(resultStr);
        }
        return returnVal;
    },
    initForm: function() {
        let isCheckboxInit = false;
        $.each($(this)[0], function(idx, element) {
            let objEle = $(element);
            let eleAttr = objEle[0];

            if (eleAttr !== "undefined" && typeof(eleAttr) != 'undefined') {
                if (typeof(objEle.attr("data-init-value")) != "undefined") {
                    if (eleAttr.type === "checkbox") {
                        isCheckboxInit = true;
                        objEle.prop("checked", objEle.attr("data-init-value"));
                        if ($(".dropdown_checkbox").length > 0)
                            comm.createMultiSelectBox.prototype.listSelect(objEle.parent());
                    } else {
                        objEle.val(objEle.attr("data-init-value"));
                    }
                } else if (eleAttr.tagName === "INPUT" || eleAttr.tagName === "TEXTAREA") {
                    if (eleAttr.type === "checkbox" && !isCheckboxInit) {
                        objEle.prop("checked", false);
                        if ($(".dropdown_checkbox").length > 0)
                            comm.createMultiSelectBox.prototype.listSelect(objEle.parent());
                    } else {
                        objEle.val("");
                    }

                    if (typeof(objEle.attr("data-diff-month")) !== "undefined" &&
                        objEle.attr("data-diff-month").length > 0) {
                        const diff = objEle.data("diff-month");
                        objEle.datepicker('setDate', dayjs().add(diff, 'month').$d);
                    }
                } else if (eleAttr.tagName === "SELECT") {
                    var firstVal = objEle.find("option:eq(0)").val();
                    objEle.val(firstVal);
                }
            }
        });
    },
    setItemValue: function(objValue) {
        const $curThis = $(this);
        let strValue = "";
        let arrInput = [];

        arrInput.push($curThis.find("input"));
        arrInput.push($curThis.find("textarea"));

        $.each(arrInput, function(idx, value) {
            $.each(value, function(idx, element) {
                if (element.type === "radio") {
                    strValue = objValue[element.name];
                } else {
                    strValue = objValue[element.id];
                }
                strValue = stringFunc.changeXSSOutputValue(strValue);
                if (strValue === undefined) {
                    return;
                }

                if (element.type === "checkbox") {
                    element.checked = (strValue === 'Y') ? true : false;
                } else if (element.type === "radio") {
                    if ($(element).data('value') === strValue) element.checked = "checked";
                } else {
                    if ($(element).attr("data-set") !== "false") {
                        element.value = strValue;
                    }
                }
            });
        });
    },
});

const comm = {
    /**
     * 테이블 로우 하이라이트
     */
    rowHighlight: (keyword, td, cellData) => {
        if ((keyword != "") && (cellData.indexOf(keyword) !== -1)) {
            const regex = new RegExp(keyword, 'gi');
            $(td).html(cellData.replace(regex, "<b class='highlight'>" + keyword + "</b>"));
        }
    }
    /**
     * Datatable extend row 생성
     */
    /*, format : function(data, row) {
        var result = '<table class="child_table" cellpadding="5" cellspacing="0" border="0">';
        if(data.eventKind=="비상벨"){
            delete row.userName;
            delete row.tel;
            delete row.sex;
            delete row.protectorAddress;
            delete row.protectorUserName;
            delete row.protectorTel;
        } else {
            row = {"address":"주소","userName":"사용자","sex":"성별","tel":"전화번호","protectorUserName":"보호자명","protectorTel":"보호자 전화번호","protectorAddress":"보호자 주소"}
        }
        $.each(row, function(key, value){
            result += '<tr>';
            result += '<td>'+value+' : </td>';
            if(data[key]==null){
                result += '<td> - </td>';
            } else {
                result += '<td>'+data[key]+'</td>';
            }
            result += '</tr>';
        })
        result += '</table>';
        return result;
    }*/
    , checkAuthority : (url, authName, permit) => {
    return new Promise((resolve, reject) => {
        $.ajax({
                   url : url + "/" + authName + "/"+ permit
                   , type : "GET"
                   , success : (result) => {resolve(result)}
                   , error : (xhr) => {reject(new Error("권한이 존재하지 않거나, 세션이 만료되었습니다."));}
            });
        });
    },
    createTable: ($target, optionObj, evt) => {
        //comm.showLoading();

        const defaultObj = {
            dom: '<"tableBody"rt><"tableBottom"p>',
            pageLength: 15,
            pagingType: "full_numbers",
            bPaginate: true,
            bLengthChange: false,
            responsive: true,
            bAutoWidth: false,
            processing: false,
            ordering: false,
            bServerSide: true,
            searching: false,
            select: true,
            scrollY: "calc(100% - 6px)",
            language: {
                emptyTable: "데이터가 없습니다.",
                zeroRecords: "검색된 데이터가 없습니다.",
                paginate: {
                    first: '<span><img src="/images/default/first.svg"></span>',
                    previous: '<span><img src="/images/default/prev.svg"></span>',
                    next: '<span><img src="/images/default/next.svg"></span>',
                    last: '<span><img src="/images/default/last.svg"></span>'
                }
            },
            excelDownload: {
                url: "",
                fileName: "",
                search: {}
            }
        }
        const newOptionObj = $.extend({}, defaultObj, optionObj);

        newOptionObj.ajax.error = (xhr, error, code) => {
            comm.showAlert("데이터를 불러오는데 실패했습니다.");
            console.error("서버에 오류가 발생했습니다 : " + code);
        }
        if (!$.isEmptyObject(evt)) {
            if (typeof evt.click !== "undefined") {
                $target.off('click');
                $target.on('click', 'tr', evt.click);
            }
            if (typeof evt.dblclick !== "undefined") {
                $target.off('dblclick');
                $target.on('dblclick', 'tr', evt.dblclick);
            }
            if (typeof evt.keyup !== "undefined") {
                evt.keyup();
            }
        }
        $.fn.DataTable.ext.pager.numbers_length = 10;
        $target.DataTable(newOptionObj);

        if (typeof newOptionObj.excelDownload !== "undefined") {
            if (newOptionObj.excelDownload && newOptionObj.excelDownload.url !== "") {
                const html = ' <p class="button excelDownloadBtn"><i><img src="/images/default/excel.svg"></i>엑셀로 내보내기</p>';
                $target.parents('.table_body').siblings('.table_bottom').append(html);
                $('.excelDownloadBtn').on('click', (e) => {
                    comm.downloadExcelFile(newOptionObj.excelDownload);
                });
            }
        }

        //comm.hideLoading();
    },
    showAlert: function(title, option) {
        var defaultOption = {
            title: title,
            text: '',
            icon: 'info',
            confirmButtonText: '확인',
            heightAuto: false
        }
        var newObj = $.extend(defaultOption, typeof(option) == "undefined" ? {} : option);

        Swal.fire(newObj);
    },
    confirm: function(title, option, fnProc, cancleProc) {
        var defaultOption = {
            icon: 'warning',
            title: title,
            text: '',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '확인',
            cancelButtonText: '취소',
            heightAuto: false
        }
        var newObj = $.extend(defaultOption, typeof(option) == "undefined" ? {} : option);
        swal.fire(newObj).then((result) => {
            if (result.value) {
                fnProc();
            } else {
                cancleProc();
            }
        });
    },
    toastOpen: function(mainObj, callback) {
        toastr.options = {
            "closeButton": true,
            "timeOut": 0,
            "extendedTimeOut": 0,
            "positionClass": "toast-top-full-width",
            "onclick": callback
        }
        //toastr.options.onclick = callback;
        var newObj = $.extend(toastr.options, typeof(mainObj.options) == "undefined" ? {} : mainObj.options);
        toastr.options = newObj;
        mainObj.options = {
            'maxToast': 5
        };

        if (typeof mainObj.options.maxToast != 'undefined') {
            toastr.subscribe(function(args) {
                if (args.state === 'visible') {
                    var toasts = $("#toast-container > *:not([hidden])");
                    if (toasts && toasts.length > mainObj.options.maxToast) {
                        toasts[toasts.length - 1].remove();
                    }
                }
            });
        }
        toastr[mainObj.type ? mainObj.type : "error"](mainObj.content, mainObj.title);
    },
    initModal: ($target) => {
        const html = '<div id="modal-background" style="display:none; position:absolute; background-color:#0000005e; width:100%; height:100%; z-index:1000"><div>'
        $('body > div:nth-child(1)').append(html);
    },
    showModal: function($popupEle) {
        $popupEle.css('z-index', '1010');
        $('#modal-background').show();
    },
    hideModal: function($popupEle) {
        $popupEle.css('z-index', 'unset');
        $('#modal-background').hide();
    },
    showLoading: function() {
        var newObj = {
            text: "<b>처리중입니다.</b>",
            color: "#fff",
            animation: "circle"
        }
        $("body").loadingModal({
            text: newObj.text,
            opacity: "0",
            color: newObj.color,
            animation: newObj.animation
        });
        $("body").loadingModal("show");
    },
    hideLoading: function() {
        $("body").loadingModal("hide");
    },
    showTooltip: function(target, option) {
        var defaultOption = {
            show: {
                effect: "fade",
                duration: 100
            },
            hide: {
                effect: "fade",
                duration: 100
            },
            content: function() {
                return $(this).prop('title');
            }

        };
        var newOption = $.extend(defaultOption, option);
        $(target).tooltip(newOption);
    },
    audioPlay: function(src) {
        var audioId = "audio" + Math.floor(Math.random() * 1000);
        var audioHtml = "<audio id='" + audioId + "' src='" + src + "' autoplay></audio>";

        $("body").append(audioHtml);

        setTimeout(function() {
            $("#" + audioId).remove();
        }, 3000);
    }
    /*, downloadExcelFile : function(paramObj) {
        let html = "";
        html += "<form id='downloadForm' method='post' action='"+paramObj.url+"'>";
        html += "</form>";
        $("body").append(html);

        $("#downloadForm").submit();

        setTimeout(function() {
            $("#downloadForm").remove();
        }, 2000);
    }*/
    ,
    downloadExcelFile: function(paramObj) {
        const body = {};
        if (typeof paramObj.search !== "undefined") {
            body.search = paramObj.search;
        }
        if (typeof paramObj.headerList !== "undefined") {
            body.headerList = paramObj.headerList;
        }

        fetch(paramObj.url, {
            method: 'POST',
            body: JSON.stringify(body),
            headers: {
                'Content-Type': 'application/json'
                // 'Content-Type': 'application/x-www-form-urlencoded',
            },
        })
            .then((res) => {
                if (res.status !== 200) {
                    comm.showAlert("엑셀다운로드에 실패했습니다.");
                    return false;
                }
                return res.blob();
            })
            .then((data) => {
                const a = document.createElement("a");
                a.href = window.URL.createObjectURL(data);
                a.download = paramObj.fileName;
                a.click();
                a.remove();
            })
            .catch(error => console.error('Error : ', error));
    }
    ,
    /* 커스텀 단일 선택 셀렉트 박스 / 이유나 2022.01.14 */
    createSelectBox: function(selector) {
        this.createSelectBox.$selectBox = null,
            this.createSelectBox.$select = null,
            this.createSelectBox.$list = null,
            this.createSelectBox.$listLi = null;

        comm.createSelectBox.prototype.init = function(selector) {
            this.$selectBox = $(selector);
            this.$select = this.$selectBox.find('.box .select_title');
            this.$list = this.$selectBox.find('.box .list');
            this.$listLi = this.$list.children('li');
        }
        comm.createSelectBox.prototype.initEvent = function(e) {
            var that = this;
            this.$select.on('click', function(e) {
                that.listOn();
            });
            this.$listLi.on('click', function(e) {
                that.listSelect($(this));
            });
            $(document).on('click', function(e) {
                that.listOff($(e.target));
            });
        }
        comm.createSelectBox.prototype.listOn = function() {
            this.$selectBox.toggleClass('on');
            if (this.$selectBox.hasClass('on')) {
                this.$list.css('display', 'block');
            } else {
                this.$list.css('display', 'none');
            };
        }
        comm.createSelectBox.prototype.listSelect = function($target) {
            $target.addClass('selected').siblings('li').removeClass('selected');
            this.$selectBox.removeClass('on');
            this.$select.text($target.text());
            this.$list.css('display', 'none');
        }
        comm.createSelectBox.prototype.listOff = function($target) {
            if (!$target.is(this.$select) && this.$selectBox.hasClass('on')) {
                this.$selectBox.removeClass('on');
                this.$list.css('display', 'none');
            };
        }
        this.createSelectBox.prototype.init(selector);
        this.createSelectBox.prototype.initEvent();
    }
    ,
    /* 커스텀 다중 선택 셀렉트 박스 / 이유나 2022.01.14 */
    createMultiSelectBox: function(selector) {
        this.createMultiSelectBox.$selectBox = null,
            this.createMultiSelectBox.$select = null,
            this.createMultiSelectBox.$list = null,
            this.createMultiSelectBox.$listLi = null;

        comm.createMultiSelectBox.prototype.init = function(selector) {
            this.$selectBox = $(selector);
            this.$select = this.$selectBox.find('.select_title');
            this.$list = this.$selectBox.find('.list');
            this.$listLi = this.$list.find('span');

            const type = this.$selectBox.data("selectboxType");
            const pObj = {
                draw: null,
                type: type
            }
            commonCode.getList(pObj, this.setList);
        }

        comm.createMultiSelectBox.prototype.setList = function(datas) {
            if (datas.data.length != 0) {

                let tData = datas.data[0];
                let data = datas.data.slice(1);

                comm.createMultiSelectBox.prototype.$select.html(tData.codeName);
                comm.createMultiSelectBox.prototype.$select.data("placeholder", tData.codeName);

                let codeValue = stringFunc.camelize(tData.codeValue);
                let listEle =
                    `<span class="checked_all">
                        <input type="checkbox" class="checkAll" id="${tData.codeSeq}All" name="${codeValue}" data-value="all">
                        <label for="${tData.codeSeq}All"><span></span>전체</label>
                    </span>`;

                data.forEach((item, idx) => {
                    let spanEle =
                        `<span>
                            <input type="checkbox" id="${item.codeSeq}" name="${codeValue}" data-value="${item.codeSeq}">
                            <label for="${item.codeSeq}"><span></span>${item.codeName}</label>
                        </span>`;

                    listEle += spanEle;
                })
                comm.createMultiSelectBox.prototype.$list.html(listEle);
            }
        }

        comm.createMultiSelectBox.prototype.initEvent = function() {
            let that = this;
            this.$listLi = this.$list.find('span');

            this.$select.on('click', function(e) {
                that.listOn($(this));
            });
            this.$listLi.on('click', function(e) {
                that.listSelect($(this));
            });
            $(document).on('click', function(e) {
                that.listOff($(e.target));
            });
        }
        comm.createMultiSelectBox.prototype.listOn = function($target) {
            let $box = $($target).parent();

            this.listOff($target);

            $box.toggleClass('selected_box');
            if ($box.hasClass('selected_box')) {
                $box.find(".list").css('display', 'grid');
            } else {
                $box.find(".list").css('display', 'none');
            }
        }
        comm.createMultiSelectBox.prototype.listSelect = function($target) {
            $target.toggleClass('selected');

            let $targetType = $target.parents(".dropdown_checkbox").data("selectboxType");

            let selectStr = "";
            let $targetInput = $target.children("input");

            let $targetSelectBox = $("[data-selectbox-type=" + $targetType + "]");
            let $targetSelect = $targetSelectBox.find(".select_title");
            let $targetListLi = $targetSelectBox.find(".list > span");

            if ($targetInput.hasClass("checkAll")) {
                selectStr = $targetInput.prop('checked') ? $target.text() : $targetSelect.data("placeholder");
                $targetListLi.children("input[type=checkbox]").prop('checked', $targetInput.prop('checked'));
            } else {
                let totalLen = $targetListLi.length - 1;
                let checkedLen = $targetListLi.find("input[type=checkbox]:checked").not(".checkAll").length;

                if (totalLen !== checkedLen) {
                    $targetListLi.find(".checkAll").prop("checked", false);
                    if ($($targetListLi).children("input[type=checkbox]:checked").length > 0) {
                        for (let i = 0; i < $targetListLi.length; i++) {
                            if ($($targetListLi[i]).children("input[type=checkbox]").prop("checked")) selectStr += $($targetListLi[i]).text();
                        }
                    } else {
                        selectStr = $targetSelect.data("placeholder");
                    }

                } else {
                    $targetListLi.find(".checkAll").prop("checked", true);
                    selectStr = $targetListLi.find(".checkAll + label").text();
                }
            }

            $targetSelect.text(selectStr);
        }
        comm.createMultiSelectBox.prototype.listOff = function($target) {
            if ($(".selected_box").length && !($($target).parents(".selected_box").length) && !($($target).hasClass("selected_box"))) {
                let selectedBox = $(".selected_box");
                selectedBox.removeClass('selected_box');
                selectedBox.children(".list").css('display', 'none');
            }
        }

        this.createMultiSelectBox.prototype.init(selector);
        this.createMultiSelectBox.prototype.initEvent();
    }
};

/**
 * 문자열 관련
 */
var stringFunc = {
    getRandomNumber: function(length) {
        var rand = 0;
        var result = "";

        for (var i = 0; i < length; i++) {
            result += Math.floor(Math.random() * 10);
        }
        return result;
    },
    getStringToByteCount: function(str, maxByte) {
        var strValue = str;
        var strLen = strValue.length;
        var totalByte = 0;
        var len = 0;
        var oneChar = "";
        var str2 = "";

        for (var i = 0; i < strLen; i++) {
            oneChar = strValue.charAt(i);
            if (escape(oneChar).length > 4) {
                totalByte += 2;
            } else {
                totalByte++;
            }
        }
        return totalByte;
    },
    getCutByteLength: function(s, len) {
        if (s == null || s.length == 0) {
            return 0;
        }
        var size = 0;
        var rIndex = s.length;
        var resultStr = "";

        for (var i = 0; i < s.length; i++) {
            size += this.charByteSize(s.charAt(i));
            if (size == len) {
                rIndex = i + 1;
                resultStr = s.substring(0, rIndex);
                break;
            } else if (size > len) {
                rIndex = i;
                resultStr = s.substring(0, rIndex) + "...";
                break;
            }
        }
        return resultStr;
    },
    charByteSize: function(ch) {
        if (ch == null || ch.length == 0) {
            return 0;
        }

        var charCode = ch.charCodeAt(0);

        if (charCode <= 0x00007F) {
            return 1;
        } else if (charCode <= 0x0007FF) {
            return 2;
        } else if (charCode <= 0x00FFFF) {
            return 3;
        } else {
            return 4;
        }
    },
    commaNumber: function(str) {
        return str.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },
    toString: function(tStr) {
        /*
        var result = "";
        var str = String(tStr);

        if(this.validRegex(str, "mobile")) {
            if(str.length==11) {
                result = str.substring(0, 3)+"-"+str.substring(3, 7)+"-"+str.substring(7, 11);
            } else if(tStr.length==10) {
                result = str.substring(0, 3)+"-"+str.substring(3, 6)+"-"+str.substring(6, 10);
            }
        } else {
            result = str;
        }
        */
        let result = "";
        if (tStr != null && tStr != "null" && typeof(tStr) != "undefined" && typeof(tStr) != null) {
            result = tStr;
        } else {
            result = "-";
        }
        return result;
    },
    getCompareText: function($a, $b) {
        var result = true;
        if ($a.val() != $b.val()) {
            result = false;
            $b.focus();
        }
        return result;
    },
    camelize: function(text) {
        return text.replace(/^([A-Z])|[\s-_]+(\w)/g, function(match, p1, p2, offset) {
            if (p2) return p2.toUpperCase();
            return p1.toLowerCase();
        });
    },
    /**
     * @param 정규식 체크 문자열
     * @param 정규식 타입
     * loginId : 아이디 형식
     * password : 비밀번호 형식
     * number : 숫자형식(0~9)
     * time : 시간 (12:30:00)
     * date : 날짜 (2018-12-30)
     * datetime : YYYYMMDDHH24MISS
     */
    validRegex: function(tVal, type) {

        var result = true;
        var regex = "";

        // 로그인 ID : 3~25 자리 영숫자.
        if (type === "loginId") {
            regex = /^[A-Za-z0-9]{3,25}$/g;
            // 로그인 이름
        } else if (type === "loginName") {
            regex = /^[A-Za-z0-9가-힣]+$/g;
            // 7~20 자리 영숫자.
        } else if (type === "password") {
            regex = /^(?=.*[a-zA-Z])((?=.*\d)|(?=.*\W)).{7,20}$/g;
        } else if (type === "name") {
            regex = /^[가-힣]+$/g;
        } else if (type === "age") {
            regex = /^1?[0-9]?[0-9]$/g;
        }
        // 8~15 숫자 문자 특수문자
        else if (type === "sPassword") {
            regex = /^.*(?=^.{8,15}$)(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/g;
            // 시간
        } else if (type === "time") {
            regex = /^[0-9]{2}:[0-9]{2}:[0-9]{2}$/g;
            // 24 시간
        } else if (type === "hms24") {
            regex = /^([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$/g;
            // 날짜
        } else if (type === "date") {
            regex = /^(19|20)\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$/g;
            // 숫자만
        } else if (type === "number") {
            regex = /^[0-9]+$/g;
            // 4자리 패스워드
        } else if (type === "numberPassword4") {
            regex = /^[0-9]{4}$/g;
        } else if (type === "unsignFloat") {
            regex = /^(\d+)\.(\d+)$/g;
            // 이메일
        } else if (type === "email") {
            regex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
            // URL
        } else if (type === "url") {
            regex = /^[a-zA-Z0-9/]+(.sn|.asn)*$/g;
            // 날짜 + 시간
        } else if (type === "dateTime") {
            regex = /^(19|20)\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[0-1])([1-9]|[01][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/g;
        } else if (type === "year") {
            regex = /^(19|20)\d{2}$/;
        } else if (type === "day") {
            regex = /^(0[1-9]|[12][0-9]|3[0-1])$/;
        }

        if (!tVal.match(regex)) {
            result = false;
        }
        return result;
    },
    /* str 빈값 체크 */
    isValidStr: function(str) {
        return str === null || str === undefined || str === "";
    },
    changeXSSInputValue: (str, level) => {
        let returnStr = "";
        if (typeof(str) === "String") {
            if (typeof level === "undefined" || level === 0) {
                returnStr = str.replace(/&/gi, '&amp;').replace(/</gi, '&lt;').replace(/>/gi, '&gt;').replace(/"/gi, '&quot;').replace(/'/gi, '&apos;');
            } else if (typeof level !== "undefined" && level === 1) {
                returnStr = str.replace(/\</g, "&lt;");
                returnStr = str.replace(/\>/g, "&gt;");
            }
        } else {
            returnStr = str;
        }
        return returnStr;
    },
    changeXSSOutputValue: (str) => {
        let returnStr = "";
        if (typeof(str) === "String") {
            returnStr = str.replace(/&amp;/gi, '&').replace(/&lt;/gi, '<').replace(/&gt;/gi, '>').replace(/&quot;/gi, '"').replace(/&apos;/gi, '\'').replace(/&nbsp;/gi, ' ');
        } else {
            returnStr = str;
        }
        return returnStr;
    }
}

/**
 * 날짜관련
 */
const dateFunc = {
    getZeroString: function(tVal) {
        return (tVal > 9 ? '' : '0') + tVal;
    },
    /**
     * 현재날짜
     * @param 이전날짜, 이후날짜
     * @returns date 타입 Object
     */
    getCurrentDate: function(d) {
        const date = new Date();
        date.setDate(date.getDate() + d);
        return date;
    },
    /**
     * 현재날짜
     * @param 이전날짜, 이후날짜
     * @returns EX) 201908191500
     */
    getCurrentDateYyyyMmDdHh24Mi: function(d, dateSep, timeSep) {
        const dateArr = [
            dateFunc.getCurrentDate(d).getFullYear(), this.getZeroString(dateFunc.getCurrentDate(d).getMonth() + 1), this.getZeroString(dateFunc.getCurrentDate(d).getDate())
        ];
        const timeArr = [
            this.getZeroString(dateFunc.getCurrentDate(d).getHours()), this.getZeroString(dateFunc.getCurrentDate(d).getMinutes()), this.getZeroString(dateFunc.getCurrentDate(d).getSeconds())
        ];

        return dateArr.join(dateSep) + " " + timeArr.join(timeSep);
    },
    getCurrentDateYyyyMmDd: function(d, sep) {
        const dateArr = [
            dateFunc.getCurrentDate(d).getFullYear(), this.getZeroString(dateFunc.getCurrentDate(d).getMonth() + 1), this.getZeroString(dateFunc.getCurrentDate(d).getDate())
        ];
        return dateArr.join(sep);
    },
    /**
     * 날짜에 따른 한글 텍스트 출력
     * @param 날짜
     * day.js 필요
     */
    getDateText: (pDate) => {
        const date = dayjs(pDate);
        const milliSeconds = new Date() - date
        const seconds = milliSeconds / 1000
        if (seconds < 60) return `방금 전`
        const minutes = seconds / 60
        if (minutes < 60) return '1시간 이내' //`${Math.floor(minutes)}분 전`
        const hours = minutes / 60
        if (hours < 24) return `${Math.floor(hours)}시간 전`
        const days = hours / 24
        if (days < 7) return `${Math.floor(days)}일 전`
        const weeks = days / 7
        if (weeks < 5) return `${Math.floor(weeks)}주 전`
        const months = days / 30
        if (months < 12) return `${Math.floor(months)}개월 전`
        const years = days / 365
        return `${Math.floor(years)}년 전`
    },
    /*
     * 달력 생성기
     * @param sDate 파라미터만 넣으면 1개짜리 달력 생성
     * @example   datePickerSet($("#datepicker"));
     *
     *
     * @param sDate,
     * @param eDate 2개 넣으면 연결달력 생성되어 서로의 날짜를 넘어가지 않음
     * @param customObj 사용자 정의 설정 Object
     * @example   datePickerSet($("#datepicker1"), $("#datepicker2"));
     */
    datePickerSet: function(sDate, eDate, flag, customObj) {
        //시작 ~ 종료 2개 짜리 달력 datepicker
        if (!stringFunc.isValidStr(sDate) && !stringFunc.isValidStr(eDate) && sDate.length > 0 && eDate.length > 0) {
            let sDay = sDate.val();
            let eDay = eDate.val();

            let defaultObj = {
                language: 'ko',
                autoClose: true,
                timepicker: true,
                timeFormat: "hh:ii",
                onSelect: function() {
                    dateFunc.datePickerSet(sDate, eDate);
                }
            }

            let otpObj = Object.assign(defaultObj, customObj);

            if (flag && !stringFunc.isValidStr(sDay) && !stringFunc.isValidStr(eDay)) { //처음 입력 날짜 설정, update...
                let sdp = sDate.datepicker().data("datepicker");
                sdp.selectDate(new Date(sDay.replace(/-/g, "/"))); //익스에서는 그냥 new Date하면 -을 인식못함 replace필요

                let edp = eDate.datepicker().data("datepicker");
                edp.selectDate(new Date(eDay.replace(/-/g, "/"))); //익스에서는 그냥 new Date하면 -을 인식못함 replace필요
            }

            //시작일자 세팅하기 날짜가 없는경우엔 제한을 걸지 않음
            if (!stringFunc.isValidStr(eDay)) {
                sDate.datepicker({
                    maxDate: new Date(eDay.replace(/-/g, "/"))
                });
            }
            sDate.datepicker(otpObj);

            //종료일자 세팅하기 날짜가 없는경우엔 제한을 걸지 않음
            if (!stringFunc.isValidStr(sDay)) {
                eDate.datepicker({
                    minDate: new Date(sDay.replace(/-/g, "/"))
                });
            }
            eDate.datepicker(otpObj);
        } else if (!stringFunc.isValidStr(sDate)) { //한개짜리 달력 datepicker
            let sDay = sDate.val();
            if (flag && !stringFunc.isValidStr(sDay)) { //처음 입력 날짜 설정, update...
                let sdp = sDate.datepicker().data("datepicker");
                sdp.selectDate(new Date(sDay.replace(/-/g, "/"))); //익스에서는 그냥 new Date하면 -을 인식못함 replace필요
            }
            let defaultObj = {
                language: 'ko',
                autoClose: true,
                timepicker: true,
                timeFormat: "hh:ii AA",
            }
            let otpObj = Object.assign(defaultObj, customObj);
            sDate.datepicker(defaultObj);
        }
    }
}
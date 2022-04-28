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
		
		if(flag) {
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
	            var arr = this.serializeArray();
	            if (arr) {
	                jQuery.each(arr, function() {
	                	obj[this.name] = this.value;
	                });
	            }
	            $.each( this[0], function (idx, element ) {
		        	if(element.tagName=="SELECT" && $(element).attr("multiple")=="multiple") {
		        		obj[$(element).attr("id")] = $(element).selectpicker("val");
		        	}
		        });
	        }
	    } catch (e) {
	    	console.log("serializeJSON Func Error : " + e.message);
	    } finally {
	    }
	    return obj;
	},
	doRequired : function() {
		var curThis = this, returnVal = true, txtLabel = "";

		$.each( curThis[0], function (idx, element ) {
			if($(element).attr("required")=="required") {
				//$(element).parents(".form-group").find("label .label_span").empty();
				//$(element).parents(".form-group").find("label .label_span").append("<i class='fas fa-check mr-2'></i>");
			
				if(element.tagName=="INPUT" || element.tagName=="TEXTAREA") {
					$(element).prev("label").find(".label_span").empty();
					//$(element).prev("label").find(".label_span").append("<i class='fas fa-check mr-2'></i>");
					$(element).prev("label").find(".label_span").append("● ").css("color", "purple");
				} else if(element.tagName=="SELECT") {
					$(element).parent().parent().find(".label_span").empty();
					//$(element).parent().parent().find(".label_span").append("<i class='fas fa-check mr-2'></i>");
					$(element).parent().parent().find(".label_span").append("● ").css("color", "purple");
				}
			}
		});
	},
	doSearchValidation : function() {
		var curThis = this;
		var result = true;

		$.each( curThis[0], function (idx, element ) {
			if($(element).attr("id")=="startDt") {
				var startDtDateArr = $(element).val().split("-");
				var startDtDate = new Date(startDtDateArr[0], Number(startDtDateArr[1])-1, startDtDateArr[2]);

				var endDtDateArr = $("#searchForm").find("#endDt").val().split("-");
				var endDtDate = new Date(endDtDateArr[0], Number(endDtDateArr[1])-1, endDtDateArr[2]);
				var betweenDay = (startDtDate.getTime()-endDtDate.getTime())/1000/60/60/24;

				if(betweenDay>0) {
					comm.showAlert("종료날짜보다 시작날짜가 더 큽니다.");
					$(element).val($("#searchForm").find("#endDt").val());
					result = false;
				}
			} else if($(element).attr("id")=="searchFullKeyword") {
				var searchKeyword = $.trim($(element).val());
				var regex = /^[A-Za-z가-헿0-9_\s]+$/;

				if(searchKeyword.length>0) {
					if(!searchKeyword.match(regex)) {
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
	doValidation : function() {
		var curThis = this, returnVal = true, txtLabel = "";
		var validType = 1;
		var $labelElement;

		$.each( curThis[0], function (idx, element) {
			$labelElement = curThis.find("label[for="+($(element).attr("id"))+"]");
			
			if(typeof($labelElement.attr("data-hidden-value"))!="undefined") {
				txtLabel = $labelElement.attr("data-hidden-value");
			} else {
				txtLabel = $labelElement.text();
			}

			if(typeof(txtLabel)!="undefined") {
				txtLabel = txtLabel.replace(" :", "");
				txtLabel = txtLabel.replace("* ", "");
			}
			
			if(!$(element).is(":disabled")) {
				if($(element).data("required")
					&& ((element.tagName=="INPUT" && $.trim($(element).val())=="") 
					|| (element.tagName=="SELECT" && ($(element).val()==null || $.trim($(element).val())=="")))) {
						$(element).focus();
						validType = 1;
						returnVal = false;
						return false;
				
				} else if($(element).val()!="" && typeof($(element).attr("data-regex"))!="undefined") {
					if(!stringFunc.validRegex($(element).val(), $(element).attr("data-regex"))) {
						$(element).focus();
						validType = 2;
						returnVal = false;
						return false;
					}
				}
			}
		});
		
		if(!returnVal) {
			var resultStr = "";
			resultStr = "[" + txtLabel + "]";
			resultStr += "<br />";

			if(validType==1) {
				resultStr += "* 필수항목 미입력 *";
			} else {
				resultStr += "* 형식에 맞지 않는 문자 *";
			}
			resultStr += "<br />다시 입력해 주십시오.";
			comm.showAlert(resultStr);
		}
		return returnVal;
	},
	initForm : function() {
		$.each($(this)[0], function(idx, element){
			var objEle = $(element);
			var eleAttr = objEle[0];

			if(eleAttr != "undefined"  && typeof(eleAttr) != 'undefined') {
				if(typeof(objEle.attr("data-init-value"))!="undefined") {
					objEle.val(objEle.attr("data-init-value"));
				} else if(eleAttr.tagName == "INPUT" || eleAttr.tagName == "TEXTAREA") {
					objEle.val("");
				} else if(eleAttr.tagName == "SELECT") {
					var firstVal = objEle.find("option:eq(0)").val();
					objEle.val(firstVal);
					//objEle.selectpicker("val", firstVal);
				}
			}
		});
	},
	setItemValue : function(objValue) {
		var $curThis = $(this);
		var strValue = "";
		var arrInput = [];
		
		arrInput.push($curThis.find("input"));
		arrInput.push($curThis.find("textarea"));
		
		$.each(arrInput, function(idx, value) {
			$.each( value, function( idx, element ){
				if(element.type === "radio"){
					strValue = objValue[element.name];
				} else {
					strValue = objValue[element.id];
				}
				if(strValue == undefined) { return; } 
				
				if(element.type == "checkbox") {
					element.checked = (strValue == 'Y')? true : false;
				} else if(element.type == "radio") {
					if($(element).data('value') == strValue) element.checked = "checked";
				} else {
					if($(element).attr("data-set")!="false") {
						element.value = strValue;
					}
				}
			});
		});
	},
	setSelectItem : function(resultData, option) {
		var $curThis = $(this);
		var isSelectArray = [];
		var className = "";
		var dataStyle = "";
		var dataContent = "";
		var firstVal;
		
		$curThis.find("option").not("[data-init=true]").remove();
		
		$.each(resultData, function(i) {
			dataContent = "";

			// 내용 포함 CLASS 적용
			if(typeof(option)!="undefined") {
				if(typeof(option.ifFunc)!="undefined") {
					$.each(option.ifFunc, function(k, v) {
						if(resultData[i].text.indexOf(k)>-1) {
							className = option.ifFunc[k];
						}
					});
				} else if(option.valueBackgroundFlag=="Y") {
					dataStyle = ("background:" + resultData[i].value);
				}
			}

			$curThis.append("<option data-content='"+dataContent+"' class='"+className+" ' value='"+resultData[i].value+"' style='" + dataStyle + "'>"+resultData[i].text+"</option>");
			
		});
		
		// 첫번째 요소 강제 선택 트리거.
		if(typeof(option)!="undefined") {
			if(typeof(option.defaultTrigger)!="undefined") {
				$curThis.selectpicker("val", firstVal);
			}
		}

		$curThis.selectpicker("val", firstVal);
		$curThis.selectpicker("refresh");
	},
	setCommonCode : function(groupCode, option) {
		var $curThis = $(this);
		var newObj = {groupCode : groupCode};
		
		if(typeof(option)!="undefined") {
			newObj = $.extend(newObj, option);
		}
		comm.ajaxPost({async : false, url : "/common/getComboListCode.ado", data : newObj}, function(resultData) {
			$curThis.setSelectItem(resultData.data, option);
		});
	}
});

var comm = {
	ajaxPost: function(obj, fnDoneCallback, fnFailCallback, fnFail) {
		var defaults = {
			async 		: true
			, contentType : "application/json; charset=utf-8" 
			, type 		: "POST" 
			, dataType 	: "json"
			, showLoading : true
			, hideLoading : true
		}
		if(typeof(obj.isExternalApi)!="undefined") {
			if(typeof(obj.dataType)=="undefined") {
				delete obj.dataType;
			}
		}
		var newObj = $.extend(defaults, obj);
		
		if(newObj.showLoading) {
			comm.showLoading();
		}

		if(typeof(obj.isExternalApi)=="undefined") {
			newObj.data = JSON.stringify(obj.data);
		}

		$.ajax(newObj).done(function(resultObj) {
			if(typeof(obj.isExternalApi)!="undefined") {
				fnDoneCallback(resultObj);
			} else {
				if(typeof(resultObj)=="object") {
					if(resultObj.resultCode=="000") {
						//msg : 컨트롤러 단에서 확인 메시지
						if(resultObj.data!=null && typeof(resultObj.data.msg)!="undefined") {
							if(resultObj.data.alertFlag!="N") {
								comm.showAlert(resultObj.data.msg);
							}
						}
						if(typeof(fnDoneCallback)!="undefined") {
							fnDoneCallback(resultObj);
						}
					} else if(resultObj.resultCode=="999") {
						if(code=="403") {
							location.href = "/admin/loginForm.do";
						} else {
							comm.showAlert(resultObj.message);
						}
					} else {
						fnDoneCallback(resultObj);
					}
				}
			}
		}).fail(function(jqXHR, textStatus, errorThrown) {
			if(typeof(fnFailCallback)=="function") {
				fnFailCallback();
			} else {				
				if(typeof(jqXHR.responseJSON) == "undefined") {
					console.log(jqXHR);
				} else {
					var message = jqXHR.responseJSON.message;
					comm.showAlert(message);
					console.log(jqXHR.responseJSON.exception);
				}
			}
		}).always(function() {
			if(newObj.hideLoading) {
				comm.hideLoading();
			}
		});
	}
	, linkPage : function(t) {
		pageIndex = t;
		doSearch();
	},
	/**
	 * 테이블 로우 하이라이트
	 */
	rowHighlight: (keyword, td, cellData) => {
		if ((keyword != "") && (cellData.indexOf(keyword) !== -1)) {
			const regex = new RegExp(keyword, 'gi');
			$(td).html(cellData.replace(regex, "<b class='highlight'>" + keyword + "</b>"));
		}
	},
	/**
	 * Datatable extend row 생성
	 */
	format : function(data, row) {
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
	}
	, createTable : ($target, optionObj, evt) => {
		comm.showLoading();
		
		const defaultObj = {
				dom: '<"tableBody"rt><"tableBottom"p>',
				pageLength: 15,
	            pagingType : "full_numbers",
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
	            language: 
		            {
	            		emptyTable : "데이터가 없습니다.",
	            		zeroRecords : "검색된 데이터가 없습니다.",
		                paginate: {
		                	first: '<span><img src="/images/settings/iconPrev2.svg"></span>',
		                    previous: '<span><img src="/images/settings/iconPrev.svg"></span>',
		                    next: '<span><img src="/images/settings/iconNext.svg"></span>',
		                	last: '<span><img src="/images/settings/iconNext2.svg"></span>'
		            	}
		        	},
				excelDownload: false
	            }
				
		
		const newOptionObj = $.extend({}, defaultObj, optionObj);
		if(!$.isEmptyObject(evt)) {
			if(typeof evt.click !== "undefined") {
				$target.off('click');
				$target.on('click', 'tr', evt.click);
			}
			if(typeof evt.dblclick !== "undefined") {
				$target.on('dblclick', 'tr', evt.dblclick);
			}
		}
		$.fn.DataTable.ext.pager.numbers_length = 10;
		$target.DataTable(newOptionObj);
		
		if(newOptionObj.excelDownload) {
			const html = '<a class="excelDownloadBtn">'
				+ '<span class="buttonIcon"><img src="/images/settings/iconExport.svg">'
				+ '</span>엑셀로 내보내기</a>'; 
			$target.parents('.tableBody').siblings('.tableBottom').append(html);
		}
		
		comm.hideLoading();
	}
	, showAlert : function(title, option) {
		var defaultOption = {
			title : title,
			text : '',
			icon : 'info',
			confirmButtonText: '확인',
			heightAuto: false
		}
		var newObj = $.extend(defaultOption, typeof(option)=="undefined" ? {} : option);
		
		Swal.fire(newObj);
	}
	, confirm : function(title, option, fnProc, cancleProc) {
		var defaultOption = {
			icon : 'warning',
			title : title,
			text : '',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: '확인',
			cancelButtonText: '취소',
			heightAuto: false
		}
		var newObj = $.extend(defaultOption, typeof(option)=="undefined" ? {} : option);
		swal.fire(newObj).then((result) => {
			if(result.value) {
				fnProc();
			} else {
				cancleProc();
			}
		});
	}
	, toastOpen : function (mainObj, callback) {
		toastr.options = {
				"closeButton" : true,
				"timeOut": 0,
				"extendedTimeOut": 0,
				"positionClass" : "toast-top-full-width",
				"onclick" : callback
		}
		var newObj = $.extend(toastr.options, typeof(mainObj.options)=="undefined" ? {} : mainObj.options);
		toastr.options = newObj;
		mainObj.options = {'maxToast':5};

		if(typeof mainObj.options.maxToast != 'undefined') {
			toastr.subscribe(function(args) {
				if (args.state === 'visible')
				{
					var toasts = $("#toast-container > *:not([hidden])");
					if (toasts && toasts.length > mainObj.options.maxToast) {
						toasts[toasts.length-1].remove();
					}
				}
			});
		}
		toastr[mainObj.type ? mainObj.type : "error"](mainObj.content, mainObj.title);
	}
	, initModal : ($target) => {
		const html = '<div id="modal-background" style="display:none; position:absolute; background-color:#0000005e; width:100%; height:100%; z-index:1000"><div>'
		$('body > div:nth-child(1)').append(html);
	}
	, showModal : function($popupEle) {
		$popupEle.css('z-index', '1010');
		$('#modal-background').show();
	}
	, hideModal : function($popupEle) {
		$popupEle.css('z-index', 'unset');
		$('#modal-background').hide(); 
	}
	, showLoading : function() {
		var newObj = {
			text : "<b>처리중입니다.</b>"
			, color : "#fff"
			, animation : "circle"
		}
		$("body").loadingModal({
			text : newObj.text,
			opacity : "0",
			color : newObj.color, 
			animation : newObj.animation
		});
		$("body").loadingModal("show");
	}
	, hideLoading : function() {
		$("body").loadingModal("hide");
	}
	, console : function(obj) {
		if(isDebug) {
			//console.log(obj);
		}
	}
	, showTooltip : function(target, option) {
		var defaultOption = {
			show : {effect : "fade", duration: 100}
			, hide : {effect : "fade", duration: 100}
			, position: {my: "left+15 center", at: "right center"}
			
		};
		var newOption = $.extend(defaultOption, option);
		$(target).tooltip(newOption);
	}
	, audioPlay : function(src) {
		var audioId = "audio" + Math.floor(Math.random()*1000);
		var audioHtml = "<audio id='"+audioId+"' src='"+src+"' autoplay></audio>";		//console.log(audioId);

		$("body").append(audioHtml);
		
		setTimeout(function() {
			$("#"+audioId).remove();
		}, 3000);
	}
	, downloadExcelFile : function(paramObj) {
		let html = "";
		html += "<form id='downloadForm' method='post' action='"+paramObj.url+"'>";
		//html += "<input type='hidden' name='columnArr' value='" +paramObj.columnArr+ "' />";
		//html += "<input type='hidden' name='columnNmArr' value='"+paramObj.columnNmArr+"'/>";
		html += "</form>";
		$("body").append(html);
		
		$("#downloadForm").submit();
		
		setTimeout(function() {
			$("#downloadForm").remove();
		}, 2000);
	}
};

/**
 * 문자열 관련
 */
var stringFunc = {
	getRandomNumber : function(length) {
		var rand = 0;
		var result = "";
		
		for(var i=0; i<length; i++) {
			result += Math.floor(Math.random() * 10);
		}
		return result;
	},
	getStringToByteCount : function(str, maxByte) {
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
	getCutByteLength : function(s, len) {
		if (s == null || s.length == 0) {
			return 0;
		}
		var size = 0;
		var rIndex = s.length;
		var resultStr = "";

		for ( var i = 0; i < s.length; i++) {
			size += this.charByteSize(s.charAt(i));
			if( size == len ) {
				rIndex = i + 1;
				resultStr = s.substring(0, rIndex);
				break;
			} else if( size > len ) {
				rIndex = i;
				resultStr = s.substring(0, rIndex) + "...";
				break;
			}
		}
		return resultStr;
	},
	charByteSize : function(ch) {
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
	commaNumber : function(str) {
		return str.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	},
	toString : function(tStr) {
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
		if(tStr != null && tStr != "null" && typeof(tStr) != "undefined" && typeof(tStr) != null) {
			result = tStr;
		} else {
			result = "-";
		}
		return result;
	},
	getCompareText : function($a, $b) {
		var result = true;
		if($a.val()!=$b.val()) {
			result = false;
			$b.focus();
		}
		return result;
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
	validRegex : function(tVal, type) {
		
		var result = true;
		var regex = "";

		// 로그인 ID : 3~25 자리 영숫자.
		if(type=="loginId") {
			regex =  /^[A-Za-z0-9가-힣]{3,25}$/g;
		// 로그인 이름
		} else if(type=="loginName") {
			regex =  /^[A-Za-z0-9가-힣]+$/g;
		// 7~20 자리 영숫자.
		} else if(type=="password") {
			regex = /^(?=.*[a-zA-Z])((?=.*\d)|(?=.*\W)).{7,20}$/g;
		} else if(type=="name") {
			regex = /^[가-힣]+$/g;
		} else if(type=="age") {
			regex = /^1?[0-9]?[0-9]$/g;
		}
		// 8~15 숫자 문자 특수문자
		else if(type=="sPassword") {
			regex = /^.*(?=^.{8,15}$)(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/g;
		// 시간
		} else if(type=="time") {
			regex = /^[0-9]{2}:[0-9]{2}:[0-9]{2}$/g;
		// 24 시간
		} else if(type=="hms24") {
			regex = /^([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$/g;
		// 날짜
		} else if(type=="date") {
			regex = /^(19|20)\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$/g;
		// 숫자만
		} else if(type=="number") {
			regex = /^[0-9]+$/g;
		// 4자리 패스워드
		} else if(type=="numberPassword4") {
			regex = /^[0-9]{4}$/g;			
		} else if(type=="unsignFloat") {
			regex = /^(\d+)\.(\d+)$/g;
		// 이메일
		} else if(type=="email") {
			regex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
		// URL
		} else if(type=="url") {
			regex = /^[a-zA-Z0-9/]+(.sn|.asn)*$/g;
		// 날짜 + 시간
		} else if(type=="dateTime") {
			regex = /^(19|20)\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[0-1])([1-9]|[01][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/g;
		} else if(type=="year") {
			regex = /^(19|20)\d{2}$/;
		} else if (type=="day") {
			regex = /^(0[1-9]|[12][0-9]|3[0-1])$/;
		}

		if(!tVal.match(regex)) {      
			result = false;
		}
		return result;
	}
}

/**
 * 날짜관련
 */
var dateFunc = {
	getZeroString : function(tVal) {
		return (tVal > 9 ? '' : '0') + tVal;
	},
	/**
	 * 현재날짜
	 * @param 이전날짜, 이후날짜
	 * @returns date 타입 Object
	*/
	getCurrentDate : function(d) {
		var date = new Date();
		date.setDate(date.getDate()+d);
		return date;
	},
	/**
	 * 현재날짜
	 * @param 이전날짜, 이후날짜
	 * @returns EX) 201908191500 
	*/
	getCurrentDateYyyyMmDdHh24Mi : function(d) {
		var dateStr;
		var year = dateFunc.getCurrentDate(d).getFullYear();
		var month = dateFunc.getCurrentDate(d).getMonth()+1;
		var day = dateFunc.getCurrentDate(d).getDate();
		var hour = dateFunc.getCurrentDate(d).getHours();
		var min = dateFunc.getCurrentDate(d).getMinutes();
		var sec = dateFunc.getCurrentDate(d).getSeconds();
		
		dateStr = year + this.getZeroString(month) + this.getZeroString(day) + this.getZeroString(hour) + this.getZeroString(min) + this.getZeroString(sec);
		return dateStr;
	},
	getCurrentDateYyyyMmDd : function(d) {
		var dateStr;
		var year = dateFunc.getCurrentDate(d).getFullYear();
		var month = dateFunc.getCurrentDate(d).getMonth()+1;
		var day = dateFunc.getCurrentDate(d).getDate();
		
		dateStr = year+"-"+(month > 9 ? '' : '0') + month +"-"+ (day > 9 ? '' : '0') + day;
		return dateStr;
	}
}
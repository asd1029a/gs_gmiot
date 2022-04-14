const admin = {
	init : () => {
		admin.getListAdmin($('#adminTable'));

		$("#initSearchForm").on('click', (e) => {
			$("#adminKeyword").val('');
		});

		$("#getListAdminBtn").on('click', (e) => {
			admin.getListAdmin($('#adminTable'));
		});

		$("#adminListCntSel").on('change', (e) => {
			admin.getListAdmin($('#adminTable'));
		});

		$("#adminKeyword").on('keyup', (e)=> {
			if(e.keyCode == 13) {
				admin.getListAdmin($('#adminTable'));
			}
		});

		$("#addAdminBtn").on('click', () => {
			admin.showAdminPopup("add");
		});

		$("#modAdminPwdBtn").on('click', () => {
			const adminObj = $('#adminTable').DataTable().rows({selected:true}).data()[0];
			if(typeof adminObj != "undefined") {
				if(adminObj.useFlag !== "2") {
					comm.showModal($("#adminPwdPopup"));
					$("#adminPwdPopup").css('display', 'flex');
					$('#adminPwdForm input[name="id"]').val(adminObj.id);
					$('#adminPwdForm input[name="userSeq"]').val(adminObj.userSeq);
				} else {
					comm.showAlert("삭제된 사용자입니다");
				}
			} else {
				comm.showAlert("변경할 사용자를 선택해주세요");
			}

		});

		$(".popup .popupButton ul:nth-child(1)").on('click', ()=> {
			admin.hideAdminPopup();
		});

		$("#addAdminProcBtn").on('click', () => {
			if($('#adminForm').doValidation() === true) {
				if($('#password').val() === $('#passwordCheck').val()) {
					if($('#duplicateCheckBtn').hasClass('active') === true) {
						admin.addAdminProc();
					} else {
						comm.showAlert('아이디 중복확인 필요합니다');
						return false;
					}
				} else {
					comm.showAlert('비밀번호가 일치하지 않습니다');
					return false;
				}
			} else {
				return false;
			}
		});

		$("#modAdminProcBtn").on('click', () => {
			if($('#adminForm').doValidation() === true) {
				admin.modAdminProc();
			} else {
				return false;
			}
		});

		$("#delAdminProcBtn").on('click', () => {
			const adminSeq = $('#userSeq').val();
			comm.confirm("해당 사용자를 제거하시겠습니까?"
				, {}
				, () => {admin.delAdminProc(adminSeq);}
			);
		});

		$("#modAdminPwdProcBtn").on('click', () => {
			const password = $('#adminPwdForm input[name="password"]').val();
			const passwordCheck = $('#adminPwdForm input[name="passwordCheck"]').val();
			if($('#adminPwdForm').doValidation() === true) {
				if( password === passwordCheck ) {
					admin.modAdminPwdProc();
				} else {
					comm.showAlert('비밀번호가 일치하지 않습니다');
				}
			} else {
				return false;
			}
		});

		$("#duplicateCheckBtn").on('click', (e) => {
			const adminId = $('#adminId').val();
			console.log("중복체크 아이디 입력값1 : " + adminId);

			if(adminId !== '' && stringFunc.validRegex(adminId, "loginId") === true) {
				admin.checkDuplAdminId(adminId);
			} else {
				comm.showAlert("사용자 아이디가 형식에 맞지 않거나 </br>올바르지 않습니다.");
			}
		});

		$("#getAddressBtn").on('click', () => {
			admin.getAdminAddress();
		});

		$('.excelDownloadBtn').on('click', (e) => {
			let paramObj = {
				url : "/admin/exportExcelAdmin.do"
			}
			comm.downloadExcelFile(paramObj);
		});
	},

	/**
	 * 로그인
	 */
	loginProc : () => {
		if($("#loginForm").doValidation()) {
			$("#loginForm").attr("onsubmit", "return true;");
			$("#loginForm").submit();
		}
	},
	/**
	 * 로그아웃
	 */
	logoutProc : () => {
		location.href = "/logout";
	},
	/**
	 * 관리자 리스트 조회
	 */
	getListAdmin : ($target) => {
		const filterObj = {
			keyword : $("#adminKeyword").val(),
			pageLength : $("#adminListCntSel").val()
		};
		const optionObj = {
			dom: '<"tableBody"rt><"tableBottom"p>',
			destroy: true,
			pageLength: filterObj.pageLength,
			scrollY: "calc(100% - 6px)",
			ajax :
				{
					'url' : "/admin/getListAdmin.ado",
					'contentType' : "application/json; charset=utf-8",
					'type' : "POST",
					'data' : function ( d ) {
						d.keyword = filterObj.keyword;
						return JSON.stringify( d );
					},
					'dataSrc' : function (result) {
						$('.tableTitle h4 span').text(result.recordsTotal);
						return result.data;
					}
				},
			columns : [
				{data: "id", className: "alignLeft"},
				{data: "userName", className: "alignLeft"},
				{data: "tel"},
				{data: "address", className: "alignLeft"},
				{data: "email", className: "alignLeft"},
				{data: "codeName"},
				{data: null}
			],
			"columnDefs": [
				{"targets": -1,
					"data": null,
					"defaultContent": '<span class="tableButton"></span>'
				},
				{
					"targets": 5,
					"createdCell": function (td, cellData, rowData, row, col) {
						if ( cellData === "사용" ) {
							$(td).css('color', '#409cfb')
						} else {
							$(td).css('color', '#f65656')
						}
					}
				}
			],
			excelDownload : true
		}

		const evt = {
			click : function(e) {
				if($(e.target).hasClass('tableButton')) {
					admin.showAdminPopup('mod');
					const rowData = $('#adminTable').DataTable().row(this).data();
					if(rowData.birth != null) {
						rowData.year = rowData.birth.substring(0,4);
						$('#month').val(rowData.birth.substring(4,6));
						rowData.day = rowData.birth.substring(6,8);
					}
					$('#adminForm').setItemValue(rowData);
					$('#adminForm [name="useFlag"][data-value="'+rowData.useFlag+'"]').prop('checked', true);
					$('#adminId').text(rowData.id);
				}
			},
			dblclick : function() {
			}
		}
		comm.createTable($target ,optionObj, evt);
	}
	/**
	 * 관리자 팝업 생성
	 */
	, showAdminPopup : (type) => {
		$('.inputPopup').hide();
		$('#adminPopup .popupButton ul li[data-mode]').hide();
		$('#adminPopup .popupContents').scrollTop(0);
		$('#adminForm dl input[name="password"]').removeAttr('data-required').parents('dl').hide();
		$('#adminForm dl input[name="passwordCheck"]').removeAttr('data-required').parents('dl').hide();
		$('#adminForm [name="useFlag"]').prop("checked", false);
		$('#adminForm').initForm();
		if(type == "add") {
			$('#adminPopup .popupTitle h4').text('사용자 계정 추가');
			$('#adminPopup .popupButton ul li[data-mode="add"]').show();
			$('#adminForm dl input[name="password"]').attr('data-required', "true").parents('dl').show();
			$('#adminForm dl input[name="passwordCheck"]').attr('data-required', "true").parents('dl').show();
			$('#adminId').remove();
			const adminIdHtml = '<input type="text" id="adminId" name="adminId" data-regex="loginId" data-required="true" placeholder="영문, 숫자 포함 25자 이하">';
			$('#adminForm .id').prepend(adminIdHtml);
			$('#adminId').on('click', () => {
				$('#adminId').attr('readonly', false);
				$('#duplicateCheckBtn').removeClass('active');
			})
			$('#duplicateCheckBtn').show();
		} else {
			$('#adminPopup .popupTitle h4').text('사용자 계정 수정');
			$('#adminForm .id #adminId').remove();
			const adminIdHtml = '<p id="adminId"></p>';
			$('#adminForm .id').append(adminIdHtml);
			$('#adminPopup .popupButton ul li[data-mode="mod"]').show();
			$('#duplicateCheckBtn').hide();

		}
		comm.showModal($('#adminPopup'));
		$('#adminPopup').css('display', 'flex');
	}
	/**
	 * 관리자 팝업 닫기
	 */
	, hideAdminPopup : () => {
		$('#adminForm').initForm();
		$('#adminPwdForm').initForm();
		$('#adminPopup .popupContents').scrollTop(0);
		comm.hideModal($('#adminPopup'));
		comm.hideModal($('#adminPwdPopup'));
		$('.inputPopup').hide();
	}
	/**
	 * 관리자 등록
	 */
	, addAdminProc : () => {
		const formObj = $('#adminForm').serializeJSON();
		formObj.birth = formObj.year + formObj.month + formObj.day;
		formObj.useFlag = $('#adminForm dl input[name="useFlag"]:checked').data('value');

		comm.ajaxPost({
				url : "/admin/addAdmin.ado"
				, type : "PUT"
				, data : formObj
			},
			(result) => {
				comm.showAlert("사용자가 등록되었습니다");
				admin.getListAdmin($('#adminTable'));
				admin.hideAdminPopup();
			});
	}
	/**
	 * 관리자 아이디 중복조회
	 */
	, checkDuplAdminId : (pAdminId) => {
		comm.ajaxPost({
				url : "/admin/checkDuplAdminId.ado"
				, data : {
					id : pAdminId
				}
			},
			(result) => {
				if(result.data.adminIdUseFlag == "N"){
					comm.showAlert("사용 가능한 아이디입니다");
					$('#adminId').attr('readonly', true);
					$('#duplicateCheckBtn').addClass('active');
				} else {
					comm.showAlert("중복된 아이디가 존재합니다");
				}
			});
	}
	/**
	 * 관리자 주소 조회
	 */
	, getAdminAddress : () => {
		new daum.Postcode({
			oncomplete: function(data) {
				let address = '';
				if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
					address = data.roadAddress;
				} else { // 사용자가 지번 주소를 선택했을 경우(J)
					address = data.jibunAddress;
				}
				$('#adminForm input[name="zipcode"]').val(data.zonecode);
				$('#adminForm input[name="address"]').val(address);
			}
			, theme : {
				bgColor: "#162525", //바탕 배경색
				searchBgColor: "#162525", //검색창 배경색
				contentBgColor: "#162525", //본문 배경색(검색결과,결과없음,첫화면,검색서제스트)
				pageBgColor: "#162525", //페이지 배경색
				textColor: "#FFFFFF", //기본 글자색
				queryTextColor: "#FFFFFF", //검색창 글자색
				//postcodeTextColor: "", //우편번호 글자색
				//emphTextColor: "", //강조 글자색
				outlineColor: "#444444" //테두리
			}
		}).open();
	}
	/**
	 * 관리자 수정
	 */
	, modAdminProc : () => {
		const formObj = $('#adminForm').serializeJSON();
		formObj.birth = formObj.year + formObj.month + formObj.day;
		formObj.useFlag = $('#adminForm dl input[name="useFlag"]:checked').data('value');

		comm.ajaxPost({
				url : "/admin/modAdmin.ado"
				, type : "PATCH"
				, data : formObj
			},
			(result) => {
				comm.showAlert("사용자가 수정되었습니다");
				admin.getListAdmin($('#adminTable'));
				admin.hideAdminPopup();
			});
	}
	/**
	 * 관리자 삭제
	 */
	, delAdminProc : (pAdminSeq) => {
		comm.ajaxPost({
				url : "/admin/delAdmin.ado"
				, type : "DELETE"
				, data : {userSeq : pAdminSeq}
			},
			(result) => {
				comm.showAlert("사용자가 삭제되었습니다");
				admin.getListAdmin($('#adminTable'));
				admin.hideAdminPopup();
			});
	}
	/**
	 * 관리자 비밀번호 수정
	 */
	, modAdminPwdProc : () => {
		const formObj = $('#adminPwdForm').serializeJSON();
		comm.ajaxPost({
				url : "/admin/modAdminPwd.ado"
				, type : "PATCH"
				, data : formObj
			},
			(result) => {
				comm.showAlert("사용자 비밀번호가 수정되었습니다");
				admin.getListAdmin($('#adminTable'));
				admin.hideAdminPopup();
			});
	}
}

/**
 * 관리자 그룹
 */
const adminGroup = {
	init : () => {
		adminGroup.getListAdminGroup($('#adminGroupTable'));
		adminGroup.getListAdminInGroup($('#adminInGroupTable'), "0");
		adminGroup.getListPermit();

		$("#adminGroupListCntSel").on('change', (e) => {
			adminGroup.getListAdminGroup($('#adminGroupTable'));
		});

		$("#addAdminGroupBtn").on('click', () => {
			adminGroup.showAdminGroupPopup("add");
		});

		$("#adminGroupPopup .popupButton li:nth-child(1)").on('click', ()=> {
			adminGroup.hideAdminGroupPopup();
		});

		$("#addAdminGroupProcBtn").on('click', () => {
			if($('#adminGroupForm').doValidation() === true) {
				adminGroup.addAdminGroupProc();
			} else {
				return false;
			}
		});

		$("#modAdminGroupProcBtn").on('click', () => {
			if($('#adminGroupForm').doValidation() === true) {
				adminGroup.modAdminGroupProc();
			} else {
				return false;
			}
		});

		$("#delAdminGroupProcBtn").on('click', () => {
			comm.confirm("해당 그룹을 제거하시겠습니까?"
				, {}
				, () => {adminGroup.delAdminGroupProc();}
			);
		});

		$("#adminGroupPopup .popupContents .popupTabButton li").on('click', (e)=> {
			const groupMenu = $(e.currentTarget).data("groupmenu");
			$(e.currentTarget).siblings().removeClass("active");
			$(e.currentTarget).addClass("active");
			$("#adminGroupPopup").find('.popupContents [data-groupcontent]').hide();
			$("#adminGroupPopup").find('.popupContents [data-groupcontent='+groupMenu+']').show();
		});

		$("#addAdminInGroupBtn").on('click', () => {
			const selectedData = $('#adminGroupTable').DataTable().row('.selected').data();
			if(typeof selectedData !== "undefined" && selectedData.userGroupSeq !== "" && selectedData.userGroupSeq !== null) {
				adminGroup.showAdminInGroupPopup(selectedData.userGroupSeq);
			} else {
				comm.showAlert("사용자 그룹을 선택해주세요");
			}
		});

		$("#adminInGroupPopup .popupButton li:nth-child(1)").on('click', ()=> {
			adminGroup.hideAdminInGroupPopup();
		});

		$("#addAdminInGroupProcBtn").on('click', () => {
			adminGroup.addAdminInGroupProc();
		});

		$("#getListAdminInGroupBtn").on('click', ()=> {
			const adminGroupSeq = $('#adminGroupTable').DataTable().row('.selected').data().userGroupSeq;
			adminGroup.getListAdminInGroupCheck($('#adminInGroupCheckTable'), adminGroupSeq);
		});

		$("#adminInGroupKeyword").on('keyup', (e)=> {
			if(e.keyCode == 13) {
				const adminGroupSeq = $('#adminGroupTable').DataTable().row('.selected').data().userGroupSeq;
				adminGroup.getListAdminInGroupCheck($('#adminInGroupCheckTable'), adminGroupSeq);
			}
		});
	},
	getListAdminGroup : ($target) => {
		const filterObj = {
			pageLength : $("#adminGroupListCntSel").val()
		};

		const optionObj = {
			dom: '<"tableBody"rt><"tableBottom"p>',
			destroy: true,
			pageLength: filterObj.pageLength,
			scrollY: "calc(100% - 6px)",
			ajax :
				{
					'url' : "/admin/getListAdminGroup.ado",
					'contentType' : "application/json; charset=utf-8",
					'type' : "POST",
					'data' : function ( d ) {
						return JSON.stringify( d );
					},
					'dataSrc' : function (result) {
						$('.tableTitle h4 #adminGroupListCnt').text(result.recordsTotal);
						return result.data;
					}
				},
			select: {
				toggleable: false
			},
			columns : [
				{data: "userGroupName"},
				{data: "adminGroupContent"},
				{data: null}
			],
			"columnDefs": [{
				"targets": -1,
				"data": null,
				"defaultContent": '<span class="tableButton"></span>'
			}]
		}

		const evt = {
			click : function(e) {
				const rowData = $target.DataTable().row($(e.currentTarget)).data();
				adminGroup.getListAdminInGroup($('#adminInGroupTable'), rowData.userGroupSeq);
				if($(e.target).hasClass('tableButton')) {
					adminGroup.showAdminGroupPopup('mod');
					if(typeof rowData.permitSeqList != "undefined" && rowData.permitSeqList !== null) {
						const permitSeqList = rowData.permitSeqList.split(",");
						permitSeqList.forEach((permitSeq, index) => {
							$('#permit'+permitSeq).prop('checked', true);
						});
					}
					$('#adminGroupForm').setItemValue(rowData);
				}
			}
		}
		comm.createTable($target ,optionObj, evt);
	},
	getListAdminInGroup : ($target, pAdminGroupSeq) => {
		const optionObj = {
			dom: '<"tableBody"rt><"tableBottom"p>',
			destroy: true,
			pageLength: 20,
			scrollY: "calc(100% - 6px)",
			ajax :
				{
					'url' : "/admin/getListAdminInGroup.ado",
					'contentType' : "application/json; charset=utf-8",
					'type' : "POST",
					'data' : function ( d ) {
						d.userGroupSeq = pAdminGroupSeq
						return JSON.stringify( d );
					},
					'dataSrc' : function (result) {
						$('.tableTitle h4 #adminInGroupCnt').text(result.recordsTotal);
						return result.data;
					}
				},
			columns : [
				{data: "id"},
				{data: "userName"},
				{data: null}
			],
			"columnDefs": [{
				"targets": -1,
				"data": null,
				"defaultContent": '<span class="tableDeleteButton"></span>'
			}],
		}

		const evt = {
			click : function(e) {
				const adminSeq = $target.DataTable().row($(e.currentTarget)).data().userSeq;
				const adminGroupSeq = $('#adminGroupTable').DataTable().row('.selected').data().userGroupSeq;
				if($(e.target).hasClass('tableDeleteButton')) {
					comm.confirm("해당 그룹 소속 사용자를 제거하시겠습니까?"
						, {}
						, () => {adminGroup.delAdminInGroupProc(adminSeq, adminGroupSeq)}
					);
				}
			},
			dblclick : function() {
			}
		}
		comm.createTable($target ,optionObj, evt);
	},
	getListAdminInGroupCheck : ($target, pAdminGroupSeq) => {
		const optionObj = {
			dom: '<"tableBody"rt>',
			destroy: true,
			scrollY: "100%",
			ajax :
				{
					'url' : "/admin/getListAdminInGroupCheck.ado",
					'contentType' : "application/json; charset=utf-8",
					'type' : "POST",
					'async' : false,
					'data' : function ( d ) {
						d.userGroupSeq = pAdminGroupSeq
						d.keyword = $('#adminInGroupKeyword').val();
						return JSON.stringify( d );
					},
					'dataSrc' : function (result) {
						return result.data;
					}
				},
			select: false,
			columns : [
				{data: null},
				{data: "id"},
				{data: "userName"}
			],
			"columnDefs": [{
				"targets": 0,
				"defaultContent": '<input class="tableCheckbox" type="checkbox"><label><span></span></label>',
			}],
			"fnCreatedRow": (nRow, aaData, iDataIndex) => {
				$(nRow).find('input').prop('id', "check"+aaData.userSeq);
				$(nRow).find('input').prop('value', aaData.userSeq);
				$(nRow).find('label').prop('for', "check"+aaData.userSeq);
				if(aaData.checked === 'checked' || $('#adminInGroupPopup').data('adminSeqList').indexOf(aaData.userSeq) > -1) {
					$(nRow).find('input').prop('checked', true);
				}
			}
		}

		const evt = {
			click : function(e) {
				if($(e.target).hasClass('tableCheckbox')) {
					const adminSeq = $(e.target).val();
					if($(e.target).prop('checked') === true) {
						$('#adminInGroupPopup').data('adminSeqList').push(adminSeq);
					} else {
						const ary = $('#adminInGroupPopup').data('adminSeqList').filter(e => e !== adminSeq);
						$('#adminInGroupPopup').data('adminSeqList', ary);
					}
				}
			}
		}
		comm.createTable($target ,optionObj, evt);
	},
	getListPermit : () => {
		comm.ajaxPost({
				url : "/admin/getListPermit.ado"
				, data : {}
			},
			(result) => {
				const resultData = result.data;
				let html = '';
				for ( let i in resultData) {
					html = "<li>"
						+ "<span>"+resultData[i].permitName+"</span>"
						+ "<span>"
						+ "<input type='radio' data-value="+resultData[i].permitSeq+" id='permit"+resultData[i].permitSeq+"' name='permitOrReject"+resultData[i].permitSeq+"'>"
						+ "<label for='permit"+resultData[i].permitSeq+"'><span class='round'>라디오버튼</span></label>"
						+ "</span>"
						+ "<span>"
						+ "<input type='radio' id='reject"+resultData[i].permitSeq+"' name='permitOrReject"+resultData[i].permitSeq+"' checked>"
						+ "<label for='reject"+resultData[i].permitSeq+"'><span class='round'>라디오버튼</span></label>"
						+ "</span>"
						+ "</li>"
					$('#adminGroupPopup .checkboxList:last').append(html);
				}
			});
	},
	showAdminGroupPopup : (type) => {
		$('#adminGroupPopup .popupContents').scrollTop(0);
		comm.showModal($('#adminGroupPopup'));
		$('#adminGroupPopup').css("display", "flex");
		$('#adminGroupPopup .popupTabButton li:first-child').trigger('click');
		$('#adminGroupPopup .checkboxList li input').not('[id*=permit]').each((index, ele)=> {
			$(ele).prop('checked', true);
		})
		$('#adminGroupForm').show();
		$('#adminGroupForm').initForm();
		$('#adminGroupPopup .popupButton ul li[data-mode]').hide();
		if(type === "add") {
			$('#adminGroupPopup .popupTitle h4').text("사용자 그룹 정보 추가");
			$('#adminGroupPopup .popupButton ul li[data-mode="'+type+'"]').show();
		} else if(type === "mod"){
			$('#adminGroupPopup .popupTitle h4').text('사용자 그룹 정보 수정');
			$('#adminGroupPopup .popupButton ul li[data-mode="'+type+'"]').show();
			$('#duplicateCheckBtn').hide();
		}
	},
	hideAdminGroupPopup : () => {
		$('#adminGroupPopup .popupContents').scrollTop(0);
		comm.hideModal($('#adminGroupPopup'));
		$('#adminGroupPopup').hide();
		$('#adminGroupForm').initForm();
	},
	showAdminInGroupPopup : (pAdminGroupSeq) => {
		$('#adminInGroupPopup').data('adminSeqList', []);
		adminGroup.getListAdminInGroupCheck($('#adminInGroupCheckTable'), pAdminGroupSeq);
		comm.showModal($('#adminInGroupPopup'));
		$('#adminInGroupPopup').css("display", "flex");
		$('#adminInGroupCheckTable').DataTable().columns.adjust();
		$('#adminInGroupCheckTable').DataTable().rows().nodes().each((ele, index)=> {
			if($(ele).find('input').prop('checked') === true) {
				$('#adminInGroupPopup').data('adminSeqList').push($(ele).find('input').val());
			}
		});
	},
	hideAdminInGroupPopup : () => {
		comm.hideModal($('#adminInGroupPopup'));
		$('#adminInGroupKeyword').val('');
		$('#adminInGroupPopup').hide();
	},

	addAdminGroupProc : () => {
		const formObj = $('#adminGroupForm').serializeJSON();
		const permitMenuSeqList = [];
		$('#adminGroupPopup .checkboxList li').each((i,e) => {
			const permitMenuSeq = $(e).find('input:checked').data('value');

			console.log("addadmingroupProc에 체크값 확인 : " + permitMenuSeq);

			// const eleId = $(e).find('input:checked').attr('id');
			if(typeof  permitMenuSeq !=="undefined"){
				permitMenuSeqList.push(permitMenuSeq)
			}

			// if (typeof permitSeq !== "undefined" && permitSeq !== "" && eleId.includes('permit')) {
			// permitSeqList.push(permitSeq);
			// }
		});
		formObj.permitSeqList = permitMenuSeqList;
		comm.ajaxPost({
				url : "/admin/addAdminGroup.ado"
				, type : "PUT"
				, data : formObj
			},
			(result) => {
				adminGroup.getListAdminGroup($('#adminGroupTable'));
				adminGroup.hideAdminGroupPopup();
				comm.showAlert("사용자 그룹이 등록되었습니다");
			});
	},
	modAdminGroupProc : () => {
		const formObj = $('#adminGroupForm').serializeJSON();
		const permitSeqList = [];
		$('#adminGroupPopup .checkboxList li').each((i,e) => {
			const permitSeq = $(e).find('input:checked').data('value');
			console.log("permitseq: " + permitSeq )
			if (typeof permitSeq !== "undefined") {
				permitSeqList.push(permitSeq);
			}
	/*		if (typeof permitSeq !== "undefined" && permitSeq !== "") {
				permitSeqList.push(permitSeq);
			}
*/		});
		formObj.permitSeqList = permitSeqList;
		comm.ajaxPost({
				url : "/admin/modAdminGroup.ado"
				, type : "PATCH"
				, data : formObj
			},
			(result) => {
				adminGroup.getListAdminGroup($('#adminGroupTable'));
				adminGroup.hideAdminGroupPopup();
				comm.showAlert("사용자 그룹이 수정되었습니다");
			});
	},
	delAdminGroupProc : () => {
		comm.ajaxPost({
				url : "/admin/delAdminGroup.ado"
				, type : "DELETE"
				, data : {userGroupSeq : $("#userGroupSeq").val()}
			},
			(result) => {
				adminGroup.getListAdminGroup($('#adminGroupTable'));
				adminGroup.getListAdminInGroup($('#adminInGroupTable'), "0");
				adminGroup.hideAdminGroupPopup();
				comm.showAlert("사용자 그룹이 삭제되었습니다");
			});
	},
	addAdminInGroupProc : () => {
		const adminSeqList = $('#adminInGroupPopup').data('adminSeqList');
		const adminGroupSeq = $('#adminGroupTable').DataTable().row('.selected').data().userGroupSeq;

		comm.ajaxPost({
				url : "/admin/addAdminInGroup.ado"
				, type : "PUT"
				, data : {adminSeqList : adminSeqList
					, userGroupSeq : adminGroupSeq}
			},
			(result) => {
				adminGroup.getListAdminInGroup($('#adminInGroupTable'), adminGroupSeq);
				adminGroup.hideAdminInGroupPopup();
				comm.showAlert("소속 사용자 목록이 등록되었습니다");
			});
	},
	delAdminInGroupProc : (pAdminSeq, pAdminGroupSeq) => {
		comm.ajaxPost({
				url : "/admin/delAdminInGroup.ado"
				, type : "DELETE"
				, data : {userGroupSeq : pAdminGroupSeq
					,	userSeq : pAdminSeq}
			},
			(result) => {
				adminGroup.getListAdminInGroup($('#adminInGroupTable'), pAdminGroupSeq);
				comm.showAlert("소속 사용자가 삭제되었습니다");
			});
	},
}
const account = {
	init : () => {
		account.getListAccount($('#accountTable'));
		
		$("#initSearchForm").on('click', (e) => {
			$("#accountKeyword").val('');
		});

		$("#getListAccountBtn").on('click', (e) => {
			account.getListAccount($('#accountTable'));
		});
		
		$("#accountListCntSel").on('change', (e) => {
			account.getListAccount($('#accountTable'));
		});
		
		$("#accountKeyword").on('keyup', (e)=> {
			if(e.keyCode == 13) {
				account.getListAccount($('#accountTable'));
			}
		});
		
		$("#initSearchForm").on('click', (e) => {
			$("#accountKeyword").val('');
		});
		
		$('.excelDownloadBtn').on('click', (e) => {
			let paramObj = {
				url : "/account/exportExcelAccount.do"
			}
			comm.downloadExcelFile(paramObj);
		});
		
	},
	/**
	 * 수용가 리스트 조회
	 */
	getListAccount : ($target) => {
		const optionObj = {
			dom: '<"tableBody"rt><"tableBottom"p>',
			destroy: true,
			pageLength: $("#accountListCntSel").val(),
            scrollY: "calc(100% - 6px)",
            scrollX: true,
            select : false,
            ajax : 
            	{
					'url' : "/account/getListAccount.ado",
					'contentType' : "application/json; charset=utf-8",
					'type' : "POST",
					'data' : function ( d ) {
						d.keyword = $("#accountKeyword").val();
						d.startDt = $("#accountStartDt").val();
						d.endDt= $("#accountEndDt").val();
						return JSON.stringify( d );
					},
					'dataSrc' : function (result) {
						$('.tableTitle h4 span').text(result.recordsTotal);
						return result.data;
					}
        	},
			columns : [
				{data: "accountNo"},
				{data: "accountNm"},
				{data: "companyNm"},
				{data: "connectDtm"},
				{data: "statusDevice"},
				{data: "stateDisplay"},
				{data: "deviceSn"},
				{data: "meterSn"},
				{data: "caliberCd"},
				{data: "mtDown"},
				{data: "mtDownDtm"},
				{data: "mtLastDtm"},
				{data: "fullAddr"},
			],
			columnDefs : [
				{
					"targets": [5, 9],
					"createdCell": function (td, cellData, rowData, row, col) {
						if ( cellData === "정상" ) {
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
				click : function() {
					//console.log(this);
				},
				dblclick : function() {
					//console.log(this);
				}
		}
		comm.createTable($target ,optionObj, evt);
	},
	/**
	 * 관제 수용가 리스트 조회
	 */
	getListAccountGis : (obj) => {
		 const paramObj = {
			 url : "/account/getListAccountGIS.ado",
			 type : "post",
			 data : obj,
			 showLoading : false
		 };			
		 comm.ajaxPost(paramObj,result => {
	
			const accountList = result.data;
		 	controlList.createList(accountList,'account');
			 
		 });
	},
	/**
	 * 검침 증가량 최대 수용가, 최소 수용가 (메인)
	 */
	loadAccountDataMinMax : () => {
		 const paramObj = {
			 url : "/account/getListAccountDataMinMax.ado",
			 data : $("#searchForm").serializeJSON(),
			 showLoading : false
		 };
		 comm.ajaxPost(paramObj, result => {
			 const data = result.data;
			 $("#accountAccountNmMin").text(data[0].accountNm);
			 $("#accountAccountNmMax").text(data[1].accountNm);
		 });
	},
	/**
	 * 검침 증가량 총 사용량 평균 사용량 (메인)
	 */
	loadAccountDataSumAvg : () => {
		 const paramObj = {
			 url : "/account/getListAccountDataSumAvg.ado",
			 data : $("#searchForm").serializeJSON(),
			 showLoading : false
		 };
		 comm.ajaxPost(paramObj, result => {
			 const data = result.data;
			 $("#accountAccountNmSum").text(data.sumDiffValue);
			 $("#accountAccountNmAvg").text(data.avgDiffValue);
		 });
	},
	/**
	 * 검침 수도 증가량 Top (메인)
	 */
	loadAccountDataTop : () => {
		 const paramObj = {
			 url : "/account/getListAccountDataTop.ado",
			 data : $("#searchForm").serializeJSON(),
			 showLoading : false
		 };
		 comm.ajaxPost(paramObj, result => {
			const data = result.data;
			let tData = [];
			let html = "";
			let $ele;

			const typeArr = ["Asc", "Desc"];

			$.each(typeArr, (x, y) => {
				html = "";
				tData = data["top"+typeArr[x]];
				$ele = $("#accountDataTop"+typeArr[x]+"Tbl");

				$ele.find("tbody").empty();
				$.each(tData, (i, v) => {
					html += "<tr>";
					html += "<td>"+(i+1)+"</td>";
					html += "<td>"+tData[i].accountNo+"</td>";
					html += "<td>"+tData[i].accountNm+"</td>";
					html += "<td>"+tData[i].diffValue+"</td>";
					html += "<td>"+tData[i].companyNm+"</td>";
					html += "</tr>";
				});
				$ele.find("tbody").append(html);
			});
		 });
	},
	/**
	 * 검침 수도 날짜별 평균 증가량 (검침 통계)
	 */
	loadAccountDataStatsChart : () => {
		comm.ajaxPost({
			url : "/account/getListAccountDataStatsChart.ado",
			data : $("#searchForm").serializeJSON(),
			showLoading : false,
			hideLoading : false,
			//async : false
		}, resultData => {
			const data = resultData.data;
			let xArr = [];
			let yArr = [];
			
			$.each(data, (i, v) => {
				xArr.push(data[i].day);
				yArr.push(data[i].avgDiffValue);
			});
			
			let options = {
				series: [
					{
						name : "날짜 증가 평균",
						data : yArr
					}
				],
				xaxis: {
					categories : xArr
				},
				chart: {
					background: '#30353f',
					toolbar : {
						show : false
					},
					zoom: {
						enabled: false
					},
					width : 1085,
					height : 250,
					type: 'line'
				},
				stroke: {
				  curve: 'smooth',
				},
				grid : {
					padding : {
						left : 30,
						right : 30
					}
				},
				colors : ["#4389F8"],
				yaxis: [
					{
						axisTicks: {
							show: true
						},
						axisBorder: {
							show: true,
							color: "#FF1654"
						},
						labels: {
							style: {
							colors: "#FFFFFF"
							}
						},
						title: {
							text: "",
							style: {
								color: "#FF1654"
							}
						}
					}
				],
				toolbar : {
					enable : false
				},
				dataLabels: {
	                enabled: false,
	            },
	            tooltip : {
	            	y :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return value;
						    }
						},
					x :  
						{
							formatter: function(value, { series, seriesIndex, dataPointIndex, w }) {
								return w.globals.categoryLabels[dataPointIndex];
						    }
						},
	            }
			}
			chart.createChart(options, "accountDataChart");
		});
	}
}

const accountGroup = {
		init : () => {
			accountGroup.getListAccountGroup($('#accountGroupTable'));
			accountGroup.getListAccountInGroup($('#accountInGroupTable'), "0");
			
			$("#accountGroupListCntSel").on('change', (e) => {
				accountGroup.getListAccountGroup($('#accountGroupTable'));
			});
			
			$("#addAccountGroupBtn").on('click', () => {
				accountGroup.showAccountGroupPopup("add");
			});
			
			$("#accountGroupPopup .popupButton li:nth-child(1)").on('click', ()=> {
				accountGroup.hideAccountGroupPopup();
			});
			
			$("#addAccountGroupProcBtn").on('click', () => {
				if($('#accountGroupForm').doValidation() === true) {
					accountGroup.addAccountGroupProc();
				} else {
					return false;
				}
			});
			
			$("#modAccountGroupProcBtn").on('click', () => {
				if($('#accountGroupForm').doValidation() === true) {
					accountGroup.modAccountGroupProc();
				} else {
					return false;
				}
			});
			
			$("#delAccountGroupProcBtn").on('click', () => {
				comm.confirm("해당 그룹을 제거하시겠습니까?"
						, {}
						, () => {accountGroup.delAccountGroupProc();}
						);
			});
			
			$("#accountGroupPopup .popupContents .popupTabButton li").on('click', (e)=> {
				const groupMenu = $(e.currentTarget).data("groupmenu");
				$(e.currentTarget).siblings().removeClass("active");
				$(e.currentTarget).addClass("active");
				$("#accountGroupPopup").find('.popupContents [data-groupcontent]').hide();
				$("#accountGroupPopup").find('.popupContents [data-groupcontent='+groupMenu+']').show();
			});
			
			$("#addAccountInGroupBtn").on('click', () => {
				const selectedData = $('#accountGroupTable').DataTable().row('.selected').data();
				console.log("selectedData??" + selectedData.accountGroupSeq );

				if(typeof selectedData !== "undefined" && selectedData.accountGroupSeq !== "" && selectedData.accountGroupSeq !== null) {
					accountGroup.showAccountInGroupPopup(selectedData.accountGroupSeq);
				} else {
					comm.showAlert("수용가 그룹을 선택해주세요");
				}
			});
			
			$("#accountInGroupPopup .popupButton li:nth-child(1)").on('click', ()=> {
				accountGroup.hideAccountInGroupPopup();
			});
			
			$("#addAccountInGroupProcBtn").on('click', () => {
				accountGroup.addAccountInGroupProc();
			});
			
			$("#getListAccountInGroupBtn").on('click', ()=> {
				const accountGroupSeq = $('#accountGroupTable').DataTable().row('.selected').data().accountGroupSeq;
				accountGroup.getListAccountInGroupCheck($('#accountInGroupCheckTable'), accountGroupSeq);
			});
			
			$("#accountInGroupKeyword").on('keyup', (e)=> {
				if(e.keyCode == 13) {
					const accountGroupSeq = $('#accountGroupTable').DataTable().row('.selected').data().accountGroupSeq;
					accountGroup.getListAccountInGroupCheck($('#accountInGroupCheckTable'), accountGroupSeq);
				}
			});
		},
		getListAccountGroup : ($target) => {
			const filterObj = {
					pageLength : $("#accountGroupListCntSel").val()
			};
			
			const optionObj = {
					dom: '<"tableBody"rt><"tableBottom"p>',
					destroy: true,
					pageLength: filterObj.pageLength,
		            scrollY: "calc(100% - 45px)",
		            ajax : 
		            	{
							'url' : "/account/getListAccountGroup.ado",
							'contentType' : "application/json; charset=utf-8",
							'type' : "POST",
							'data' : function ( d ) {
								return JSON.stringify( d );
							},
							'dataSrc' : function (result) {
								$('.tableTitle h4 #accountGroupListCnt').text(result.recordsTotal);
								return result.data;
							}
		        	},
		        	select: {
		                toggleable: false
		            },
					columns : [
						{data: "accountGroupName"},
						{data: "accountGroupContent"},
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
						accountGroup.getListAccountInGroup($('#accountInGroupTable'), rowData.accountGroupSeq);
						if($(e.target).hasClass('tableButton')) {
							accountGroup.showAccountGroupPopup('mod');
							$('#accountGroupForm').setItemValue(rowData);
						}
					}
				}
				comm.createTable($target ,optionObj, evt);
		},
		getListAccountInGroup : ($target, pAccountGroupSeq) => {
			const optionObj = {
					dom: '<"tableBody"rt><"tableBottom"p>',
					destroy: true,
					pageLength: 20,
		            scrollY: "calc(100% - 45px)",
		            ajax : 
		            	{
							'url' : "/account/getListAccountInGroup.ado",
							'contentType' : "application/json; charset=utf-8",
							'type' : "POST",
							'data' : function ( d ) {
								d.accountGroupSeq = pAccountGroupSeq
								return JSON.stringify( d );
							},
							'dataSrc' : function (result) {
								$('.tableTitle h4 #accountInGroupCnt').text(result.recordsTotal);
								return result.data;
							}
		        	},
					columns : [
						{data: "accountNo"},
						{data: "accountNm"},
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
							const accountNo = $target.DataTable().row($(e.currentTarget)).data().accountNo;
							const accountGroupSeq = $('#accountGroupTable').DataTable().row('.selected').data().accountGroupSeq;
							if($(e.target).hasClass('tableDeleteButton')) {
								comm.confirm("해당 그룹 소속 수용가를 제거하시겠습니까?"
											, {}
											, () => {accountGroup.delAccountInGroupProc(accountNo, accountGroupSeq)}
											);
							}
						},
						dblclick : function() {
						}
				}
				comm.createTable($target ,optionObj, evt);
		},
		getListAccountInGroupCheck : ($target, pAccountGroupSeq) => {
			const optionObj = {
					dom: '<"tableBody"rt>',
					destroy: true,
		            scrollY: "100%",
		            ajax : 
		            	{
							'url' : "/account/getListAccountInGroupCheck.ado",
							'contentType' : "application/json; charset=utf-8",
							'type' : "POST",
							'async' : false,
							'data' : function ( d ) {
								d.accountGroupSeq = pAccountGroupSeq
								d.keyword = $('#accountInGroupKeyword').val();
								return JSON.stringify( d );
							},
							'dataSrc' : function (result) {
								return result.data;
							}
		        	},
		        	select: false,
					columns : [
						{data: null},
						{data: "accountNo"},
						{data: "accountNm"}
					],
					"columnDefs": [{
						"targets": 0,
						"defaultContent": '<input class="tableCheckbox" type="checkbox"><label><span></span></label>',
			        }],
			        "fnCreatedRow": (nRow, aaData, iDataIndex) => {
			        	$(nRow).find('input').prop('id', "check"+aaData.accountNo);
			        	$(nRow).find('input').prop('value', aaData.accountNo);
			        	$(nRow).find('label').prop('for', "check"+aaData.accountNo);
			        	if(aaData.checked === 'checked'  || $('#accountInGroupPopup').data('accountNoList').indexOf(aaData.accountNo) > -1) {
			        		$(nRow).find('input').prop('checked', true);
			        	}
			        }
				}
			
				const evt = {
						click : function(e) {
							if($(e.target).hasClass('tableCheckbox')) {
								const accountNo = $(e.target).val();
								if($(e.target).prop('checked') === true) {
									$('#accountInGroupPopup').data('accountNoList').push(accountNo);
								} else {
									const ary = $('#accountInGroupPopup').data('accountNoList').filter(e => e !== accountNo);
									$('#accountInGroupPopup').data('accountNoList', ary);
								}
							}
						}
				}
				comm.createTable($target ,optionObj, evt);
		},
		showAccountGroupPopup : (type) => {
			$('#accountGroupPopup .popupContents').scrollTop(0);
			comm.showModal($('#accountGroupPopup'));
			$('#accountGroupPopup').css("display", "flex");
			$('#accountGroupPopup .popupTabButton li:first-child').trigger('click');
			$('#accountGroupForm').show();
			$('#accountGroupForm').initForm();
			$('#accountGroupPopup .popupButton ul li[data-mode]').hide();
			if(type === "add") {
				$('#accountGroupPopup .popupTitle h4').text("수용가 그룹 정보 추가");
				$('#accountGroupPopup .popupButton ul li[data-mode="'+type+'"]').show();
			} else if(type === "mod"){
				$('#accountGroupPopup .popupTitle h4').text('수용가 그룹 정보 수정');
				$('#accountGroupPopup .popupButton ul li[data-mode="'+type+'"]').show();
				
				$('#duplicateCheckBtn').hide();
			}
		},
		hideAccountGroupPopup : () => {
			$('#accountGroupPopup .popupContents').scrollTop(0);
			comm.hideModal($('#accountGroupPopup'));
			$('#accountGroupPopup').hide();
			$('#accountGroupForm').initForm();
		},
		showAccountInGroupPopup : (pAccountGroupSeq) => {
			$('#accountInGroupPopup').data('accountNoList', []);
			accountGroup.getListAccountInGroupCheck($('#accountInGroupCheckTable'), pAccountGroupSeq);
			comm.showModal($('#accountInGroupPopup'));
			$('#accountInGroupPopup').css("display", "flex");
			$('#accountInGroupCheckTable').DataTable().columns.adjust();
			$('#accountInGroupCheckTable').DataTable().rows().nodes().each((ele, index)=> {
				if($(ele).find('input').prop('checked') === true) {
					$('#accountInGroupPopup').data('accountNoList').push($(ele).find('input').val());
				}
			});
		},
		hideAccountInGroupPopup : () => {
			comm.hideModal($('#accountInGroupPopup'));
			$('#accountInGroupKeyword').val('');
			$('#accountInGroupPopup').hide();
		},
		addAccountGroupProc : () => {
			const formObj = $('#accountGroupForm').serializeJSON();
			comm.ajaxPost({
				url : "/account/addAccountGroup.ado"
				, type : "PUT"
				, data : formObj
			},
			(result) => {
				accountGroup.getListAccountGroup($('#accountGroupTable'));
				accountGroup.hideAccountGroupPopup();
				comm.showAlert("수용가 그룹이 등록되었습니다");
			});
		},
		modAccountGroupProc : () => {
			const formObj = $('#accountGroupForm').serializeJSON();
			comm.ajaxPost({
				url : "/account/modAccountGroup.ado"
				, type : "PATCH"
				, data : formObj
			},
			(result) => {
				accountGroup.getListAccountGroup($('#accountGroupTable'));
				accountGroup.hideAccountGroupPopup();
				comm.showAlert("수용가 그룹이 수정되었습니다");
			});
		},
		delAccountGroupProc : () => {
			comm.ajaxPost({
				url : "/account/delAccountGroup.ado"
				, type : "DELETE"
				, data : {accountGroupSeq : $("#accountGroupSeq").val()}
			},
			(result) => {
				accountGroup.getListAccountGroup($('#accountGroupTable'));
				accountGroup.getListAccountInGroup($('#accountInGroupTable'), "0");
				accountGroup.hideAccountGroupPopup();
				comm.showAlert("수용가 그룹이 삭제되었습니다");
			});
		},
		addAccountInGroupProc : () => {
			const accountNoList = $('#accountInGroupPopup').data('accountNoList');
			const accountGroupSeq = $('#accountGroupTable').DataTable().row('.selected').data().accountGroupSeq;
			comm.ajaxPost({
				url : "/account/addAccountInGroup.ado"
				, type : "PUT"
				, data : {accountNoList : accountNoList
						, accountGroupSeq : accountGroupSeq}
			},
			(result) => {
				accountGroup.getListAccountInGroup($('#accountInGroupTable'), accountGroupSeq);
				accountGroup.hideAccountInGroupPopup();
				comm.showAlert("소속 수용가 목록이 등록되었습니다");
			});
		},
		delAccountInGroupProc : (pAccountNo, pAccountGroupSeq) => {
			comm.ajaxPost({
				url : "/account/delAccountInGroup.ado"
				, type : "DELETE"
				, data : {accountGroupSeq : pAccountGroupSeq
						  ,	accountNo : pAccountNo}
			},
			(result) => {
				accountGroup.getListAccountInGroup($('#accountInGroupTable'), pAccountGroupSeq);
				comm.showAlert("소속 수용가가 삭제되었습니다");
			});
		},
}

const accountData = {
	init : () => {
		accountData.getListAccountData($('#accountDataTable'));
		
		$("#initSearchForm").on('click', (e) => {
			$("#accountDataKeyword").val('');
		});

		$("#getListAccountDataBtn").on('click', (e) => {
			accountData.getListAccountData($('#accountDataTable'));
		});
		
		$("#accountDataListCntSel").on('change', (e) => {
			accountData.getListAccountData($('#accountDataTable'));
		});
		
		$("#accountDataKeyword").on('keyup', (e)=> {
			if(e.keyCode == 13) {
				accountData.getListAccountData($('#accountDataTable'));
			}
		});
		
		$("#initSearchForm").on('click', (e) => {
			$("#accountDataKeyword").val('');
		});
		
		$('.excelDownloadBtn').on('click', (e) => {
			let paramObj = {
				url : "/account/exportExcelAccountData.do"
			}
			comm.downloadExcelFile(paramObj);
		});
	},
	/**
	 * 검침 리스트 조회
	 */
	getListAccountData : ($target) => {
		const optionObj = {
			dom: '<"tableBody"rt><"tableBottom"p>',
			destroy: true,
			pageLength: $("#accountDataListCntSel").val(),
            scrollY: "calc(100% - 6px)",
            select : false,
            ajax : 
            	{
					'url' : "/account/getListAccountData.ado",
					'contentType' : "application/json; charset=utf-8",
					'type' : "POST",
					'data' : function ( d ) {
						d.keyword = $("#accountDataKeyword").val();
						d.startDt = $("#accountDataStartDt").val();
						d.endDt = $("#accountDataEndDt").val();
						return JSON.stringify( d );
					},
					'dataSrc' : function (result) {
						$('.tableTitle h4 span').text(result.recordsTotal);
						return result.data;
					}
        	},
			columns : [
				{data: "accountNo"},
				{data: "meterDtm"},
				{data: "value"},
				{data: "digits"},
				{data: "leakState"},
				{data: "termBatt"},
				{data: "mLowBatt"},
				{data: "mLeak"},
				{data: "mOverload"},
				{data: "mReverse"},
				{data: "mNotUse"},
			],
			columnDefs : [
				{
					"targets": [4,6,7,8,9,10],
					"createdCell": function (td, cellData, rowData, row, col) {
						if ( cellData === "O" ) {
							$(td).css('color', '#f65656')
						} else {
							$(td).css('color', '#409cfb')
						}
					}
				}
			],
			excelDownload : true
		}
		const evt = {
				click : function() {
					//console.log(this);
				},
				dblclick : function() {
					//console.log(this);
				}
		}
		comm.createTable($target ,optionObj, evt);
	}
}


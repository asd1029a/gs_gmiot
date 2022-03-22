/**
 * 공지사항
 */

const board = {
		init : () => {
			board.getListBoard($('#boardTable'));
			
			$("#initSearchForm").on('click', (e) => {
				$("#boardKeyword").val('');
			});
			
			$("#getListBoardBtn").on('click', (e) => {
				board.getListBoard($('#boardTable'));
			});
			
			$("#boardListCntSel").on('change', (e) => {
				board.getListBoard($('#boardTable'));
			});
			
			$("#boardKeyword").on('keyup', (e)=> {
				if(e.keyCode == 13) {
					board.getListBoard($('#boardTable'));
				}
			});
			
			$("#addBoardBtn").on('click', () => {
				board.showBoardPopup("add");
			});
			
			$("#boardPopup .popupButton li:nth-child(1)").on('click', ()=> {
				board.hideBoardPopup();
			});
			
			$("#addBoardProcBtn").on('click', () => {
				if($('#boardForm').doValidation() === true) {
					board.addBoardProc();
				} else {
					return false;
				}
			});
			
			$("#modBoardProcBtn").on('click', () => {
				if($('#boardForm').doValidation() === true) {
					board.modBoardProc();
				} else {
					return false;
				}
			});
			
			$("#delBoardProcBtn").on('click', () => {
				comm.confirm(
					"공지사항을 삭제하시겠습니까?"
					, {}
					, () => {
						const boardSeq = $('#boardSeq').val();
						board.delBoardProc(boardSeq);
					}
				);
			});
			
			$('.excelDownloadBtn').on('click', (e) => {
				let paramObj = {
					url : "/board/exportExcelBoard.do"
				}
				comm.downloadExcelFile(paramObj);
			});
		},
		getListBoard : ($target) => {
			const filterObj = {
					keyword : $("#boardKeyword").val(),
					pageLength : $("#boardListCntSel").val()
			};
			
			const optionObj = {
					dom: '<"tableBody"rt><"tableBottom"p>',
					destroy: true,
					pageLength: filterObj.pageLength,
		            scrollY: "calc(100% - 45px)",
		            ajax : 
		            	{
							'url' : "/board/getListBoard.ado",
							'contentType' : "application/json; charset=utf-8",
							'type' : "POST",
							'data' : function ( d ) {
								d.keyword = filterObj.keyword;
								return JSON.stringify( d );
							},
							'dataSrc' : function (result) {
								$('.tableTitle h4 #boardListCnt').text(result.recordsTotal);
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
					],
					"columnDefs": [{
						"targets": -1,
						"data": null,
						"defaultContent": '<span class="tableButton"></span>'
			        }
					, {
						targets: 0,
				        render: $.fn.dataTable.render.ellipsis( 30, true )
					}
					, {
						targets: 1,
				        render: $.fn.dataTable.render.ellipsis( 50, true )
					}]
		            , excelDownload : true
				}

				const evt = {
						click : function(e) {
							const rowData = $target.DataTable().row($(e.currentTarget)).data();
							if($(e.target).hasClass('tableButton')) {
								board.showBoardPopup('mod');
								$('#boardForm').setItemValue(rowData);
							}
						}
				}
				comm.createTable($target ,optionObj, evt);
		},
		getListBoardForMain : (callback) => {
			comm.ajaxPost({
				url : "/board/getListBoardForMain.ado"
				, data : {}
			}, (result) => {
				callback(result);
			});
		},
		showBoardPopup : (type) => {
			$('#boardPopup .popupContents').scrollTop(0);
			comm.showModal($('#boardPopup'));
			$('#boardPopup').css("display", "flex");
			$('#boardPopup').show();
			$('#boardForm').initForm();
			$('#boardPopup [data-mode]').hide();
			if(type === "add") {
				$('#boardPopup .popupTitle h4').text("공지사항 게시글 등록");
				$('#boardPopup').css('height', '480px');
				$('#boardPopup [data-mode="'+type+'"]').show();
			} else if(type === "mod"){
				$('#boardPopup .popupTitle h4').text('공지사항 게시글 수정');
				$('#boardPopup').css('height', '780px');
				$('#boardPopup [data-mode="'+type+'"]').show();
			}
		},
		hideBoardPopup : () => {
			$('#boardPopup .popupContents').scrollTop(0);
			comm.hideModal($('#boardPopup'));
			$('#boardPopup').hide();
		},
		addBoardProc : () => {
			const formObj = $('#boardForm').serializeJSON();
			
			comm.ajaxPost({
				url : "/board/addBoard.ado"
				, type : "PUT"
				, data : formObj
			},
			(result) => {
				comm.showAlert("공지사항이 등록되었습니다");
				board.getListBoard($('#boardTable'));
				board.hideBoardPopup();
			});
		},
		modBoardProc : () => {
			const formObj = $('#boardForm').serializeJSON();
			
			comm.ajaxPost({
				url : "/board/modBoard.ado"
				, type : "PATCH"
				, data : formObj
			},
			(result) => {
				comm.showAlert("공지사항이 수정되었습니다");
				board.getListBoard($('#boardTable'));
				board.hideBoardPopup();
			});
		},
		delBoardProc : (pBoardSeq) => {
			comm.ajaxPost({
				url : "/board/delBoard.ado"
				, type : "DELETE"
				, data : {boardSeq : pBoardSeq}
			},
			(result) => {
				comm.showAlert("공지사항이 삭제되었습니다");
				board.getListBoard($('#boardTable'));
				board.hideBoardPopup();
			});
		}
}
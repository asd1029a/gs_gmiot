/* popup.js */

var dialogManager;
$(function() {
	var list = new Map();
	/**
	 * 지도의 마커와 연결선이 이어지고, 크기 조절 및 위치 이동이 가능한 팝업을 생성하고 관리한다.
	 * @author - 
	 * @version 0.0.1
	 * @class connectDialog
	 * @property {object} layer - openlayer vector layer
	 * @example
	 * // JQuery function으로 지정되어 있어 아래와 같이 호출해야함.
	 * // $.connectDialog(header, body, draggable, clickable, option)
	 * const dialog = $.connectDialog(undefined, popupVideo, true, true, dialogOption);
	 * */
	var manager = {
		list : list,
		layer : undefined,
		/**
		 * connectDialog init 함수.
		 * @function connectDialog.init
		 * @returns {object} - this
		 * */
		init : function() {
			const source = new ol.source.Vector();
			this.layer = mapManager.createVectorLayer('connectLine', this.connectLineStyle, source);

			try {
				mapManager.setMapEventListener('postrender', function(e){
					manager.mapMoveEvent();
				})
			} catch(e) {
				console.log(e);
			}
			
			return this;
		},
		connectLineStyle : function(feature) {
			const fcltPtz = feature.get('name');
			
			const color = fcltPtz == '1' ? '#4abcb5' : '#ff956b';

			return new ol.style.Style({
				stroke : new ol.style.Stroke({
					color : color,
					width : 4,
					lineDash : [ .1, 5 ]
				})
			})
		},
		/**
		 * 맵을 이동시 마커으 위치가 이동되기 때문에 연결선을 다시 그려주는 함수.
		 * @function connectDialog.mapMoveEvent
		 * */
		mapMoveEvent : function() {
			list.forEach(function(val, key, map) {
				const dialog = val;
				const data = val.data('data');
				const lineFeature = val.data('lineFeature');
				const dLeft = parseInt(dialog.css('left'));
				const dTop = parseInt(dialog.css('top'));
				const left = dLeft
						+ (dialog.outerWidth() / 2);
				const top = dTop
						+ (dialog.outerHeight() / 2);

				manager.setLineFeatureGeom(data, lineFeature, left, top);
			});
		},
		/**
		 * connectDialog의 위치를 자동으로 정렬시켜주는 함수.
		 * @function connectDialog.sortDialog
		 * */
		sortDialog : function() {
			if(list.length <= 0) return;
			
			var leftCount = 0;
			var rightCount = 0;
			const center = mapManager.map.getView().getCenter();
			var count = 0;
			list.forEach(function(val, key, map) {
				const dialog = val;
				const width = 404;
				const height = 326;
				var elTop = 0, elLeft = 0, top = 0, left = 0;
				const rest = count % 2;
				const quotient = Math.trunc(count / 2);
				dialog.width('400px');
				dialog.height('320px');
				const right = $('#map').width() - width;
				if(quotient > 2 && rest == 0) {
					elLeft = width;
					elTop = height * (quotient - 3);
				} else if(quotient > 2 && rest == 1) {
					elLeft = right - width;
					elTop = height * (quotient - 3);
				} else if(rest == 0) {
					elTop = height * quotient;
				} else if (rest == 1) {
					elLeft = right;
					elTop = height * quotient;
				}
				
				top = elTop + (height / 2);
				left = elLeft + (width / 2);
				
				manager.setDialogPosition(val, elTop, elLeft, top, left);
				count++;
			});
		},
		/**
		 * 연결선의 스타일 지정 함수.
		 * @function connectDilaog.setFeatureStyle
		 * @param {object} lineFeature - 지도 line feature 데이터
		 * @param {string} color - 라인 색상
		 * */
		setFeatureStyle : function(lineFeature, color) {
			const overStyle = new ol.style.Style({
				stroke : new ol.style.Stroke({
					color : color,
					width : 4,
					lineDash : [ .1, 5 ]
				})
			});

			lineFeature.setStyle(overStyle);
		},
		/**
		 * 연결선의 좌표 데이터 세팅 함수.
		 * @function connectDialog.setLineFeatureGeom
		 * @param {object} feature - 연결선이 연결 될 지도 마커 데이터
		 * @param {object} lineFeature - 지도 line feature 데이터
		 * @param {number} left - left value
		 * @param {number} top - top value 
		 * */
		setLineFeatureGeom : function(data, lineFeature, left, top) {
			const fPoint = ol.proj.transform([data.lon, data.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
			const elPoint = mapManager.map
					.getCoordinateFromPixel([ left, top ]);

			const line = [ fPoint, elPoint ];

			lineFeature.setGeometry(new ol.geom.LineString(line));
		},
		/**
		 * Connect Dialog 생성 함수 
		 * @function connectDialog.createDialog
		 * @param {boolean} draggable - 팝업 이동 여부(true : 이동가능, false : 이동불가)
		 * @param {object} option - dialog option
		 * @param {object} option.feature - 연결선과 연결될 지도 마커 데이터
		 * @param {object} option.css - dialog에 적용할 css 데이터
		 * @param {object} option.data - 카메라 데이터
		 * @returns {object} dialog object
		 * */
		createDialog : function(option) {
			const data = option.data;
				
			const listObj = {};

			const fPoint = ol.proj.transform([data.lon, data.lat], mapManager.properties.pro4j[mapManager.properties.type], mapManager.properties.projection);
			const fPixel = mapManager.map.getPixelFromCoordinate(fPoint);

			const left = fPixel[0];
			const top = fPixel[1];

			option.css.left = left + 'px';
			option.css.top = top + 'px';
			
			const color = option.data.cctvAgYn == '1' ? '#4abcb5' : '#ff956b';
			
			option.css.border = '2px solid ' + color;
			//option.css.borderTop = '4px solid ' + color;

			const elPoint = mapManager.map
					.getCoordinateFromPixel([ left, top ]);

			const line = [ fPoint, elPoint ];

			const lineFeature = new ol.Feature({
				name : option.data.cctvAgYn,
				geometry : new ol.geom.LineString(line),
				visible : true,
				style : new ol.style.Style({
					stroke : new ol.style.Stroke({
						color : color,
						width : 4,
						lineDash : [ .1, 5 ]
					})
				})
			});

			const source = this.layer.getSource();

			source.addFeature(lineFeature);
			
			const dialog = $('<div>').addClass('dialog').css(option.css).bind({
				'mouseover' : function() {
					manager.setFeatureStyle(lineFeature, '#FF0000');
				},
				'mouseleave' : function() {
					if($(dialog).hasClass('active')) return;
					manager.setFeatureStyle(lineFeature, color);
				},
				'click' : function(e) {
					const selectedOverlay = manager.setDialogSelectedOverlay(fPoint);
					
					dialog.data('selectedOverlay', selectedOverlay);
					
					$('.dialog.active').removeClass('active');
					$('.dialog').mouseleave();
					
					dialog.addClass('active');
					manager.setFeatureStyle(lineFeature, '#FF0000');
					
					window.onkeydown = function(e) {
						if(e.keyCode !== 27) return;
						if(confirm("선택된 영상을 종료하시겠습니까?")) {
							dialog.children('.close').click();
							window.onkeydown = undefined;
						}
					}
				}
			});
			
			dialog.data('data', data);
			dialog.data('lineFeature', lineFeature);
			dialog.data('option', option);

			list.set(option.data.fcltId, dialog);

			return dialog;
		},
		/**
		 * connect dialog의 위치를 조정하는 함수.
		 * @function connectDialog.setDialogPosition
		 * @param {object} obj - dialog object
		 * @param {number} elTop - element top position value
		 * @param {number} elLeft - element left position value
		 * @param {number} top - top position value
		 * @param {number} left - left position value
		 * */
		setDialogPosition : function(dialog, elTop, elLeft, top, left) {
			dialog.css({top : elTop + 'px', left : elLeft + 'px'});
		},
		/**
		 * 개소감시 팝업에 닫기 버튼 추가 함수(미사용)
		 * @function connectDialog.addCloseBtnForSitePopup
		 * @param {object} - dialog object
		 * */
		addCloseBtnForSitePopup : function(obj) {
			const closeBtn = document.createElement('a');
			closeBtn.classList.add('close');
			
			const img = document.createElement('img');
			img.src = '/images/icons/popup_close.png';
			
			closeBtn.addEventListener('click' ,function() {
				manager.closeSite(obj);
			});
			
			closeBtn.appendChild(img);
			obj.dialog.appendChild(closeBtn);
		},
		/**
		 * connect dialog에 닫기 버튼 추가 함수(미사용)
		 * @function connectDialog.addCloseBtn
		 * @param {object} - dialog object
		 * */
		addCloseBtn : function(dialog) {
			const img = $('<img>').attr('src', '/images/icons/icon_closed.png');
			
			const closeBtn = $('<a>').addClass('close').bind('click' ,function() {
				manager.close(dialog);
			}).append(img);
			
			dialog.append(closeBtn);
		},
		/**
		 * connect dialog에 header element 추가 함수
		 * @function connectDialog.addHeader
		 * @param {element} dialog - dialog element
		 * @param {element} header - header element
		 * */
		addHeader : function(dialog, header) {
			dialog.append(header);
		},
		/**
		 * connect dialog에 element 추가 함수
		 * @function connectDialog.append
		 * @param {element} dialog - dialog element
		 * @param {element} header - header element
		 * */
		append : function(dialog, child) {
			dialog.append(child);
		},
		/**
		 * connect dialog에 drag 이벤트 추가 함수
		 * @function connectDialog.addDragEvents
		 * @param {object} feature - 연결선이 연결 될 지도 마커 데이터
		 * @param {element} dialog - dialog element
		 * @param {object} lineFeature - 지도 line feature 데이터
		 * */
		addDragEvents : function(data, dialog, lineFeature) {
			let pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0, left = 0, top = 0, elTop = 0, elLeft = 0;
			dialog.bind('mousedown', function(e) {
				e = e || window.event;
				e.preventDefault();
				pos3 = e.clientX;
				pos4 = e.clientY;
				document.onmouseup = closeDragElement;
				document.onmousemove = elementDrag;
			});

			function elementDrag(e) {
				e = e || window.event;
				e.preventDefault();
				pos1 = pos3 - e.clientX;
				pos2 = pos4 - e.clientY;
				pos3 = e.clientX;
				pos4 = e.clientY;
				const xLeft = 0;
				const xRight = $('#map').width();
				const yTop = 0;
				const yBottom = $('#map').height();
				const width = dialog.outerWidth();
				const height = dialog.outerHeight();
				const dLeft = parseInt(dialog.css('left'));
				const dTop = parseInt(dialog.css('top'));
				
				// 좌우측 최대 최소 설정
				if(xRight <= ((dLeft - pos1) + width)) {
					elLeft = xRight - width;
					left = elLeft + (width / 2);
				} else if (xLeft >= ((dLeft - pos1))) {
					elLeft = 0;
					left = elLeft + (width / 2);
				} else {
					elLeft = (dLeft - pos1);
					left = elLeft + (width / 2);
				}
				
				// 상하단 최대 최소 설정
				if(yBottom <= ((dTop - pos2) + height)) {
					elTop = yBottom - height;
					top = elTop + (height / 2);
				} else if(yTop >= ((dTop - pos2))) {
					elTop = 0;
					top = elTop + (height / 2);
				} else {
					elTop = (dTop - pos2);
					top = elTop + (height / 2);
				}
				
				dialog.css({top : elTop + 'px', left : elLeft + 'px'});
				manager.setLineFeatureGeom(data, lineFeature, left, top);
			}

			function closeDragElement() {
				document.onmouseup = null;
				document.onmousemove = null;
			}
		},
		setDialogSelectedOverlay : function (position) {
			const content = manager.createDialogSelectedOverlay();
			const option = {
				id : 'dialogSelected',
				position : position,
				element : content,
				offset : [ 0, 0 ],
				positioning : 'center-center',
				stopEvent : false,
				insertFirst : true
			}
			
			return mapManager.setOverlay(option);
		},
		createDialogSelectedOverlay : function() {
			let wrap = document.createElement('div');
			
			wrap.classList.add('dialog-selected-overlay');
			
			let img = document.createElement('img');
			img.src = '/images/icons/dialog_selected.gif';
			
			wrap.appendChild(img);
			
			return wrap;
		},
		/**
		 * connect dialog 닫기 함수
		 * @function connectDialog.close
		 * @param {jQuery} dialog - dialog object
		 * */
		close : function(dialog) {
			mapManager.removeOverlay(dialog.data('selectedOverlay'));
			this.layer.getSource().removeFeature(dialog.data('lineFeature'));
			list.delete(dialog.data('option').data.fcltId);
			dialog.remove();
			//videoManager.removeData(obj.option.data);
		},
		closeAll : function() {
			list.forEach(function(val, key, map) {
				const dialog = val;
				dialog.children('.close').click();
			});
			
			mapManager.removeOverlayById('dialogSelected');
		}
	}

	dialogManager = manager.init();
});

$.connectDialog = function(option) {
	const clickable = option.clickable;
	const draggable = option.draggable;
	const header = option.header;
	const body = option.body;
	const dialog = dialogManager.createDialog(option);
	
	if(!dialog) return;
	
	if(header) {
		dialogManager.addHeader(dialog, header);
	}

	if(body) {
		dialogManager.append(dialog, body);
	}

	if(draggable) {
		dialog.addClass('draggable').draggable({
			containment: $('#map'),
			stop: function(event, ui){
				
			}
		})
		//dialogManager.addDragEvents(dialog.data('data'), dialog, dialog.data('lineFeature'));
	}

	dialog.resizable({
		autoHide: true,
		minWidth: parseInt(option.css.width),
		minHeight: parseInt(option.css.height),
	});
	
	dialogManager.addCloseBtn(dialog, option);
	
	if($('#btnVideoTransparent').hasClass('active')) {
		dialog.addClass('transparent');
	}

	$('#map').append(dialog);

	return dialog;
}
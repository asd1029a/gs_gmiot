/* heatmap.js */

(function($){
	/**
	 * heatmaps 생성용 class
	 * @author - 
	 * @version 0.0.1
	 * @class heatmaps
	 * @property {obejct} prop - heatmaps 관련 property
	 * @property {string} prop.id - heatmaps를 생성 할 element id
	 * @property {Array} prop.x - x axis 데이터 배열
	 * @property {Array} prop.y - y axis 데이터 배열
	 * @property {string} prop.width - width 값(500px)
	 * @property {string} prop.height - height 값(500px)
	 * @property {number} prop.itemsOnPage - 한 페이지에 표시 될 y 데이터 개수
	 * @property {number} prop.itmes - y 데이터 총 개수
	 * @property {number} prop.displayedPages - 표시할 페이지 개수
	 * @property {boolean} prop.isTooltip - tooltip 사용 여부
	 * @property {Array} prop.colors - 사용 될 색상 값 배열 데이터
	 * @property {Array} prop.legends - 색상 별 범위 지정 배열 데이터
	 * @property {string} prop.xLabel - x axis 대표 라벨
	 * @property {string} prop.yLabel - y axis 대표 라벨
	 * @example 
	 * 
	 * const option = {
	 * 	x : x, // x data list
	 * 	y : y, // y data list
	 * 	data : data, // data list
	 * 	id : 'heatmapsDisplayId',
	 * 	width : '500px',
	 * 	height: '500px',
	 * 	itemsOnPage : 150,
	 * 	items : 500,
	 * 	displayedPages : 10,
	 * 	isTooltip : true,
	 * 	colors : ['#051923', '#003554', '#006494', '#0582ca', '#00a6fb'],
	 * 	legends : ['0%', '1% ~ 87%', '88% ~ 99%', '100%'],
	 * 	yLabel : '관리번호',
	 * 	xLabel : '날짜'
	 * };
	 * 
	 * const heatmap = $("#oprtStorageHeatmap").customHeatmap(option);
	 * */
	const heatmaps = {
		prop : {
			id : undefined,
			x : [],
			y : [],
			width : undefined,
			height : undefined,
			data : undefined,
			itemsOnPage : 10,
			items : 0,
			displayedPages : 5,
			isTooltip : false,
			colors : ['#051923', '#003554', '#006494', '#0582ca', '#00a6fb'],
    		legends : ['0%', '1% ~ 87%', '88% ~ 99%', '100%'],
			yLabel : undefined,
			xLabel : undefined
		},
		/**
		 * 기본 property 세팅 함수.
		 * @function heatmaps.setProp
		 * @param {object} option - 기본 세팅 될 option
		 * @see {@link heatmaps.prop} - heatmaps.prop
		 * */
		setProp : function(option) {
			const prop = $.extend(heatmaps.prop, option);
			heatmaps.prop = prop;
		},
		/**
		 * init 함수.
		 * @function heatmaps.init
		 * @param {object} option - 기본 세팅 될 option
		 * @returns {heatmaps} - heatmaps를 생성하고 return 한다
		 * @see {@link heatmaps.prop} - heatmaps.prop
		 * */
		init: function(option) {
			heatmaps.setProp(option);
			//heatmaps.createPagenation();
			
			if(option.isTooltip) {
				heatmaps.createTooltip();
			}
			
			return this;
		},
		/**
		 * tooltip show 함수.
		 * @function heatmaps.showTooltip
		 * */
		showTooltip : function() {
			const tooltip = heatmaps.tooltip;
			tooltip.style.display = 'flex';
		},
		/**
		 * tooltip hide 함수.
		 * @function heatmaps.hideTooltip
		 * */
		hideTooltip : function() {
			const tooltip = heatmaps.tooltip;
			tooltip.style.display = 'none';
		},
		/**
		 * tooltip 위치 지정 함수.
		 * @function heatmaps.setTooltipPosition
		 * @param {number} left - left position 값
		 * @param {number} top - top position 값
		 * */
		setTooltipPosition : function(left, top) {
			const tooltip = heatmaps.tooltip;
			tooltip.style.left = left + 'px';
			tooltip.style.top = top + 'px';
		},
		/**
		 * tooltip에 표시할 데이터 지정 함수.
		 * @function heatmaps.setTooltipData
		 * @param {string} key - key label
		 * @param {number|string} val - 표시할 데이터 값
		 * */
		setTooltipData : function(key, val) {
			const tooltip = heatmaps.tooltip;
			
			//const text = documenet.createElement('div');
			
			tooltip.innerHTML = val + key;
			
			/*for (let [key, value] of Object.entries(data)) {
				console.log(`${key}: ${value}`);
			}*/
		},
		/**
		 * tooltip 생성 함수.
		 * @function heatmaps.createTooltip
		 * */
		createTooltip : function() {
			const tooltip = document.createElement('div');
			//tooltip.style.display = 'none';
			tooltip.classList.add('heatmap_tooltip');
			tooltip.style.display =  'none';
			heatmaps.tooltip = tooltip;
			
			tooltip.addEventListener('mouseleave', function(e) {
				const tooltip = heatmaps.tooltip;
				heatmaps.hideTooltip();
			});
			
			document.body.appendChild(tooltip);
		},
		/**
		 * y label 생성 함수.
		 * @function heatmaps.createYLabel
		 * @param {element} parent - 부모 element
		 * */
		createYLabel : function(parent) {
			if(heatmaps.prop.yLabel === undefined) return;
			
			const yLabel = document.createElement('div');
			yLabel.classList.add('y_label');
			yLabel.innerHTML = '(' + heatmaps.prop.yLabel + ')';
			
			parent.appendChild(yLabel);
			
		},
		/**
		 * x label 생성 함수.
		 * @function heatmaps.createYLabel
		 * @param {element} parent - 부모 element
		 * */
		createXLabel : function(parent) {
			if(heatmaps.prop.yLabel === undefined) return;
			
			const xLabel = document.createElement('div');
			xLabel.classList.add('x_label');
			xLabel.innerHTML = '(' + heatmaps.prop.xLabel + ')';
			
			parent.appendChild(xLabel);
			
		},
		/**
		 * pagenation 생성 함수.
		 * @function heatmaps.createPagenation
		 * @deprecated 미사용
		 * */
		createPagenation : function() {
			$('#'+heatmaps.prop.pagenationId).simplePagination({
				items: heatmaps.prop.items,
				itemsOnPage : heatmaps.prop.itemsOnPage,
				displayedPages : heatmaps.prop.displayedPages,
				edges : 0,
				prevText : "<",
				nextText : ">",
				cssStyle: 'custom-theme',
				onInit : function (){
					heatmaps.movePage(1, heatmaps.prop.data, heatmaps.prop.itemsOnPage);
				},
				//클릭할때
				onPageClick :  function (pagenumber) {
					heatmaps.movePage(pagenumber, heatmaps.propdata, heatmaps.prop.itemsOnPage);
				}
			});
		},
		/**
		 * 넘어온 val 값을 color scale에 맞춰 return 해주는 함수.
		 * @function heatmaps.getColorScale
		 * @param {number} val - cell 데이터
		 * @returns {number} - percent 데이터
		 * */
		getColorScale : function(val) {
			var level = 0;
			val === 100 ? level = 100 : val >= 88 ? level = 90 : val >= 1 ? level = 75 : level = 50;
			//val === 100 ? level = 4 : val >= 97 ? level = 3 : val >= 90 ? level = 2 : val >= 75 ? level = 1 : level = 0;
			
			
			//return heatmaps.prop.colors[level];
			return level;
		},
		/**
		 * 현재 cell의 y data에 맞는 y list의 index 값을 return한다.
		 * @function heatmaps.getYKeyIndex
		 * @param {number} y - y data
		 * @returns {number} - y index
		 * */
		getYKeyIndex : function(y) {
			const yList = heatmaps.prop.y;
			const temp = yList.findIndex(function(item){
				return item.y === y;
			});
			return temp;
		},
		/**
		 * 현재 cell의 y data에 맞는 y list의 value 값을 return한다.
		 * @function heatmaps.getYKeyIndex
		 * @param {number} y - y data
		 * @returns {number} - y value
		 * */
		getYKeyVal : function(y) {
			const yList = heatmaps.prop.y;
			const temp = yList.filter(function(item){
				return item.y === y;
			});
			return temp;
		},
		/**
		 * x, y 위치에 맞는 셀을 찾아 데이터를 입력하는 함수.
		 * @function heatmaps.setCellData
		 * @param {number|string} x - x value
		 * @param {number|string} y - y value
		 * @param {string} val - cell data
		 * */
		setCellData : function(x, y, val) {
			const yTemp = heatmaps.getYKeyIndex(y);
			const yIndex = yTemp;
			const xIndex = heatmaps.prop.x.indexOf(x);
			
			if(yIndex == -1 || xIndex == -1) return;
			
			const grid = heatmaps.prop.grid;
			const row = grid.childNodes[yIndex];
			const cell = row.childNodes[xIndex];
			
			const backgroundColor = 'hsl(0, 100%,' + heatmaps.getColorScale(val) + '%)';
			//const backgroundColor = heatmaps.getColorScale(val);
			cell.style.backgroundColor = backgroundColor;
			cell.style.opacity = '1';
			
			const width = cell.getBoundingClientRect().width;
			const height = cell.getBoundingClientRect().height;
			const offsetX = width / 2;
			const offsetY = height / 2;
			
			cell.addEventListener('mouseenter', function(e) {
				const tooltip = heatmaps.tooltip;
				heatmaps.setTooltipData('%', val);
				heatmaps.showTooltip();
				heatmaps.setTooltipPosition(e.target.offsetLeft + 12, e.clientY - e.offsetY - 38);
			});
			
			cell.addEventListener('mouseleave', function(e) {
				const tooltip = heatmaps.tooltip;
				if(e.relatedTarget !== tooltip) heatmaps.hideTooltip();
			});
		},
		/**
		 * heatmap을 표시할 테이블을 생성한다.
		 * @function heatmaps.createTable
		 * @param {element} parent - 부모 element
		 * @returns {element} - xAxisWrap
		 * @see {@link heatmaps.setPage} - page 선택 시 실행됨.
		 * */
		createTable : function(parent) {
			const xAxisWrap = document.createElement('div');
			xAxisWrap.classList.add('x_axis_wrap');
			
			xAxisWrap.style.width = 'calc(100% - 48px)';
			
			const grid = document.createElement('div');
			grid.classList.add('grid');
			grid.style.height = 'calc(100% - 40px)';
			
			const yMax = heatmaps.prop.y.length;
			const rowHeight = 100 / yMax;
			
			for(var i = 0; i < yMax; i++) {
				const row = document.createElement('div');
				row.classList.add('row');
				row.style.width = 'calc(100%)';
				//row.style.height = 'calc(' + rowHeight + '%)';
				row.style.height = '18px';
				
				const xMax = heatmaps.prop.x.length;
				const cellWidth = 100 / xMax;
				
				for(var j = 0; j < xMax; j++) {
					const cell = document.createElement('div');
					cell.classList.add('cell');
					cell.style.width = 'calc(' + cellWidth + '%)';
					
					//cell.innerHTML = xData[j];
					
					row.appendChild(cell);
				}
				
				grid.appendChild(row);
			}
			
			grid.addEventListener('scroll', function(e) {
				const yAxis = document.getElementById('yAxis');
				
				yAxis.scrollTop = e.target.scrollTop; 
			});
			
			xAxisWrap.appendChild(grid);
			
			heatmaps.prop.grid = grid;
			
			parent.appendChild(xAxisWrap);
			return xAxisWrap;
		},
		/**
		 * x axis를 생성한다.
		 * @function heatmaps.createXAxis
		 * @param {element} parent - 부모 element
		 * @returns {element} - xAxis
		 * @see {@link heatmaps.setPage} - page 선택 시 실행됨.
		 * */
		createXAxis : function(parent) {
			const xAxis = document.createElement('div');
			xAxis.classList.add('x_axis');
			const x = this.prop.x;
			const max = x.length;
			const width = 100 / max;
			
			for(var i = 0; i < max; i++) {
				const column = document.createElement('div');
				column.classList.add('x_axis_column');
				column.style.width = 'calc(' + width + '%)';
				
				column.innerHTML = x[i].substr(x[i].length - 2, 2);
				
				xAxis.appendChild(column);
			}

			parent.appendChild(xAxis);
			return xAxis;
		},
		/**
		 * y axis를 생성한다.
		 * @function heatmaps.createYAxis
		 * @param {element} parent - 부모 element
		 * @returns {element} - yAxis
		 * @see {@link heatmaps.setPage} - page 선택 시 실행됨.
		 * */
		createYAxis : function(parent) {
			const yAxis = document.createElement('div');
			yAxis.classList.add('y_axis');
			yAxis.id = 'yAxis';
			
			const max = heatmaps.prop.y.length;
			const height = 100 / max;
			
			for(var i = 0; i < max; i++) {
				const column = document.createElement('div');
				const label = heatmaps.prop.y[i].label;
				column.classList.add('y_axis_column');
				//column.style.height = 'calc(' + height + '%)';
				column.style.height = '17px';
				
				column.innerHTML = heatmaps.prop.y[i].y;
				
				column.addEventListener('mouseenter', function(e) {
					const tooltip = heatmaps.tooltip;
					heatmaps.setTooltipData('', label);
					heatmaps.showTooltip();
					heatmaps.setTooltipPosition(e.target.offsetLeft + 12, e.clientY - e.offsetY - 38);
				});
				
				column.addEventListener('mouseleave', function(e) {
					const tooltip = heatmaps.tooltip;
					if(e.relatedTarget !== tooltip) heatmaps.hideTooltip();
				});
				
				yAxis.appendChild(column);
			}
			
			parent.appendChild(yAxis);
			return yAxis;
		},
		/**
		 * legned(범례)를 생성한다.
		 * @function heatmaps.createLegend
		 * @param {element} parent - 부모 element
		 * @see {@link heatmaps.setPage} - page 선택 시 실행됨.
		 * */
		createLegend : function(parent) {
			const size = heatmaps.prop.legends.length;
			const legendWrap = document.createElement('div');
			legendWrap.classList.add('legend_wrap');
			var legendMin = 0;
			
			for(var i = 0; i < size; i++) {
				const legend = document.createElement('div');
				legend.classList.add('legend');
				
				const rect = document.createElement('div');
				rect.classList.add('legend_rect');
				const range = heatmaps.prop.legends[i].split('~');
				const colorVal = range.length == 1 ? parseInt(range[0].trim()) : parseInt(range[1].trim());
				const backgroundColor = 'hsl(0, 100%,' + heatmaps.getColorScale(colorVal) + '%)';
				rect.style.backgroundColor = backgroundColor;
				
				const label = document.createElement('div');
				label.classList.add('legend_label');
				label.innerHTML = heatmaps.prop.legends[i];
				
				legend.appendChild(rect);
				legend.appendChild(label);
				
				legendWrap.appendChild(legend);
			}
			
			parent.appendChild(legendWrap);
		},
		/**
		 * page 정보를 세팅한다.
		 * @function heatmaps.setPage
		 * @see {@link heatmaps.movePage} - page 이동시 실행됨.
		 * */
		setPage : function() {
			const parent = document.getElementById(heatmaps.prop.id);
			parent.innerHTML = '';
			
			const wrap = document.createElement('div');
			wrap.classList.add('heatmap_wrap');
			
			const yAxisWrap = document.createElement('div');
			yAxisWrap.classList.add('y_axis_wrap');

			const tableWrap = heatmaps.createTable(yAxisWrap);
			const xAxis = heatmaps.createXAxis(tableWrap);
			const yAxis = heatmaps.createYAxis(yAxisWrap);
			
			heatmaps.createYLabel(wrap);
			heatmaps.createXLabel(wrap);
			heatmaps.createLegend(wrap);
			
			wrap.appendChild(yAxisWrap);
				
			parent.appendChild(wrap);
		},
		/**
		 * heatmap 테이블에 지정될 값들을 지정한다.
		 * @function heatmaps.setTableData
		 * @see {@link heatmaps.movePage} - page 이동시 실행됨.
		 * */
		setTableData : function() {
			const data = heatmaps.prop.data;
			const max = data.length;
			for(var i = 0; i < max; i++) {
				heatmaps.setCellData(data[i].x, data[i].y, data[i].value)
			}
		},
		/**
		 * page 이동 함수.
		 * @function heatmaps.movePage
		 * */
		movePage : function() {
			heatmaps.setPage();
			
			heatmaps.setTableData();
		}
	}
	$.fn.customHeatmap = function(heatmap) {
		
		return heatmaps.init(heatmap);
		// Method calling logic
		/*if (heatmaps[heatmap] && heatmap.charAt(0) != '_') {
			return heatmaps[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof heatmap === 'object' || !heatmap) {
			return heatmaps.init.apply(this, arguments);
		} else {
			$.error('Method ' +  heatmap + ' does not exist on jQuery.pagination');
		}*/

	};

})(jQuery);
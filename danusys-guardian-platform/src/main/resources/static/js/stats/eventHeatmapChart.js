/**
 * 이벤트 heatmap chart 생성 관련 함수
 * @namespace eventHeatmapChart
 * */

/**
 * 이벤트 heatmap chart 생성 함수
 * @function eventHeatmapChart.blockChart
 * */
function blockChart() {
	var jsonObj = {};
	jsonObj.heatmapKind = "e";
	$('#heatmapChart').empty();
	var yAxis = [];
	var xAxis = [];
	var query;
	var optVal = $('#selectBarChartType').val();
	if(optVal == "0"){
		query = "eventChart.selectHeatMapByTZ"
		jsonObj.kind = "t";
	} else if(optVal == "1"){
		query = "eventChart.selectHeatMapByDay"
		jsonObj.kind = "d";
	} else {
		query = "eventChart.selectHeatMapByMonth"
		jsonObj.kind = "m";
	}
	
	jsonObj.sDate = statsMain.sDate;
	jsonObj.eDate = statsMain.eDate;
	jsonObj.sigCode = baseSigCode.toString();
	
	d3.json("/selectHeatMap/"+query+"/action")
		.header("Content-Type", "application/json; charset=utf-8")
		.post(JSON.stringify(jsonObj), function(error, data) {
		var colors = ["#36393d","#edf8b1","#c7e9b4","#7fcdbb","#41b6c4","#1d91c0","#225ea8","#253494","#081d58"];
			//yAxis = ["광명1동", "광명2동", "광명3동", "광명4동", "광명6동", "광명7동", "철산1동","철산2동", "철산3동", "철산4동", "하안1동", "하안2동", "하안3동", "하안4동", "소하1동", "소하2동", "학온동"],
			//xAxis = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"];
		yAxis = data.y;
		xAxis = data.x;
		
		const chartWidth = $('.stats-area.c .cb').width();
		const chartHeight = $('.stats-area.c .cb').height();
		
		var margin = { top: 50, right: 10, bottom: 50, left: 75 },
			width = chartWidth - margin.left - margin.right,
			height = chartHeight - margin.top - margin.bottom,
			gridSizeW = Math.floor(width /xAxis.length),
			gridSizeH = Math.floor(height /yAxis.length),
			buckets = 9,
			legendElementWidth = Math.floor((width-10)/buckets);
		
		var svg = d3.select("#heatmapChart").append("svg")
					.attr("width", chartWidth)
					.attr("height", chartHeight)
					.append("g")
					.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
		
		var yLabels = svg.selectAll(".yLabel")
							.data(yAxis)
							.enter().append("text")
							.text(function (d) { return d; })
							.attr("x", 0)
							.attr("y", function (d, i) { return i * gridSizeH; })
							.style("text-anchor", "end")
							.style("font-size","12px")
							.attr("transform", "translate(-6," + gridSizeH / 1.5 + ")")
							.attr("class", function (d, i) {
								//return ((i >= 0 && i <= 4) ? "dayLabel mono axis axis-workweek" : "dayLabel mono axis");
								return "yLabel mono r"+i;
							});

		var xLabels = svg.selectAll(".xLabel")
							.data(xAxis).enter().append("text")
							.text(function(d) { return d; })
							.attr("x", function(d, i) { return i * gridSizeW; })
							.attr("y", 0)
							.style("text-anchor", "middle")
							.attr("transform", "translate(" + gridSizeW / 2 + ", -6)")
							.attr("class", function(d, i) {
								//return ((i >= 7 && i <= 16) ? "timeLabel mono axis axis-worktime" : "timeLabel mono axis");
								return "xLabel mono r"+i;
							});
		
		
		var colorScale = d3.scale.quantile()
							.domain([-1.6, buckets - 1, d3.max(data.data, function (d) { return d.value; })])
							.range(colors);
		var cards = svg.selectAll(".block")
						.data(data.data);
		
		cards.append("title");
		
		cards.enter().append("rect")
			.attr("x", function(d) { return (d.xtarget - 1) * gridSizeW; })
			.attr("y", function(d) { return (yAxis.indexOf(d.ytarget)) * gridSizeH; /* return (d.ytarget - 1) * gridSizeH; */ })
			.attr("rx", 4)
			.attr("ry", 4)
			.attr("class", "block bordered")
			.attr("width", gridSizeW)
			.attr("height", gridSizeH)
			.style("fill", colors[0]);
		
		cards.transition().duration(1000)
			.style("fill", function(d) { return colorScale(d.value); });
		
		cards.select("title")
			.text(function(d) { return d.value; });
		
		cards.exit().remove();
		
		var tooltip = d3.select("#heatmapChart").append("div").attr("class", "heatmapTooltip");
		
		/* var mouseover = function(d) {
			tooltip.style("opacity",1);
			//d3.selectAll(".yLabel").classed("text-highlight",function(r,ri){ return ri==(d.ytarget-1);});
            //d3.selectAll(".xLabel").classed("text-highlight",function(c,ci){ return ci==(d.xtarget-1);});
		} */
		var mousemove = function(d) {
			d3.select(this).style("cursor", "pointer");
			
            d3.selectAll(".xLabel").classed("text-highlight",function(c,ci){ return ci==(d.xtarget-1);});
			//var name;
			d3.selectAll(".yLabel").classed("text-highlight",function(r,ri){
				//return ri==(d.ytarget-1);
				return ri==yAxis.indexOf(d.ytarget);
			});
			tooltip.html(d.value + "건" )
				.style('left', (d3.mouse(this)[0]+80) + "px")
				.style('top', (d3.mouse(this)[1]+75) + "px");
			/* tooltip.html('<p style="margin:0;font-size:15px;">' + d.ytarget + '</p><br> <div id="heatmapPieArea"></div>')
					.style("left", (d3.mouse(this)[0]+70) + "px")
					.style("top", (d3.mouse(this)[1]+70) + "px");
			
			if(typeof(d.pieData)!="undefined" && d.pieData.length>0 && d.value>0) {
				d3.select('.heatmapTooltip').style('width', '430px').style('height', '230px');
				heatmapPieChart(d.pieData);				
			}
			else {
				d3.select('.heatmapTooltip').style('width', '240px').style('height', '100px');
				d3.select('#heatmapPieArea').append('div').style('font-size', '20px').text('현재까지 발생한 이벤트 건수가 없습니다.');
			} */
			
		}
		var mouseleave = function(d) {
			tooltip.style("opacity",0);
			 d3.selectAll(".yLabel").classed("text-highlight",false);
             d3.selectAll(".xLabel").classed("text-highlight",false);
		}
		svg.append('text').attr('text-anchor','middle')
			.attr('transform', 'translate('+(width/2-30)+","+(height+40)+')')
			.style({'font-size':'13px', 'fill': '#cecece'})
			.text($("#selectBarChartType option:selected").text()+"-행정동 발생건수 히트맵");
		svg.selectAll(".block")
			.on("mouseover", function(d) {
				tooltip.style("opacity",1);
			})
			.on("mousemove", mousemove)
			.on("mouseleave", mouseleave);
		
		var legend = svg.selectAll(".legend").data([0].concat(colorScale.quantiles()), function(d) { return d; });
		
		legend.enter().append("g").attr("class", "legend");
		
		legend.append("rect")
			.attr("x", function(d, i) { return legendElementWidth * i; })
			.attr("y", height)
			.attr("width", legendElementWidth)
			.attr("height", 10).style("fill", function(d, i) { return colors[i]; });
		
		legend.append("text").attr("class", "mono")
			.text(function(d) { return "≥ " + Math.round(d); })
			.attr("x", function(d, i) { return legendElementWidth * i; })
			.attr("y", height + 20);
		
		legend.exit().remove();
		
		
		
		///파이차트///
		/* function heatmapPieChart(makedata) {
			//초기화
			const heatmapPieColorScale = d3.scale.category20();
			//var margin = { top: 0, left: 0, bottom: 0, right: 0 };
			var heatmapPieChartWidth = 220;
			var heatmapPieCharHeight = 220;
			
			const outerRadius = heatmapPieCharHeight / 2 - 20;
			const innerRadius = 0;
			const cornerRadius = 0;
			
			//파이위치
			var c3_g = d3.select('#heatmapPieArea').append('svg')
						.attr('width', heatmapPieChartWidth)
						.attr('height', heatmapPieCharHeight)
						.style('left', '10px')
						.append('g')
						.style('stroke', 'white')
						.attr('transform', 'translate(80,40)');
			
			var arc = d3.svg.arc().innerRadius(50).outerRadius(outerRadius).cornerRadius(cornerRadius);
			//var arcOver = d3.svg.arc().innerRadius(innerRadius).outerRadius(outerRadius + 20).cornerRadius(cornerRadius);
			var pie = d3.layout.pie().value(function (d) {return d.value;}).sort(null).padAngle(.01);
			
			var g = c3_g.selectAll('.fan').data(pie(makedata)).enter().append('g')
					.attr('transform', 'translate(30,50)').style('opacity', '1');
			g.append('path').attr('d', arc).attr('class', 'fan')
				.attr('fill', function (d) {return heatmapPieColorScale(d.data.name);});
			g.transition().duration(500).delay(function (d, i) {return i * 50;}).style('opacity', '1');
			g.append('text').attr('transform', function (d) {return makedata.length>1?'translate(' + (arc.centroid(d) + 1) + ')rotate(0)':'';})
				.attr('font-size', 13).style('text-anchor', "middle").style('font-weight', 'bold').style('stroke', 'none').style('fill', 'white')
				.text(function (d) {
					return d.data.value;
				});
			d3.select("#heatmapPieArea").append('svg').attr('class', 'legendBack')
				.style('background', 'none').style('position', 'absolute').style('height', '100%');
			
			//파이 범례
			var legend = d3.select('#heatmapPieArea').select('.legendBack').selectAll('.legend')
							.data(makedata.map(function (d) {return d.name;})).enter()//.reverse())
							.append('g').attr('class', 'legend')
							.attr('transform', function (d, i) { return "translate(0," + (i * 20) + ")"; })
							.style("opacity", "1");
			legend.append('rect').attr('x', 5).attr('y', 5).attr('ry', 3).attr('width', 15).attr('height', 15)
				.style('stroke', 'white').style('fill', function (d) { return heatmapPieColorScale(d); });
			legend.append('text').attr('x', 25).attr('y', 13).attr('dy', '.35em').attr('font-size', 13)
				.style('text-anchor', 'start').style('fill', 'white').style('render-order', 99)
				.text(function (d) { return d; });
			
			var total = 0;
			legend.append('text')
				.data(makedata.map(function (d) {
					total += Number(d.value);
					return Number(d.value);
				})).attr('x', 130).attr('y', 13).attr('dy', '.35em').attr('font-size', 13)
				.style('text-anchor', 'start').style('fill', 'white').style('render-order', 99)
				.text(function (d) {
					return "(" + d + "/" + total + ")  " + Math.round((d / total) * 100) + "%";
				});
		} */
	});
}

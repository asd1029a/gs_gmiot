/**
 * 이벤트 bar chart 2 생성 관련 함수
 * @namespace eventBarChart2
 * */

/**
 * 이벤트 bar chart 2 생성 함수
 * @function eventBarChart2.barChart2
 * */
function barChart2() {
	var jsonObj = {};
	$("#barChart2").empty();
	var yAxis = [];
	var xAxis = [];
	var query;
	var optVal = $('#selectBarChart2Type').val();
	if(optVal == "0"){
		query = "eventChart.eventByTZ"
	} else if(optVal == "1"){
		query = "eventChart.eventByDay"
	} else {
		query = "eventChart.eventByMonth"
	}
	
	jsonObj.sDate = statsMain.sDate;
	jsonObj.eDate = statsMain.eDate;
	jsonObj.sigCode = baseSigCode.toString();
	
	d3.json("/selectBarChart/"+query+"/action")
	.header("Content-Type", "application/json; charset=utf-8")
	.post(JSON.stringify(jsonObj), function(error, data) {
		yAxis = data.y;
		xAxis = data.x;
		const chartWidth = $('.stats-area.r .rt').width();
		const chartHeight = $('.stats-area.r .rt').height();

		var margin = { top: 20, right: 30, bottom: 60, left: 10 },
		    width = chartWidth - margin.left - margin.right,
		    height = chartHeight - margin.top - margin.bottom;
		
		var color=['#1B5164','#3C8097','#A0CFDF','#407dd6d6','#BDDEE9','#DDEEF4','#461E5D','#e54f15ab','#ffa900b5','#0ca31fc4','#72448C','#C6A9D6','#D8C4E4','#EBE0F1','#3F5821','#6A844A','#C2D0B1','#D5DFC9','#b59a9a','#ab27aaa1','#00a9d1ba','#e65d8ac9','#77b500db','#c7433ba3','#3e77a6','#ab5caa','#22b7aa','#3C8097','#A0CFDF'];
		
		var x = d3.scale.ordinal().rangeRoundBands([0,width], .1);
		var y = d3.scale.linear().range([height,0]);
		
		var chart = d3.select('#barChart2').append("svg")
					.attr("width",chartWidth)
					.attr("height",chartHeight);
		
		y.domain([0, d3.max(data, function(d){return d.cnt;})]);
		x.domain(data.map(function(d){return d.xtarget;}));
		
		var bar = chart.selectAll('g')
						.data(data)
						.enter().append('g')
						.attr("class",'bar')
						.attr("transform", function(d){ return "translate("+(x(d.xtarget)+30)+",20)";}) //*;
		
		var tooltip = d3.select('#barChart2').append('div').attr("class",'barTooltip');
		
		bar.append('rect')
			.on("mouseover",function(d) {
				d3.select(this).style("fill","#B54444");
				tooltip.style("opacity",1);
			})
			.on("mousemove",function(d) {
				d3.select(this).style("cursor", "pointer");
				
				tooltip.html(d.cnt + "건" )
					.style('left', (d3.mouse(this.parentElement.parentElement)[0]) + "px")
					.style('top', (d3.mouse(this)[1]) + "px");
			})
			.on("mouseout",function(d) {
				d3.select(this).style("fill",function(d,i){
					for(var i=0; i<data.length;i++){
						if(data[i].xtarget==d.xtarget){
							return color[i];
						}
					}
				});
				tooltip.style("opacity",0);
			})
			.style('fill',function(d,i){
				return color[i];
			})
			.attr('ry',15)
			.attr("height",0)
			.transition()
			.duration(800)
			.attr('y', function(d){return y(d.cnt);})
			.attr("height", function(d){return height - y(d.cnt);})
			.delay(function(d,i) {return i*50;})
			.attr("width", x.rangeBand());
		
		bar.append('text')
			.attr("x", x.rangeBand()/2)
			.attr('y',function(d){return y(d.cnt)-13;})
			.style({"font-size":"11px", "text-anchor": "middle", "fill" : "#cecece"})
			.transition()
			.duration(800)
			.delay(function(d,i) {return i*50;})
			.attr('dy',".75em")
			.text(function(d){ return d.cnt;});
		
		var xAxis = d3.svg.axis().scale(x).orient("bottom");
		var yAxis = d3.svg.axis().scale(y).orient("left").tickSize(-width);
		
		chart.append('g')
			.attr('class','x axis')
			.attr('transform','translate(30,'+(height+20)+')')
			.call(xAxis)
        	.selectAll('text')
        	.style({'fill': '#cecece'});
		
		chart.append('g')
			.attr('class','y axis')
			.attr("transform",'translate(30,20)')
			.call(yAxis)
        	.selectAll('text')
        	.style({'fill': '#cecece'});

		chart.select('.y.axis').selectAll('.tick').selectAll('line').style("stroke","#cecece");
		chart.select('.y.axis').selectAll('path').style({"stroke": "#cecece", "opacity": '0.3'});
		chart.select('.x.axis').selectAll('.tick').selectAll('line').style("stroke","#cecece");
		chart.select('.x.axis').selectAll('path').style({"stroke": "#cecece", "opacity": '0.3'});
		
		var xname  = new Array();
		
		$.each(data[0],function(key, value){
			xname.push(key);
		});
		
		chart.append('text').attr('text-anchor','middle')
							.attr('transform', 'translate('+(width/2)+","+(margin.top+height+40)+')')
							.style({'font-size':'13px', 'fill': '#cecece'})
							.text($("#selectBarChart2Type option:selected").text()+" 발생건수");
	});
}
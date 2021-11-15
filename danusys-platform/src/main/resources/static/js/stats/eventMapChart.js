/**
 * 이벤트 map chart 관련 모음
 * @namespace eventMapChart
 * */

/**
 * @memberof eventMapChart
 * @property {string} doName - 시 도 명칭 
 * */
var doName = "";
/**
 * @memberof eventMapChart
 * @property {object} sigNames - 시군구 명칭
 * */
var sigNames = {};

/**
 * @memberof eventMapChart
 * @property {string} baseSigCode - 시군구 코드
 * */
var baseSigCode = "all";

/**
 * @memberof eventMapChart
 * @property {Array} baseCenter - 기본 중심 좌표 저장 배열 데이터
 * */
//var baseCenter = [];
//var baseCenter = [127.224575, 37.220119];	//용인시
var baseCenter = [127.25704, 37.97012];	//포천시

/**
 * @memberof eventMapChart
 * @property {number} baseScale - 기본 스케일 값
 * */
//var baseScale = 0;
//var baseScale = 85000;	//용인시
var baseScale = 62000;		//포천시

/**
 * @memberof eventMapChart
 * @property {Array} clickCenter - 클릭된 시군구의 좌표 저장 배열 데이터
 * */
var clickCenter = [];
/**
 * @memberof eventMapChart
 * @property {number} clickMapScale - 클릭된 시군구의 기본 스케일 값
 * */
var clickMapScale = 0;


/**
 * 이벤트 맵 차트 생성 함수
 * @function eventMapChart.mapChart
 * @param {string} sigCode - 시군구 코드
 * @param {Array} mapCenter - 맵 중심 좌표
 * @param {number} mapScale - 맵 기본 스케일 값
 * */
function mapChart(sigCode, mapCenter, mapScale) {
	$('#mapChart').empty();
	d3.select('#mapChart').selectAll('svg').remove();
	d3.select(".mapTooltip").remove();
	
	d3.json("/searchDongGis1?sigCode=" + sigCode+"&timeS="+statsMain.sDate+"&timeE="+statsMain.eDate,function (error, geodata) {
		//시군구 코드 및 시군구 명
		if (sigCode == 'all') {
			const prpt = geodata.features[0].properties;
			clickMapScale = baseScale;
			clickCenter = baseCenter;
			sigNames['all'] = doName;
			geodata.features.map(function (f) {
				sigNames[f.properties.region_cd] = doName + " " + f.properties.region_kor_nm;
			});
		}
		
		const chartWidth = $('.stats-area.l').width();
		const chartHeight = $('.stats-area.l').height()-20;
		
		var c1_g = d3.select("#mapChart").append("svg").attr("width", chartWidth).attr("height", chartHeight);
		
		var mapTrst1 = chartWidth/2;
		var mapTrst2 = chartHeight/2;
		var projection = d3.geo.mercator().center(clickCenter).scale(clickMapScale).translate([mapTrst1,mapTrst2]);
		var path = d3.geo.path().projection(projection);
		var features = geodata.features;
		
		//max값 찾기
		var maxCount;
		const countList = new Array();
		for (var i = 0; i < geodata.features.length; i++) {
			countList[i] = geodata.features[i].properties.count;
		}
		
		//var key;
		for (i = 1; i < countList.length; i++) {
			const key = countList[i];
			for (j = i - 1; j >= 0 && countList[j] > key; j--) {
				countList[j + 1] = countList[j];
			}
			countList[j + 1] = key;
		}
		
		const realList = new Array();
		$.each(countList, function (i, val) {
			if (realList.indexOf(val) == -1) {
				realList.push(val);
			}
		});
		maxCount = realList.pop();
		
		if(maxCount==0) {
			maxCount = 4;
		}
		else {
			if((maxCount%4)!=0) {
				if(maxCount<4) maxCount += (4 - maxCount);
				else maxCount += (4 - (maxCount%4));
			}
		}
		
		const baseCount = Math.round(maxCount / 4);
		var colorScale = d3.scale.threshold().domain([0, baseCount, baseCount * 2, baseCount * 3, maxCount+1]).range(d3.schemeBlues[5]);
		var x = d3.scale.linear().domain([0, baseCount, baseCount * 2, baseCount * 3, maxCount+1]).range([0, 50, 100, 150, 200]); //x
		
		//툴팁
		var tooltip = d3.select('#mapChart').append('div').attr("class", "mapTooltip").style('display', 'none');
		
		///파이차트///
		function mapPieChart(makedata) {
			//초기화
			const mapPieColorScale = d3.scale.category20b();
			//var margin = { top: 0, left: 0, bottom: 0, right: 0 };
			var mapPieChartWidth = 220;
			var mapPieCharHeight = 220;
			
			const outerRadius = mapPieCharHeight / 2 - 20;
			const innerRadius = 0;
			const cornerRadius = 0;
			
			//파이위치
			var c3_g = d3.select('#mapPieArea').append('svg')
						.attr('width', mapPieChartWidth)
						.attr('height', mapPieCharHeight)
						.style('left', '10px')
						.append('g')
						.style('stroke', 'white')
						.attr('transform', 'translate(80,50)');
			
			var arc = d3.svg.arc().innerRadius(innerRadius).outerRadius(outerRadius).cornerRadius(cornerRadius);
			var pie = d3.layout.pie().value(function (d) {return d.value;}).sort(null).padAngle(.01);
			
			var g = c3_g.selectAll('.fan').data(pie(makedata)).enter().append('g')
					.attr('transform', 'translate(30,50)').style('opacity', '1');
			g.append('path').attr('d', arc).attr('class', 'fan')
				.attr('fill', function (d) {return mapPieColorScale(d.data.type);});
			g.transition().duration(500).delay(function (d, i) {return i * 50;}).style('opacity', '1');
			g.append('text').attr('transform', function (d) {return makedata.length>1?'translate(' + (arc.centroid(d) + 1) + ')rotate(0)':'';})
				.attr('font-size', 13).style('text-anchor', "middle").style('font-weight', 'bold').style('stroke', 'none').style('fill', '#36393d')
				.text(function (d) {
					return d.data.value;
				});
			d3.select("#mapPieArea").append('svg').attr('class', 'legendBack')
				.style('background', 'none').style('position', 'absolute').style('height', '100%');
			
			//파이 범례
			var legend = d3.select('#mapPieArea').select('.legendBack').selectAll('.legend')
							.data(makedata.map(function (d) {return d.type;})).enter()//.reverse())
							.append('g').attr('class', 'legend')
							.attr('transform', function (d, i) { return "translate(0," + (i * 20) + ")"; })
							.style("opacity", "1");
			legend.append('rect').attr('x', 5).attr('y', 5).attr('ry', 3).attr('width', 15).attr('height', 15)
				.style('stroke', 'white').style('fill', function (d) { return mapPieColorScale(d); });
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
		}//mapPieChart end
		
		//마우스 이벤트 함수
		function mouseon(d, i) {
			d3.select(this).style("cursor", "pointer");
			d3.select('#' + d.properties.region_kor_nm).style("stroke-dasharray", "5.5").style("stroke", "red").style("stroke-width", 3);
			c1_g.selectAll("path").sort(function (a, b) {
				if (a.id != d.id) return -1;
				else return 1;
			});
			tooltip.style('display', 'inline');
		}
		
		function mousemove(d) {
			var mouseXY = d3.mouse(this);
			tooltip.html('<p style="margin:0;font-size:15px;">읍면동 이름 : ' + d.properties.region_kor_nm + '</p><br> <div id="mapPieArea"></div>')
				.style('left', (d3.event.pageX) + 'px').style('top', (d3.event.pageY-150) + 'px');
			
			var dataset = geodata.pieData;
			for (var i = 0; i < dataset.length; i++) {
				if (typeof(dataset[i][0])!="undefined"&& (d.properties.region_kor_nm) == (dataset[i][0].region_kor_nm)) {
					var zeros = 0;
					dataset[i].map(function (d) {
						if (d.value == 0) { zeros++; }
					});
					
					if (zeros == dataset[i].length) {
						d3.select('#mapPieArea').append('div').style('font-size', '20px').text('현재까지 발생한 이벤트 건수가 없습니다.');
						d3.select('.mapTooltip').style('width', '240px').style('height', '100px');
					}
					else {
						mapPieChart(dataset[i]);
						d3.select('.mapTooltip').style('width', '450px').style('height', '400px');
					}
				}
			}
		}
		
		function mouseoff(d, i) {
			d3.select("#" + d.properties.region_kor_nm)
				.style("stroke", "#717171").style("stroke-opacity", "1").style("stroke-width", 2).style("stroke-dasharray", "0");
			tooltip.style('display', 'none');
		}
		
		function dblclick(d) {
			baseSigCode = d.properties.region_cd;
			mapChart(baseSigCode);
		}
		
		function dblclick(d) {
			if (d.properties.region_cd.toString().length <= 5) {
				var cen = path.centroid(d);
				var mouseXY = d3.mouse(this);

				baseSigCode = d.properties.region_cd;
				sigNames[baseSigCode] = d.properties.region_kor_nm;

				clickCenter = [ d.properties.cityLon, d.properties.cityLat ];
				clickMapScale = d.properties.cityScale;
				
				mapChart(baseSigCode);
				barChart();
				blockChart();
				barChart2();
				blockChart2();
				setMapTable();
			}
		}
		
		// 경계등록 TESTTEST
		c1_g.selectAll("path").data(features).enter().append("path").attr("d", path)
			.attr("id", function (d) { return d.properties.region_kor_nm }) //id
			.attr("stroke", "#717171").attr("stroke-opacity", "1").attr("stroke-width", 2)
			.attr("fill", function (d) {
				if(d.properties.count==0) {
					return new String('#36393d');
				}
				else {
					return colorScale(d.properties.count);
				}
			})
			.attr("class", function (d) { return "municipality" + colorScale(d.properties.count) })
			.on('dblclick', dblclick).on("mouseover", mouseon).on("mousemove", mousemove).on("mouseout", mouseoff);
		
		lableSwitch(); //초기화
		
		function lableSwitch() {
			c1_g.selectAll(".place-label").remove();
			
			//라벨
			c1_g.selectAll(".place-label").data(features).enter().append("text")
				.attr("class", function (d) {
					return "place-label";
				})
				.attr("transform", function (d) {
					return "translate(" + path.centroid(d) + ")";
				})
				.text(function (d) {
					return d.properties.region_kor_nm + "(" + d.properties.count + ")";
				});
		}
		
		var mapZoom = d3.behavior.zoom().translate(projection.translate())
						.scale(projection.scale())
						.scaleExtent([60000, 1000000])//*369462.2150164106
						.on("zoom", zoomed);
		
		c1_g.call(mapZoom).on("dblclick.zoom", null);
		
		function zoomed() {
			projection.translate(mapZoom.translate()).scale(mapZoom.scale());
			d3.select('#mapChart').selectAll("path").attr("d", path);
			
			//라벨
			c1_g.selectAll(".place-label").attr("transform", function (d) {
				return "translate(" + path.centroid(d) + ")";
			});
			
			tooltip.style('display', 'none');
			clickMapScale = projection.scale();
		}
		
		var mulcount = maxCount / 200;
		
		//맵범례
		var legend = c1_g.append("g").attr("class", "key").attr("transform", "translate(20," + (chartHeight-60) + ")");
		legend.selectAll("rect")
			.data(colorScale.range().map(function (d) {
				d = colorScale.invertExtent(d);
				if (d[0] == null) d[0] = x.domain()[0];
				if (d[1] == null) d[1] = x.domain()[1];
				return d;
			}))
			.enter().append("rect").attr("height", 15)
			.attr("x", function (d) { return x(d[0]); })
			.attr("stroke-width", 1)
			.attr("stroke", "#2f2f2f")
			.attr("width", function (d) { return x(d[1]) - x(d[0]); })
			.attr("fill", function (d) {
				return colorScale(d[0]);
			});
		legend.append("text").attr("class", "caption")
			.attr("x", x.range()[0]).attr("y", -6).attr("fill", "black")
			.attr("text-anchor", "start").attr("font-weight", "bold").text("");
		legend.call(d3.svg.axis(x).tickSize(15).tickValues(x.range()) //***
			.tickFormat(function (x) {
				return Math.floor(x * mulcount) === maxCount ? Math.floor(x * mulcount) + "건" : Math.floor(x * mulcount);
			}))
			.attr("font-size", "14px").attr("fill", "#ececec").select(".domain").remove();
		
		//구 클릭시
		if (sigCode != 'all') {
			c1_g.selectAll("path").on('dblclick', null);
			c1_g.call(mapZoom).on("dblclick.zoom", null);
			
			c1_g.append("rect").attr("id", "btnSwitch")
				.attr("class", "bs").attr('ry', 10)
				.attr("width", "108px").attr("height", "30px")
				.attr("fill", "#566172").style('stroke', "lightgray")
				.attr("opacity", 0.8).attr("render-order", "999")
				.attr("transform", "translate(15,0)");
			//button text
			c1_g.append("text").attr("class", "bs")
				.attr("width", "100px").attr("height", "30px")
				.attr("fill", "lightgray").attr("font-size", 13)
                         .attr("transform", "translate(27,20)").text("시군구 다시보기");
			
			//시군구 다시보기(버튼)
			d3.selectAll(".bs").on("click", function (d) {
				baseSigCode = "all";
				clickCenter = baseCenter;
				clickMapScale = baseScale;
				mapChart(baseSigCode);
				barChart();
				blockChart();
				barChart2();
				blockChart2();
				setMapTable();
			}).on("mouseover", function (d) {
				d3.select(this).style("cursor", "pointer").style("stroke", "black");
			})
			.on("mouseleave", function (d) {
				d3.select(this).style("stroke", "lightgray");
			});
		} //구클릭 end
		
		//시군구 정보
		c1_g.append('g').attr('id', 'infoArea');
		c1_g.select('#infoArea').append('text')
			.attr("transform", "translate(20," + (chartHeight-80) + ")").attr('dy', '.70em')
			.text(function (d) {return sigNames[sigCode];});
	});
}

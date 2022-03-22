<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%@ taglib prefix="j" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<aside class="aside_wrap">
	<div class="header">
		<dl class="header_contents">
			<dt>
				<h4 class="menu"><a href="#"><span class="icon icon_menu"></span></a></h4>
				<p class="logo"><a href="/">지도관제시스템</a></p>
			</dt>
			<dd>
				<sec:authorize access="isAuthenticated()">
					<p class="loginTitle"><i class="fas fa-user"></i>&nbsp;&nbsp;<sec:authentication property="principal.adminId"/> 님</p>
				</sec:authorize>
			</dd>
		</dl>
		<ul class="gnb">
			<li class="active" data-large-menu="cctv"><a href="#">CCTV</a></li>
			<sec:authorize access="isAuthenticated()">
				<sec:authentication var="permitList" property="principal.permitList"/>
					<j:set var="eventFlag" value="true" />
					<j:forEach var="permit" items="${permitList}">
						<j:if test="${permit eq '16' || permit eq '17' || permit eq '18'}">
							<j:if test="${eventFlag}">
								<li data-large-menu="event"><a href="#">이벤트</a></li>
								<j:set var="eventFlag" value="false" />
							</j:if>
						</j:if>
					</j:forEach>
			</sec:authorize>
			<li data-large-menu="place"><a href="#">주소/장소</a></li>
			<li data-large-menu="favorite"><a href="#">즐겨찾기</a></li>
		</ul>
	</div>
	<form id="cctv_form" onsubmit="return false;">
		<div class="lnb_wrap_1">
			<div class="lnb">		
				<ul class="lnb_chkbox">
				</ul>
				<div class="lnb_select">
					<p class="select_check_tit">행정동 선택</p>
					<ul class="select_check cctv">
						<li class="checkbox">						
							<input class="input_checkbox allCheck" type="checkbox" id="cctv_input_checkbox"/>
							<i></i>
							<label for="cctv_input_checkbox">전체선택</label>
						</li>
					</ul>
				</div>
				<div class="searchWrap">
					<dl class="lnb_search">
						<dt><input type="text" name="cctv_keyword" value="" placeholder="CCTV 검색"></dt>
						<dd><span class="icon icon_search"></span></dd>
					</dl>
					 <p class="lnb_searchReset"><span class="icon icon_reset"></span></p>
				</div>
			</div>
			<div class="list_contents">
				<dl class="list_result">
					<dt>조회결과</dt>
					<dd><span class="list_result_count"></span><span>건</span></dd>
				</dl>
				<div class="list_table">
					<table class="cctv_table" style="width:calc(100% - 5px);">
						<thead>
							<tr>
								<th>
									연번
								</th>
								<th>
									CCTV 이름
								</th>
								<th>
									구역
								</th>
								<th>
								</th>
							</tr>
						</thead>
						<!-- <tbody>
						</tbody> -->
					</table>						
				</div>
				<!-- <ul class="list_nav">
				</ul> -->
			</div>
		</div>
	</form>
	<form id="event_form" onsubmit="return false;">
		<div class="lnb_wrap_2">
			<div class="lnb">
				<article class="lnb_tab_wrap">
					<ul class="lnb_tab">
						<sec:authorize access="isAuthenticated()">
							<sec:authentication var="permitList" property="principal.permitList"/>
								<j:forEach var="permit" items="${permitList}">
									<j:if test="${permit eq '16'}">
										<li class="active" id="safety"><a href="#">안전귀가</a></li>
									</j:if>
									<j:if test="${permit eq '17'}">
										<li id="bell"><a href="#">비상벨</a></li>
									</j:if>
									<j:if test="${permit eq '18'}">
										<li id="wanted_car"><a href="#">수배차량</a></li>
									</j:if>
								</j:forEach>
						</sec:authorize>
					</ul>
				</article>
				<div id="lnb_safety_option">
					<div class="lnb_date">
						<ul>
							<li>발생<br>시각</li>
							<li>
								<dl>
									<input type="text" class="start_date" id="safety_start_date" readonly="readonly"/>
									<input type="text" id="safety_start_time" value="00:00"/>
								</dl>
								<dl>
									<input type="text" class="end_date" id="safety_end_date" readonly="readonly"/>
									<input type="text" id="safety_end_time" value="23:59"/>
								</dl>
							</li>
						</ul>					
					</div>
					<div class="lnb_select">
						<ul>
							<li>
								<p class="select_check_tit half">행정동 선택</p>
								<ul class="select_check safety">
									<li class="checkbox">						
										<input class="input_checkbox allCheck" type="checkbox" id="safety_input_checkbox"/>
										<i></i>
										<label for="safety_input_checkbox">전체선택</label>
									</li>
								</ul>
							</li>
							<li>
								<select id="safetyFlag">
									<option value="">* 전체 *</option>
									<option selected value="0">발생</option>
									<option value="1">종료</option>
								</select>
							</li>
							<li class="searchBtnArea">
								<button class="icon icon_search"></button>
							</li>
						</ul>
					</div>
				</div>
				<div id="lnb_bell_option">
					<div class="lnb_date">
						<ul>
							<li>발생<br>시각</li>
							<li>
								<dl>
									<input type="text" class="start_date" id="bell_start_date" readonly="readonly"/>
									<input type="text" id="bell_start_time" value="00:00"/>
								</dl>
								<dl>
									<input type="text" class="end_date" id="bell_end_date" readonly="readonly"/>
									<input type="text" id="bell_end_time" value="23:59"/>
								</dl>
							</li>
						</ul>					
					</div>
					<div class="lnb_select">
						<ul>
							<li class="fullSelect">
								<select id="bellFlag">
									<option value="">* 전체 *</option>
									<option selected value="1">발생</option>
									<option value="2">종료</option>
								</select>
							</li>
						</ul>
					</div>
					<div class="searchWrap">
						<dl class="lnb_search full">
							<dt><input type="text" name="bell_keyword" value="" placeholder="검색"></dt>
							<dd><span class="icon icon_search"></span></dd>
						</dl>
					</div>
				</div>
				<div id="lnb_wanted_car_option">
					<div class="lnb_date">
						<ul>
							<li>발생<br>시각</li>
							<li>
								<dl>
									<input class="start_date" id="wanted_car_start_date" readonly="readonly"/>
									<input class="start_time" id="wanted_car_start_time"/>
								</dl>
								<dl>
									<input class="end_date" id="wanted_car_end_date" readonly="readonly"/>
									<input class="end_time" id="wanted_car_end_time"/>
								</dl>
							</li>
						</ul>					
					</div>
					<div class="lnb_select">
						<ul>
							<li class="fullSelect">
								<select id="wantedCarFlag">
									<option value="">* 전체 *</option>
									<option selected value="30">발생</option>
									<option value="91">종료</option>
								</select>
							</li>
						</ul>
					</div>
					<div class="searchWrap">
						<dl class="lnb_search full">
							<dt><input type="text" name="wanted_car_keyword" value="" placeholder="차량번호 검색"></dt>
							<dd><span class="icon icon_search"></span></dd>
						</dl>
					</div>
				</div>
			</div>
			<div class="list_contents safety">
				<dl class="list_result">
					<dt>조회결과</dt>
					<dd><span class="list_result_count"></span><span>건</span></dd>
				</dl>
				<div class="list_table_safety">
					<table class="event_table_safety" style="width:calc(100% - 5px);">
						<colgroup>
							<col width="5%" />
							<col width="25%" />
							<col width="25%" />
							<col width="45%" />
						</colgroup>
						<thead>
							<tr>
								<th>
								</th>
								<th>
									신고자
								</th>
								<th>
									처리상태
								</th>
								<th>
									발생시각
								</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>						
				</div>
				<ul class="list_nav">
				</ul>
			</div>
			<div class="list_contents bell">
				<dl class="list_result">
					<dt>조회결과</dt>
					<dd><span class="list_result_count"></span><span>건</span></dd>
				</dl>
				<div class="list_table_bell">
					<table class="event_table_bell" style="width:calc(100% - 5px);">
						<colgroup>
							<col width="30%" />
							<col width="40%" />
							<col width="30%" />
						</colgroup>
						<thead>
							<tr>
								<th>
									관리번호
								</th>
								<th>
									지번주소
								</th>
								<th>
									발생일
								</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>						
				</div>
				<ul class="list_nav">
				</ul>
			</div>
			<div class="list_contents wanted_car">
				<dl class="list_result">
					<dt>조회결과</dt>
					<dd><span class="list_result_count"></span><span>건</span></dd>
				</dl>
				<div class="list_table_wanted_car">
					<table class="event_table_wanted_car" style="width:calc(100% - 5px);">
						<colgroup>
							<col width="25%" />
							<col width="45%" />
							<col width="25%" />
						</colgroup>
						<thead>
							<tr>
								<th>
									차번
								</th>
								<th>
									장소
								</th>
								<th>
									발생시각
								</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>						
				</div>
				<ul class="list_nav">
				</ul>
			</div>
		</div>
	</form>
	<form id="place_form" onsubmit="return false;">
		<div class="lnb_wrap_3">				
			<div class="lnb">
				<article class="lnb_tab_wrap">
					<ul class="lnb_tab">
						<li class="active"><a href="#">전체</a></li>
						<li><a href="#">주소</a></li>
						<li><a href="#">장소</a></li>
					</ul>
				</article>
				<dl class="lnb_search">
					<dt><input type="text" name="place_total_keyword" value="" placeholder="주소/장소 검색"></dt>
					<dd><span class="icon icon_search"></span></dd>
				</dl>
				<dl class="lnb_search_result">
					<dt><span class="txt_blue txt_result"></span><span>검색결과</span></dt>
					<dd><span class="icon icon_closed_g"></span></dd>
				</dl>
			</div>
			<div class="list_contents_double">
				<div class="list_address_min">
					<h5 class="tit">주소</h5>
					<ul class="list_contentsUL">
					</ul>
				</div>
				<div class="more_contents">
					<a>주소 더보기</a>
				</div>
				<div class="list_area_min">
					<h5 class="tit">장소</h5>
					<ul class="list_contentsUL">
					</ul>
				</div>
				<div class="more_contents">
					<a>장소 더보기</a>
				</div>
			</div>
			<div class="list_contents_single address">
				<div class="list_address_max">
					<h5 class="tit">주소</h5>
					<ul class="list_contentsUL">
					</ul>
				</div>
				<ul class="list_nav">
				</ul>
			</div>
			<div class="list_contents_single area">
				<div class="list_area_max">
					<h5 class="tit">장소</h5>
					<ul class="list_contentsUL">
					</ul>
				</div>
				<ul class="list_nav">
				</ul>
			</div>
		</div>
	</form>
	<form id="favorite_form" onsubmit="return false;">
		<div class="lnb_wrap_4">				
			<div class="lnb">
				<article class="lnb_tab_wrap">
					<ul class="lnb_tab">
						<li class="active"><a href="#">전체</a></li>
						<li><a href="#">CCTV</a></li>
						<li><a href="#">주소</a></li>
						<li><a href="#">장소</a></li>
					</ul>
				</article>
			</div>
			<div class="list_contents">
				<dl class="list_result">
					<dt>즐겨찾기 리스트</dt>
				</dl>
				<div class="list_table">
					<table class="favorite_table" style="width:calc(100% - 5px);">
						<colgroup>
							<col width="15%" />
							<col width="40%" />
							<col width="40%" />
							<col width="5%" />
						</colgroup>
						<thead>
							<tr>
								<th>
									유형
								</th>
								<th>
									별칭
								</th>
								<th>
									주소
								</th>
								<th>
								</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<ul class="list_nav">
				</ul>
			</div>
		</div>
	</form>
</aside>
<div class="aside_nav"></div>
<aside class="admin_aside_wrap">
	<div class="admin_aside">
		<dl class="header">
			<!-- 광명시 이미지 -->
			<!-- <dt><img src="/css/images/toggle_logo.svg"></dt> -->
			<dt>
				<img src="/css/images/cctvgisLogo.svg">
				지도관제시스템
			</dt>
			<dd class="admin_closed"><a href="#"><span class="icon icon_closed_g"></span></a></dd>
		</dl>
		<ul class="admin_gnb">
		</ul>
		<div class="admin_logout_area">
			<i class="fas fa-sign-out-alt" onclick="sec.logoutProc()"><p>로그아웃</p></i>
		</div>
		<ul class="admin_utmenu">
			<li id="serviceTerms">서비스 이용약관</li>
			<li id="legalTerms">법정 공지 및 정보제공처</li>
			<li id="privercyTerms">개인정보 처리방침</li>
			<li id="operationTerms">운영 정책</li>
		</ul>
	</div>
</aside>
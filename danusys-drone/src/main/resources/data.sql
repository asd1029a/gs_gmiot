-- 기본명령
insert into mission_list(name,description)  values('waypoint','경유지');
insert into mission_list(name,description)  values('return_home','귀환');
insert into mission_list(name,description)  values('landing','착륙');
insert into mission_list(name,description)  values('takeoff','이륙');


-- 로이터
insert into mission_list(name,description)  values('loiter','로이터');
insert into mission_list(name,description)  values('loiter_time','로이터(시간)');
insert into mission_list(name,description)  values('loiter_altitude','로이터(고도)');

-- 고급

insert into mission_list(name,description)  values('pause','일시대기');
--지정 항목으로 이동 서보 모터 설정 마운트 구성 마운트 제어


-- 조건부

-- 비행제어
insert into mission_list(name,description)  values('change_speed','기체속도변경');
--착륙시작마커

-- 카메라
insert into mission_list(name,description)  values('roi_setting','관심영역(ROI)설정');
insert into mission_list(name,description)  values('roi_setting','관심영역(ROI)설정');
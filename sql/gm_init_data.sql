-- 광명 개발 db 쿼리 작업
-- 개소 삭제
--delete from t_station;
-- 스마트정류장 개소 데이터
-- 철산역2번출구_14128, 37.47588847099903, 126.86795945284835
-- 철산2동행정복지_14115, 37.48490802415163, 126.86636347252329
-- 광명시의원앞_14086, 37.47794477957908, 126.86377557839151
-- 광명시청건너_14088, 37.47894482549452, 126.86371158125836
-- 광명종합복지관_14362, 37.48081600903785, 126.8501437360373
-- 광명시민회관_14239, 37.476437828978746, 126.86376418197553
-- 광명경찰서_14117, 37.47490255508231, 126.86656782497457

INSERT INTO public.t_station (station_name, station_kind, administ_zone, address, station_image, station_compet_dt, station_size, station_material, latitude, longitude)
VALUES ('철산역2번출구_14128', 62, '41210102', '경기 광명시 철산동 526', null, null, null, null, 37.47588847099903, 126.86795945284835);
INSERT INTO public.t_station (station_name, station_kind, administ_zone, address, station_image, station_compet_dt, station_size, station_material, latitude, longitude)
VALUES ('철산2동행정복지_14115', 62, '41210102', '경기 광명시 철산동 468-1', null, null, null, null, 37.48490802415163, 126.86636347252329);
INSERT INTO public.t_station (station_name, station_kind, administ_zone, address, station_image, station_compet_dt, station_size, station_material, latitude, longitude)
VALUES ('광명시의원앞_14086', 62, '41210102', '경기 광명시 철산동 222-1', null, null, null, null, 37.47794477957908, 126.86377557839151);
INSERT INTO public.t_station (station_name, station_kind, administ_zone, address, station_image, station_compet_dt, station_size, station_material, latitude, longitude)
VALUES ('광명시청건너_14088', 62, '41210102', '경기 광명시 철산동 468-1', null, null, null, null, 37.47894482549452, 126.86371158125836);
INSERT INTO public.t_station (station_name, station_kind, administ_zone, address, station_image, station_compet_dt, station_size, station_material, latitude, longitude)
VALUES ('광명종합복지관_14362', 62, '41210102', '경기 광명시 광명동 158-970', null, null, null, null, 37.48081600903785, 126.8501437360373);
INSERT INTO public.t_station (station_name, station_kind, administ_zone, address, station_image, station_compet_dt, station_size, station_material, latitude, longitude)
VALUES ('광명시민회관_14239', 62, '41210102', '경기 광명시 철산동 467-1', null, null, null, null, 37.476437828978746, 126.86376418197553);
INSERT INTO public.t_station (station_name, station_kind, administ_zone, address, station_image, station_compet_dt, station_size, station_material, latitude, longitude)
VALUES ('광명경찰서_14117', 62, '41210102', '경기 광명시 철산동 418', null, null, null, null, 37.47490255508231, 126.86656782497457);

UPDATE t_station SET administ_zone = fn_lonlat_to_emdcode(longitude, latitude);

select * from t_station;

select * from t_facility;

select * from t_facility_opt;

-- 아래는 테스트 쿼리
--스마트폴 실시간
SELECT t1.event_seq,
       v1.code_value as event_kind,
       v2.code_value as event_grade,
       v3.code_value as event_proc_stat,
       t1.event_address,
       t1.event_start_dt,
       t1.event_end_dt,
       t1.event_manager,
       t1.event_mng_dt,
       t1.event_mng_content,
       t1.insert_dt,
       t1.station_seq,
       t1.facility_seq,
       t1.event_message,
       v1.code_name  AS event_kind_name,
       v2.code_name  AS event_grade_name,
       v3.code_name  AS event_proc_stat_name,
       t2.longitude,
       t2.latitude
FROM t_event t1
         INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq
         INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq
         INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq
         INNER JOIN t_station t2 ON t1.station_seq = t2.station_seq
WHERE (v3.code_value in ('1', '2', '3'));

SELECT t1.station_seq,
       t1.station_name,
       t1.station_kind,
       t1.administ_zone,
       t1.station_image,
       t1.station_compet_dt,
       t1.station_size,
       t1.station_material,
       t1.latitude,
       t1.longitude,
       t2.code_name  AS station_kind_name,
       t2.code_value AS station_kind_value,
       t3.emd_nm     AS administ_zone_name
FROM t_station t1
         LEFT OUTER JOIN v_facility_station t2 on t1.station_kind = t2.code_seq
         LEFT OUTER JOIN t_area_emd t3 on t1.administ_zone = t3.emd_cd
where t2.code_value = 'smart_station'
ORDER BY t1.station_seq;

select * from v_facility_station;

select * from t_station;






SELECT count(*)
FROM t_station t1
         LEFT OUTER JOIN v_facility_station t2 on t1.station_kind = t2.code_seq
         LEFT OUTER JOIN t_area_emd t3 on t1.administ_zone = t3.emd_cd;



SELECT t1.event_seq, v1.code_value as event_kind, v2.code_value as event_grade, v3.code_value as event_proc_stat, t1.event_address, t1.event_start_dt, t1.event_end_dt, t1.event_manager, t1.event_mng_dt, t1.event_mng_content, t1.insert_dt, t1.station_seq, t1.facility_seq, t1.event_message, v1.code_name AS event_kind_name, v2.code_name AS event_grade_name, v3.code_name AS event_proc_stat_name, t2.longitude, t2.latitude FROM t_event t1 INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq INNER JOIN t_station t2 ON t1.station_seq = t2.station_seq WHERE (v3.code_value in ('9'))

--스마트폴 개소
SELECT t1.station_seq,
       t1.station_name,
       t1.station_kind,
       t1.administ_zone,
       t1.station_image,
       t1.station_compet_dt,
       t1.station_size,
       t1.station_material,
       t1.latitude,
       t1.longitude,
       t2.code_name  AS station_kind_name,
       t2.code_value AS station_kind_value,
       t3.emd_nm     AS administ_zone_name
FROM t_station t1
         LEFT OUTER JOIN v_facility_station t2 on t1.station_kind = t2.code_seq
         LEFT OUTER JOIN t_area_emd t3 on t1.administ_zone = t3.emd_cd
where t1.station_kind = 4
ORDER BY t1.station_seq

SELECT t1.event_seq, v1.code_value as event_kind, v2.code_value as event_grade, v3.code_value as event_proc_stat, t1.event_address, t1.event_start_dt, t1.event_end_dt, t1.event_manager, t1.event_mng_dt, t1.event_mng_content, t1.insert_dt, t1.station_seq, t1.facility_seq, t1.event_message, v1.code_name AS event_kind_name, v2.code_name AS event_grade_name, v3.code_name AS event_proc_stat_name, t2.longitude, t2.latitude FROM t_event t1 INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq INNER JOIN t_station t2 ON t1.station_seq = t2.station_seq WHERE (v3.code_value in ('1', '2', '3'))
SELECT t1.event_seq, v1.code_value as event_kind, v2.code_value as event_grade, v3.code_value as event_proc_stat, t1.event_address, t1.event_start_dt, t1.event_end_dt, t1.event_manager, t1.event_mng_dt, t1.event_mng_content, t1.insert_dt, t1.station_seq, t1.facility_seq, t1.event_message, v1.code_name AS event_kind_name, v2.code_name AS event_grade_name, v3.code_name AS event_proc_stat_name, t2.longitude, t2.latitude FROM t_event t1 INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq INNER JOIN t_station t2 ON t1.station_seq = t2.station_seq WHERE (v3.code_value in ('9'))


--스마트폴 과거이력

--스마트정류장 실시간
--스마트정류장 개소
SELECT t1.station_seq,
       t1.station_name,
       t1.station_kind,
       t1.administ_zone,
       t1.station_image,
       t1.station_compet_dt,
       t1.station_size,
       t1.station_material,
       t1.latitude,
       t1.longitude,
       t2.code_name  AS station_kind_name,
       t2.code_value AS station_kind_value,
       t3.emd_nm     AS administ_zone_name
FROM t_station t1
         LEFT OUTER JOIN v_facility_station t2 on t1.station_kind = t2.code_seq
         LEFT OUTER JOIN t_area_emd t3 on t1.administ_zone = t3.emd_cd
where t1.station_kind = 62
ORDER BY t1.station_seq;


select * from t_station


--스마트정류장 과거이력


--조회/관리, 개소 관리
SELECT t1.station_seq,
       t1.station_name,
       t1.station_kind,
       t1.administ_zone,
       t1.station_image,
       t1.station_compet_dt,
       t1.station_size,
       t1.station_material,
       t1.latitude,
       t1.longitude,
       t2.code_name  AS station_kind_name,
       t2.code_value AS station_kind_value,
       t3.emd_nm     AS administ_zone_name,
       t4.in_facility_kind
FROM t_station t1
         LEFT OUTER JOIN v_facility_station t2 on t1.station_kind = t2.code_seq
         LEFT OUTER JOIN t_area_emd t3 on t1.administ_zone = t3.emd_cd
         LEFT OUTER JOIN (SELECT station_seq,
                                 CASE
                                     WHEN cnt = 1 THEN in_facility_kind_name
                                     ELSE CONCAT(in_facility_kind_name, ' 외 ', CAST(cnt - 1 AS VARCHAR(20)),
                                                 '종') END AS in_facility_kind
                          FROM (SELECT t.station_seq,
                                       MIN(t.facility_kind_name) AS in_facility_kind_name,
                                       COUNT(*)                  AS cnt
                                FROM (SELECT t1.station_seq,
                                             t1.facility_kind,
                                             t2.code_name  AS facility_kind_name,
                                             t2.code_value AS facility_kind_value
                                      FROM t_facility t1
                                               LEFT OUTER JOIN v_facility_kind t2 on t1.facility_kind = t2.code_seq
                                      WHERE t2.code_value in
                                            ('lamp_road', 'lamp_walk', 'FMCW', 'GW', 'EMS', 'iot_sensor', 'DRONE',
                                             'DRONE_STATION', 'signage')) t
                                WHERE t.station_seq IS NOT NULL
                                GROUP BY t.station_seq) t) t4 on t1.station_seq = t4.station_seq
WHERE (t4.station_seq IS NOT NULL AND t2.code_value in ('lamp_road', 'smart_station'))
ORDER BY t1.station_seq
    LIMIT 15 OFFSET 0
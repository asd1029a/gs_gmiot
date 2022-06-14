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
VALUES ('철산역2번출구_14128', 62, '41210102', '경기 광명시 철산동 526', null, null, null, null, 37.47588847099903, 126.86795945284835)
     ,('철산2동행정복지_14115', 62, '41210102', '경기 광명시 철산동 468-1', null, null, null, null, 37.48490802415163, 126.86636347252329)
     ,('광명시의원앞_14086', 62, '41210102', '경기 광명시 철산동 222-1', null, null, null, null, 37.47794477957908, 126.86377557839151)
     ,('광명시청건너_14088', 62, '41210102', '경기 광명시 철산동 468-1', null, null, null, null, 37.47894482549452, 126.86371158125836)
     ,('광명종합복지관_14362', 62, '41210102', '경기 광명시 광명동 158-970', null, null, null, null, 37.48081600903785, 126.8501437360373)
     ,('광명시민회관_14239', 62, '41210102', '경기 광명시 철산동 467-1', null, null, null, null, 37.476437828978746, 126.86376418197553)
     ,('광명경찰서_14117', 62, '41210102', '경기 광명시 철산동 418', null, null, null, null, 37.47490255508231, 126.86656782497457);

UPDATE t_station SET administ_zone = fn_lonlat_to_emdcode(longitude, latitude);


select * from t_station order by station_seq desc;

SELECT *
FROM t_common_code c
WHERE c.parent_code_seq in (SELECT t_common_code.code_seq
                            FROM t_common_code
                            WHERE t_common_code.parent_code_seq = 0
                              AND t_common_code.code_id::text = 'facility_kind'::text)
order by code_seq;


INSERT INTO public.t_common_code (code_id, code_name, code_value, parent_code_seq, use_kind, insert_dt, insert_user_seq, update_dt, update_user_seq) VALUES
('air_conditioner', '냉난방기', 'air_conditioner', 16, 'Y', now(), 1, null, null)
,('light', '조명', 'light', 16, 'Y', now(), 1, null, null)
,('air_purification', '공기정화', 'air_purification', 16, 'Y', now(), 1, null, null)
,('pest_trap', '포충기', 'pest_trap', 16, 'Y', now(), 1, null, null)
,('ex_door_button', '출입문 외부버튼', 'ex_door_button', 16, 'Y', now(), 1, null, null)
,('smart_chair', '냉온열의자', 'smart_chair', 16, 'Y', now(), 1, null, null)
,('air_curtain', '에어커튼', 'air_curtain', 16, 'Y', now(), 1, null, null)
,('auto_door', '자동문열림', 'auto_door', 16, 'Y', now(), 1, null, null)
,('no_stop_light', '무정차조명', 'no_stop_light', 16, 'Y', now(), 1, null, null)
,('LED_lighting_panel', 'LED라이트닝패널', 'LED_lighting_panel', 16, 'Y', now(), 1, null, null)
,('snowmelting', '스노우멜팅', 'snowmelting', 16, 'Y', now(), 1, null, null)
,('AED', '자동심장충격기', 'AED', 16, 'Y', now(), 1, null, null)
,('occupancy_sensor', '재실감지', 'occupancy_sensor', 16, 'Y', now(), 1, null, null);


INSERT INTO public.api_param (seq, field_nm, field_map_nm, data_type, required, param_type, value, description, api_id, crypto_type, crypto_key, parent_seq) VALUES
  (1, 'clientId', 'arg0', 'COOKIE', true, 'REQUEST', 'clientId', 'clientId (쿠키값 가져오기)', 46, null, null, 0)
, (2, 'stationId', 'arg1', 'NUMBER', true, 'REQUEST', 'ST1', '스테이션 코드', 46, null, null, 0)
, (3, 'pointPaths', 'arg2', 'SOAP_DATA_PATH', true, 'REQUEST', '', 'xml 파일 값 가져오기', 46, null, null, 0)
, (1, 'return', 'return', 'OBJECT', true, 'RESPONSE', null, '포인트 값 목록', 46, null, null, 0)
, (2, 'pointValues', 'pointValues', 'ARRAY', true, 'RESPONSE', null, null, 46, null, null, 1)
, (3, 'presentValue', 'presentValue', 'STRING', true, 'RESPONSE', null, null, 46, null, null, 2)
, (4, 'settingValue', 'settingValue', 'STRING', true, 'RESPONSE', null, null, 46, null, null, 2)
, (5, 'pointPath', 'pointPath', 'STRING', true, 'RESPONSE', null, null, 46, null, null, 2)
, (6, 'pointState', 'pointState', 'STRING', true, 'RESPONSE', null, null, 46, null, null, 2)
, (7, 'ddcAlarmStatus', 'ddcAlarmStatus', 'STRING', true, 'RESPONSE', null, null, 46, null, null, 2)
, (8, 'ddcCorrectionValue', 'ddcCorrectionValue', 'STRING', true, 'RESPONSE', null, null, 46, null, null, 2);


-- api 추가

INSERT INTO public.api (call_url, method_type, request_body_type, response_body_type, api_type, target_url, target_path, content_type, service_nm, service_prefix, auth_info, tokens) VALUES
    ('gmDataPointList', 'POST', 'OBJECT', 'OBJECT_MAPPING', 'SOAP', 'http://192.168.14.104:9999/bms/ws/PointService', 'http://ws.bms.swc.lge.com/', 'text/xml; charset=utf-8', 'getPointValues', 'ws', 'soplogin_gmForcedLogin', null)

INSERT INTO public.api_param (seq, field_nm, field_map_nm, data_type, required, param_type, value, description, api_id, crypto_type, crypto_key, parent_seq) VALUES
(1, 'clientId', 'arg0', 'COOKIE', true, 'REQUEST', 'clientId', 'clientId (쿠키값 가져오기)', 49, null, null, 0)
    , (2, 'stationId', 'arg1', 'NUMBER', true, 'REQUEST', 'ST1', '스테이션 코드', 49, null, null, 0)
    , (3, 'pointPaths', 'arg2', 'SOAP_DATA_PATH', true, 'REQUEST', '', 'xml 파일 값 가져오기', 49, null, null, 0)
    , (1, 'return', 'return', 'OBJECT', true, 'RESPONSE', null, '포인트 값 목록', 49, null, null, 0)
    , (2, 'pointValues', 'pointValues', 'ARRAY', true, 'RESPONSE', null, null, 49, null, null, 1)
    , (3, 'presentValue', 'presentValue', 'STRING', true, 'RESPONSE', null, null, 49, null, null, 2)
    , (4, 'settingValue', 'settingValue', 'STRING', true, 'RESPONSE', null, null, 49, null, null, 2)
    , (5, 'pointPath', 'pointPath', 'STRING', true, 'RESPONSE', null, null, 49, null, null, 2)
    , (6, 'pointState', 'pointState', 'STRING', true, 'RESPONSE', null, null, 49, null, null, 2)
    , (7, 'ddcAlarmStatus', 'ddcAlarmStatus', 'STRING', true, 'RESPONSE', null, null, 49, null, null, 2)
    , (8, 'ddcCorrectionValue', 'ddcCorrectionValue', 'STRING', true, 'RESPONSE', null, null, 49, null, null, 2);

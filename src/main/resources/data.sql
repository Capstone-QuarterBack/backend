-- 먼저 지우고 다시 넣기
DELETE FROM charger_info WHERE station_id = 'station-001';
DELETE FROM charging_station WHERE station_id = 'station-001';

-- 그 다음 새로 삽입
INSERT INTO charging_station (station_id, model, vendor_id, latitude, longitude, address, update_status_time_stamp, station_status)
VALUES ('station-001', 'R1', 'quarterback', 37.5665, 126.9780, '서울특별시 중구 세종대로 110', '2025-04-17T11:20:00', 'inactive');

INSERT INTO charger_info (evse_id, charger_status, update_status_time_stamp, station_id)
VALUES (1, 'Available', '2025-04-20T12:30:00', 'station-001');
INSERT INTO charger_info (evse_id, charger_status, update_status_time_stamp, station_id)
VALUES (2, 'Available', '2025-04-20T12:30:00', 'station-001');
INSERT INTO charger_info (evse_id, charger_status, update_status_time_stamp, station_id)
VALUES (3, 'Available', '2025-04-20T12:30:00', 'station-001');

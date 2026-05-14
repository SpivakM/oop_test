-- ========================
--  ROUTES
-- ========================
INSERT INTO routes (origin, destination, distance_km) VALUES ('Львів', 'Київ', 540);
INSERT INTO routes (origin, destination, distance_km) VALUES ('Київ', 'Одеса', 475);
INSERT INTO routes (origin, destination, distance_km) VALUES ('Львів', 'Харків', 1120);
INSERT INTO routes (origin, destination, distance_km) VALUES ('Дніпро', 'Запоріжжя', 85);
INSERT INTO routes (origin, destination, distance_km) VALUES ('Київ', 'Львів', 540);

-- ========================
--  VEHICLES
-- ========================
INSERT INTO vehicles (license_plate, vehicle_type, max_weight_kg, max_volume_m3, current_mileage_km, fuel_consumption_per_100km, status)
VALUES ('АА1234ВВ', 'TRUCK', 10000, 60, 45000, 28.5, 'AVAILABLE');

INSERT INTO vehicles (license_plate, vehicle_type, max_weight_kg, max_volume_m3, current_mileage_km, fuel_consumption_per_100km, status)
VALUES ('КА5678МН', 'VAN', 1500, 12, 23000, 12.0, 'AVAILABLE');

INSERT INTO vehicles (license_plate, vehicle_type, max_weight_kg, max_volume_m3, current_mileage_km, fuel_consumption_per_100km, status)
VALUES ('ЛВ9876СС', 'REFRIGERATOR_TRUCK', 8000, 45, 67000, 32.0, 'AVAILABLE');

INSERT INTO vehicles (license_plate, vehicle_type, max_weight_kg, max_volume_m3, current_mileage_km, fuel_consumption_per_100km, status)
VALUES ('ОД1111АА', 'TRUCK', 15000, 90, 12000, 35.0, 'MAINTENANCE');

-- ========================
--  DRIVERS
-- ========================
INSERT INTO drivers (first_name, last_name, license_number, fatigue_level, status)
VALUES ('Іван', 'Петренко', 'АВ123456', 15, 'AVAILABLE');

INSERT INTO drivers (first_name, last_name, license_number, fatigue_level, status)
VALUES ('Олег', 'Коваленко', 'СД789012', 30, 'AVAILABLE');

INSERT INTO drivers (first_name, last_name, license_number, fatigue_level, status)
VALUES ('Микола', 'Шевченко', 'ЕФ345678', 70, 'AVAILABLE');

INSERT INTO drivers (first_name, last_name, license_number, fatigue_level, status)
VALUES ('Василь', 'Бондаренко', 'ГЖ901234', 10, 'ON_LEAVE');

-- ========================
--  CARGO
-- ========================
INSERT INTO cargo (name, weight_kg, volume_m3, cargo_type, status)
VALUES ('Побутова техніка', 800, 5.5, 'STANDARD', 'ON_WAREHOUSE');

INSERT INTO cargo (name, weight_kg, volume_m3, cargo_type, status)
VALUES ('Скляні вироби', 200, 2.0, 'FRAGILE', 'ON_WAREHOUSE');

INSERT INTO cargo (name, weight_kg, volume_m3, cargo_type, status)
VALUES ('Хімічні реагенти', 500, 3.0, 'DANGEROUS', 'ON_WAREHOUSE');

INSERT INTO cargo (name, weight_kg, volume_m3, cargo_type, status)
VALUES ('Морозиво та заморожені продукти', 1200, 8.0, 'REFRIGERATED', 'ON_WAREHOUSE');

INSERT INTO cargo (name, weight_kg, volume_m3, cargo_type, status)
VALUES ('Меблі', 1500, 18.0, 'STANDARD', 'ON_WAREHOUSE');

INSERT INTO cargo (name, weight_kg, volume_m3, cargo_type, status)
VALUES ('Фармацевтика (термочутлива)', 150, 1.2, 'REFRIGERATED', 'ON_WAREHOUSE');

INSERT INTO cargo (name, weight_kg, volume_m3, cargo_type, status)
VALUES ('Промислові запчастини', 3000, 12.0, 'STANDARD', 'ON_WAREHOUSE');
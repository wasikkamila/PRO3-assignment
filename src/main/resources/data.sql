-- Demo data for the Central Traceability Store

-- Station 1: registered animals
INSERT INTO animal (registration_number, weight, arrival_time) VALUES ('A-1001', 112.5, '2026-10-01 07:15:00');
INSERT INTO animal (registration_number, weight, arrival_time) VALUES ('A-1002', 108.0, '2026-10-01 07:40:00');
INSERT INTO animal (registration_number, weight, arrival_time) VALUES ('A-1003', 115.2, '2026-10-01 08:05:00');
-- A-1004 has arrived but has not been cut yet (no parts, no products)
INSERT INTO animal (registration_number, weight, arrival_time) VALUES ('A-1004', 101.7, '2026-10-01 08:30:00');

-- Station 2: trays (one part type per tray)
INSERT INTO tray (id, part_type, max_weight, current_weight) VALUES ('T-01', 'LEG',      40.0, 38.5);
INSERT INTO tray (id, part_type, max_weight, current_weight) VALUES ('T-02', 'RIB',      30.0, 17.5);
INSERT INTO tray (id, part_type, max_weight, current_weight) VALUES ('T-03', 'LOIN',     30.0, 18.0);
INSERT INTO tray (id, part_type, max_weight, current_weight) VALUES ('T-04', 'SHOULDER', 30.0, 17.8);

-- Station 2: parts, each with a reference to its animal and its tray
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-01', 'LEG',      9.8, 'A-1001', 'T-01');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-02', 'LEG',      9.6, 'A-1001', 'T-01');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-03', 'RIB',      8.9, 'A-1001', 'T-02');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-04', 'LOIN',     9.1, 'A-1001', 'T-03');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-05', 'LEG',      9.4, 'A-1002', 'T-01');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-06', 'RIB',      8.6, 'A-1002', 'T-02');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-07', 'LOIN',     8.9, 'A-1002', 'T-03');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-08', 'SHOULDER', 8.7, 'A-1002', 'T-04');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-09', 'SHOULDER', 9.1, 'A-1003', 'T-04');
INSERT INTO animal_part (id, type, weight, animal_registration_number, tray_id) VALUES ('PT-10', 'LEG',      9.7, 'A-1003', 'T-01');

-- Station 3: products
INSERT INTO product (id, type, created_at) VALUES ('P-5001', 'PART_PACKAGE', '2026-10-01 12:00:00');
INSERT INTO product (id, type, created_at) VALUES ('P-5002', 'PART_PACKAGE', '2026-10-01 12:10:00');
INSERT INTO product (id, type, created_at) VALUES ('P-5003', 'HALF_ANIMAL',  '2026-10-01 12:30:00');
INSERT INTO product (id, type, created_at) VALUES ('P-5004', 'PART_PACKAGE', '2026-10-01 12:45:00');

-- P-5001: package of two legs, both from A-1001
INSERT INTO product_part (product_id, part_id) VALUES ('P-5001', 'PT-01');
INSERT INTO product_part (product_id, part_id) VALUES ('P-5001', 'PT-02');
-- P-5002: package with one leg from A-1002
INSERT INTO product_part (product_id, part_id) VALUES ('P-5002', 'PT-05');
-- P-5003: "half an animal" - parts from three different animals
INSERT INTO product_part (product_id, part_id) VALUES ('P-5003', 'PT-10');
INSERT INTO product_part (product_id, part_id) VALUES ('P-5003', 'PT-03');
INSERT INTO product_part (product_id, part_id) VALUES ('P-5003', 'PT-07');
INSERT INTO product_part (product_id, part_id) VALUES ('P-5003', 'PT-09');
-- P-5004: package with one rib from A-1002
INSERT INTO product_part (product_id, part_id) VALUES ('P-5004', 'PT-06');

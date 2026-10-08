

DROP TABLE IF EXISTS product_part CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS animal_part CASCADE;
DROP TABLE IF EXISTS tray CASCADE;
DROP TABLE IF EXISTS animal CASCADE;

-- Station 1: animals are weighed and registered
CREATE TABLE animal
(
    registration_number VARCHAR(50) PRIMARY KEY,
    weight              DOUBLE PRECISION NOT NULL,
    arrival_time        TIMESTAMP        NOT NULL
);

-- Station 2: trays hold one type of part, up to a maximum weight
CREATE TABLE tray
(
    id             VARCHAR(50) PRIMARY KEY,
    part_type      VARCHAR(50)      NOT NULL,
    max_weight     DOUBLE PRECISION NOT NULL,
    current_weight DOUBLE PRECISION DEFAULT 0 NOT NULL
);

-- Station 2: each part references the animal it comes from and the tray it is put in
CREATE TABLE animal_part
(
    id                         VARCHAR(50) PRIMARY KEY,
    type                       VARCHAR(50)      NOT NULL,
    weight                     DOUBLE PRECISION NOT NULL,
    animal_registration_number VARCHAR(50)      NOT NULL,
    tray_id                    VARCHAR(50)      NOT NULL,
    FOREIGN KEY (animal_registration_number) REFERENCES animal (registration_number),
    FOREIGN KEY (tray_id) REFERENCES tray (id)
);

-- Station 3: packed products
CREATE TABLE product
(
    id         VARCHAR(50) PRIMARY KEY,
    type       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL
);

-- A product contains 1..* parts (many-to-many, see domain model)
CREATE TABLE product_part
(
    product_id VARCHAR(50) NOT NULL,
    part_id    VARCHAR(50) NOT NULL,
    PRIMARY KEY (product_id, part_id),
    FOREIGN KEY (product_id) REFERENCES product (id),
    FOREIGN KEY (part_id) REFERENCES animal_part (id)
);

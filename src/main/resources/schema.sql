DROP TABLE IF EXISTS revenue;
DROP TABLE IF EXISTS rent;
DROP TABLE IF EXISTS return_process;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS branch;
DROP TABLE IF EXISTS car_rental;

CREATE TABLE car_rental
(
    car_rental_id BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    domain        VARCHAR(255) NOT NULL,
    address       VARCHAR(255) NOT NULL,
    owner         VARCHAR(255) NOT NULL,
    logo          VARCHAR(255) NOT NULL
);

CREATE TABLE branch
(
    branch_id     BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    address       VARCHAR(255) NOT NULL,
    car_rental_id BIGINT,
    FOREIGN KEY (car_rental_id) REFERENCES car_rental (car_rental_id)
);

CREATE TABLE car
(
    car_id     BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    make       VARCHAR(255)  NOT NULL,
    model      VARCHAR(255)  NOT NULL,
    body_style VARCHAR(255)  NOT NULL,
    year       INT,
    colour     VARCHAR(255)  NOT NULL,
    mileage    INT,
    status     TINYINT       NOT NULL,
    price      DECIMAL(9, 2) NOT NULL,
    branch_id  BIGINT,
    FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
);

CREATE TABLE employee
(
    employee_id BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    surname     VARCHAR(255) NOT NULL,
    position    TINYINT      NOT NULL,
    branch_id   BIGINT,
    FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
);

CREATE TABLE client
(
    client_id BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    surname   VARCHAR(255) NOT NULL,
    address   VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    branch_id BIGINT,
    FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
);

CREATE TABLE reservation
(
    reservation_id  BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    start_date      DATE,
    end_date        DATE,
    price           DECIMAL(7, 2),
    start_branch_id BIGINT,
    end_branch_id   BIGINT,
    car_id          BIGINT,
    client_id       BIGINT,
    FOREIGN KEY (start_branch_id) REFERENCES branch (branch_id),
    FOREIGN KEY (end_branch_id) REFERENCES branch (branch_id),
    FOREIGN KEY (car_id) REFERENCES car (car_id),
    FOREIGN KEY (client_id) REFERENCES client (client_id)
);

CREATE TABLE rent
(
    rent_id        BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    comments       VARCHAR(255) NOT NULL,
    rent_date      DATE         NOT NULL,
    reservation_id BIGINT       NOT NULL,
    employee_id BIGINT,
    FOREIGN KEY (reservation_id) REFERENCES reservation (reservation_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);


CREATE TABLE revenue
(
    revenue_id BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    amount     DECIMAL
);

CREATE TABLE return_process
(
    return_id      BIGINT AUTO_INCREMENT UNIQUE PRIMARY KEY,
    comments       VARCHAR(255)  NOT NULL,
    return_date    DATE          NOT NULL,
    upcharge       DECIMAL(7, 2) NOT NULL,
    reservation_id BIGINT        NOT NULL,
    employee_id BIGINT,
    FOREIGN KEY (reservation_id) REFERENCES reservation (reservation_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);
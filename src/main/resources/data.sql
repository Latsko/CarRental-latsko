INSERT INTO car_rental (name, domain, address, owner, logo)
VALUES ("Car Rental Name", "www.carRental.pl", "44 Serenity Road, Meadowbrook Grove, Tranquil Springs, TX 13579",
        "John Doe", "Logo");


INSERT INTO branch (name, address, car_rental_id)
VALUES ('Krakow Branch', '567 Parkowa Street', 1),
       ('Gdansk Branch', '890 Portowa Avenue', 1),
       ('Warsaw Branch', '234 Sienkiewicza Street', 1);

INSERT INTO car (make, model, body_style, year, colour, mileage, status, price, branch_id)
VALUES ('Toyota', 'Corolla', 'Sedan', 2023, 'Silver', 15000, 0, 250.00, 1),
       ('Honda', 'Civic', 'Hatchback', 2022, 'Blue', 20000, 1, 220.00, 2),
       ('Ford', 'Mustang', 'Coupe', 2023, 'Red', 12000, 2, 350.00, 3),
       ('Chevrolet', 'Malibu', 'Sedan', 2022, 'Black', 18000, 0, 280.00, 1),
       ('Nissan', 'Altima', 'Sedan', 2023, 'White', 16000, 1, 260.00, 2),
       ('Audi', 'A4', 'Sedan', 2023, 'Gray', 14000, 2, 320.00, 3),
       ('BMW', '3 Series', 'Sedan', 2022, 'Silver', 19000, 0, 380.00, 1),
       ('Mercedes-Benz', 'C-Class', 'Sedan', 2023, 'Black', 17000, 1, 400.00, 2),
       ('Volkswagen', 'Passat', 'Sedan', 2022, 'Blue', 15000, 2, 270.00, 3),
       ('Hyundai', 'Elantra', 'Sedan', 2023, 'Red', 13000, 0, 240.00, 1),
       ('Tesla', 'Model S', 'Sedan', 2023, 'White', 10000, 1, 600.00, 2),
       ('Kia', 'Seltos', 'SUV', 2022, 'Gray', 17000, 2, 230.00, 3),
       ('Subaru', 'Outback', 'Wagon', 2023, 'Green', 12000, 0, 270.00, 1),
       ('Mazda', 'CX-5', 'SUV', 2022, 'Red', 14000, 1, 260.00, 2),
       ('Jeep', 'Wrangler', 'SUV', 2023, 'Black', 9000, 2, 350.00, 3),
       ('Lexus', 'RX', 'SUV', 2022, 'Silver', 16000, 0, 400.00, 1),
       ('Infiniti', 'Q50', 'Sedan', 2023, 'Blue', 11000, 1, 320.00, 2),
       ('Volvo', 'XC60', 'SUV', 2022, 'White', 13000, 2, 380.00, 3),
       ('Porsche', '911', 'Coupe', 2023, 'Red', 8000, 0, 1200.00, 1),
       ('Ferrari', '488 GTB', 'Coupe', 2022, 'Yellow', 5000, 1, 2500.00, 2);

INSERT INTO employee (name, surname, position, branch_id)
VALUES ('John', 'Doe', 0, 1),
       ('Jane', 'Smith', 1, 2),
       ('Alice', 'Johnson', 0, 3),
       ('Bob', 'Williams', 1, 1),
       ('Emma', 'Brown', 0, 2),
       ('Michael', 'Jones', 1, 3),
       ('Olivia', 'Davis', 0, 1),
       ('William', 'Garcia', 1, 2),
       ('Sophia', 'Miller', 0, 3),
       ('Liam', 'Martinez', 1, 1),
       ('Ava', 'Wilson', 0, 2),
       ('James', 'Anderson', 1, 3),
       ('Ella', 'Taylor', 0, 1),
       ('Alexander', 'Clark', 1, 2),
       ('Grace', 'Hill', 0, 3),
       ('Mia', 'Scott', 1, 1),
       ('Benjamin', 'Green', 0, 2),
       ('Chloe', 'Adams', 1, 3),
       ('Lucas', 'Wright', 0, 1),
       ('Madison', 'Baker', 1, 2);

INSERT INTO client (name, surname, address, email, branch_id)
VALUES ('Sophie', 'Bennett', '111 Hill St', 'sophie@example.com', 1),
       ('Jacob', 'Fisher', '222 River St', 'jacob@example.com', 2),
       ('Nora', 'Clarkson', '333 Oak St', 'nora@example.com', 3),
       ('Gabriel', 'Powell', '444 Pine St', 'gabriel@example.com', 1),
       ('Lillian', 'Roberts', '555 Elm St', 'lillian@example.com', 2),
       ('Caleb', 'Hughes', '666 Birch St', 'caleb@example.com', 3),
       ('Violet', 'Harrison', '777 Spruce St', 'violet@example.com', 1),
       ('Levi', 'Foster', '888 Cedar St', 'levi@example.com', 2),
       ('Stella', 'Murray', '999 Walnut St', 'stella@example.com', 3),
       ('Hudson', 'Simmons', '101 Cherry St', 'hudson@example.com', 1);


INSERT INTO reservation (start_date, end_date, price, start_branch_id, end_branch_id, car_id, client_id)
VALUES ('2024-01-10', '2024-01-15', 350.00, 1, 2, 1, 1),
       ('2024-01-12', '2024-01-20', 400.00, 2, 3, 2, 2),
       ('2024-01-14', '2024-01-18', 300.00, 3, 1, 3, 3),
       ('2024-01-16', '2024-01-22', 450.00, 1, 2, 4, 4),
       ('2024-01-18', '2024-01-25', 500.00, 2, 3, 5, 5),
       ('2024-01-20', '2024-01-27', 380.00, 3, 1, 6, 6),
       ('2024-01-22', '2024-01-29', 420.00, 1, 2, 7, 7),
       ('2024-01-24', '2024-01-31', 480.00, 2, 3, 8, 8),
       ('2024-01-26', '2024-02-02', 520.00, 3, 1, 9, 9),
       ('2024-01-28', '2024-02-04', 390.00, 1, 2, 10, 10);

INSERT INTO rent (comments, rent_date, reservation_id, employee_id)
VALUES ('Good condition', '2024-01-10', 1, 1),
       ('Needs cleaning', '2024-01-12', 2, 2),
       ('Smooth ride', '2024-01-14', 3, 3),
       ('Great experience', '2024-01-16', 4, 4),
       ('Minor issue with AC', '2024-01-18', 5, 5),
       ('Comfortable interior', '2024-01-20', 6, 6),
       ('Excellent mileage', '2024-01-22', 7, 7),
       ('Fantastic handling', '2024-01-24', 8, 8);

INSERT INTO return_process (comments, return_date, upcharge, reservation_id, employee_id)
VALUES ('Clean and tidy', '2024-01-15', 50.00, 1, 1),
       ('Minor scratches', '2024-01-17', 30.00, 2, 2),
       ('Fuel tank full', '2024-01-19', 20.00, 3, 3),
       ('Smooth return', '2024-01-21', 10.00, 4, 4),
       ('Needs cleaning', '2024-01-23', 40.00, 5, 5),
       ('Issues with brakes', '2024-01-25', 60.00, 6, 6),
       ('Excellent condition', '2024-01-27', 15.00, 7, 7),
       ('Great experience', '2024-01-29', 25.00, 8, 8);
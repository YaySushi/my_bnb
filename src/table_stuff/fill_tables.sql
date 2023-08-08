USE `mybnb`;

INSERT INTO Amenities VALUES ('Essentials', 'Wifi', '');
INSERT INTO Amenities VALUES ('Essentials', 'Kitchen', '');
INSERT INTO Amenities VALUES ('Essentials', 'Washer', '');
INSERT INTO Amenities VALUES ('Essentials', 'Dryer', '');
INSERT INTO Amenities VALUES ('Essentials', 'Air conditioning', '');
INSERT INTO Amenities VALUES ('Essentials', 'Heating', '');
INSERT INTO Amenities VALUES ('Essentials', 'TV', '');
INSERT INTO Amenities VALUES ('Essentials', 'Dedicated workspace', '');
INSERT INTO Amenities VALUES ('Essentials', 'Hair dryer', '');
INSERT INTO Amenities VALUES ('Essentials', 'Iron', '');
INSERT INTO Amenities VALUES ('Features', 'Pool', '');
INSERT INTO Amenities VALUES ('Features', 'Hot tub', '');
INSERT INTO Amenities VALUES ('Features', 'Free parking', '');
INSERT INTO Amenities VALUES ('Features', 'EV charger', '');
INSERT INTO Amenities VALUES ('Features', 'Crib', '');
INSERT INTO Amenities VALUES ('Features', 'Gym', '');
INSERT INTO Amenities VALUES ('Features', 'BBQ grill', '');
INSERT INTO Amenities VALUES ('Features', 'Breakfast', '');
INSERT INTO Amenities VALUES ('Features', 'Indoor fireplace', '');
INSERT INTO Amenities VALUES ('Features', 'Smoking allowed', '');
INSERT INTO Amenities VALUES ('Location', 'Beachfront', '');
INSERT INTO Amenities VALUES ('Location', 'Waterfront', '');
INSERT INTO Amenities VALUES ('Location', 'Ski-in/ski-out', '');
INSERT INTO Amenities VALUES ('Safety', 'Smoke alarm', '');
INSERT INTO Amenities VALUES ('Safety', 'Carbon monoxide alarm', '');

INSERT INTO Addresses (address, city, province, country, postalcode) 
		-- host addresses
VALUES 	('1 host street', 'scarborough', 'ontario', 'canada', 'm2j3e5'),  -- aid 1
		('2 host street', 'scarborough', 'ontario', 'canada', 'm2j3e5'),
        
        -- renter addresses
        ('1 renter street', 'scarborough', 'ontario', 'canada', 'm1j4e1'),  -- aid 3
        ('2 renter street', 'scarborough', 'ontario', 'canada', 'm1j4e1'),
        ('3 renter street', 'scarborough', 'ontario', 'canada', 'm1j4e1'),
        ('4 renter street', 'scarborough', 'ontario', 'canada', 'm1j4e1'),  -- aid 6
        
        -- listing addresses
		('900 cold street', 'icity A', 'naples', 'italy', 'n2j3k4'),  -- aid 7
        ('901 cold street', 'city A', 'naples', 'italy', 'n2j3k5'),
        ('10 hot street', 'city B', 'naples', 'italy', 'n2j3i9'),
        ('50 hot street', 'city C', 'naples', 'italy', 'n2j3m1'),  -- aid 10
        
        ('small road', 'city F', 'barcelona', 'spain', 'n2j3m1'),  -- aid 11
        ('medium road', 'city F', 'barcelona', 'spain', 'n2j3m1'),
        ('large road', 'city F', 'barcelona', 'spain', 'n2j3m1');  -- aid 13


INSERT INTO Users (sin, firstname, lastname, email, password, dob, occupation, aid)
		-- hosts
VALUES 	(101, 'ali', 'host', 'a@gmail.com', 'a', '1990-01-01', 'student', 1),      -- hid 1
		(102, 'vinesh', 'host', 'b@gmail.com', 'b', '1990-01-01', 'student', 2), -- hid 2
        
		-- renters
        (200, 'josh', 'renter', 'j@gmail.com', 'j', '2001-10-10', 'athlete', 3),  -- uid 3
        (240, 'mary', 'renter', 'm@gmail.com', 'm', '2001-01-25', 'teacher', 4),  -- uid 4
        (250, 'sarah', 'renter', 's@gmail.com', 's', '2001-01-25', 'fisher', 5),  -- uid 5
        (260, 'declan', 'renter', 'd@gmail.com', 'd', '2001-01-25', 'doctor', 6); -- uid 6

INSERT INTO Hosts (hid)
VALUES	(1),
		(2);

INSERT INTO Renters (rid, creditcard)
VALUES	(3, '1234 5678 1234 0890'),
		(4, '1008 1382 7833 3339'),
        (5, '1234 5678 1234 0890'),
        (6, '1234 5678 1234 0890');

INSERT INTO Listings (aid, lat, lng, listingtype, hid)
		-- ali's listings
VALUES	(7, -80.02, 12.27, 'Apartment', 1),  -- lid 1
        (8, -85.9, 12.4, 'Apartment', 1),    -- lid 2
        (9, -71.11, 12.08, 'Hotel', 1),      -- lid 3
        (10, 15.2, 0.27, 'Hotel', 1),        -- lid 4
        
		-- benny's listings
        (11, 48, -28, 'Apartment', 2),       -- lid 5
        (12, -79.80, -30.8, 'Apartment', 2), -- lid 6
        (13, 67.2, -4.2, 'Apartment', 2);    -- lid 7


INSERT INTO Availabilities (date, status, price, lid)
VALUES 	('2023-06-14', 'Booked', 80, 1),        -- should not show up during commenting.
		('2023-07-28', 'Booked', 90, 1),
		('2023-07-29', 'Booked', 90, 1),
		('2023-08-12', 'Available', 140, 1),
		('2023-08-13', 'Available', 140, 1),
        ('2023-08-14', 'Available', 145, 1),
        ('2023-08-15', 'Available', 150, 1),
        ('2023-08-16', 'Available', 150, 1),
        
        ('2023-08-15', 'Available', 200, 2),
		('2023-08-16', 'Available', 200, 2),
        ('2023-08-17', 'Available', 200, 2),
        ('2023-08-18', 'Available', 400, 2),
        
        ('2023-09-20', 'Available', 2000, 3),
        ('2023-09-21', 'Available', 2000, 3),
        
        ('2023-09-15', 'Available', 400, 4),
		('2023-09-16', 'Available', 400, 4),
        ('2023-09-17', 'Available', 400, 4),
        ('2023-09-18', 'Available', 400, 4),
        
        ('2023-08-01', 'Available', 24, 5),
        ('2023-08-02', 'Available', 26, 5),
        ('2023-09-15', 'Available', 600, 5),
		('2023-09-16', 'Available', 600, 5),
        ('2023-09-17', 'Available', 600, 5),
        ('2023-09-18', 'Available', 600, 5),
        ('2023-11-11', 'Available', 600, 5),
        ('2023-11-15', 'Available', 600, 5),
        
        ('2023-10-01', 'Booked', 700, 6),
		('2023-10-02', 'Booked', 700, 6),
        ('2023-10-03', 'Available', 700, 6),
        
        ('2021-03-18', 'Booked', 250, 7),		-- should not show up during commenting.
		('2021-03-19', 'Booked', 250, 7),		-- should not show up during commenting.
        ('2021-04-06', 'Booked', 100, 7),		-- should not show up during commenting.
        ('2023-10-27', 'Available', 2400, 7);


INSERT INTO has (amenity, lid)
VALUES  ("Wifi", 1),
		("Hot tub", 1),
        ("Kitchen", 1),
        
        ("Wifi", 2),
		("Hot tub", 2),
        ("Kitchen", 2),
        
        ("Wifi", 3),
        ("Heating", 3),
        
        ("Breakfast", 4),
        ("Free parking", 4),
        
        ("Heating", 5),
        ("Hot tub", 5),
        ("Free parking", 5),
        ("EV charger", 5),
        ("Gym", 5),
        ("Crib", 5),
        ("BBQ grill", 5),
        ("Breakfast", 5),
        ("Indoor fireplace", 5),
        
        ("BBQ grill", 6),
        ("Breakfast", 6),
		
        ("Beachfront", 7),
        ("Smoke alarm", 7),
        ("Carbon monoxide alarm", 7);


INSERT INTO Bookings (rating, comment, cost, startdate, enddate, cancelled, lid, rid)
VALUES  (null, null, 80, '2023-06-14', '2023-06-14',   false, 1, 3),
		(null, null, 180, '2023-07-28', '2023-07-29',  false, 1, 3),
        (null, null, 1200, '2023-11-11', '2023-11-15',  true,  5, 6),
		(null, null, 100, '2023-08-02', '2023-08-02',  false,  5, 6),
        (null, null, 1400, '2023-10-01', '2023-10-01', false, 6, 5),
        (null, null, 500, '2023-03-18', '2023-03-19',  false, 7, 6),
        (null, null, 100, '2021-04-06', '2021-04-06',  false, 7, 6);

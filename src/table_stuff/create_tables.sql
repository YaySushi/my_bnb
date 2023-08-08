-- TODO: verify the the policies.
-- TODO: check constraints for comments.

DROP DATABASE IF EXISTS `mybnb`;
CREATE DATABASE `mybnb`;
USE `mybnb`;

CREATE TABLE Addresses (
  aid INTEGER NOT NULL AUTO_INCREMENT,
  address VARCHAR(256) NOT NULL,
  city VARCHAR(24) NOT NULL,
  province VARCHAR(24) NOT NULL, 
  country VARCHAR(24) NOT NULL,
  postalcode VARCHAR(7) NOT NULL,

  PRIMARY KEY (aid)
);



CREATE TABLE Users (
  uid INTEGER NOT NULL AUTO_INCREMENT,
  sin INTEGER NOT NULL,	
  firstname VARCHAR(50) NOT NULL,
  lastname VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  dob DATE NOT NULL,
  occupation VARCHAR(50) NOT NULL,
  aid INTEGER NOT NULL,
  
  PRIMARY KEY (uid),
  
  FOREIGN KEY(aid) REFERENCES Addresses(aid)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  
  UNIQUE (email),
  UNIQUE (sin)
);

CREATE TABLE Hosts (
  hid INTEGER NOT NULL,

  FOREIGN KEY (hid)
  REFERENCES Users(uid)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);

CREATE TABLE Renters (
  rid INTEGER NOT NULL,
  creditcard VARCHAR(50),

  FOREIGN KEY (rid)
  REFERENCES Users(uid)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);

CREATE TABLE Listings (
  lid INTEGER NOT NULL AUTO_INCREMENT,
  aid INTEGER NOT NULL,
  lat DECIMAL(10,5),
  lng DECIMAL(10,5),
  listingtype ENUM('Apartment', 'House', 'Guesthouse', 'Hotel', 'Other') NOT NULL,
  
  hid INTEGER NOT NULL,
  
  PRIMARY KEY (lid),
  
  FOREIGN KEY (aid)
  REFERENCES Addresses(aid)
  ON DELETE RESTRICT
  ON UPDATE CASCADE,
  
  FOREIGN KEY (hid)
  REFERENCES Hosts(hid)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
 
  UNIQUE (lat, lng),
  
  CHECK (lat >= -90 AND lat <= 90),
  CHECK (lng >= -180 AND lng <= 180)
);

CREATE TABLE Bookings (
  bid INTEGER NOT NULL AUTO_INCREMENT,
  rating SMALLINT UNSIGNED,
  comment VARCHAR(512),
  cost DOUBLE NOT NULL,
  startdate DATE NOT NULL,
  enddate DATE NOT NULL,
  cancelled bool NOT NULL DEFAULT false,
  
  lid INTEGER NOT NULL,
  rid INTEGER NOT NULL,
  
  PRIMARY KEY (bid),
  
  FOREIGN KEY (lid)
  REFERENCES Listings(lid)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  
  FOREIGN KEY (rid)
  REFERENCES Renters(rid)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);

CREATE TABLE Availabilities (
  `date` date NOT NULL,
  status ENUM('Available', 'Booked') NOT NULL,
  price DECIMAL NOT NULL,
  lid INTEGER NOT NULL,

  PRIMARY KEY (`date`, lid),
  
  FOREIGN KEY(lid)
  REFERENCES Listings(lid)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);

CREATE TABLE Amenities (
  type ENUM('Essentials', 'Features', 'Location', 'Safety') NOT NULL,
  amenity ENUM('Wifi', 'Kitchen', 'Washer', 'Dryer', 'Air conditioning', 'Heating', 'TV', 'Dedicated workspace', 'Hair dryer', 'Iron', 'Pool', 'Hot tub', 'Free parking', 'EV charger', 'Crib','Gym', 'BBQ grill', 'Breakfast', 'Indoor fireplace', 'Smoking allowed', 'Beachfront', 'Waterfront', 'Ski-in/ski-out', 'Carbon monoxide alarm', 'Smoke alarm') NOT NULL,
  description VARCHAR(512) NOT NULL,
  
  PRIMARY KEY (amenity)
);



CREATE TABLE comments (
  cid INTEGER NOT NULL AUTO_INCREMENT,
  comment VARCHAR(512) NOT NULL,
  rating SMALLINT UNSIGNED NOT NULL,
  rid INT NOT NULL,
  hid INT NOT NULL,
  reviewer ENUM('Host', 'Renter') NOT NULL,

  PRIMARY KEY (cid),

  FOREIGN KEY (rid)
  REFERENCES Renters(rid)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  
  FOREIGN KEY (hid)
  REFERENCES Hosts(hid)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);

CREATE TABLE has (
  amenity ENUM('Wifi', 'Kitchen', 'Washer', 'Dryer', 'Air conditioning', 'Heating', 'TV', 'Dedicated workspace', 'Hair dryer', 'Iron', 'Pool', 'Hot tub', 'Free parking', 'EV charger', 'Crib','Gym', 'BBQ grill', 'Breakfast', 'Indoor fireplace', 'Smoking allowed', 'Beachfront', 'Waterfront', 'Ski-in/ski-out', 'Carbon monoxide alarm', 'Smoke alarm'),
  lid INT NOT NULL,

  FOREIGN KEY (amenity) 
  REFERENCES Amenities(amenity)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  
  FOREIGN KEY (lid)
  REFERENCES Listings(lid)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);
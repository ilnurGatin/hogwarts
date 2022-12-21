CREATE TABLE driver (
    id INTEGER PRIMARY KEY,
    name VARCHAR NOT NULL,
    age INTEGER CHECK ( age > 0 ),
    license BOOLEAN,
    car_id INTEGER REFERENCES car (id)
);

CREATE TABLE car (
    id INTEGER PRIMARY KEY,
    type VARCHAR NOT NULL,
    model VARCHAR NOT NULL,
    cost NUMERIC(11,2) NOT NULL
);
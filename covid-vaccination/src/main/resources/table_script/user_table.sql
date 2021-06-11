create table user (
	id integer not null auto_increment,
    first_name VARCHAR(20) not null,
    last_name VARCHAR(30) not null,
    serial_number VARCHAR(11) unique not null,
	birth_date date not null,
    vaccine VARCHAR(40) not null,
    dose   int not null,
    vaccination_date date,
    first_dose_date date,
    primary key(id)
);

drop table user;


create table vaccine 
(
	id integer not null auto_increment,
    name varchar(50) not null,
    dose_first bigint,
    dose_second bigint,
    primary key(id)
);
drop table vaccine;
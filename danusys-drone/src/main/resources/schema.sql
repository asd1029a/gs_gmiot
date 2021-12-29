create table mission_details ( id SERIAL PRIMARY KEY ,
                      name varchar(50) not null,
                      index int,
                      x float8,
                      y float8,
                      z float8
);

create table mission(
                       id SERIAL PRIMARY KEY ,
                       name varchar(50) not null
);


create table mission_list(

    id SERIAL PRIMARY KEY,
    name varchar(100) not null,
    description varchar(100) not null
);
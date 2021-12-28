create table misson_details ( id SERIAL PRIMARY KEY ,
                      name varchar(50) not null,
                      index int,
                      x float8,
                      y float8,
                      z float8
)

create table misson(
                       id SERIAL PRIMARY KEY ,
                       name varchar(50) not null
)
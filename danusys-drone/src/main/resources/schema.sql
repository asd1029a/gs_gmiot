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


-- 미션 리스트

create table mission_list(

    id SERIAL PRIMARY KEY,
    name varchar(100) not null,
    description varchar(100) not null
);


-- 드론
create table drone
(
    id                 bigserial
        constraint drone_pk
            primary key,
    drone_device_name  varchar(100),
    misson_id          varchar(50),
    thumbnail_imh      varchar(300),
    thumbnail_real_img varchar(300),
    insert_user_id     bigint,
    insert_dt          date,
    update_user_id     bigint,
    update_dt          date
);


--드론 디테일
-- auto-generated definition
create table drone_details
(
    id                              bigint not null,
    status                          varchar(50),
    drone_id                        bigint,
    drone_device_name               varchar(100),
    location                        varchar(200),
    size                            varchar(200),
    weight                          double precision,
    maximum_operating_distance      double precision,
    operating_temperature_range_min double precision,
    operating_temperature_range_max double precision,
    sim_number                      varchar(50),
    master_manager                  varchar(50),
    sub_manager                     varchar(50),
    manufacturer                    varchar(50),
    type                            varchar(50),
    maximum_management_altitude     integer,
    maximum_operating_speed         integer,
    maximun_speed                   integer,
    insert_user_id                  bigint,
    insert_dt                       date,
    update_user_id                  bigint,
    update_dt                       date
);


--미션

create table mission
(
    id              bigint default nextval('misson_id_seq1'::regclass) not null
        constraint misson_pkey1
            primary key,
    name            varchar(50)                                        not null,
    insert_dt       timestamp,
    update_dt       timestamp,
    insert_user_seq integer,
    update_user_seq integer
);

--미션 디테일

create table mission_details
(
    id              bigint           default nextval('misson_id_seq'::regclass) not null
        constraint misson_pkey
            primary key,
    name            varchar(50)                                                 not null,
    index           integer,
    gps_x           double precision,
    gps_y           double precision,
    alt             double precision,
    mission_id      bigint
        constraint fk_misson_details_misson_id
            references mission,
    speed           double precision,
    time            double precision default 0,
    insert_dt       timestamp,
    update_dt       integer,
    insert_user_seq integer,
    update_user_seq integer
);

insert into user2 values (1, 'admin', 'hansik', 'shin', 'hansik.shink@danusys.com');
insert into user2 values (2, 'kai', 'kai', 'shin', 'kai@gmail.com');



insert into admin_test (id,password,roles,username) values(1,'$2a$12$w9JyiB2Qt4vo3snpJEo.SOvFSgeX2y.kdqfU6gx5JuLO9YfioyZrK','admin','admin');

insert into admin_test values(1,'admin','1234','관리자','123');
insert into admin_test values(1,'admin','1234','관리자','admin','1234','admin');



update admin_test

set
    roles ='ROLE_ADMIN'
where
        username='admin';




update admin_test

set
    password ='{SHA-256}bde57f35c6f48946231f1b2c777e6faf9c58cbe5c38012c1cdfb22cf2983f623'
where
        username='admin3';


insert into admin_test (id,password,roles,username)  values(2,'BDE57F35C6F48946231F1B2C777E6FAF9C58CBE5C38012C1CDFB22CF2983F623','ROLE_ADMIN','admin2');


insert into admin_test (id,password,roles,username)  values(3,'{SHA-256}BDE57F35C6F48946231F1B2C777E6FAF9C58CBE5C38012C1CDFB22CF2983F623','ROLE_ADMIN','admin3');


insert into admin_test (id,password,roles,username) values (4,'{SHA-256}03f6b52348a5ec4720fb59a1c311f23cdeba2f6ce111b1a0e77a6e3944f8e139','ROLE_ADMIN','danu');


//로그인관련

insert into t_user values(6,'test','{SHA-256}6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b');

insert into t_user_group values(5,'groupA','Agroup',now(),5);

insert into t_permit(permit_seq,permit_name, insert_dt, insert_user_seq) values(1,'ROLE_ADMIN',now(),123);

insert into t_user_in_user_group values(5,6,now(),0,1);

insert into t_user_group_permit (user_group_seq, permit_seq,insert_dt,insert_user_seq,idx)values(5,1,now(),8,1);
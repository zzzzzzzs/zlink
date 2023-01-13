CREATE TABLE user_info_source (
                                  `id` INT,
                                  `name` STRING,
                                  `age` INT,
                                  primary key (id) not enforced
) with (
      'connector' = 'mysql-cdc',
      'hostname' = '192.168.25.110',
      'port' = '3367',
      'username' = 'root',
      'password' = '111',
      'database-name' = 'test',
      'table-name' = 'user_info',
      'scan.startup.mode' = 'initial'
      );
CREATE TABLE user_info_sink (
                                `id` INT,
                                `name` STRING,
                                `age` INT,
                                primary key (id) not enforced
) with (
      'connector' = 'jdbc',
      'driver' = 'com.mysql.cj.jdbc.Driver',
      'url' = 'jdbc:mysql://192.168.25.110:3367/cdc',
      'username' = 'root',
      'password' = '111',
      'table-name' = 'user_info'
      );
insert into user_info_sink select * from user_info_source
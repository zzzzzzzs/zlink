CREATE TABLE datagen_1 (
                           f0 INT,
                           f1 INT,
                           f2 STRING
) WITH (
      'connector' = 'datagen',
      'rows-per-second'='5',
      'fields.f0.kind'='sequence',
      'fields.f0.start'='1',
      'fields.f0.end'='1000',
      'fields.f1.min'='1',
      'fields.f1.max'='1000',
      'fields.f2.length'='10'
      );
CREATE TABLE print_table (
                             f0 INT,
                             f1 INT,
                             f2 STRING
) WITH (
      'connector' = 'print'
      );
insert into print_table select * from datagen_1
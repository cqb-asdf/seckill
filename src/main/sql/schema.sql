/*数据库初始化脚本*/

/*创建数据库*/
CREATE DATABASE seckill;

/*进入及使用数据库*/
USE seckill;

/*创建库存表*/
CREATE TABLE seckill(
seckill_id bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
name varchar(120) NOT NULL COMMENT '商品名称',
number int NOT NULL COMMENT '库存数量',
start_time timestamp NOT NULL COMMENT '秒杀开启时间',
end_time timestamp NOT NULL COMMENT '秒杀结束时间',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀创建时间',
PRIMARY KEY(seckill_id),
KEY idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='库存表';

/*初始化库存表数据*/
INSERT INTO seckill(name, number, start_time, end_time)
VALUES
('1500元秒杀iphone7 plus', 500, '2017-06-01 00:00:00', '2017-07-01 00:00:00'),
('1000元秒杀ipad', 800, '2017-06-01 00:00:00', '2017-07-01 00:00:00'),
('600元秒杀华为p9', 1000, '2017-06-01 00:00:00', '2017-07-01 00:00:00'),
('300元秒杀小米', 1500, '2017-06-01 00:00:00', '2017-07-01 00:00:00'),
('100元秒杀蓝牙耳机', 2500, '2017-06-01 00:00:00', '2017-07-01 00:00:00');

/*秒杀成功明细表*/
CREATE TABLE success_killed(
seckill_id bigint NOT NULL COMMENT '秒杀商品id',
user_phone bigint NOT NULL COMMENT '秒杀成功的用户手机号',
state tinyint NOT NULL COMMENT '状态表示：-1代表无效 0代表成功 1代表已付款',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀创建时间',
PRIMARY KEY(seckill_id, user_phone),
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';


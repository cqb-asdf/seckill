/*秒杀执行存储过程,将秒杀逻辑直接在Mysql中执行*/
/* 将Mysql默认的语句分隔符；改为"$$" */
DELIMITER $$
/* 定义存储过程 */
/* IN:输入参数 OUT：输出参数*/
CREATE PROCEDURE seckill.execute_seckill
  (IN v_seckill_id BIGINT, IN v_phone BIGINT, IN v_kill_time TIMESTAMP, OUT r_result INT)
  BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION;
    INSERT IGNORE INTO success_killed(seckill_id, user_phone, create_time)
      VALUES (v_seckill_id,v_phone,v_kill_time);
    /* row_count():返回上一条修改类型的sql(delete,update,insert)的影响行数*/
    SELECT row_count() INTO insert_count;
    /* row_count():-->0：未修改数据; >0:表示修改的行数; <0：上一条修改类型sql语句执行出现错误/未执行修改sql */
    IF (insert_count = 0) THEN
      ROLLBACK ;
      SET r_result = -1;/* 出现重复秒杀*/
    ELSEIF (insert_count < 0) THEN
      ROLLBACK ;
      SET r_result = -2; /*系统出现异常*/
    ELSE
      UPDATE seckill
      SET number = number - 1
      WHERE seckill_id = v_seckill_id
          AND end_time > v_kill_time
          AND start_time < v_kill_time;
      SELECT row_count() INTO insert_count;
      IF (insert_count = 0) THEN
        ROLLBACK;
        SET r_result = 0; /*秒杀结束*/
      ELSEIF (insert_count < 0) THEN
        ROLLBACK ;
        SET r_result = -2;/*系统出错*/
      ELSE
        COMMIT ;/*秒杀成功，commit事务*/
        SET r_result = 1;
      END IF;
    END IF;
  END $$ /*存储过程定义结束*/

DELIMITER ;

SET @r_result = -3;
/*执行存储过程*/
CALL execute_seckill(1004,13813811111, now(),@r_result);
/*获取存储过程执行结果*/
SELECT @r_result;


/*存储过程*/
/*1.存储过程优化:事务行级锁持有的时间*/
/*2.不要过度依赖存储过程*/
/*3.简单的逻辑可以应用存储过程*/
/*4.QPS：一个秒杀单6000qps*/



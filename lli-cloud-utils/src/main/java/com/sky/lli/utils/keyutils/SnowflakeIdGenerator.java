package com.sky.lli.utils.keyutils;

import com.sky.lli.utils.exception.ExceptionEnum;
import com.sky.lli.utils.exception.UtilsException;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述:  全局唯一ID
 * snowflake 是 twitter 开源的分布式ID生成算法，其核心思想为，一个long型的ID：
 * 1. 41 bit 作为毫秒数 - 41位的长度可以使用69年
 * 2. 10 bit 作为机器编号 （5个bit是数据中心，5个bit的机器ID） - 10位的长度最多支持部署1024个节点
 * 3. 12 bit 作为毫秒内序列号 - 12位的计数顺序号支持每个节点每毫秒产生4096个ID序号
 *
 * @author lihao
 * @date 2021/4/19
 */
@Slf4j
public class SnowflakeIdGenerator {
    //================================================Algorithm's Parameter=============================================
    /**
     * 系统开始时间截 (UTC 2017-06-28 00:00:00)
     */
    private static final long START_TIME = 1498608000000L;
    /**
     * 机器id所占的位数
     */
    private static final long WORKER_ID_BITS = 5L;
    /**
     * 数据标识id所占的位数
     */
    private static final long DATA_CENTER_ID_BITS = 5L;
    /**
     * 支持的最大机器id(十进制)，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     * -1L 左移 5位 (worker id 所占位数) 即 5位二进制所能获得的最大十进制数 - 31
     * 公式: -1L ^ (-1L << WORKER_ID_BITS)
     */
    private static final long MAX_WORKER_ID = 31L;
    /**
     * 支持的最大数据标识id - 31
     * 公式: -1L ^ (-1L << DATA_CENTER_ID_BITS)
     */
    private static final long MAX_DATA_CENTER_ID = 31L;
    /**
     * 序列在id中占的位数
     */
    private static final long SEQUENCE_BITS = 12L;
    /**
     * 机器ID 左移位数 - 12 (即末 sequence 所占用的位数)
     */
    private static final long WORKER_ID_MOVE_BITS = SEQUENCE_BITS;
    /**
     * 数据标识id 左移位数 - 17(12+5)
     */
    private static final long DATA_CENTER_ID_MOVE_BITS = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * 时间截向 左移位数 - 22(5+5+12)
     */
    private static final long TIMESTAMP_MOVE_BITS = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    /**
     * 生成序列的掩码(12位所对应的最大整数值)，这里为4095 (0b111111111111=0xfff=4095)
     * 公式: -1L ^ (-1L << SEQUENCE_BITS)
     */
    private static final long SEQUENCE_MASK = 4095L;
    //=================================================Works's Parameter================================================
    /**
     * 工作机器ID(0~31)
     */
    private long workerId;
    /**
     * 数据中心ID(0~31)
     */
    private long dataCenterId;
    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;
    //===============================================Constructors=======================================================

    /**
     * 构造函数
     * workerId 工作ID默认为 0
     * dataCenterId 数据中心ID默认为0
     */
    public SnowflakeIdGenerator() {
        this.workerId = 0;
        this.dataCenterId = 0;
    }

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("DataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    // ==================================================Methods========================================================

    /**
     * 线程安全的获得下一个 ID 的方法
     */
    public synchronized long nextId() {
        long timestamp = currentTime();
        //如果当前时间小于上一次ID生成的时间戳: 说明系统时钟回退过 - 这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new UtilsException(ExceptionEnum.SYS_FAILURE_EXCEPTION,
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出 即 序列 > 4095
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = blockTillNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIME) << TIMESTAMP_MOVE_BITS)
                | (dataCenterId << DATA_CENTER_ID_MOVE_BITS)
                | (workerId << WORKER_ID_MOVE_BITS)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒 即 直到获得新的时间戳
     *
     * @param lastTimestamp 参数
     */
    protected long blockTillNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }

    /**
     * 获得以毫秒为单位的当前时间
     */
    protected long currentTime() {
        return System.currentTimeMillis();
    }

    //====================================================Test Case=====================================================

    /**
     * 方法说明: 测试
     */
    public static void main(String[] args) {
        SnowflakeIdGenerator idWorker = new SnowflakeIdGenerator();
        int num = 100;
        for (int i = 0; i < num; i++) {
            long id = idWorker.nextId();
            log.info("生成ID:{}", id);
        }
    }
}

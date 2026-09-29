package com.wuji.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于Snowflake算法的UUID操作工具类，53位long类型的ID, 兼容js支持的最大长度为16位的number类型
 * <pre>
 * 当前前端js支持的number类型的最大值为2的53次方，为9007199254740992。如果超过这个值，那么js会出现不精确的问题，故只生成53位的ID。
 * 使用 Snowflake 算法生成的 53 位 long 类型的 ID，结构如下:
 * 1位标识 - 39位时间戳 - 5位机器ID - 8位序列号 = 53位
 * 0 - 0000000000 0000000000 0000000000 000000000 - 00000 - 00000000
 * 1) 01 位标识，由于 long 在 Java 中是有符号的，最高位是符号位，正数是 0，负数是 1，ID 一般使用正数，所以最高位是 0
 * 2) 39 位时间截(毫秒级)，注意，时间截不是存储当前时间的时间截，而是存储时间截的差值(当前时间 - 开始时间)得到的值，
 *       开始时间截，一般是业务开始的时间，由我们程序来指定，如 SnowflakeIdWorker 中的 START_TIMESTAMP 属性。
 *       39 位的时间截，可以使用 17 年: (2^39)/(1000*60*60*24*365) = 17.43 年
 * 3) 5 位的数据机器位，可以部署在 32 个节点
 * 4) 8 位序列，毫秒内的计数，计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生 256 个 ID 序号
 *
 * 特性：
 * 1）整体上按照时间自增排序，并且整个分布式系统内不会产生 ID 碰撞(由机器 ID 作区分)；
 * 2）最多支持 32 台机器，每台机器每毫秒能够生成最多 256 个 ID，整个集群理论上每秒可以生成 32 * 1000 * 256 = 800万 个ID；
 * </pre>
 * <p>
 */
public class SnowFlakeIdUtils {
    public static void main(String[] args) {
        // 标识位
        String firstStr = "0";
        Long first = Long.parseLong(firstStr, 2);
        System.out.println(StrUtil.format("{}位标识位，设置为:{}", firstStr.length(), first));
        // 最大时间戳
        long timestamp = ~(-1L << TIME_STAMP_BITS);
        System.out.println(StrUtil.format("{}位时间戳，最大值:{}毫秒，约{}年", TIME_STAMP_BITS, timestamp, timestamp / (1000.0 * 60 * 60 * 24 * 365)));
        // 机器位
        long workId = ~(-1L << MACHINE_ID_BITS);
        System.out.println(StrUtil.format("{}位机器号，支持最大机器数量:{}", MACHINE_ID_BITS, workId + 1));
        // 序列
        long inx = ~(-1L << SEQUENCE_BITS);
        System.out.println(StrUtil.format("{}位序列，每毫秒生成:{}个ID，每秒生成:{}个ID", SEQUENCE_BITS, inx + 1, (inx + 1) * 1000));
        // 标识位 + 时间戳 + 机器位 + 序列
        long allBit = 1L + TIME_STAMP_BITS + MACHINE_ID_BITS +SEQUENCE_BITS;
        long id = ~(-1L << allBit);
        String binaryString = Long.toBinaryString(id);
        System.out.println(StrUtil.format("组成的二进制:{} 共{}位", binaryString, allBit));
        System.out.println(StrUtil.format("{} - 共{}位", Long.toString(id), Long.toString(id).length()));


        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 60; i++) {
            executorService.execute(() -> {
                for(int j=0;j<5; j++){
                    System.out.println(generateStr());
                }
            });
        }
        // 关闭线程池，不再接受新任务，但会继续执行已提交的任务
        executorService.shutdown();

        try {
            // 等待所有任务完成，或者超时后继续执行后续代码
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // 强制关闭线程池
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow(); // 当前线程被中断时，强制关闭线程池
            Thread.currentThread().interrupt(); // 保留中断状态
        }
        System.out.println("所有任务已完成");


    }

    /**
     * 开始时间戳 (2021-01-01)
     */
    private final static long START_STAMP = 1609430400000L;
    /**
     * 时间戳所占的位数
     */
    private static final long TIME_STAMP_BITS = 39L;
    /**
     * 机器 ID 所占的位数
     */
    private static final long MACHINE_ID_BITS = 2L;

    /**
     * 序列在 ID 中占的位数
     */
    private static final long SEQUENCE_BITS = 4L;

    /**
     * 机器 ID 向左移 SEQUENCE_BITS 位
     */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 时间截向左移 N 位(MACHINE_ID_BITS+SEQUENCE_BITS)
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    /**
     * 生成序列的掩码，这里为 256(0B11111111=0xFF=256)
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final char[] charSet62 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K','L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final char[] charSet36 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * 默认生成的静态实例
     */
    private static SnowFlakeIdUtils INSTANCE = new SnowFlakeIdUtils();

    /**
     * 工作机器 ID(0~31)
     */
    private long machineId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成 ID 的时间截
     */
    private long lastTimestamp = -1L;


    /**
     * 无参构造，使用工作机器的序号，范围是 [0, 31]
     */
    private SnowFlakeIdUtils() {
        this.machineId = getWorkId();
    }

    /**
     * 生成一个UUID
     *
     * @return UUID
     */
    public static long generateId() {
        return INSTANCE.nextId();
    }

    /**
     * 生成一个字符串
     *
     * @return UUID
     */
    public static String generateStr() {
        long id = INSTANCE.nextId();
        StringBuilder stringBuilder = new StringBuilder();
        while (id > 0){
            int remain = (int) (id % charSet36.length);
            stringBuilder.append(charSet36[remain]);
            id = id / charSet36.length;
        }
        return stringBuilder.reverse().toString();
    }

    /**
     * 获得下一个 ID(该方法是线程安全的)
     *
     * @return long 类型的 ID
     */
    private synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String
                    .format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;

            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成 ID 的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成 53 位的 ID
        return ((timestamp - START_STAMP) << TIMESTAMP_LEFT_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成 ID 的时间截
     * @return 当前时间戳(毫秒)
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }

        return timestamp;
    }

    /**
     * 返回当前时间，以毫秒为单位
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 通过IP地址转化获取机器ID
     * 如果在spring容器内会获取IP地址+端口，防止同一台机器上启动多台实例
     *
     * @return
     */
    private Long getWorkId() {
        String port = "";
        try {
            Environment environment = SpringUtil.getBean(Environment.class);
            port = ":" + environment.getProperty("server.port", "8080");
        } catch (Exception e) {

        }
        Long num = 1L << MACHINE_ID_BITS;
        try {

            String hostAddress = Inet4Address.getLocalHost().getHostAddress() + port;
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums += b;
            }
            // 对32取模，返回0-31
            return (long) (sums % num);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, num -1);
        }
    }
}

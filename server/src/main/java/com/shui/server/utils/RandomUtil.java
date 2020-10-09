package com.shui.server.utils;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 *  随机数生成util
 *  for循环生成，量大的时候还是慢
 *  没法排序
 */
public class RandomUtil {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmssSS");
    /**
     *  ThreadLocalRandom 高并发下保证线程安全，相比Random
     */
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     *  生成订单编号-方式一
     */
    public static String generateOrderCode(){
        // 时间戳 + 4位随机数流水号
        synchronized (SDF) {
            return SDF.format(DateTime.now().toDate()) + generateNumber(4);
        }
    }

    /**
     *  num位随机数流水号
     */
    public static String generateNumber(final int num){
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= num; i++){
            // 0～9
            sb.append(RANDOM.nextInt(9));
        }
        return sb.toString();
    }

}
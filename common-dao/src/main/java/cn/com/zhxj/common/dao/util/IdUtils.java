package cn.com.zhxj.common.dao.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ID生成器
 */
public class IdUtils {

    private static final SecureRandom numberGenerator = new SecureRandom();
    private static final AtomicLong   numberIndex     = new AtomicLong(-1);
    private static final AtomicLong   lastSec         = new AtomicLong(System.currentTimeMillis() / 100L);

    //去掉一些长得比较相似的，加入 'w','x','y','z'
    private static final char[] ENCODE_TABLE = {
            /*'0',*/ '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',   /*'l',*/ 'm',
            'n',  /*'o',*/ 'p', /*'q',*/ 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    /**
     * 与UUID对比
     * 1.比uuid 108 bit, 这个 90 bit
     * 2.uuid使用16进制编码，32个字符，这个使用32进制编码，18个字符
     * 3.uuid是无序的，这个是相对有序的,秒级有序,并且 同一秒内，256个ID都有序的
     * 4.性能：单线程每秒50w id
     * 5.前10bit用于存储表名hash,可读性更强
     * 6.去掉一些 显示 容易混淆 的 字符 比如 0 o l 1
     * 7.第10个字符代表 0.1秒内 的 并发程度 32级
     *
     * @param tableName 数据库表名 表名只用来计算前两位字符
     * @return
     */
    public static String newId(String tableName) {

        tableName = tableName.toUpperCase(Locale.ENGLISH);
        // 10 bit 表名 + 35 bit秒数 + 5bit index + 40 bit random
        //     2         7              1
        // 10 bit 表名: 1024
        // 35 bit 秒数：34359738368 约 108 年
        // 5  bit index：32
        // 40 bit randome : 1099511627776 约 1万亿

        int hashCode = tableName.hashCode();
        long sec = System.currentTimeMillis() / 100L; // 1/10 秒

        if (lastSec.getAndSet(sec) != sec) {
            numberIndex.set(-1);
        }
        long index = numberIndex.incrementAndGet() % 32;
        BitArray bitArray = new BitArray();
        bitArray.addTail(hashCode, 10);
        bitArray.addTail(sec, 35);
        bitArray.addTail(index, 5);
        byte[] randomBytes = new byte[5];
        numberGenerator.nextBytes(randomBytes);

        for (byte randomByte : randomBytes) {
            bitArray.add(randomByte, 0, 8);
        }
        return bitArray.parseAsBase32();
    }

    public static void main(String[] args) throws InterruptedException {
//        while (true){
//            long current = System.currentTimeMillis();
//            for (int i = 0; i < 1000000; i++) {
//                String id = newId("TD_ORDERSEND");
//            }
//
//            System.out.println("100w ms:"+ (System.currentTimeMillis()-current));
//            Thread.sleep(2000);
//        }


//        long total=0L;
//
//        while (true) {
//            HashMap<String, String> map = new HashMap<>();
//            long current = System.currentTimeMillis();
//            for (int i = 0; i < 1000000; i++) {
//                total++;
//                String id = newId("TD_ORDERSEND");
//                if(map.containsKey(id)){
//                    System.out.println("------- 第"+total+"个 id 冲突："+id);
//                    throw new RuntimeException("------- 第"+total+"个 id 冲突："+id);
//                }
//                map.put(id,"");
//            }
//
//            System.gc();
//            System.out.println("100w ms:" + (System.currentTimeMillis() - current)+" 共:"+total);
//            Thread.sleep(1000);
//        }

//        String lastId=newId("TD_ORDERSEND");
//
//        while (true) {
//            for (int i = 0; i < 200; i++) {
//                String id = newId("TD_ORDERSEND");
//                System.out.println(id);
//                if(id.compareTo(lastId)<0){
//                    System.out.println("------- 个 id 无序："+id +"<" +lastId);
//                    throw new RuntimeException(("------- 个 id 无序："+id +"<" +lastId));
//                }
//                lastId=id;
//                Thread.sleep(5);
//            }
//        }


//        while (true) {
//            String id = newId("TD_ORDERSEND");
//            System.out.println(id);
//            Thread.sleep(5);
//        }

    }


    private static void printBit(int a) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((a >>> 24) & 255);
        bytes[1] = (byte) ((a >>> 16) & 255);
        bytes[2] = (byte) ((a >>> 8) & 255);
        bytes[3] = (byte) ((a) & 255);
        printBit(bytes);
    }

    private static void printBit(long a) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) ((a >>> 56) & 255);
        bytes[1] = (byte) ((a >>> 48) & 255);
        bytes[2] = (byte) ((a >>> 40) & 255);
        bytes[3] = (byte) ((a >>> 32) & 255);
        bytes[4] = (byte) ((a >>> 24) & 255);
        bytes[5] = (byte) ((a >>> 16) & 255);
        bytes[6] = (byte) ((a >>> 8) & 255);
        bytes[7] = (byte) ((a) & 255);
        printBit(bytes);
    }

    private static void printBit(byte... bytes) {

        for (byte aByte : bytes) {
            if ((aByte & 0b10000000) != 0) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }

            if ((aByte & 0b01000000) != 0) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
            System.out.print("-");
            if ((aByte & 0b00100000) != 0) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
            if ((aByte & 0b00010000) != 0) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
            System.out.print("-");
            if ((aByte & 0b00001000) != 0) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
            if ((aByte & 0b00000100) != 0) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
            System.out.print("-");
            if ((aByte & 0b00000010) != 0) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
            if ((aByte & 0b00000001) != 0) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }

            System.out.print("  ");
        }
        System.out.println("  ");

    }

    private static class BitArray {
        private List<Boolean> vals = new ArrayList<>();

        String parseAsBase32() {
            int charCount = vals.size() / 5;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < charCount; i++) {
                int fromInclude = i * 5;
                int charIndex = read5Bit(fromInclude);
                sb.append(ENCODE_TABLE[charIndex]);
            }
            return sb.toString();
        }

        int read5Bit(int fromInclude) {
            int val = 0;
            for (int i = 0; i < 5; i++) {
                if (vals.get(i + fromInclude)) {
                    val = val | (1 << (4 - i));
                }
            }
            return val;
        }

        /**
         * 从高位开始取值,包含符号位
         *
         * @param v
         * @param from
         * @param length
         */
        void add(int v, int from, int length) {
            for (int i = 0; i < length; i++) {
                if (0 != (v & (1 << (31 - from)))) {
                    vals.add(true);
                } else {
                    vals.add(false);
                }
                from++;
            }
        }

        void add(long v, int from, int length) {
            for (int i = 0; i < length; i++) {
                if (0 != (v & (1L << (63 - from)))) {
                    vals.add(true);
                } else {
                    vals.add(false);
                }
                from++;
            }
        }

        void add(byte v, int from, int length) {
            for (int i = 0; i < length; i++) {
                if (0 != (v & (byte) (1 << (7 - from)))) {
                    vals.add(true);
                } else {
                    vals.add(false);
                }
                from++;
            }
        }

        void addTail(int v, int length) {
            for (int i = length; i > 0; i--) {
                if (0 != (v & (1 << (i - 1)))) {
                    vals.add(true);
                } else {
                    vals.add(false);
                }
            }
        }

        void addTail(long v, int length) {
            for (int i = length; i > 0; i--) {
                if (0 != (v & (1L << (i - 1)))) {
                    vals.add(true);
                } else {
                    vals.add(false);
                }
            }
        }


    }

}

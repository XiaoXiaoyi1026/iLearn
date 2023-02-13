package com.ilearn.base.utils;


import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * 随机字符串工具
 */
public class RandomStringUtil {
    /**
     * 获取指定长度随机字符串
     *
     * @param length 长度
     * @return 指定长度随机字符串
     */
    public static @NotNull String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(3);
            long result;
            switch (number) {
                case 0:
                    result = Math.round(Math.random() * 25 + 65);
                    sb.append((char) result);
                    break;
                case 1:
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append((char) result);
                    break;
                case 2:
                    sb.append(new Random().nextInt(10));
                    break;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(RandomStringUtil.getRandomString(5));
        String str2 = RandomStringUtils.random(12, "123456789");
        System.out.println(str2);
    }


}

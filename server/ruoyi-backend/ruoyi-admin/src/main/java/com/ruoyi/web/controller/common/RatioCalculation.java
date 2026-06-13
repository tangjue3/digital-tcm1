package com.ruoyi.web.controller.common;

public class RatioCalculation {
    public static void main(String[] args) {
        double numerator = 3;
        double denominator = 25;

        // 计算比例
        double ratio = numerator / denominator;

        // 将比例转换为百分比
        double percentage = ratio * 100;

        // 输出结果
        System.out.println("3/25 的占比是: " + ratio + "%");
    }
}

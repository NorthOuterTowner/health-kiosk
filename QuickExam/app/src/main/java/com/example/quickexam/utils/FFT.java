package com.example.quickexam.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FFT {

    int n, m;
    double[] cos;
    double[] sin;

    public FFT(int n) {
        if (n <= 0 || (n & (n - 1)) != 0) {
            throw new IllegalArgumentException("FFT length must be a positive power of 2");
        }
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));
        cos = new double[n / 2];
        sin = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }
    }

    public void fft(double[] x, double[] y) {
        if (x.length != n || y.length != n) {
            throw new IllegalArgumentException("Input array length must be equal to FFT length");
        }
        int i, j, k, n1, n2, a;
        double c, s, t1, t2;
        j = 0;
        n2 = n / 2;
        for (i = 1; i < n - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;
            if (i < j) {
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }
        n1 = 0;
        n2 = 1;
        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;
            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);
                for (k = j; k < n; k = k + n2) {
                    t1 = c * x[k + n1] - s * y[k + n1];
                    t2 = s * x[k + n1] + c * y[k + n1];
                    x[k + n1] = x[k] - t1;
                    y[k + n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
    }
    public static double calculateRMS(double[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("数组为空或为 null");
        }
        // 计算数组的平方和
        double sumOfSquares = Arrays.stream(array).map(d -> d * d).sum(); // 修改mapToDouble为map
        // 计算均方根
        double rms = Math.sqrt(sumOfSquares / array.length);
        return rms;
    }
    public static boolean calculateListValue(List<Double> list){
        // 将 list 转为 stream 流
        double sum = list.stream().mapToDouble(Double::doubleValue).sum(); // 修改map为mapToDouble
        // 计算平均值
        double average = sum / list.size();
        // 计算最大值
        double max = list.stream().mapToDouble(Double::doubleValue).max().getAsDouble(); // 修改int为double
        // 计算最小值
        double min = list.stream().mapToDouble(Double::doubleValue).min().getAsDouble(); // 修改int为double
        // 计算最大值索引
        int maxIndex = list.indexOf(max);
        // 计算最小值索引
        int minIndex = list.indexOf(min);

        System.out.println("average: " + average+",max: " + max+",min: " + min);
        System.out.println("maxIndex: " + maxIndex);
        System.out.println("minIndex: " + minIndex);
        if((minIndex == (list.size()-1))&&(min<(average*0.5))){
            if(Math.abs(max - average)<(average*0.1)){
                return true;
            }
        }
        return false;
    }
    public static Double RemoveDCComponent (List<Double> list) {

        // Calculate the average to remove DC component
        double average = list.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // Remove DC component and keep AC component
//        List<Double> acData = list.stream()
//                .map(value -> value - average)
//                .collect(Collectors.toList());

        // acData now contains only the AC component
        return ((list.get(list.size() - 1)-average)+(list.get(list.size() - 2)-average))/2;
    }
}
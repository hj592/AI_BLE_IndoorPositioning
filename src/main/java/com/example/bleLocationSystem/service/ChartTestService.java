package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.ChartUI;
import com.example.bleLocationSystem.model.KalmanFilter;
import com.example.bleLocationSystem.model.LibtorchPredictor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ChartTestService {

    private final ChartUI chartUI;
    KalmanFilter kFilterForAp1;
    private double tempAlpha;
    private double lossNum;
    LibtorchPredictor predictor;
    ArrayList<Double> kalman_data;

    double ori_mean;
    double kalman_mean;
    double ori_last_meter;
    double ori_mean_meter;
    double kalman_mean_meter;
    double ai_meter;
    double weighted_ai_meter;

    public ChartTestService() {
        this.chartUI = ChartUI.getInstance(); // 싱글턴 인스턴스
        this.chartUI.setVisible(true);
        kFilterForAp1 = new KalmanFilter();
        kalman_data = new ArrayList<Double>();
        predictor = new LibtorchPredictor();
    }

    public void processData(ArrayList<Double> rssiData) {

        ori_mean = calcMean(rssiData);

        ori_mean_meter = calcDistance(ori_mean);

        ori_last_meter = calcDistance(rssiData.get(rssiData.size()-1));

        for(int i=0; i<rssiData.size(); i++) {
            double filterdRssi1 = kFilterForAp1.kalmanFiltering(rssiData.get(i));

            kalman_data.add(filterdRssi1);
        }

        kalman_mean = calcMean(kalman_data);

        kalman_mean_meter = calcDistance(kalman_mean);

        //딥러닝으로 10개로 추론 후 meter 구하기
        double[] a = rssiData.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        ai_meter = predictor.predictDistance(a);

        weighted_ai_meter = calcWeighted(ori_mean_meter, ai_meter);
        // 데이터 처리하고 GUI 즉시 업데이트
        //chartUI.addNewDataPoint(ori_mean_meter, kalman_mean_meter, ai_meter);


        chartUI.addNewDataPoint(ori_last_meter, kalman_mean_meter , ai_meter, weighted_ai_meter, 7);

        kalman_data.clear();
    }

    public double calcWeighted(double ori_avg_meter, double ai_meter) {
        double w = 0.7;

        return (ori_avg_meter*(1.0-w)) + (ai_meter*w);
    }

    public double calcMean(ArrayList<Double> rssiData) {
        double mean;

        double sum = 0;
        for (double num : rssiData) {
            sum += num;
        }
        mean = sum / rssiData.size();

        return mean;
    }

    public double calcDistance(double tempRssi) {

        tempAlpha = -56;
        lossNum = 3;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }

}
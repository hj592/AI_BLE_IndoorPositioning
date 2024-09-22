package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.ChartUI;
import org.springframework.stereotype.Service;

@Service
public class ChartTestService {

    private final ChartUI chartUI;

    public ChartTestService() {
        this.chartUI = ChartUI.getInstance(); // 싱글턴 인스턴스
        this.chartUI.setVisible(true);
    }

    public void processData(double value1, double value2, double value3) {

        // 데이터 처리하고 GUI 즉시 업데이트
        chartUI.addNewDataPoint(value1, value2, value3);
    }
}
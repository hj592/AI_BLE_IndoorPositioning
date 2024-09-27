package com.example.bleLocationSystem;

import com.example.bleLocationSystem.model.UserLocation;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Font;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

@Slf4j
public class ChartUI extends JFrame {

    private static ChartUI instance;
    private XYSeries original;
    private XYSeries kalman;
//    private XYSeries ours;
    private XYSeries cnn_lstm_ours;
    private XYSeries weighted_cnn_lstm_ours;
    private XYSeries ground_truth;
    private int time = 0;

    public ChartUI(String title) { //double w, double h,
        setTitle(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width, d.height-50);
        setLocationRelativeTo(null); // 중앙에 위치
        original = new XYSeries("original");
        kalman = new XYSeries("kalman");
        cnn_lstm_ours= new XYSeries("cnn-lstm(ours)");
        weighted_cnn_lstm_ours= new XYSeries("weighted cnn-lstm(ours)");
        ground_truth= new XYSeries("ground truth");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(original);
        dataset.addSeries(kalman);
        dataset.addSeries(cnn_lstm_ours);
        //dataset.addSeries(weighted_cnn_lstm_ours);
        dataset.addSeries(ground_truth);

        // 차트 생성
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Real-Time Data Chart",
                "count",
                "meters",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        // XYPlot 가져오기
        XYPlot plot = chart.getXYPlot();

        // 글씨 크기
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 20)); // 제목 크기
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // X축 레이블 크기
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 20)); // Y축 레이블 크기
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 14)); // X축 눈금 레이블 크기
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 14)); // Y축 눈금 레이블 크기
        //범례
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 20)); // 범례 글자 크기
        // 선 굵기
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // raw
        renderer.setSeriesStroke(1, new BasicStroke(2.0f)); // 칼만
        renderer.setSeriesStroke(2, new BasicStroke(4.0f)); // 딥러닝
        //renderer.setSeriesStroke(3, new BasicStroke(4.0f)); // weighted
        renderer.setSeriesStroke(3, new BasicStroke(6.0f)); // gt
        //renderer.setSeriesStroke(4, new BasicStroke(6.0f)); // gt

        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(d.width, d.height-50));
        setContentPane(chartPanel);
//        chartPanel.setVisible(true);
    }
    public static ChartUI getInstance() {
        if (instance == null) {
            instance = new ChartUI("tttest");
            instance.setVisible(true); // 첫 인스턴스가 생성될 때 GUI를 보여줌
        }
        return instance;
    }
    // 실시간 데이터 추가
    public void addNewDataPoint(double value1, double value2, double value3, double value4, double gt) {
        original.add(time, value1);
        kalman.add(time, value2);
        cnn_lstm_ours.add(time, value3);
        weighted_cnn_lstm_ours.add(time,value4);
        ground_truth.add(time,gt);
        time++;
        repaint();
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            ChartUI example = new ChartUI("Real-Time Line Chart Example");
//            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//            example.setSize(d.width, d.height-50);
//            example.setLocationRelativeTo(null);
//            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//            example.setVisible(true);
//
//            // 타이머로 데이터를 주기적으로 추가
//            Random rand = new Random();
//            new Timer(500, e -> {
//                double value1 = rand.nextDouble() * 100;
//                double value2 = rand.nextDouble() * 100;
//                double value3 = rand.nextDouble() * 100;
//
//                // 값이 하나씩 들어올 때마다 차트 업데이트
//                example.addNewDataPoint(value1, value2, value3);
//            }).start();
//        });
//    }
}

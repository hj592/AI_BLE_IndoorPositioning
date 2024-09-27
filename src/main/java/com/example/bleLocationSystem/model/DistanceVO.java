package com.example.bleLocationSystem.model;

import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
@Data
public class DistanceVO {
    int size;

    private String deviceName;

    private ArrayList<Double> distance_arr;

    public DistanceVO(String deviceName) {
        this.deviceName = deviceName;
        size = 8;
        distance_arr = new ArrayList<Double>(size);
//        distance_arr = Arrays.asList(new Double[size]);
        for(int i=0; i < size; i++) {
            distance_arr.add(-1.0);
        }
    }
}

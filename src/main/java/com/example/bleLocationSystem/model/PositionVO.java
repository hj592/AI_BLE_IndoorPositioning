package com.example.bleLocationSystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.ArrayList;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@JsonFormat
public class PositionVO {
    private String deviceName;
    private String mac;
    private ArrayList<Double> rssi;
    private int co;
}

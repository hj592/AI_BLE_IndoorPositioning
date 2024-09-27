package com.example.bleLocationSystem.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MAC {

    Map<String, Integer> mac_addr_idx_dict;

    String[] mac_addrs;

    public MAC() {
        mac_addr_idx_dict = new HashMap<String, Integer>();

        mac_addrs = new String[] {"DC:0D:30:00:1A:7C",
                "DC:0D:30:00:1A:5E",
                "DC:0D:30:00:26:BD",
                "DC:0D:30:18:F2:B1",
                "DC:0D:30:18:F2:97",
                "DC:0D:30:18:F2:A9",
                "DC:0D:30:18:F2:A8",
                "DC:0D:30:18:F2:C0"};

        for (int i=0; i<mac_addrs.length; i++) {
            mac_addr_idx_dict.put(mac_addrs[i], i);
        }
    }


}

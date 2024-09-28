package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class AITestService {
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99
    private double tempAlpha;
    private double lossNum;
    Map<String, DistanceVO> user_map = new HashMap<String, DistanceVO>();

    MAC mac;
    DistanceVO distanceVO;
    ArrayList<Double> prev_list;
    ArrayList<Double> ori_prev_list;
    ArrayList<Double> kalman_prev_list;

    double aiDistance;
    double ori_mean_distance;
    double kalman_mean_distance;

    String user_device_name;
    String ori_user_device_name;
    String kalman_user_device_name;
    int proxiCheckNum;
    int triangleNum;
    int ori_triangleNum;
    int kalman_triangleNum;
    int beacon_amount;

    Ap proximityAp;

    UserLocation oriUl;
    UserLocation kalmanUl;
    UserLocation aiOriUl;
    UserLocation aiProxiUl;
    LocMAFilter locMAFilter;
    RemoveOutlier rm;
    LibtorchPredictor predictor;

    // -------------------------
    ExelPOIHelper poiHelper;
    ArrayList<KalmanFilter> kalmanList;
    KalmanFilter kFilterForAp;

    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;
    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;
    KalmanFilter kFilterForAp7;
    KalmanFilter kFilterForAp8;
    // -------------------------

    public AITestService() {
        mac = new MAC();
        locMAFilter = new LocMAFilter();
        rm = new RemoveOutlier();
        predictor = new LibtorchPredictor();

        // ---------------------------------
        poiHelper = new ExelPOIHelper();
        kalmanList = new ArrayList<KalmanFilter>();

        for (int i=0; i < 8; i++) {
            kFilterForAp = new KalmanFilter();
            kalmanList.add(kFilterForAp);
        }

//        kFilterForAp1 = new KalmanFilter();
//        kFilterForAp2 = new KalmanFilter();
//        kFilterForAp3 = new KalmanFilter();
//        kFilterForAp4 = new KalmanFilter();
//        kFilterForAp5 = new KalmanFilter();
//        kFilterForAp6 = new KalmanFilter();
//        kFilterForAp7 = new KalmanFilter();
//        kFilterForAp8 = new KalmanFilter();
        // ---------------------------------
    }

    public UserLocation trilateration(PositionVO positionVO) {
        user_device_name = positionVO.getDeviceName();

        ori_user_device_name = user_device_name + "original";
        kalman_user_device_name = user_device_name + "kalman";


        if (!user_map.containsKey(user_device_name)) {
            distanceVO = new DistanceVO(user_device_name);
            user_map.put(user_device_name, distanceVO);
        }

        if (!user_map.containsKey(ori_user_device_name)) {
            distanceVO = new DistanceVO(ori_user_device_name);
            user_map.put(ori_user_device_name, distanceVO);
        }
        if (!user_map.containsKey(kalman_user_device_name)) {
            distanceVO = new DistanceVO(kalman_user_device_name);
            user_map.put(kalman_user_device_name, distanceVO);
        }

//-------------------------------------------------------------- oriUl --------------------------------------------------------------
        int original_mac_idx = mac.getMac_addr_idx_dict().get(positionVO.getMac());

        // 합계 계산
        double ori_sum = 0.0;
        ArrayList<Double> oriRssiList = positionVO.getRssi();
        for (double rssi : oriRssiList) {
            ori_sum += rssi;
        }
        double ori_mean_rssi = ori_sum / oriRssiList.size();

        double ori_mean_distance = calcDistance(ori_mean_rssi);

        ori_prev_list = user_map.get(ori_user_device_name).getDistance_arr();

        ori_prev_list.set(original_mac_idx, ori_mean_distance);
        user_map.get(ori_user_device_name).setDistance_arr(ori_prev_list);

        // 삼변측량 측위
        ori_triangleNum = selectTriangle(user_map.get(ori_user_device_name));
//        System.out.println("Selected Triangle Num = " + ori_triangleNum);
//            System.out.println("");
        // Original 측위 결과
        oriUl = makeTrilUl(user_map.get(ori_user_device_name), ori_triangleNum);

//-------------------------------------------------------------- kalmanUl --------------------------------------------------------------
        int kalman_mac_idx = mac.getMac_addr_idx_dict().get(positionVO.getMac());
        ArrayList<Double> oriKalmanRssiList = positionVO.getRssi();
        ArrayList<Double> kalmanRssiList = new ArrayList<Double>();

        for (double rssi : oriKalmanRssiList) {
            double filterdRssi = kalmanList.get(kalman_mac_idx).kalmanFiltering(rssi);
            kalmanRssiList.add(filterdRssi);
        }

        // 합계 계산
        double kalman_sum = 0.0;

        for (double rssi : kalmanRssiList) {
            kalman_sum += rssi;
        }
        double kalman_mean_rssi = kalman_sum / kalmanRssiList.size();

        double kalman_mean_distance = calcDistance(kalman_mean_rssi);

        kalman_prev_list = user_map.get(kalman_user_device_name).getDistance_arr();

        kalman_prev_list.set(kalman_mac_idx, kalman_mean_distance);
        user_map.get(kalman_user_device_name).setDistance_arr(kalman_prev_list);

        // 삼변측량 측위
        kalman_triangleNum = selectTriangle(user_map.get(kalman_user_device_name));
//        System.out.println("Selected Triangle Num = " + ori_triangleNum);
//            System.out.println("");
        // 칼만 측위 결과
        kalmanUl = makeTrilUl(user_map.get(kalman_user_device_name), kalman_triangleNum);

//-------------------------------------------------------------- aiOriUl &&  aiProxiUl --------------------------------------------------------------
        proxiCheckNum = 0;

        double[] a = positionVO.getRssi().stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        aiDistance = predictor.predictDistance(a);

        int mac_idx = mac.getMac_addr_idx_dict().get(positionVO.getMac());
//        System.out.println(mac_idx);

        prev_list = user_map.get(user_device_name).getDistance_arr();
//        System.out.println("prev ="+prev_list);
        prev_list.set(mac_idx, aiDistance);
        user_map.get(user_device_name).setDistance_arr(prev_list);

//        System.out.println("");
//        System.out.println(user_device_name+" | list = "+user_map.get(user_device_name).getDistance_arr());
//        System.out.println("");


        // Proximity 검사
        proxiCheckNum = checkProximity(user_map.get(user_device_name));
//        System.out.println("");
        System.out.println("Proxi Check Num = "+proxiCheckNum);

//        beacon_amount = user_map.get(user_device_name).getSize();

        if (proxiCheckNum != 0) {
            // Proximity 측위
            proximityAp = makeProximityAP(user_map.get(user_device_name), proxiCheckNum);
            Trilateration proximityTr = new Trilateration(user_map.get(user_device_name).getDeviceName(), proximityAp, proxiCheckNum);
            // AI Proximity 측위 결과
            aiProxiUl = proximityTr.calcProximityLocation();

            // AI 측위 결과
            triangleNum = selectTriangle(user_map.get(user_device_name));
            aiOriUl = makeTrilUl(user_map.get(user_device_name), triangleNum);

        } else {
            // 삼변측량 측위
            triangleNum = selectTriangle(user_map.get(user_device_name));
//            System.out.println("Selected Triangle Num = " + triangleNum);
//            System.out.println("");
            // AI Proximity 측위 결과
            aiProxiUl = makeTrilUl(user_map.get(user_device_name), triangleNum);
            // AI 측위 결과
            aiOriUl = makeTrilUl(user_map.get(user_device_name), triangleNum);
        }

//        좌표 이상치 제거
//        if (rm.rmXYOutlier(aiOriUl, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
//            aiUl = new UserLocation(999, 999, "ddd");
//            return null;
//        }

        createCsvEx2(oriUl, kalmanUl, aiOriUl, aiProxiUl);

        return aiProxiUl;
    }

    private int checkProximity(DistanceVO distanceVO) {
        double errorValue = 999.9;
        double valueTemp;
        int keyTemp;

        double valueTemp2;
        int keyTemp2;

        DistanceVO tmpDistVo = distanceVO;
        int distVo_size = tmpDistVo.getSize();

        ArrayList<Double> prev_list = tmpDistVo.getDistance_arr();

        Map<Integer, Double> map = new HashMap<Integer, Double>();

        for(int i=0; i<distVo_size; i++) {
            if (prev_list.get(i) >= 0) {
                map.put(i+1, prev_list.get(i));
            } else {
                map.put(i+1, errorValue);
            }
        }
//        System.out.println(map);

        valueTemp = map.get(1);
        keyTemp=1;

        for(int i = 2; i<distVo_size+1; i++) {
            if(valueTemp > map.get(i) && 0<=map.get(i)) {    //0보단 크고 지금까지 나온 값들보다 작은 값인 경우
                keyTemp = i;
                valueTemp = map.get(i);
            }
        }
//        log.info("first min = {}", keyTemp);
//        map.put(keyTemp, errorValue);

        if(keyTemp==1) {
            keyTemp2 = 2;
            valueTemp2 = map.get(2);

            for(int i = 3; i<distVo_size+1; i++) {

                if(valueTemp > map.get(i) && 0 <= map.get(i)) {
                    keyTemp2 = i;
                    valueTemp2 = map.get(i);
                }
            }

        } else {
            keyTemp2=1;
            valueTemp2 = map.get(1);

            for(int i = 2; i<distVo_size+1; i++) {
                if (keyTemp != i) {
                    if (valueTemp2 > map.get(i) && 0 <= map.get(i)) {
                        keyTemp2 = i;
                        valueTemp2 = map.get(i);
                    }
                }
            }
        }

//        log.info("1 = {}", valueTemp);
//        log.info("2 = {}", valueTemp2);
        if(valueTemp >= 0 && valueTemp <= 3.0 && valueTemp2 > 3.0) { // 젤 작은 값이 0m보단 크고 3m보단 작아야함, 그리고 두번째로 큰 값이 3m보다 커야함
            return keyTemp;
        }

        return 0;
    }

    private Ap makeProximityAP(DistanceVO distanceVO, int tmpProxiNum) {
        ArrayList<Double> tmp_arr = distanceVO.getDistance_arr();
        Ap tmp_Ap = null;
        switch (tmpProxiNum) {
            case 1:
                triangleNum = 1;
                tmp_Ap = new Ap(0, 0, tmp_arr.get(proxiCheckNum - 1));
                break;
            case 2:
                triangleNum = 1;
                tmp_Ap = new Ap(w / 2, h, tmp_arr.get(proxiCheckNum - 1));
                break;
            case 3:
                triangleNum = 2;
                tmp_Ap = new Ap(w, 0, tmp_arr.get(proxiCheckNum - 1));
                break;
            case 4:
                triangleNum = 3;
                tmp_Ap = new Ap(w + (w / 2), h, tmp_arr.get(proxiCheckNum - 1));
                break;
            case 5:
                triangleNum = 4;
                tmp_Ap = new Ap(w * 2, 0, tmp_arr.get(proxiCheckNum - 1));
                break;
            case 6:
                triangleNum = 5;
                tmp_Ap = new Ap((w * 2) + (w / 2), h, tmp_arr.get(proxiCheckNum - 1));
                break;
            case 7:
                triangleNum = 6;
                tmp_Ap = new Ap(w * 3, 0, tmp_arr.get(proxiCheckNum - 1));
                break;
            case 8:
                triangleNum = 6;
                tmp_Ap = new Ap((w * 3) + (w / 2), h, tmp_arr.get(proxiCheckNum - 1));
                break;
        }
        return tmp_Ap;
    }

    public int selectTriangle(DistanceVO distanceVO) {
        int num;
        double valueTemp;
        int keyTemp;
        double errorValue = 999.9;

        double rssiTmp1;
        double rssiTmp2;

        int tmpTriNum;

        DistanceVO tmpDistVo = distanceVO;
        int distVo_size = tmpDistVo.getSize();

        ArrayList<Double> prev_list = tmpDistVo.getDistance_arr();

        Map<Integer, Double> map = new HashMap<Integer, Double>();
        for(int i=0; i<distVo_size; i++) {
            if (prev_list.get(i) >= 0) {
                map.put(i+1, prev_list.get(i));
            } else {
                map.put(i+1, errorValue);
            }
        }

        List<Integer> keyList = new ArrayList<Integer>();

        //
        for(int j = 0; j<3; j++) {
            //ap 신호중 가장 작은 미터 값 찾기
            valueTemp = map.get(1);
            keyTemp=1;

            for(int i = 2; i<9; i++) {

                if(valueTemp > map.get(i) && 0<=map.get(i)) {
                    keyTemp = i;
                    valueTemp = map.get(i);
                }
            }
            if(!map.get(keyTemp).equals(errorValue)) {
                keyList.add(keyTemp);
                map.put(keyTemp, errorValue);
            }
        }

        if(keyList.size() == 3) {
            Collections.sort(keyList);

            log.info("deviceName = {} key list = {}", tmpDistVo.getDeviceName(), keyList.toString());


            int n1 = keyList.get(1) - keyList.get(0);
            int n2 = keyList.get(2) - keyList.get(1);

            if(n1 == 1 && n2 ==1) {
                return keyList.get(0);
            }
            else if(n1 == 1 && n2 == 2) {
                if ( !map.get(keyList.get(2)-1).equals(errorValue) ) {
                    rssiTmp1 = map.get(keyList.get(0));
                    rssiTmp2 = map.get(keyList.get(2));

                    if (rssiTmp1 > rssiTmp2) {
                        return keyList.get(0);
                    } else {
                        return keyList.get(1);
                    }
                }
            }
            else if(n1 == 2  && n2 == 1) {
                if ( !map.get(keyList.get(1)-1).equals(errorValue) ) {
                    rssiTmp1 = map.get(keyList.get(0));
                    rssiTmp2 = map.get(keyList.get(2));

                    if (rssiTmp1 > rssiTmp2) {
                        return keyList.get(0);
                    } else {
                        return keyList.get(1)-1;
                    }
                }
            }
            else if(n1 ==2  && n2 == 2) {
                if ( !map.get(keyList.get(1)-1).equals(errorValue) &&  !map.get(keyList.get(1)+1).equals(errorValue) ) {
                    return keyList.get(1)-1;
                }
                else if ( !map.get(keyList.get(1)-1).equals(errorValue) && map.get(keyList.get(1)+1).equals(errorValue) ) {
                    return keyList.get(0);
                }
                else if ( map.get(keyList.get(1)-1).equals(errorValue) && !map.get(keyList.get(1)+1).equals(errorValue) ) {
                    return keyList.get(1);
                }
            }
        }
        return 0;
    }

    private UserLocation makeTrilUl(DistanceVO distanceVO, int triangleNum) {
        ArrayList<Double> tmp_arr = distanceVO.getDistance_arr();
        double distance1 = 0;
        double distance2 = 0;
        double distance3 = 0;
        Ap ap1;
        Ap ap2;
        Ap ap3;

        UserLocation tmpUl;

        switch (triangleNum) {
            case 0:
                break;
            case 1:
                distance1 = tmp_arr.get(0);
                distance2 = tmp_arr.get(1);
                distance3 = tmp_arr.get(2);
                break;
            case 2:
                distance1 = tmp_arr.get(1);
                distance2 = tmp_arr.get(2);
                distance3 = tmp_arr.get(3);
                break;
            case 3:
                distance1 = tmp_arr.get(2);
                distance2 = tmp_arr.get(3);
                distance3 = tmp_arr.get(4);
                break;
            case 4:
                distance1 = tmp_arr.get(3);
                distance2 = tmp_arr.get(4);
                distance3 = tmp_arr.get(5);
                break;
            case 5:
                distance1 = tmp_arr.get(4);
                distance2 = tmp_arr.get(5);
                distance3 = tmp_arr.get(6);
                break;
            case 6:
                distance1 = tmp_arr.get(5);
                distance2 = tmp_arr.get(6);
                distance3 = tmp_arr.get(7);
                break;
        }

        if (triangleNum != 0) {
            if (triangleNum % 2 == 0) {     // (1~8중에) 짝수인 경우
                ap1 = new Ap((w / 2.0) * (triangleNum - 1), h, distance1);
                ap2 = new Ap((w / 2.0) * triangleNum, 0, distance2);
                ap3 = new Ap((w / 2.0) * (triangleNum + 1), h, distance3);
            } else {
                ap1 = new Ap((w / 2.0) * (triangleNum - 1), 0, distance1);
                ap2 = new Ap((w / 2.0) * triangleNum, h, distance2);
                ap3 = new Ap((w / 2.0) * (triangleNum + 1), 0, distance3);
            }
            Trilateration tr = new Trilateration(distanceVO.getDeviceName(), ap1, ap2, ap3);
            tmpUl = tr.calcUserLocation();
        } else {
            tmpUl = new UserLocation(999, 999, "ddd");
//            tmpUl = null;
        }
        return tmpUl;
    }

    public double calcDistance(double tempRssi) {

        tempAlpha = -56;
        lossNum = 3;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }


    //엑셀 파일 만들기
    public void createCsvEx2(UserLocation originalUl, UserLocation kalmanUl, UserLocation aiOriginalUl, UserLocation aiProximityUl) {
        try {
            poiHelper.writeExcelforLocFilter(originalUl, kalmanUl, aiOriginalUl, aiProximityUl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
@Slf4j //로깅 어노테이션
public class AIPositioningService {
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99
    Map<String, DistanceVO> user_map = new HashMap<String, DistanceVO>();

    UserLocation aiUl;
    MAC mac;
    DistanceVO distanceVO;
    ArrayList<Double> prev_list;

    double aiDistance;

    String user_device_name;
    int proxiCheckNum;
    int triangleNum;
    int beacon_amount;

    Ap proximityAp;


    UserLocation aiOriUl;
    LocMAFilter locMAFilter;
    RemoveOutlier rm;

    public AIPositioningService() {
        mac = new MAC();
        locMAFilter = new LocMAFilter();
        rm = new RemoveOutlier();
//        distanceVO = new DistanceVO();
//        System.out.println(mac.getMac_addr_idx_dict());
    }

    public UserLocation trilateration(PositionVO positionVO) {
        user_device_name = positionVO.getDeviceName();
        if (!user_map.containsKey(user_device_name)) {
            distanceVO = new DistanceVO(user_device_name);
            user_map.put(user_device_name, distanceVO);
        }
        proxiCheckNum = 0;
        // -------------------- 수정 필요 (AI 적용) --------------------
        aiDistance = 2.0; //ex) prediction.predAiDistance(positionVO.getRssi())  -> AI 예측 코드 들어갈 자리
        // -----------------------------------------------------------
        int mac_idx = mac.getMac_addr_idx_dict().get(positionVO.getMac());
//        System.out.println(mac_idx);

        prev_list = user_map.get(user_device_name).getDistance_arr();
//        System.out.println("prev ="+prev_list);
        prev_list.set(mac_idx, aiDistance);
        user_map.get(user_device_name).setDistance_arr(prev_list);

        System.out.println("");
        System.out.println(user_device_name+" | list = "+user_map.get(user_device_name).getDistance_arr());
//        System.out.println("");
        
        
        // Proximity 검사
        proxiCheckNum = checkProximity(user_map.get(user_device_name));
        System.out.println("");
        System.out.println("Proxi Check Num = "+proxiCheckNum);

//        beacon_amount = user_map.get(user_device_name).getSize();

        if (proxiCheckNum != 0) {
            // Proximity 측위
            proximityAp = makeProximityAP(user_map.get(user_device_name), proxiCheckNum);
            Trilateration proximityTr = new Trilateration(user_map.get(user_device_name).getDeviceName(), proximityAp, proxiCheckNum);
            // 측위 결과
            aiOriUl = proximityTr.calcProximityLocation();
//            log.info();
        } else {
            // 삼변측량 측위
            triangleNum = selectTriangle(user_map.get(user_device_name));
            System.out.println("Selected Triangle Num = " + triangleNum);
            System.out.println("");
            // 측위 결과
            aiOriUl = makeTrilUl(user_map.get(user_device_name), triangleNum);
        }
        //좌표 이상치 제거
        if (rm.rmXYOutlier(aiOriUl, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
//            aiUl = new UserLocation(999, 999, "ddd");
            return null;
        }
        // ----- MAF 코드 -----
//        else {
//            aiUl = locMAFilter.push(kalmanProximityUl);
//            if(aiUl == null) {
//                aiUl = new UserLocation(999, 999, "ddd");
//            }
//        }
        // -------------------
        return aiOriUl;
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
        }
        return tmpUl;
    }
}

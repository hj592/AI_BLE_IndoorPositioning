package com.example.bleLocationSystem.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.bleLocationSystem.LocFilterTestUI;
import com.example.bleLocationSystem.TestUI;
import com.example.bleLocationSystem.UI;
import com.example.bleLocationSystem.model.*;
import com.example.bleLocationSystem.originalTestUI;
import com.example.bleLocationSystem.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.util.*;

@RestController
@Slf4j //로깅 어노테이션
public class ApController {
    // Real Real Real
    PositioningService positioningService = new PositioningService();

    AIPositioningService aiPositioningService = new AIPositioningService();

    //실제
//    ApService apService = new ApService();
    UI ui = new UI(aiPositioningService.getW(),aiPositioningService.getH());
    UserLocation ul;
    Map<String, Double> map = new HashMap<String, Double>();

    VO vo;

    CO co = new CO();

    boolean coDangerTmp;

    double coDangerTmpFloat = 0.0;

    //Original Test
//    OriginalTestService originalService = new OriginalTestService();
//    UserLocation ul;
//    originalTestUI ui = new originalTestUI(originalService.getW(), originalService.getH());


    //Test
//    TestService testService = new TestService();
//    TestUI ui = new TestUI(testService.getW(), testService.getH());
//    UserLocation ul;
//    ArrayList<UserLocation> ulList;
//    Map<String, Double> map = new HashMap<String, Double>();


    //RSSI Test for Experiment 1
//    VO vooo;
//    RSSIFilterTestService testService = new RSSIFilterTestService();

    //Loc Filter Test
//    LocFiterTestService locService = new LocFiterTestService();
//    ArrayList<UserLocation> ulList;
//    LocFilterTestUI ui = new LocFilterTestUI(locService.getW(), locService.getH());

    //Thres Test
//    ThresTestService thresTestService = new ThresTestService();
//    ArrayList<UserLocation> ulList;
//    LocFilterTestUI ui = new LocFilterTestUI(locService.getW(), locService.getH());

    //Location Filter Test
//    LocationFilterTestService locationService = new LocationFilterTestService();
//    ArrayList<UserLocation> ulList;
//    LocFilterTestUI ui = new LocFilterTestUI(locationService.getW(), locationService.getH());

    //Threshold Test
//    ThresholdTestService thresholdTestService = new ThresholdTestService();
//    ArrayList<UserLocation> ulList;
//    LocFilterTestUI ui = new LocFilterTestUI(thresholdTestService.getW(), thresholdTestService.getH());

    //Dinamic Test
//    LocationFilterTestService locationService = new LocationFilterTestService();
//    ArrayList<UserLocation> ulList;
//    TestUI ui = new TestUI(locationService.getW(), locationService.getH());



    //앱으로부터 ap1, ap2, ap3 각각의 거리값 받기
    // @PostMapping("/api/distance")
    // public ResponseEntity<Map<String, Double>> receiveDistance(VO vo) throws Exception {

    //테스트시
//    @PostMapping("/api/distance")
//    public void receiveDistance(VO vo) throws Exception {

        //논문 실험 1 테스트시
//    @PostMapping("/api/distance")
//    public ResponseEntity<VO> receiveDistance(VO vo) throws Exception {

//    @Autowired
//    private PredictionTestService predictionTestService;

//    @Autowired
//    private ChartTestService chartTestService;

    int count=0;

    //실제 사용
    @PostMapping("/api/positioning")
    public ResponseEntity<Map<String, Double>> receiveVo(@RequestBody PositionVO positionVO) throws Exception {
        count = count + 1;
        System.out.println("---------------------------------------- Start ----------------------------------------");
//        System.out.println("");
        System.out.println("count = "+count);
        System.out.println(positionVO);
        Map<String, Double> rep_map = new HashMap<String, Double>();

        ul = aiPositioningService.trilateration(positionVO);
        log.info("x = {}, y = {}", ul.getX(), ul.getY());

        co.setCOValue(positionVO.getCo());

        coDangerTmp = co.checkDanger();

        if (coDangerTmp == true) {
            coDangerTmpFloat = 1.0;
        }
        if (count == 15) {
            coDangerTmpFloat = 1.0;
        }
        log.info("coDanger float = {}", coDangerTmpFloat);
        if (ul != null) {
            ui.setUserLocation(ul);
            rep_map.put("triangleNum", positioningService.getKalmanTriangleNum() * 1.0);
            rep_map.put("x", ul.getX());
            rep_map.put("y", ul.getY());
            rep_map.put("coDanger", coDangerTmpFloat);
        } else {
            rep_map.put("triangleNum", -1.0);
            rep_map.put("x", -1.0);
            rep_map.put("y", -1.0);
            rep_map.put("coDanger", coDangerTmpFloat);
        }


//        if (ul != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(map);
//        } else {
//            return ResponseEntity.status(HttpStatus.OK).body(map);
//        }

        return (ul != null) ?
                ResponseEntity.status(HttpStatus.OK).body(rep_map) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/predict")
    public ResponseEntity<String> predictDistance(@RequestBody ArrayList<Double> rssiData) {
        // Service 호출하여 Python 스크립트 실행
        System.out.println(rssiData);
//        String result = predictionTestService.runPythonPrediction(rssiData);
//        System.out.println(result);

        if(rssiData != null) {
            System.out.println("in");
//            chartTestService.processData(rssiData);
        }
        return (ul != null) ?
                ResponseEntity.ok(rssiData.toString()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        // 결과를 클라이언트에 반환
//        return ResponseEntity.ok(result);
    }

    //실제 사용 (CO 포함 X)
//    @PostMapping("/api/distance")
//    public ResponseEntity<Map<String, Double>> receiveDistance(@RequestBody VO vo) throws Exception {

    //이전버전 실제 사용 (CO 포함 JSON 받을때)
    @PostMapping("/api/distance")
    public ResponseEntity<Map<String, Double>> receiveDistance(@RequestBody JSONVO jsonVo) throws Exception {
        vo.setDeviceName(jsonVo.getDeviceName());
        vo.setRssi1(jsonVo.getRssi1());
        vo.setDistance1(jsonVo.getDistance1());

        vo.setRssi2(jsonVo.getRssi2());
        vo.setDistance2(jsonVo.getDistance2());

        vo.setRssi3(jsonVo.getRssi3());
        vo.setDistance3(jsonVo.getDistance3());

        vo.setRssi4(jsonVo.getRssi4());
        vo.setDistance4(jsonVo.getDistance4());

        vo.setRssi5(jsonVo.getRssi5());
        vo.setDistance5(jsonVo.getDistance5());

        vo.setRssi6(jsonVo.getRssi6());
        vo.setDistance6(jsonVo.getDistance6());

        vo.setRssi7(jsonVo.getRssi7());
        vo.setDistance7(jsonVo.getDistance7());

        vo.setRssi8(jsonVo.getRssi8());
        vo.setDistance8(jsonVo.getDistance8());

        co.setCOValue(jsonVo.getCO());

        coDangerTmp = co.checkDanger();

        if (coDangerTmp == true) {
            coDangerTmpFloat = 1.0;
        }

        //-------------Real Real Real--------------
        System.out.println("1 : " + vo.getRssi1() + "| 2 : " + vo.getRssi2() + "| 3 : " + vo.getRssi3() + "| 4 : " + vo.getRssi4() + "| 5 : " + vo.getRssi5() + "| 6 : " + vo.getRssi6() + "| 7 : " + vo.getRssi7() + "| 8 : " + vo.getRssi8());
//        ul = positioningService.trilateration(vo);
        if (ul != null) {
//            ui.setUserLocation(ul);
            map.put("triangleNum", positioningService.getKalmanTriangleNum() * 1.0);
            map.put("x", ul.getX());
            map.put("y", ul.getY());
            map.put("coDanger", coDangerTmpFloat);
        }

        return (ul != null) ?
                ResponseEntity.status(HttpStatus.OK).body(map) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }



        //-------------Real--------------
//        ul = apService.trilateration(vo);
//
//        if(ul != null) {
//            ui.setUserLocation(ul);
//            map.put("triangleNum", apService.getTriangleNum()*1.0);
//            map.put("x", ul.getX());
//            map.put("y", ul.getY());
//        }
//
//        return (ul != null) ?
//                ResponseEntity.status(HttpStatus.OK).body(map) :
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();


        //Original Test
//        ul = originalService.trilateration(vo);
//        if(ul != null) {
//            ui.setUserLocation(ul);
//        }


//        ulList = testService.trilateration(vo);

//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//            map.put("triangleNum", testService.getTriangleNum()*1.0);
//            map.put("x", ul.getX());
//            map.put("y", ul.getY());
//        }


        //--------------Test--------------
//        ul = testService.trilateration(vo);
//        ulList = null;
//        ulList = testService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//            map.put("triangleNum", testService.getTriangleNum()*1.0);
//            map.put("x", ul.getX());
//            map.put("y", ul.getY());
//        }

//        return (ulList != null) ?
//                ResponseEntity.status(HttpStatus.OK).body(ulList.get(2)) :
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

//    }

        //--------------RSSI Test for Experiment 1--------------
        //        ul = testService.trilateration(vo);
//    vooo = null;
//    vooo = testService.trilateration(vo);
//
//        return (vooo != null) ?
//            ResponseEntity.status(HttpStatus.OK).body(vooo) :
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();


        //Loc Fiter Test
//        ulList = null;
//        ulList = locService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }

        //Thres Test
//        ulList = null;
//        ulList = thresTestService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }

        //--------------Location Filter Test--------------
        //--------------Threshold Test--------------

        //Location Filter Test
//        ulList = null;
//        ulList = locationService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//            map.put("triangleNum", locationService.getTriangleNum()*1.0);
//            map.put("x", ulList.get(2).getX());
//            map.put("y", ulList.get(2).getY());
//        }
//
//        return (ulList != null) ?
//        ResponseEntity.status(HttpStatus.OK).body(map) :
//        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();




        //Threshold Test
//        ulList = null;
//        ulList = thresholdTestService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }

        //--------------Dynamic Test--------------
//        ulList = null;
//        ulList = locationService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }

//    }
}

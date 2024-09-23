package com.example.bleLocationSystem.model;

import java.util.ArrayList;

public class Predictor {
    public native float predictDistance(ArrayList<Double> rssiValues);

    static {
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\c10.dll");
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\asmjit.dll");
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\libiomp5md.dll");

        //System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\torch_global_deps.dll");
        //System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\uv.dll");
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\fbgemm.dll");
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\libiompstubs5md.dll");
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\uv.dll");
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\torch_cpu.dll");
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\torch.dll");
        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\c10.dll");

        //System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\pytorch_jni.dll");

        //System.load("C:\\Users\\heejin\\Desktop\\chart\\LibtorchPredictor\\out\\production\\LibtorchPredictor\\torch_cpu.dll");  // DLL 이름

        //System.loadLibrary("lstmcnnTest");  // DLL 이름
        System.load("C:\\Users\\heejin\\Desktop\\chart\\LibtorchPredictor\\lstmcnnTest.dll");  // DLL 이름
    }
}

package com.example.bleLocationSystem.model;

public class LibtorchPredictor {
//    public native float predictDistance(ArrayList<Double> rssiValues);

    public native float predictDistance(float[] rssiValues);

    static {

        // HJ
//        String root_dir_path = '';
        // JH
//        String root_dir_path = "C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\";
        // HJ
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\c10.dll");
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\asmjit.dll");
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\libiomp5md.dll");
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\fbgemm.dll");
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\libiompstubs5md.dll");
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\uv.dll");
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\torch_cpu.dll");
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\torch.dll");
//        System.load("C:\\Users\\heejin\\Documents\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\c10.dll");
//
//        //System.loadLibrary("lstmcnnTest");  // DLL 이름
//        System.load("C:\\Users\\heejin\\Desktop\\chart\\LibtorchPredictor\\lstmcnnTest.dll");  // DLL 이름

        // JH
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\c10.dll");
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\asmjit.dll");
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\libiomp5md.dll");
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\fbgemm.dll");
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\libiompstubs5md.dll");
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\uv.dll");
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\torch_cpu.dll");
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\torch.dll");
        System.load("C:\\Users\\JaeHyuk\\Desktop\\libtorch-win-shared-with-deps-2.4.1+cpu\\libtorch\\lib\\c10.dll");

//        System.load( root_dir_path + "lib\\c10.dll");
//        System.load(root_dir_path + "lib\\asmjit.dll");
//        System.load(root_dir_path + "lib\\libiomp5md.dll");
//        System.load(root_dir_path + "lib\\fbgemm.dll");
//        System.load(root_dir_path + "lib\\libiompstubs5md.dll");
//        System.load(root_dir_path + "lib\\uv.dll");
//        System.load( root_dir_path + "lib\\torch_cpu.dll");
//        System.load(root_dir_path + "lib\\torch.dll");
//        System.load(root_dir_path + "lib\\c10.dll");

        //System.loadLibrary("lstmcnnTest");  // DLL 이름
        System.load("C:\\Users\\JaeHyuk\\Desktop\\LibtorchPredictor\\lstmcnnTest.dll");  // DLL 이름
    }
}

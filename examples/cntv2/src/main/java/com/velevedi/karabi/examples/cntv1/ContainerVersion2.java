package com.velevedi.karabi.examples.cntv1;

import com.velevedi.karabi.Container;

public class ContainerVersion2 implements Container {
    @Override
    public String id() {
        return "ContainerVersion2";
    }

    @Override
    public void init() {
        System.out.println("ContainerVersion2 init");
    }

    @Override
    public void close() {
//        System.out.println("ContainerVersion2 close");
    }
}

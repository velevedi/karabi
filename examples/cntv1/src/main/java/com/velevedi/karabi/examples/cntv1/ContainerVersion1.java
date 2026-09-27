package com.velevedi.karabi.examples.cntv1;

import com.velevedi.karabi.Container;

public class ContainerVersion1 implements Container {
    @Override
    public String id() {
        return "ContainerVersion1";
    }

    @Override
    public void init() {
        System.out.println("ContainerVersion1 init");
    }

    @Override
    public void close() {
//        System.out.println("ContainerVersion1 close");
    }
}

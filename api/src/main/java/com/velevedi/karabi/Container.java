package com.velevedi.karabi;

public interface Container extends AutoCloseable {

    String id();

    default void init() {}

}

package com.velevedi.karabi;

/// An entity that has a life cycle
/// - init() - initializes container with configuration
/// - close() - frees all resources
public interface Container extends AutoCloseable {

    String id();

    default void init(Context context) {}

}

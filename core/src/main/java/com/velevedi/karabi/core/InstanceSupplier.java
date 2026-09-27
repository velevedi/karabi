package com.velevedi.karabi.core;

public interface InstanceSupplier<T> {

    void registerListener(Listener<T> listener);

    interface Listener<T> {
        void onChange(Snapshot<T> newValue);
    }

}

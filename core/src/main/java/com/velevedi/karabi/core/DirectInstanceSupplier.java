package com.velevedi.karabi.core;

import com.velevedi.karabi.Container;
import com.velevedi.karabi.Manager;

import java.net.URL;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArraySet;

public class DirectInstanceSupplier<T extends Container> implements InstanceSupplier<T> {

    private final Class<T> type;
    private final CopyOnWriteArraySet<Listener<T>> listeners = new CopyOnWriteArraySet<>();

    public DirectInstanceSupplier(Class<T> type) {
        this.type = type;
    }

    @Override
    public void registerListener(Listener<T> listener) {
        listeners.add(listener);
    }

    public void reload(URL[] urls) {
        ChildFirstClassloader classLoader = new ChildFirstClassloader(urls, Manager.class.getClassLoader());

        try {
            ServiceLoader<T> loader = ServiceLoader.load(type, classLoader);
            T instance = loader.findFirst().orElse(null);
            if (instance == null) {
                throw new IllegalStateException(
                        "No implementations found for [" + type.getSimpleName() + "] in [" + Arrays.toString(urls) + "]"
                );
            }
            for  (Listener<T> listener : listeners) {
                listener.onChange(new Snapshot<>(instance, classLoader));
            }
        } catch (Throwable t) {
            try {
                classLoader.close();
            } catch (Exception e) {
                t.addSuppressed(t);
            }
            throw t;
        }
    }
}

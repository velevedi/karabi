package com.velevedi.karabi.core;

import java.lang.ref.WeakReference;
import java.net.URLClassLoader;
import java.time.Instant;

/// Holds instance along the classloader the instance was created with.
public final class Snapshot<T> implements AutoCloseable {

    private volatile T instance;
    private volatile ChildFirstClassloader classLoader;
    private final WeakReference<ChildFirstClassloader> weakReference;
    private final Instant timestamp =  Instant.now();

    public Snapshot(T instance, ChildFirstClassloader classLoader) {
        this.instance = instance;
        this.classLoader = classLoader;
        this.weakReference = new WeakReference<>(classLoader);
    }

    public T instance() {
        return instance;
    }

    public URLClassLoader classLoader() {
        return classLoader;
    }

    public Instant timestamp() {
        return timestamp;
    }

    @Override
    public void close() throws Exception {

        Exception closeException = null;
        try {
            if (instance instanceof AutoCloseable live) {
                live.close();
            }
        } catch (Exception e) {
            closeException = e;
        }

        try {
            if (classLoader != null) {
                classLoader.close();
            }
        } catch (Exception e) {
            if (closeException != null) {
                closeException.addSuppressed(e);
            } else {
                closeException = e;
            }
            throw closeException;
        } finally {
            instance = null;
            classLoader = null;
        }
    }

    public boolean gcCleared() {
        return weakReference.get() == null;
    }
}

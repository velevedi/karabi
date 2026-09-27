package com.velevedi.karabi;

import com.velevedi.karabi.core.InstanceSupplier;
import com.velevedi.karabi.core.Snapshot;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/// One manager per container.
public class Manager<T extends Container> implements AutoCloseable {

    private final Class<T> type;
    private final AtomicReference<Snapshot<T>> current = new AtomicReference<>();
    private final T proxy;

    private final ReentrantLock lock = new ReentrantLock();

    public Manager(Class<T> type, InstanceSupplier<T> asyncSupplier) {
        this.type = type;
        this.proxy = createProxy();

        asyncSupplier.registerListener(newSnapshot -> {
            lock.lock();
            try {
                Snapshot<T> oldHolder = current.getAndSet(newSnapshot);
                if (oldHolder != null) {
                    unload(oldHolder);
                }
            } finally {
                lock.unlock();
            }
        });
    }

    public T current() {
        return proxy;
    }

    @Override
    public void close() {
        lock.lock();
        try {
            Snapshot<T> oldHolder = current.getAndSet(null);
            if (oldHolder != null) {
                unload(oldHolder);
            }
        } finally {
            lock.unlock();
        }
    }

    private void unload(Snapshot<T> snapshot) {
        try {
            if (snapshot.instance() != null) {
                try {
                    snapshot.instance().close();
                } catch (Exception e) {
                    System.err.println("Error while closing container: " + e);
                }
            }
            snapshot.close();
        } catch (Throwable t) {
            throw new IllegalStateException("Unable to unload container", t);
        }
    }

    @SuppressWarnings("unchecked")
    private T createProxy() {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "toString" -> "Proxy[" + type.getSimpleName() + "]";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> method.invoke(this, args);
                };
            }

            Snapshot<T> snapshot = current.get();
            if (snapshot == null || snapshot.instance() == null) {
                throw new IllegalStateException(
                        "No container loaded for " + type.getSimpleName());
            }
            return method.invoke(snapshot.instance(), args);
        };

        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                handler
        );
    }

}

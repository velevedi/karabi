package com.velevedi.karabi.core;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static java.util.Collections.enumeration;
import static java.util.Collections.list;

public class ChildFirstClassloader extends URLClassLoader {

    private final ClassLoader systemClassLoader;

    public ChildFirstClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.systemClassLoader = getSystemClassLoader();
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {

            Class<?> loadedClass = findLoadedClass(className);

            if (loadedClass == null) {

                try { // jdk classes + module classes + host application class path
                    if (systemClassLoader != null) {
                        loadedClass = systemClassLoader.loadClass(className);
                    }
                } catch (ClassNotFoundException ex) {}

                try { // deployed application classpath
                    if (loadedClass == null) {
                        loadedClass = findClass(className);
                    }
                } catch (ClassNotFoundException e) {
                    // parent first classloader
                    loadedClass = super.loadClass(className, resolve);
                }
            }

            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> resources = new ArrayList<>();

        // jdk classes + module classes + host application class path
        Enumeration<URL> systemResources = systemClassLoader.getResources(name);
        if (systemResources != null) {
            resources.addAll(list(systemResources));
        }
        // deployed application classpath
        Enumeration<URL> childResources = findResources(name);
        if (childResources != null) {
            resources.addAll(list(childResources));
        }
        // parent first classloader
        Enumeration<URL> parentResources = super.findResources(name);
        if (parentResources != null) {
            resources.addAll(list(parentResources));
        }

        return enumeration(resources);
    }

    @Override
    public URL getResource(String name) {
        URL resource = null;
        // jdk classes + module classes + host application class path
        if (systemClassLoader != null) {
            resource = systemClassLoader.getResource(name);
        }
        // deployed application classpath
        if (resource == null) {
            resource = findResource(name);
        }
        // parent first classloader
        if (resource == null) {
            resource = super.getResource(name);
        }
        return resource;
    }

}

package com.velevedi.karabi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PomExtractor implements Function<InputStream, String> {
    @Override
    public String apply(InputStream inputStream) {

        try (JarInputStream jarInputStream = new JarInputStream(inputStream)) {
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (name.startsWith("META-INF/maven/") && name.endsWith("/pom.xml")) {
                    return new String(jarInputStream.readAllBytes(), UTF_8);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse pom.xml", e);
        }
        throw new IllegalStateException("Unable to find pom.xml");
    }

}




package com.velevedi.karabi.util;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class PomExtractorTest {

    @Test
    void extractPom() {

        InputStream jarStream = this.getClass().getClassLoader().getResourceAsStream("lib/cntv1-1.0-SNAPSHOT.jar");

        String pom = new PomExtractor().apply(jarStream);

        assertThat(pom, containsString("<artifactId>cntv1</artifactId>"));
    }

}

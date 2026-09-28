package com.velevedi.karabi;

import com.velevedi.karabi.core.DirectReloadSupplier;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.fail;

class ManagerTest {

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void reload() {

        URL url1 = this.getClass().getClassLoader().getResource("lib/cntv1-1.0-SNAPSHOT.jar");
        URL url2 = this.getClass().getClassLoader().getResource("lib/cntv2-1.0-SNAPSHOT.jar");

        String previous = "";

        DirectReloadSupplier<Container> reloader = new DirectReloadSupplier<>(Container.class);

        try (Manager manager = new Manager(Container.class, reloader)) {

            for (int i = 0; i < 1000; i++) {

                if (i % 2 == 0) {
                    reloader.reload(new URL[]{url1});
                } else {
                    reloader.reload(new URL[]{url2});
                }

                String current = manager.current().id();

                if (previous.equals(current)) {
                    fail("reload should change the implementation");
                }
                previous = current;
            }
        }
    }
}

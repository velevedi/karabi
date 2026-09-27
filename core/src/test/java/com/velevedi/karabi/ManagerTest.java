package com.velevedi.karabi;

import com.velevedi.karabi.core.DirectInstanceSupplier;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

class ManagerTest {

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void basicLoad() throws MalformedURLException {

        Path path1 = Paths.get("/Users/velevedi/Projects/karabi/examples/cntv1/target/cntv1-1.0-SNAPSHOT.jar");
        URL url1 = path1.toUri().toURL();

        Path path2 = Paths.get("/Users/velevedi/Projects/karabi/examples/cntv2/target/cntv2-1.0-SNAPSHOT.jar");
        URL url2 = path2.toUri().toURL();

        String previous = "";

        DirectInstanceSupplier<Container> reloader = new DirectInstanceSupplier<>(Container.class);

        try (Manager manager = new Manager(Container.class, reloader)) {

            for (int i = 0; i < 1000; i++) {

                if (i % 2 == 0) {
                    reloader.reload(new URL[]{url1});
                } else {
                    reloader.reload(new URL[]{url2});
                }

                String current = manager.current().id();

                if (previous.equals(current)) {
                    System.out.println(">>>> Error");
                    System.exit(-1);
                }
                previous = current;
            }
        }
    }
}

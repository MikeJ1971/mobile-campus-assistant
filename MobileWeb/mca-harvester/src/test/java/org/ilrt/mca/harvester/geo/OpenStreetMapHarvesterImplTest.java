package org.ilrt.mca.harvester.geo;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import org.ilrt.mca.harvester.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static org.junit.Assert.assertTrue;

public class OpenStreetMapHarvesterImplTest extends AbstractTest {

    @Before
    public void setUp() throws IOException, InstantiationException {

        ws = new GrizzlyWebServer(9090);


        ws.addGrizzlyAdapter(new GrizzlyAdapter() {

            @Override
            public void service(GrizzlyRequest grizzlyRequest, GrizzlyResponse grizzlyResponse) {

                try {

                    InputStream is = getClass().getResourceAsStream("/pcavailability.html");

                    if (is != null) {
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader reader = new BufferedReader(isr);

                        PrintWriter writer = grizzlyResponse.getWriter();

                        String s;

                        while ((s = reader.readLine()) != null) {
                            writer.println(s);
                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, new String[]{"/"});
        ws.start();
    }

    @Test
    public void testSources() {
        assertTrue(true);
    }

    @After
    public void tearDown() throws IOException, InstantiationException {
        //ws.stop();

    }

    GrizzlyWebServer ws;
}

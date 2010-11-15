package org.ilrt.mca.harvester;

import com.hp.hpl.jena.sdb.util.StoreUtils;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import org.ilrt.mca.rdf.DataManager;
import org.ilrt.mca.rdf.StoreWrapper;
import org.ilrt.mca.rdf.StoreWrapperManager;
import org.ilrt.mca.rdf.StoreWrapperManagerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static org.junit.Assert.assertTrue;

public abstract class AbstractTest {

    protected void setUpStore() throws Exception {

        StoreWrapper storeWrapper = getStoreWrapper();

        if (StoreUtils.isFormatted(storeWrapper.getStore())) {
            storeWrapper.getStore().getTableFormatter().truncate();
        } else {
            storeWrapper.getStore().getTableFormatter().format();
        }

        assertTrue("The store is not formatted", StoreUtils.isFormatted(storeWrapper.getStore()));

        storeWrapper.close();
    }

    protected StoreWrapper getStoreWrapper() {
        return getStoreWrapperManager().getStoreWrapper();
    }

    protected StoreWrapperManager getStoreWrapperManager() {
        return new StoreWrapperManagerImpl(TEST_CONFIG);
    }

    protected void startServer(String resourcePath, String mediaType) throws IOException {
        ws = new GrizzlyWebServer(portNumber);

        ws.addGrizzlyAdapter(createAdapter(resourcePath, mediaType), new String[]{resourcePath});
        ws.start();
    }

    protected void stopServer() {
        ws.stop();
    }

    protected GrizzlyAdapter createAdapter(final String resourcePath, final String mediaType) {
        return new GrizzlyAdapter() {
            @Override
            public void service(GrizzlyRequest grizzlyRequest, GrizzlyResponse grizzlyResponse) {

                try {

                    grizzlyResponse.setContentType(mediaType);


                    OutputStream os = grizzlyResponse.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                   


                    InputStream is = getClass().getResourceAsStream(resourcePath);

                    if (is != null) {
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader reader = new BufferedReader(isr);

                        PrintWriter writer = new PrintWriter(osw);


                        String s;

                        while ((s = reader.readLine()) != null) {
                            writer.println(s);
                        }

                    }
                    osw.flush();
                    os.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        };
    }


    private String TEST_CONFIG = "/test-sdb.ttl";
    public String host = "http://localhost";
    GrizzlyWebServer ws;
    public DataManager dataManager;

    public int portNumber = 9090;
}
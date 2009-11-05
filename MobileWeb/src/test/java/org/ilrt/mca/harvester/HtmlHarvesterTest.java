package org.ilrt.mca.harvester;

import org.junit.Test;
import org.w3c.tidy.Tidy;
import static junit.framework.Assert.assertTrue;

import javax.swing.text.Document;
import java.io.StringWriter;

public class HtmlHarvesterTest {

    @Test
    public void test() throws Exception {

        Tidy tidy = new Tidy();
        tidy.setXHTML(true);

        StringWriter writer = new StringWriter();

        tidy.parse(getClass().getResourceAsStream("/pcavailability.html"), writer);

        System.out.println(writer.getBuffer().toString());


        assertTrue(true);
    }

}

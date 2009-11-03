/*
 *  © University of Bristol
 */

package org.ilrt.mca.domain.events;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class EventItemImplTest {

    public EventItemImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testCloseAllTags() {
        String s;
        String s_expected;
        String result;

        s = "hello <b>world</b>";
        s_expected = "hello <b>world</b>";
        result = EventItemImpl.closeAllTags(s);
        assertEquals("Handling properly closed tags", s_expected, result);

        s = "hello <b>world";
        s_expected = "hello <b>world</b>";
        result = EventItemImpl.closeAllTags(s);
        assertEquals("Handling single closing tags", s_expected, result);

        s = "<span>hello <b>world</b>";
        s_expected = "<span>hello <b>world</b></span>";
        result = EventItemImpl.closeAllTags(s);
        assertEquals("Handling single closing tags with inner tag", s_expected, result);

        s = "<span>hello <b>world";
        s_expected = "<span>hello <b>world</b></span>";
        result = EventItemImpl.closeAllTags(s);
        assertEquals("Handling multiple closing tags", s_expected, result);

        s = "<span class='thisisit' id='xyz'>hello <b class nonsense=true>world";
        s_expected = "<span class='thisisit' id='xyz'>hello <b class nonsense=true>world</b></span>";
        result = EventItemImpl.closeAllTags(s);
        assertEquals("Handling tag attributes", s_expected, result);

        s = "<span>hello <b>world</b";
        s_expected = "<span>hello <b>world</b></span>";
        result = EventItemImpl.closeAllTags(s);
        assertEquals("Handling unclosed end tag 1",s_expected, result);

        s = "<span>hello <b>world</";
        s_expected = "<span>hello <b>world</b></span>";
        result = EventItemImpl.closeAllTags(s);
        assertEquals("Handling unclosed end tag 2", s_expected, result);

        s = "<span>hello <b>world<";
        s_expected = "<span>hello <b>world</b></span>";
        result = EventItemImpl.closeAllTags(s);
        assertEquals("Handling unclosed end tag 3", s_expected, result);
    }

}
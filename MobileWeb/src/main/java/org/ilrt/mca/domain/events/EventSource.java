/*
 *  © University of Bristol
 */

package org.ilrt.mca.domain.events;

import org.ilrt.mca.domain.Item;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public interface EventSource extends Item {

    String getHTMLLink();

    String getiCalLink();
}

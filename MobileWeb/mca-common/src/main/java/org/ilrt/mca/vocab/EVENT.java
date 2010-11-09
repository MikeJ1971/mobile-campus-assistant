/*
 * Copyright (c) 2009, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.ilrt.mca.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Chris Bailey (c.bailey@bristol.ac.uk)
 */
public class EVENT {

    private static final Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://www.w3.org/2002/12/cal/ical#";

    public static String getURI() {
        return NS;
    }

    public static final Resource NAMESPACE = model.createResource(NS);

    public static final Resource event = model.createResource(NS + "Vevent");

    public static final Resource start = model.createResource(NS + "dtstart");

    public static final Resource end = model.createResource(NS + "dtend");

    public static final Property startDate = model.createProperty(NS + "dtstart");

    public static final Property endDate = model.createProperty(NS + "dtend");

    public static final Resource startDateResource = model.createResource(NS + "dtstart");

    public static final Resource endDateResource = model.createResource(NS + "dtend");

    public static final Property date = model.createProperty(NS + "date");

    public static final Property dateTime = model.createProperty(NS + "dateTime");

    public static final Property organizerName = model.createProperty(NS + "organizer");

    public static final Property organizerEmail = model.createProperty(NS + "calAddress");

    public static final Property location = model.createProperty(NS + "location");

    public static final Property subject = model.createProperty(NS + "summary");

    public static final Property description = model.createProperty(NS + "description");

    public static final Property UID = model.createProperty(NS + "uid");

    public static final Property byMonth = model.createProperty(NS + "bymonth");

    public static final Property byDay = model.createProperty(NS + "byday");

    public static final Property frequency = model.createProperty(NS + "freq");

    public static final Property interval = model.createProperty(NS + "interval");

    public static final Property until = model.createProperty(NS + "until");

    public static final Property rrule = model.createProperty(NS + "rrule");
}

package org.ilrt.mca.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class MCA_GEO {

    private static final Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://vocab.bris.ac.uk/mca/geo#";

    public static String getURI() {
        return NS;
    }

    public static final Resource NAMESPACE = model.createResource(NS);

    public static final Resource Amenity = model.createResource(NS + "amenity");

    public static final Resource Shop = model.createResource(NS + "shop");

    public static final Resource Bar = model.createResource(NS + "bar");

    public static final Resource Cafe = model.createResource(NS + "cafe");

    public static final Resource Pub = model.createResource(NS + "pub");

    public static final Resource Restaurant = model.createResource(NS + "restaurant");

    public static final Resource PostBox = model.createResource(NS + "post_box");

    public static final Resource BicycleParking = model.createResource(NS + "bicycle_parking");

    public static final Resource WasteBasket = model.createResource(NS + "waste_basket");

    public static final Resource Bank = model.createResource(NS + "bank");

    public static final Resource PostOffice = model.createResource(NS + "post_office");

    public static final Resource Telephone = model.createResource(NS + "telephone");

    public static final Resource Theatre = model.createResource(NS + "theatre");

    public static final Resource Cinema = model.createResource(NS + "cinema");

    public static final Resource NightClub = model.createResource(NS + "nightclub");

    public static final Resource ArtsCentre = model.createResource(NS + "arts_centre");

    public static final Resource FastFood = model.createResource(NS + "fast_food");

    public static final Resource Pharmacy = model.createResource(NS + "pharmacy");

    public static final Resource Supermarket = model.createResource(NS + "supermarket");

    public static final Resource Books = model.createResource(NS + "books");

    public static final Resource MusicalInstrument = model.createResource(NS + "musical_instruments");

    public static final Property hasTag = model.createProperty(NS + "hasTag");
}

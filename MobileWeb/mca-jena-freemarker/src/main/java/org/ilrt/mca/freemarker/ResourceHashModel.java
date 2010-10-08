package org.ilrt.mca.freemarker;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PrefixMapping;
import freemarker.template.SimpleCollection;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class to help display a Jena Resource in a freemarker template. For example, for the
 * following triple:
 * <p/>
 * &lt;http://example.org/1/&gt; &lt;http://www.w3.org/2000/01/rdf-schema#label&gt; "Example Label" .
 * <p/>
 * The following freemarker syntax:
 * <p/>
 * &lt;p&gt;${resource}&lt;p&gt;
 * &lt;p&gt;${resource['http://www.w3.org/2000/01/rdf-schema#label']}&lt;p&gt;
 * &lt;p&gt;${resource['rdfs#label']}&lt;p&gt;
 * <p/>
 * Would produce:
 * <p/>
 * &lt;p&gt;http://example.org/1/&lt;p&gt;
 * &lt;p&gt;Example Label&lt;p&gt;
 * &lt;p&gt;Example Label&lt;p&gt;
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class ResourceHashModel implements TemplateHashModelEx, TemplateScalarModel {

    // ---------- constructors

    public ResourceHashModel(Resource resource) {
        this.resource = resource;
        this.prefixMapping = PrefixMapping.Factory.create();

        // hard coded - not ideal
        prefixMapping.setNsPrefixes(PrefixMapping.Standard);
        prefixMapping.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        prefixMapping.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        prefixMapping.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
        prefixMapping.setNsPrefix("mca", "http://org.ilrt.mca/registry#");
        prefixMapping.setNsPrefix("rss", "http://purl.org/rss/1.0/");
        prefixMapping.setNsPrefix("ical", "http://www.w3.org/2002/12/cal/ical#");
    }

    // ---------- TemplateModel interface methods

    @Override
    public int size() throws TemplateModelException {
        StmtIterator iterator = resource.listProperties();
        return iterator.toList().size();
    }

    @Override
    public TemplateCollectionModel keys() throws TemplateModelException {

        ArrayList<String> list = new ArrayList<String>();

        StmtIterator iterator = resource.listProperties();

        while (iterator.hasNext()) {
            list.add(iterator.next().getPredicate().getURI());
        }

        return new SimpleCollection(list);
    }

    @Override
    public TemplateCollectionModel values() throws TemplateModelException {

        ArrayList<String> list = new ArrayList<String>();

        StmtIterator iterator = resource.listProperties();

        while (iterator.hasNext()) {

            RDFNode node = iterator.next().getObject();

            if (node.isLiteral()) {
                list.add(((Literal) node).getLexicalForm());
            } else if (node.isResource()) {
                list.add(((Resource) node).getURI());
            }

        }

        return new SimpleCollection(list);
    }

    // ---------- TemplateHashModel interface methods

    @Override
    public TemplateModel get(String s) throws TemplateModelException {


        String uri = prefixMapping.expandPrefix(s);

        Property property = ResourceFactory.createProperty(uri);

        if (!resource.hasProperty(property)) {
            return null; // bail
        }

        List<TemplateModel> list = new ArrayList<TemplateModel>();

        StmtIterator iter = resource.listProperties(property);

        while (iter.hasNext()) {
            list.add(resolveModel(iter.next().getObject()));
        }

        return new SimpleSequence(list);
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {

        StmtIterator iterator = resource.listProperties();
        return iterator.toList().size() == 0;
    }

    // ---------- TemplateScalarModel interface methods


    @Override
    public String getAsString() throws TemplateModelException {
        if (resource.getURI() == null) {
            return INVALID_URL;          // b-nodes return null and their ids are useless
        } else {
            return resource.getURI();
        }
    }

    private TemplateModel resolveModel(RDFNode node) {

        if (node.isLiteral()) {

            try {

                Object value = ((Literal) node).getValue();

                if (value instanceof String) {
                    return new SimpleScalar((String) value);
                } else if (value instanceof Number) {
                    return new SimpleNumber((Number) value);
                } else if (value instanceof Date) {
                    return new SimpleDate((Date) value, TemplateDateModel.UNKNOWN);
                } else if (value instanceof Calendar) {
                    return new SimpleDate(((Calendar) value).getTime(), TemplateDateModel.UNKNOWN);
                } else if (value instanceof XSDDateTime) {
                    return new SimpleDate(((XSDDateTime) value).asCalendar().getTime(),
                            TemplateDateModel.DATETIME);
                }

                return new SimpleScalar(((Literal) node).getLexicalForm());

            } catch (DatatypeFormatException ex) {
                return new SimpleScalar(((Literal) node).getLexicalForm());
            }


        } else if (node.isResource()) {
            return new ResourceHashModel(((Resource) node));
        }

        return null;
    }

    private final Resource resource;
    protected final PrefixMapping prefixMapping;
    public final static String INVALID_URL = "http://invalid.org";
}

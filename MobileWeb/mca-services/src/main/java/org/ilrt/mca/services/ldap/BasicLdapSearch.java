package org.ilrt.mca.services.ldap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;
import org.apache.log4j.Logger;
import org.ilrt.mca.vocab.FOAF;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

public class BasicLdapSearch {

    public static void main(String[] args) {

        String filter = "(cn=Mike Jones)";

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldaps://ldap-srv.bris.ac.uk:636");
        env.put(Context.SECURITY_PROTOCOL, "SSL");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");


        try {
            BasicLdapSearch basicLdapSearch = new BasicLdapSearch(env);
            basicLdapSearch.search(filter);
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    public BasicLdapSearch(Hashtable<String, String> env) throws NamingException {

        this.env = env;


    }


    private Model search(String filter) throws NamingException {


        Model m = ModelFactory.createDefaultModel();

        log.info("Connecting to LDAP server.");
        DirContext ctx = new InitialDirContext(env);


        NamingEnumeration<SearchResult> results =
                ctx.search("cn=Users,dc=bris,dc=ac,dc=uk", filter, null);


        while (results.hasMore()) {

            SearchResult result = results.nextElement();

            Resource resource = createContact(result);

            if (resource != null) {
                m.add(resource.getModel());
            }


        }


        m.write(System.out);

        return null;
    }

    private Resource createContact(SearchResult result) throws NamingException {

        Attributes attributes = result.getAttributes();

        String uid = (String) attributes.get("uid").get();

        System.out.println(result.getName());


        Resource resource = ModelFactory.createDefaultModel().createResource();

        // name
        if (attributes.get("displayName") != null) {
            String name = (String) attributes.get("displayName").get();
            resource.addLiteral(VCARD.NAME, name);
        }

        // job title
        if (attributes.get("title") != null) {
            String title = (String) attributes.get("title").get();
            resource.addLiteral(VCARD.TITLE, title);
        }


        // organizational unit
        if (attributes.get("ou") != null) {
            String ou = (String) attributes.get("ou").get();
            resource.addLiteral(VCARD.Orgname, ou);
        }

        // address
        if (attributes.get("postalAddress") != null) {
            String address = (String) attributes.get("postalAddress").get();

            if (attributes.get("postalCode") != null) {
                address = address + "," + attributes.get("postalCode").get();
            }

            resource.addLiteral(VCARD.ADR, address);
        }

        // email
        if (attributes.get("mail") != null) {
            String mail = (String) attributes.get("mail").get();
            resource.addLiteral(VCARD.EMAIL, mail);
        }

        // telephone number
        if (attributes.get("telephoneNumber") != null) {
            String tel = (String) attributes.get("telephoneNumber").get();
            handleTelephone(tel);
            resource.addLiteral(VCARD.TEL, tel);
            resource.addProperty(FOAF.phone, handleTelephone(tel));
        }
        
        return resource;
    }

    private Resource handleTelephone(String telNumber) {

        telNumber = telNumber.replace(" ", "");
        telNumber = telNumber.replace("(0)", "");

        Resource r = ResourceFactory.createProperty("tel:" + telNumber);
        r.addProperty(RDFS.label, telNumber);


        return null;
    }


    Logger log = Logger.getLogger(org.ilrt.mca.services.ldap.BasicLdapSearch.class);

    private final Hashtable env;
}

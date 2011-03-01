package org.ilrt.mca.services.ldap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
<<<<<<< HEAD
<<<<<<< HEAD
=======
import com.hp.hpl.jena.vocabulary.DC;
>>>>>>> 3d4abc5... work on searching ldap and displaying results
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.ilrt.mca.services.SearchService;
import org.ilrt.mca.vocab.FOAF;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
=======
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
>>>>>>> 3749458... Added service module and started support for LDAP
import java.util.Hashtable;

public class BasicLdapSearch implements SearchService {

<<<<<<< HEAD

    /**
     * Constructor for the ldap search service. Two hash tables need to be provided. First,
     * the connection details for the ldap server. These are related to javax.naming.Context
     * and contain values for "java.naming.provider.url", "java.naming.security.protocol" etc.
     * Second, more general configuration details for retrieving data, such as mapping
     * attributes and defining a search template.
     *
     * @param env the values need to connect to an LDAP server.
     * @param cfg the values that provide mapping for attributes.
     */
    public BasicLdapSearch(Hashtable<Object, Object> env, Hashtable<Object, Object> cfg) {

        this.env = env;

        // get the limit of results
        try {
            RESULT_LIMIT = Integer.valueOf((String) cfg.get("resultLimit"));
        } catch (NumberFormatException ex) {
            RESULT_LIMIT = 10;
        }

        // get the attribute mapping values
        uidMapping = (String) cfg.get("uid");
        displayNameMapping = (String) cfg.get("displayName");
        titleMapping = (String) cfg.get("title");
        ouMapping = (String) cfg.get("ou");
        postalAddressMapping = (String) cfg.get("postalAddress");
        postCodeMapping = (String) cfg.get("postCode");
        mailMapping = (String) cfg.get("mail");
        telephoneNumberMapping = (String) cfg.get("telephoneNumber");

        // person URI prefix
        personUriPrefix = (String) cfg.get("personUriPrefix");

        // base DN for searches
        baseDN = (String) cfg.get("baseDN");


    }

    @Override
    public Resource search(Object... args) {

<<<<<<< HEAD
    public Model search(String filter) {

        Model m = ModelFactory.createDefaultModel();
=======
        if (args.length != 2) {
            throw new RuntimeException("Received " + args.length + " arguments. Expected 2");
        }

        Resource r = (Resource) args[0];
        String filter = (String) args[1];
>>>>>>> c2236f4... moved configureable stuff out to properties files. update the UI

        try {

            log.info("Connecting to LDAP server.");
            DirContext ctx = new InitialDirContext(env);

            SearchControls ctls = new SearchControls();
            ctls.setCountLimit(RESULT_LIMIT);

            NamingEnumeration<SearchResult> results =
                    ctx.search(baseDN, filter, ctls);

<<<<<<< HEAD
=======

>>>>>>> c2236f4... moved configureable stuff out to properties files. update the UI
            while (results.hasMore()) {
                createContact(m, results.nextElement());
            }

        } catch (SizeLimitExceededException ex) {
            r.addProperty(DC.description, "Your search returned more than " + RESULT_LIMIT +
                    " results. To refine your search, try entering more details (e.g. " +
                    "forename and surname).");
            log.info("Search results are limited");
        } catch (NamingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

<<<<<<< HEAD

        return m;
=======
        return r;
>>>>>>> c2236f4... moved configureable stuff out to properties files. update the UI
    }


    private void createContact(Model m, SearchResult result) throws NamingException,
            NoSuchAlgorithmException {

        // get the attributes from the directory
        Attributes attributes = result.getAttributes();


        // get the unique identifier for the user
        String uid = (String) attributes.get(uidMapping).get();
        String personUri = generateUri(uid);

<<<<<<< HEAD
        // In Bristol, policy dictates we can't advertise the UID externally.
        // I'd like a unique identifier, so hash the UID. Performance issues?
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(uid.getBytes());
        byte hash[] = digest.digest();
        char[] hex = Hex.encodeHex(hash); // create a hex value of the hash

        // TODO - look at creating a URI that can be dereferenced
        String personUri = "person://" + new String(hex);


        // TOO - URI for the person?
        Resource resource = m.createResource(personUri);
=======
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
>>>>>>> 3749458... Added service module and started support for LDAP
=======
        // TODO - URI for the person?
        Resource resource = r.getModel().createResource(personUri);
>>>>>>> c2236f4... moved configureable stuff out to properties files. update the UI

        // name
        if (displayNameMapping != null) {
            if (attributes.get(displayNameMapping) != null) {
                String name = (String) attributes.get(displayNameMapping).get();
                resource.addLiteral(VCARD.NAME, name);
            }
        }

        // job title
        if (titleMapping != null) {
            if (attributes.get(titleMapping) != null) {
                String title = (String) attributes.get(titleMapping).get();
                resource.addLiteral(VCARD.TITLE, title);
            }
        }

<<<<<<< HEAD
=======

>>>>>>> 3749458... Added service module and started support for LDAP
        // organizational unit
        if (ouMapping != null) {
            if (attributes.get(ouMapping) != null) {
                String ou = (String) attributes.get(ouMapping).get();
                resource.addLiteral(VCARD.Orgname, ou);
            }
        }

        // address
        if (postalAddressMapping != null) {
            if (attributes.get(postalAddressMapping) != null) {
                String address = (String) attributes.get(postalAddressMapping).get();

                if (postCodeMapping != null) { // post code might be part of the address
                    if (attributes.get(postCodeMapping) != null) {
                        address = address + "," + attributes.get(postCodeMapping).get();
                    }
                }

                resource.addLiteral(VCARD.ADR, address);
            }
        }

        // email
<<<<<<< HEAD
        if (attributes.get("mail") != null) {
            String mail = (String) attributes.get("mail").get();
<<<<<<< HEAD
            resource.addLiteral(FOAF.mbox, mail);
=======
            resource.addLiteral(VCARD.EMAIL, mail);
>>>>>>> 3749458... Added service module and started support for LDAP
        }

        // telephone number
        if (attributes.get("telephoneNumber") != null) {
            String tel = (String) attributes.get("telephoneNumber").get();
<<<<<<< HEAD
            resource.addProperty(FOAF.phone, handleTelephone(m, tel));
=======
        if (mailMapping != null) {
            if (attributes.get(mailMapping) != null) {
                String mail = (String) attributes.get(mailMapping).get();
                resource.addLiteral(FOAF.mbox, mail);
            }
        }

        // telephone number
        if (telephoneNumberMapping != null) {
            if (attributes.get(telephoneNumberMapping) != null) {
                String tel = (String) attributes.get(telephoneNumberMapping).get();
                resource.addProperty(FOAF.phone, handleTelephone(r.getModel(), tel));
            }
>>>>>>> c2236f4... moved configureable stuff out to properties files. update the UI
        }

    }

    private Resource handleTelephone(Model m, String telNumber) {

        // create format that can be used by phones
        String telNumberUri = "tel:" + telNumber.replace(" ", "");
        telNumberUri = telNumberUri.replace("(0)", "");

        Resource r = m.createResource(telNumberUri);
        r.addProperty(RDFS.label, telNumber);

        return r;
=======
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
>>>>>>> 3749458... Added service module and started support for LDAP
    }

    /**
     * In Bristol, policy dictates we can't advertise the UID externally.
     * I'd like a unique identifier, so hash the UID. Performance issues?
     *
     * @param uid the unique identifier of a person in the directory.
     * @return a URI to represented the person based in a hash of the URI.
     */
    private String generateUri(String uid) {

        try {

            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(uid.getBytes());
            byte hash[] = digest.digest();
            char[] hex = Hex.encodeHex(hash); // create a hex value of the hash
            return personUriPrefix + new String(hex);

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

    }


    Logger log = Logger.getLogger(org.ilrt.mca.services.ldap.BasicLdapSearch.class);

    private final Hashtable env;
    private int RESULT_LIMIT;

    private String baseDN;
    private String personUriPrefix;
    private String uidMapping;
    private String displayNameMapping;
    private String titleMapping;
    private String ouMapping;
    private String postalAddressMapping;
    private String postCodeMapping;
    private String mailMapping;
    private String telephoneNumberMapping;

}

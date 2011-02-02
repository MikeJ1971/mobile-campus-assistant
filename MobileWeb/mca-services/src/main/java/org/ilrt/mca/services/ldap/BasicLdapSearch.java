package org.ilrt.mca.services.ldap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
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
import java.util.Hashtable;

public class BasicLdapSearch {


    public BasicLdapSearch(Hashtable<Object, Object> env) {

        this.env = env;
    }


    public Model search(String filter) {

        Model m = ModelFactory.createDefaultModel();

        try {
            log.info("Connecting to LDAP server.");
            DirContext ctx = new InitialDirContext(env);

            SearchControls ctls = new SearchControls();
            ctls.setCountLimit(10);

            NamingEnumeration<SearchResult> results =
                    ctx.search("cn=Users,dc=bris,dc=ac,dc=uk", filter, ctls);

            while (results.hasMore()) {
                createContact(m, results.nextElement());
            }

        } catch (SizeLimitExceededException ex) {
            log.info("Search results are limited");
        } catch (NamingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }


        return m;
    }


    private void createContact(Model m, SearchResult result) throws NamingException,
            NoSuchAlgorithmException {

        // get the attributes from the directory
        Attributes attributes = result.getAttributes();


        // get the unique identifier for the user
        String uid = (String) attributes.get("uid").get();

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
            resource.addLiteral(FOAF.mbox, mail);
        }

        // telephone number
        if (attributes.get("telephoneNumber") != null) {
            String tel = (String) attributes.get("telephoneNumber").get();
            resource.addProperty(FOAF.phone, handleTelephone(m, tel));
        }

    }

    private Resource handleTelephone(Model m, String telNumber) {

        // create format that can be used by phones
        String telNumberUri = "tel:" + telNumber.replace(" ", "");
        telNumberUri = telNumberUri.replace("(0)", "");

        Resource r = m.createResource(telNumberUri);
        r.addProperty(RDFS.label, telNumber);

        return r;
    }


    Logger log = Logger.getLogger(org.ilrt.mca.services.ldap.BasicLdapSearch.class);

    private final Hashtable env;
}

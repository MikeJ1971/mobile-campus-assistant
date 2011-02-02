package org.ilrt.mca.services.ldap;

import com.hp.hpl.jena.rdf.model.Model;

import javax.naming.Context;
import java.util.Hashtable;


public class BasicLdapSearchTest {

    public static void main(String[] args) {

        String filter = "(|(sn=Jones)(cn=Jones))";

        Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldaps://ldap-srv.bris.ac.uk:636");
        env.put(Context.SECURITY_PROTOCOL, "SSL");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        System.out.println("Using filter ...");


        BasicLdapSearch basicLdapSearch = new BasicLdapSearch(env);
        Model m = basicLdapSearch.search(filter);

        m.write(System.out);
    }


}

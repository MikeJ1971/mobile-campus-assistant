package org.ilrt.mca.rdf;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceManager {

    public DataSourceManager() {

        try {
            Context ctx = new InitialContext();
            Context env = (Context) ctx.lookup("java:comp/env");
            dataSource = (DataSource) env.lookup("jdbc/mca");

        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    private DataSource dataSource = null;
}

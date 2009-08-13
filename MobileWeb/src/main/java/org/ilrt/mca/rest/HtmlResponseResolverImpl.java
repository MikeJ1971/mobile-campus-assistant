package org.ilrt.mca.rest;

import com.sun.jersey.api.view.Viewable;
import org.ilrt.mca.Common;
import org.ilrt.mca.dao.ItemDao;
import org.ilrt.mca.domain.Item;

import javax.ws.rs.core.Response;


public class HtmlResponseResolverImpl implements ResponseResolver {

    public HtmlResponseResolverImpl(final ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public Response reponse(String path) {

        // are we just after the root?
        if (path == null || path.equals("")) {

            Item item = itemDao.findHomePage();

            return Response.ok(new Viewable(getTemplatePath(item.getTemplate()), item)).build();
        }

        String uri = Common.MCA_STUB + path;

        Item item = itemDao.findItem(uri);

        if (item != null) {
            return Response.ok(new Viewable(getTemplatePath(item.getTemplate()), item)).build();
        }

        // default to not found
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private String getTemplatePath(String templatePath) {
        return "/" + templatePath.substring(Common.TEMPLATE_STUB.length());
    }

    final ItemDao itemDao;
}

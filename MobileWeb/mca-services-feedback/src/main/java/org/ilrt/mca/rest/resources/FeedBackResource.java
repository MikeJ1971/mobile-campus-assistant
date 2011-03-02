package org.ilrt.mca.rest.resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.resource.Singleton;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Properties;

@Singleton
@Path("/feedback/")
public class FeedBackResource {

    public FeedBackResource() throws IOException {

        mailProperties = new Properties();
        mailProperties.load(getClass().getResourceAsStream("/feedback.properties"));

        Model m = ModelFactory.createDefaultModel();
        r = m.createResource();
        r.addProperty(RDFS.label, "Feedback");
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    public Response processForm(@QueryParam("email") String email,
                                @QueryParam("comment") String comment) throws MessagingException {

        if (comment == null || comment.isEmpty()) {
            return Response.ok(new Viewable("/feedback", r)).build();
        }

        // create the session
        Session session = Session.getDefaultInstance(mailProperties);

        // construct the message
        MimeMessage message = new MimeMessage(session);
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(mailProperties.getProperty("mail.to")));

        // Note the email if one exists
        if (!(email == null || email.isEmpty())) {
            comment = "Mail from: " + email + "\n" + comment;
        }

        message.setSubject(mailProperties.getProperty("mail.subject"));
        message.setText(comment);

        // send the message
        Transport.send(message);

        // show a thank you page
        return Response.ok(new Viewable("/feedback_thanks", r)).build();
    }

    Properties mailProperties;

    Resource r;
}

package com.bytemedrive.events.control;

import com.bytemedrive.events.entity.EncryptedEvent;
import com.bytemedrive.events.entity.EncryptedSecretKey;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;


@Path("/customers")
@RegisterRestClient(configKey = "events-api")
public interface EventsRestClient {


    @GET
    @Path("/{usernameSha3}/private-keys")
    List<EncryptedSecretKey> getPrivateKeys(@PathParam("usernameSha3") String usernameSha3, @HeaderParam("Authorization") String authorizationHeader);

    @GET
    @Path("/{usernameSha3}/events")
    List<EncryptedEvent> allEvents(@PathParam("usernameSha3") String usernameSha3, @HeaderParam("Authorization") String authorizationHeader, @QueryParam("offset") Integer offset);
}

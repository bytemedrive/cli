package com.bytemedrive.download.control;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.UUID;


@Path("/files")
@RegisterRestClient(configKey = "files-api")
public interface FileRestClient {

    @GET
    @Path("{viewId}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    byte[] getFileChunk(@PathParam("viewId") UUID viewId);

}

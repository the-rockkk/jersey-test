package com.gae.jersey.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")
public class JerseyTest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response testJSON() {
		return Response.ok().entity("{ \"message\": \"Hello from Jersey!\"}").build();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response testHTML() {
		return Response.ok().entity("<html><body>Hello from Jersey!</body></html>").build();
	}

}

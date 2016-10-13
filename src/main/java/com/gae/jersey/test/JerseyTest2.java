package com.gae.jersey.test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test2")
public class JerseyTest2 {

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response testJSON(@Context final ServletContext context, @Context final HttpServletRequest request) {
		final StringBuilder sb = new StringBuilder();

		sb.append("{ \"message\": \"Hello from Jersey!\",\"ServletContext is null\":"
				+ (null == context ? "true" : "false") + ",\"HttpServletRequest Is Null\":"
				+ (null == request ? "true" : "false") + "}");
		return Response.ok().entity(sb.toString()).build();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response testHTML(@Context final ServletContext context, @Context final HttpServletRequest request) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><body><p>Hello from Jersey JSP!</p>");
		sb.append("<p>This is page #2</p>");
		sb.append("<p>ServletContext is null: ").append(null == context ? "true" : "false").append("</p>");
		sb.append("<p>HttpServletRequest is null: ").append(null == request ? "true" : "false").append("</p>");
		sb.append("</body></html>");
		return Response.ok().entity(sb.toString()).build();
	}

}

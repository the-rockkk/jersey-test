package com.gae.jersey.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger logger = Logger.getLogger(GenericExceptionMapper.class.getName());

	@Override
	public Response toResponse(Throwable ex) {
		logger.log(Level.SEVERE, ex.getMessage(), ex);

		if (ex instanceof WebApplicationException) {
			final WebApplicationException wae = (WebApplicationException) ex;
			final Response r = wae.getResponse();
			return Response.status(r.getStatus()).entity(r.getStatusInfo().getReasonPhrase()).build();
		} else {
			return Response.serverError().entity(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase()).build();
		}
	}

}

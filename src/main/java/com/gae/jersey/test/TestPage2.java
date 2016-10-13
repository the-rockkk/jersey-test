package com.gae.jersey.test;

import java.io.Serializable;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path(value = "/test2.jsp")
@Produces(value = { "text/html" })
@PermitAll
public class TestPage2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Handles the GET HTTP method
	 * 
	 * @param context
	 *            The servlet context
	 * @param request
	 *            The servlet request
	 * @return The view object
	 * @throws Exception
	 *             An issue building the page
	 */
	@GET
	public String get(@Context final ServletContext context, @Context final HttpServletRequest request)
			throws Exception {
		return "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
				+ "<html>\n" + "<head>\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n" + "</head>\n"
				+ "<body>\n" + "	Jersey <em>without</em> MVC\n" + "</body>\n" + "</html>\n";
	}

}

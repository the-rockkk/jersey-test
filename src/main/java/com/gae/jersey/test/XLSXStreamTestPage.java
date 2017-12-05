package com.gae.jersey.test;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.mvc.Viewable;

@Path(value = "/xlsxstreamtest.jsp")
@Produces(value = { "text/html" })
@PermitAll
public class XLSXStreamTestPage implements Serializable {

	private static final Logger logger = Logger.getLogger(XLSXStreamTestPage.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final String getTemplatePath() {
		logger.info("getTemplatePath() called");
		String value = null;
		final Path path = this.getClass().getAnnotation(Path.class);
		if (path != null) {
			value = StringUtils.removeEndIgnoreCase(path.value(), ".jsp");
		}
		return value;
	}

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
	public Viewable get(@Context final ServletContext context, @Context final HttpServletRequest request)
			throws Exception {
		logger.info("get() called");
		final String path = this.getTemplatePath();
		logger.info("TemplateName=" + path);
		return new Viewable(path, this);
	}

}

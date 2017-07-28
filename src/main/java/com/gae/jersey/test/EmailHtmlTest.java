/**
 * 
 */
package com.gae.jersey.test;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;

/**
 * @author Derek Altamirano
 *
 */
@Path("/email/html")
public class EmailHtmlTest {

	private static final Logger logger = Logger.getLogger(EmailPlainTextTest.class.getName());

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response testJSON() {
		final String error = testEmail();
		if (null == error) {
			return Response.ok().entity("{ \"message\": \"Email sent\"}").build();
		} else {
			return Response.serverError().entity("{ \"message\": \"" + error.replace("\"", "\\\"") + "\"}").build();
		}
	}

	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response testHTML() {
		final String error = testEmail();
		if (null == error) {
			return Response.ok().entity("<html><body>Email sent</body></html>").build();
		} else {
			return Response.ok().entity("<html><body>Error: " + error + "</body></html>").build();
		}
	}

	private String testEmail() {
		try {
			final HtmlEmail email = new HtmlEmail();

			email.setHostName("smtp.googlemail.com");
			email.setSmtpPort(465);
			email.setFrom("no-reply-devon@disney.io");
			email.addTo("di.dl-app.global.devon@disney.com");
			email.addTo("therock@cyberrock.net");
			email.setSubject("Test Mail sent @ " + new Date().toString());
			email.setHtmlMsg("<p>This is a test mail ... :-)</p>");

			email.send();
			return null;
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return StringUtils.defaultString(e.getMessage(), "An error occurred");
		}

	}

}

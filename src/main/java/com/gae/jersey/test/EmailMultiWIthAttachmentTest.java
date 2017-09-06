/**
 * 
 */
package com.gae.jersey.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

// import org.apache.commons.mail.HtmlEmail;

/**
 * @author Derek Altamirano
 *
 */
@Path("/email/attachment")
public class EmailMultiWIthAttachmentTest {

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

	// protected String testEmail() {
	// try {
	// final HtmlEmail email = new HtmlEmail();
	//
	// email.setHostName("smtp.googlemail.com");
	// email.setSmtpPort(465);
	// email.setFrom("no-reply-devon@disney.io");
	// email.addTo("di.dl-app.global.devon@disney.com");
	// email.addTo("therock@cyberrock.net");
	// email.setSubject("Test Mail sent @ " + new Date().toString());
	// email.setTextMsg("this is a fallback message in plain text");
	// email.setHtmlMsg("<p>This is a test mail ... :-)</p>");
	//
	// final byte[] data = "The quick brown fox jumped over the lazy dog".getBytes(StandardCharsets.UTF_8);
	// final DataSource ds = new ByteArrayDataSource(data, MediaType.TEXT_PLAIN.toString());
	// email.attach(ds, "foo.txt", null);
	//
	// email.send();
	// return null;
	// } catch (Exception e) {
	// logger.log(Level.SEVERE, e.getMessage(), e);
	// return StringUtils.defaultString(e.getMessage(), "An error occurred");
	// }
	//
	// }

	protected String testEmail() {
		try {
			final Properties props = new Properties();
			final Session session = Session.getDefaultInstance(props, null);

			final Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress("no-reply@jerseytest-141215.appspotmail.com"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("di.dl-app.global.devon@disney.com"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("therock@cyberrock.net"));
			msg.setSubject("Test Mail sent @ " + new Date().toString());

			final MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent("<p>This is a test mail ... :-)</p>", "text/html");

			final MimeBodyPart textPart = new MimeBodyPart();
			textPart.setContent("this is a fallback message in plain text", "text/plain");

			final Multipart mp = new MimeMultipart("alternative");
			mp.addBodyPart(textPart);
			mp.addBodyPart(htmlPart);

			final byte[] data = "The quick brown fox jumped over the lazy dog".getBytes(StandardCharsets.UTF_8);

			final MimeBodyPart attachment = new MimeBodyPart();
			InputStream attachmentDataStream = new ByteArrayInputStream(data);

			attachment.setFileName("foo.txt");
			attachment.setContent(attachmentDataStream, MediaType.TEXT_PLAIN.toString());
			mp.addBodyPart(attachment);

			msg.setContent(mp);

			Transport.send(msg);
			return null;
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return StringUtils.defaultString(e.getMessage(), "An error occurred");
		}

	}

}

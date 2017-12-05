/**
 * 
 */
package com.gae.jersey.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartProperties;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.jvnet.mimepull.Header;
import org.jvnet.mimepull.MIMEConfig;
import org.jvnet.mimepull.MIMEMessage;
import org.jvnet.mimepull.MIMEParsingException;
import org.jvnet.mimepull.MIMEPart;

/**
 * Helper class to process multipart/form-data content. Using this because I cannot get Jersey MultiPart working no
 * matter what I've tried: https://jersey.github.io/documentation/latest/media.html#multipart
 * 
 * @author Derek Altamirano
 *
 */
public class MultiPartFormParser {

	private static final Logger logger = Logger.getLogger(MultiPartFormParser.class.getName());

	private final MIMEConfig mimeConfig;

	public MultiPartFormParser() {
		mimeConfig = new MIMEConfig();
		mimeConfig.setMemoryThreshold(MultiPartProperties.BUFFER_THRESHOLD_MEMORY_ONLY);
		mimeConfig.setDir(null);
		mimeConfig.setParseEagerly(true);
	}

	private MediaType getContentType(final HttpServletRequest httpRequest) {
		final String s = httpRequest.getContentType();
		if (StringUtils.isBlank(s)) {
			return null;
		}

		final MediaType contentType = MediaType.valueOf(s);
		return contentType;
	}

	private String getBoundary(final MediaType contentType) {
		final Map<String, String> contentTypeParams = contentType.getParameters();
		final String boundary = StringUtils.replaceAll(contentTypeParams.get("boundary"), "^\"|\"$", "");
		return boundary;
	}

	/**
	 * Parse the "multipart/*" content
	 * 
	 * @param headers
	 *            the headers
	 * @param httpRequest
	 *            the request
	 * @param messageBodyWorkers
	 *            the workers
	 * @return the multipart data
	 * @throws IOException
	 */
	public MultiPart parse(final HttpHeaders headers, final HttpServletRequest httpRequest,
			final Provider<MessageBodyWorkers> messageBodyWorkers) throws IOException {
		final MediaType mediaType = getContentType(httpRequest);
		final String boundary = getBoundary(mediaType);

		try (final InputStream stream = httpRequest.getInputStream()) {
			try (final MIMEMessage mimeMessage = new MIMEMessage(stream, boundary, mimeConfig)) {
				return getMultiPart(headers, httpRequest, messageBodyWorkers, mediaType, mimeMessage);
			}
		}
	}

	public List<FileData> parseFiles(final HttpHeaders headers, final HttpServletRequest httpRequest,
			final Provider<MessageBodyWorkers> messageBodyWorkers) throws IOException {
		final MediaType mediaType = getContentType(httpRequest);
		final String boundary = getBoundary(mediaType);

		final List<FileData> inputFiles = new ArrayList<>();

		try (final InputStream stream = httpRequest.getInputStream()) {
			try (final MIMEMessage mimeMessage = new MIMEMessage(stream, boundary, mimeConfig)) {
				final MultiPart multiPart = getMultiPart(headers, httpRequest, messageBodyWorkers, mediaType,
						mimeMessage);
				if (multiPart instanceof FormDataMultiPart) {
					final FormDataMultiPart formData = (FormDataMultiPart) multiPart;
					for (final Entry<String, List<FormDataBodyPart>> kvp : formData.getFields().entrySet()) {
						final List<FormDataBodyPart> parts = kvp.getValue();
						if (null != parts) {
							for (final FormDataBodyPart part : parts) {

								final ContentDisposition contentDisp = part.getContentDisposition();
								final String fileName = contentDisp.getFileName();
								if (StringUtils.isNotBlank(fileName)) {
									final Object entity = part.getEntity();
									if (entity instanceof BodyPartEntity) {
										final BodyPartEntity body = part.getEntityAs(BodyPartEntity.class);

										try (final InputStream is = body.getInputStream()) {
											final byte[] data = IOUtils.toByteArray(is);
											final FileData file = new FileData(fileName, data);
											inputFiles.add(file);
										}
									} else {
										logger.severe("Entity for field '" + kvp.getKey() + "' is not of type "
												+ BodyPartEntity.class.getName());
									}
								} else {
									logger.warning("Form data field '" + kvp.getKey() + "' does not have a file name");
								}
							}
						}
					}
				} else {
					logger.severe("Parsed Multipart is not of type " + FormDataMultiPart.class.getName());
				}
			}
		}

		return inputFiles;
	}

	private MultiPart getMultiPart(final HttpHeaders headers, final HttpServletRequest httpRequest,
			final Provider<MessageBodyWorkers> messageBodyWorkers, final MediaType mediaType,
			final MIMEMessage mimeMessage) {
		final boolean formData = MediaTypes.typeEqual(mediaType, MediaType.MULTIPART_FORM_DATA_TYPE);
		@SuppressWarnings("resource")
		final MultiPart multiPart = formData ? new FormDataMultiPart() : new MultiPart();

		final MessageBodyWorkers workers = messageBodyWorkers.get();
		multiPart.setMessageBodyWorkers(workers);

		final MultivaluedMap<String, String> headersMap = headers.getRequestHeaders();

		final MultivaluedMap<String, String> multiPartHeaders = multiPart.getHeaders();
		for (final Map.Entry<String, List<String>> entry : headersMap.entrySet()) {
			final List<String> values = entry.getValue();

			for (final String value : values) {
				multiPartHeaders.add(entry.getKey(), value);
			}
		}

		final boolean fileNameFix;
		if (!formData) {
			multiPart.setMediaType(mediaType);
			fileNameFix = false;
		} else {
			// see if the User-Agent header corresponds to some version of MS Internet Explorer
			// if so, need to set fileNameFix to true to handle issue http://java.net/jira/browse/JERSEY-759
			final String userAgent = headersMap.getFirst(HttpHeaders.USER_AGENT);
			fileNameFix = userAgent != null && userAgent.contains(" MSIE ");
		}

		for (final MIMEPart mimePart : getMimeParts(mimeMessage)) {
			final BodyPart bodyPart = formData ? new FormDataBodyPart(fileNameFix) : new BodyPart();

			// Configure providers.
			bodyPart.setMessageBodyWorkers(workers);

			// Copy headers.
			for (final Header header : mimePart.getAllHeaders()) {
				bodyPart.getHeaders().add(header.getName(), header.getValue());
			}

			try {
				final String contentType = bodyPart.getHeaders().getFirst("Content-Type");
				if (contentType != null) {
					bodyPart.setMediaType(MediaType.valueOf(contentType));
				}

				bodyPart.getContentDisposition();
			} catch (final IllegalArgumentException ex) {
				throw new BadRequestException(ex);
			}

			// Copy data into a BodyPartEntity structure.
			bodyPart.setEntity(new BodyPartEntity(mimePart));

			// Add this BodyPart to our MultiPart.
			multiPart.getBodyParts().add(bodyPart);
		}

		return multiPart;
	}

	/**
	 * Get a list of mime part attachments from given mime message. If an exception occurs during parsing the message
	 * the parsed mime parts are closed (any temporary files are deleted).
	 *
	 * @param message
	 *            mime message to get mime parts from.
	 * @return list of mime part attachments.
	 */
	private List<MIMEPart> getMimeParts(final MIMEMessage message) {
		try {
			return message.getAttachments();
		} catch (final MIMEParsingException obtainPartsError) {
			logger.log(Level.FINE, LocalizationMessages.PARSING_ERROR(), obtainPartsError);

			message.close();

			// Re-throw the exception.
			throw obtainPartsError;
		}
	}

}

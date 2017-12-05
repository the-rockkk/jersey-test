/**
 * 
 */
package com.gae.jersey.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.JSONP;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.xlsx.StreamingReader;

/**
 * @author Derek Altamirano
 *
 */
@Path("/upload/xlsx")
@PermitAll
public class ProcessXLSX {

	private static final Logger logger = Logger.getLogger(ProcessXLSX.class.getName());

	protected static final ObjectMapper mapper = new ObjectMapper();

	@Context
	protected ServletContext context;

	@Context
	protected HttpHeaders headers;

	@Context
	protected HttpServletRequest httpRequest;

	/**
	 * Injectable helper to look up appropriate {@link MessageBodyReader}s for our body parts.
	 */
	@Inject
	private Provider<MessageBodyWorkers> messageBodyWorkers;

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@JSONP(callback = "callback", queryParam = "callback")
	public String post() {
		try {
			final MultiPartFormParser parser = new MultiPartFormParser();
			final List<FileData> inputFiles = parser.parseFiles(headers, httpRequest, messageBodyWorkers);
			if (inputFiles.size() > 0) {
				final FileData inputFile = inputFiles.get(0);
				if (".xlsx".equalsIgnoreCase(inputFile.extension())) {
					int row = 0;
					int cell = 0;
					try (final InputStream input = new ByteArrayInputStream(inputFile.getData())) {
						final Workbook workbook = StreamingReader.builder() //
								.rowCacheSize(100) // number of rows to keep in memory (defaults to 10)
								.bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to
													// 1024)
								.open(input); // InputStream or File for XLSX file (required)

						for (Sheet sheet : workbook) {
							System.out.println(sheet.getSheetName());
							for (Row r : sheet) {
								++row;
								for (Cell c : r) {
									++cell;
									System.out.println(c.getStringCellValue());
								}
							}
						}

					}

					final String msg = String.format("XLSX was successfully parsed. Found %d rows and %d cells.", row,
							cell);
					logger.info(msg);
					return msg;
				} else {
					return "file is not an xlsx";
				}
			} else {
				return "no files uploaded";
			}
		} catch (Exception e) {
			final String msg = ExceptionUtils.getMessage(e);
			logger.log(Level.SEVERE, msg, e);
			ExceptionUtils.rethrow(e);
			return msg;
		}
	}

}

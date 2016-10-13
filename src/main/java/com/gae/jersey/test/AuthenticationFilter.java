package com.gae.jersey.test;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

@Provider
@Priority(Priorities.AUTHENTICATION)
@PermitAll
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

	// @javax.inject.Inject
	private HttpSession session;

	//@javax.ws.rs.core.Context
	private HttpServletRequest request;

	@javax.ws.rs.core.Context
	private ServletContext context;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.info(String.format("AuthenticationFilter: HttpSession=%s", (null == session ? "[null]" : "true")));
		logger.info(
				String.format("AuthenticationFilter: HttpServletRequest=%s", (null == request ? "[null]" : "true")));
		logger.info(String.format("AuthenticationFilter: ServletContext=%s", (null == context ? "[null]" : "true")));
		final String sessionId = (session != null ? session.getId()
				: (null != request ? (null != request.getSession() ? request.getSession().getId() : null) : null));
		logger.info(String.format("AuthenticationFilter: SessionId=%s", sessionId));

		for (final Entry<String, Cookie> kvp : requestContext.getCookies().entrySet()) {
			logger.info(String.format("Cookie: Name=%s, Value=%s", kvp.getKey(), kvp.getValue()));
		}

		final UriInfo uri = requestContext.getUriInfo();
		final String uriPath = uri.getPath();
		if (StringUtils.startsWith(uriPath, "secured")) {
			final String authHeader = requestContext.getHeaderString("Authorization");
			if (StringUtils.isBlank(authHeader)) {
				requestContext.abortWith(
						Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic realm=\"\"").build());
			}
		}
	}

}

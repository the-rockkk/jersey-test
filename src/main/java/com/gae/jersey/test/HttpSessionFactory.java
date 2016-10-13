package com.gae.jersey.test;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.glassfish.hk2.api.Factory;

/**
 * Custom injection provider to extract the session from a request
 * 
 * @author Derek Altamirano
 *
 */
public class HttpSessionFactory implements Factory<HttpSession> {

	private static final Logger logger = Logger.getLogger(HttpSessionFactory.class.getName());

	private final HttpServletRequest request;

	/**
	 * Constructs a new {@link HttpSession} factory
	 * 
	 * @param request
	 *            The http request
	 */
	@Inject
	public HttpSessionFactory(final HttpServletRequest request) {
		logger.info("CTOR Called");
		this.request = request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.glassfish.hk2.api.Factory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(HttpSession instance) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.glassfish.hk2.api.Factory#provide()
	 */
	@Override
	public HttpSession provide() {
		logger.info("provide Called");
		return request.getSession();
	}

}

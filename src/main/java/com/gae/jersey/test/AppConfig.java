package com.gae.jersey.test;

import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class AppConfig extends ResourceConfig {

	private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
	
	public AppConfig() {
		logger.config("Starting AppConfig");
		this.register(JspMvcFeature.class);
		this.register(RolesAllowedDynamicFeature.class);
		this.register(JacksonJsonProvider.class);
		this.register(GenericExceptionMapper.class);
		this.register(new AbstractBinder() {

			@Override
			protected void configure() {
				bindFactory(HttpSessionFactory.class).to(HttpSession.class).proxy(true).proxyForSameScope(false)
						.in(RequestScoped.class);
			}

		});
		logger.config("AppConfig Complete");
	}
}

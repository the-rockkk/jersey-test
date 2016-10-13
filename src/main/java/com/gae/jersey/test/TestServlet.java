/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.gae.jersey.test;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class TestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			response.setStatus(200);
			response.setContentType("text/plain");
			response.getWriter().println("Hello from a servlet!");
			response.getWriter().println();
		} catch (WebApplicationException e) {
			final Response r = e.getResponse();
			response.sendError(r.getStatus(), r.getStatusInfo().getReasonPhrase());
			e.printStackTrace();
		} catch (Exception e) {
			response.sendError(500, "Error: " + e.getMessage());
			e.printStackTrace(response.getWriter());
		}

	}
}

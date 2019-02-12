package com.frt.fhir.filter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/***
 * /*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/**
 * Example filter that dumps interesting state information about a request to
 * the associated servlet context log file, before allowing the servlet to
 * process the request in the usual way. This can be installed as needed to
 * assist in debugging problems.
 *
 * @author Craig McClanahan
 * @version $Revision: 500674 $ $Date: 2007-01-28 00:15:00 +0100 (dim., 28 janv.
 *          2007) $
 */

public class FHIRRequestDumperFilter implements Filter {
	private static final String HTML_DIV_OPEN = "<div>";
	private static final String HTML_DIV_CLOSE = "</div>";
	private static final String HTML_PRELOG = "	<!DOCTYPE html>\r\n" + 
			"	<html lang=\"en-US\">\r\n" + 
			"	<head>\r\n" + 
			"	<title>FHIR Connecthon20 Tests</title>\r\n" + 
			"	<meta charset=\"utf-8\">\r\n" + "<body>\r\n" + 
					"";
	private static final String HTML_POSTLOG = "</body>\r\n" + 
			"</html>\r\n" + 
			""; 

	// ----------------------------------------------------- Instance Variables

	/**
	 * The filter configuration object we are associated with. If this value is
	 * null, this filter instance is not currently configured.
	 */
	private FilterConfig filterConfig = null;

	// --------------------------------------------------------- Public Methods

	/**
	 * Take this filter out of service.
	 */
	public void destroy() {

		this.filterConfig = null;

	}

	/**
	 * Time the processing that is performed by all subsequent filters in the
	 * current filter stack, including the ultimately invoked servlet.
	 *
	 * @param request The servlet request we are processing
	 * @param result  The servlet response we are creating
	 * @param chain   The filter chain we are processing
	 *
	 * @exception IOException      if an input/output error occurs
	 * @exception ServletException if a servlet error occurs
	 */
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (filterConfig == null)
			return;
		
		FHIRRequestWrapper request = new FHIRRequestWrapper((HttpServletRequest)req); 
		// Render the generic servlet request properties
		long suffix = System.currentTimeMillis();
		File dumpDir = new File(new File(System.getProperty("java.io.tmpdir")), "FHIR_TEST_DUMP_"+System.currentTimeMillis());
		boolean b = dumpDir.mkdirs();
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		outputDiv(writer, "Request Received at " + (new Timestamp(System.currentTimeMillis())));
		outputDiv(writer, " characterEncoding=" + request.getCharacterEncoding());
		outputDiv(writer, "     contentLength=" + request.getContentLength());
		outputDiv(writer, "       contentType=" + request.getContentType());
		outputDiv(writer, "            locale=" + request.getLocale());
		Enumeration locales = request.getLocales();
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		while (locales.hasMoreElements()) {
			Locale locale = (Locale) locales.nextElement();
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(locale.toString());
		}
		outputDiv(writer, "           locales=" + sb.toString());
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			sb = new StringBuilder();
			String name = (String) names.nextElement();
			sb.append("         parameter=" + name + "=");
			String values[] = request.getParameterValues(name);
			for (int i = 0; i < values.length; i++) {
				if (i > 0)
					sb.append(", ");
				sb.append(values[i]);
			}
			outputDiv(writer, sb.toString());
		}
		outputDiv(writer, "          protocol=" + request.getProtocol());
		outputDiv(writer, "        remoteAddr=" + request.getRemoteAddr());
		outputDiv(writer, "        remoteHost=" + request.getRemoteHost());
		outputDiv(writer, "            scheme=" + request.getScheme());
		outputDiv(writer, "        serverName=" + request.getServerName());
		outputDiv(writer, "        serverPort=" + request.getServerPort());
		outputDiv(writer, "          isSecure=" + request.isSecure());

		// Render the HTTP servlet request properties
		if (request instanceof HttpServletRequestWrapper) {
			outputDiv(writer, "HttpServletRequest---------------------------------------------");
			HttpServletRequest hrequest = (HttpServletRequest) request;
			outputDiv(writer, "       contextPath=" + hrequest.getContextPath());
			Cookie cookies[] = hrequest.getCookies();
			if (cookies == null)
				cookies = new Cookie[0];
			for (int i = 0; i < cookies.length; i++) {
				outputDiv(writer, "            cookie=" + cookies[i].getName() + "=" + cookies[i].getValue());
			}
			names = hrequest.getHeaderNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				String value = hrequest.getHeader(name);
				outputDiv(writer, "            header=" + name + "=" + value);
			}
			outputDiv(writer, "            method=" + hrequest.getMethod());
			outputDiv(writer, "          pathInfo=" + hrequest.getPathInfo());
			outputDiv(writer, "       queryString=" + hrequest.getQueryString());
			outputDiv(writer, "        remoteUser=" + hrequest.getRemoteUser());
			outputDiv(writer, "requestedSessionId=" + hrequest.getRequestedSessionId());
			outputDiv(writer, "        requestURI=" + hrequest.getRequestURI());
			outputDiv(writer, "       servletPath=" + hrequest.getServletPath());
			outputDiv(writer, "REQUEST BODY ---------------------------------------------");
			if (hrequest.getInputStream() != null) {
				try {
					Thread.currentThread().sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File bodyFile = new File(dumpDir, "request_body_" + suffix + ".json");
				PrintWriter bodywriter = new PrintWriter(bodyFile);
				BufferedInputStream bis = new BufferedInputStream(hrequest.getInputStream());
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				int result = bis.read();
				while (result != -1) {
					buf.write((byte) result);
					result = bis.read();
				}
				// StandardCharsets.UTF_8.name() > JDK 7
				String body = buf.toString("UTF-8");
				bodywriter.println(body);
				bodywriter.close();
				outputDiv(writer, "<a href=\"" + bodyFile.getName() + "\">REQUEST BODY (json)</a><br>");
			}
		}
		outputDiv(writer, "END REQUEST DUMP =============================================");
		// Log the resulting string
		writer.flush();
		filterConfig.getServletContext().log(sw.getBuffer().toString());
		File headersFile = new File(dumpDir, "request_dump_" + suffix + ".html");
		PrintWriter requestwriter = new PrintWriter(headersFile);
		requestwriter.println(HTML_PRELOG);
		requestwriter.println(sw.getBuffer().toString());
		requestwriter.println(HTML_POSTLOG);
		requestwriter.close();
		// Pass control on to the next filter
		chain.doFilter(request, response);
	}

	private void outputDiv(PrintWriter writer, String line) {
		writer.print(HTML_DIV_OPEN);
		writer.println(line);
		writer.print(HTML_DIV_CLOSE);
	}

	/**
	 * Place this filter into service.
	 *
	 * @param filterConfig The filter configuration object
	 */
	public void init(FilterConfig filterConfig) throws ServletException {

		this.filterConfig = filterConfig;

	}

	/**
	 * Return a String representation of this object.
	 */
	public String toString() {

		if (filterConfig == null)
			return ("FHIRRequestDumperFilter()");
		StringBuffer sb = new StringBuffer("FHIRRequestDumperFilter(");
		sb.append(filterConfig);
		sb.append(")");
		return (sb.toString());

	}

}

/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druĹľbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAĂ‡Ă�O, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package si.stecce.societies.crowdtasking.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import si.stecce.societies.crowdtasking.api.RESTful.impl.UsersAPI;
import si.stecce.societies.crowdtasking.model.AuthenticatedUser;
import si.stecce.societies.crowdtasking.model.CTUser;

/**
 * Describe your class here...
 *
 * @author Simon JureĹˇa
 *
 */
public class SessionFilter implements Filter {
    private static final Logger log = Logger.getLogger(SessionFilter.class.getName());
	private List<String> urlList;
	private String loginUrl, logoutUrl, registerUrl;
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();
		String url = request.getRequestURI();
		String parameters = request.getQueryString();
		if (parameters != null) {
			url+="?"+parameters;
		}
		if (!url.startsWith("/_ah") && 
				!url.equalsIgnoreCase("/admin") &&
				!url.equalsIgnoreCase("/rest/noaccess") &&
				!url.startsWith(loginUrl) &&
				!url.equalsIgnoreCase(logoutUrl) &&
				!url.startsWith("/apk") &&
				!urlList.contains(url)) { // restricted access
			
			// logged in?
			if (session.getAttribute("loggedIn") == null) {
                log.info("loggedIn attribute is not yet set");
				AuthenticatedUser authenticatedUser = UsersAPI.getAuthenticatedUser(session);
				if (authenticatedUser != null) {
					session.setAttribute("loggedIn", "true");
					session.setAttribute("authenticatedUser", authenticatedUser);
					CTUser user = UsersAPI.getUserByFederatedId(authenticatedUser);
					if (user != null) {
						user.setLastLogin(new Date());
						if (user.getId() != null) {
							session.setAttribute("CTUserId", user.getId());
						}
						UsersAPI.saveUser(user);
					}
				}
				else {
                    log.info("user is not authenticated");
					if (!url.startsWith("/rest")) {
						response.sendRedirect(loginUrl+"?continue="+url);
					}
					else {
						response.sendRedirect("/rest/noaccess");
					}
					return;
				}
			}
			// registered?
			if (session.getAttribute("CTUserId") == null && !registerUrl.equalsIgnoreCase(url) && !url.startsWith("/rest/user")) {
				response.sendRedirect(registerUrl);
				return;
			}
		}
		if (url.equalsIgnoreCase("/apk")) {
			response.sendRedirect("/apk/index.html");
			return;
		}
        chain.doFilter(req, res);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		loginUrl = config.getInitParameter("loginUrl");
		logoutUrl = config.getInitParameter("logoutUrl");
		registerUrl = config.getInitParameter("registerUrl");
		String urls = config.getInitParameter("unrestricted");
		StringTokenizer token = new StringTokenizer(urls, ",");
		urlList = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			urlList.add(token.nextToken());
		}
	}

}

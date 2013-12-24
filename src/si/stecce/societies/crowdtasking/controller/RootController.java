/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp.,
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
package si.stecce.societies.crowdtasking.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import si.stecce.societies.crowdtasking.Util;

/**
 * Describe your class here...
 *
 * @author Simon Jureša
 *
 */
public class RootController extends HttpServlet {
	private static final long serialVersionUID = 1865512175295488417L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

    	String template = null;
		String uri = request.getRequestURI();
		if ("/profile".equalsIgnoreCase(uri)) {
			template = "WEB-INF/html/profile.html";
		}
		if ("/register".equalsIgnoreCase(uri)) {
			template = "WEB-INF/html/profile.html";
		}
		if ("/menu".equalsIgnoreCase(uri) || "/mobile".equalsIgnoreCase(uri)) { // zaradi androidne aplikacije dokler se ne apdejta 
			template = "WEB-INF/html/menu.html";
		}
		if ("/settings".equalsIgnoreCase(uri)) {
			template = "WEB-INF/html/settings.html";
		}
		if ("/newsfeed".equalsIgnoreCase(uri)) {
			template = "WEB-INF/html/feed.html";
		}
		if ("/remoteControl".equalsIgnoreCase(uri)) {
			template = "WEB-INF/html/remoteControl.html";
		}
	    if (template == null) {
	    	template = request.getRequestURI();
	    	if (template.startsWith("/")) {
	    		template = template.substring(1);
	    	}
	    }
        System.out.println("template:"+template);
        template = Util.readFile(template);
        if (template == null) {
            response.sendRedirect("/menu");
            return;
        }
        String head = Util.readFile("WEB-INF/html/include/head.html");
        if (head == null) {
            System.out.println("head == null");
        }
        template = template.replaceAll("\\{\\{ head \\}\\}", head);

	    response.setContentType("text/html");
	    response.getWriter().write(template);
	}
}

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
package si.setcce.societies.android.rest;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Describe your class here...
 * 
 * @author Simon Jureša
 * 
 */
public class RestTask extends AsyncTask<HttpUriRequest, Void, String[]> {

	public static final String HTTP_RESPONSE = "httpResponse";

	private Context context;
	private HttpClient client;
	private String action;
	private String cookie;
    private CookieStore cookieStore = new BasicCookieStore();
    private String domain;

	public RestTask(Context context, String action, String cookie, String domain) {
		this.context = context;
		this.action = action;
		this.cookie = cookie;
		this.client = new DefaultHttpClient();
        this.domain = domain;
	}

	@Override
	protected String[] doInBackground(HttpUriRequest... params) {
        String[] response = new String[2];
		try {
			HttpUriRequest request = params[0];
			Log.i("RestTask url: ", request.getURI().toString());
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			if (cookie != null && !cookie.equals("")) {
				String[] cookies = cookie.split(";");
				for (int i = 0; i < cookies.length; i++) {
					String[] nvp = cookies[i].split("=");
                    if (nvp.length == 2) {
                        BasicClientCookie c = new BasicClientCookie(nvp[0], nvp[1]);
                        // c.setVersion(1);
                        c.setDomain(domain);
                        cookieStore.addCookie(c);
                    }
				}
			}

			HttpResponse serverResponse = client
					.execute(request, localContext);

            response[0] = serverResponse.getStatusLine().toString();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            serverResponse.getEntity().writeTo(out);
            out.close();
            response[1] = out.toString();
            System.out.println(response);
            return response;
		} catch (Exception e) {
			e.printStackTrace();
            response[0] = e.getMessage();
			Log.e("SCT RestTask exception message: ", e.getMessage());
			return response;
		}
	}

	@Override
	protected void onPostExecute(String[] result) {
		Intent intent = new Intent(action);
		intent.putExtra(HTTP_RESPONSE, result);
//		intent.putExtra(HTTP_RESPONSE, result[1]);
        if (action.equalsIgnoreCase("si.setcce.societies.android.rest.LOGIN_USER")) {
            for (Cookie kuki:cookieStore.getCookies()) {
                if (kuki.getDomain().equalsIgnoreCase(domain)) {
                    intent.putExtra("cookie", kuki.toString());
                }
            }
        }
		context.sendBroadcast(intent);
	}

}

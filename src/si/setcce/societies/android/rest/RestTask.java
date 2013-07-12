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
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
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

/**
 * Describe your class here...
 * 
 * @author Simon Jureša
 * 
 */
public class RestTask extends AsyncTask<HttpUriRequest, Void, String> {

	public static final String HTTP_RESPONSE = "httpResponse";

	private Context context;
	private HttpClient client;
	private String action;
	private String cookie;

	public RestTask(Context context, String action, String cookie) {
		this.context = context;
		this.action = action;
		this.cookie = cookie;
		this.client = new DefaultHttpClient();
	}

	@Override
	protected String doInBackground(HttpUriRequest... params) {
		try {
			HttpUriRequest request = params[0];
			Log.i("SCT RestTask(70)", request.getURI().toString());
			HttpContext localContext = new BasicHttpContext();
			CookieStore cookieStore = new BasicCookieStore();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			if (!cookie.equals("")) {
				String[] cookies = cookie.split(";");
				for (int i = 0; i < cookies.length; i++) {
					String[] nvp = cookies[i].split("=");
					BasicClientCookie c = new BasicClientCookie(nvp[0], nvp[1]);
					// c.setVersion(1);
					c.setDomain("crowdtasking.appspot.com");
					cookieStore.addCookie(c);
				}
			}

			HttpResponse serverResponse = client
					.execute(request, localContext);

			BasicResponseHandler handler = new BasicResponseHandler();
			String response = handler.handleResponse(serverResponse);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			String msg = e.getMessage();
			Log.e("SCT RestTask(94)", msg);
			return msg;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		Intent intent = new Intent(action);
		intent.putExtra(HTTP_RESPONSE, result);
		// Broadcast the completion
		context.sendBroadcast(intent);
	}

}

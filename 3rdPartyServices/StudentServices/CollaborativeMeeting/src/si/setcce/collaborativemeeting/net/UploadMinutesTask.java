/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
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
package si.setcce.collaborativemeeting.net;

import java.net.URI;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Parameters:<br>
 *   [0] upload URL<br>
 *   [1] XML file contents<br>
 *   [2] Crowd Tasking server URL for meeting minutes<br>
 *   [3] meeting ID<br>
 *   [4] download URL<br>
 */ 
public class UploadMinutesTask extends AsyncTask<String, Void, String[]> {
	
	private static final String TAG = UploadMinutesTask.class.getSimpleName();
	
	private Context context;
	
	public UploadMinutesTask(Context context) {
		this.context = context;
	}

	@Override
	protected String[] doInBackground(String... params) {

		Log.i(TAG, "doInBackground");
		
		try {
			URI uri = new URI(params[0]);
			Net net = new Net(uri);
			if (net.put(params[1])) {
				return new String[] {params[2], params[3], params[4]};
			}
			else {
				Log.w(TAG, "Uploading minutes failed");
				return null;
			}
		} catch (Exception e) {
			Log.w(TAG, "doInBackground", e);
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(String[] result) {
		Log.i(TAG, "onPostExecute");
		
		if (result == null) {
			Log.w(TAG, "onPostExecute: result is null, uploading minutes failed");
			Toast.makeText(context, "Could not upload minutes to remote server", Toast.LENGTH_LONG).show();
			return;
		}
		
		Log.d(TAG, "Download URL = " + result[0]);
		Log.d(TAG, "Meeting ID =  " + result[1]);
		Log.d(TAG, "Download URL = " + result[2]);
		
		new PassDownloadUriTask(context).execute(result);
	}
}

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
package org.societies.thirdpartyservices.askfree.tools;

import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.contentproviders.CSSContentProvider;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;


/**
 * Describe your class here...
 *
 * @author Jiannis
 *
 */
public class SocietiesManager{

	protected static final String LOG_TAG = SocietiesManager.class.getSimpleName();

	Context applicationContext;

	public SocietiesManager(Context applicationContext){
		this.applicationContext = applicationContext;
	}

	public String getCssId() {
		Cursor cursor = this.applicationContext.getContentResolver().query(CSSContentProvider.CssRecord.CONTENT_URI, null, null, null, null);
		cursor.moveToFirst();
		String cssId = cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY));
		Log.d(LOG_TAG, "Get CSSId: " + cssId);

		int end = cssId.indexOf('.');
		String username = cssId.substring(0, end);
		return username;
	}

	public String getFullCssId() {
		Cursor cursor = this.applicationContext.getContentResolver().query(CSSContentProvider.CssRecord.CONTENT_URI, null, null, null, null);
		cursor.moveToFirst();
		String cssId = cursor.getString(cursor.getColumnIndex(CSSContentProvider.CssRecord.CSS_RECORD_CSS_IDENTITY));
		Log.d(LOG_TAG, "Get full CSSId: " + cssId);
		return cssId;
	}

	public void test(){

	}
}

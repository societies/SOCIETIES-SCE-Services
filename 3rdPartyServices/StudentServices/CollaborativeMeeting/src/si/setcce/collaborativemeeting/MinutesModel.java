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
package si.setcce.collaborativemeeting;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * Created by Clive on 24.9.2013.
 */
public class MinutesModel implements Serializable {

	private static final long serialVersionUID = -1988642601733856730L;

	private static final String TAG = MinutesModel.class.getSimpleName();

	private String userId;
	private String userName;
	private String minute;
	private Date date;
	private boolean important = false;

	/**
	 * Constructor
	 */
	public MinutesModel() {
	}

	/**
	 * Constructor
	 */
	public MinutesModel(String userId, String userName, String minute, Date date, boolean important) {
		this.userId = userId;
		this.userName = userName;
		this.minute = minute;
		this.date = date;
		this.important = important;
	}
	
	public String getUserName()
	{
		return this.userName;
	}

	public void setUserName(String userName) {
		Log.d(TAG, "User name set to " + userName);
		this.userName = userName;
	}

	/**
	 * 
	 * @return User ID for Crowd Tasking, not for this app
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 
	 * @param userId User ID for Crowd Tasking, not for this app
	 */
	public void setUserId(String userId) {
		Log.d(TAG, "User ID set to " + userId);
		this.userId = userId;
	}

	public String getMinute()
	{
		return this.minute;
	}

	public void setMinute(String minute)
	{
		this.minute = minute;
	}

	public Date getDate() {
		return date;
	}

	public String getDateString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}
	
	@Override
	public String toString() {
		return date + " important=" + important + "; " + userName + ": " + minute;
	}
}

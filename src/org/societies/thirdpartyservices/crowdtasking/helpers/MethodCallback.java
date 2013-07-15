package org.societies.thirdpartyservices.crowdtasking.helpers;

import org.societies.android.api.comms.IMethodCallback;

public class MethodCallback implements IMethodCallback {
	/*
	 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
	 */
	@Override
	public void returnException(String exception) {
		System.out.println("returnException");
	}

	/*
	 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
	 */
	@Override
	public void returnAction(String result) {
		System.out.println("returnAction");
	}

	/*
	 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
	 */
	@Override
	public void returnAction(boolean resultFlag) {
		System.out.println("returnAction");
	}
}

package org.societies.ext3p.nzone.client;


import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

public class NZoneCisDirCallback {


	
	public CountDownLatch cisDirCallbackSignal;
	public boolean bResponseReceived ;
	public List<CisAdvertisementRecord> adList;

	public NZoneCisDirCallback() {
		super();
		cisDirCallbackSignal = new CountDownLatch(1);
		adList = null;
	}






	// callback
	ICisDirectoryCallback iCisDirectoryCallback = new ICisDirectoryCallback() {

		@Override
		public void getResult(List<CisAdvertisementRecord> list) {
			if (list != null)
				adList = list;
			cisDirCallbackSignal.countDown();
		};
	};

}
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

package org.societies.thirdPartyServices.disasterManagement.disasterDataCollector.dmt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Tools {
	
	private static int DEFAULT_BUFFER_SIZE = 4096;
	
	/** Takes an inputStream and a byte array <code>bytes</code> and writes the next <code>bytes.length</code>
	 * bytes into the array. Blocks (i.e. runs in a loop) if not enough bytes are received.
	 * @param preStream
	 * @param bytes
	 * @throws IOException
	 */
	public static void read2ByteArray(InputStream preStream, byte[] bytes) throws IOException {
		int copiedBytes = 0;
		int totalBytes = bytes.length;
		
		copiedBytes = preStream.read(bytes);
		while (copiedBytes < totalBytes){
			copiedBytes = copiedBytes + preStream.read(bytes, copiedBytes, totalBytes-copiedBytes);
		}
	}
	
	//The following two methods are originating from de.dlr.kn.tools.ByteTools
	/** Converts a given integer into a byte array of length 4
	 * @param integer
	 * @return The integer as byte array of length 4
	 */
	public static byte[] intToByte(int integer){
		
		ByteBuffer buffer = ByteBuffer.allocate(4);
		
		buffer.putInt(integer);
		
		// fill with zeros until length is 4
		buffer.flip();
		while (buffer.capacity() != 4)
			buffer.put((byte)0x00);
		buffer.flip();
		
		return buffer.array();
	}
	
	/** Converts a byte array into an integer. The array length has to be 4, if shorter it will
	 *  be filled with zeros (at MSB), if it is greater than 4 it will be cutted (and just the
	 *  LSBs will be used). 
	 * @param byteArray
	 * @return The byte array as int
	 */
	public static int byteArrayToInt (byte[] byteArray){
		byte[] tempArray = {0,0,0,0};
		if (byteArray.length <= 4){
			for (int i = 0; i < byteArray.length; i++){
				tempArray[3-i] = byteArray[3-i];
			}
		}
		else{
			for (int i = 0; i < 4; i++)
				tempArray[i] = byteArray[i];
		}
		
		ByteBuffer buffer = ByteBuffer.wrap(tempArray);
		return buffer.getInt();
	}
	
	public static void copyAmountOfBytes(InputStream input, OutputStream output, int totalBytes) throws IOException {
		
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		
		int copiedBytes = 0;
		int n = 0;
		
		while (((totalBytes - copiedBytes) > DEFAULT_BUFFER_SIZE) && -1 != (n = input.read(buffer))) {
		    output.write(buffer, 0, n);
		    copiedBytes += n;
		}
		
		byte[] decreasingBuffer = new byte[(totalBytes - copiedBytes)];
		while ((totalBytes > copiedBytes) && -1 != (n = input.read(decreasingBuffer))){
			output.write(decreasingBuffer, 0, n);
			copiedBytes += n;
			decreasingBuffer = new byte[(totalBytes - copiedBytes)];
		}
	}
	
	public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
	
	public static long copyLarge(InputStream input, OutputStream output)
	    throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
		    output.write(buffer, 0, n);
		    count += n;
			}
		return count;
	}

}

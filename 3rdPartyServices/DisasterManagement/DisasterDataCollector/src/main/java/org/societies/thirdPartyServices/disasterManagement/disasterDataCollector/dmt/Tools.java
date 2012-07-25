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

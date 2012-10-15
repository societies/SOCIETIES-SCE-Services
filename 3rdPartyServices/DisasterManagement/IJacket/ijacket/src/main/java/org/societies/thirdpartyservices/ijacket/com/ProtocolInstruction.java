/*
* Copyright 2012 Anders Eie, Henrik Goldsack, Johan Jansen, Asbj�rn 
* Lucassen, Emanuele Di Santo, Jonas Svarvaa, Bj�rnar H�kenstad Wold
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/
package org.societies.thirdpartyservices.ijacket.com;

import org.societies.thirdpartyservices.ijacket.com.Protocol.OpCode;


/**
 * Used internally by the Protocol standard for each communication Packet
 */
public class ProtocolInstruction {
	private static final byte START_BYTE = (byte)0xFF;
	
	private OpCode opcode;
	private byte flag;
	private byte[] content;
	
	private boolean response;
	
	public ProtocolInstruction(OpCode opcode, byte flag, byte[] content){
		this.opcode = opcode;
		this.flag = flag;
		this.content = content;
		response = false;
	}
	
	public ProtocolInstruction(OpCode opcode, byte flag, byte[] content, boolean response){
		this.opcode = opcode;
		this.flag = flag;
		this.content = content;
		this.response = response;
	}
	
	public byte[] getInstructionBytes(){
		int size = content.length;
		
		byte[] instruction = new byte[size + 5];
		
		//Packet header
		instruction[0] = START_BYTE;
		instruction[1] = (byte) (size >> 8);
		instruction[2] = (byte) (size & 0xFF);
		instruction[3] = opcode.value;
		instruction[4] = flag;
		
		//Payload
		System.arraycopy(content, 0, instruction, 5, content.length);
		
		return instruction;
	}
	
	public OpCode getOpcode(){
		return opcode;
	}
	
	public byte getFlag(){
		return flag;
	}
	
	public byte[] getContent(){
		return content;
	}
	
	public boolean hasResponse(){
		return response;
	}
}

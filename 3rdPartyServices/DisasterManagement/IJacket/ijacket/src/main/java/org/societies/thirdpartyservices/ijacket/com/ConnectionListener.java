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

public interface ConnectionListener {
	/**
	 * Event that is fired whenever a connection was successfully established
	 * @param connection The connection this event was fired from
	 */
	void onConnect(BluetoothConnection connection);
	
	/**
	 * Event that is fired whenever a new connection is being established
	 * @param connection The connection this event was fired from
	 */
	void onConnecting(BluetoothConnection connection);
	
	/**
	 * Event that is fired whenever a connection was closed
	 * @param connection The connection this event was fired from
	 */
	void onDisconnect(BluetoothConnection connection);
}

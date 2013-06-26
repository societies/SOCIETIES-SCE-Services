using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using agsXMPP;
using agsXMPP.protocol.extensions.pubsub;

namespace CommsFrwk
{
    public class CommsManager
    {
        private readonly XmppClientConnection conn;
        private readonly PubSubManager pubSubManager;

        public CommsManager(String hostUrl, String username, String password)
        {
            conn = new XmppClientConnection();
            conn.Server = hostUrl;
            conn.Username = username;
            conn.Password = password;

            pubSubManager = new PubSubManager(conn);
        }


        //       Node ensureNodeExists(String nodeName) throws XMPPException;

        //<T> void registerListener(String nodeName, String myJID, Class<T> expectedClass, IncomingNodeMessageListener<T> eventHandler) throws XMPPException;

        //<T> void unregisterListener(String nodeName, IncomingNodeMessageListener<T> eventHandler) throws XMPPException;

        //<T> void sendEvent(String nodeName, T payload) throws XMPPException;

        //<T> void sendEvent(String messageId, String nodeName, T payload) throws XMPPException;

    }
}

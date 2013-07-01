using System;
using jabber;
using jabber.connection;

namespace CommsFrwk
{
    public class CommsManager
    {
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(typeof(CommsManager));

        private readonly PubSubManager pubSubManager;

        private readonly String hostUrl;
        private readonly String username;

        public CommsManager(String hostUrl, String username, String password)
        {
            this.hostUrl = hostUrl;
            this.username = username;

            if (log.IsDebugEnabled)
                log.Debug("Logging in to " + hostUrl);

            pubSubManager = new PubSubManager();
        }


        //       Node ensureNodeExists(String nodeName) throws XMPPException;

        //<T> void registerListener(String nodeName, String myJID, Class<T> expectedClass, IncomingNodeMessageListener<T> eventHandler) throws XMPPException;
        public void RegisterListener(String nodeName)
        {
            log.Debug("Registering listener for " + nodeName);

            JID jid = new JID(username + "@" + hostUrl);

            PubSubNode node = pubSubManager.GetNode(jid, nodeName, 1);
            node.OnItemPublished += node_OnItemPublished;
            node.Subscribe();
        }

        void node_OnItemPublished(PubSubNode node, jabber.protocol.iq.PubSubItem item)
        {
            log.Debug(String.Format("node_OnItemPublished {0} \n {1} \n {2} ",
                node,
                item));
        }


        //<T> void unregisterListener(String nodeName, IncomingNodeMessageListener<T> eventHandler) throws XMPPException;

        //<T> void sendEvent(String nodeName, T payload) throws XMPPException;

        //<T> void sendEvent(String messageId, String nodeName, T payload) throws XMPPException;

    }
}

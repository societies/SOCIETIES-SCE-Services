package org.temp.CISIntegeration;

import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.services.IServices;
import javax.sql.DataSource;
public interface ContextBinderInf{	
	public ICisManager getCisMgm();
	public ICtxBroker  getCtxBrk();
	public ICommManager getComMgt();
	public IServices getServices();
	public DataSource getDataSource();
}
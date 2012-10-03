package org.temp.CISIntegeration;

import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.services.IServices;
import javax.sql.DataSource;
public interface ContextBinderInf{
	public void setCisMgm(ICisManager cisMgm);
	public void setCtxBrk(ICtxBroker ctxBrk);
	public void setComMgt(ICommManager comMgt);
	public void setServices(IServices services);
	public void setDataSource(DataSource source);
}
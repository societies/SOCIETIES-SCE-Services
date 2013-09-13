package org.temp.CISIntegeration;

import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.services.IServices;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

public class ContextBinder implements ContextBinderInf {

	@Autowired
	private ICisManager cisMgm;
	
	@Autowired
	private ICtxBroker ctxBrk;
	
	@Autowired
	private ICommManager comMgt;
	
	@Autowired
	private IServices services;
	
	@Autowired
	private DataSource dataSource;

	public static ContextBinder instance;
	
	public ContextBinder(){
		instance=this;
	}
	
	public ICisManager getCisMgm() {
		return cisMgm;
	}

	public ICtxBroker getCtxBrk() {
		return ctxBrk;
	}

	public ICommManager getComMgt() {
		return comMgt;
	}

	public IServices getServices() {
		return services;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}

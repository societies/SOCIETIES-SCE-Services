package org.temp.CISIntegeration;

import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.services.IServices;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

public class ContextBinder implements ContextBinderInf {
	private ICisManager cisMgm;
	private ICtxBroker ctxBrk;
	private ICommManager comMgt;
	private IServices services;
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
	public void setCisMgm(ICisManager ics){
		this.cisMgm=ics;
	}
	public void setCtxBrk(ICtxBroker cbk){
		this.ctxBrk=cbk;
	}
	public void setComMgt(ICommManager icm){
		this.comMgt=icm;
	}
	public void setServices(IServices isc){
		this.services=isc;
	}
	public void setDataSource(DataSource ds){
		this.dataSource=ds;
	}
}

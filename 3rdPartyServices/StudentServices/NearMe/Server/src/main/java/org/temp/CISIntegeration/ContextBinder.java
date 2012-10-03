package org.temp.CISIntegeration;

import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.services.IServices;
import javax.sql.DataSource;
public class ContextBinder implements ContextBinderInf{
private static ICisManager cisMgm;
private static ICtxBroker ctxBrk;
private static ICommManager comMgt;
private static IServices services;
private static DataSource dataSource;
public static ICisManager getCisMgm() {
	return cisMgm;
}
public void setCisMgm(ICisManager cisMgm) {
	this.cisMgm = cisMgm;
}
public static ICtxBroker getCtxBrk() {
	return ctxBrk;
}
public void setCtxBrk(ICtxBroker ctxBrk) {
	this.ctxBrk = ctxBrk;
}
public static ICommManager getComMgt() {
	return comMgt;
}
public void setComMgt(ICommManager comMgt) {
	this.comMgt = comMgt;
}
public static IServices getServices() {
	return services;
}
public void setServices(IServices services) {
	this.services = services;
}
public void setDataSource(DataSource source){
	this.dataSource=source;
}
public static DataSource getDataSource(){
	return dataSource;
}
}

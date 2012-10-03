package mockplatform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

public class MockCtxBroker implements ICtxBroker {
	public static boolean init = true;

	@Override
	public Future<CtxAssociation> createAssociation(Requestor arg0,
			IIdentity arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxAttribute> createAttribute(Requestor arg0,
			CtxEntityIdentifier arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		return new Future<CtxAttribute>(){
			public CtxAttribute get(){
				return 
						new CtxAttribute(null) {
			public void setStringValue(String str) {
				System.err.println(str);
			}
			};
				}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public CtxAttribute get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	@Override
	public Future<CtxEntity> createEntity(Requestor arg0, IIdentity arg1,
			String arg2) throws CtxException {
		// TODO Auto-generated method stub
		return new Future<CtxEntity>() {
			public CtxEntity get() {
				return new CtxEntity(null) {
					public Set<CtxAttribute> getAttributes(String name) {
						Set<CtxAttribute> res = new HashSet<CtxAttribute>();
						res.add(new CtxAttribute(null) {
							public void setStringValue(String str) {
								System.err.println(str);
							}
						});
						return res;
					}
				};
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public CtxEntity get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	@Override
	public Future<List<Object>> evaluateSimilarity(Serializable arg0,
			List<Serializable> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxIdentifier>> lookup(Requestor arg0, IIdentity arg1,
			CtxModelType arg2, String arg3) throws CtxException {
		// TODO Auto-generated method stub
		if (init) {
			System.err.println("not found!");
		} else {
			System.err.println("found!");
		}
		System.err.print("lookup req:" + arg0);
		System.err.print("\tid:" + arg1.getJid());
		System.err.print("\ttype:" + arg2);
		System.err.print("\tname:" + arg3);
		final boolean iinit = init;
		Future<List<CtxIdentifier>> ls = new Future<List<CtxIdentifier>>() {

			@Override
			public boolean cancel(boolean arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public List<CtxIdentifier> get() throws InterruptedException,
					ExecutionException {
				// TODO Auto-generated method stub
				List<CtxIdentifier> lls = new ArrayList<CtxIdentifier>();
				if (!iinit) {
					lls.add(null);
				}
				return lls;
			}

			@Override
			public List<CtxIdentifier> get(long arg0, TimeUnit arg1)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return false;
			}
		};
		init = false;
		return ls;
	}

	@Override
	public Future<List<CtxIdentifier>> lookup(Requestor arg0,
			CtxEntityIdentifier arg1, CtxModelType arg2, String arg3)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> lookupEntities(Requestor arg0,
			IIdentity arg1, String arg2, String arg3, Serializable arg4,
			Serializable arg5) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerForChanges(Requestor arg0, CtxChangeEventListener arg1,
			CtxIdentifier arg2) throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerForChanges(Requestor arg0, CtxChangeEventListener arg1,
			CtxEntityIdentifier arg2, String arg3) throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public Future<CtxModelObject> remove(Requestor arg0, CtxIdentifier arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxModelObject> retrieve(Requestor arg0, CtxIdentifier arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return new Future<CtxModelObject>() {

			@Override
			public boolean cancel(boolean arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public CtxModelObject get() throws InterruptedException,
					ExecutionException {
				// TODO Auto-generated method stub
				return new CtxEntity(null) {
					public Set<CtxAttribute> getAttributes(String name) {
						Set<CtxAttribute> res = new HashSet<CtxAttribute>();
						res.add(new CtxAttribute(null) {
							public void setStringValue(String str) {
								System.err.println(str);
							}
						});
						return res;
					}
				};
			}

			@Override
			public CtxModelObject get(long arg0, TimeUnit arg1)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return false;
			}

		};
	}

	@Override
	public Future<CtxEntity> retrieveAdministratingCSS(Requestor arg0,
			CtxEntityIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<CtxBond>> retrieveBonds(Requestor arg0,
			CtxEntityIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxEntityIdentifier> retrieveCommunityEntityId(
			Requestor arg0, IIdentity arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			Requestor arg0, CtxEntityIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(Requestor arg0,
			CtxAttributeIdentifier arg1, Date arg2) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(Requestor arg0,
			CtxAttributeIdentifier arg1, int arg2) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(Requestor arg0,
			CtxAttributeIdentifier arg1, int arg2) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(Requestor arg0,
			CtxAttributeIdentifier arg1, Date arg2, Date arg3)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxEntityIdentifier> retrieveIndividualEntityId(
			Requestor arg0, IIdentity arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			Requestor arg0, CtxEntityIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			Requestor arg0, CtxEntityIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unregisterFromChanges(Requestor arg0,
			CtxChangeEventListener arg1, CtxIdentifier arg2)
			throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterFromChanges(Requestor arg0,
			CtxChangeEventListener arg1, CtxEntityIdentifier arg2, String arg3)
			throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public Future<CtxModelObject> update(Requestor arg0, final CtxModelObject arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		System.err.println("update!");
		return new Future<CtxModelObject>(){

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public CtxModelObject get() throws InterruptedException,
					ExecutionException {
				// TODO Auto-generated method stub
				return arg1;
			}

			@Override
			public CtxModelObject get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
	}

}

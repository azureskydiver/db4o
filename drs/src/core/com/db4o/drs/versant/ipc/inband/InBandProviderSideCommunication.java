package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;

public class InBandProviderSideCommunication implements ProviderSideCommunication {

	private VodDatabase _vod;
	private VodCobra _cobra;
	
	public InBandProviderSideCommunication(VodDatabase vod, VodCobra cobra) {
		_vod = vod;
		_cobra = cobra;
	}
	
	public void registerClassMetadata(ClassMetadata classMetadata) {
		ClassMetadata changed = ensureStoreChanged(classMetadata, new Function4<ClassMetadata, Boolean>() {
			public Boolean apply(ClassMetadata value) {
				return value.monitored();
			}
		});
		if(changed == null){
			Class eventListenerProgram = com.db4o.drs.versant.eventlistener.Program.class;
			throw new IllegalStateException("Event listener process did not respond to ClassMetadata creation for " 
					+ classMetadata.name() + ". Ensure that " + eventListenerProgram.getName() + " is running.");
		}
	}

	private static boolean DISABLE_ISOLATION = true;
	
	public void requestIsolation(IsolationMode isolationMode) {
		if(DISABLE_ISOLATION) {
			return;
		}
		IsolationModeRequest isolationRequest = singleInstance(IsolationModeRequest.class, new IsolationModeRequest(isolationMode));
		isolationRequest.isolationMode(isolationMode);
		isolationRequest.isResponse(false);
		IsolationModeRequest response = ensureStoreChanged(isolationRequest, new Function4<IsolationModeRequest, Boolean>() {
			public Boolean apply(IsolationModeRequest isolationRequest) {
				return isolationRequest.isResponse();
			}
		});
		if(response == null || !response.isResponse()) {
			throw new IllegalStateException("No isolation mode response received.");
		}
	}

	public long requestTimestamp() {
		TimestampSyncRequest syncRequest = singleInstance(TimestampSyncRequest.class, new TimestampSyncRequest());
		TimestampSyncRequest response = ensureStoreChanged(syncRequest, new Function4<TimestampSyncRequest, Boolean>() {
			public Boolean apply(TimestampSyncRequest syncRequest) {
				return syncRequest.isAnswered();
			}
		});
		if(response == null || !response.isAnswered()) {
			throw new IllegalStateException("No timestamp sync response received.");
		}
		return response.timestamp();
	}

	private <T> T singleInstance(Class<T> extent, T defaultValue) {
		Long loid = _cobra.singleInstanceLoid(extent);
		return loid == null ? defaultValue : (T)_cobra.objectByLoid(loid);
	}

	private <T> T ensureStoreChanged(T obj, final Function4<T, Boolean> modifiedCheck) {
		final VodJdo jdo = new VodJdo(_vod);
		jdo.store(obj);
		jdo.commit();
		
		final ByRef<T> peeked = ByRef.newInstance(obj);
		
		try{
			int timeoutInMillis = 10000;
			int millisecondsBetweenRetries = 50;
			boolean changed = Runtime4.retry(timeoutInMillis, millisecondsBetweenRetries, new Closure4<Boolean>() {
				public Boolean run() {
					peeked.value = jdo.peek(peeked.value);
					return modifiedCheck.apply(peeked.value);
				}
			});
			return changed ? peeked.value : null;
		} finally {
			jdo.close();
		}
	}

}

package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;

public class InBandProviderSideCommunication implements ProviderSideCommunication {

	private VodCobra _cobra;
	
	public InBandProviderSideCommunication(VodDatabase vod, VodCobra cobra) {
		_cobra = cobra;
	}

	private static boolean DISABLE_ISOLATION = true;
	
	public void requestIsolation(IsolationMode isolationMode) {
		if(DISABLE_ISOLATION) {
			return;
		}
		Long loid = _cobra.singleInstanceLoid(IsolationModeRequest.class);
		IsolationModeRequest isolationRequest;
		if(loid == null){
			isolationRequest = new IsolationModeRequest(isolationMode);
		} else {
			isolationRequest = (IsolationModeRequest)_cobra.objectByLoid(loid);
		}
		isolationRequest.isolationMode(isolationMode);
		isolationRequest.isResponse(false);
		IsolationModeRequest response = ensureStoreChanged(isolationRequest, loid, new Function4<IsolationModeRequest, Boolean>() {
			public Boolean apply(IsolationModeRequest isolationRequest) {
				return isolationRequest.isResponse();
			}
		});
		if(response == null || !response.isResponse()) {
			throw new IllegalStateException("No isolation mode response received.");
		}
	}
	
	public void syncTimestamp(long timestamp){
		sendTimestampSync(true, timestamp);
	}

	public long requestTimestamp() {
		return sendTimestampSync(false, 0);
	}

	private long sendTimestampSync(boolean forceSync, long myTimeStamp) {
		Long loid = _cobra.singleInstanceLoid(TimestampSyncRequest.class);
		TimestampSyncRequest syncRequest;
		if(loid == null){
			syncRequest = new TimestampSyncRequest();
			loid = 0L;
		} else {
			syncRequest = (TimestampSyncRequest)_cobra.objectByLoid(loid);
		}
		syncRequest.resetForRequest();
		syncRequest.timestamp(myTimeStamp);
		syncRequest.forceSync(forceSync);
		TimestampSyncRequest response = ensureStoreChanged(syncRequest, loid, new Function4<TimestampSyncRequest, Boolean>() {
			public Boolean apply(TimestampSyncRequest syncRequest) {
				return syncRequest.isAnswered();
			}
		});
		if(response == null || !response.isAnswered()) {
			throw new IllegalStateException("No timestamp sync response received.");
		}
		return response.timestamp();
	}

	public void waitForClassMetadataAcknowledgment(String fullyQualifiedName) {
		Long loid = _cobra.singleInstanceLoid(ClassMetadataAcknowledgement.class);
		ClassMetadataAcknowledgement acknowledgement;
		if(loid == null){
			acknowledgement = new ClassMetadataAcknowledgement(fullyQualifiedName, false);
			loid = 0L;
		} else {
			acknowledgement = (ClassMetadataAcknowledgement)_cobra.objectByLoid(loid);
		}
		ClassMetadataAcknowledgement response = ensureStoreChanged(acknowledgement, loid, new Function4<ClassMetadataAcknowledgement, Boolean>() {
			public Boolean apply(ClassMetadataAcknowledgement acknowledgement) {
				return acknowledgement.acknowledged();
			}
		});
		if(!response.acknowledged()) {
			throw new IllegalStateException("No class metadata acknowledgment received for " + fullyQualifiedName);
		}
	}

	private <T> T ensureStoreChanged(T obj, long loid, final Function4<T, Boolean> modifiedCheck) {
		if(loid == 0){
			loid = _cobra.store(obj);
		} else {
			_cobra.store(loid, obj);
		}
		_cobra.commit();
		final long finalLoid = loid;
		final ByRef<T> peeked = ByRef.newInstance(obj);
		int timeoutInMillis = 10000;
		int millisecondsBetweenRetries = 50;
		boolean changed = Runtime4.retry(timeoutInMillis, millisecondsBetweenRetries, new Closure4<Boolean>() {
			public Boolean run() {
				peeked.value = _cobra.objectByLoid(finalLoid);
				return modifiedCheck.apply(peeked.value);
			}
		});
		return changed ? peeked.value : null;
	}

}

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
		IsolationModeRequest isolationRequest = 
			_cobra.singleInstanceOrDefault(
					IsolationModeRequest.class, 
					new IsolationModeRequest(isolationMode));
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
	
	public void syncTimestamp(long timestamp){
		sendTimestampSync(true, timestamp);
	}

	public long requestTimestamp() {
		return sendTimestampSync(false, 0);
	}

	private long sendTimestampSync(boolean forceSync, long myTimeStamp) {
		TimestampSyncRequest syncRequest = 
			_cobra.singleInstanceOrDefault(TimestampSyncRequest.class,new TimestampSyncRequest());
		syncRequest.resetForRequest();
		syncRequest.timestamp(myTimeStamp);
		syncRequest.forceSync(true);
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

	public void waitForClassMetadataAcknowledgment(String fullyQualifiedName) {
		ClassMetadataAcknowledgement acknowledgement = 
			_cobra.singleInstanceOrDefault(
					ClassMetadataAcknowledgement.class, 
					new ClassMetadataAcknowledgement(fullyQualifiedName));
		acknowledgement.acknowledged(false);
		ClassMetadataAcknowledgement response = ensureStoreChanged(acknowledgement, new Function4<ClassMetadataAcknowledgement, Boolean>() {
			public Boolean apply(ClassMetadataAcknowledgement acknowledgement) {
				return acknowledgement.acknowledged();
			}
		});
		if(!response.acknowledged()) {
			throw new IllegalStateException("No class metadata acknowledgment received for " + fullyQualifiedName);
		}
	}

	private <T extends CobraPersistentObject> T ensureStoreChanged(T obj, final Function4<T, Boolean> modifiedCheck) {
		_cobra.store(obj);
		_cobra.commit();
		final ByRef<T> peeked = ByRef.newInstance(obj);
		int timeoutInMillis = 10000;
		int millisecondsBetweenRetries = 50;
		boolean changed = Runtime4.retry(timeoutInMillis, millisecondsBetweenRetries, new Closure4<Boolean>() {
			public Boolean run() {
				peeked.value = _cobra.<T>objectByLoid(peeked.value.loid());
				return modifiedCheck.apply(peeked.value);
			}
		});
		return changed ? peeked.value : null;
	}

}

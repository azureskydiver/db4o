/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.foundation.*;
import com.versant.event.*;

public class InBandEventProcessorSideCommunication implements EventProcessorSideCommunication {

	private VodCobra _cobra;
	private VodEventClient _client;
	private final Object _lock;
	
	public InBandEventProcessorSideCommunication(VodCobra cobra, VodEventClient client, Object lock) {
		_lock = lock;
		_cobra = cobra;
		_client = client;
	}

	public void acknowledgeClassMetadataRegistration(String fullyQualifiedName) {
		ClassMetadataAcknowledgement acknowledgment = _cobra.singleInstance(ClassMetadataAcknowledgement.class);
		acknowledgment.acknowledged(true);
		_cobra.store(acknowledgment);
		_cobra.commit();
	}

	public void acknowledgeIsolationMode(int isolationMode) {
		IsolationModeRequest acknowledgment = _cobra.singleInstance(IsolationModeRequest.class);
		acknowledgment.isolationMode(isolationMode);
		acknowledgment.isResponse(true);
		_cobra.store(acknowledgment);
		_cobra.commit();
	}

	public void registerIsolationRequestListener(final Procedure4<Integer> listener) {
		EventChannel channel = _client.produceClassChannel(IsolationModeRequest.class.getName());
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceModified (VersantEventObject event){
				processIsolationRequest(event);
			}
			
			public void instanceCreated (VersantEventObject event) {
				processIsolationRequest(event);
			}
			
			public void instanceDeleted (VersantEventObject event) {
			}
			
			private void processIsolationRequest(VersantEventObject event) {
				synchronized(_lock) {
					long isolationRequestLoid = VodCobra.loidAsLong(event.getRaiserLoid());
					IsolationModeRequest isolationRequest = _cobra.objectByLoid(isolationRequestLoid);
					if(isolationRequest.isResponse()) {
						return;
					}
					listener.apply(isolationRequest.isolationMode());
				}
			}
		});
	}
	
	public void registerSyncRequestListener(final Procedure4<Long> listener) {
		EventChannel channel = _client.produceClassChannel(TimestampSyncRequest.class.getName());
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceModified (VersantEventObject event){
				processSynchronizationRequest(event);
			}
			
			public void instanceCreated (VersantEventObject event) {
				processSynchronizationRequest(event);
			}
			
			public void instanceDeleted (VersantEventObject event) {
			}
			
			private void processSynchronizationRequest(VersantEventObject event) {
				synchronized(_lock) {
					long syncRequestLoid = VodCobra.loidAsLong(event.getRaiserLoid());
					TimestampSyncRequest syncRequest = _cobra.objectByLoid(syncRequestLoid);
					if(syncRequest.isAnswered()) {
						return;
					}
					long newTimeStamp = syncRequest.forceSync() ? syncRequest.timestamp() : 0;
					listener.apply(newTimeStamp);
				}
			}
		});
	}

	public void sendTimestamp(long timestamp) {
		synchronized(_lock) {
			TimestampSyncRequest syncRequest = _cobra.singleInstance(TimestampSyncRequest.class);
			syncRequest.timestamp(timestamp);
			syncRequest.answered(true);
			_cobra.store(syncRequest);
			_cobra.commit();
		}
	}

	public void shutdown() {
		_client.shutdown();
	}

}

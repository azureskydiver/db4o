/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.ipc.inband;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.metadata.*;
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
		Long loid = _cobra.singleInstanceLoid(ClassMetadataAcknowledgement.class);
		ClassMetadataAcknowledgement acknowledgment = _cobra.objectByLoid(loid);
		acknowledgment.acknowledged(true);
		_cobra.store(loid, acknowledgment);
		_cobra.commit();
	}

	public void acknowledgeIsolationMode(IsolationMode isolationMode) {
		// TODO 
	}

	public void registerIsolationRequestListener(Procedure4<IsolationMode> listener) {
		// TODO 		
	}

	public void registerSyncRequestListener(final Block4 listener) {
		EventChannel channel = _client.produceClassChannel(TimestampSyncRequest.class.getName());
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceModified (VersantEventObject event){
				queueSynchronizationRequest(event);
			}
			
			public void instanceCreated (VersantEventObject event) {
				queueSynchronizationRequest(event);
			}
			
			public void instanceDeleted (VersantEventObject event) {
			}
			
			private void queueSynchronizationRequest(VersantEventObject event) {
				synchronized(_lock) {
					long syncRequestLoid = VodCobra.loidAsLong(event.getRaiserLoid());
					TimestampSyncRequest syncRequest = _cobra.objectByLoid(syncRequestLoid);
					if(syncRequest.isAnswered()) {
						return;
					}
					listener.run();
				}
			}
		});
	}

	public void sendTimestamp(long timestamp) {
		synchronized(_lock) {
			Long loid = _cobra.singleInstanceLoid(TimestampSyncRequest.class);
			TimestampSyncRequest syncRequest = _cobra.objectByLoid(loid);
			syncRequest.timestamp(timestamp);
			_cobra.store(loid, syncRequest);
			_cobra.commit();
		}
	}

	public void shutdown() {
		_client.shutdown();
	}

}

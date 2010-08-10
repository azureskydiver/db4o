package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;

public interface EventProcessorSideCommunication {
	void registerIsolationRequestListener(Procedure4<IsolationMode> listener);
	void registerSyncRequestListener(Block4 listener);
	void acknowledgeClassMetadataRegistration(String fullyQualifiedName);
	void acknowledgeIsolationMode(IsolationMode isolationMode);
	void sendTimestamp(long timeStamp);
	void shutdown();
	
	public static class ClassMetadataRegistrationEvent {
		public final ClassMetadata _classMetadata;
		public final long _loid;

		public ClassMetadataRegistrationEvent(ClassMetadata classMetadata, long loid) {
			_classMetadata = classMetadata;
			_loid = loid;
		}
	}
}

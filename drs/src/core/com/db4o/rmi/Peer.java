package com.db4o.rmi;

public interface Peer<T> extends ByteArrayConsumer {

	T sync();

	T async();

	<R> T async(Callback<R> callback);
	
	void setConsumer(ByteArrayConsumer constumer);

}

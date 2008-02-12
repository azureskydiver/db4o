/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class MCommittedInfo extends MsgD implements ClientSideMessage {

	public MCommittedInfo encode(CallbackObjectInfoCollections callbackInfo) {
		byte[] bytes = encodeInfo(callbackInfo);
		MCommittedInfo committedInfo = (MCommittedInfo) getWriterForLength(transaction(),
			bytes.length);
		committedInfo._payLoad.append(bytes);
		return committedInfo;
	}

	private byte[] encodeInfo(CallbackObjectInfoCollections callbackInfo) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		encodeObjectInfoCollection(os, callbackInfo.added, new InternalIDEncoder());
		encodeObjectInfoCollection(os, callbackInfo.deleted, new FrozenObjectInfoEncoder());
		encodeObjectInfoCollection(os, callbackInfo.updated, new InternalIDEncoder());
		
		return os.toByteArray();
	}
	
	private final class FrozenObjectInfoEncoder implements ObjectInfoEncoder {
		public void encode(ByteArrayOutputStream os, ObjectInfo info) {
			try {
				writeLong(os, info.getInternalID());
				writeUUID(os, info.getUUID());
				writeLong(os, info.getVersion());
			} catch (IOException x) {
				throw new Db4oException(x);
			}
		}

		public ObjectInfo decode(ByteArrayInputStream is) {
			try {
				long id = readLong(is);
				if (id == -1) {
					return null;
				}
				
				Db4oUUID uuid = readUUID(is);
				long version = readLong(is);
				return new FrozenObjectInfo(null, id, uuid, version);
			} catch (IOException x) {
				throw new Db4oException(x);
			}
		}
	}

	private final class InternalIDEncoder implements ObjectInfoEncoder {
		public void encode(ByteArrayOutputStream os, ObjectInfo info) {
			writeLong(os, info.getInternalID());
		}

		public ObjectInfo decode(ByteArrayInputStream is) {
			long id = readLong(is);
			if (id == -1) {
				return null;
			}
			return new LazyObjectReference(transaction(), (int) id);
		}
	}

	interface ObjectInfoEncoder {
		void encode(ByteArrayOutputStream os, ObjectInfo info);
		ObjectInfo decode(ByteArrayInputStream is);
	}
	
	private void encodeObjectInfoCollection(ByteArrayOutputStream os,
			ObjectInfoCollection collection, final ObjectInfoEncoder encoder) {
		Iterator4 iter = collection.iterator();
		while (iter.moveNext()) {
			ObjectInfo obj = (ObjectInfo) iter.current();
			encoder.encode(os, obj);
		}
		writeLong(os, -1);
	}
	
	public CallbackObjectInfoCollections decode() {
		ByteArrayInputStream is = new ByteArrayInputStream(_payLoad._buffer);
		
		final ObjectInfoCollection added = decodeObjectInfoCollection(is, new InternalIDEncoder());
		final ObjectInfoCollection deleted = decodeObjectInfoCollection(is, new FrozenObjectInfoEncoder());
		final ObjectInfoCollection updated = decodeObjectInfoCollection(is, new InternalIDEncoder());
		return new CallbackObjectInfoCollections(added, updated, deleted);
	}

	private ObjectInfoCollection decodeObjectInfoCollection(ByteArrayInputStream is, ObjectInfoEncoder encoder){
		final Collection4 collection = new Collection4();
		while (true) {
			ObjectInfo info = encoder.decode(is);
			if (null == info) {
				break;
			}
			collection.add(info);
		}
		return new ObjectInfoCollectionImpl(collection);
	}

	private void writeLong(ByteArrayOutputStream os, long l) {
		for (int i = 0; i < 64; i += 8) {
			os.write((int) (l >> i));
		}
	}

	private long readLong(ByteArrayInputStream is) {
		long l = 0;
		for (int i = 0; i < 64; i += 8) {
			l += ((long) (is.read())) << i;
		}
		return l;
	}

	public boolean processAtClient() {
		final CallbackObjectInfoCollections callbackInfos = decode();
		new Thread(new Runnable() {
			public void run() {
				if(stream().isClosed()){
					return;
				}
				stream().callbacks().commitOnCompleted(transaction(), callbackInfos);
			}
		}).start();
		return true;
	}

	private void writeUUID(ByteArrayOutputStream os, final Db4oUUID uuid)
			throws IOException {
		if (uuid == null) {
			writeLong(os, -1);
			return;
		}
		writeLong(os, uuid.getLongPart());
		writeByteArray(os, uuid.getSignaturePart());
	}
	
	private Db4oUUID readUUID(ByteArrayInputStream is)	throws IOException {
		long longPart = readLong(is);
		if (-1 == longPart) {
			return null;
		}
		byte[] signature = readByteArray(is);
		return new Db4oUUID(longPart, signature);
	}

	private byte[] readByteArray(ByteArrayInputStream is) throws IOException {
		long length = readLong(is);
		byte[] array = new byte[(int)length];
		is.read(array);
		return array;
	}

	protected void writeByteArray(ByteArrayOutputStream os, byte[] signaturePart) throws IOException {
		writeLong(os, signaturePart.length);
		os.write(signaturePart);
	}
}

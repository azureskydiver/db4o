/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.IntIterator4;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

/**
 * Prefetchs multiples objects at once (in a single message).
 * 
 * @exclude
 */
public class SingleMessagePrefetchingStrategy implements PrefetchingStrategy {

	public static final PrefetchingStrategy INSTANCE = new SingleMessagePrefetchingStrategy();
	
	private SingleMessagePrefetchingStrategy() {
	}

	public int prefetchObjects(ClientObjectContainer container,
			IntIterator4 ids, Object[] prefetched, int prefetchCount) {
		int count = 0;

		int toGet = 0;
		int[] idsToGet = new int[prefetchCount];
		int[] position = new int[prefetchCount];

		while (count < prefetchCount) {
			if (!ids.moveNext()) {
				break;
			}
			int id = ids.currentInt();
			if (id > 0) {
                Object obj = container.objectForIDFromCache(id);
                if(obj != null){
                    prefetched[count] = obj;
                }else{
					idsToGet[toGet] = id;
					position[toGet] = count;
					toGet++;
				}
				count++;
			}
		}

		if (toGet > 0) {
			MsgD msg = Msg.READ_MULTIPLE_OBJECTS.getWriterForIntArray(container.getTransaction(),
					idsToGet, toGet);
			container.writeMsg(msg, true);
			MsgD message = (MsgD) container.expectedResponse(Msg.READ_MULTIPLE_OBJECTS);
			int embeddedMessageCount = message.readInt();
			for (int i = 0; i < embeddedMessageCount; i++) {
				MsgObject mso = (MsgObject) Msg.OBJECT_TO_CLIENT
						.clone(container.getTransaction());
				mso.payLoad(message.payLoad().readYapBytes());
				if (mso.payLoad() != null) {
					mso.payLoad().incrementOffset(Const4.MESSAGE_LENGTH);
					StatefulBuffer reader = mso.unmarshall(Const4.MESSAGE_LENGTH);
                    Object obj = container.objectForIDFromCache(idsToGet[i]);
                    if(obj != null){
                        prefetched[position[i]] = obj;
                    }else{
    					prefetched[position[i]] = new ObjectReference(idsToGet[i]).readPrefetch(container, reader);
                    }
				}
			}
		}
		return count;
	}

}

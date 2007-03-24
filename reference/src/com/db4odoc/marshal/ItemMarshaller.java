package com.db4odoc.marshal;

import com.db4o.config.ObjectMarshaller;
import com.db4o.foundation.PrimitiveCodec;

public class ItemMarshaller implements ObjectMarshaller{
	
	// Write field values to a byte array
    // No reflection is used
		public void writeFields(Object obj, byte[] slot, int offset) {
			Item item = (Item)obj;
			PrimitiveCodec.writeInt(slot, offset, item._one);
			offset+= PrimitiveCodec.INT_LENGTH;
			PrimitiveCodec.writeLong(slot, offset, item._two);
			offset+= PrimitiveCodec.LONG_LENGTH;
			PrimitiveCodec.writeInt(slot, offset, item._three);
		}
	
        // Restore field values from the byte array
        // No reflection is used
		public void readFields(Object obj, byte[] slot, int offset) {
			Item item = (Item)obj;
			item._one = PrimitiveCodec.readInt(slot, offset);
			offset+= PrimitiveCodec.INT_LENGTH;
			item._two = PrimitiveCodec.readLong(slot, offset);
			offset+= PrimitiveCodec.LONG_LENGTH;
			item._three = PrimitiveCodec.readInt(slot, offset);
		}
	
		public int marshalledFieldLength() {
			return PrimitiveCodec.INT_LENGTH * 2 + PrimitiveCodec.LONG_LENGTH;
		}
	}

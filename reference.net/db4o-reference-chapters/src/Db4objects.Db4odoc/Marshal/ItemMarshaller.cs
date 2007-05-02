/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Foundation;

namespace Db4objects.Db4odoc.Marshal
{
    class ItemMarshaller: IObjectMarshaller
    {
        // Write field values to a byte array
        // No reflection is used
		public void WriteFields(object obj, byte[] slot, int offset) {
			Item item = (Item)obj;
			PrimitiveCodec.WriteInt(slot, offset, item._one);
			offset+= PrimitiveCodec.INT_LENGTH;
			PrimitiveCodec.WriteLong(slot, offset, item._two);
			offset+= PrimitiveCodec.LONG_LENGTH;
			PrimitiveCodec.WriteInt(slot, offset, item._three);
		}

	    // Restore field values from the byte array
        // No reflection is used
		public void ReadFields(object obj, byte[] slot, int offset) {
			Item item = (Item)obj;
			item._one = PrimitiveCodec.ReadInt(slot, offset);
			offset+= PrimitiveCodec.INT_LENGTH;
			item._two = PrimitiveCodec.ReadLong(slot, offset);
			offset+= PrimitiveCodec.LONG_LENGTH;
			item._three = PrimitiveCodec.ReadInt(slot, offset);
		}
	
		public int MarshalledFieldLength() {
			return PrimitiveCodec.INT_LENGTH * 2 + PrimitiveCodec.LONG_LENGTH;
		}
    }
}

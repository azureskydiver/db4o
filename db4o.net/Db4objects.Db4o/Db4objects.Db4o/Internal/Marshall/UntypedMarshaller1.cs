/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Marshall;

namespace Db4objects.Db4o.Internal.Marshall
{
	/// <exclude></exclude>
	public class UntypedMarshaller1 : UntypedMarshaller
	{
		public override bool UseNormalClassRead()
		{
			return false;
		}

		public override ITypeHandler4 ReadArrayHandler(Transaction trans, ByteArrayBuffer
			[] reader)
		{
			int payLoadOffSet = reader[0].ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			ITypeHandler4 ret = null;
			reader[0]._offset = payLoadOffSet;
			int yapClassID = reader[0].ReadInt();
			ClassMetadata yc = trans.Container().ClassMetadataForId(yapClassID);
			if (yc != null)
			{
				ITypeHandler4 configuredHandler = trans.Container().ConfigImpl().TypeHandlerForClass
					(yc.ClassReflector(), HandlerRegistry.HandlerVersion);
				if (configuredHandler != null && configuredHandler is IFirstClassHandler)
				{
					ret = ((IFirstClassHandler)configuredHandler).ReadArrayHandler(trans, _family, reader
						);
				}
				else
				{
					ret = yc.ReadArrayHandler(trans, _family, reader);
				}
			}
			return ret;
		}
	}
}

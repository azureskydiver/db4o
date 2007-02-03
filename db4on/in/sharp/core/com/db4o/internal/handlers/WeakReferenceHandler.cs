/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.@internal.handlers
{
	internal class WeakReferenceHandler : WeakReference
	{
		public object yapObject;

		internal WeakReferenceHandler(Object queue, Object yapObject, Object obj) : base(obj, false){
			this.yapObject = yapObject;
			((WeakReferenceHandlerQueue) queue).Add(this);
		}

		public object Get(){
			return this.Target;
		}
	}
}
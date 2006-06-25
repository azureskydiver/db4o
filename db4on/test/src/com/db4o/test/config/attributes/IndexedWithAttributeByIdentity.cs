/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

using System;

using com.db4o.query;
using com.db4o.config.attributes;

namespace com.db4o.test.config.attributes {
	
	public class IndexedWithAttributeByIdentity {
		
		[Indexed]
		Atom atom;

		const int COUNT = 10;
		const string NAME_PREFIX = "iwabi_";

		public void Configure ()
		{
		}

		public void Store ()
		{
			for (int i = 0; i < COUNT; i++) {
				IndexedWithAttributeByIdentity iwabi = new IndexedWithAttributeByIdentity ();
				iwabi.atom = new Atom (NAME_PREFIX + i);
				Tester.Store (iwabi);
			}
		}

		public void Test ()
		{
			for (int i = 0; i < COUNT; i++) {
				Query q = Tester.Query ();
				q.Constrain (typeof (Atom));
				q.Descend ("name").Constrain (NAME_PREFIX + i);
				ObjectSet objectSet = q.Execute ();
				Atom child = (Atom) objectSet.Next ();

				q = Tester.Query ();
				q.Constrain (typeof (IndexedWithAttributeByIdentity));
				q.Descend ("atom").Constrain (child).Identity ();
				objectSet = q.Execute ();
				Tester.Ensure (objectSet.Size () == 1);

				IndexedWithAttributeByIdentity iwabi = (IndexedWithAttributeByIdentity) objectSet.Next ();
				Tester.Ensure (iwabi.atom == child);
			}
		}
	}
}

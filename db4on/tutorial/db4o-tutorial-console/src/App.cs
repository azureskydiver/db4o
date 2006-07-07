using System;

namespace db4otutorialrunner
{
	class App
	{
		[STAThread]
		static void Main(string[] args)
		{
			com.db4o.f1.chapter1.FirstStepsExample.Main(args);
			com.db4o.f1.chapter2.StructuredExample.Main(args);
			com.db4o.f1.chapter3.CollectionsExample.Main(args);
			com.db4o.f1.chapter4.InheritanceExample.Main(args);
			com.db4o.f1.chapter5.ClientServerExample.Main(args);
			com.db4o.f1.chapter6.TranslatorExample.Main(args);
			com.db4o.f1.chapter21.IndexedExample.fillUpDB();
			com.db4o.f1.chapter21.IndexedExample.noIndex();
			com.db4o.f1.chapter21.IndexedExample.fullIndex();
			com.db4o.f1.chapter21.IndexedExample.pilotIndex();
			com.db4o.f1.chapter21.IndexedExample.pointsIndex();
		}
	}
}

/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Foundation;

namespace Db4objects.Db4odoc.UUIDs
{
	public class UUIDExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args) 
		{
			TestChangeIdentity();
			SetObjects();
			TestGenerateUUID();
		}
		// end Main
	
		private static string PrintSignature(byte[] Signature)
		{
			String str="";
			for (int i = 0; i < Signature.Length; i++) 
			{
				str = str+Signature[i];
			}
			return str;
		}
		// end PrintSignature

		public static void TestChangeIdentity()
		{
			File.Delete(YapFileName);
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			Db4oDatabase db;
			byte[] oldSignature;
			byte[] newSignature;
			try 
			{
				db = oc.Ext().Identity();
				oldSignature = db.GetSignature();
				Console.WriteLine("oldSignature: " + PrintSignature(oldSignature));	
				((YapFile)oc).GenerateNewIdentity();
			} 
			finally 
			{
				oc.Close();
			}        
			oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				db = oc.Ext().Identity();
				newSignature = db.GetSignature();
				Console.WriteLine("newSignature: " + PrintSignature(newSignature));
			} 
			finally 
			{
				oc.Close();
			}
        
			bool same = true;
        
			for (int i = 0; i < oldSignature.Length; i++) 
			{
				if(oldSignature[i] != newSignature[i])
				{
					same =false;
				}
			}
        
			if (same)
			{
				Console.WriteLine("Database signatures are identical");
			} 
			else 
			{
				Console.WriteLine("Database signatures are different");
			}
		}
		// end TestChangeIdentity

		public static void SetObjects()
		{
			Db4oFactory.Configure().ObjectClass(typeof(Pilot)).GenerateUUIDs(true);
			File.Delete(YapFileName);
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
				oc.Set(car);
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end SetObjects

		public static void TestGenerateUUID()
		{
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IQuery query = oc.Query();
				query.Constrain(typeof(Car));
				IObjectSet result = query.Execute();
				Car car = (Car)result[0];
				IObjectInfo carInfo = oc.Ext().GetObjectInfo(car);
				Db4oUUID carUUID = carInfo.GetUUID();
				Console.WriteLine("UUID for Car class are not generated:");
				Console.WriteLine("Car UUID: " + carUUID);
			
				Pilot pilot = car.Pilot;
				IObjectInfo pilotInfo = oc.Ext().GetObjectInfo(pilot);
				Db4oUUID pilotUUID = pilotInfo.GetUUID();
				Console.WriteLine("UUID for Car class are not generated:");
				Console.WriteLine("Pilot UUID: " + pilotUUID);
				Console.WriteLine("long part: " + pilotUUID.GetLongPart() +"; signature: " + PrintSignature(pilotUUID.GetSignaturePart()));
				long ms = TimeStampIdGenerator.IdToMilliseconds(pilotUUID.GetLongPart());
				Console.WriteLine("Pilot object was created: " + (new DateTime(1970,1,1)).AddMilliseconds(ms).ToString());
				Pilot pilotReturned = (Pilot)oc.Ext().GetByUUID(pilotUUID);
				Console.WriteLine("Pilot from UUID: " + pilotReturned);	
			} 
			finally 
			{
				oc.Close();
			}        
		}
		// end TestGenerateUUID
	}
}

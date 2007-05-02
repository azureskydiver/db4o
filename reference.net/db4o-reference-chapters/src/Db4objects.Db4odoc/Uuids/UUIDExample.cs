/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;

namespace Db4objects.Db4odoc.UUIDs
{
	public class UUIDExample
	{
		private const string Db4oFileName = "reference.db4o";

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

        private static void TestChangeIdentity()
		{
			File.Delete(Db4oFileName);
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			Db4oDatabase db;
			byte[] oldSignature;
			byte[] newSignature;
			try 
			{
				db = container.Ext().Identity();
				oldSignature = db.GetSignature();
				Console.WriteLine("oldSignature: " + PrintSignature(oldSignature));
                ((LocalObjectContainer)container).GenerateNewIdentity();
			} 
			finally 
			{
				container.Close();
			}        
			container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				db = container.Ext().Identity();
				newSignature = db.GetSignature();
				Console.WriteLine("newSignature: " + PrintSignature(newSignature));
			} 
			finally 
			{
				container.Close();
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

        private static void SetObjects()
		{
            File.Delete(Db4oFileName);
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(Pilot)).GenerateUUIDs(true);
            IObjectContainer container = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try 
			{
				Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
				container.Set(car);
			} 
			finally 
			{
				container.Close();
			}
		}
		// end SetObjects

        private static void TestGenerateUUID()
		{
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IQuery query = container.Query();
				query.Constrain(typeof(Car));
				IObjectSet result = query.Execute();
				Car car = (Car)result[0];
				IObjectInfo carInfo = container.Ext().GetObjectInfo(car);
				Db4oUUID carUUID = carInfo.GetUUID();
				Console.WriteLine("UUID for Car class are not generated:");
				Console.WriteLine("Car UUID: " + carUUID);
			
				Pilot pilot = car.Pilot;
				IObjectInfo pilotInfo = container.Ext().GetObjectInfo(pilot);
				Db4oUUID pilotUUID = pilotInfo.GetUUID();
				Console.WriteLine("UUID for Car class are not generated:");
				Console.WriteLine("Pilot UUID: " + pilotUUID);
				Console.WriteLine("long part: " + pilotUUID.GetLongPart() +"; signature: " + PrintSignature(pilotUUID.GetSignaturePart()));
				long ms = TimeStampIdGenerator.IdToMilliseconds(pilotUUID.GetLongPart());
				Console.WriteLine("Pilot object was created: " + (new DateTime(1970,1,1)).AddMilliseconds(ms).ToString());
				Pilot pilotReturned = (Pilot)container.Ext().GetByUUID(pilotUUID);
				Console.WriteLine("Pilot from UUID: " + pilotReturned);	
			} 
			finally 
			{
				container.Close();
			}        
		}
		// end TestGenerateUUID
	}
}

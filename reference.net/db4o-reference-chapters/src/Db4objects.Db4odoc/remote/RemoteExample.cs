/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Messaging;

namespace Db4objects.Db4odoc.Remote
{
	public class RemoteExample 
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args) 
		{
			SetObjects();
			UpdateCars();
			SetObjects();
			UpdateCarsWithMessage();
		}
		// end Main

		public static void SetObjects()
		{
			File.Delete(YapFileName);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				for (int i = 0; i < 5; i++) 
				{
					Car car = new Car("car"+i);
					db.Set(car);
				}
				db.Set(new RemoteExample());
			} 
			finally 
			{
				db.Close();
			}
			CheckCars();
		}
		// end SetObjects
	
		public static void UpdateCars()
		{
			// triggering mass updates with a singleton
			// complete server-side execution
			IObjectServer server=Db4oFactory.OpenServer(YapFileName,0);
			try 
			{
				IObjectContainer client=server.OpenClient();
				IQuery q = client.Query();
				q.Constrain(typeof(RemoteExample));
				q.Constrain(new UpdateEvaluation());
				q.Execute();
				client.Close();
			} 
			finally 
			{
				server.Close();
			}
			CheckCars();
		}
		// end UpdateCars
	
		private static void CheckCars()
		{
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IQuery q = db.Query();
				q.Constrain(typeof(Car));
				IObjectSet objectSet = q.Execute();
				ListResult(objectSet);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckCars
	
		public static void UpdateCarsWithMessage() 
		{
			IObjectServer server=Db4oFactory.OpenServer(YapFileName,0);
			// create message handler on the server
			server.Ext().Configure().ClientServer().SetMessageRecipient(
				new UpdateMessageRecipient());
			try 
			{
				IObjectContainer client=server.OpenClient();
				// send message object to the server
                IMessageSender sender = client.Ext().Configure().ClientServer().GetMessageSender();
				sender.Send(new UpdateServer());
				client.Close();
			}
			finally 
			{
				server.Close();
			}
			CheckCars();
		}
		// end UpdateCarsWithMessage

		public static void ListResult(IObjectSet result)
		{
			System.Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				System.Console.WriteLine(item);
			}
		}
		// end ListResult
	}


	public class UpdateEvaluation : IEvaluation
	{ 

		public void Evaluate(ICandidate candidate) 
		{
			// evaluate method is executed on the server
			// use it to run update code
			IObjectContainer objectContainer = candidate.ObjectContainer();
			IQuery q2 = objectContainer.Query();
			q2.Constrain(typeof(Car));
			IObjectSet objectSet = q2.Execute();
			while(objectSet.HasNext())
			{
				Car car = (Car)objectSet.Next();
				car.Model =  "Update1-"+ car.Model;
				objectContainer.Set(car);
			}
			objectContainer.Commit();
		}
		// end Evaluate
	}

	public class UpdateMessageRecipient: IMessageRecipient
	{
		public void ProcessMessage(IObjectContainer objectContainer,object message) 
		{
			// message type defines the code to be executed
			if(message.GetType().Equals(typeof(UpdateServer)))
			{
				IQuery q = objectContainer.Query();
				q.Constrain(typeof(Car));
				IObjectSet objectSet = q.Execute();
				while(objectSet.HasNext())
				{
					Car car = (Car)objectSet.Next();
					car.Model ="Updated2-"+ car.Model;
					objectContainer.Set(car);
				}
				objectContainer.Commit();
			}
		}
		// end ProcessMessage
	}
}
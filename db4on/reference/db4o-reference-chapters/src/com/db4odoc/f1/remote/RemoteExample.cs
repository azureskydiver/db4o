/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System.IO;
using com.db4o;
using com.db4o.query;
using com.db4o.messaging;

namespace com.db4odoc.f1.remote 
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
			ObjectContainer db = Db4o.OpenFile(YapFileName);
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
			ObjectServer server=Db4o.OpenServer(YapFileName,0);
			try 
			{
				ObjectContainer client=server.OpenClient();
				Query q = client.Query();
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
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Query q = db.Query();
				q.Constrain(typeof(Car));
				ObjectSet objectSet = q.Execute();
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
			ObjectServer server=Db4o.OpenServer(YapFileName,0);
			// create message handler on the server
			server.Ext().Configure().SetMessageRecipient(
				new UpdateMessageRecipient());
			try 
			{
				ObjectContainer client=server.OpenClient();
				// send message object to the server
				MessageSender sender =	client.Ext().Configure().GetMessageSender();
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

		public static void ListResult(ObjectSet result)
		{
			System.Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				System.Console.WriteLine(item);
			}
		}
		// end ListResult
	}


	public class UpdateEvaluation : Evaluation
	{ 

		public void Evaluate(Candidate candidate) 
		{
			// evaluate method is executed on the server
			// use it to run update code
			ObjectContainer objectContainer = candidate.ObjectContainer();
			Query q2 = objectContainer.Query();
			q2.Constrain(typeof(Car));
			ObjectSet objectSet = q2.Execute();
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

	public class UpdateMessageRecipient: MessageRecipient
	{
		public void ProcessMessage(ObjectContainer objectContainer,object message) 
		{
			// message type defines the code to be executed
			if(message.GetType().Equals(typeof(UpdateServer)))
			{
				Query q = objectContainer.Query();
				q.Constrain(typeof(Car));
				ObjectSet objectSet = q.Execute();
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
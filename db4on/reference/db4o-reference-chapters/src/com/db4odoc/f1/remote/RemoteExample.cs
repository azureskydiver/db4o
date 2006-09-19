/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System.IO;
using com.db4o;
using com.db4o.query;
using com.db4o.messaging;

namespace com.db4odoc.f1.remote 
{
	public class RemoteExample : Util 
	{

		public static void main(string[] args) 
		{
			SetObjects();
			UpdateCars();
			SetObjects();
			UpdateCarsWithMessage();
	
		}

		public static void SetObjects()
		{
			File.Delete(Util.YapFileName);
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
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
	
	
		public static void UpdateCars()
		{
			// triggering mass updates with a singleton
			// complete server-side execution
			ObjectServer server=Db4o.OpenServer(Util.YapFileName,0);
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
	
		private static void CheckCars()
		{
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
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
	
		public static void UpdateCarsWithMessage() 
		{
			ObjectServer server=Db4o.OpenServer(Util.YapFileName,0);
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
	}
}
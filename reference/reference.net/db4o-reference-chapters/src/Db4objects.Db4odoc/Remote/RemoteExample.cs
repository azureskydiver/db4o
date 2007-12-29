/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Messaging;

namespace Db4objects.Db4odoc.Remote
{
	public class RemoteExample 
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args) 
		{
			SetObjects();
			UpdateCars();
			SetObjects();
			UpdateCarsWithMessage();
		}
		// end Main

		private static void SetObjects()
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void UpdateCars()
		{
			// triggering mass updates with a singleton
			// complete server-side execution
			IObjectServer server=Db4oFactory.OpenServer(Db4oFileName,0);
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
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void UpdateCarsWithMessage() 
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // create message handler on the server
            configuration.ClientServer().SetMessageRecipient(
				new UpdateMessageRecipient());
			IObjectServer server=Db4oFactory.OpenServer(configuration, Db4oFileName,0xdb40);
            server.GrantAccess("user", "password");
			try 
			{
				// send message object to the server
                IConfiguration clientConfiguration = Db4oFactory.NewConfiguration();
                IMessageSender sender = clientConfiguration.ClientServer().GetMessageSender();
                IObjectContainer client = Db4oFactory.OpenClient(clientConfiguration, 
                    "localhost", 0xdb40, "user", "password");
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

        private static void ListResult(IObjectSet result)
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
        public void ProcessMessage(IMessageContext context, object message) 
		{
			// message type defines the code to be executed
			if(message.GetType().Equals(typeof(UpdateServer)))
			{
				IQuery q = context.Container.Query();
				q.Constrain(typeof(Car));
				IObjectSet objectSet = q.Execute();
				while(objectSet.HasNext())
				{
					Car car = (Car)objectSet.Next();
					car.Model ="Updated2-"+ car.Model;
                    context.Container.Set(car);
				}
                context.Container.Commit();
			}
		}
		// end ProcessMessage
	}
}
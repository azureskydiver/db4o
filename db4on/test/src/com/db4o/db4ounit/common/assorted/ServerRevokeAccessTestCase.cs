namespace com.db4o.db4ounit.common.assorted
{
	public class ServerRevokeAccessTestCase : Db4oUnit.TestCase
	{
		internal static readonly string FILE = "ServerRevokeAccessTest.yap";

		internal const int SERVER_PORT = unchecked((int)(0xdb42));

		internal static readonly string SERVER_HOSTNAME = "localhost";

		#if !CF_1_0 && !CF_2_0
		public virtual void Test()
		{
			com.db4o.foundation.io.File4.Delete(FILE);
			com.db4o.ObjectServer server = com.db4o.Db4o.OpenServer(FILE, SERVER_PORT);
			try
			{
				string user = "hohohi";
				string password = "hohoho";
				server.GrantAccess(user, password);
				com.db4o.ObjectContainer con = com.db4o.Db4o.OpenClient(SERVER_HOSTNAME, SERVER_PORT
					, user, password);
				Db4oUnit.Assert.IsNotNull(con);
				con.Close();
				server.Ext().RevokeAccess(user);
				Db4oUnit.Assert.Expect(typeof(System.Exception), new _AnonymousInnerClass37(this, 
					user, password));
			}
			finally
			{
				server.Close();
			}
		}
		#endif // !CF_1_0 && !CF_2_0

		private sealed class _AnonymousInnerClass37 : Db4oUnit.CodeBlock
		{
			public _AnonymousInnerClass37(ServerRevokeAccessTestCase _enclosing, string user, 
				string password)
			{
				this._enclosing = _enclosing;
				this.user = user;
				this.password = password;
			}

			public void Run()
			{
				com.db4o.Db4o.OpenClient(com.db4o.db4ounit.common.assorted.ServerRevokeAccessTestCase
					.SERVER_HOSTNAME, com.db4o.db4ounit.common.assorted.ServerRevokeAccessTestCase.SERVER_PORT
					, user, password);
			}

			private readonly ServerRevokeAccessTestCase _enclosing;

			private readonly string user;

			private readonly string password;
		}
	}
}

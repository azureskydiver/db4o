namespace Db4oUnit.Extensions
{
	public interface Db4oFixture
	{
		string GetLabel();

		void Open();

		void Close();

		void Reopen();

		void Clean();

		com.db4o.@internal.LocalObjectContainer FileSession();

		com.db4o.ext.ExtObjectContainer Db();

		com.db4o.config.Configuration Config();

		bool Accept(System.Type clazz);
	}
}

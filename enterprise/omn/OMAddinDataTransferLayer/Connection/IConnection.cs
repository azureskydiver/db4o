using System.Collections;
using OManager.BusinessLayer.Login;
namespace OMAddinDataTransferLayer.Connection
{
	public interface IConnection
	{
        string ConnectToDatabase(RecentQueries currRecentQueries);		
		Hashtable FetchAllStoredClasses();
		Hashtable FetchAllStoredClassesForAssembly();
		Hashtable FetchStoredFields(string nodeName);
		int NoOfClassesInDb();
		long GetFreeSizeOfDb();
		long GetTotalDbSize();
		long DbCreationTime();
		int GetFieldCount(string classname);
		void Closedb();
		bool DbConnectionStatus();
	    bool CheckForClientServer();
	}
}

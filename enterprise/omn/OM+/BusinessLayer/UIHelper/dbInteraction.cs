using System;
using System.Collections;
using System.Collections.Generic;
using Db4objects.Db4o.Reflect;
using OManager.BusinessLayer.QueryManager;
using OManager.DataLayer.ObjectsModification;
using OManager.DataLayer.Modal;
using OManager.DataLayer.QueryParser;
using OManager.DataLayer.Connection;
using OManager.BusinessLayer.Login;
using OManager.DataLayer.PropertyTable;
using System.Windows.Forms;
using OManager.DataLayer.Maintanence;
using OManager.DataLayer.Reflection;
using OME.AdvancedDataGridView;
using OManager.DataLayer.CommonDatalayer;
using OManager.DataLayer.DemoDBCreation;
using OME.Logging.Common;

namespace OManager.BusinessLayer.UIHelper
{
	public class dbInteraction
	{
		DbInformation  dbInfo;
		readonly RenderHierarchy clsRenderHierarchy;      
		ModifyObjects modObj;
		public RunQuery runQuery;

		//user has saved an object. "modify", "delete", "refresh".
		public string strInteraction;

		public dbInteraction()
		{
			dbInfo = new DbInformation(); 
			clsRenderHierarchy = new RenderHierarchy();
		}

		public void EditObject(object parentobjm, string attribute, string value )
		{
			modObj = new ModifyObjects(parentobjm);
			modObj.EditObjects(attribute, value);
		}
		public void DeleteObject(object obj,bool boolcascadeOnDelete)
		{
			ModifyObjects modObj = new ModifyObjects(obj);            
			modObj.cascadeonDelete(boolcascadeOnDelete);  

		}

        public IType GetFieldType(string declaringClassName, string name)
        {
            return new FieldDetails(declaringClassName, name).GetFieldType();
        }

	    public object SaveObjects(object parentobjm)
		{
			ModifyObjects modObj = new ModifyObjects(parentobjm);
			modObj.SaveObjects();

			return parentobjm;
		}

		public void RefreshObject(object obj, int level)
		{
			ModifyObjects modObj = new ModifyObjects(obj);
			modObj.RefreshObjects(level);
		}

		public void UpdateCollection(IList objList, IList<int> offsetList, IList<string> names, IList<IType> type, object value)
		{
			try
			{
				ModifyCollections modColl = new ModifyCollections();

				modColl.EditCollections(objList, offsetList, names, type, value);
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		public  void SetFieldToNull(object obj, string fieldName)
		{
			try
			{
				IReflectClass klass = DataLayerCommon.ReflectClassFor(obj);
				if (klass != null)
				{
					IReflectField field = DataLayerCommon.GetDeclaredField(klass, fieldName);
					if (field == null)
						return;
					
					field.Set(obj, null);
					Db4oClient.Client.Store(obj);
					Db4oClient.Client.Commit();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		public void SaveCollection(object obj, int level)
		{
			ModifyCollections modColl = new ModifyCollections();

			modColl.SaveCollections(obj, level);

		}
		public Hashtable FetchAllStoredClasses()
		{
            
			return dbInfo.GetAllStoredClasses();

		}

		public Hashtable FetchAllStoredClassesForAssembly()
		{

			return dbInfo.GetAllStoredClassesAssemblyWise(); 

		}
		public Hashtable FetchStoredFields(string classname)
		{
			ClassDetails clsDetails = new ClassDetails(classname);
			return clsDetails.GetFields();
		}


		public int NoOfClassesInDb()
		{
           
			return dbInfo.GetNumberOfClassesinDB();
		}

		public long GetFreeSizeOfDb()
		{
         
			return dbInfo.GetFreeSizeofDatabase();
		}

		public long GetTotalDbSize()
		{
          
			return dbInfo.getTotalDatabaseSize();
		}

		public int NoOfObjectsforAClass(string classname)
		{
			ClassDetails db = new ClassDetails(classname);
			return db.GetNumberOfObjects();
		}

		public bool CheckForPrimitiveFields(string classname, string fieldname)
		{
			FieldDetails fl = new FieldDetails(classname, fieldname);
			return fl.IsPrimitive();
		}

		public bool CheckForCollection(string classname, string fieldname)
		{
			FieldDetails fl = new FieldDetails(classname, fieldname);
			return fl.IsCollection();
		}

		public bool CheckForArray(string classname, string fieldname)
		{
			FieldDetails fl = new FieldDetails(classname, fieldname);
			return fl.IsArray();
		}

		public void SetProxyInfo(ProxyAuthentication proxyInfo)
		{
			ProxyAuthenticator proxyAuth = new ProxyAuthenticator();
			proxyAuth.AddProxyInfoToDb(proxyInfo);
		}

		public ProxyAuthentication RetrieveProxyInfo()
		{
			ProxyAuthenticator proxyAuth = new ProxyAuthenticator();
			proxyAuth = proxyAuth.ReturnProxyAuthenticationInfo();
			if (proxyAuth != null)
				return proxyAuth.ProxyAuthObj;
			
			return null;
		}

		public List<string> GetSearchString(ConnParams conn)
		{
			GroupofSearchStrings searchStrings = new GroupofSearchStrings(conn);
			return searchStrings.ReturnStringList();
		}


		public void SaveSearchString(ConnParams conn, SeachString searchString)
		{
			GroupofSearchStrings searchStrings = new GroupofSearchStrings(conn);
			if (searchString.SearchString != string.Empty)
				searchStrings.AddSearchStringToList(searchString);
		}

		public List<FavouriteFolder> GetFavourites(ConnParams conn)
		{
			FavouriteList lstFav = new FavouriteList(conn);
			return lstFav.ReturnFavouritFolderList(); 
		}

		public void SaveFavourite(ConnParams conn, FavouriteFolder FavFolder )
		{
			FavouriteList lstFav = new FavouriteList(conn);
			lstFav.AddFolderToDatabase(FavFolder);
		}
		public void UpdateFavourite(ConnParams conn, FavouriteFolder FavFolder)
		{
			FavouriteList lstFav = new FavouriteList(conn);
			lstFav.RemoveFolderfromDatabase(FavFolder);  
		}

		public void RenameFolderInDatabase(ConnParams conn, FavouriteFolder oldFav,FavouriteFolder newFav)
		{
			FavouriteList lstFav = new FavouriteList(conn);
			lstFav.RenameFolderInDatabase(oldFav, newFav);  
		}

		public FavouriteFolder GetFolderfromDatabaseByFoldername(ConnParams conn, string folderName)
		{
			FavouriteList lstFav = new FavouriteList(conn);
			lstFav = lstFav.FindFolderWithClassesByFolderName(folderName);
			return lstFav.lstFavFolder[0]; 
		}
       

		public void ExpandTreeNode(TreeGridNode node,bool activate)
		{
			if (IsCollection(node.Tag))
				clsRenderHierarchy.ExpandCollectionNode(node);
			else if (IsArray(node.Tag))
				clsRenderHierarchy.ExpandArrayNode(node);
			else if (IsPrimitive(node.Tag))
				return;
			else
				clsRenderHierarchy.ExpandObjectNode(node, activate);
		}

		public bool IsCollection(object expandedObject)
		{
			return DataLayerCommon.IsCollection(expandedObject);
		}

		public bool IsPrimitive(object expandedObject)
		{
			return DataLayerCommon.IsPrimitive(expandedObject);
		}
		public bool CheckForDateTimeOrString(object expandedObject)
		{
			return DataLayerCommon.CheckForDatetimeOrString(expandedObject);
		}
		public object GetObjById(long id)
		{
			ObjectDetails objDetails = new ObjectDetails(null);
			return objDetails.GetObjById(id); 

		}
		public long GetLocalID(object obj)
		{
			ObjectDetails objDetails = new ObjectDetails(obj);
			return objDetails.GetLocalID();
		}
		public int GetDepth(object obj)
		{
			ObjectDetails objDetails = new ObjectDetails(obj);
			return objDetails.GetDepth(obj);
		}

		public bool IsArray(object expandedObject)
		{
			return DataLayerCommon.IsArray(expandedObject);
		}


		public TreeGridView GetObjectHierarchy(object selectedObj,string classname,bool activate )
		{
			return clsRenderHierarchy.ReturnHierarchy(selectedObj, classname, activate);
		}

		public long[] ExecuteQueryResults(OMQuery omQuery)
		{

			runQuery = new RunQuery(omQuery);
			return runQuery.ExecuteQuery();            
            

		}

		public List<Hashtable> ExecuteQueryResults(OMQuery omQuery,PagingData pgData,bool refresh,Hashtable attributeList )
		{

			runQuery  = new RunQuery(omQuery);

			runQuery.ExecuteQuery();
			return ReturnQueryResults(pgData, refresh, omQuery.BaseClass, attributeList);  

		}

		public List<Hashtable> ReturnQueryResults(PagingData pagData ,bool Refresh,string baseclass,Hashtable attributeList)
		{

			return runQuery.ReturnResults(pagData, Refresh, baseclass, attributeList);  
        }

		public bool Cascadeondelete(object obj, bool checkforCascade)
		{
			ModifyObjects m = new ModifyObjects(obj); 
			m.cascadeonDelete(checkforCascade);
			return false;
		}
        
		public bool DefragDatabase(string ConnectionPath)
		{
			db4oDefrag defrag = new db4oDefrag(ConnectionPath);
			bool check = false;
			try
			{
				defrag.db4oDefragDatabase();
			}
			catch (Exception oEx)
			{
				check = true;
				LoggingHelper.HandleException(oEx);
			}

			return check;
		}

		public bool BackUpDatabase(string LocationToBackUp)
		{
			db4oBackup backup = new db4oBackup(LocationToBackUp); 
			bool check = false;
			try
			{
				backup.db4oBackupDatabase();
			}
			catch (Exception oEx)
			{
				check = true;
				LoggingHelper.HandleException(oEx);
				MessageBox.Show(oEx.Message, "ObjectManager Enterprise",MessageBoxButtons.OK,MessageBoxIcon.Error);
			}

			return check;            
		}

		public List<RecentQueries> FetchRecentQueries()
		{
			return Config.Config.Instance.GetRecentQueries();
		}

		public int GetFieldCount(string classname)
		{
			ClassDetails clsDetails = new ClassDetails(classname);
			return clsDetails.GetFieldCount();
         
		}

		public ClassPropertiesTable GetClassProperties(string classname)
		{
			ClassPropertiesTable classtable = new ClassPropertiesTable(classname);            
			return classtable.GetClassProperties();
		}
		public ObjectPropertiesTable GetObjectProperties(object obj)
		{
			ObjectPropertiesTable objtable = new ObjectPropertiesTable(obj);
			return objtable.GetObjectProperties();

		}

		public string ConnectoToDB(RecentQueries recConnection)
		{
			DBConnect db = new DBConnect();
			return db.dbConnection(recConnection.ConnParam);                        

		}

		public void closedb(RecentQueries recConnection)
		{
			if (Db4oClient.RecentConnFile==null)
			{
				Db4oClient.RecentConnFile = Config.Config.OMNConfigDatabasePath();
			}
			SaveRecentConnection(Db4oClient.CurrentRecentConnection);
			Db4oClient.CloseConnection(Db4oClient.Client);
			Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
		}

		public void CloseCurrDb()
		{
			Db4oClient.CloseConnection(Db4oClient.Client);
		}

		public void CloseRecentConn()
		{
			Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
		}

		public void SetIndexedConfiguration(string fieldname, string className, bool isIndexed)
		{
			ClassPropertiesTable classtable = new ClassPropertiesTable(className);
			classtable.SetIndex(fieldname, className, isIndexed);
		}

		public  void ReopenCurrDb()
		{
			string t=Db4oClient.Client.ToString();
           
		}

		public RecentQueries GetCurrentRecentConnection()
		{
			//Db4oClient.RecentConn.Activate(Db4oClient.CurrentRecentConnection, 4);
			return Db4oClient.CurrentRecentConnection;
		}
		public void SetCurrentRecentConnection(RecentQueries conn)
		{
			Db4oClient.CurrentRecentConnection = conn;
		}

		public void SaveRecentConnection(RecentQueries recQueries)
		{
			Config.Config.Instance.SaveRecentConnection(recQueries);
		}

		public bool CreateDemoDb(string demoFilePath)
		{
			try
			{
				DemoDatabaseCreation dbCreationObj = new DemoDatabaseCreation();
				dbCreationObj.CreateDemoDb(demoFilePath);
				return true;

			}
			catch (Exception e)
			{
				return false;
			}
		}
	}
}
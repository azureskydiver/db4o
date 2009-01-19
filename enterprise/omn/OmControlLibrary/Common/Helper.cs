using System;
using System.Text;
using System.Windows.Forms;
using System.Collections;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.IO;
using Microsoft.Win32;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.Login;
using EnvDTE;
using OManager.BusinessLayer.UIHelper;
using sforce;
using System.Net;

using OMControlLibrary.LoginToSalesForce;
using Microsoft.VisualStudio.CommandBars;
using System.Threading;
using System.Data;
using OME.Crypto;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary.Common
{
	public class Helper
	{
		#region Member Variable

		private static dbInteraction m_dbInteraction;
		private static string m_className;
		private static string m_baseClass;
		private static List<OMQuery> m_listOMQueries;
		private static object m_selectedObject;
		private static bool m_isSameOMQuery;
		private static Hashtable m_OMResultedQuery = new Hashtable();
		private static SeatAuthorization m_responseTicket;
		static readonly string m_guidposForClassDetails = Guid.NewGuid().ToString("B");
		static string m_guidposForQueryResult = Guid.NewGuid().ToString("B");
		private static int m_depth;
		private static int m_tabIndex;

		private static Window loginToolWindow;
		private static Window queryResultToolWindow;

		public static Window winSalesPage;
		private static List<Hashtable> m_hashList;
		private static Hashtable m_hashClassGUID;
		private static string m_selectedClass;
		private static bool m_isValidQuery;
		private static Hashtable m_hashTableBaseClass = new Hashtable();
		static System.Threading.Thread SessionThread;
		private static bool checkObjectBrowser;

		public static AccountManagementService serviceProxy;
	    static readonly string m_ddnUsername = string.Empty;
		static readonly string m_ddnPassword = string.Empty;
		static string m_sessionId = string.Empty;
		public static CommandBarButton m_statusLabel;
		private static bool m_IsQueryResultUpdated;

		private static bool m_checkforIfAlreadyLoggedIn;

	    #endregion

		#region Constant

	    private const string PLACEHOLDER_KEY = "<<KEY>>";
		private const string GENERIC_TEXT = "(G) ";
		private const string CLASS_NAME_PROPERTIES = "OMControlLibrary.PropertiesTab";
		private const string RECENT_QUERY_QUERY_COLUMN = "Query";
		private const string RECENT_QUERY_OMQUERY_COLUMN = "OMQuery";
		private const char CONST_COMMA = ',';
		private const char CONST_DOT = '.';
		private const char CONST_TILD = '~';
		private const string CONST_SYSTEM = "System";
		private const string CONST_COLLECTION = "Collections";
		private const string CONST_BACKSLASH = "\\";
		private const string CONST_NULL = "null";
		internal const string STATUS_FULLFUNCTIONALITYMODE = "Connected - All OME functionality available";
		internal const string STATUS_REDUCEDMODELOGGEDIN = "Connected -  OME Functionality is limited";

		internal const string STATUS_LOGGEDOUT = "Not Connected -All Functionality is limited";
		private const string PROXYCONFIG_LOCATION = "under Tools -> ObjectManager Enterprise -> Options -> Proxy Configurations ";

		#endregion

		#region Static Properties

		public static bool CheckForIfAlreadyLoggedIn
		{
			get { return m_checkforIfAlreadyLoggedIn; }
			set { m_checkforIfAlreadyLoggedIn = value; }
		}

		public static bool CheckObjectBrowser
		{
			get { return checkObjectBrowser; }
			set { checkObjectBrowser = value; }
		}

		public static bool IsValidQuery
		{
			get { return m_isValidQuery; }
			set { m_isValidQuery = value; }
		}

		public static Hashtable HashTableBaseClass
		{
			get { return m_hashTableBaseClass; }
			set { m_hashTableBaseClass = value; }
		}

		public static bool IsSameOMQuery
		{
			get { return m_isSameOMQuery; }
			set { m_isSameOMQuery = value; }
		}

		public static SeatAuthorization ResponseTicket
		{
			get { return m_responseTicket; }
			set { m_responseTicket = value; }
		}

		public static Hashtable OMResultedQuery
		{
			get { return m_OMResultedQuery; }
			set { m_OMResultedQuery = value; }
		}

		public static string SelectedClass
		{
			get { return m_selectedClass; }
			set { m_selectedClass = value; }
		}

		public static Hashtable HashClassGUID
		{
			get { return m_hashClassGUID; }
			set { m_hashClassGUID = value; }
		}

		public static List<Hashtable> HashList
		{
			get { return m_hashList; }
			set { m_hashList = value; }
		}

		public static int Tab_index
		{
			get { return m_tabIndex; }
			set { m_tabIndex = value; }
		}

		public static int Depth
		{
			get { return m_depth; }
			set { m_depth = value; }
		}

		public static string GuidposForQueryResult
		{
			get { return m_guidposForQueryResult; }
			set { m_guidposForQueryResult = value; }
		}

		public static Window QueryResultToolWindow
		{
			get { return queryResultToolWindow; }
			set { queryResultToolWindow = value; }
		}

		public static Window LoginToolWindow
		{
			get { return loginToolWindow; }
			set { loginToolWindow = value; }
		}

		/// <summary>
		/// Get the instace of dbInteaction Class
		/// </summary>
		public static dbInteraction DbInteraction
		{
			get
			{
				if (m_dbInteraction == null)
					m_dbInteraction = new dbInteraction();
				return m_dbInteraction;
			}

			set { m_dbInteraction = value; }
		}

		/// <summary>
		/// Get/Set the selected class
		/// </summary>
		public static string ClassName
		{
			get
			{
				if (m_className != null)
				{
					if (m_className.Contains(GENERIC_TEXT))
						m_className = m_className.Replace(GENERIC_TEXT, string.Empty);
				}
				return m_className;
			}
			set { m_className = value; }
		}

		/// <summary>
		/// Get/Set the base class name for selected node. 
		/// </summary>
		public static string BaseClass
		{
			get
			{
				if (m_baseClass != null)
				{
					if (m_baseClass.Contains(GENERIC_TEXT))
						m_baseClass = m_baseClass.Replace(GENERIC_TEXT, string.Empty);
				}
				return m_baseClass;
			}
			set { m_baseClass = value; }
		}

		public static List<OMQuery> ListOMQueries
		{
			get
			{
				RecentQueries recQueries = DbInteraction.GetCurrentRecentConnection();
				if (recQueries != null)
				{
					m_listOMQueries = recQueries.QueryList;
				}
				if (m_listOMQueries != null)
				{
					CompareQueryTimestamps comp = new CompareQueryTimestamps();
					m_listOMQueries.Sort(comp);
				}
				return m_listOMQueries;
			}
			set { m_listOMQueries = value; }
		}

		public static object SelectedObject
		{
			get { return m_selectedObject; }
			set { m_selectedObject = value; }
		}

		public static bool IsQueryResultUpdated
		{
			get { return m_IsQueryResultUpdated; }
			set { m_IsQueryResultUpdated = value; }
		}

		#endregion

		#region Get Resource String
		/// <summary>
		/// 
		/// </summary>
		/// <param name="key"></param>
		/// <returns></returns>
		public static string GetResourceString(string key)
		{
			string resourceValue = string.Empty;
			Dictionary<string, string> m_dictionaryResourceString = new Dictionary<string, string>();
			try
			{
				if (m_dictionaryResourceString.ContainsKey(key))
				{
					resourceValue = m_dictionaryResourceString[key];
				}
				else
				{
					resourceValue = ApplicationManager.LanguageResource.GetString(key);
					m_dictionaryResourceString.Add(key, resourceValue);
				}
			}
			catch (ArgumentNullException objargEx)
			{
				objargEx.ToString();
			}
			catch (Exception objException)
			{
				string strsg = "RESOURCE_KEY_NOT_PRESENT";
				if (strsg.Contains(PLACEHOLDER_KEY))
					strsg = strsg.Replace(PLACEHOLDER_KEY, key);
				objException.ToString();

			}
			return resourceValue;
		}

		#endregion Get Resource String

		#region public static bool RegisterAssembly(Path asmPath)
		/// <summary>
		/// Registers asembly in Registry.
		/// </summary>
		/// <param name="asmPath"></param>
		/// <returns></returns>
		public static bool RegisterAssembly(string asmPath)
		{
			bool isRegistered = false;
			try
			{
				RegistryKey key = Registry.CurrentUser.OpenSubKey("Software\\Microsoft\\.NETFramework\\AssemblyFolders", true);
				RegistryKey newkey = key.CreateSubKey("Db4Objects");
				newkey.SetValue("", asmPath);//@"E:\DB4Objects\Read Material\db4o-6.1\bin\net-2.0"
				isRegistered = true;
			}
			catch
			{
				isRegistered = false;
			}
			return isRegistered;
		}
		#endregion

		#region Listing Helper Methods

		public static string GetConfigFolderPath()
		{
			string filePath = Application.StartupPath;

			try
			{
				int seperatorLastIndex = filePath.LastIndexOf(Path.DirectorySeparatorChar);
				filePath = filePath.Remove(seperatorLastIndex);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

			return filePath;
		}

		// public static void Add

		public static string GetClassGUID(string className)
		{
			if (className.Contains(GENERIC_TEXT))
				className = className.Replace(GENERIC_TEXT, string.Empty);

			string classGUID = Guid.NewGuid().ToString("B");
		    try
			{
				if (m_hashClassGUID == null)
				{
					m_hashClassGUID = new Hashtable();
				}

				bool isPresent = m_hashClassGUID.ContainsKey(className);
				if (isPresent)
				{
					classGUID = (string)m_hashClassGUID[className];
				}
				else
				{
					m_hashClassGUID.Add(className, classGUID);
				}
			}
			catch (Exception)
			{
				classGUID = null;
			}
			return classGUID;
		}

		public static void PopulateRecentQueries(ComboBox comboboxRecentQueries, object obj)
		{
			try
			{
				if (obj is ObjectBrowser)
				{
					PopulateRecentQueryComboBox(ListOMQueries, comboboxRecentQueries);
				}
				else
				{
					QueryResult qres = (QueryResult)obj;
					string className = qres.ClassName;
					List<OMQuery> qrylist = Helper.DbInteraction.GetCurrentRecentConnection().FetchQueriesForAClass(className);
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		public static void PopulateRecentQueryComboBox(List<OMQuery> qrylist, ComboBox comboboxRecentQueries)
		{
			DataTable recentQueriesDatatable;

			try
			{
				recentQueriesDatatable = new DataTable();
				recentQueriesDatatable.Columns.Add(RECENT_QUERY_QUERY_COLUMN, typeof(string));
				recentQueriesDatatable.Columns.Add(RECENT_QUERY_OMQUERY_COLUMN, typeof(OMQuery));

				comboboxRecentQueries.DataSource = null;
				comboboxRecentQueries.Items.Clear();

				DataRow newRow = recentQueriesDatatable.NewRow();
				newRow[0] = GetResourceString(Constants.COMBOBOX_DEFAULT_TEXT);
				newRow[1] = null;

				recentQueriesDatatable.Rows.Add(newRow);

				foreach (OMQuery qry in qrylist)
				{
					if (qry != null)
					{
						newRow = recentQueriesDatatable.NewRow();
						newRow[0] = qry.QueryString;
						newRow[1] = qry;

						recentQueriesDatatable.Rows.Add(newRow);
					}
				}
				comboboxRecentQueries.DisplayMember = RECENT_QUERY_QUERY_COLUMN;
				comboboxRecentQueries.ValueMember = RECENT_QUERY_OMQUERY_COLUMN;

				comboboxRecentQueries.DataSource = recentQueriesDatatable;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		public static string GetTypeOfObject(string nodetype)
		{
			int indexof = nodetype.IndexOf(CONST_COMMA);
			string typeofObject = string.Empty;

			if (indexof != -1)
				typeofObject = nodetype.Substring(0, indexof);
			return typeofObject;
		}

		public static bool OnTreeViewAfterExpand(object sender, TreeViewEventArgs e)
		{
			try
			{
				if (e.Node.Nodes.Count < 1)
					return false;
			    TreeNode treenode = e.Node;

				if (treenode.Nodes[0].Name == Constants.DUMMY_NODE_TEXT)
					treenode.Nodes[Constants.DUMMY_NODE_TEXT].Remove();
				else
					return false;

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		    return true;

		}

		/// <summary>
		/// Check for the primitive data type
		/// </summary>
		/// <param name="type"></param>
		/// <returns></returns>
		public static bool IsPrimitive(string type)
		{
			bool isPrimitive;

			switch (type)
			{
				case OManager.BusinessLayer.Common.BusinessConstants.STRING:
				case OManager.BusinessLayer.Common.BusinessConstants.SINGLE:
				case OManager.BusinessLayer.Common.BusinessConstants.DATETIME:
				case OManager.BusinessLayer.Common.BusinessConstants.BYTE:
				case OManager.BusinessLayer.Common.BusinessConstants.CHAR:
				case OManager.BusinessLayer.Common.BusinessConstants.BOOLEAN:
				case OManager.BusinessLayer.Common.BusinessConstants.DECIMAL:
				case OManager.BusinessLayer.Common.BusinessConstants.DOUBLE:
				case OManager.BusinessLayer.Common.BusinessConstants.INT16:
				case OManager.BusinessLayer.Common.BusinessConstants.INT32:
				case OManager.BusinessLayer.Common.BusinessConstants.INT64:
				case OManager.BusinessLayer.Common.BusinessConstants.INTPTR:
				case OManager.BusinessLayer.Common.BusinessConstants.SBYTE:
				case OManager.BusinessLayer.Common.BusinessConstants.UINT16:
				case OManager.BusinessLayer.Common.BusinessConstants.UINT32:
				case OManager.BusinessLayer.Common.BusinessConstants.UINT64:
				case OManager.BusinessLayer.Common.BusinessConstants.UINTPTR:
				case "":
					isPrimitive = true;
					break;
				default:
					isPrimitive = false;
					break;
			}

			return isPrimitive;
		}

		public static object GetValue(string type, object value)
		{
			object resultValue = false;
			switch (type)
			{
				case OManager.BusinessLayer.Common.BusinessConstants.STRING:
					if (value == null)
						value = string.Empty;
					
                    resultValue = value.ToString() == CONST_NULL ? value.ToString() : Convert.ToString(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.SINGLE:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToSingle(0);
					else
						resultValue = Convert.ToSingle(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.DATETIME:
					if (value != null)
						resultValue = value.ToString();
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.BYTE:

					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToByte(0);
					else
						resultValue = Convert.ToByte(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.CHAR:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToChar(0);
					else
						resultValue = Convert.ToChar(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.BOOLEAN:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = false;
					else
						resultValue = Convert.ToBoolean(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.DECIMAL:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToDecimal(0);
					else
						resultValue = Convert.ToDecimal(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.DOUBLE:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToDouble(0);
					else
						resultValue = Convert.ToDouble(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.INT16:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToInt16(0);
					else
						resultValue = Convert.ToInt16(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.INT32:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToInt32(0);
					else
						resultValue = Convert.ToInt32(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.INT64:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToInt64(0);
					else
						resultValue = Convert.ToInt64(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.INTPTR:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = 0;
					else
						resultValue = value;
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.SBYTE:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToSByte(0);
					else
						resultValue = Convert.ToSByte(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.UINT16:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToUInt16(0);
					else
						resultValue = Convert.ToUInt16(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.UINT32:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToUInt32(0);
					else
						resultValue = Convert.ToUInt32(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.UINT64:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = Convert.ToUInt64(0);
					else
						resultValue = Convert.ToUInt64(value);
					break;
				case OManager.BusinessLayer.Common.BusinessConstants.UINTPTR:
					if (value != null && value.ToString() == CONST_NULL)
						resultValue = value.ToString();
					else
						resultValue = value;
					break;
				default:
					resultValue = value;
					break;
			}

			return resultValue;
		}
		public static string FormulateParentName(TreeNode tempTreeNode, IDictionaryEnumerator eNum)
		{
			string parentName;
			if (tempTreeNode.Parent == null || tempTreeNode.Parent.Tag.ToString() == "Fav Folder")
			{

				parentName = tempTreeNode.Text.Split(',')[0];
				parentName = parentName.Split('.')[1] + '.' + eNum.Key;
			}
			else
			{
				parentName = FormulateCompleteClassPath(tempTreeNode) + eNum.Key;

			}
			return parentName;
		}
		public static string FormulateCompleteClassPath(TreeNode treeNode)
		{
			StringBuilder fullpath = new StringBuilder(string.Empty);
			List<string> stringParent = new List<string>();
			try
			{
				while (treeNode.Parent != null && treeNode.Parent.Tag != null
					&& treeNode.Parent.Tag.ToString() != "Fav Folder" && treeNode.Parent.Tag.ToString() != "Assembly View")
				{

					if (!string.IsNullOrEmpty(treeNode.Text))
						stringParent.Add(treeNode.Text);

					treeNode = treeNode.Parent;

				}
				stringParent.Add((treeNode.Text.Split(',')[0]).Split('.')[1]);

				for (int i = stringParent.Count; i > 0; i--)
				{
					string parent = stringParent[i - 1] + ".";
					fullpath.Append(parent);
				}
			}
			catch (Exception ex)
			{
				LoggingHelper.ShowMessage(ex);
			}
			return fullpath.ToString();

		}

		public static string FindRootNode(TreeNode node)
		{
			try
			{
				if (node != null)
				{
					while (node.Parent != null && node.Parent.Tag != null && node.Parent.Tag.ToString() != "Fav Folder" && node.Parent.Tag.ToString() != "Assembly View")
					{
						node = node.Parent;
					}

					return node.Text;
				}
			}
			catch (Exception ex)
			{
				LoggingHelper.ShowMessage(ex);
			}
			return string.Empty;
		}

		public static bool IsArrayOrCollection(string type)
		{
			bool isArrayOrCollection = false;

			switch (type)
			{
				case OManager.BusinessLayer.Common.BusinessConstants.STRINGARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.SINGLEARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.DATETIMEARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.BYTEARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.CHARARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.BOOLEANARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.DECIMALARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.DOUBLEARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.INT16ARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.INT32ARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.INT64ARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.SBYTEARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.UINT16ARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.UINT32ARRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.UINT64RRAY:
				case OManager.BusinessLayer.Common.BusinessConstants.COLLECTION_ICOLLECTION:
				case OManager.BusinessLayer.Common.BusinessConstants.COLLECTION_ILIST:
				case "":
					isArrayOrCollection = true;
					break;
			}
			if (type.Contains(CONST_COLLECTION))
				isArrayOrCollection = true;

			return isArrayOrCollection;
		}

		public static void ListClassAttributes(dbTreeView dbtreeview, TreeNode tn)
		{
			TreeNode treeNodeNew = null;
			TreeNode treeNodeParent;

			try
			{
			    bool isPrimitiveType = false;

				string treenodeparent = tn.Name;

				if (!OnTreeViewAfterExpand(dbtreeview, null))
					return;

				string nodeName = tn.Name.IndexOf(CONST_COMMA.ToString()) == -1 ? tn.Tag.ToString() : tn.Name;

				Hashtable list = DbInteraction.FetchStoredFields(nodeName);

				dbtreeview.BeginUpdate();

				IDictionaryEnumerator enumerator = list.GetEnumerator();
				while (enumerator.MoveNext())
				{
				    string nodevalue = enumerator.Key.ToString();
					string nodetype = enumerator.Value.ToString();

					if (!string.IsNullOrEmpty(nodevalue))
						treeNodeNew = new TreeNode(nodevalue);

					treeNodeNew.Name = nodevalue;
					treeNodeNew.Tag = nodetype;

					if (string.IsNullOrEmpty(treenodeparent))
						dbtreeview.Nodes.Add(treeNodeNew);
					else
					{
						int indexof = nodetype.IndexOf(CONST_COMMA);
						string typeofObject = string.Empty;

						if (indexof != -1)

							typeofObject = nodetype.Substring(0, indexof);

						else if (!nodetype.StartsWith(CONST_SYSTEM))
						{
							typeofObject = nodetype;
						}
						isPrimitiveType = IsPrimitive(typeofObject);

						treeNodeParent = dbtreeview.Nodes[treenodeparent];
						treeNodeParent.Nodes.Add(treeNodeNew);
					}

					if (!isPrimitiveType)
					{
						ListClassAttributes(dbtreeview, treeNodeNew);
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			finally
			{
				dbtreeview.EndUpdate();
			}
		}

		public static string ReturnAttributeName(string type)
		{
			int intIndex = type.LastIndexOf(CONST_DOT);
			string type1 = type.Substring(intIndex + 1);
			return type1;
		}

		public static bool CheckUniqueNessAttributes(string fullpath, dbDataGridView datagridAttributeList)
		{
			try
			{
				if (datagridAttributeList.Rows.Count > 0)
				{
					for (int i = 0; i < datagridAttributeList.Rows.Count; i++)
					{
						if (fullpath.Equals(datagridAttributeList.Rows[i].Cells[0].Value.ToString()))
							return false;
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

			return true;
		}

		#endregion

		#region Public  Methods

		public static bool IsAlphaNumeric(string strToCheck)
		{
			Regex objAlphaNumericPattern = new Regex(Constants.VALIDATION_REGX_ALPHANUMERIC);
			return !objAlphaNumericPattern.IsMatch(strToCheck);
		}

		public static bool IsNumeric(string strToCheck)
		{
			Regex objAlphaNumericPattern = new Regex(Constants.VALIDATION_REGX_NUMERIC);
			return objAlphaNumericPattern.IsMatch(strToCheck);
		}

		public static void ClearAllCachedAttributes()
		{
			try
			{
				BaseClass = string.Empty;
				checkObjectBrowser = false;
				ClassName = string.Empty;
				DbInteraction = null;
				Depth = 0;
				GuidposForQueryResult = string.Empty;
				if (HashClassGUID != null)
					HashClassGUID.Clear();

				if (HashList != null)
					HashList.Clear();

				if (HashTableBaseClass != null)
					HashTableBaseClass.Clear();

				IsQueryResultUpdated = false;
				IsSameOMQuery = false;
				IsValidQuery = false;

				LoginToolWindow = null;

				if (OMResultedQuery != null)
					OMResultedQuery.Clear();

				QueryResultToolWindow = null;

				if (ListOMQueries != null)
					ListOMQueries.Clear();

				SelectedClass = string.Empty;
				SelectedObject = null;
				Tab_index = 0;
				ListofModifiedObjects.Instance.Clear();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		#endregion

		#region WebService Related Methods

		public static byte[] EncryptPass(string pass)
		{
			try
			{
				CryptoDES objCryptoDES = new CryptoDES();
				objCryptoDES.Initialize();
				string encryptSTR = objCryptoDES.DESSelfEncrypt(pass);
				byte[] contents = StrToByteArray(encryptSTR);
				return contents;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
				return null;
			}

		}
		private static byte[] StrToByteArray(string str)
		{
			System.Text.ASCIIEncoding encoding = new ASCIIEncoding();
			return encoding.GetBytes(str);
		}

		private static string ByteArrayToStr(byte[] array)
		{
			System.Text.ASCIIEncoding encoding = new ASCIIEncoding();
			return encoding.GetString(array);
		}

		public static string DecryptPass(byte[] array)
		{
			CryptoDES objCryptoDES = new CryptoDES();
			objCryptoDES.Initialize();
			string info = ByteArrayToStr(array);
			info = objCryptoDES.DESSelfDecrypt(info);
			return info;
		}


		public static bool ReleaseSeat()
		{
			try
			{
				NetworkCredential cred = (NetworkCredential)serviceProxy.Proxy.Credentials;
				serviceProxy.ReleaseSeat(m_sessionId, Environment.MachineName, cred.Domain + CONST_BACKSLASH + cred.UserName);

				serviceProxy.Logout(m_sessionId);
				serviceProxy.Credentials = null;
				serviceProxy.Proxy = null;
				serviceProxy.CookieContainer = null;
				serviceProxy.Dispose();
				ResponseTicket = null;
				serviceProxy = null;
				m_sessionId = string.Empty;
				AbortSession();
				return true;
			}
			catch (WebException e)
			{
				if (e.Message.Contains("407) Proxy Authentication Required"))
				{
					return HandleProxyException();
				}
				else if (e.Message.Contains("The underlying connection was closed: Could not establish trust relationship for the SSL/TLS secure channel."))
				{
					m_checkforIfAlreadyLoggedIn = false;
					DialogResult dialogRes = MessageBox.Show("Connecting to network has failed due to an expired authentication certificate.  The common cause of this expired certificate is due to time differences between your computer and the network. Please verify your computer's clock settings are correct and then press Retry Or press cancel to continue without logging out ", GetResourceString(Constants.PRODUCT_CAPTION), MessageBoxButtons.RetryCancel, MessageBoxIcon.Exclamation);
					return (dialogRes == DialogResult.Retry) ? ReleaseSeat() : false;
				}
				else if (e.Message.Contains("The remote name could not be resolved") || e.Message.Contains("Unable to connect to the remote server") || e.Message.Contains("The remote server returned an error: (500) Internal Server Error"))
				{

					MessageBox.Show("Error in Network Connection. Please check the proxy configurations " + PROXYCONFIG_LOCATION,
						GetResourceString(Constants.PRODUCT_CAPTION),
						MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
					return false;

				}
				else
				{
					LoggingHelper.ShowMessage(e);
					return false;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
				return false;
			}
		}

		public static void AbortSession()
		{
			try
			{
				if (SessionThread != null)
				{
					SessionThread.Abort();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

	    private static bool CheckProxyAuthetication()
		{
	        try
			{
				OMETrace.WriteFunctionStart();

				GetResponseFromWebservice(m_ddnUsername, m_ddnPassword);

				SessionThread = new System.Threading.Thread(KeepSessionAlive);
				SessionThread.IsBackground = true;
				SessionThread.Start();
				return true;
			}
			catch (WebException e)
			{
				if (e.Message.Contains("407) Proxy Authentication Required"))
				{
					return HandleProxyException();
				}
				
                throw;
			}
			catch (Exception)
			{
				if (SessionThread != null)
					SessionThread.Abort();
				throw;
			}
		}

		private static bool HandleProxyException()
		{
			CustomCookies cookies = new CustomCookies();
			ProxyLogin plogin = new ProxyLogin();
			plogin.Text = "Proxy Login ";
			plogin.buttonLogin.Text = "&Ok";
			plogin.ShowDialog();
			if (plogin.DialogResult == DialogResult.OK)
			{
				dbInteraction dbint = new dbInteraction();
				ProxyAuthentication pAuth = new ProxyAuthentication();
				pAuth.Port = plogin.textBoxPort.Text;
				pAuth.ProxyAddress = plogin.textBoxProxy.Text;
				pAuth.UserName = plogin.textBoxUserID.Text;
				pAuth.PassWord = Helper.EncryptPass(plogin.textBoxPassword.Text);
				dbint.SetProxyInfo(pAuth);

				string username = string.Empty;
				string domain = string.Empty;
				string[] stringArr = pAuth.UserName.Split('\\');
				if (stringArr.Length > 1)
				{
					username = stringArr[1].Replace("\\", "");
					domain = stringArr[0];
				}
				WebProxy selectedProxy = new WebProxy(pAuth.ProxyAddress + ":" + pAuth.Port);
				selectedProxy.Credentials = new NetworkCredential(username, DecryptPass(pAuth.PassWord), domain);
				serviceProxy.Proxy = selectedProxy;
				return CheckProxyAuthetication();
			}
			else
			{
				cookies.SetCookies(null);
				if (SessionThread != null)
					SessionThread.Abort();
				return false;
			}
		}

		private static void KeepSessionAlive()
		{
			try
			{
				OMETrace.WriteFunctionStart();

				while (true)
				{
					serviceProxy.SeatKeepAlive(m_sessionId);
					System.Threading.Thread.Sleep(3600000);
				}
			}
			catch (ThreadAbortException ex)
			{
				System.Threading.Thread.ResetAbort();
				OMETrace.WriteLine(ex.Message);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

			OMETrace.WriteFunctionEnd();
		}

		private static void GetResponseFromWebservice(string ddnUsername, string ddnPassword)
		{
		    WebProxy selectedProxy;
		    dbInteraction dbInt = new dbInteraction();
		    ProxyAuthentication proxy = dbInt.RetrieveProxyInfo();
		    if (proxy != null)
		    {
		        string[] stringArr = proxy.UserName.Split('\\');
		        string username = string.Empty;
		        string domain = string.Empty;
		        if (stringArr.Length > 1)
		        {
		            username = stringArr[1].Replace("\\", "");

		            domain = stringArr[0];
		        }
		        selectedProxy = new WebProxy(proxy.ProxyAddress + ":" + proxy.Port);
		        selectedProxy.Credentials = new NetworkCredential(username, DecryptPass(proxy.PassWord), domain);
		    }
		    else
		    {
		        selectedProxy = (WebProxy)GlobalProxySelection.Select;
		        selectedProxy.UseDefaultCredentials = true;
		    }

		    serviceProxy.Proxy = selectedProxy;

		    serviceProxy.CookieContainer = new CookieContainer();
		    m_sessionId = serviceProxy.Login(ddnUsername, ddnPassword);

		    SeatAuthorization response = serviceProxy.ReserveSeat();
		    ResponseTicket = response;

		    // Response ticket is null for invalid credentials
		    if (ResponseTicket == null)
		    {
		        throw new Exception(GetResourceString(Constants.VALIDATION_MSG_INVALID_CREDENTIALS));
		    }

		    LoginNotification();
		}

		public static void LoginNotification()
		{
			LoginNotice str = serviceProxy.GetLoginNotice();
			if (str != null)
			{
				MessageBox.Show(str.Message,
					GetResourceString(Constants.PRODUCT_CAPTION),
					MessageBoxButtons.OK,
					MessageBoxIcon.Information);
			}
		}
		#endregion

		internal static string GetFullPath(TreeNode treenode)
		{
			StringBuilder fullpath = new StringBuilder(string.Empty);
			TreeNode treenodeParent;
			List<string> stringParent = new List<string>();
			string parentName;

			try
			{
				OMETrace.WriteFunctionStart();

				treenodeParent = treenode.Parent;
				while (treenodeParent != null && treenodeParent.Tag.ToString() != "Fav Folder" && treenodeParent.Tag.ToString() != "Assembly View")
				{
					if (treenodeParent.Name.LastIndexOf(",") != -1)
					{
						char[] splitChar = { ',' };
						//Set the base class name for selected field
						BaseClass = treenodeParent.Name;

						//get parent name from node text
						parentName = treenodeParent.Name.Split(splitChar)[0];


					    int classIndex = parentName.LastIndexOf('.');

						//get the parent name of selected node
						parentName = parentName.Substring(classIndex + 1, parentName.Length - classIndex - 1);
					}
					else if (treenodeParent.Tag != null) //get name of patent in class view
						if (treenodeParent.Name != "")
							parentName = treenodeParent.Name;
						else
							parentName = treenodeParent.Text;
					else
						parentName = string.Empty;

					if (!string.IsNullOrEmpty(parentName))
						stringParent.Add(parentName);

					if (treenodeParent.Parent != null)
					{
						treenodeParent = treenodeParent.Parent;
					}
					else
						break;
				}

				//Prepare fullpath of the selected node
				for (int i = stringParent.Count; i > 0; i--)
				{
					string parent = stringParent[i - 1] + ".";
					fullpath.Append(parent);
				}

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			return fullpath.Append(treenode.Name).ToString();
		}
	}
}

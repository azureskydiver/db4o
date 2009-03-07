using System;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Windows.Forms;
using System.Collections;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.IO;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.Login;
using EnvDTE;
using OManager.BusinessLayer.UIHelper;
using sforce;
using Microsoft.VisualStudio.CommandBars;
using System.Data;
using OME.Crypto;
using OME.Logging.Common;
using OME.Logging.Tracing;
using stdole;

namespace OMControlLibrary.Common
{
	public class Helper
	{
		#region Member Variable

		private static dbInteraction m_dbInteraction;
		private static string m_className;
		private static string m_baseClass;
		private static List<OMQuery> m_listOMQueries;
		private static Hashtable m_OMResultedQuery = new Hashtable();
		private static int m_tabIndex;

		private static Window loginToolWindow;
		private static Window queryResultToolWindow;

		public static Window winSalesPage;
		private static List<Hashtable> m_hashList;
		private static Hashtable m_hashClassGUID;
		private static Hashtable m_hashTableBaseClass = new Hashtable();
		static System.Threading.Thread SessionThread;
		private static bool checkObjectBrowser;

		public static AccountManagementService serviceProxy;
		public static CommandBarButton m_statusLabel;

		#endregion

		#region Constant

		private const string GENERIC_TEXT = "(G) ";
		private const string RECENT_QUERY_QUERY_COLUMN = "Query";
		private const string RECENT_QUERY_OMQUERY_COLUMN = "OMQuery";
		private const char CONST_COMMA = ',';
		private const char CONST_DOT = '.';
		private const string CONST_COLLECTION = "Collections";
		internal const string STATUS_FULLFUNCTIONALITYMODE = "Connected - All OME functionality available";
		internal const string STATUS_REDUCEDMODELOGGEDIN = "Connected -  OME Functionality is limited";

		internal const string STATUS_LOGGEDOUT = "Not Connected -All Functionality is limited";

		#endregion

		#region Static Properties

		public static bool CheckObjectBrowser
		{
			get { return checkObjectBrowser; }
		}

		public static Hashtable HashTableBaseClass
		{
			get { return m_hashTableBaseClass; }
			set { m_hashTableBaseClass = value; }
		}

		public static Hashtable OMResultedQuery
		{
			get { return m_OMResultedQuery; }
			set { m_OMResultedQuery = value; }
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
				RecentQueries recQueries = dbInteraction.GetCurrentRecentConnection();
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

		#endregion

		public static string GetResourceString(string key)
		{
			try
			{
				return ApplicationManager.ResourceManager.GetString(key);
			}
			catch (ArgumentNullException objargEx)
			{
				objargEx.ToString();
			}
			return string.Empty;
		}

		public static StdPicture GetResourceImage(string key)
		{
			try
			{
				using (Stream imageStream = Assembly.GetExecutingAssembly().GetManifestResourceStream(key))
				{
					return (StdPicture)MyHost.IPictureDisp(Image.FromStream(imageStream));
				}
			}
			catch
			{
				return null;
			}
		}



		#region Listing Helper Methods

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
			TreeNode treenode = e.Node;
			if (treenode.Nodes.Count >= 1 && treenode.Nodes[0].Name == Constants.DUMMY_NODE_TEXT)
			{
				treenode.Nodes[Constants.DUMMY_NODE_TEXT].Remove();
				return true;
			}
			
			return false;
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
				if (HashClassGUID != null)
					HashClassGUID.Clear();

				if (HashList != null)
					HashList.Clear();

				if (HashTableBaseClass != null)
					HashTableBaseClass.Clear();

				LoginToolWindow = null;

				if (OMResultedQuery != null)
					OMResultedQuery.Clear();

				QueryResultToolWindow = null;

				if (ListOMQueries != null)
					ListOMQueries.Clear();

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
						parentName = treenodeParent.Name != "" ? treenodeParent.Name : treenodeParent.Text;
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

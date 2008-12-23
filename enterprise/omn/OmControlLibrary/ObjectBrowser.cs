using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows.Forms;
using EnvDTE;
using EnvDTE80;
using System.Reflection;
using OMControlLibrary.Common;
using OManager.BusinessLayer.Login;
using OManager.BusinessLayer.QueryManager;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary
{
	public partial class ObjectBrowser : ViewBase
	{
		#region Private Member Variables

		string typeOfNode = string.Empty;
		RecentQueries recConnection = null;
		dbInteraction dbInteractionObject = null;
		dbTreeView dbAssemblyTreeView = null;

		internal dbTreeView DbAssemblyTreeView
		{
			get { return dbAssemblyTreeView; }
			set { dbAssemblyTreeView = value; }
		}
		Hashtable storedclasses = null;
		Hashtable storedAssemblies = null;
		private static int classCount = 0;
		string filterString = string.Empty;

		//internal
		internal OMQuery omQuery = null;
		internal Hashtable listQueryAttributes = null;
		internal List<string> listSearchStrings = null;

		//Controls
		QueryBuilder queryBuilder = null;
		PropertiesTab propertiesTab = null;

		//Constants
		private const char CONST_COMMA_CHAR = ',';
		private const char CONST_DOT_CHAR = '.';
		private const char CONST_BACK_SLASH_CHAR = '\\';
		private const string CONST_COMMA_STRING = ",";
		private const string CONST_DOT_STRING = ".";
		private const string CONST_BACK_SLASH_STRING = "\\";
		private const string CONST_FILTER_DEFAULT_STRING = "<Search>";

		#endregion

		#region Singleton for Object Browser
		private static ObjectBrowser instance;

		public static ObjectBrowser Instance
		{
			get
			{
				if (instance == null)
				{
					instance = new ObjectBrowser();
				}
				return instance;
			}
		}
		#endregion


		#region Public Properties
		#endregion

		#region Constructor
		public ObjectBrowser()
		{
			this.SetStyle(ControlStyles.CacheText |
			   ControlStyles.OptimizedDoubleBuffer, true);

			InitializeComponent();
			SetLiterals();


		}
		#endregion

		#region Override Methods
		/// <summary>
		/// A method from ViewBase class overriden for setting the text to all the labels.
		/// </summary>
		public override void SetLiterals()
		{
			try
			{
				toolStripComboBoxFilter.ToolTipText = Helper.GetResourceString(Common.Constants.OBJBROWSER_FIND_TEXT);
				toolStripButtonClear.ToolTipText = Helper.GetResourceString(Common.Constants.TOOLTIP_OBJECTBROWSER_CLEARSEARCH);
				toolStripButtonAssemblyView.ToolTipText = Helper.GetResourceString(Common.Constants.TOOLTIP_OBJECTBROWSER_ASSEMBLY_VIEW);
				toolStripButtonFlatView.ToolTipText = Helper.GetResourceString(Common.Constants.TOOLTIP_OBJECTBROWSER_CLASS_VIEW);
				toolStripButtonFilter.ToolTipText = Helper.GetResourceString(Common.Constants.TOOLTIP_OBJECTBROWSER_FILTER);
				toolStripButtonPrevious.ToolTipText = Helper.GetResourceString(Common.Constants.TOOLTIP_OBJECTBROWSER_FILTER_PREV);
				toolStripButtonNext.ToolTipText = Helper.GetResourceString(Common.Constants.TOOLTIP_OBJECTBROWSER_FILTER_NEXT);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Events

		//Object Browser Event Handler
		private void ObjectBrowser_Load(object sender, EventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();
				ObjectBrowser.CheckForIllegalCrossThreadCalls = false;
				this.SetLiterals();
				Helper.ClassName = null;
				dbInteractionObject = new dbInteraction();
				//Fetch All stored classes 

				storedclasses = dbInteractionObject.FetchAllStoredClasses();

				if (storedclasses != null)
					classCount = storedclasses.Count;

				InitializeAssemblyTreeView();

				//Set Treeview Image List
				dbtreeviewObject.SetTreeViewImages();

				//Populate the TreeView
				dbtreeviewObject.AddFavouritFolderFromDatabase();
				dbtreeviewObject.AddTreeNode(storedclasses, null);

				//Set Images for treeView
				SetObjectBrowserImages();

				//Set the Selected Class
				if (dbtreeviewObject.Nodes.Count > 0)
				{
					TreeNode node = dbtreeviewObject.Nodes[0];
					while (node != null && node.Tag != null && node.Tag.ToString() == "Fav Folder")
					{
						node = node.NextNode;

					}
					if (node != null)
						SetClassName(new TreeViewEventArgs(node));
				}
				//Register contextmenu event for class treeview
				dbtreeviewObject.OnContextMenuItemClicked += new EventHandler<DBContextItemClickedEventArg>(TreeView_OnContextMenuItemClicked);
				dbtreeviewObject.MouseDown += new MouseEventHandler(TreeView_MouseDown);
				propertiesTab = PropertiesTab.Instance;
				if (classCount == 0)
				{
					propertiesTab.ShowClassProperties = false;
					toolStripButtonAssemblyView.Enabled =
						toolStripButtonFlatView.Enabled = false;
				}
				recConnection = dbInteractionObject.GetCurrentRecentConnection();
				listSearchStrings = dbInteractionObject.GetSearchString(recConnection.ConnParam);
				FillFilterComboBox(listSearchStrings);
				toolStripComboBoxFilter.SelectedIndex = 0;
				dbtreeviewObject.Focus();
				OMETrace.WriteFunctionEnd();
				toolStripButtonFlatView.Checked = true;
				instance = this;



			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


		#region WindowEvents
		void _windowsEvents_WindowActivated(Window GotFocus, Window LostFocus)
		{
			if (LostFocus.Caption == "Closed")
				return;
			if (GotFocus.Caption != "Query Builder" && GotFocus.Caption != "db4o Browser" && GotFocus.Caption != "DataBase Properties" && GotFocus.Caption != "")
			{
				PropertiesTab.Instance.ShowObjectPropertiesTab = false;
			}
			else
			{
				if (toolStripButtonAssemblyView.Checked)
				{
					dbAssemblyTreeView.FindTreeNodesAssemblyView(storedAssemblies, GotFocus.Caption);
				}
				else
				{
					dbtreeviewObject.FindTreeNodesClasses(storedclasses, null, GotFocus.Caption);
				}
				SetObjectBrowserImages();
			}


		}
		#endregion

		/// <summary>
		/// Clear Search funtionality
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void buttonClear_Click(object sender, EventArgs e)
		{
			ClearSearch();
		}

		/// <summary>
		/// Event track the selection of the treeview items
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void dbtreeviewObject_AfterSelect(object sender, TreeViewEventArgs e)
		{
			// string nodeName = string.Empty;
			try
			{
				dbTreeView dbTreeviewObj = sender as dbTreeView;
				OMETrace.WriteFunctionStart();

				//if (e.Node.Name.LastIndexOf(CONST_COMMA_CHAR) == -1 && e.Node.Tag != null)
				//    nodeName = e.Node.Tag.ToString();
				//else
				//{
				//    nodeName = e.Node.Name;
				//}

				//Set the class name to get the result 
				SetClassName(e);

				Helper.SelectedObject = null;

				//Refresh Properties Pane for selected class
				propertiesTab = PropertiesTab.Instance;
				propertiesTab.ShowObjectPropertiesTab = false;
				if (classCount == 0 || (dbTreeviewObj.SelectedNode != null && dbTreeviewObj.SelectedNode.Tag != null && (dbTreeviewObj.SelectedNode.Tag.ToString() == "Fav Folder" || dbTreeviewObj.SelectedNode.Tag.ToString() == "Assembly View")))
					propertiesTab.ShowClassProperties = false;
				else
					propertiesTab.ShowClassProperties = true;

				propertiesTab.RefreshPropertiesTab(null);

				//Heightlight the selected node
				//To do change the code

				((dbTreeView)sender).UpdateTreeNodeSelection(e.Node, toolStripButtonAssemblyView.Checked);



				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		//TreeView Event Handlers
		/// <summary>
		/// Event handles the expand event and get the child objects for expanded node
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void dbtreeviewObject_AfterExpand(object sender, TreeViewEventArgs e)
		{
			Hashtable storedfields = null;

			string nodeName = string.Empty;

			try
			{
				OMETrace.WriteFunctionStart();

				//After adding child nodes dont add again
				if (((TreeNode)e.Node).Parent != null && ((TreeNode)e.Node).Parent.Tag != null && ((TreeNode)e.Node).Parent.Tag.ToString() == "Fav Folder")
				{
					((TreeNode)e.Node).TreeView.SelectedNode = e.Node;
				}
				if (!Helper.OnTreeViewAfterExpand(sender, e))
					return;

				//Get the name of selected item with the namespace
				if (e.Node.Name.LastIndexOf(CONST_COMMA_CHAR) == -1)
					nodeName = e.Node.Tag.ToString();
				else
					nodeName = e.Node.Name;

				storedfields = Helper.DbInteraction.FetchStoredFields(nodeName);

				dbtreeviewObject.AddTreeNode(storedfields, e.Node);

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			finally
			{
				SetClassName(e);
			}
		}

		/// <summary>
		/// After Collapse set the class name for collapsed tree node
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void dbtreeviewObject_AfterCollapse(object sender, TreeViewEventArgs e)
		{
			try
			{
				if (Helper.ClassName != null)
				{
					if (!Helper.ClassName.Equals(e.Node.FullPath.Split('\\')[0]))
					{
						SetClassName(e);
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Excecutes the query
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void buttonRunQuery_Click(object sender, EventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();
				ExecuteQuery(sender, e);
				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void ExecuteQuery(object sender, EventArgs e)
		{
			try
			{

				//objectBrowser_OnRunQueryClick(sender, e);


				if (Helper.IsValidQuery)
				{

					if (Helper.HashList.Count > 0)
					{
						// Remove base class from helper base class hashtable
						// so that next time it will set for the new class
						//if (Helper.BaseClass != null)
						//Helper.HashTableBaseClass.Clear();

						CreateQueryResultToolWindow();

						//Clear all expression/query group after running the query each time.
						//queryBuilder = QueryBuilder.Instance;
						//queryBuilder.ClearAllQueries();
					}
					else
					{
						MessageBox.Show(Helper.GetResourceString(Common.Constants.OBJECTMANAGER_MSG_RESULT_NOT_FOUND),
										Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION),
										MessageBoxButtons.OK,
										MessageBoxIcon.Information);
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}
		private void listBoxAttributes_DragOver(object sender, DragEventArgs e)
		{
			e.Effect = DragDropEffects.Move;
		}

		private void toolStripComboBoxFilter_KeyDown(object sender, KeyEventArgs e)
		{
			if (toolStripComboBoxFilter.Text.Contains(CONST_FILTER_DEFAULT_STRING) || toolStripComboBoxFilter.SelectedText.Contains(CONST_FILTER_DEFAULT_STRING))
			{
				e.Handled = true;
				toolStripComboBoxFilter.SelectAll();
				toolStripComboBoxFilter.Text = string.Empty;
				return;
			}

			if (e.KeyCode == Keys.Enter)
				toolStripButtonFilter_Click(sender, e);
		}

		private void toolStripComboBoxFilter_Click(object sender, EventArgs e)
		{
			try
			{
				if (toolStripComboBoxFilter.SelectedIndex <= 0)
				{
					if (toolStripComboBoxFilter.SelectedText == CONST_FILTER_DEFAULT_STRING ||
						toolStripComboBoxFilter.Text == CONST_FILTER_DEFAULT_STRING)
						toolStripComboBoxFilter.Text = string.Empty;

				}

				filterString = toolStripComboBoxFilter.Text;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void toolStripComboBoxFilter_SelectedIndexChanged(object sender, EventArgs e)
		{
			try
			{
				if (toolStripComboBoxFilter.SelectedIndex <= 0)
				{
					if (toolStripComboBoxFilter.SelectedText == CONST_FILTER_DEFAULT_STRING ||
						toolStripComboBoxFilter.Text == CONST_FILTER_DEFAULT_STRING || toolStripComboBoxFilter.SelectedText.Contains(CONST_FILTER_DEFAULT_STRING))
						ClearSearch();
					toolStripComboBoxFilter.Text = CONST_FILTER_DEFAULT_STRING;
				}
				else
				{
					if (toolStripComboBoxFilter.SelectedIndex == listSearchStrings.Count)
					{
						toolStripButtonPrevious.Enabled = true;
						toolStripButtonNext.Enabled = false;
					}
					else if (toolStripComboBoxFilter.SelectedIndex > 1 && toolStripComboBoxFilter.SelectedIndex < listSearchStrings.Count - 1)
					{
						toolStripButtonPrevious.Enabled =
						   toolStripButtonNext.Enabled = true;
					}
					else if (toolStripComboBoxFilter.SelectedIndex == 1)
					{
						toolStripButtonNext.Enabled = true;
						toolStripButtonPrevious.Enabled = false;
					}
				}

				filterString = toolStripComboBoxFilter.Text;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void toolStripButtonFilter_Click(object sender, EventArgs e)
		{
			try
			{
				if (toolStripComboBoxFilter.SelectedIndex == 0 || toolStripComboBoxFilter.SelectedText == CONST_FILTER_DEFAULT_STRING
					|| toolStripComboBoxFilter.Text == CONST_FILTER_DEFAULT_STRING || toolStripComboBoxFilter.SelectedText.Contains(CONST_FILTER_DEFAULT_STRING))
					return;

				filterString = toolStripComboBoxFilter.Text.Trim().ToLower();

				//Check which View is selected for listing of classes, call find method for relavent view
				if (toolStripButtonAssemblyView.Checked)
				{
					dbAssemblyTreeView.FindTreeNodesAssemblyView(storedAssemblies, filterString);
				}
				else
				{
					dbtreeviewObject.FindTreeNodesClasses(storedclasses, null, filterString);
				}

				SeachString searchString = new SeachString(DateTime.Now, toolStripComboBoxFilter.Text.Trim());
				dbInteractionObject.SaveSearchString(recConnection.ConnParam, searchString);

				SetObjectBrowserImages();

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			finally
			{
				listSearchStrings = dbInteractionObject.GetSearchString(recConnection.ConnParam);
				FillFilterComboBox(listSearchStrings);
				if (toolStripComboBoxFilter.Text != "")
					EnableDisablePrevNextButtons();
				else
					DisablePrevNextButtons();
			}
		}

		private void DisablePrevNextButtons()
		{
			toolStripButtonNext.Enabled = false;
			toolStripButtonPrevious.Enabled = false;
		}

		private void EnableDisablePrevNextButtons()
		{
			if (toolStripComboBoxFilter.Items[toolStripComboBoxFilter.Items.Count - 1].ToString() != toolStripComboBoxFilter.Text)
			{
				toolStripButtonNext.Enabled = true;
				toolStripButtonPrevious.Enabled = false;
			}
			else
			{
				toolStripButtonNext.Enabled = false;
				toolStripButtonPrevious.Enabled = true;
			}

		}

		private void toolStripComboBoxFilter_TextChanged(object sender, EventArgs e)
		{
			try
			{
				if (toolStripComboBoxFilter.Text.Contains(CONST_FILTER_DEFAULT_STRING))
					return;
				if (string.IsNullOrEmpty(toolStripComboBoxFilter.Text.Trim()))
				{
					ClearSearch();
					return;
				}
				toolStripButtonClear.Enabled = true;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void toolStripComboBoxFilter_KeyPress(object sender, KeyPressEventArgs e)
		{
			if (toolStripComboBoxFilter.Text.Contains(CONST_FILTER_DEFAULT_STRING) ||
				toolStripComboBoxFilter.SelectedText.Contains(CONST_FILTER_DEFAULT_STRING))
			{
				e.Handled = true;
				toolStripComboBoxFilter.SelectAll();
				toolStripComboBoxFilter.Text = string.Empty;
				return;
			}
		}

		private void toolStripButtonClear_Click(object sender, EventArgs e)
		{
			ClearSearch();
		}

		private void toolStripButtonAssemblyView_Click(object sender, EventArgs e)
		{
			try
			{
				//if (toolStripButtonFlatView.Checked)
				//    toolStripButtonFlatView.Checked = false;
				//else
				//    toolStripButtonFlatView.Checked = true;
				if (toolStripButtonAssemblyView.Checked == false)
				{
					toolStripButtonAssemblyView.Checked = true;
					toolStripButtonFlatView.Checked = false;
					ShowAssemblyTreeView();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		private void toolStripButtonFlatView_Click(object sender, EventArgs e)
		{
			try
			{

				if (toolStripButtonFlatView.Checked == false)
				{
					toolStripButtonFlatView.Checked = true;
					toolStripButtonAssemblyView.Checked = false;
					dbtreeviewObject.Nodes.Clear();
					dbtreeviewObject.AddFavouritFolderFromDatabase();
					ShowAssemblyTreeView();

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		private void toolStripTextBoxFind_KeyPress(object sender, KeyPressEventArgs e)
		{
			try
			{
				char c = e.KeyChar;

				//Allow only alphanumeric charaters in filter textbox.
				if (!Helper.IsAlphaNumeric(c.ToString()))
					e.Handled = true;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void TreeView_OnContextMenuItemClicked(object sender, DBContextItemClickedEventArg e)
		{

			try
			{
				queryBuilder = QueryBuilder.Instance;
				dbInteraction dbI = new dbInteraction();
				FavouriteFolder Fav = null;
				switch (e.Tag.ToString())
				{
					case Common.Constants.CONTEXT_MENU_SHOW_ALL_OBJECTS:
						ShowAllObjects();
						break;
					case Common.Constants.CONTEXT_MENU_ADD_TO_ATTRIBUTE:
						if (queryBuilder != null)
						{
							AddToAttributeList(queryBuilder.DataGridViewAttributes, (TreeNode)e.Data);

							ApplicationObject.ToolWindows.DTE.Windows.Item("Query Builder").Activate();
						}
						break;
					case Common.Constants.CONTEXT_MENU_EXPRESSION_GROUP:

						string itmName = ((ToolStripItem)e.Item).Name;
						char[] splitChar = { '_' };
						int lastIndex = itmName.LastIndexOf("_");
						int index = Convert.ToInt32(itmName.Split(splitChar, itmName.Length - lastIndex)[1].Split(splitChar)[1]);


						DataGridViewGroup dgvGroup = (DataGridViewGroup)queryBuilder.TableLayoutPanelQueries.Controls[index];

						dbDataGridView datagridObject = dgvGroup.DataGridViewQuery;
						bool rowAdded = datagridObject.AddToQueryBuilder((TreeNode)e.Data, queryBuilder);
						if (rowAdded)
						{
							queryBuilder = QueryBuilder.Instance;
							queryBuilder.EnableRunQuery = true;
						}

						ApplicationObject.ToolWindows.DTE.Windows.Item("Query Builder").Activate();
						break;
					case "Rename":
						dbtreeviewObject.LabelEdit = true;
						dbtreeviewObject.SelectedNode.BeginEdit();
						break;
					case "Delete Folder":

						Fav = new FavouriteFolder(null, dbtreeviewObject.SelectedNode.Text);
						dbI.UpdateFavourite(dbI.GetCurrentRecentConnection().ConnParam, Fav);
						dbtreeviewObject.Nodes.Remove(dbtreeviewObject.SelectedNode);
						break;
					case "Delete Class":
						TreeNode tNode = dbtreeviewObject.SelectedNode;
						TreeNode parentNode = dbtreeviewObject.SelectedNode.Parent;
						if (tNode != null && parentNode != null)
						{

							Fav = new FavouriteFolder(null, tNode.Parent.Text);

							parentNode.Nodes.Remove(tNode);
							if (parentNode.Nodes.Count > 0)
							{
								List<string> lststr = new List<string>();
								foreach (TreeNode tempNode in parentNode.Nodes)
								{
									lststr.Add(tempNode.Text);
								}

								Fav.ListClass = lststr;
							}

							dbI.SaveFavourite(dbI.GetCurrentRecentConnection().ConnParam, Fav);
						}
						break;
					default:
						break;

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		internal void AddToAttributeList(dbDataGridView datagridAttributeList, TreeNode tempTreeNode)
		{
			string className = string.Empty;

			try
			{
				//If field is not selected and Query Group has no clauses then reset the base class.
				if (datagridAttributeList.Rows.Count == 0)
				{
					queryBuilder = QueryBuilder.Instance;
					queryBuilder.CheckForDataGridViewQueryRows();
				}

				//Get the full path of the selected item
				//ObjectBrowser obj = new ObjectBrowser();
				string fullpath = Helper.GetFullPath(tempTreeNode);

				if (tempTreeNode.Parent != null)
				{
					if (tempTreeNode.Parent.Tag.ToString().Contains(CONST_COMMA_STRING))
						className = tempTreeNode.Parent.Tag.ToString();
					else
						className = tempTreeNode.Parent.Name.ToString();
				}

				//Check if dragged item is from same class or not, if not then dont allow to drag item
				if (Helper.HashTableBaseClass.Count > 0)
					if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
						return;

				//Check whether attributes is allready added in list, if yes dont allow to added again. 
				if (!Helper.CheckUniqueNessAttributes(fullpath, datagridAttributeList))
					return;

				//Add a new row and assing required values.
				datagridAttributeList.Rows.Add(1);
				int index = datagridAttributeList.Rows.Count - 1;
				datagridAttributeList.Rows[index].Cells[0].Value = fullpath;
				datagridAttributeList.Rows[index].Cells[0].Tag = className;

				datagridAttributeList.ClearSelection();
				datagridAttributeList.Rows[index].Cells[0].Selected = true;

				if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
					Helper.HashTableBaseClass.Add(Helper.BaseClass, string.Empty);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		void TreeView_MouseDown(object sender, MouseEventArgs e)
		{
			try
			{
				if (e.Button == MouseButtons.Right)
				{


					TreeNode treenode = null;
					queryBuilder = QueryBuilder.Instance;
					List<string> list = null;
					if (dbtreeviewObject != null && dbtreeviewObject.Visible)
					{
						treenode = dbtreeviewObject.GetNodeAt(e.X, e.Y);
						if (treenode.Parent != null && treenode.Parent.Tag.ToString() == "Fav Folder")
						{
							dbtreeviewObject.BuildContextMenu(null, true, true);
						}
						else if (treenode.Parent != null && treenode.Parent.Tag.ToString() != "Fav Folder")
						{
							list = queryBuilder.GetAllQueryGroups();
							dbtreeviewObject.BuildContextMenu(list, true, true);
						}
						else if (treenode.Parent == null && treenode.Tag.ToString() != "Fav Folder")
						{
							dbtreeviewObject.BuildContextMenu(null, true, false);
						}
					}
					else if (dbAssemblyTreeView != null && dbAssemblyTreeView.Visible)
					{
						treenode = dbAssemblyTreeView.GetNodeAt(e.X, e.Y);
						if (treenode.Tag != null && treenode.Tag.ToString() != "Assembly View")
						{
							if (treenode.Parent != null && treenode.Parent.Tag != null && treenode.Parent.Tag.ToString() == "Assembly View")
							{
								dbAssemblyTreeView.BuildContextMenu(null, true, false);
							}
							else
							{
								list = queryBuilder.GetAllQueryGroups();
								dbAssemblyTreeView.BuildContextMenu(list, true, false);
							}
						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void toolStripButtonPrevFilter_Click(object sender, EventArgs e)
		{
			try
			{
				if (listSearchStrings == null || (listSearchStrings != null && listSearchStrings.Count == 0))
					return;

				if (toolStripComboBoxFilter.SelectedIndex > 1)
				{
					toolStripComboBoxFilter.Text = listSearchStrings[toolStripComboBoxFilter.SelectedIndex - 2];
					toolStripButtonNext.Enabled = true;
					if (toolStripComboBoxFilter.SelectedIndex == 1)
						toolStripButtonPrevious.Enabled = false;
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void toolStripButtonNextFilter_Click(object sender, EventArgs e)
		{
			try
			{
				if (listSearchStrings == null || (listSearchStrings != null && listSearchStrings.Count == 0))
					return;
				if (toolStripComboBoxFilter.SelectedIndex <= 0)
				{
					try
					{
						if (toolStripComboBoxFilter.Items.IndexOf(toolStripComboBoxFilter.Text) != -1)
						{
							toolStripComboBoxFilter.SelectedIndex = toolStripComboBoxFilter.Items.IndexOf(toolStripComboBoxFilter.Text);
							// toolStripComboBoxFilter.SelectedIndex=toolStripComboBoxFilter.Items[toolStripComboBoxFilter.Text]
						}
					}
					catch (ArgumentException)
					{
					}
				}
				if (toolStripComboBoxFilter.SelectedIndex >= 0)
				{
					if (toolStripComboBoxFilter.SelectedIndex < listSearchStrings.Count - 1)
					{
						toolStripComboBoxFilter.Text = listSearchStrings[toolStripComboBoxFilter.SelectedIndex + 1 - 1];
						toolStripButtonPrevious.Enabled = true;
					}
					else
					{
						toolStripComboBoxFilter.Text = listSearchStrings[listSearchStrings.Count - 1];
						toolStripButtonNext.Enabled = false;
					}
				}



			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Helper Methods

		/// <summary>
		/// Shows all the objects of selected class
		/// </summary>
		private void ShowAllObjects()
		{
			try
			{
				OMETrace.WriteFunctionStart();
				ShowObjectsForAClass();
				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}




		/// <summary>
		/// Show the result for selected class in both case Run Query/View Objects
		/// </summary>
		private void ShowObjectsForAClass()
		{
			try
			{
				queryBuilder = QueryBuilder.Instance;

				//if (Helper.ListQueryAttributes == null)
				//    Helper.ListQueryAttributes = new Hashtable();

				if (this.listQueryAttributes == null)
					this.listQueryAttributes = new Hashtable();

				this.listQueryAttributes = queryBuilder.GetSelectedAttributes();

				//Only Set BaseClass with current class

				if (toolStripButtonAssemblyView.Checked)
				{
					if (dbAssemblyTreeView.SelectedNode.Parent != null)
						Helper.BaseClass = dbAssemblyTreeView.SelectedNode.Text;
					else
						Helper.BaseClass = Helper.ClassName;
				}
				else
				{
					if (dbtreeviewObject.SelectedNode.Parent != null)
						Helper.BaseClass = dbtreeviewObject.SelectedNode.Text;
					else
						Helper.BaseClass = Helper.ClassName;

				}


				//Prepare query for View Objects
				omQuery = new OMQuery(Helper.BaseClass, DateTime.Now);

				if (Helper.HashTableBaseClass.Contains(Helper.BaseClass))
					omQuery.AttributeList = this.listQueryAttributes;//this.GetQueryAttributes();

				//Need to fectch attribute list in query result for same OMQuery
				if (Helper.OMResultedQuery.Contains(omQuery.BaseClass))
				{
					Helper.OMResultedQuery[omQuery.BaseClass] = omQuery;
				}
				else
				{
					Helper.OMResultedQuery.Add(omQuery.BaseClass, omQuery);
				}

				dbtreeviewObject.UpdateTreeNodeSelection(dbtreeviewObject.SelectedNode, toolStripButtonAssemblyView.Checked);
				bw = new BackgroundWorker();
				bw.WorkerReportsProgress = true;
				bw.WorkerSupportsCancellation = true;
				bw.ProgressChanged += new ProgressChangedEventHandler(bw_ProgressChanged);
				bw.DoWork += new DoWorkEventHandler(bw_DoWork);
				bw.RunWorkerCompleted += new RunWorkerCompletedEventHandler(bw_RunWorkerCompleted);
				ViewBase.ApplicationObject.StatusBar.Animate(true, vsStatusAnimation.vsStatusAnimationBuild);
				bw.RunWorkerAsync();

				for (double i = 1; i < 10000; i++)
				{
					i++;
					if (bw.IsBusy)
						bw.ReportProgress((int)i * 100 / 10000);


					if (isrunning == false)
						break;
					if (i == 9999)
						i = 1;
				}


				////CreateQueryResultToolWindow();


			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


		#region Background worker for showing all objects

		void bw_ProgressChanged(object sender, ProgressChangedEventArgs e)
		{
			ViewBase.ApplicationObject.StatusBar.Progress(true, "Running Query ... ", e.ProgressPercentage * 10, 10000);

		}

		bool isrunning = true;
		BackgroundWorker bw = new BackgroundWorker();
		long[] objectid;

		void bw_DoWork(object sender, DoWorkEventArgs e)
		{

			try
			{
				ObjectBrowser.Instance.Enabled = false;
				PropertiesTab.Instance.Enabled = false;
				QueryBuilder.Instance.Enabled = false;
				objectid = Helper.DbInteraction.ExecuteQueryResults(omQuery);
				e.Result = objectid;
				bw.ReportProgress(1000);
				isrunning = false;


			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}


		}

		void ClearStatusBar()
		{
			bw.CancelAsync();
			bw = null;
			ViewBase.ApplicationObject.StatusBar.Clear();
			ViewBase.ApplicationObject.StatusBar.Animate(false, vsStatusAnimation.vsStatusAnimationBuild);

		}

		void bw_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
		{
			// CreateQueryResultToolWindow();
			try
			{
				CreateQueryResultToolWindow();
				//Refresh Object Browser window after runnig the query each time.
				queryBuilder.ClearAllQueries();
				ObjectBrowser.Instance.Enabled = true;
				PropertiesTab.Instance.Enabled = true;
				QueryBuilder.Instance.Enabled = true;
				isrunning = false;
				bw.CancelAsync();
				bw = null;
				ViewBase.ApplicationObject.StatusBar.Clear();
				ViewBase.ApplicationObject.StatusBar.Progress(false, "Query run successfully!", 0, 0);
				ViewBase.ApplicationObject.StatusBar.Text = "Query run successfully!";
				ViewBase.ApplicationObject.StatusBar.Animate(false, vsStatusAnimation.vsStatusAnimationBuild);
			}
			catch (Exception oEx)
			{
				ClearStatusBar();
				LoggingHelper.ShowMessage(oEx);
			}

		}
		#endregion


		public delegate void delPassData(long[] objectid);

		/// <summary>
		/// Intialize Assembly TreeView
		/// </summary>
		private void InitializeAssemblyTreeView()
		{
			try
			{
				dbAssemblyTreeView = new dbTreeView();

				dbAssemblyTreeView.SetTreeViewImages();

				this.dbAssemblyTreeView.Visible = false;
				this.dbAssemblyTreeView.Dock = DockStyle.Fill;
				this.dbAssemblyTreeView.AllowDrop = true;
				this.dbAssemblyTreeView.Location = new System.Drawing.Point(0, 0);
				this.dbAssemblyTreeView.Name = "dbtreeviewObject";
				this.dbAssemblyTreeView.Size = new System.Drawing.Size(1234, 740);
				this.dbAssemblyTreeView.Font = new System.Drawing.Font("Tahoma", 8F);
				this.dbAssemblyTreeView.TabIndex = 2;
				this.dbAssemblyTreeView.AfterCollapse += new TreeViewEventHandler(dbtreeviewObject_AfterCollapse);
				this.dbAssemblyTreeView.AfterSelect += new TreeViewEventHandler(dbtreeviewObject_AfterSelect);
				this.dbAssemblyTreeView.AfterExpand += new TreeViewEventHandler(dbtreeviewObject_AfterExpand);
				this.dbAssemblyTreeView.OnContextMenuItemClicked += new EventHandler<DBContextItemClickedEventArg>(TreeView_OnContextMenuItemClicked);
				this.dbAssemblyTreeView.MouseDown += new MouseEventHandler(TreeView_MouseDown);

				this.tableLayoutPanelObjectTreeView.Controls.Add(this.dbAssemblyTreeView, 0, 1);

				//dbAssemblyTreeView.BuildContextMenu(null);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Set the required images for treeview
		/// </summary>
		private void SetObjectBrowserImages()
		{
			try
			{
				toolStripButtonClear.Image = dbImages.ClearFilter;
				toolStripButtonFlatView.Image = dbImages.ClassView;
				toolStripButtonAssemblyView.Image = dbImages.AssemblyView;
				toolStripButtonFilter.Image = dbImages.Filter;
				toolStripButtonPrevious.Image = dbImages.PreviousFilter;
				toolStripButtonNext.Image = dbImages.NextFilter;
				toolStripButtonFolder.Image = dbImages.closedFolder;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Clears the filter.
		/// </summary>
		private void ClearSearch()
		{
			try
			{
				if (toolStripButtonAssemblyView.Checked)
				{

					//Clear all the nodes added to the Assembly Treeview
					dbAssemblyTreeView.Nodes.Clear();
					//Repopulate Assembly Treeview
					dbAssemblyTreeView.PopulateAssemblyTreeView(storedAssemblies);
				}
				else
				{
					//if (dbtreeviewObject.Nodes.Count < storedclasses.Count)
					//{
					//Clear all the nodes added to the Class Treeview
					dbtreeviewObject.Nodes.Clear();
					//Repopulate Class Treeview
					dbtreeviewObject.AddFavouritFolderFromDatabase();
					dbtreeviewObject.AddTreeNode(storedclasses, null);
					//}
				}

				//Reset the treeview images
				SetObjectBrowserImages();
				toolStripButtonPrevious.Enabled = false;
				toolStripButtonNext.Enabled = true;
				toolStripButtonClear.Enabled = false;
				toolStripComboBoxFilter.SelectedIndex = 0;

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		//private void CreateQueryResultToolWindow()
		//{
		//    try
		//    {
		//        OMETrace.WriteFunctionStart();

		//        string assemblypath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
		//        string className = "OMControlLibrary.QueryResult";

		//        string guidpos = Helper.GetClassGUID(Helper.BaseClass);

		//        int index = Helper.BaseClass.LastIndexOf(CONST_COMMA_CHAR);
		//        string strClassName = Helper.BaseClass.Remove(0, index);

		//        string str = Helper.BaseClass.Remove(index);

		//        index = str.LastIndexOf(CONST_DOT_CHAR);
		//        string caption = str.Remove(0, index + 1) + strClassName;

		//        object ctlobj = null;
		//        AddIn addinobj = ApplicationObject.AddIns.Item(1);
		//        EnvDTE80.Windows2 wins2obj = (Windows2)ApplicationObject.Windows;

		//        // Creates Tool Window and inserts the user control in it.
		//        Helper.QueryResultToolWindow = wins2obj.CreateToolWindow2(addinobj, assemblypath,
		//                                         className, caption, guidpos, ref ctlobj);

		//        QueryResult queryResult = new QueryResult();
		//        queryResult.omQuery = this.omQuery;

		//        Helper.QueryResultToolWindow.IsFloating = false;
		//        Helper.QueryResultToolWindow.Linkable = false;
		//        if (Helper.QueryResultToolWindow.AutoHides == true)
		//        {
		//            Helper.QueryResultToolWindow.AutoHides = false;
		//        }
		//        Helper.QueryResultToolWindow.Visible = true;

		//        OMETrace.WriteFunctionEnd();
		//    }
		//    catch (Exception oEx)
		//    {
		//        LoggingHelper.ShowMessage(oEx);
		//    }
		//}
		private void CreateQueryResultToolWindow()
		{
			try
			{
				OMETrace.WriteFunctionStart();

				string assemblypath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
				string className = "OMControlLibrary.QueryResult";

				string guidpos = Helper.GetClassGUID(Helper.BaseClass);

				int index = Helper.BaseClass.LastIndexOf(CONST_COMMA_CHAR);
				string strClassName = Helper.BaseClass.Remove(0, index);

				string str = Helper.BaseClass.Remove(index);

				index = str.LastIndexOf(CONST_DOT_CHAR);
				string caption = str.Remove(0, index + 1) + strClassName;

				object ctlobj = null;
				AddIn addinobj = ApplicationObject.AddIns.Item(1);

				EnvDTE80.Windows2 wins2obj = (Windows2)ApplicationObject.Windows;

				// Creates Tool Window and inserts the user control in it.
				Helper.QueryResultToolWindow = wins2obj.CreateToolWindow2(addinobj, assemblypath,
												 className, caption, guidpos, ref ctlobj);

				QueryResult queryResult = ctlobj as QueryResult;
				delPassData del = new delPassData(queryResult.Setobjectid);

				del(objectid);

				//     Stream imgageStream = Helper.m_AddIn_Assembly.GetManifestResourceStream(OMControlLibrary.Common.Constants.DB4OICON);


				//stdole.IPictureDisp Pic;

				//Pic = MyHost.IPictureDisp(Image.FromStream(imgageStream));

				//Helper.QueryResultToolWindow.SetTabPicture(imgageStream);
				Helper.QueryResultToolWindow.IsFloating = false;
				Helper.QueryResultToolWindow.Linkable = false;
				if (Helper.QueryResultToolWindow.AutoHides == true)
				{
					Helper.QueryResultToolWindow.AutoHides = false;
				}
				Helper.QueryResultToolWindow.Visible = true;

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		/// <summary>
		/// Set ClassName when the treenode selected
		/// </summary>
		/// <param name="e"></param>
		private void SetClassName(TreeViewEventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (e.Node.Parent == null && e.Node.Tag != null && (e.Node.Tag.ToString() == "Fav Folder" || e.Node.Tag.ToString() == "Assembly View"))
				{
					PropertiesTab.Instance.ShowClassProperties = false;
					return;
				}
				else
				{
					PropertiesTab.Instance.ShowClassProperties = true;
				}

				//Check Selected view and set the class name accordingly
				if (toolStripButtonAssemblyView.Checked || (e.Node.Parent != null) && (e.Node.Parent.Tag.ToString() == "Fav Folder" || e.Node.Tag.ToString() == "Assembly View"))
				{
					if (e.Node.FullPath.IndexOf(CONST_BACK_SLASH_CHAR) != -1)
					{
						string className = e.Node.FullPath.Split(CONST_BACK_SLASH_CHAR)[1];

						if (className.IndexOf(CONST_BACK_SLASH_CHAR) != -1)
							className = className.Split(CONST_BACK_SLASH_CHAR)[0];

						Helper.ClassName = className;
					}
					else Helper.ClassName = e.Node.FullPath;

					dbAssemblyTreeView.SelectedNode = e.Node;
				}
				else
				{
					if (Helper.ClassName != null)
					{
						if (!Helper.ClassName.Equals(e.Node.FullPath.Split(CONST_BACK_SLASH_CHAR)[0]))
						{
							Helper.ClassName = e.Node.FullPath.Split(CONST_BACK_SLASH_CHAR)[0];
						}
					}
					else
						Helper.ClassName = e.Node.FullPath.Split(CONST_BACK_SLASH_CHAR)[0];

					dbtreeviewObject.SelectedNode = e.Node;
				}

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Switch between Assembly/Class View 
		/// </summary>
		private void ShowAssemblyTreeView()
		{
			TreeNode selectedTreeNode = null;
			try
			{
				//Check if Assembly button is selected
				if (toolStripButtonAssemblyView.Checked)
				{
					//Get list of class grouped by assemblies
					if (dbAssemblyTreeView.Nodes.Count < 1)
					{
						storedAssemblies = Helper.DbInteraction.FetchAllStoredClassesForAssembly();
						//dbAssemblyTreeView.PopulateAssemblyTreeView( storedAssemblies);
					}

					//If user specified filtervalue in class view and switched to assembly view
					//if (!string.IsNullOrEmpty(toolStripComboBoxFilter.Text.Trim()) 
					//    && toolStripComboBoxFilter.Text.Trim() != CONST_FILTER_DEFAULT_STRING)
					if (!string.IsNullOrEmpty(filterString) && filterString == toolStripComboBoxFilter.Text.Trim().ToLower())
					{
						//Get the filtered node for given filter value
						dbAssemblyTreeView.FindTreeNodesAssemblyView(storedAssemblies,
							toolStripComboBoxFilter.Text.Trim().ToLower());
					}
					else
					{
						//Clear and Polulate the tree view for assemblies allready fetched from the database
						//dbAssemblyTreeView.Nodes.Clear();
						dbAssemblyTreeView.PopulateAssemblyTreeView(storedAssemblies);
					}

					dbAssemblyTreeView.Visible = true;
					dbtreeviewObject.Visible = false;
					dbAssemblyTreeView.UpdateTreeNodeSelection(dbtreeviewObject.SelectedNode, toolStripButtonAssemblyView.Checked);
					dbAssemblyTreeView.Focus();
				}
				else
				{
					dbAssemblyTreeView.Visible = false;
					dbtreeviewObject.Visible = true;

					//if user switched to the class view and filter values is specified in filter text box.
					//Get the filtered node and populate the class treeview
					if (!string.IsNullOrEmpty(filterString) && filterString == toolStripComboBoxFilter.Text.Trim().ToLower())
					{
						dbtreeviewObject.FindTreeNodesClasses(storedclasses, null,
							toolStripComboBoxFilter.Text.Trim().ToLower());
					}
					else
					{
						//If user clears the filter value and switched to class view
						//if (storedclasses.Count > dbAssemblyTreeView.Nodes.Count)
						//{
						//clear all nodes
						//dbtreeviewObject.Nodes.Clear();
						//repopulate the tree view
						dbtreeviewObject.AddTreeNode(storedclasses, null);
						//}
					}

					if (dbAssemblyTreeView.SelectedNode != null && dbAssemblyTreeView.SelectedNode.Tag == null)
					{
						if (dbtreeviewObject.Nodes.Count > 0)
						{
							selectedTreeNode = dbtreeviewObject.SelectedNode = dbtreeviewObject.Nodes[0];
						}
						else
						{
							selectedTreeNode = dbAssemblyTreeView.SelectedNode;
						}
					}
					else
						selectedTreeNode = dbAssemblyTreeView.SelectedNode;

					dbtreeviewObject.UpdateTreeNodeSelection(selectedTreeNode, toolStripButtonAssemblyView.Checked);
					dbtreeviewObject.Focus();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		private void FillFilterComboBox(List<string> listSearchString)
		{
			try
			{
				toolStripComboBoxFilter.Items.Clear();
				toolStripComboBoxFilter.Items.Add(CONST_FILTER_DEFAULT_STRING);
				//toolStripComboBoxFilter.Items.Add("<Clear Search>");
				List<string> listfilterString = listSearchString;

				if (listfilterString != null)
				{
					for (int i = 0; i < listfilterString.Count; i++)
					{
						string strSearch = listfilterString[i];
						toolStripComboBoxFilter.Items.Add(strSearch);
					}
				}


			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}
		#endregion

		private void toolStripButtonPrevious_Click(object sender, EventArgs e)
		{
			try
			{
				if (listSearchStrings == null || (listSearchStrings != null && listSearchStrings.Count == 0))
					return;

				if (toolStripComboBoxFilter.SelectedIndex > 1)
				{
					toolStripComboBoxFilter.Text = listSearchStrings[toolStripComboBoxFilter.SelectedIndex - 2];
					toolStripButtonNext.Enabled = true;
					if (toolStripComboBoxFilter.SelectedIndex == 1)
						toolStripButtonPrevious.Enabled = false;
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void toolStripButtonNext_Click(object sender, EventArgs e)
		{
			try
			{
				if (listSearchStrings == null || (listSearchStrings != null && listSearchStrings.Count == 0))
					return;
				if (toolStripComboBoxFilter.SelectedIndex <= 0)
				{
					try
					{
						if (toolStripComboBoxFilter.Items.IndexOf(toolStripComboBoxFilter.Text) != -1)
						{
							toolStripComboBoxFilter.SelectedIndex = toolStripComboBoxFilter.Items.IndexOf(toolStripComboBoxFilter.Text);
							// toolStripComboBoxFilter.SelectedIndex=toolStripComboBoxFilter.Items[toolStripComboBoxFilter.Text]
						}
					}
					catch (ArgumentException)
					{
					}
				}
				if (toolStripComboBoxFilter.SelectedIndex >= 0)
				{
					if (toolStripComboBoxFilter.SelectedIndex < listSearchStrings.Count - 1)
					{
						toolStripComboBoxFilter.Text = listSearchStrings[toolStripComboBoxFilter.SelectedIndex + 1 - 1];
						toolStripButtonPrevious.Enabled = true;
					}
					else
					{
						toolStripComboBoxFilter.Text = listSearchStrings[listSearchStrings.Count - 1];
						toolStripButtonNext.Enabled = false;
					}
				}



			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void toolStripButtonFolder_Click(object sender, EventArgs e)
		{
			dbtreeviewObject.AddFavoriteFolder();
		}

	}
	public class MyHost : System.Windows.Forms.AxHost
	{


		public MyHost()
			: base("59EE46BA-677D-4d20-BF10-8D8067CB8B33")
		{
		}

		public static stdole.IPictureDisp IPictureDisp(System.Drawing.Image Image)
		{
			return (stdole.IPictureDisp)AxHost.GetIPictureDispFromPicture(Image);
		}

	}
}

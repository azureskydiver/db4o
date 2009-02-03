using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows.Forms;
using EnvDTE;
using OManager.BusinessLayer.UIHelper;
using OMControlLibrary.Common;
using OManager.BusinessLayer.Login;
using OManager.BusinessLayer.QueryManager;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary
{
	public partial class ObjectBrowser
	{
		#region Private Member Variables

		RecentQueries recConnection;
		dbInteraction dbInteractionObject;
		dbTreeView dbAssemblyTreeView;

		internal dbTreeView DbAssemblyTreeView
		{
			get { return dbAssemblyTreeView; }
			set { dbAssemblyTreeView = value; }
		}

		Hashtable storedclasses;
		Hashtable storedAssemblies;
		private static int classCount;
		string filterString = string.Empty;

		//internal
		internal OMQuery omQuery;
		internal Hashtable listQueryAttributes;
		internal List<string> listSearchStrings;

		//Controls
		QueryBuilder queryBuilder;
		PropertiesTab propertiesTab;

		//Constants
		private const char CONST_COMMA_CHAR = ',';
		private const char CONST_DOT_CHAR = '.';
		private const char CONST_BACK_SLASH_CHAR = '\\';
		private const string CONST_COMMA_STRING = ",";
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
			SetStyle(ControlStyles.CacheText | ControlStyles.OptimizedDoubleBuffer, true);

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
				CheckForIllegalCrossThreadCalls = false;
				SetLiterals();
				Helper.ClassName = null;
				dbInteractionObject = new dbInteraction();

				storedclasses = dbInteractionObject.FetchAllStoredClasses();

				if (storedclasses != null)
					classCount = storedclasses.Count;

				InitializeAssemblyTreeView();

				//Set Treeview Image List
				dbtreeviewObject.SetTreeViewImages();

				//Populate the TreeView
				dbtreeviewObject.AddFavouritFolderFromDatabase();
				dbtreeviewObject.AddTreeNode(storedclasses, null);

				SetObjectBrowserImages();

				SelectFirstClassNode();

				dbtreeviewObject.OnContextMenuItemClicked += TreeView_OnContextMenuItemClicked;
				dbtreeviewObject.MouseDown += TreeView_MouseDown;

				propertiesTab = PropertiesTab.Instance;
				if (classCount == 0)
				{
					propertiesTab.ShowClassProperties = false;
					toolStripButtonAssemblyView.Enabled = toolStripButtonFlatView.Enabled = false;
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

		private void SelectFirstClassNode()
		{
			if (dbtreeviewObject.Nodes.Count == 0)
			{
				return;
			}

			TreeNode node = FindFirstClassNode(dbtreeviewObject.Nodes[0]);
			if (node != null)
				SetClassName(node);
		}

		private static TreeNode FindFirstClassNode(TreeNode node)
		{
			while (node != null && node.Tag != null && node.Tag.ToString() == "Fav Folder")
			{
				node = node.NextNode;
			}
			return node;
		}

		/// <summary>
		/// Event track the selection of the treeview items
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void dbtreeviewObject_AfterSelect(object sender, TreeViewEventArgs e)
		{
			try
			{
				dbTreeView dbTreeviewObj = sender as dbTreeView;
				OMETrace.WriteFunctionStart();

				//Set the class name to get the result 
				SetClassName(e.Node);

				Helper.SelectedObject = null;

				//Refresh Properties Pane for selected class
				propertiesTab = PropertiesTab.Instance;
				propertiesTab.ShowObjectPropertiesTab = false;
				if (classCount == 0 || (dbTreeviewObj.SelectedNode != null && dbTreeviewObj.SelectedNode.Tag != null && (dbTreeviewObj.SelectedNode.Tag.ToString() == "Fav Folder" || dbTreeviewObj.SelectedNode.Tag.ToString() == "Assembly View")))
					propertiesTab.ShowClassProperties = false;
				else
					propertiesTab.ShowClassProperties = true;

				propertiesTab.RefreshPropertiesTab(null);

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
			try
			{
				OMETrace.WriteFunctionStart();

				//After adding child nodes dont add again
				if (e.Node.Parent != null && e.Node.Parent.Tag != null && e.Node.Parent.Tag.ToString() == "Fav Folder")
				{
					e.Node.TreeView.SelectedNode = e.Node;
				}

				if (!Helper.OnTreeViewAfterExpand(sender, e))
					return;

				//Get the name of selected item with the namespace
				string nodeName = e.Node.Name.LastIndexOf(CONST_COMMA_CHAR) == -1 ? e.Node.Tag.ToString() : e.Node.Name;

				Hashtable storedfields = Helper.DbInteraction.FetchStoredFields(nodeName);

				dbtreeviewObject.AddTreeNode(storedfields, e.Node);

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			finally
			{
				SetClassName(e.Node);
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
						SetClassName(e.Node);
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
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

		private void TreeView_OnContextMenuItemClicked(object sender, DBContextItemClickedEventArg e)
		{
			try
			{
				queryBuilder = QueryBuilder.Instance;
				dbInteraction dbI = new dbInteraction();
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
						FavouriteFolder Fav = new FavouriteFolder(null, dbtreeviewObject.SelectedNode.Text);
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
					className = tempTreeNode.Parent.Tag.ToString().Contains(CONST_COMMA_STRING) 
												? tempTreeNode.Parent.Tag.ToString() 
												: tempTreeNode.Parent.Name;
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
					TreeNode treenode;
					queryBuilder = QueryBuilder.Instance;
					List<string> list;
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

				if (listQueryAttributes == null)
					listQueryAttributes = new Hashtable();

				listQueryAttributes = queryBuilder.GetSelectedAttributes();

				//Only Set BaseClass with current class

				if (toolStripButtonAssemblyView.Checked)
				{
					Helper.BaseClass = dbAssemblyTreeView.SelectedNode.Parent != null ? dbAssemblyTreeView.SelectedNode.Text : Helper.ClassName;
				}
				else
				{
					Helper.BaseClass = dbtreeviewObject.SelectedNode.Parent != null ? dbtreeviewObject.SelectedNode.Text : Helper.ClassName;
				}


				//Prepare query for View Objects
				omQuery = new OMQuery(Helper.BaseClass, DateTime.Now);

				if (Helper.HashTableBaseClass.Contains(Helper.BaseClass))
					omQuery.AttributeList = listQueryAttributes;

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
				bw.ProgressChanged += bw_ProgressChanged;
				bw.DoWork += bw_DoWork;
				bw.RunWorkerCompleted += bw_RunWorkerCompleted;
				ApplicationObject.StatusBar.Animate(true, vsStatusAnimation.vsStatusAnimationBuild);
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

		static void bw_ProgressChanged(object sender, ProgressChangedEventArgs e)
		{
			ApplicationObject.StatusBar.Progress(true, "Running Query ... ", e.ProgressPercentage * 10, 10000);

		}

		bool isrunning = true;
		BackgroundWorker bw = new BackgroundWorker();
		long[] objectid;

		void bw_DoWork(object sender, DoWorkEventArgs e)
		{

			try
			{
				Instance.Enabled = false;
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
			ApplicationObject.StatusBar.Clear();
			ApplicationObject.StatusBar.Animate(false, vsStatusAnimation.vsStatusAnimationBuild);
		}

		void bw_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
		{
			// CreateQueryResultToolWindow();
			try
			{
				CreateQueryResultToolWindow();
				//Refresh Object Browser window after runnig the query each time.
				queryBuilder.ClearAllQueries();
				Instance.Enabled = true;
				PropertiesTab.Instance.Enabled = true;
				QueryBuilder.Instance.Enabled = true;
				isrunning = false;
				bw.CancelAsync();
				bw = null;
				ApplicationObject.StatusBar.Clear();
				ApplicationObject.StatusBar.Progress(false, "Query run successfully!", 0, 0);
				ApplicationObject.StatusBar.Text = "Query run successfully!";
				ApplicationObject.StatusBar.Animate(false, vsStatusAnimation.vsStatusAnimationBuild);
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

				dbAssemblyTreeView.Visible = false;
				dbAssemblyTreeView.Dock = DockStyle.Fill;
				dbAssemblyTreeView.AllowDrop = true;
				dbAssemblyTreeView.Location = new System.Drawing.Point(0, 0);
				dbAssemblyTreeView.Name = "dbtreeviewObject";
				dbAssemblyTreeView.Size = new System.Drawing.Size(1234, 740);
				dbAssemblyTreeView.Font = new System.Drawing.Font("Tahoma", 8F);
				dbAssemblyTreeView.TabIndex = 2;
				dbAssemblyTreeView.AfterCollapse += dbtreeviewObject_AfterCollapse;
				dbAssemblyTreeView.AfterSelect += dbtreeviewObject_AfterSelect;
				dbAssemblyTreeView.AfterExpand += dbtreeviewObject_AfterExpand;
				dbAssemblyTreeView.OnContextMenuItemClicked += TreeView_OnContextMenuItemClicked;
				dbAssemblyTreeView.MouseDown += TreeView_MouseDown;

				tableLayoutPanelObjectTreeView.Controls.Add(dbAssemblyTreeView, 0, 1);

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
					dbtreeviewObject.Nodes.Clear();
					dbtreeviewObject.AddFavouritFolderFromDatabase();
					dbtreeviewObject.AddTreeNode(storedclasses, null);
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

		private void CreateQueryResultToolWindow()
		{
			try
			{
				OMETrace.WriteFunctionStart();

				string guidpos = Helper.GetClassGUID(Helper.BaseClass);

				int index = Helper.BaseClass.LastIndexOf(CONST_COMMA_CHAR);
				string strClassName = Helper.BaseClass.Remove(0, index);

				string str = Helper.BaseClass.Remove(index);

				index = str.LastIndexOf(CONST_DOT_CHAR);
				string caption = str.Remove(0, index + 1) + strClassName;

				QueryResult queryResult;
				Helper.QueryResultToolWindow = CreateToolWindow("OMControlLibrary.QueryResult", caption, guidpos, out queryResult);

				queryResult.Setobjectid(objectid);

				Helper.QueryResultToolWindow.IsFloating = false;
				Helper.QueryResultToolWindow.Linkable = false;
				if (Helper.QueryResultToolWindow.AutoHides)
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
		/// <param name="node"></param>
		private void SetClassName(TreeNode node)
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (node.Parent == null && node.Tag != null && (node.Tag.ToString() == "Fav Folder" || node.Tag.ToString() == "Assembly View"))
				{
					PropertiesTab.Instance.ShowClassProperties = false;
					return;
				}
				
				PropertiesTab.Instance.ShowClassProperties = true;

				//Check Selected view and set the class name accordingly
				if (toolStripButtonAssemblyView.Checked || (node.Parent != null) && (node.Parent.Tag.ToString() == "Fav Folder" || node.Tag.ToString() == "Assembly View"))
				{
					if (node.FullPath.IndexOf(CONST_BACK_SLASH_CHAR) != -1)
					{
						string className = node.FullPath.Split(CONST_BACK_SLASH_CHAR)[1];

						if (className.IndexOf(CONST_BACK_SLASH_CHAR) != -1)
							className = className.Split(CONST_BACK_SLASH_CHAR)[0];

						Helper.ClassName = className;
					}
					else Helper.ClassName = node.FullPath;

					dbAssemblyTreeView.SelectedNode = node;
				}
				else
				{
					if (Helper.ClassName != null)
					{
						if (!Helper.ClassName.Equals(node.FullPath.Split(CONST_BACK_SLASH_CHAR)[0]))
						{
							Helper.ClassName = node.FullPath.Split(CONST_BACK_SLASH_CHAR)[0];
						}
					}
					else
						Helper.ClassName = node.FullPath.Split(CONST_BACK_SLASH_CHAR)[0];

					dbtreeviewObject.SelectedNode = node;
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
						dbtreeviewObject.FindTreeNodesClasses(storedclasses, null, toolStripComboBoxFilter.Text.Trim().ToLower());
					}
					else
					{
						//If user clears the filter value and switched to class view
						dbtreeviewObject.AddTreeNode(storedclasses, null);
					}

					TreeNode selectedTreeNode;
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

		private void FillFilterComboBox(IEnumerable<string> listSearchString)
		{
			try
			{
				toolStripComboBoxFilter.Items.Clear();
				toolStripComboBoxFilter.Items.Add(CONST_FILTER_DEFAULT_STRING);

				if (listSearchString != null)
				{
					foreach (string searchItem in listSearchString)
					{
						toolStripComboBoxFilter.Items.Add(searchItem);
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
				if (!HasItems(listSearchStrings))
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
				if (!HasItems(listSearchStrings))
					return;

				if (toolStripComboBoxFilter.SelectedIndex <= 0)
				{
					try
					{
						if (toolStripComboBoxFilter.Items.IndexOf(toolStripComboBoxFilter.Text) != -1)
						{
							toolStripComboBoxFilter.SelectedIndex = toolStripComboBoxFilter.Items.IndexOf(toolStripComboBoxFilter.Text);
						}
					}
					catch (ArgumentException)
					{
					}
				}

				if (toolStripComboBoxFilter.SelectedIndex < 0)
					return;
				
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
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private static bool HasItems<T>(ICollection<T> list)
		{
			return list != null && list.Count > 0;
		}

		private void toolStripButtonFolder_Click(object sender, EventArgs e)
		{
			dbtreeviewObject.AddFavoriteFolder();
		}

	}
	public class MyHost : AxHost
	{


		public MyHost()
			: base("59EE46BA-677D-4d20-BF10-8D8067CB8B33")
		{
		}

		public static stdole.IPictureDisp IPictureDisp(System.Drawing.Image Image)
		{
			return (stdole.IPictureDisp) GetIPictureDispFromPicture(Image);
		}

	}
}

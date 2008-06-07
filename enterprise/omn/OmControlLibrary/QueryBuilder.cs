using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using OMControlLibrary.Common;
using OManager.BusinessLayer.Login;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.ObjectExplorer;
using System.Collections.Specialized;
using OManager.BusinessLayer.Common;
using OME.Logging.Common;
using OME.Logging.Tracing;
using System.Reflection;
using Microsoft.VisualStudio.CommandBars;
using EnvDTE;
using EnvDTE80;

namespace OMControlLibrary
{
    public partial class QueryBuilder : ViewBase
    {

        private static QueryBuilder instance;

        public static QueryBuilder Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new QueryBuilder();
                }
                return instance;
            }
        }


        #region Member Variable

        private string typeOfNode = string.Empty;
        private int m_attributeCount = 0;
        private int m_queryGroupCount;

        private OMQuery omQuery = null;

        //Controls 
        private TableLayoutPanel tableLayoutPanelQueries = null;


        private dbDataGridView dbDataGridAttributes = null;
        DataGridViewGroup dataGridViewGroup = null;
        private string className;


        private DataGridViewGroup defaultGroup = null;

        private ToolTip recentQueriesToolTip = null;

        //Constants 
        private const string CONST_DOT_STRING = ".";
        private const char CONST_DOT_CHAR = '.';

        private const string CONST_COMMA_STRING = ",";
        private const char CONST_COMMA_CHAR = ',';
        WindowEvents _windowsEvents;
        #endregion

        #region Properties

        /// <summary>
        /// Get the Attribute DataGridView
        /// </summary>
        public dbDataGridView DataGridViewAttributes
        {
            get { return dbDataGridAttributes; }
        }

        /// <summary>
        /// Get/Set number of query groups
        /// </summary>
        public int QueryGroupCount
        {
            get { return m_queryGroupCount; }
            set { m_queryGroupCount = value; }
        }

        /// <summary>
        /// Get number of attributes added
        /// </summary>
        public int AttributeCount
        {
            get
            {
                m_attributeCount = dbDataGridAttributes.RowCount;
                return m_attributeCount;
            }
        }

        public string ClassName
        {
            get { return className; }
            set { className = value; }
        }

        public TableLayoutPanel TableLayoutPanelQueries
        {
            get { return tableLayoutPanelQueries; }
            set { tableLayoutPanelQueries = value; }
        }

        public bool EnableRunQuery
        {
            get { return buttonRunQuery.Enabled; }
            set { buttonRunQuery.Enabled = value; }
        }

        #endregion

        #region Event Handlers

        ////Handles the Drags events item dragged to the DataGridViewGroup DatagridView
        //internal event EventHandler<DragEventArgs> OnQueryBuilderDragEnter;
        ////Event Handler if DataGridView is removed
        //internal event EventHandler<DbEventArgs> OnQueryBuilderRemoveClick;
        ////Event Handler for Operator is changed for query group 
        //internal event EventHandler<DbEventArgs> OnQueryBuilderDataGridIndexChanged;
        ////Events handles id item is dragged to the Attribute datagridview
        //internal event EventHandler<DragEventArgs> OnAttributesDragEnter;

        internal event EventHandler<DbEventArgs> OnRecentQueryChanged;

        #endregion

        #region Constructor


        public QueryBuilder()
        {
            InitializeComponent();
        }

        #endregion

        #region Query Builder Events

        /// <summary>
        /// Load Event of Query Builder
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void QueryBuilder_Load(object sender, EventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();

                //Initialization of Queries tablelayour where the QueryGridGrup will be added
                InitializeQueriesTableLayoutPanel();

                //Add DataGridview Group to the Query Panel
                //if(defaultGroup == null)
                defaultGroup = AddDataGridViewToPanel();

                //Initialize Recent Queries
                InitializeRecentQueries();

                //Initialization of Attribute List
                InitializeAttributesDataGrid();

                SetLiterals();

                recentQueriesToolTip = new ToolTip();

                instance = this;
                EnvDTE.Events events = ApplicationObject.Events;
                _windowsEvents = (EnvDTE.WindowEvents)events.get_WindowEvents(null);
                _windowsEvents.WindowActivated += new _dispWindowEvents_WindowActivatedEventHandler(_windowsEvents_WindowActivated); 
                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

        }

        void _windowsEvents_WindowActivated(Window GotFocus, Window LostFocus)
        {
            if (GotFocus.Caption == "Query Builder")
            {                
                PropertiesTab.Instance.ShowObjectPropertiesTab = false;
                PropertiesTab.Instance.ShowClassProperties = true;
                PropertiesTab.Instance.SelectDefaultTab();

            }
        }

        /// <summary>
        /// Sets the width of Attribute column
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void QueryBuilder_Resize(object sender, EventArgs e)
        {
            try
            {
                if (dbDataGridAttributes != null)
                    dbDataGridAttributes.Columns[0].Width = dbDataGridAttributes.Width - 5;
                comboboxRecentQueries.Refresh();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }
        #endregion

        #region DataGridViewGroup Events

        /// <summary>
        /// Raise when expressions removed from groups 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dataGridViewGroup_OnRowsRemoved(object sender, EventArgs e)
        {
            DataGridViewGroup dbdataGridViewGroup = null;

            try
            {
                dbdataGridViewGroup = (DataGridViewGroup)((dbDataGridView)sender).Parent;

                if (tableLayoutPanelQueries.Controls.Count == 1)
                {
                    if (dbdataGridViewGroup.DataGridViewQuery.Rows.Count == 0)
                        buttonRunQuery.Enabled = false;
                }
                else
                {

                    //if Query quilder has more then one group
                    if (tableLayoutPanelQueries.Controls.Count != 1 && dbdataGridViewGroup != null &&
                        dbdataGridViewGroup.Removable == true)
                    {
                        //if all expression removed from the query group
                        //get the confirmation from user to remove that Query Group
                        if (MessageBox.Show(Helper.GetResourceString(Common.Constants.CONFIRMATION_MSG_REMOVE_QUERY_GROUP),
                                            Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION),
                                            MessageBoxButtons.YesNo,
                                            MessageBoxIcon.Question) == DialogResult.Yes)
                        {
                            int dataGridViewGroupHeight = dbdataGridViewGroup.Height;

                            tableLayoutPanelQueries.Controls.Remove(dbdataGridViewGroup);
                            QueryGroupCount--;
                            tableLayoutPanelQueries.RowCount = QueryGroupCount;
                            tableLayoutPanelQueries.Height = tableLayoutPanelQueries.Height
                                                                    - dataGridViewGroupHeight;
                            //reset the expression group names
                            RenameQueryGroupCaption();
                        }
                    }
                }

                CheckForDataGridViewQueryRows();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Raise an event OnQueryBuilderDataGridIndexChanged
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dataGridViewGroup_OnDataGridViewComboBoxIndexChanged(object sender, DbEventArgs e)
        {
            dbDataGridView datagrid = e.Data as dbDataGridView;
            string operatorValue = string.Empty;
            string operatorColumnName = string.Empty;
            int operatorColumnIndex = 0;

            try
            {
                OMETrace.WriteFunctionStart();

                operatorColumnName = Helper.GetResourceString(Common.Constants.QUERY_GRID_OPERATOR);
                operatorColumnIndex = datagrid.Columns[operatorColumnName].Index;

                if (datagrid.CurrentCell.ColumnIndex == operatorColumnIndex)
                {
                    if (datagrid.Rows.Count > 1)
                    {
                        operatorValue = ((ComboBox)sender).SelectedItem.ToString();

                        for (int i = 1; i < datagrid.Rows.Count; i++)
                        {
                            datagrid.Rows[i].Cells[operatorColumnName].Value = operatorValue;
                        }
                    }
                }

                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Rainse an event OnQueryBuilderRemoveClick
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dataGridViewGroup_OnRemoveClick(object sender, DbEventArgs e)
        {
            try
            {
                if (e.Data is DataGridViewGroup)
                {
                    DataGridViewGroup dataGridViewGroup = (DataGridViewGroup)e.Data;
                    int dataGridViewGroupHeight = dataGridViewGroup.Height;

                    if (dataGridViewGroup.Parent is TableLayoutPanel)
                    {
                        TableLayoutPanel tableLayoutPanelQueries = dataGridViewGroup.Parent as TableLayoutPanel;
                        tableLayoutPanelQueries.Controls.Remove(dataGridViewGroup);
                        QueryGroupCount--;
                        tableLayoutPanelQueries.RowCount = QueryGroupCount;
                        tableLayoutPanelQueries.Height = tableLayoutPanelQueries.Height
                                                                - dataGridViewGroupHeight;
                    }
                }               

                CheckForDataGridViewQueryRows();
                RenameQueryGroupCaption();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }


        private void buttonRunQuery_Click(object sender, EventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();
                //if (Helper.CheckForIfAlreadyLoggedIn == true)
                //{
                //    ExecuteQuery(sender, e);
                //}
                //else
                //{
                
                    if (Helper.CheckPermissions(Common.Constants.OBJECTMANAGER_USER_PERMISSION_QUERYBUILDER))
                    {
                        //Helper.CheckForIfAlreadyLoggedIn = true;
                        ExecuteQuery(sender, e);
                        
                    }
                    else
                    {
                        //Helper.CheckForIfAlreadyLoggedIn = false;
                        string filepath = System.Reflection.Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8); ;

                        int index = filepath.LastIndexOf('/');
                        filepath = filepath.Remove(index);
                        filepath = filepath + Common.Constants.OBJECTMANAGER_CONTACT_US_FILE_PATH;
                        try
                        {
                            if (Helper.winSalesPage == null || Helper.winSalesPage.Visible != true)
                                Helper.winSalesPage = ApplicationObject.DTE.ItemOperations.Navigate(filepath, vsNavigateOptions.vsNavigateOptionsNewWindow);
                            else
                                Helper.winSalesPage.Visible = true;
                        }
                        catch
                        {
                            Helper.winSalesPage = ApplicationObject.DTE.ItemOperations.Navigate(filepath, vsNavigateOptions.vsNavigateOptionsNewWindow);
                        }
                    //}

                    OMETrace.WriteFunctionEnd();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }


         //------------
        void bw_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            ViewBase.ApplicationObject.StatusBar.Progress(true, "Running Query ... ", e.ProgressPercentage * 10, 10000);
         //   ViewBase.ApplicationObject.StatusBar.Animate(true, vsStatusAnimation.vsStatusAnimationBuild);
        }

        bool isrunning = true;
        BackgroundWorker bw = new BackgroundWorker();
        long[] objectid;
        void bw_DoWork(object sender, DoWorkEventArgs e)
        {

            try
            {
               

                objectid = Helper.DbInteraction.ExecuteQueryResults(omQuery);
                e.Result = objectid;
                bw.ReportProgress(1000);
                isrunning = false;

            }
            catch (Exception oEx)
            {
                bw.CancelAsync();
                bw = null;
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
                ObjectBrowser.Instance.Enabled = true;
                PropertiesTab.Instance.Enabled = true;
                QueryBuilder.Instance.Enabled = true;
                if (Login.m_cmdBarCtrlBackup != null)
                    Login.m_cmdBarCtrlBackup.Enabled = true;
                if (Login.m_cmdBarCtrlDefrag != null)
                    Login.m_cmdBarCtrlDefrag.Enabled = true;
                if (Login.m_cmdBarCtrlConnect != null)
                    Login.m_cmdBarCtrlConnect.Enabled = true;
                if (Login.m_cmdBarBtnConnect != null)
                    Login.m_cmdBarBtnConnect.Enabled = true;
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
        private void ExecuteQuery(object sender, EventArgs e)
        {
            string errorMessage = string.Empty;
            omQuery = new OMQuery(Helper.BaseClass, DateTime.Now);

            try
            {
                //Check the for valid query. user must specifies the values for each query expression
                Helper.IsValidQuery = IsValidQuery(out errorMessage);

                if (!Helper.IsValidQuery)
                {
                    MessageBox.Show(errorMessage,
                                    Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION),
                                    MessageBoxButtons.OK, MessageBoxIcon.Information);
                    return;
                }

                omQuery = PrepareOMQuery();

                AddQueryToCurrentConnection(omQuery);

                if (Helper.IsValidQuery)
                {
                    ObjectBrowser.Instance.Enabled = false;
                    PropertiesTab.Instance.Enabled = false;
                    QueryBuilder.Instance.Enabled = false;
                    if (Login.m_cmdBarCtrlBackup != null)
                        Login.m_cmdBarCtrlBackup.Enabled = false;
                    if (Login.m_cmdBarCtrlDefrag != null)
                        Login.m_cmdBarCtrlDefrag.Enabled = false;
                    if (Login.m_cmdBarCtrlConnect != null)
                        Login.m_cmdBarCtrlConnect.Enabled = false;
                    if (Login.m_cmdBarBtnConnect != null)
                        Login.m_cmdBarBtnConnect.Enabled = false;
                    bw = new BackgroundWorker();
                    bw.WorkerReportsProgress = true;
                    bw.WorkerSupportsCancellation = true;
                    bw.ProgressChanged += new ProgressChangedEventHandler(bw_ProgressChanged);
                    bw.DoWork += new DoWorkEventHandler(bw_DoWork);
                    bw.RunWorkerCompleted += new RunWorkerCompletedEventHandler(bw_RunWorkerCompleted);
                    ViewBase.ApplicationObject.StatusBar.Animate(true, vsStatusAnimation.vsStatusAnimationBuild);
                    isrunning = true;
                  
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

               
                }
            }
            catch (Exception oEx)
            {
                bw.CancelAsync();
                bw = null;
                ClearStatusBar();
                LoggingHelper.ShowMessage(oEx);
            }

        }
        public delegate void delPassData(long[] objectid); 

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
        /// Raise an event OnQueryBuilderDragEnter
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dataGridViewGroup_OnDataGridViewDragEnter(object sender, DragEventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();

                dbDataGridView datagridObject = sender as dbDataGridView;               
                if (e.Data.GetDataPresent(typeof(TreeNode).ToString(), true))
                {
                    
                    //Get the selected item from classes tree
                    TreeNode tempTreeNode = (TreeNode)e.Data.GetData(typeof(TreeNode).ToString(), true);
                    bool rowadded;
                    if (tempTreeNode != null )
                    {
                        if (tempTreeNode.Tag != null && tempTreeNode.Tag.ToString() != "Fav Folder" && tempTreeNode.Tag.ToString() != "Assembly View")
                        {
                            //If dragged item has child node dont allow to be dragged to Query Builder
                            if (tempTreeNode.Nodes.Count > 0)
                            {
                                rowadded = datagridObject.AddAllItemsOfClassToQueryBuilder(tempTreeNode, this);
                            }
                            else
                            {
                                rowadded = datagridObject.AddToQueryBuilder(tempTreeNode, this);
                            }

                            if (rowadded)
                                buttonRunQuery.Enabled = true;
                        }

                    }
                }

                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        #endregion

        #region Event Handlers

        /// <summary>
        /// Adds a new query Group
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void buttonAddQueryGroup_Click(object sender, EventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();

                int queryGroupCount = tableLayoutPanelQueries.Controls.Count;

                if (queryGroupCount == 0)
                    return;

                DataGridViewGroup dataGridViewGroup =
                    (DataGridViewGroup)tableLayoutPanelQueries.Controls[queryGroupCount - 1];

                if (dataGridViewGroup.DataGridViewQuery.RowCount < 1)
                    return;

                DataGridViewGroup dgvGroup = AddDataGridViewToPanel();
                //focus at new added group
                panelQueryGrid.ScrollControlIntoView(dgvGroup);


                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Clears all queries from Query Builder
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void buttonClearAll_Click(object sender, EventArgs e)
        {
            ClearAllQueries();
            comboboxRecentQueries.SelectedIndex = 0;
        }

        /// <summary>
        /// Removes the selected attributes if "Delete" key pressed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void dataGridAttributes_KeyDown(object sender, KeyEventArgs e)
        {

            List<DataGridViewRow> selectedRows = null;
            try
            {
                OMETrace.WriteFunctionStart();

                if (e.KeyCode == Keys.Delete)
                {
                    if (dbDataGridAttributes.Rows.Count > 0)
                    {
                        if (dbDataGridAttributes.SelectedRows.Count > 1)
                        {
                            selectedRows = new List<DataGridViewRow>();

                            int rowCount = dbDataGridAttributes.Rows.Count;
                            for (int i = 0; i < rowCount; i++)
                            {
                                if (dbDataGridAttributes.Rows[i].Selected)
                                    selectedRows.Add(dbDataGridAttributes.Rows[i]);
                            }

                            for (int i = 0; i < selectedRows.Count; i++)
                            {
                                dbDataGridAttributes.Rows.Remove(selectedRows[i]);
                            }
                            selectedRows.Clear();
                        }
                        else
                        {
                            dbDataGridAttributes.Rows.RemoveAt(dbDataGridAttributes.SelectedCells[0].OwningRow.Index);
                        }
                        e.Handled = true;
                    }

                }
                else
                    e.Handled = false;

                OMETrace.WriteTraceBlockStartEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
            finally
            {
                selectedRows = null;
            }

        }

        /// <summary>
        /// Calls when row is removed from the Attribute List
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void dataGridAttributes_RowsRemoved(object sender, DataGridViewRowsRemovedEventArgs e)
        {
            CheckForDataGridViewQueryRows();
        }

        /// <summary>
        /// Raise an event OnAttributesDragEnter
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void dataGridAttributes_DragEnter(object sender, DragEventArgs e)
        {           
            string className = string.Empty;
            if (e.Data.GetDataPresent(typeof(TreeNode).ToString(), true))
            {
                //Get which tree node is dragged from the classes tree
                TreeNode tempTreeNode = (TreeNode)e.Data.GetData(typeof(TreeNode).ToString(), true);
                if (tempTreeNode != null)
                {
                    if (tempTreeNode.Tag != null && tempTreeNode.Tag.ToString() != "Fav Folder" && tempTreeNode.Tag.ToString() != "Assembly View")
                    {
                        Helper.BaseClass = Helper.FindRootNode(tempTreeNode);
                        //Don't drag the node if the treenode has childs, because its not premitive type 
                        if (tempTreeNode.Nodes.Count > 0)
                        {
                            AddAllTheElementsofClassIntoAttributeList(tempTreeNode);

                        }
                        else
                        {
                            string typeOfNode = Helper.GetTypeOfObject(tempTreeNode.Tag.ToString());

                            //If selected item is not a primitive type than dont allow to drage item
                            if (!Helper.IsPrimitive(typeOfNode))
                                return ;

                            //If field is not selected and Query Group has no clauses then reset the base class.
                            if (dbDataGridAttributes.Rows.Count == 0)
                            {
                                CheckForDataGridViewQueryRows();
                            }
                            //Check if dragged item is from same class or not, if not then dont allow to drag item
                            if (Helper.HashTableBaseClass.Count > 0)
                                if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
                                    return;
                            //Get the full path of the selected item
                            string fullpath = Helper.GetFullPath(tempTreeNode);
                            //Check whether attributes is allready added in list, if yes dont allow to added again. 
                            if (!Helper.CheckUniqueNessAttributes(fullpath, dbDataGridAttributes))
                                return;
                            if (tempTreeNode.Parent != null)
                            {
                                if (tempTreeNode.Parent.Tag.ToString().Contains(CONST_COMMA_STRING))
                                    className = tempTreeNode.Parent.Tag.ToString();
                                else
                                    className = tempTreeNode.Parent.Name.ToString();
                            }
                            //Add a new row and assing required values.
                            AddElementToAttributeGrid(className, fullpath);
                            if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
                                Helper.HashTableBaseClass.Add(Helper.BaseClass, string.Empty);
                        }

                    }
                    e.Effect = DragDropEffects.Move;
                }
            }
        }

        private void AddAllTheElementsofClassIntoAttributeList(TreeNode tempTreeNode)
        {
            string className = string.Empty;  
            if (tempTreeNode.Tag.ToString().Contains(","))
                className = tempTreeNode.Tag.ToString();
            else
                className = tempTreeNode.Name.ToString();

            if (dbDataGridAttributes.Rows.Count == 0)
            {
                CheckForDataGridViewQueryRows();
            }
            //Check if dragged item is from same class or not, if not then dont allow to drag item
            if (Helper.HashTableBaseClass.Count > 0)
                if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
                    return;

            if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
                Helper.HashTableBaseClass.Add(Helper.BaseClass, string.Empty);

            Hashtable storedfields = Helper.DbInteraction.FetchStoredFields(className);
            if (storedfields != null)
            {
                IDictionaryEnumerator eNum = storedfields.GetEnumerator();

                if (eNum != null)
                {
                    while (eNum.MoveNext())
                    {
                        typeOfNode = Helper.GetTypeOfObject(eNum.Value.ToString());
                        if (!Helper.IsPrimitive(typeOfNode))
                            continue;
                        else
                        {
                                                      

                            //Check whether attributes is allready added in list, if yes dont allow to added again. 
                                                      
                            string parentName = Helper.FormulateParentName(tempTreeNode, eNum);
                            if (!Helper.CheckUniqueNessAttributes(parentName, dbDataGridAttributes))
                                continue;
                            AddElementToAttributeGrid(typeOfNode, parentName);
                        }
                    }
                }
            }
        }

        private void AddElementToAttributeGrid(string className, string fullpath)
        {
            dbDataGridAttributes.Rows.Add(1);
            int index = dbDataGridAttributes.Rows.Count - 1;
            dbDataGridAttributes.Rows[index].Cells[0].Value = fullpath;
            dbDataGridAttributes.Rows[index].Cells[0].Tag = className;
            dbDataGridAttributes.ClearSelection();
            dbDataGridAttributes.Rows[index].Cells[0].Selected = true;

        }


        internal void AddToAttributeList(ref dbDataGridView dataGridAttributes, TreeNode tempTreeNode)
        {
            string className = string.Empty;

            try
            {
                //If field is not selected and Query Group has no clauses then reset the base class.
                if (dataGridAttributes.Rows.Count == 0)
                {
                    CheckForDataGridViewQueryRows();
                }

                //Get the full path of the selected item
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
                if (!Helper.CheckUniqueNessAttributes(fullpath, dataGridAttributes))
                    return;

                //Add a new row and assing required values.
                AddElementToAttributeGrid(className, fullpath);               

                if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
                    Helper.HashTableBaseClass.Add(Helper.BaseClass, string.Empty);
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }
        /// <summary>
        /// Build context menu if its null
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dataGridAttributes_MouseDown(object sender, MouseEventArgs e)
        {
            try
            {
                DataGridView.HitTestInfo hitTestInfo = dbDataGridAttributes.HitTest(e.X, e.Y);
                DataGridViewHitTestType hitTestType = hitTestInfo.Type;
                if (hitTestType == DataGridViewHitTestType.Cell)
                {
                    if (dbDataGridAttributes.ContextMenuStrip == null)
                        dbDataGridAttributes.BuildRowContextMenu();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Populates the recent queries
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void comboboxRecentQueries_Click(object sender, EventArgs e)
        {
            try
            {
                Helper.PopulateRecentQueryComboBox(Helper.ListOMQueries, comboboxRecentQueries);
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Handles the changed Recent Queries.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void comboboxRecentQueries_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                if (comboboxRecentQueries.SelectedIndex <= 0)
                    return;

                if (comboboxRecentQueries.SelectedValue == null ||
                    (comboboxRecentQueries.SelectedValue != null && comboboxRecentQueries.SelectedValue.GetType() == typeof(System.DBNull)))
                {
                    ClearAllQueries();
                    return;
                }

                if (!string.IsNullOrEmpty(((OMQuery)comboboxRecentQueries.SelectedValue).QueryString))
                {
                    //Set tooltip for selected recent query
                    recentQueriesToolTip.SetToolTip(comboboxRecentQueries, comboboxRecentQueries.SelectedText.ToString());

                    Helper.IsQueryResultUpdated = true;

                    //Get the OMQuery for selected query expression
                    OMQuery omQuery = (OMQuery)comboboxRecentQueries.SelectedValue; //(OMQuery)((dbDataGridView)sender).SelectedRows[0].Tag;

                    //Reset the OMQuery
                    SetOMQuery(omQuery);

                    //Set the base class name
                    Helper.ClassName = omQuery.BaseClass;


                    //Clear all the queies from the QueryBuilder 
                    ClearAllQueries();

                    //Repopulate the query builder
                    PopulateQueryBuilderGroup(omQuery);

                    if (OnRecentQueryChanged != null)
                    {
                        DbEventArgs eventArgs = new DbEventArgs();
                        eventArgs.Data = omQuery.BaseClass;
                        OnRecentQueryChanged(sender, eventArgs);
                    }
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Event to get tooltip of combobox
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void comboboxRecentQueries_DropdownItemSelected(object sender, ToolTipComboBox.DropdownItemSelectedEventArgs e)
        {
            try
            {
                if (e.SelectedItem < 0 || e.Scrolled) recentQueriesToolTip.Hide(comboboxRecentQueries);
                else if (comboboxRecentQueries.SelectedIndex != 0)
                    recentQueriesToolTip.Show(comboboxRecentQueries.Text.ToString(),
                                            comboboxRecentQueries,
                                            e.Bounds.Location.X + Cursor.Size.Width,
                                            e.Bounds.Location.Y + Cursor.Size.Height);
            }
            catch (Exception ex)
            {
                LoggingHelper.HandleException(ex);
            }
        }

        #endregion

        #region Private Methods DataGridViewFunctionality

        /// <summary>
        /// Initialize Tablelayout panel for each DataGridView Group
        /// </summary>
        private void InitializeQueriesTableLayoutPanel()
        {
            try
            {
                tableLayoutPanelQueries = new TableLayoutPanel();

                tableLayoutPanelQueries.RowCount = 1;
                tableLayoutPanelQueries.ColumnCount = 1;
                tableLayoutPanelQueries.GrowStyle = TableLayoutPanelGrowStyle.AddRows;
                tableLayoutPanelQueries.AutoSize = true;
                panelQueryGrid.BackColor = Color.Gray;
                panelQueryGrid.Controls.Add(tableLayoutPanelQueries);
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

        }

        /// <summary>
        /// Initialize DataGridView Group
        /// </summary>
        /// <returns></returns>
        private DataGridViewGroup InitializeDataGridViewGroup()
        {
            dataGridViewGroup = new DataGridViewGroup();

            try
            {
                //Set Properties
                dataGridViewGroup.Dock = DockStyle.Fill;
                dataGridViewGroup.LabelQueryGroup = Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_GROUP_CAPTION)
                                                        + QueryGroupCount.ToString();
                tableLayoutPanelQueries.Width = dataGridViewGroup.Width;

                //Register Events for dataGridViewGroup
                dataGridViewGroup.OnDataGridViewDragEnter +=
                    new EventHandler<DragEventArgs>(dataGridViewGroup_OnDataGridViewDragEnter);

                dataGridViewGroup.OnRemoveClick +=
                    new EventHandler<DbEventArgs>(dataGridViewGroup_OnRemoveClick);

                dataGridViewGroup.OnDataGridViewComboBoxIndexChanged +=
                    new EventHandler<DbEventArgs>(dataGridViewGroup_OnDataGridViewComboBoxIndexChanged);

                dataGridViewGroup.OnRowsRemoved +=
                    new EventHandler<EventArgs>(dataGridViewGroup_OnRowsRemoved);
                
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

            return dataGridViewGroup;
        }

    

        /// <summary>
        /// Add QueryGroup control
        /// </summary>
        /// <returns></returns>
        private DataGridViewGroup AddDataGridViewToPanel()
        {

            DataGridViewGroup dataGridViewGroup = InitializeDataGridViewGroup();

            try
            {
                OMETrace.WriteFunctionStart();

                tableLayoutPanelQueries.RowStyles.Add(new RowStyle(SizeType.AutoSize, 70));
                tableLayoutPanelQueries.Controls.Add(dataGridViewGroup, 0, QueryGroupCount);
                tableLayoutPanelQueries.SetRow(dataGridViewGroup, QueryGroupCount);

                if (QueryGroupCount == 0)
                    dataGridViewGroup.EnableOperator = false;
                else
                    dataGridViewGroup.Removable = true;

                //increase row count
                QueryGroupCount++;

                tableLayoutPanelQueries.Height = dataGridViewGroup.Height * QueryGroupCount;

                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

            return dataGridViewGroup;
        }

        /// <summary>
        /// Initialize/populates Recent queries
        /// </summary>
        private void InitializeRecentQueries()
        {
            try
            {
                if (Helper.ClassName == null)
                {
                    //Set the recent queries forom cache
                    Helper.PopulateRecentQueryComboBox(Helper.ListOMQueries, comboboxRecentQueries);
                }
                else
                {
                    //Get the recent queries from the repositary
                    List<OMQuery> qrylist = Helper.DbInteraction.GetCurrentRecentConnection().FetchQueriesForAClass(Helper.ClassName);
                    Helper.PopulateRecentQueryComboBox(qrylist, comboboxRecentQueries);
                    Helper.DbInteraction.CloseRecentConn();
                }

                comboboxRecentQueries.DropdownItemSelected += new ToolTipComboBox.DropdownItemSelectedEventHandler(comboboxRecentQueries_DropdownItemSelected);
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

        }

        /// <summary>
        /// Set the query to current connection
        /// </summary>
        /// <param name="query"></param>
        private void AddQueryToCurrentConnection(OMQuery query)
        {
            try
            {
                if (!string.IsNullOrEmpty(query.QueryString))
                {
                    RecentQueries currentConnection = Helper.DbInteraction.GetCurrentRecentConnection();
                    currentConnection.AddQueryToList(query);
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Prepares the OMQuery Groups
        /// </summary>
        /// <param name="datagridview"></param>
        /// <returns></returns>
        private OMQueryGroup PrepareQueryCollection(dbDataGridView datagridview)
        {
            int rowCount = 0;
            string className = string.Empty;
            string fieldName = string.Empty;
            string stringCondition = string.Empty;
            string stringValue = string.Empty;
            string stringOperator = string.Empty;
            string fieldType = string.Empty;

            //Get all the columns names from resource
            string fieldColumnName = Helper.GetResourceString(Common.Constants.QUERY_GRID_FIELD);
            string conditionColumnName = Helper.GetResourceString(Common.Constants.QUERY_GRID_CONDITION);
            string valueColumnName = Helper.GetResourceString(Common.Constants.QUERY_GRID_VALUE);
            string operatorColumnName = Helper.GetResourceString(Common.Constants.QUERY_GRID_OPERATOR);
            string classColumnName = Common.Constants.QUERY_GRID_CALSSNAME_HIDDEN;
            string fieldTypeColumnName = Common.Constants.QUERY_GRID_FIELDTYPE_HIDDEN;


            OMQueryGroup objectManagerQueryGroup = null;

            try
            {
                OMETrace.WriteFunctionStart();

                rowCount = datagridview.RowCount;

                if (rowCount > 0)
                {
                    objectManagerQueryGroup = new OMQueryGroup();
                    stringOperator = datagridview.Rows[0].Cells[operatorColumnName].Value.ToString();
                    CommonValues.LogicalOperators clauseOperator =
                                                            (CommonValues.LogicalOperators)
                                                            Enum.Parse(typeof(CommonValues.LogicalOperators),
                                                            stringOperator);
                    for (int i = 0; i < rowCount; i++)
                    {

                        fieldName = datagridview.Rows[i].Cells[fieldColumnName].Value.ToString();
                        stringCondition = datagridview.Rows[i].Cells[conditionColumnName].Value.ToString();
                        className = datagridview.Rows[i].Cells[classColumnName].Value.ToString();
                        fieldType = datagridview.Rows[i].Cells[fieldTypeColumnName].Value.ToString();

                        //get the value for each expression if value not specified then return null
                        if (datagridview.Rows[i].Cells[valueColumnName].Value != null)
                            stringValue = datagridview.Rows[i].Cells[valueColumnName].Value.ToString();
                        else
                            return null;

                        OMQueryClause queryClause = new OMQueryClause(className, fieldName, stringCondition, stringValue, clauseOperator, fieldType);
                        objectManagerQueryGroup.AddOMQueryClause(queryClause);
                    }
                }

                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

            return objectManagerQueryGroup;

        }

        /// <summary>
        /// Initialise the Attribute DataGrid
        /// </summary>
        private void InitializeAttributesDataGrid()
        {
            try
            {
                dbDataGridAttributes = new dbDataGridView();

                dbDataGridAttributes.Dock = DockStyle.Fill;
                dbDataGridAttributes.MultiSelect = true;
                dbDataGridAttributes.ColumnHeadersHeight = 12;
                dbDataGridAttributes.ColumnHeadersBorderStyle = DataGridViewHeaderBorderStyle.None;
                DataGridViewCellStyle style = new DataGridViewCellStyle();
                style.BackColor = SystemColors.Control;
                style.ForeColor = Color.Black;
                style.Font = new System.Drawing.Font("Tahoma", 8.25F, System.Drawing.FontStyle.Bold);
                dbDataGridAttributes.ColumnHeadersDefaultCellStyle = style;
                dbDataGridAttributes.BuildRowContextMenu();
                splitContainerQueryBuilder.Panel2.Controls.Add(dbDataGridAttributes);

                dbDataGridAttributes.PopulateDisplayGrid(OMControlLibrary.Common.Constants.VIEW_ATTRIBUTES, null);

                //Register Event Handlers
                dbDataGridAttributes.DragEnter += new DragEventHandler(dataGridAttributes_DragEnter);
                dbDataGridAttributes.KeyDown += new KeyEventHandler(dataGridAttributes_KeyDown);
                dbDataGridAttributes.RowsRemoved += new DataGridViewRowsRemovedEventHandler(dataGridAttributes_RowsRemoved);
                dbDataGridAttributes.MouseDown += new MouseEventHandler(dataGridAttributes_MouseDown);
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Checks if Query builder has groups but expressions are removed from all groups
        /// then allow use to get the attributes from other class
        /// </summary>
        internal void CheckForDataGridViewQueryRows()
        {
            int count = 0;

            bool rowsNotFound = false;

            try
            {
                count = tableLayoutPanelQueries.Controls.Count;

                for (int i = 0; i < count; i++)
                {
                    DataGridViewGroup dataGridViewGroup = (DataGridViewGroup)tableLayoutPanelQueries.Controls[i];
                    dbDataGridView dataGridView = dataGridViewGroup.DataGridViewQuery;
                    if (dataGridView.Rows.Count == 0)
                    {
                        rowsNotFound = true;
                    }
                    else
                    {
                        rowsNotFound = false;
                        break;
                    }
                }

                if (dbDataGridAttributes.Rows.Count == 0 && rowsNotFound)
                {
                    //if (Helper.BaseClass != null)
                    Helper.HashTableBaseClass.Clear();
                    buttonRunQuery.Enabled = false;
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Clear all Query Groups and Expressions 
        /// </summary>
        internal void ClearAllQueries()
        {
            int count = 0;

            try
            {
                OMETrace.WriteFunctionStart();

                //Get all query groups added to query builder
                count = tableLayoutPanelQueries.Controls.Count;

                for (int i = count; i > 1; i--)
                {
                    tableLayoutPanelQueries.Controls.RemoveAt(i - 1);
                    QueryGroupCount--;
                }

                DataGridViewGroup dataGridViewGroup = (DataGridViewGroup)tableLayoutPanelQueries.Controls[0];
                dbDataGridView dataGridView = dataGridViewGroup.DataGridViewQuery;

                //Clear all query expressions
                if (dataGridView != null && dataGridView.RowCount > 0)
                {
                    dataGridView.Rows.Clear();
                }

                //Clears the attribute list
                if (dbDataGridAttributes != null && dbDataGridAttributes.RowCount > 0)
                {
                    dbDataGridAttributes.Rows.Clear();
                }

                tableLayoutPanelQueries.Height = tableLayoutPanelQueries.Height - dataGridViewGroup.Height;

                // Remove base class from helper base class hashtable
                // so that next time it will set for the new class
                Helper.HashTableBaseClass.Clear();
               
                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Validate the Query Expression for empty value
        /// </summary>
        /// <returns></returns>
        internal bool IsValidQuery(out string errorMessage)
        {
            int totalQueryGroups = 0;
            errorMessage = string.Empty;

            try
            {
                OMETrace.WriteFunctionStart();

                string valueColumn = Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_GRID_VALUE);

                totalQueryGroups = tableLayoutPanelQueries.Controls.Count;

                for (int i = 0; i < totalQueryGroups; i++)
                {
                    DataGridViewGroup dataGridViewGrp = (DataGridViewGroup)tableLayoutPanelQueries.Controls[i];
                    dbDataGridView datagridView = dataGridViewGrp.DataGridViewQuery;

                    if (totalQueryGroups > 1 && i == 0 && datagridView.Rows.Count == 0)
                    {
                        errorMessage = dataGridViewGrp.LabelQueryGroup +
                                       Helper.GetResourceString(OMControlLibrary.Common.Constants.VALIDATION_DEFAULT_GROUP_IS_EMPTY);
                        return false;
                    }

                    for (int j = 0; j < datagridView.Rows.Count; j++)
                    {
                        string type = datagridView.Rows[j].Cells[OMControlLibrary.Common.Constants.QUERY_GRID_FIELDTYPE_HIDDEN].Value.ToString();
                        if (type != OManager.BusinessLayer.Common.BusinessConstants.STRING && type != OManager.BusinessLayer.Common.BusinessConstants.CHAR) 
                        {
                            if (datagridView.Rows[j].Cells[valueColumn].Value == null)
                            {
                                errorMessage = Helper.GetResourceString(OMControlLibrary.Common.Constants.VALIDATION_MESSAGE_VALUE_NOT_SPECIFIED);
                                return false;
                            }

                            else if (string.IsNullOrEmpty(datagridView.Rows[j].Cells[valueColumn].Value.ToString()))
                            {
                                errorMessage = Helper.GetResourceString(OMControlLibrary.Common.Constants.VALIDATION_MESSAGE_VALUE_NOT_SPECIFIED);
                                return false;
                            }
                        }
                    }
                }

                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }


            return true;
        }

        /// <summary>
        /// Sets the OMQuery to the cached list
        /// </summary>
        /// <param name="omQuery"></param>
        private void SetOMQuery(OMQuery omQuery)
        {
            try
            {
                if (Helper.OMResultedQuery.ContainsKey(omQuery.BaseClass))
                {
                    Helper.OMResultedQuery[omQuery.BaseClass] = omQuery;
                }
                else
                {
                    Helper.OMResultedQuery.Add(omQuery.BaseClass, omQuery);
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Updates the item dragged to the Attributes List 
        /// </summary>
        /// <param name="omAttrib"></param>
        /// <param name="baseclass"></param>
        /// <returns></returns>
        private Hashtable UpdateAttributes(Hashtable omAttrib, string baseclass)
        {
            Hashtable result = new Hashtable();

            try
            {
                IDictionaryEnumerator enumerator = omAttrib.GetEnumerator();
                string modifiedVal = baseclass.Split(CONST_COMMA_CHAR)[0].Split(CONST_DOT_CHAR)[1].ToString();
                while (enumerator.MoveNext())
                {
                    string key = enumerator.Key.ToString();
                    key = modifiedVal + CONST_DOT_STRING + key;
                    result.Add(key, enumerator.Value.ToString());
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

            return result;

        }

        /// <summary>
        /// Resets the Query Expression Group if any group is deleted
        /// </summary>
        private void RenameQueryGroupCaption()
        {
            int totalQueryGroups = 0;

            try
            {
                totalQueryGroups = tableLayoutPanelQueries.Controls.Count;
                for (int i = 0; i < totalQueryGroups; i++)
                {
                    DataGridViewGroup dataGridViewGrp = (DataGridViewGroup)tableLayoutPanelQueries.Controls[i];
                    dataGridViewGrp.LabelQueryGroup =
                        Helper.GetResourceString(Common.Constants.QUERY_GROUP_CAPTION) + i.ToString();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Resets the Query Expression Group if any group is deleted
        /// </summary>
        public List<string> GetAllQueryGroups()
        {
            List<string> groupCollection = new List<string>();
            int totalQueryGroups = 0;

            try
            {
                totalQueryGroups = tableLayoutPanelQueries.Controls.Count;
                for (int i = 0; i < totalQueryGroups; i++)
                {
                    DataGridViewGroup dataGridViewGrp = (DataGridViewGroup)tableLayoutPanelQueries.Controls[i];
                    groupCollection.Add(dataGridViewGrp.LabelQueryGroup);
                    //Helper.GetResourceString(Common.Constants.QUERY_GROUP_CAPTION) + i.ToString();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

            return groupCollection;
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Poulates Query in Query Result pane/recent query selected
        /// </summary>
        /// <param name="omQry"></param>
        public void PopulateQueryBuilderGroup(OMQuery omQry)
        {
            List<OMQueryGroup> listOMQueryGroup = null;
            Hashtable listQueryAttributes = null;
            OMQueryGroup omGroup = null;
            DataGridViewGroup group = null;
            OMQuery omQuery = null;

            try
            {
                OMETrace.WriteFunctionStart();

                //Set the omQuery 
                if (omQry != null)
                {
                    omQuery = omQry;

                    //query is not available then dont do anything
                    if (omQuery == null)
                        return;

                    listOMQueryGroup = omQuery.ListQueryGroup;
                    Helper.HashTableBaseClass.Clear();
                    Helper.BaseClass = Helper.ClassName = omQuery.BaseClass;

                    //Only Add when recent query is populated 
                    //if (queryResult == null && omQuery.ListQueryGroup.Count > 0)
                    if (omQuery.ListQueryGroup.Count > 0)
                    {
                        Helper.HashTableBaseClass.Add(omQuery.BaseClass, string.Empty);
                    }

                    for (int i = 0; i < listOMQueryGroup.Count; i++)
                    {
                        //Add DataGridViewGrop
                        omGroup = listOMQueryGroup[i];
                        if (i != 0)
                        {
                            //Add Query Group
                            group = AddDataGridViewToPanel();
                        }
                        else //if Group is note more than get the first group
                            group = defaultGroup;

                        for (int j = 0; j < omGroup.ListQueryClauses.Count; j++)
                        {
                            //Get the clauses from the group
                            OMQueryClause omQueryClause = omGroup.ListQueryClauses[j];
                            dbDataGridView gridQuery = group.DataGridViewQuery;
                            gridQuery.Rows.Add(1);

                            //Fill the Conditions depending upon the field name
                            gridQuery.FillConditionsCombobox(omQueryClause.FieldType, j);
                            gridQuery.FillOperatorComboBox();

                            gridQuery.Rows[j].Cells[Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_GRID_FIELD)].Value = omQueryClause.Fieldname;
                            gridQuery.Rows[j].Cells[Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_GRID_CONDITION)].Value = omQueryClause.Operator;
                            gridQuery.Rows[j].Cells[Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_GRID_VALUE)].Value = omQueryClause.Value;
                            gridQuery.Rows[j].Cells[Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_GRID_OPERATOR)].Value = omQueryClause.ClauseLogicalOperator.ToString();

                            //Make the operator cell readonly for other than 1st Rows
                            if (j > 0)
                                gridQuery.Rows[j].Cells[Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_GRID_OPERATOR)].ReadOnly = true;

                            gridQuery.Rows[j].Cells[OMControlLibrary.Common.Constants.QUERY_GRID_CALSSNAME_HIDDEN].Value = omQueryClause.Classname;
                            gridQuery.Rows[j].Cells[OMControlLibrary.Common.Constants.QUERY_GRID_FIELDTYPE_HIDDEN].Value = omQueryClause.FieldType;
                        }

                        //Set the logical operator for Query Group
                        group.OperatorComboBox.SelectedItem = omGroup.GroupLogicalOperator.ToString();

                        if (group.DataGridViewQuery.Rows.Count > 0)
                            buttonRunQuery.Enabled = true;
                    }

                    listQueryAttributes = omQuery.AttributeList;

                    int count = 0;
                    if (listQueryAttributes != null)
                    {
                        IDictionaryEnumerator enumerator = listQueryAttributes.GetEnumerator();
                        while (enumerator.MoveNext())
                        {
                            dbDataGridAttributes.Rows.Add(1);
                            dbDataGridAttributes.Rows[count].Cells[0].Value = enumerator.Key.ToString();
                            dbDataGridAttributes.Rows[count].Cells[0].Tag = enumerator.Value.ToString();
                            count++;
                        }
                    }

                    OMETrace.WriteFunctionEnd();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Preapers OMQuery object
        /// </summary>
        /// <returns></returns>
        public OMQuery PrepareOMQuery()
        {
            int totalQueryGroups = 0;


            try
            {
                OMETrace.WriteFunctionStart();

                //Get count of query groups 
                totalQueryGroups = tableLayoutPanelQueries.Controls.Count;

                //iterate through all query groups
                for (int i = 0; i < totalQueryGroups; i++)
                {
                    DataGridViewGroup dataGridViewGrp = (DataGridViewGroup)tableLayoutPanelQueries.Controls[i];

                    dbDataGridView datagridView = dataGridViewGrp.DataGridViewQuery;

                    //Prepare the query expression for each query group
                    OMQueryGroup omQueryGroup = PrepareQueryCollection(datagridView);

                    //if query builder has only one group get new instance of OMQueryGroup
                    //and set the logical operator to empty
                    if (i == 0)
                    {
                        if (omQueryGroup == null)
                        {
                            omQueryGroup = new OMQueryGroup();
                        }
                        omQueryGroup.GroupLogicalOperator = CommonValues.LogicalOperators.EMPTY;
                    }
                    else //Get the selected operatior for each query group
                    {
                        //Ignore empty group
                        if (omQueryGroup == null)
                            continue;

                        omQueryGroup.GroupLogicalOperator = (CommonValues.LogicalOperators)
                                                            Enum.Parse(typeof(CommonValues.LogicalOperators),
                                                            dataGridViewGrp.QueryGroupOperator);
                    }

                    if (omQuery.BaseClass != null)
                        Helper.BaseClass = omQuery.BaseClass;
                    else
                    {
                        if (Helper.HashTableBaseClass.Count > 0)
                        {
                            IDictionaryEnumerator enumerator = Helper.HashTableBaseClass.GetEnumerator();
                            enumerator.MoveNext();
                            string baseClass = enumerator.Key.ToString();

                            Helper.BaseClass = baseClass;
                        }
                    }

                    omQuery.AttributeList = GetSelectedAttributes();//Helper.GetQueryAttributes();

                    //Add OMQueryGroup to OMQuery
                    if (omQueryGroup != null)
                        omQuery.AddOMQuery(omQueryGroup);
                }

                omQuery.QueryString = omQuery.ToString();

                //Set the OMQuery object to the Resulted query
                if (Helper.OMResultedQuery.ContainsKey(omQuery.BaseClass))
                {
                    //update omquery if query is prepared not for first time
                    Helper.OMResultedQuery[omQuery.BaseClass] = omQuery;
                }
                else
                {
                    //Add omquery if query is prepared for first time
                    Helper.OMResultedQuery.Add(omQuery.BaseClass, omQuery);
                }

                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

            return omQuery;
        }

        /// <summary>
        /// Fetch the all the Attributes added to Attribute Grid
        /// </summary>
        public Hashtable GetSelectedAttributes()
        {
            Hashtable list = new Hashtable();

            try
            {
                OMETrace.WriteFunctionStart();
                for (int attributeCount = 0; attributeCount < this.dbDataGridAttributes.RowCount; attributeCount++)
                {
                    list.Add(dbDataGridAttributes.Rows[attributeCount].Cells[0].Value.ToString()
                             , dbDataGridAttributes.Rows[attributeCount].Cells[0].Tag.ToString());
                }

                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
            return list;
        }

        /// <summary>
        /// Set all litrals for this control
        /// </summary>
        public override void SetLiterals()
        {
            try
            {
                labelRecentQueries.Text = Helper.GetResourceString(Common.Constants.LABEL_RECENT_QUERIES_CAPTION);
                labelBuildQuery.Text = Helper.GetResourceString(Common.Constants.LABEL_QUERY_BUILDER_CAPTION);
                buttonAddQueryGroup.Text = Helper.GetResourceString(Common.Constants.BUTTON_ADD_GROUP_CAPTION);
                buttonClearAll.Text = Helper.GetResourceString(Common.Constants.BUTTON_CLEAR_AAL_CAPTION);
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

        }
        #endregion



    }
}

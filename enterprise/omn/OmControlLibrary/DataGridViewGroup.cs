using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Data;
using System.Text;
using System.Windows.Forms;
using OMControlLibrary.Common;
using OManager.BusinessLayer.ObjectExplorer;

using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary
{
    /// <summary>
    /// Class to prepare collection of expression
    /// </summary>
    public partial class DataGridViewGroup : ViewBase
    {
        #region Member Variable

        private bool m_removable = false;
        private bool m_enableOperator = false;
        private string m_queryGroupOperator = string.Empty;
        private string m_typeOfObject = string.Empty;
        private string m_previousCellValue = string.Empty;

        #endregion

        #region Event Handlers

        //Event to handle when row removed from datagridview
        internal event EventHandler<EventArgs> OnRowsRemoved;
        //Event to handle if the group is removed or remove button is clicked for group
        internal event EventHandler<DbEventArgs> OnRemoveClick;
        //Event to handle if item is dragged to the control
        internal event EventHandler<DragEventArgs> OnDataGridViewDragEnter;
        //Event to handle the operator of group is changed
        internal event EventHandler<DbEventArgs> OnDataGridViewComboBoxIndexChanged;
        

        #endregion

        #region Properties

        /// <summary>
        /// Get or Set the Type of object
        /// </summary>
        public string TypeOfObject
        {
            get { return m_typeOfObject; }
            set { m_typeOfObject = value; }
        }

        /// <summary>
        /// Get or Set the operator for each Query Group 
        /// </summary>
        public string QueryGroupOperator
        {
            get { return m_queryGroupOperator; }
            set { m_queryGroupOperator = value; }
        }

        /// <summary>
        /// Get the DataGridView control 
        /// </summary>
        public dbDataGridView DataGridViewQuery
        {
            get { return dbDataGridView; }
        }

        /// <summary>
        /// Make the Operator Combox accessible from different user control
        /// </summary>
        public ComboBox OperatorComboBox 
        {
            get { return comboBoxOperator; }
        }

        /// <summary>
        /// Query Group Expression
        /// </summary>
        public string LabelQueryGroup
        {
            get { return labelQueryGroup.Text; }
            set { labelQueryGroup.Text = value; }
        }

        /// <summary>
        /// Get or Set the Query Group removable
        /// </summary>
        public bool Removable
        {
            get { return labelRemove.Visible; }
            set {
                m_removable = value;
                labelRemove.Visible = m_removable;
                }
        }

        /// <summary>
        /// Get or set the Query Group operator enable or disable
        /// </summary>
        public bool EnableOperator
        {
            get { return comboBoxOperator.Visible; }
            set { 
                m_enableOperator = value;
                comboBoxOperator.Visible = m_enableOperator;
                }
        }

        #endregion

        #region Constructor

        /// <summary>
        /// Default constructor for DataGridView
        /// </summary>
        public DataGridViewGroup()
        {
            InitializeComponent();
        }

        #endregion

        #region Event Handlers

        /// <summary>
        /// Load Event of DataGridView
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DataGridViewGroup_Load(object sender, EventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();

                //Populate the Query Group operator combobox
                string[] operatorList = QueryHelper.GetOperators();
                dbDataGridView.PopulateDisplayGrid(Common.Constants.VIEW_QUERYBUILDER, null);
                comboBoxOperator.Items.AddRange(operatorList);
                comboBoxOperator.SelectedIndex = 0;
                dbDataGridView.MultiSelect = true;
                dbDataGridView.OnDBGridCellClick += 
                    new EventHandler<dbDataGridViewEventArgs>(dbDataGridView_OnDBGridCellClick);
                
                //Build context menu specific to rows
                dbDataGridView.BuildRowContextMenu();
                
                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Handles the multiselection of the rows in DataGridView
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_OnDBGridCellClick(object sender, dbDataGridViewEventArgs e)
        {
            try
            {
                DataGridViewCell cell = e.Data as DataGridViewCell;
                int rowIndex = cell.RowIndex;

                if (rowIndex == Common.Constants.INVALID_INDEX_VALUE)
                {
                    OMETrace.WriteTraceInvalidCondition(Common.Constants.TRACEMESSAGE_INVALIDROW_INDEX);
                    return;
                }

                dbDataGridView.CurrentRow.Selected = false;
                dbDataGridView.Rows[rowIndex].Selected = true;
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Builds the context menu if is null
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_MouseDown(object sender, MouseEventArgs e)
        {
            try
            {
                DataGridView.HitTestInfo hitTestInfo = dbDataGridView.HitTest(e.X, e.Y);
                DataGridViewHitTestType hitTestType = hitTestInfo.Type;
                if (hitTestType == DataGridViewHitTestType.Cell)
                {
                    if (dbDataGridView.ContextMenuStrip == null)
                        dbDataGridView.BuildRowContextMenu();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Selects the operator for group.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void comboBoxOperator_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                if (comboBoxOperator.SelectedItem != null)
                    m_queryGroupOperator = comboBoxOperator.SelectedItem.ToString();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Handle if any item drags in datagridview
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_DragEnter(object sender, DragEventArgs e)
        {
            if (OnDataGridViewDragEnter != null)
            {
                OnDataGridViewDragEnter(sender, e);
            }
        }

        /// <summary>
        /// Cell Click Event: Make all operator cell readonly for other than first row 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_CellClick(object sender, DataGridViewCellEventArgs e)
        {
            try
            {
                if (e.RowIndex == -1)
                    return;
                
                if (dbDataGridView.Rows[e.RowIndex].Cells[e.ColumnIndex] is DataGridViewImageCell)
                {
                    dbDataGridView.Rows.RemoveAt(e.RowIndex);
                    
                    //Make the first operator cell readonly for fist row
                    if ( dbDataGridView.Rows.Count != 0)
                        dbDataGridView.Rows[0].Cells[3].ReadOnly = false;
                }
            }
            catch(Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Registers the SelectedIndexChanged when user starts editing the operator
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_EditingControlShowing(object sender, DataGridViewEditingControlShowingEventArgs e)
        {
            ComboBox operatorComboBox = null;

            try
            {
                if (operatorComboBox == null)
                {
                    if (e.Control is ComboBox)
                    {
                        operatorComboBox = (ComboBox)e.Control;
                        operatorComboBox.SelectedIndexChanged += new EventHandler(operatorComboBox_SelectedIndexChanged);
                    }
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Set the value when user changes the value.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void operatorComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                if (OnDataGridViewComboBoxIndexChanged != null)
                {
                    DbEventArgs eventArgs = new DbEventArgs();
                    eventArgs.Data = dbDataGridView;
                    OnDataGridViewComboBoxIndexChanged(sender, eventArgs);
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// Validate/Commit value to control when vlaue changed/enter in datagridview value column
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_CellEndEdit(object sender, DataGridViewCellEventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();
                string valueColumn = Helper.GetResourceString(Constants.QUERY_GRID_VALUE);

                object value = dbDataGridView.Rows[e.RowIndex].Cells[valueColumn].Value;
                string type = dbDataGridView.Rows[e.RowIndex].Cells[Constants.QUERY_GRID_FIELDTYPE_HIDDEN].Value.ToString();

                if (dbDataGridView.Rows[e.RowIndex].Cells[valueColumn].Value != null
                    && !string.IsNullOrEmpty(dbDataGridView.Rows[e.RowIndex].Cells[valueColumn].Value.ToString()))
                {
                    //Validate the entered value
                    bool isValid = Validations.ValidateDataType(type,ref value);

                    if (!isValid)
                    {
                        //reset the previous value if value is not valid
                        dbDataGridView.Rows[e.RowIndex].Cells[valueColumn].Value = m_previousCellValue;
                        Helper.IsQueryResultUpdated = false; 
                    }
                    else
                    {
                        //set the changed value if value is valid
                        dbDataGridView.Rows[e.RowIndex].Cells[valueColumn].Value = value.ToString();
                        Helper.IsQueryResultUpdated = true; 
                    }

                    m_previousCellValue = string.Empty;

                    OMETrace.WriteFunctionEnd();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

        }

        /// <summary>
        /// Handles the Data Error if occured
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_DataError(object sender, System.Windows.Forms.DataGridViewDataErrorEventArgs e)
        {
            e.Cancel = true;
        }

        /// <summary>
        /// CellBeginEdit: Get the previous value of cell if exit.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_CellBeginEdit(object sender, DataGridViewCellCancelEventArgs e)
        {
            try
            {
                string valueColumn = Helper.GetResourceString(Constants.QUERY_GRID_VALUE);

                if (dbDataGridView.Rows[e.RowIndex].Cells[valueColumn].Value != null)
                    m_previousCellValue = dbDataGridView.Rows[e.RowIndex].Cells[valueColumn].Value.ToString();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        /// <summary>
        /// RowRemoved: resets the height of the grid if row removed.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_RowsRemoved(object sender, DataGridViewRowsRemovedEventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();

                if (dbDataGridView.Rows.Count == 0)
                {
                    if (OnRowsRemoved != null)
                        OnRowsRemoved(sender, e);
                }
                else //set the opertator column of the first row readonly false
                    dbDataGridView.Rows[0].Cells[3].ReadOnly = false;

                if (this.dbDataGridView.RowCount > 0)
                {
                    int height = this.dbDataGridView.ColumnHeadersHeight + this.dbDataGridView.RowTemplate.Height +
                        (this.dbDataGridView.RowTemplate.Height * this.dbDataGridView.RowCount);

                    if (this.dbDataGridView.Height > 106)
                    {
                        this.dbDataGridView.Height = height;
                        this.Height = panelTop.Height + height;
                    }
                }
                else
                {
                    this.dbDataGridView.Height = 106;
                    this.Height = panelTop.Height + this.dbDataGridView.Height;
                }

                OMETrace.WriteFunctionEnd();
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }

        }

        /// <summary>
        /// RowAdded: sets the height of the datagridview when row is added
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_RowsAdded(object sender, DataGridViewRowsAddedEventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();

                if (this.dbDataGridView.RowCount > 0)
                {
                    int height = this.dbDataGridView.ColumnHeadersHeight + this.dbDataGridView.RowTemplate.Height +
                        (this.dbDataGridView.RowTemplate.Height * this.dbDataGridView.RowCount);

                    if (height > 106)
                    {
                        this.dbDataGridView.Height = height;
                        this.Height = panelTop.Height + height;
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
        /// DataGridView KeyDown: removed selected row if delete key is pressed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void dbDataGridView_KeyDown(object sender, KeyEventArgs e)
        {
            try
            {
                OMETrace.WriteFunctionStart();
                
                if (e.KeyCode == Keys.Delete)
                {
                    if (dbDataGridView.Rows.Count > 0)
                    {
                        //Deletes the multiple selected rows
                        if (dbDataGridView.SelectedRows.Count > 1)
                        {
                            List<DataGridViewRow> selectedRows = new List<DataGridViewRow>();

                            int rowCount = dbDataGridView.Rows.Count;
                            for (int i = 0; i < rowCount; i++)
                            {
                                if (dbDataGridView.Rows[i].Selected)
                                    selectedRows.Add(dbDataGridView.Rows[i]);
                            }

                            for (int i = 0; i < selectedRows.Count; i++)
                            {
                                dbDataGridView.Rows.Remove(selectedRows[i]);
                            }
                            selectedRows.Clear();
                        }
                        else //Deletes single selected rows
                        {
                            dbDataGridView.Rows.RemoveAt(dbDataGridView.SelectedCells[0].OwningRow.Index);
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
        }

        /// <summary>
        /// changes the effect as visual studio close button
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void labelRemove_MouseHover(object sender, EventArgs e)
        {
            labelRemove.FlatStyle = FlatStyle.Popup;
            labelRemove.BackColor = Color.FromArgb(162, 174, 200);
            labelRemove.BorderStyle = BorderStyle.FixedSingle;
        }

        /// <summary>
        /// Resets the effects of the remove button
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void labelRemove_MouseLeave(object sender, EventArgs e)
        {
            labelRemove.FlatStyle = FlatStyle.Standard;
            labelRemove.BackColor = SystemColors.Control;
            labelRemove.BorderStyle = BorderStyle.None;
        }

        /// <summary>
        /// Remove the datagridview group
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void labelRemove_Click(object sender, EventArgs e)
        {
            try
            {
                if (OnRemoveClick != null)
                {
                    DbEventArgs eventArg = new DbEventArgs();
                    eventArg.Data = this;
                    OnRemoveClick(sender, eventArg);
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        #endregion 
    }

    /// <summary>
    /// Class for custom event argument
    /// </summary>
    public class DbEventArgs : EventArgs
    {
        private object m_data = null;

        public object Data
        {
            get { return m_data; }
            set { m_data = value; }
        }

        public DbEventArgs(object data)
        {
            m_data = data;
        }

        public DbEventArgs()
        { 
        
        }
    }

   
}

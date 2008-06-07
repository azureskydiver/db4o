using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace OME.AdvancedDataGridView
{
    /// <summary>
    /// This class is associated with dbDataGridViewDateTimePickerColumn.
    /// It is simple DataGridView TextBox Cell but in Edit mode it 
    /// show AIDataGridViewDateTimePickerEditingControl.
    /// </summary>
    public class TreeGridViewDateTimePickerCell : DataGridViewTextBoxCell
    {
        #region Member Variables

        //string m_CellData = string.Empty;

        #endregion Member Variables

        #region Constructor

        public TreeGridViewDateTimePickerCell()
            : base()
        {

        }
        #endregion Constructor

        #region Override methods

        /// <summary>
        /// Initialize Editing Control, control type is AIDataGridViewDateTimePickerEditingControl.
        /// </summary>
        /// <param name="rowIndex"></param>
        /// <param name="initialFormattedValue"></param>
        /// <param name="dataGridViewCellStyle"></param>
        public override void InitializeEditingControl(int rowIndex, object
                initialFormattedValue, DataGridViewCellStyle dataGridViewCellStyle)
        {
            // Set the value of the editing control to the current cell value.
            try
            {
                base.InitializeEditingControl(rowIndex,
                    initialFormattedValue,
                    dataGridViewCellStyle);

                string typeOfValue = this.DataGridView.Rows[rowIndex].Cells[2].Value.ToString();

                if (typeOfValue == typeof(System.DateTime).ToString())
                {
                    TreeGridViewDateTimePickerEditingControl ctl =
                        DataGridView.EditingControl as TreeGridViewDateTimePickerEditingControl;

                    if (this.Value != null && this.Value != this.OwningColumn.DefaultCellStyle)
                    {
                        try
                        {
                            ctl.Value = Convert.ToDateTime(this.Value.ToString());
                        }
                        catch (Exception ex)
                        {
                            ex.ToString();
                            ctl.Value = System.DateTime.Now;
                        }
                    }
                }
                else if (typeOfValue == typeof(System.Boolean).ToString())
                {
                    //intializing editing control (DataGridViewComboBoxEditingControl)
                    DataGridViewComboBoxEditingControl ctl =
                        this.DataGridView.EditingControl as DataGridViewComboBoxEditingControl;

                    //setting combox style
                    ctl.DropDownStyle = ComboBoxStyle.DropDownList;
                    ctl.FlatStyle = FlatStyle.Popup;
                    //ctl.DropDownWidth = this.Value.ToString().Length;
                    FillBoolColumnValue(ctl);

                    if (this.Value != null && this.Value != this.OwningColumn.DefaultCellStyle)
                    {
                        try
                        {
                            ctl.EditingControlFormattedValue  = this.Value.ToString();
                        }
                        catch (Exception ex)
                        {
                            ex.ToString();
                            ctl.SelectedItem = ctl.Items[0].ToString();
                        }
                    }
                    ctl.Width = this.OwningColumn.Width;
                }
            }
            catch (Exception oEx)
            {
                string str = oEx.Message;
                
            }
        }

        public override Type EditType
        {
            get
            {
                // Return the type of the editing contol that AIDataGridViewDateTimePickerCell uses.
                
                Type controlType = typeof(DataGridViewTextBoxEditingControl);

                try
                {
                    string typeOfValue  = this.DataGridView.Rows[this.RowIndex].Cells[2].Value.ToString();
                    
                    if (typeOfValue == typeof(System.DateTime).ToString())
                    {
                        controlType = typeof(TreeGridViewDateTimePickerEditingControl);
                    }
                    else if (typeOfValue == typeof(System.Boolean).ToString())
                    {
                        controlType = typeof(DataGridViewComboBoxEditingControl);
                    }
                }
                catch (Exception oEx)
                {
                }
                return controlType;
            }
        }

        public override Type ValueType
        {
            get
            {
                // Return the type of the value that AIDataGridViewDateTimePickerCell contains.
                return typeof(String);
            }
        }

        public void FillBoolColumnValue(DataGridViewComboBoxEditingControl ctrl)
        {
            try
            {
                ctrl.Items.Clear();
                ctrl.Items.AddRange(new object[] { "True" , "False" });
                ctrl.SelectedIndex = 0;
            }
            catch (Exception oEx)
            {
               // LoggingHelper.ShowMessage(oEx);
            }
        }

        #endregion Override methods
    }
}

using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using OMControlLibrary.Common;

namespace OMControlLibrary.Common
{
	/// <summary>
	/// This class is associated with dbDataGridViewDateTimePickerColumn.
	/// It is simple DataGridView TextBox Cell but in Edit mode it 
	/// show AIDataGridViewDateTimePickerEditingControl.
	/// </summary>
	public class dbDataGridViewDateTimePickerCell : DataGridViewTextBoxCell
	{
		#region Member Variables

		//string m_CellData = string.Empty;

		#endregion Member Variables

		#region Constructor

		public dbDataGridViewDateTimePickerCell()
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

				string typeOfValue = string.Empty;

				if (this.Tag == null)
					typeOfValue = this.DataGridView.Rows[rowIndex].Cells[Constants.QUERY_GRID_FIELDTYPE_HIDDEN].Value.ToString();

				if (typeOfValue == typeof(System.DateTime).ToString() ||
					(this.Tag != null && this.Tag.ToString() == typeof(System.DateTime).ToString()))
				{
					dbDataGridViewDateTimePickerEditingControl ctl =
						DataGridView.EditingControl as dbDataGridViewDateTimePickerEditingControl;

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
				else if (typeOfValue == typeof(System.Boolean).ToString() ||
					(this.Tag != null && this.Tag.ToString() == typeof(System.Boolean).ToString()))
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
							ctl.EditingControlFormattedValue = this.Value.ToString();
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
					string typeOfValue = string.Empty;

					if (this.Tag == null)
					{
						try
						{
							typeOfValue = this.DataGridView.Rows[this.RowIndex].Cells[Constants.QUERY_GRID_FIELDTYPE_HIDDEN].Value.ToString();
						}
						catch
						{
							typeOfValue = string.Empty;
						}
					}
					if (typeOfValue == typeof(System.DateTime).ToString() ||
						(this.Tag != null && this.Tag.ToString() == typeof(System.DateTime).ToString()))
					{
						controlType = typeof(dbDataGridViewDateTimePickerEditingControl);
					}
					else if (typeOfValue == typeof(Boolean).ToString() ||
						(Tag != null && Tag.ToString() == typeof(Boolean).ToString()))
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
				ctrl.Items.AddRange(new object[] { OManager.BusinessLayer.Common.BusinessConstants.CONDITION_TRUE, 
                            OManager.BusinessLayer.Common.BusinessConstants.CONDITION_FALSE});
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

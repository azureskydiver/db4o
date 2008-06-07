namespace OMControlLibrary
{
    partial class RecentQueries
    {
        /// <summary> 
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
            this.dataGridViewRecentQueries = new OMControlLibrary.Common.dbDataGridView();
            this.RecentQueriesColumn = new System.Windows.Forms.DataGridViewTextBoxColumn();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridViewRecentQueries)).BeginInit();
            this.SuspendLayout();
            // 
            // dataGridViewRecentQueries
            // 
            this.dataGridViewRecentQueries.AllowDrop = true;
            this.dataGridViewRecentQueries.AllowUserToAddRows = false;
            this.dataGridViewRecentQueries.AllowUserToDeleteRows = false;
            this.dataGridViewRecentQueries.AllowUserToOrderColumns = true;
            this.dataGridViewRecentQueries.AllowUserToResizeRows = false;
            this.dataGridViewRecentQueries.BackgroundColor = System.Drawing.SystemColors.ActiveCaptionText;
            this.dataGridViewRecentQueries.ColumnHeadersBorderStyle = System.Windows.Forms.DataGridViewHeaderBorderStyle.None;
            dataGridViewCellStyle1.BackColor = System.Drawing.SystemColors.GradientInactiveCaption;
            dataGridViewCellStyle1.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold);
            dataGridViewCellStyle1.ForeColor = System.Drawing.Color.Black;
            this.dataGridViewRecentQueries.ColumnHeadersDefaultCellStyle = dataGridViewCellStyle1;
            this.dataGridViewRecentQueries.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataGridViewRecentQueries.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.RecentQueriesColumn});
            dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.TopLeft;
            dataGridViewCellStyle2.BackColor = System.Drawing.SystemColors.Window;
            dataGridViewCellStyle2.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            dataGridViewCellStyle2.ForeColor = System.Drawing.SystemColors.ControlText;
            dataGridViewCellStyle2.SelectionBackColor = System.Drawing.SystemColors.Highlight;
            dataGridViewCellStyle2.SelectionForeColor = System.Drawing.SystemColors.HighlightText;
            dataGridViewCellStyle2.WrapMode = System.Windows.Forms.DataGridViewTriState.False;
            this.dataGridViewRecentQueries.DefaultCellStyle = dataGridViewCellStyle2;
            this.dataGridViewRecentQueries.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dataGridViewRecentQueries.EnableHeadersVisualStyles = false;
            this.dataGridViewRecentQueries.GridColor = System.Drawing.SystemColors.ActiveCaptionText;
            this.dataGridViewRecentQueries.Location = new System.Drawing.Point(0, 0);
            this.dataGridViewRecentQueries.MultiSelect = false;
            this.dataGridViewRecentQueries.Name = "dataGridViewRecentQueries";
            this.dataGridViewRecentQueries.ReadOnly = true;
            this.dataGridViewRecentQueries.RowHeadersVisible = false;
            dataGridViewCellStyle3.SelectionBackColor = System.Drawing.SystemColors.MenuHighlight;
            dataGridViewCellStyle3.SelectionForeColor = System.Drawing.Color.White;
            this.dataGridViewRecentQueries.RowsDefaultCellStyle = dataGridViewCellStyle3;
            this.dataGridViewRecentQueries.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataGridViewRecentQueries.ShowCellErrors = false;
            this.dataGridViewRecentQueries.ShowEditingIcon = false;
            this.dataGridViewRecentQueries.ShowRowErrors = false;
            this.dataGridViewRecentQueries.Size = new System.Drawing.Size(383, 150);
            this.dataGridViewRecentQueries.TabIndex = 0;
            this.dataGridViewRecentQueries.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dataGridViewRecentQueries_CellDoubleClick);
            
            // 
            // RecentQueriesColumn
            // 
            this.RecentQueriesColumn.HeaderText = "Recent Queries";
            this.RecentQueriesColumn.Name = "RecentQueriesColumn";
            this.RecentQueriesColumn.ReadOnly = true;
            // 
            // RecentQueries
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.ActiveCaptionText;
            this.Controls.Add(this.dataGridViewRecentQueries);
            this.Name = "RecentQueries";
            this.Size = new System.Drawing.Size(383, 150);
            this.Load += new System.EventHandler(this.RecentQueries_Load);
            this.Resize += new System.EventHandler(this.RecentQueries_Resize);
            ((System.ComponentModel.ISupportInitialize)(this.dataGridViewRecentQueries)).EndInit();
            this.ResumeLayout(false);

        }

       

        #endregion

        private OMControlLibrary.Common.dbDataGridView dataGridViewRecentQueries;
        private System.Windows.Forms.DataGridViewTextBoxColumn RecentQueriesColumn;



    }
}

using System;
using System.ComponentModel;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace OMControlLibrary.CustomControls
{
    public partial class propertiesPanel : Panel
    {
        public propertiesPanel()
        {
            //this.SetStyle(ControlStyles.CacheText |
            //     ControlStyles.AllPaintingInWmPaint |
            //     ControlStyles.UserPaint |
            //     ControlStyles.OptimizedDoubleBuffer |
            //     ControlStyles.Opaque, true);
            SetDefaultProperties();
        }

        /// <summary>
        /// This method is used to set default behaviour of the DataGridView.
        /// </summary>
        private void SetDefaultProperties()
        {
            this.BackColor = Color.White;
            this.Visible = true;
          
        }

        public void AddLabel(string Labeltext, Point p)
        {
            Label lbl = new Label();
            lbl.Text = Labeltext;
            lbl.Location = p;
            this.Controls.Add(lbl);
        }

    }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace OMControlLibrary
{
	public partial class ProgressBar : Form
	{
		public ProgressBar()
		{
			this.SetStyle(ControlStyles.CacheText |
			   ControlStyles.AllPaintingInWmPaint |
			   ControlStyles.UserPaint |
			   ControlStyles.OptimizedDoubleBuffer |
			   ControlStyles.Opaque, true);
			InitializeComponent();
		}
	}
}
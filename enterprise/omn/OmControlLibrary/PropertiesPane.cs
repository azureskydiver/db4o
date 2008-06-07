using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using OMControlLibrary.Common;
using System.Collections;
using OManager.DataLayer.PropertyTable;
using OManager.BusinessLayer.QueryManager;
using OMControlLibrary.CustomControls;

namespace OMControlLibrary
{
    public partial class PropertiesPane : ViewBase
    {
        string className;
        long objectId;

        dbDataGridView dataGridViewClassProperties = null;
        Panel panelForDbProperties = null;
        public PropertiesPane()
        {
            InitializeComponent();
        }

        public long ObjectId
        {
            get { return objectId; }
            set { objectId = value; }
        }

        public string ClassName
        {
            get { return className; }
            set { className = value; }
        }

        private void PropertiesPane_Load(object sender, EventArgs e)
        {
            if (Helper.ClassName != null)
            {
                this.ClassName = Helper.ClassName; //Chanaged by om
                dataGridViewClassProperties = new dbDataGridView();
                dataGridViewClassProperties.Size = this.Size;
                this.Controls.Add(dataGridViewClassProperties);

                ArrayList fieldPropertiesList = GetFieldsForAllClass();

                ListingHelper.PopulateDisplayGrid(dataGridViewClassProperties, Constants.VIEW_CLASSPOPERTY
                    , fieldPropertiesList);
                dataGridViewClassProperties.Show();
            }
            else
            {
                //dataGridViewClassProperties.Visible = false;
                panelForDbProperties = new propertiesPanel();
                panelForDbProperties.Size = this.Size;
                this.Controls.Add(panelForDbProperties);

                dbInteraction DbInteraction=new dbInteraction();
                Point tempPoint = new Point(5, 5);
               
                Point p1 = AddLabel("Total Size of Db: ", tempPoint);
                tempPoint = new Point(p1.X + 100, p1.Y);
                Point p2 = AddLabel(DbInteraction.GetTotalDbSize().ToString() , tempPoint);
             
                tempPoint=new Point(p1.X,p1.Y+30);
                Point p3 = AddLabel( "Number of classes in the database: ", tempPoint);
            
                tempPoint = new Point(p3.X + 100, p3.Y);
              
                Point p4 = AddLabel(DbInteraction.NoOfClassesInDb().ToString(), tempPoint);
                tempPoint = new Point(p3.X, p4.Y + 30);
                
                Point p5 = AddLabel("Free Space in Database :", tempPoint);

                tempPoint = new Point(p5.X + 100, p5.Y);

                Point p6 = AddLabel(DbInteraction.GetFreeSizeOfDb().ToString(), tempPoint);
            }
        }

        private Point AddLabel(string LblText, Point p)
        {
            Label lbl = new Label();
            lbl = new Label();
            lbl.Text = LblText;
            lbl.ForeColor = Color.DarkBlue;
            if(p!=null)
            lbl.Location = p;
            panelForDbProperties.Controls.Add(lbl);
            lbl.Show();
            return lbl.Location;
        }

        private ArrayList GetFieldsForAllClass()
        {
            dbInteraction dbInteractionObj = new dbInteraction();
            ClassPropertiesTable classPropTable = dbInteractionObj.GetClassProperties(ClassName);
            return classPropTable.FieldEntries;

        }

        private void PropertiesPane_Resize(object sender, EventArgs e)
        {
            if(dataGridViewClassProperties!=null)
                this.dataGridViewClassProperties.Size = this.Size;
            if (panelForDbProperties != null)
                this.panelForDbProperties.Size = this.Size;
        }
    }
}

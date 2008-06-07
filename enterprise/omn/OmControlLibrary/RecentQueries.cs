using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using OMControlLibrary.Common;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.Login;
using System.Collections;

namespace OMControlLibrary
{
    public partial class RecentQueries : ViewBase
    {
        internal event EventHandler<DbEventArgs> OnQueryBuilderRecentQueryCellDoubleClick;

        public RecentQueries()
        {
            InitializeComponent();
            
        }



        private void RecentQueries_Load(object sender, EventArgs e)
        {
            try
            {
                dataGridViewRecentQueries.Columns[0].Width = dataGridViewRecentQueries.Width;
                if (Helper.ClassName == null)
                {
                //    List<OMQuery> qrylist = Helper.DbInteraction.GetCurrentRecentConnection().FetchAllQueries(Helper.Strconnection);
                //Helper.PopulateGrid(qrylist, dataGridViewRecentQueries);
                    Helper.PopulateGrid(Helper.RecQueries, dataGridViewRecentQueries);
                }
                else
                {
                    List<OMQuery> qrylist = Helper.DbInteraction.GetCurrentRecentConnection().FetchQueriesForAClass(Helper.ClassName,Helper.Strconnection );
                    Helper.PopulateGrid(qrylist, dataGridViewRecentQueries);
                }
            }
            catch
            { 
            
            }
        }
     

        private void RecentQueries_Resize(object sender, EventArgs e)
        {
            //dataGridViewRecentQueries.Width = this.Width;
            dataGridViewRecentQueries.Columns[0].Width = dataGridViewRecentQueries.Width - 5;
        }


        private void dataGridViewRecentQueries_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
        {
            try
            {
                DbEventArgs eventArg = new DbEventArgs();
                eventArg.Data = ((dbDataGridView)sender).SelectedRows[0].Tag;
                OMQuery omQuery = (OMQuery)((dbDataGridView)sender).SelectedRows[0].Tag;


                SetOMQuery(omQuery);

                Helper.ListQueryAttributes = UpdateAttributes(omQuery.AttributeList, omQuery.BaseClass);

                Helper.ClassName = omQuery.BaseClass;

                if(OnQueryBuilderRecentQueryCellDoubleClick != null)
                   OnQueryBuilderRecentQueryCellDoubleClick(sender, eventArg);
                
            }
            catch (Exception ex) {  }

        }

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
            catch (Exception) { }
        }


        private Hashtable UpdateAttributes(Hashtable omAttrib, string baseclass)
        {
            Hashtable result = new Hashtable();

            try
            {

                IDictionaryEnumerator enumerator = omAttrib.GetEnumerator();
                string modifiedVal = baseclass.Split(',')[0].Split('.')[1].ToString();
                while (enumerator.MoveNext())
                {
                      //enumerator.Value.ToString();
                    string key = enumerator.Key.ToString();
                    
                    key = modifiedVal + "." + key;

                    result.Add(key, enumerator.Value.ToString());
                }
            }
            catch (Exception)
            {
               
            }

            return result;

        }
    }
}

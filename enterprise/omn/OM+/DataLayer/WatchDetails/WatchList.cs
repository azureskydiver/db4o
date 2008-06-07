using System;
using System.Collections.Generic;
using System.Text;

namespace OManager.DataLayer.WatchDetails
{
    class WatchList
    {
        List<WatchItems> m_watchList;

        internal List<WatchItems> Watch_List
        {
            get { return this.m_watchList; }
            set { this.m_watchList = value; }
        }

        public void AddItemsToWatch(WatchItems wt)
        {
            Watch_List.Add(wt);
        }
    }
}

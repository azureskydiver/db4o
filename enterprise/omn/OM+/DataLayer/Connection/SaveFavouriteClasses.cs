using System;
using System.Collections.Generic;
using OManager.BusinessLayer.Login;
using System.Text;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.Connection
{
    class FavouriteList
    {
        List<FavouriteFolder> m_lstFavfolder;

        internal List<FavouriteFolder> lstFavFolder
        {
            get { return m_lstFavfolder; }
            set { m_lstFavfolder = value; }
        }
        ConnParams m_connParam;

        public ConnParams ConnParam
        {
            get { return m_connParam; }
            set { m_connParam = value; }
        }
        [Transient]
        IObjectContainer container = null;

        public FavouriteList(ConnParams connParam)
        {
            m_connParam = connParam;           
            m_lstFavfolder = new List<FavouriteFolder>(); 
        }
        internal void AddFolderToDatabase(FavouriteFolder favFolder)
        {
            try
            {
                container = Db4oClient.RecentConn;
                if (m_lstFavfolder != null)
                {
                    FavouriteList favList = FetchAllFavouritesForAConnection();
                    container.Activate(favList, 5);
                    if (favList == null)
                    {
                        favList = new FavouriteList(m_connParam);
                        List<FavouriteFolder> lstFavfolder = new List<FavouriteFolder>();

                        lstFavfolder.Add(favFolder);
                        container.Set(favList);
                        container.Commit();
                        return;
                    }

                    List<FavouriteFolder> lstFavFolder = favList.lstFavFolder;
                    bool check = false;
                    FavouriteFolder temp = null;
                    foreach (FavouriteFolder str in lstFavFolder)
                    {
                        if (str != null)
                        {
                            if (str.FolderName.Equals(favFolder.FolderName))
                            {
                                temp = str;
                                check = true;
                                break;
                            }
                        }
                    }
                    if (check == false)
                    {

                        lstFavFolder.Add(favFolder);
                    }
                    else
                    {
                        lstFavFolder.Remove(temp);
                        lstFavFolder.Add(favFolder);
                        container.Delete(temp);
                    }


                    favList.lstFavFolder = lstFavFolder;
                    container.Ext().Set(favList, 5);
                    container.Commit();


                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);  
            }
        }


        internal void RemoveFolderfromDatabase(FavouriteFolder favFolder)
        {
            try
            {
                container = Db4oClient.RecentConn;
                FavouriteList favList = FetchAllFavouritesForAConnection();


                List<FavouriteFolder> lstFavFolder = favList.lstFavFolder;
                bool check = false;
                FavouriteFolder temp = null;
                foreach (FavouriteFolder str in lstFavFolder)
                {
                    if (str.FolderName.Equals(favFolder.FolderName))
                    {
                        temp = str;
                        check = true;
                        break;
                    }
                }
                if (check == true)
                {
                    lstFavFolder.Remove(temp);
                    favList.lstFavFolder = lstFavFolder;
                    container.Delete(temp);
                    container.Ext().Set(favList, 5);
                    container.Commit();
                }
            }
            
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);  
            }

        }


        internal void RenameFolderInDatabase(FavouriteFolder oldFavFolder, FavouriteFolder newFavFolder)
        {
            try
            {
                container = Db4oClient.RecentConn;
                FavouriteList favList = FetchAllFavouritesForAConnection();


                List<FavouriteFolder> lstFavFolder = favList.lstFavFolder;
                bool check = false;
                FavouriteFolder temp = null;
                foreach (FavouriteFolder str in lstFavFolder)
                {
                    if (str != null)
                    {
                        if (str.FolderName.Equals(oldFavFolder.FolderName))
                        {
                            temp = str;
                            check = true;
                            break;
                        }
                    }
                }
                if (check == true)
                {
                    lstFavFolder.Remove(temp);
                    container.Delete(temp);
                    temp.FolderName = newFavFolder.FolderName;
                    lstFavFolder.Add(temp);
                    favList.lstFavFolder = lstFavFolder;
                    container.Ext().Set(favList, 5);
                    container.Commit();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

        }
       
        internal FavouriteList FindFolderWithClassesByFolderName(string folderName)
        {
            FavouriteList FavList = null;
            try
            {
                container = Db4oClient.RecentConn;
                IQuery query = container.Query();
                query.Constrain(typeof(FavouriteList));
                query.Descend("m_connParam").Descend("m_connection").Constrain(m_connParam.Connection);
                query.Descend("m_lstFavfolder").Descend("m_folderName").Constrain(folderName);
                IObjectSet objSet = query.Execute();
                if (objSet.Count != 0)
                {
                    FavList = (FavouriteList)objSet.Next();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

            return FavList ;
        }
      

        private FavouriteList FetchAllFavouritesForAConnection()
        {
            FavouriteList FavList = null;
            try
            {

                container = Db4oClient.RecentConn;
                IQuery query = container.Query();
                query.Constrain(typeof(FavouriteList));
                query.Descend("m_connParam").Descend("m_connection").Constrain(m_connParam.Connection);                
                IObjectSet objSet = query.Execute();
                if (objSet != null)
                {
                    if (objSet.Count != 0)
                    {
                        FavList = (FavouriteList)objSet.Next();
                    }
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

            return FavList;
        }

        internal List<FavouriteFolder> ReturnFavouritFolderList()
        {
            List<FavouriteFolder> FavList = null;
            try
            {

                FavouriteList Fav = FetchAllFavouritesForAConnection();
                if (Fav != null)
                {
                    FavList = Fav.lstFavFolder;                    
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

            return FavList;
        }

        internal void RemoveFavouritFolderForAConnection()
        {

            try
            {
                FavouriteList Fav = FetchAllFavouritesForAConnection();
                if (Fav != null)
                {
                    //foreach (FavouriteFolder favFolder in Fav.lstFavFolder)
                    //{

                    for (int i = 0; i < Fav.lstFavFolder.Count; i++)
                    {
                        FavouriteFolder favFolder = Fav.lstFavFolder[i];
                        if (favFolder != null)
                        {
                            //Fav.lstFavFolder.Remove(favFolder);
                            //chkCount = Fav.lstFavFolder.Count;
                            container.Delete(favFolder);
                        }

                    }
                    Fav.lstFavFolder.Clear();                      
                    container.Ext().Set(Fav, 5);
                    container.Commit();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

            
        }

    }

   
}

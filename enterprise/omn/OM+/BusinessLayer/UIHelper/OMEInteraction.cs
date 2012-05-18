using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using OManager.BusinessLayer.Login;
using OManager.DataLayer.Connection;

namespace OManager.BusinessLayer.UIHelper
{
    public class OMEInteraction
    {
        public static RecentQueries GetCurrentRecentConnection()
        {
            return Db4oClient.CurrentRecentConnection;
        }

        public static void SetCurrentRecentConnection(RecentQueries conn)
        {
            Db4oClient.CurrentRecentConnection = conn;
        }

        public static void SaveRecentConnection(RecentQueries recQueries)
        {
            Config.Config.Instance.SaveRecentConnection(recQueries);
        }
        public static void CloseOMEdb()
        {

            SaveRecentConnection(Db4oClient.CurrentRecentConnection);
            Db4oClient.CloseRecentConnectionFile();
        }

        public static void RemoveFavFolder()
        {
            FavouriteList favouriteList = new FavouriteList(GetCurrentRecentConnection().ConnParam);
            favouriteList.RemoveFavouritFolderForAConnection();
        }

        public static void RemoveSearchString()
        {
            GroupofSearchStrings SearchStringList = new GroupofSearchStrings(GetCurrentRecentConnection().ConnParam);
            SearchStringList.RemovesSearchStringsForAConnection();
        }

        public static void RemoveRecentQueries()
        {
            RecentQueries recentQueries = new RecentQueries(GetCurrentRecentConnection().ConnParam);
            recentQueries.deleteRecentQueriesForAConnection();
        }

        public static void SaveFavourite(FavouriteFolder favFolder)
        {
            FavouriteList lstFav = new FavouriteList(GetCurrentRecentConnection().ConnParam);
            lstFav.AddFolderToDatabase(favFolder);
        }

        public static void UpdateFavourite(FavouriteFolder favFolder)
        {
            FavouriteList lstFav = new FavouriteList(GetCurrentRecentConnection().ConnParam);
            lstFav.RemoveFolderfromDatabase(favFolder);
        }

        public static void RenameFolderInDatabase(FavouriteFolder oldFav, FavouriteFolder newFav)
        {
            FavouriteList lstFav = new FavouriteList(GetCurrentRecentConnection().ConnParam);
            lstFav.RenameFolderInDatabase(oldFav, newFav);
        }


        public static FavouriteFolder GetFolderfromDatabaseByFoldername(string folderName)
        {
            FavouriteList lstFav = new FavouriteList(GetCurrentRecentConnection().ConnParam);
            lstFav = lstFav.FindFolderWithClassesByFolderName(folderName);
            return lstFav.lstFavFolder[0];
        }

        public static List<string> GetSearchString()
        {
            GroupofSearchStrings searchStrings = new GroupofSearchStrings(GetCurrentRecentConnection().ConnParam);
            return searchStrings.ReturnStringList();
        }


        public static void SaveSearchString(SeachString searchString)
        {
            GroupofSearchStrings searchStrings = new GroupofSearchStrings(GetCurrentRecentConnection().ConnParam);
            if (searchString.SearchString != String.Empty)
                searchStrings.AddSearchStringToList(searchString);
        }

        public static List<FavouriteFolder> GetFavourites()
        {
            FavouriteList lstFav = new FavouriteList(GetCurrentRecentConnection().ConnParam);
            return lstFav.ReturnFavouritFolderList();
        }

        public static long GetTimeforFavCreation()
        {
            FavouriteList lstFav = new FavouriteList(GetCurrentRecentConnection().ConnParam);
            return lstFav.ReturnTimeWhenFavouriteListCreated();
        }

        public static long GetTimeforSearchStringCreation()
        {
            GroupofSearchStrings lstsearchstring = new GroupofSearchStrings(GetCurrentRecentConnection().ConnParam);
            return lstsearchstring.ReturnTimeWhenSearchStringCreated();
        }

        public static long GetTimeforRecentQueriesCreation()
        {
            RecentQueries rQueries = new RecentQueries(GetCurrentRecentConnection().ConnParam);
            return rQueries.ReturnTimeWhenRecentQueriesCreated();
        }

        public static void DeleteConfigConnection(string path, ConnParams connnection)
        {
            RecentQueries rQueries = new RecentQueries(connnection);
            rQueries.RemoveCustomConfigPath(path); 
        }
        
        public static List<RecentQueries> FetchRecentQueries(bool checkRemote)
        {
            return Config.Config.Instance.GetRecentQueries(checkRemote);
        }
        public static void SetProxyInfo(ProxyAuthentication proxyInfo)
		{
			ProxyAuthenticator proxyAuth = new ProxyAuthenticator();
			proxyAuth.AddProxyInfoToDb(proxyInfo);
		}

		public static ProxyAuthentication RetrieveProxyInfo()
		{
			ProxyAuthenticator proxyAuth = new ProxyAuthenticator();
			proxyAuth = proxyAuth.ReturnProxyAuthenticationInfo();
			if (proxyAuth != null)
				return proxyAuth.ProxyAuthObj;
			
			return null;
		}
    }
}

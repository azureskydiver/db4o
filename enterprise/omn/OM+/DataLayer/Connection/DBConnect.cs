using System;
using OManager.BusinessLayer.Login;
using Db4objects.Db4o;
using OME.Logging.Common;


namespace OManager.DataLayer.Connection
{
    class DBConnect
    {
        IObjectContainer dbConn;
        public string dbConnection(ConnParams login, bool customConfig)
        {
            try
            {
                Db4oClient.conn = login;
                Db4oClient.CustomConfig = customConfig;
                dbConn = Db4oClient.Client;
                return Db4oClient.exceptionConnection;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return "";
            }
        }
    }
}

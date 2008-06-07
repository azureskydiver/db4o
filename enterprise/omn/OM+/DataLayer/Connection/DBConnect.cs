using System;
using System.Collections.Generic;
using System.Text;
using OManager.BusinessLayer.Login;
using Db4objects.Db4o;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.Connection
{
    class DBConnect
    {
        IObjectContainer dbConn;

        //
        public string dbConnection(ConnParams login)
        {
            try
            {
                Db4oFactory.Configure().AllowVersionUpdates(true);
                Db4oClient.conn = login;

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

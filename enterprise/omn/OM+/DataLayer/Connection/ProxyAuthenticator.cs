using System;
using System.Collections.Generic;
using System.Text;
using OManager.BusinessLayer.Login;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using OME.Logging.Common;
using System.IO;

namespace  OManager.DataLayer.Connection
{
    public class ProxyAuthenticator
    {

        [Transient]
        IObjectContainer container = null;
        ProxyAuthentication proxyAuthObj;

        public ProxyAuthentication ProxyAuthObj
        {
            get { return proxyAuthObj; }
            set { proxyAuthObj = value; }
        }

        public void AddProxyInfoToDb(ProxyAuthentication proxyAuthObj)
        {
            if (Db4oClient.RecentConnFile == null)
            {
                string RecentConnFileName = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects" + Path.DirectorySeparatorChar + "ObjectManagerEnterprise" + Path.DirectorySeparatorChar + "ObjectManagerPlus.yap";
                Db4oClient.RecentConnFile = RecentConnFileName;
            }
            this.proxyAuthObj = proxyAuthObj;
            container = Db4oClient.RecentConn;
            ProxyAuthenticator proxyobj = ReturnProxyAuthenticationInfo();
            if (proxyobj == null)
            {
                container.Set(this);
            }
            else
            {
                proxyobj.proxyAuthObj = proxyAuthObj;
               
                container.Set(proxyobj);
            }
            container.Commit();
            container.Ext().Refresh(proxyobj, 1);
            //container.Close();

        }

        public ProxyAuthenticator ReturnProxyAuthenticationInfo()
        {
            try
            {
                if (Db4oClient.RecentConnFile == null)
                {
                    string RecentConnFileName = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects" + Path.DirectorySeparatorChar + "ObjectManagerEnterprise" + Path.DirectorySeparatorChar + "ObjectManagerPlus.yap";
                    Db4oClient.RecentConnFile = RecentConnFileName;
                }
                container = Db4oClient.RecentConn;
                IObjectSet ObjSet = container.Get(typeof(ProxyAuthenticator));
                if (ObjSet.Count >0)
                {
                    ProxyAuthenticator proxyobj = (ProxyAuthenticator)ObjSet.Next();
                    return proxyobj;
                }
                else
                    return null;
         
            }
            catch (Exception e)
            {
                LoggingHelper.HandleException(e);
                return null;
            }
        }
    }
}

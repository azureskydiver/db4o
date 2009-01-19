using System;
using OManager.BusinessLayer.Config;
using OManager.BusinessLayer.Login;
using Db4objects.Db4o;
using OME.Logging.Common;

namespace  OManager.DataLayer.Connection
{
    public class ProxyAuthenticator
    {
        [Transient]
        IObjectContainer _container;
        ProxyAuthentication _proxyAuthObj;

        public ProxyAuthentication ProxyAuthObj
        {
            get { return _proxyAuthObj; }
            set { _proxyAuthObj = value; }
        }

        public void AddProxyInfoToDb(ProxyAuthentication proxyAuthObj)
        {
            if (Db4oClient.RecentConnFile == null)
            {
            	Db4oClient.RecentConnFile = Config.OMNConfigDatabasePath();
            }
            _proxyAuthObj = proxyAuthObj;
            _container = Db4oClient.RecentConn;
            ProxyAuthenticator proxyobj = ReturnProxyAuthenticationInfo();
            if (proxyobj == null)
            {
                _container.Store(this);
            }
            else
            {
                proxyobj._proxyAuthObj = proxyAuthObj;
                _container.Store(proxyobj);
            }
            _container.Commit();
            _container.Ext().Refresh(proxyobj, 1);
        }

        public ProxyAuthenticator ReturnProxyAuthenticationInfo()
        {
            try
            {
                if (Db4oClient.RecentConnFile == null)
                {
                	Db4oClient.RecentConnFile = Config.OMNConfigDatabasePath();
                }
                _container = Db4oClient.RecentConn;
                IObjectSet ObjSet = _container.QueryByExample(typeof(ProxyAuthenticator));
            	
				return ObjSet.Count > 0 ? (ProxyAuthenticator) ObjSet.Next() : null;
            }
            catch (Exception e)
            {
                LoggingHelper.HandleException(e);
                return null;
            }
        }
    }
}

using System;
using System.Text;
using com.db4o;

namespace com.db4o.test.cs
{
    /// <summary>
    /// A facade to an ObjectContainer executing in a different AppDomain.
    /// </summary>
    public class MarshalByRefDatabase : MarshalByRefObject, IDisposable
    {
        protected ObjectServer _server;
        protected ObjectContainer _container;

        public void Open(string fname, bool clientServer)
        {
            if (clientServer)
            {
                _server = Db4o.openServer(fname, 0);
                _container = _server.openClient();
            }
            else
            {
                _container = Db4o.openFile(fname);
            }
        }

        public void Dispose()
        {
            if (null != _container)
            {
                _container.close();
                _container = null;
            }
            if (null != _server)
            {
                _server.close();
                _server = null;
            }
            // MAGIC: give some time for the db4o background threads to exit
            System.Threading.Thread.Sleep(1000);
        }
    }
}

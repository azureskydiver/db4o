using System;
using System.Configuration;
using System.Web;
using Db4objects.Db4o;

namespace Db4oDoc.WebApp.Infrastructure
{
    public class Db4oProvider : IHttpModule
    {
        private HttpApplication context;
        private const string DataBaseInstance = "db4o-database-instance";
        private const string SessionKey = "db4o-session";

        // #example: open database when the application starts
        public void Init(HttpApplication context)
        {
            this.context = context;
            context.Application[DataBaseInstance] = OpenDatabase();
            RegisterSessionCreation(context);
        }

        private IEmbeddedObjectContainer OpenDatabase()
        {
            string relativePath = ConfigurationSettings.AppSettings["DatabaseFileName"];
            string filePath = HttpContext.Current.Server.MapPath(relativePath);
            return Db4oEmbedded.OpenFile(filePath);
        }
        // #end example

        // #example: close the database when the application shuts down
        public void Dispose()
        {
            IDisposable toDispose = context.Application[DataBaseInstance] as IDisposable;
            if (null != toDispose)
            {
                toDispose.Dispose();
            }
        }
        // #end example

        // #example: provide access to the database
        public static IObjectContainer Database
        {
            get { return (IObjectContainer)HttpContext.Current.Items[SessionKey]; }
        }
        // #end example

        // #example: A object container per request
        private void RegisterSessionCreation(HttpApplication httpApplication)
        {
            httpApplication.BeginRequest += OpenSession;
            httpApplication.EndRequest += CloseSession;
        }

        private void OpenSession(object sender, EventArgs e)
        {
            IEmbeddedObjectContainer container =
                (IEmbeddedObjectContainer)context.Application[DataBaseInstance];
            IObjectContainer session = container.OpenSession();
            HttpContext.Current.Items[SessionKey] = session;
        }

        private void CloseSession(object sender, EventArgs e)
        {
            IObjectContainer session = (IObjectContainer)HttpContext.Current.Items[SessionKey];
            session.Dispose();
        }
        // #end example
    }
}
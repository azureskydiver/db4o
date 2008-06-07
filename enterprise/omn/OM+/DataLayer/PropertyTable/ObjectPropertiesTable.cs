using System;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o;
using OManager.DataLayer.Modal;
using OManager.DataLayer.Connection;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.PropertyTable
{
    public class ObjectPropertiesTable
    {
        string m_UUID;
        long m_LocalID;
        int m_Depth;
        long m_version;
        object m_detailsforObject;

        public object DetailsforObject
        {
            get { return m_detailsforObject; }
            set { m_detailsforObject = value; }
        }

        public ObjectPropertiesTable(object obj)
        {
            this.m_detailsforObject = obj;  
        }
        public long Version
        {
            get { return m_version; }
            set { m_version = value; }
        }

        public string UUID
        {
            get { return m_UUID; }
            set { m_UUID = value; }
        }
        public long LocalID
        {
            get { return m_LocalID; }
            set { m_LocalID = value; }
        }
        public int Depth
        {
            get { return m_Depth; }
            set { m_Depth = value; }
        }

        public ObjectPropertiesTable GetObjectProperties()
        {
            try
            {
                IObjectContainer objContainer = Db4oClient.Client;

                ObjectDetails objDetails = new ObjectDetails(m_detailsforObject);
                this.UUID = objDetails.GetUUID();

                this.LocalID = objDetails.GetLocalID();
                this.Version = objDetails.GetVersion();
                this.Depth = objDetails.GetDepth(m_detailsforObject); 
                return this;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }
    }
}

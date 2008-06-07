using System;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Ext;
using OManager.DataLayer.Modal;

namespace OManager.DataLayer.WatchDetails
{
    class WatchItems
    {
        
        string  m_fieldName;
        string   m_UUID;
        long m_LocalID;
        int m_Depth;
        string m_type;
        string m_modifier;
        object m_value;
        bool m_indexed;

        public WatchItems()
        {
            //IObjectContainer oc = null;
            //IReflectClass rclass = oc.Ext().Reflector().ForObject(obj);
           
        }

        public WatchItems GetfieldDetails(string classname)
        {
            //IReflectClass reff=null;
            //FieldDetails obj = new FieldDetails();
            ////this.UUID = obj.GetUUID(); 
            //this.Modifier = obj.GetModifier(obj);
            //this.LocalID = obj.GetLocalID(obj);
            //keep on writtin for all the above

            return null;
        }
        public WatchItems GetObjectDetails(long objectId)
        {
            //IReflectClass reff=null;
            //ObjectDetails obj = new ObjectDetails();
            ////this.UUID = obj.GetUUID(); 
            //this.Modifier = obj.GetModifier(obj);
            //this.LocalID = obj.GetLocalID(obj);
            //keep on writtin for all the above

            return null;
        }


        public string FieldName
        {
            get { return m_fieldName; }
            set { m_fieldName = value; }
        }

        public bool Indexed
        {
            get { return m_indexed; }
            set { m_indexed = value; }
        }

        public object Value
        {
            get { return this.m_value; }
            set { this.m_value = value; }
        }

        public string Modifier
        {
            get { return m_modifier; }
            set { m_modifier = value; }
        }

        public string Type
        {
            get { return m_type; }
            set { m_type = value; }
        }


        public int Depth
        {
            get { return m_Depth; }
            set { m_Depth = value; }
        }

        public string  UUID
        {
            get
            {
                return this.m_UUID;
            }            
            set
            {
                this.m_UUID = value;
            }
        }
        public long LocalID
        {
            get { return m_LocalID; }
            set { m_LocalID = value; }
        }




    }
}


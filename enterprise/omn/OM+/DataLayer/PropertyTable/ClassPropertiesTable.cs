using System;
using System.Collections;
using Db4objects.Db4o;
using OManager.DataLayer.Connection;
using OManager.DataLayer.Modal;
using OME.Logging.Common;

namespace OManager.DataLayer.PropertyTable
{

    public class ClassPropertiesTable
    {
        string m_className;
        ArrayList m_fieldEntries;
        int m_noOfObjects;

        public ClassPropertiesTable(string classname)
        {
            m_className = classname;  
        }
        public string ClassName
        {
            get { return m_className; }
            set { m_className = value; }
        }

        public int NoOfObjects
        {
            get { return m_noOfObjects; }
            set { m_noOfObjects = value; }
        }
       

        public ArrayList FieldEntries
        {
            get { return m_fieldEntries; }
            set { m_fieldEntries = value; }
        }

        public ClassPropertiesTable GetClassProperties()
        {
            try
            {
                ClassDetails clsDetails = new ClassDetails(m_className);

				m_fieldEntries = FieldProperties.FieldsFrom(m_className);
                m_noOfObjects = clsDetails.GetNumberOfObjects();
                return this;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
            
            
        }
		public void SetIndex(ArrayList fieldnames, string className, ArrayList Indexed)
        {
			for (int i = 0; i < fieldnames.Count; i++)
			{
				Db4oClient.SetIndex(fieldnames[i].ToString(), className, Convert.ToBoolean(Indexed[i]));
				
			}
        }

    }
}

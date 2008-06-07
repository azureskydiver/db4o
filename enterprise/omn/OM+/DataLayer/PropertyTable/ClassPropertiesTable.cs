using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using OManager.DataLayer.Modal;
using OME.Logging.Common;
using OME.Logging.Tracing;
namespace OManager.DataLayer.PropertyTable
{

    public class ClassPropertiesTable
    {
        string m_className;
        ArrayList m_fieldEntries;
        int m_noOfObjects;

        public ClassPropertiesTable(string classname)
        {
            this.m_className = classname;  
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
                FieldProperties fieldProp = new FieldProperties(m_className);
                ClassDetails clsDetails = new ClassDetails(m_className);

                this.m_fieldEntries = fieldProp.GetAllFieldsForTheClass();
                this.m_noOfObjects = clsDetails.GetNumberOfObjects();
                return this;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
            
            
        }
        public void SetIndex(string fieldname, string className, bool isIndexed)
        {
            
            Db4objects.Db4o.Db4oFactory.Configure().ObjectClass(className).ObjectField(fieldname).Indexed(isIndexed);
        }

    }
}

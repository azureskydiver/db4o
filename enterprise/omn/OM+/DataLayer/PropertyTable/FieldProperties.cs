using System;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o.Reflect;
using OManager.DataLayer.Modal;
using System.Collections;
using Db4objects.Db4o.Reflect.Generic;

using OME.Logging.Common;
using OME.Logging.Tracing;
namespace OManager.DataLayer.PropertyTable
{
    public class FieldProperties
    {
        string m_fieldName;
        string m_dataType;
        bool m_isIndexed;
        bool m_isPublic;
        string m_classname;
        public FieldProperties(string classname)
        {
            this.m_classname = classname;  
        }
        public bool Indexed
        {
            get { return m_isIndexed; }
            set { m_isIndexed = value; }
        }

        public bool Public
        {
            get { return m_isPublic; }
            set { m_isPublic = value; }
        }

        public string Field
        {
            get { return m_fieldName; }
            set { m_fieldName = value; }
        }

        public string DataType
        {
            get { return m_dataType; }
            set { m_dataType = value; }
        }

        public ArrayList GetAllFieldsForTheClass()
        {
            try
            {
                
                ArrayList listFieldProperties = new ArrayList();
                ClassDetails clDetails = new ClassDetails(m_classname);
                IReflectField[] reflectFields = clDetails.GetFieldList();
                foreach (IReflectField field in reflectFields)
                {
                    if (!(field is GenericVirtualField))
                    {
                        FieldProperties fp = new FieldProperties(m_classname);

                        string fieldDataType = field.GetFieldType().GetName();
                        if (fieldDataType.Contains(","))
                        {
                            int index = fieldDataType.IndexOf(',');
                            fp.DataType = fieldDataType.Substring(0, index);
                        }
                        fp.Field = field.GetName();
                        FieldDetails fd = new FieldDetails(m_classname, fp.Field);
                        fp.m_isPublic = fd.GetModifier();//field.IsPublic();
                        fp.m_isIndexed = fd.IsIndexed();
                        listFieldProperties.Add(fp);
                    }
                }
                return listFieldProperties;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }
    }

}

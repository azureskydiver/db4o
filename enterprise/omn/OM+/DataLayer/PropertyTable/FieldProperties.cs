/* Copyright (C) 2004 - 2009  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.Reflect;
using OManager.DataLayer.Connection;
using OManager.DataLayer.Modal;
using System.Collections;
using Db4objects.Db4o.Reflect.Generic;
using OManager.DataLayer.Reflection;
using OME.Logging.Common;

namespace OManager.DataLayer.PropertyTable
{
    public class FieldProperties
    {
		private string m_fieldName;
		private bool m_isIndexed;
		private bool m_isPublic;
		private readonly string m_dataType;

		public FieldProperties(string fieldName, string fieldType)
		{
			m_fieldName = fieldName;
			
			IType type = Db4oClient.TypeResolver.Resolve(fieldType);
			m_dataType = type.DisplayName;
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
        }

		public static ArrayList FieldsFrom(string className)
        {
            try
            {
                ArrayList listFieldProperties = new ArrayList();
                ClassDetails clDetails = new ClassDetails(className);
            	foreach (IReflectField field in clDetails.GetFieldList())
                {
                    if (!(field is GenericVirtualField))
                    {
                    	FieldProperties fp = FieldPropertiesFor(className, field);
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

    	private static FieldProperties FieldPropertiesFor(string className, IReflectField field)
    	{
    		FieldProperties fp = new FieldProperties(field.GetName(), field.GetFieldType().GetName());
    		FieldDetails fd = new FieldDetails(className, fp.Field);
    		fp.m_isPublic = fd.GetModifier();
    		fp.m_isIndexed = fd.IsIndexed();
    		
			return fp;
    	}
    }

}

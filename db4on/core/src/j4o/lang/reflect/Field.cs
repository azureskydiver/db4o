/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using System.Collections;

namespace j4o.lang.reflect
{
    public class Field
    {
        private FieldInfo _fieldInfo;
        private EventInfo _eventInfo;
        private Class _fieldClass;
        private int _modifiers;

        private static IList _transientMarkers;

        public Field(FieldInfo fieldInfo, EventInfo eventInfo)
        {
            _fieldInfo = fieldInfo;
            _eventInfo = eventInfo;
        	_modifiers = -1;
        }
        
        public Object get(Object obj)
        {
            return _fieldInfo.GetValue(obj);
        }

        public int getModifiers()
        {
            if (_modifiers == -1)
            {
            	_modifiers = composeModifiers();
            }
            return _modifiers;
        }

        private int composeModifiers()
        {
            int modifiers = 0;
            if (_fieldInfo.IsFamilyAndAssembly)
            {
                modifiers |= Modifier.PROTECTED;
            }
            if (_fieldInfo.IsInitOnly)
            {
                modifiers |= Modifier.FINAL;
            }
            if (_fieldInfo.IsLiteral)
            {
                modifiers |= Modifier.FINAL;
                modifiers |= Modifier.STATIC;
            }
            if (_fieldInfo.IsPrivate)
            {
                modifiers |= Modifier.PRIVATE;
            }
            if (_fieldInfo.IsPublic)
            {
                modifiers |= Modifier.PUBLIC;
            }
            if (_fieldInfo.IsStatic)
            {
                modifiers |= Modifier.STATIC;
            }

            if (isTransientType(_fieldInfo.FieldType))
            {
                modifiers |= Modifier.TRANSIENT;
            }
            else if (checkForTransient(_fieldInfo.GetCustomAttributes(true)))
            {
                modifiers |= Modifier.TRANSIENT;
            }
            else if (_eventInfo != null)
            {
                if (checkForTransient(_eventInfo.GetCustomAttributes(true)))
                {
                    modifiers |= Modifier.TRANSIENT;
                }
            }
            return modifiers;
        }

		private bool checkForTransient(object[] attributes)
		{
			if (attributes == null) return false;

			foreach (object attribute in attributes)
			{
				string attributeName = attribute.ToString();
				if ("com.db4o.Transient" == attributeName) return true;
				if (_transientMarkers == null) continue;
				if (_transientMarkers.Contains(attributeName)) return true;
			}
			return false;
		}

        private bool isTransientType(System.Type type)
        {
            return type.IsPointer
                || type.IsSubclassOf(typeof(Delegate));
        }

        public String getName()
        {
            return _fieldInfo.Name;
        }

        public Class getType()
        {
            if (_fieldClass == null)
            {
            	_fieldClass = Class.getClassForType(_fieldInfo.FieldType);
            }
            return _fieldClass;
        }

        public static void markTransient(string attributeName)
        {
            if (_transientMarkers == null)
            {
                _transientMarkers = new ArrayList();
            }
            else if (_transientMarkers.Contains(attributeName))
            {
                return;
            }
            _transientMarkers.Add(attributeName);
        }

        public void set(Object obj, Object value)
        {
        	_fieldInfo.SetValue(obj, value);
        }
    }
}

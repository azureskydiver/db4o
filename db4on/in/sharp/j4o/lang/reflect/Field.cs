/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using System.Collections;
using com.db4o;

namespace j4o.lang.reflect
{
    public class Field
    {
        private FieldInfo fieldInfo;
        private EventInfo eventInfo;
        private Class fieldClass;
        private int fieldModifiers;

        private static IList _transientMarkers;

        public Field(FieldInfo fieldInfo, EventInfo eventInfo)
        {
            this.fieldInfo = fieldInfo;
            this.eventInfo = eventInfo;
            fieldModifiers = -1;
        }

        private bool checkForTransient(object[] attributes)
        {
            if (attributes == null) return false;

            foreach (object attribute in attributes)
            {
                string attributeName = attribute.ToString();
                if ("com.db4o.Transient" == attributeName) return true;

                if (_transientMarkers == null) continue;
                
                foreach (string transientMarker in _transientMarkers)
                {
                    if (transientMarker == attributeName) return true;
                }
            }
            return false;
        }

        public Object get(Object obj)
        {
            return fieldInfo.GetValue(obj);
        }

        public int getModifiers()
        {
            if (fieldModifiers == -1)
            {
                fieldModifiers = composeModifiers();
            }
            return fieldModifiers;
        }

        private int composeModifiers()
        {
            int modifiers = 0;
            if (fieldInfo.IsFamilyAndAssembly)
            {
                modifiers |= Modifier.PROTECTED;
            }
            if (fieldInfo.IsInitOnly)
            {
                modifiers |= Modifier.FINAL;
            }
            if (fieldInfo.IsLiteral)
            {
                modifiers |= Modifier.FINAL;
                modifiers |= Modifier.STATIC;
            }
            if (fieldInfo.IsPrivate)
            {
                modifiers |= Modifier.PRIVATE;
            }
            if (fieldInfo.IsPublic)
            {
                modifiers |= Modifier.PUBLIC;
            }
            if (fieldInfo.IsStatic)
            {
                modifiers |= Modifier.STATIC;
            }

            if (isTransientType(fieldInfo.FieldType))
            {
                modifiers |= Modifier.TRANSIENT;
            }
            else if (checkForTransient(fieldInfo.GetCustomAttributes(true)))
            {
                modifiers |= Modifier.TRANSIENT;
            }
            else if (eventInfo != null)
            {
                if (checkForTransient(eventInfo.GetCustomAttributes(true)))
                {
                    modifiers |= Modifier.TRANSIENT;
                }
            }
            return modifiers;
        }

        private bool isTransientType(System.Type type)
        {
            return type.IsPointer
                || type.IsSubclassOf(typeof(Delegate));
        }

        public String getName()
        {
            return fieldInfo.Name;
        }

        public Class getType()
        {
            if (fieldClass == null)
            {
                fieldClass = Class.getClassForType(fieldInfo.FieldType);
            }
            return fieldClass;
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
            fieldInfo.SetValue(obj, value);
        }
    }
}

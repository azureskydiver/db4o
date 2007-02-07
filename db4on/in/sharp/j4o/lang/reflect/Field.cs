﻿/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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
        
        public Object Get(Object obj)
        {
            return _fieldInfo.GetValue(obj);
        }

        public int GetModifiers()
        {
            if (_modifiers == -1)
            {
            	_modifiers = ComposeModifiers();
            }
            return _modifiers;
        }

        private int ComposeModifiers()
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

            if (IsTransient(_fieldInfo))
            {
                modifiers |= Modifier.TRANSIENT;
            }
            else if (_eventInfo != null)
            {
                if (CheckForTransient(_eventInfo.GetCustomAttributes(true)))
                {
                    modifiers |= Modifier.TRANSIENT;
                }
            }
            return modifiers;
        }

        public String GetName()
        {
            return _fieldInfo.Name;
        }

        public Class GetFieldType()
        {
            if (_fieldClass == null)
            {
            	_fieldClass = Class.GetClassForType(_fieldInfo.FieldType);
            }
            return _fieldClass;
        }

        public void Set(Object obj, Object value)
        {
            _fieldInfo.SetValue(obj, value);
        }

        public static bool IsTransient(FieldInfo field)
        {
            if (field.IsNotSerialized) return true;
            if (field.IsDefined(typeof(com.db4o.TransientAttribute), true)) return true;
            if (_transientMarkers == null) return false;
            return CheckForTransient(field.GetCustomAttributes(true));
        }

        private static bool CheckForTransient(object[] attributes)
        {
            if (attributes == null) return false;

            foreach (object attribute in attributes)
            {
                string attributeName = attribute.GetType().FullName;
                if (_transientMarkers.Contains(attributeName)) return true;
            }
            return false;
        }

        public static void MarkTransient(System.Type attributeType)
        {
            MarkTransient(attributeType.FullName);
        }

        public static void MarkTransient(string attributeName)
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

        public static void ResetTransientMarkers()
        {
            _transientMarkers = null;
        }
    }
}

/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using System.Reflection;
using System.Collections;
using com.db4o;

namespace j4o.lang.reflect {

    public class Field {

        private FieldInfo fieldInfo;
        private EventInfo eventInfo;
        private Class fieldClass;
        private int fieldModifiers;

        private static IList markedTransient;

        public Field(FieldInfo fieldInfo, EventInfo eventInfo) {
            this.fieldInfo = fieldInfo;
            this.eventInfo = eventInfo;
            fieldModifiers = -1;
        }

        private int checkTransient(int modifiers, object[] attributes) {
            if(attributes != null) {
                for(int i = 0; i < attributes.Length; i++) {
                    if("com.db4o.Transient".Equals(attributes[i].ToString())) {
                        modifiers |= Modifier.TRANSIENT;
                        return modifiers;
                    }
                    if(markedTransient != null) {
                        IEnumerator e = markedTransient.GetEnumerator();
                        while(e.MoveNext()) {
                            if(e.Current.Equals(attributes[i].ToString())) {
                                modifiers |= Modifier.TRANSIENT;
                                return modifiers;
                            }
                        }
                    }
                }
            }
            return modifiers;
        }

        public Object get(Object obj) {
            return fieldInfo.GetValue(obj);
        }

        public int getModifiers() {
            if(fieldModifiers == -1) {
                int modifiers = 0;
                if(fieldInfo.IsFamilyAndAssembly) {
                    modifiers |= Modifier.PROTECTED;
                }
                if(fieldInfo.IsInitOnly) {
                    modifiers |= Modifier.FINAL;
                }
                if(fieldInfo.IsLiteral) {
                    modifiers |= Modifier.FINAL;
                    modifiers |= Modifier.STATIC;
                }
                if(fieldInfo.IsPrivate) {
                    modifiers |= Modifier.PRIVATE;
                }
                if(fieldInfo.IsPublic) {
                    modifiers |= Modifier.PUBLIC;
                }
                if(fieldInfo.IsStatic) {
                    modifiers |= Modifier.STATIC;
                }
                modifiers = checkTransient(modifiers, fieldInfo.GetCustomAttributes(true));
                if(eventInfo != null) {
                    modifiers = checkTransient(modifiers, eventInfo.GetCustomAttributes(true));
                }
                fieldModifiers = modifiers;
            }
            return fieldModifiers;
        }

        public String getName() {
            return fieldInfo.Name;
        }

        public Class getType() {
            if(fieldClass == null) {
                fieldClass = Class.getClassForType(fieldInfo.FieldType);
            }
            return fieldClass;
        }

        public static void markTransient(String attributeName) {
            if(markedTransient == null) {
                markedTransient = new ArrayList();
            }
            IEnumerator i = markedTransient.GetEnumerator();
            while(i.MoveNext()) {
                if(attributeName.Equals(i.Current)) {
                    return;
                }
            }
            markedTransient.Add(attributeName);
        }

        public void set(Object obj, Object value) {
            fieldInfo.SetValue(obj, value);
        }
    }
}

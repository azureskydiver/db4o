/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using System.Collections;
using j4o.lang.reflect;

namespace j4o.lang {

    public class Class {

        public static Hashtable assemblies = new Hashtable();
        private static Hashtable typeToClassMap = new Hashtable();

        private static Type[] PRIMITIVE_TYPES = {
            typeof(DateTime), typeof(Decimal)
        };

        private Type type;
        private String name;
        private bool primitive;

        public Class(Type type) {
            this.type = type;
            primitive = type.IsPrimitive;
            getName();
            for(int i = 0; i < PRIMITIVE_TYPES.Length; i++) {
                if(type == PRIMITIVE_TYPES[i]) {
                    primitive = true;
                }
            }
        }

        internal static BindingFlags declared() {
            return BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public;
        }

        public override bool Equals(object obj) {
            Class clazz = obj as Class;
            return clazz != null && clazz.type == type;
            return false;
        }

        public static Class forName(String name) {
            try {
                Type t = Type.GetType(name);
                if(t == null) {
                    int pos = name.IndexOf(",");
                    if(pos > 0) {
                        AssemblyNameHint anh = (AssemblyNameHint)assemblies[name.Substring(pos + 2)];
                        if(anh != null) {
                            t = Type.GetType(name.Substring(0, pos) + ", " + anh.longName);
                        }
                    }
                }
                return getClassForType(t);
            } catch(TypeLoadException ex) {
                throw new ClassNotFoundException(name);
            }
        }

        public static Class getClassForObject(object obj) {
            return getClassForType(obj.GetType());
        }

        public static Class getClassForType(Type forType) {
            if(forType == null) {
                return null;
            }
            Class clazz = (Class)typeToClassMap[forType];
            if(clazz == null) {
                clazz = new Class(forType);
                typeToClassMap[forType] = clazz;
            }
            return clazz;

        }

        public Class getComponentType() {
            return getClassForType(type.GetElementType());
        }

        public Constructor[] getDeclaredConstructors() {
            ConstructorInfo[] constructorInfos = type.GetConstructors(declared());
            Constructor[] constructors = new Constructor[constructorInfos.Length];
            for(int i = 0; i < constructorInfos.Length; i++) {
                constructors[i] = new Constructor(constructorInfos[i]);
            }
            return constructors;
        }

        public Field getDeclaredField(String name) {
            return getField(type.GetField(name, declared() | BindingFlags.Static));
        }

        public Field[] getDeclaredFields() {
            FieldInfo[] fieldInfos = type.GetFields(declared() | BindingFlags.Static);
            Field[] fields = new Field[fieldInfos.Length];
            for(int i = 0; i < fieldInfos.Length; i++) {
                fields[i] = getField(fieldInfos[i]);
            }
            return fields;
        }

        public Method getDeclaredMethod(String name, Class[] parameterTypes) {
            return getMethod(type.GetMethod(name, declared(), null, getTypes(parameterTypes), null));
        }

        public Method[] getDeclaredMethods() {
            MethodInfo[] methodInfos = type.GetMethods(declared());
            Method[] methods = new Method[methodInfos.Length];
            for(int i = 0; i < methodInfos.Length; i++) {
                methods[i] = new Method(methodInfos[i]);
            }
            return methods;
        }

        private Field getField(FieldInfo fieldInfo) {
            if(fieldInfo == null) {
                return null;
            }
            return new Field(fieldInfo, type.GetEvent(fieldInfo.Name, declared()));
        }

        public Field getField(String name) {
            return getField(type.GetField(name));
        }

        public Method getMethod(String name, Class[] parameterTypes) {
            return getMethod(type.GetMethod(name, getTypes(parameterTypes)));
        }

        private Method getMethod(MethodInfo methodInfo) {
            if(methodInfo == null) {
                return null;
            }
            return new Method(methodInfo);
        }

        public int getModifiers() {
            int modifiers = 0;
            if(type.IsAbstract) {
                modifiers |= Modifier.ABSTRACT;
            }
            if(type.IsPublic || type.IsNestedPublic) {
                modifiers |= Modifier.PUBLIC;
            }
            if(type.IsNestedPrivate) {
                modifiers |= Modifier.PRIVATE;
            }
            if(type.IsInterface) {
                modifiers |= Modifier.INTERFACE;
            }
            return modifiers;
        }

        public String getName() {
            if(name == null) {
                String fullAssemblyName = type.Assembly.GetName().ToString();
                String shortAssemblyName = fullAssemblyName;
                int pos = fullAssemblyName.IndexOf(",");
                if(pos > 0) {
                    shortAssemblyName = fullAssemblyName.Substring(0, pos);
                }
                name = type.FullName + ", " + shortAssemblyName;
                Type testType = Type.GetType(name);
                if(testType == null) {
                    testType = Type.GetType(type.FullName + ", " + fullAssemblyName);
                    if(testType != null) {
                        AssemblyNameHint anh = (AssemblyNameHint)assemblies[shortAssemblyName];
                        if(anh != null) {
                            anh.longName = fullAssemblyName;
                        } else {
                            anh = new AssemblyNameHint(shortAssemblyName, fullAssemblyName);
                            assemblies[shortAssemblyName] = anh;
                        }
                    }
                }
            }
            return name;
        }

        public Type getNetType() {
            return type;
        }

        public Class getSuperclass() {
            return getClassForType(type.BaseType);
        }

        public static Type[] getTypes(Class[] classes) {
            if(classes == null) {
                return new Type[] {};
            }
            Type[] types = new Type[classes.Length];
            for(int i = 0; i < types.Length; i++) {
                types[i] = classes[i].getNetType();
            }
            return types;
        }

        public bool isArray() {
            return type.IsArray;
        }

        public bool isAssignableFrom(Class clazz) {
            return type.IsAssignableFrom(clazz.type);
        }

        public bool isInstance(object obj) {
            if(obj == null) {
                return false;
            }
            if(type.IsInterface) {
                return type.IsAssignableFrom(obj.GetType());
            }
            return obj.GetType() == type;
        }

        public bool isInterface() {
            return type.IsInterface;
        }

        public bool isPrimitive() {
            return primitive;
        }

        public Object newInstance() {
            return Activator.CreateInstance(type);
        }

        public static bool operator !=(Class class1, Class class2) {
            if((object)class1 == null) {
                return (object)class2 != null;
            }
            if((object)class2 == null) {
                return true;
            }
            return class1.type != class2.type;
        }

        public static bool operator ==(Class clazz1, Class clazz2) {
            if((object)clazz1 == null) {
                return (object)clazz2 == null;
            }
            if((object)clazz2 == null) {
                return false;
            }
            return clazz1.type == clazz2.type;
        }
    }
}

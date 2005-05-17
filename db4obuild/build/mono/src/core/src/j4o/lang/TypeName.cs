/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
using System;
using System.Reflection;
using System.Text;
using System.Text.RegularExpressions;

namespace j4o.lang
{
    public class TypeName
    {
        string _simpleName;
        string _arrayQualifier;
        AssemblyName _assembly;
        TypeName[] _genericArguments;

        private TypeName(string simpleName, string arrayQualifier, AssemblyName assemblyName, TypeName[] genericArguments)
        {
            _simpleName = simpleName;
            _arrayQualifier = arrayQualifier;
            _assembly = assemblyName;
            _genericArguments = genericArguments;
        }

        public string SimpleName
        {
            get
            {
                return _simpleName;
            }
        }

        public AssemblyName AssemblyName
        {
            get
            {
                return _assembly;
            }
        }

        public bool HasGenericArguments
        {
            get
            {
                return _genericArguments.Length > 0;
            }
        }

        public TypeName[] GenericArguments
        {
            get
            {
                return _genericArguments;
            }
        }

        public string GetUnversionedName()
        {
            StringBuilder builder = new StringBuilder();
            AppendUnversionedName(builder);
            return builder.ToString();
        }

        void AppendUnversionedName(StringBuilder builder)
        {
            builder.Append(_simpleName);
            if (HasGenericArguments)
            {
                builder.Append("[");
                for (int i=0; i<_genericArguments.Length; ++i)
                {
                    if (i > 0)
                    {
                        builder.Append(",");
                    }
                    builder.Append("[");
                    _genericArguments[i].AppendUnversionedName(builder);
                    builder.Append("]");
                }
                builder.Append("]");
            }
            builder.Append(_arrayQualifier);
            if (null != _assembly)
            {
                builder.Append(", ");
                builder.Append(_assembly.Name);
            }
        }

        public System.Type Resolve()
        {
            return _assembly == null
                ? Type.GetType(ExpandName(), true)
                : ResolveAssembly().GetType(ExpandName());
        }

        private System.Reflection.Assembly ResolveAssembly()
        {
            Assembly found = null;
            try
            {
                found = Assembly.Load(_assembly);
            }
            catch (Exception)
            {
                AssemblyName unversioned = (AssemblyName)_assembly.Clone();
                unversioned.Version = null;
#if COMPACT_1_0
                found = Assembly.Load(unversioned);
#else
                try
                {
                    found = Assembly.Load(unversioned);
                }
                catch (Exception)
                {
                    found = Assembly.LoadWithPartialName(unversioned.FullName);
                    if (null == found)
                    {
                        throw;
                    }
                }
#endif
            }
            return found;

        }

        string ExpandName()
        {
            if (!HasGenericArguments)
            {
                return _simpleName + _arrayQualifier;
            }

            StringBuilder builder = new StringBuilder(_simpleName);
            builder.Append("[");
            for (int i = 0; i < _genericArguments.Length; ++i)
            {
                if (i > 0)
                {
                    builder.Append(",");
                }
                builder.Append("[");

				System.Type type = _genericArguments[i].Resolve();
#if COMPACT_1_0
				builder.Append(type.FullName);
				builder.Append(", ");
				builder.Append(type.Assembly.FullName);
#else
                builder.Append(type.AssemblyQualifiedName);
#endif
                builder.Append("]");
            }
            builder.Append("]");
            builder.Append(_arrayQualifier);
            return builder.ToString();
        }

        public override bool Equals(object obj)
        {
            if (null == obj)
            {
                return false;
            }

            TypeName other = obj as TypeName;
            if (null == other)
            {
                return false;
            }

            if (_simpleName != other._simpleName ||
                _assembly.FullName != other._assembly.FullName ||
                _genericArguments.Length != other._genericArguments.Length)
            {
                return false;
            }

            for (int i = 0; i < _genericArguments.Length; ++i)
            {
                if (!_genericArguments[i].Equals(other._genericArguments[i]))
                {
                    return false;
                }
            }
            return true;
        }

        static readonly TypeName[] NoGenericArguments = new TypeName[0];

        static readonly Regex TopLevelTypeNameRegex = new Regex(@"^(?<SimpleName>(\w|\d|\.|\+)+)((?<GenericSuffix>`(?<GenericArgCount>\d+))\[(?<GenericArgs>.+)\])?(?<ArrayQualifier>(\[,*\])+)?(,\s+(?<AssemblyName>.+))?$");

        static readonly Regex ArrayQualifierRegex = new Regex(@"(?<ArrayQualifier>(\[,*\])+)$");

        static readonly Regex AssemblyNameSeparatorRegex = new Regex(@",\s*");

        static readonly Regex PairSeparatorRegex = new Regex(@"\s*=\s*");

        public static TypeName Parse(string name)
        {
            if (null == name)
            {
                throw new ArgumentNullException("name");
            }
            return new TypeNameParser(name).Parse();
        }

		public static TypeName FromType(System.Type type)
		{
			if (null == type)
			{
				throw new ArgumentNullException("type");
			}
			StringBuilder builder = new StringBuilder(type.FullName);
			builder.Append(", ");
			builder.Append(type.Assembly.FullName);
			return Parse(builder.ToString());
		}

        class TypeNameParser
        {
            string _name;

            public TypeNameParser(string name)
            {
                _name = name;
            }

            public TypeName Parse()
            {
                Match m = TopLevelTypeNameRegex.Match(_name);
                if (!m.Success)
                {
                    InvalidTypeName();
                }

                AssemblyName assemblyName = null;
                TypeName[] genericArguments = NoGenericArguments;
                string simpleName = m.Groups["SimpleName"].Value;
                string arrayQualifier = m.Groups["ArrayQualifier"].Value;
                if (m.Groups["GenericArgCount"].Success)
                {
                    simpleName += m.Groups["GenericSuffix"];
                    int argCount = int.Parse(m.Groups["GenericArgCount"].Value);
                    string args = m.Groups["GenericArgs"].Value;
                    if (!args.EndsWith("]"))
                    {
                        // fix overeager matching of [] at the end
                        Match aqm = ArrayQualifierRegex.Match(args + "]");
                        if (!aqm.Success)
                        {
                            InvalidTypeName();
                        }

                        arrayQualifier = aqm.Groups["ArrayQualifier"].Value;
                        args = args.Substring(0, aqm.Index - 1);
                    }
                    genericArguments = ParseGenericArguments(argCount, args);
                }

                if (m.Groups["AssemblyName"].Success)
                {
                    assemblyName = ParseAssemblyName(m.Groups["AssemblyName"].Value);
                }
                return new TypeName(simpleName, arrayQualifier, assemblyName, genericArguments);
            }

            private AssemblyName ParseAssemblyName(string s)
            {
                AssemblyName name = new AssemblyName();
                string[] parts = AssemblyNameSeparatorRegex.Split(s);
                name.Name = parts[0];
                for (int i=1; i<parts.Length; ++i)
                {
                    string part = parts[i];
                    string[] pair = PairSeparatorRegex.Split(part);
                    if (2 != pair.Length)
                    {
                        InvalidTypeName();
                    }
                    switch (pair[0])
                    {
                        case "Culture":
                        {
                            if ("neutral" == pair[1])
                            {
                                name.CultureInfo = System.Globalization.CultureInfo.InvariantCulture;
                            }
                            else
                            {
                                name.CultureInfo = new System.Globalization.CultureInfo(pair[1]);
                            }
                            break;
                        }

                        case "Version":
                        {
                            name.Version = new Version(pair[1]);
                            break;
                        }

                        case "PublicKeyToken":
                        {
                            if ("null" != pair[1])
                            {
                                name.SetPublicKeyToken(ParsePublicKeyToken(pair[1]));
                            }
                            break;
                        }
                    }
                }
                return name;
            }

            byte[] ParsePublicKeyToken(string token)
            {
                int len = token.Length / 2;
                byte[] bytes = new byte[len];
                for (int i = 0; i < len; ++i)
                {
                    bytes[i] = byte.Parse(token.Substring(i*2, 2), System.Globalization.NumberStyles.HexNumber);
                }
                return bytes;
            }

            private TypeName[] ParseGenericArguments(int count, string args)
            {
                int start = 0;
                TypeName[] returnValue = new TypeName[count];
                for (int i = 0; i < returnValue.Length; ++i)
                {
                    string typeName = NextTypeName(args, ref start);
                    returnValue[i] = TypeName.Parse(typeName);
                }
                return returnValue;
            }

            private string NextTypeName(string s, ref int start)
            {
                start = s.IndexOf('[', start);
                if (start < 0)
                {
                    InvalidTypeName();
                }

                int bracketMatch = 0;
                for (int i = start; i < s.Length; ++i)
                {
                    char ch = s[i];
                    if ('[' == ch)
                    {
                        ++bracketMatch;
                    }
                    else if (']' == ch)
                    {
                        if (--bracketMatch == 0)
                        {
                            ++start; // skip starting and ending brackets
                            string typeName = s.Substring(start, i - start);
                            start = i + 1;
                            return typeName;
                        }
                    }
                }
                InvalidTypeName();
                return null;
            }

            private void InvalidTypeName()
            {
                throw new ArgumentException(string.Format("'{0}' is not a valid type name.", _name), "name");
            }
        }
    }
}





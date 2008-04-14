/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
using System.Text;

using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Marshall;
using Db4objects.Db4o.Typehandlers;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Reflect;

namespace Db4objects.Db4odoc.Typehandler
{

    public class StringBuilderHandler : ITypeHandler4, ISecondClassTypeHandler
        , IVariableLengthTypeHandler, IEmbeddedTypeHandler
    {
        private IReflectClass _classReflector;

        public StringBuilderHandler()
        {

        }

        public IReflectClass ClassReflector(IReflector reflector)
        {
            if (_classReflector == null)
            {
                _classReflector = reflector.ForClass(typeof(StringBuilder));
            }
            return _classReflector;
        }

        public void Delete(IDeleteContext context)
        {
            context.ReadSlot();
        }
        // end Delete


        private static int Compare(StringBuilder a_compare, StringBuilder a_with)
        {
            if (a_compare == null)
            {
                if (a_with == null)
                {
                    return 0;
                }
                return -1;
            }
            if (a_with == null)
            {
                return 1;
            }
            char[] c_compare = new char[a_compare.Length];
            a_compare.CopyTo(0, c_compare, 0, a_compare.Length);
            char[] c_with = new char[a_with.Length];
            a_with.CopyTo(0, c_with, 0, a_with.Length);

            return CompareChars(c_compare, c_with);
        }
        // end Compare

        private static int CompareChars(char[] compare, char[] with)
        {
            int min = compare.Length < with.Length ? compare.Length : with.Length;
            for (int i = 0; i < min; i++)
            {
                if (compare[i] != with[i])
                {
                    return compare[i] - with[i];
                }
            }
            return compare.Length - with.Length;
        }
        // end CompareChars


        public void Write(IWriteContext context, object obj)
        {
            string str = ((StringBuilder)obj).ToString();
            IWriteBuffer buffer = context;
            buffer.WriteInt(str.Length);
            WriteToBuffer(buffer, str);
        }
        // end Write

        private static void WriteToBuffer(IWriteBuffer buffer, string str)
        {
            int length = str.Length;
            char[] chars = new char[length];
            str.CopyTo(0, chars, 0, length);
            for (int i = 0; i < length; i++)
            {
                buffer.WriteByte((byte)(chars[i] & 0xff));
                buffer.WriteByte((byte)(chars[i] >> 8));
            }
        }
        // end WriteToBuffer


        private static string ReadBuffer(IReadBuffer buffer, int length)
        {
            char[] chars = new char[length];
            for (int ii = 0; ii < length; ii++)
            {
                chars[ii] = (char)((buffer.ReadByte() & 0xff) | ((buffer.ReadByte() & 0xff) << 8));
            }
            return new string(chars, 0, length);
        }
        // end ReadBuffer

        public object Read(IReadContext context)
        {
            IReadBuffer buffer = context;
            string str = "";
            int length = buffer.ReadInt();
            if (length > 0)
            {
                str = ReadBuffer(buffer, length);
            }
            return new StringBuilder(str);
        }
        // end Read

        public void Defragment(IDefragmentContext context)
        {
            // To stay compatible with the old marshaller family
            // In the marshaller family 0 number 8 represented
            // length reqiored to store ID and object length information
            context.IncrementOffset(8);
        }
        // end Defragment

        public IPreparedComparison PrepareComparison(IContext con, object obj)
        {
            return new PreparedComparison(obj);
        }
        // end PrepareComparison

        private class PreparedComparison : IPreparedComparison
        {
            object _source = null;

            public PreparedComparison(object source)
            {
                _source = source;
            }

            public int CompareTo(object target)
            {
                return Compare((StringBuilder)_source, (StringBuilder)target);
            }
        }
        // end PreparedComparison
    }

}

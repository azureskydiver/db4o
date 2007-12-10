/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System.Collections;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Internal.Query.Processor;
using Db4objects.Db4o.Marshall;
using Db4objects.Db4o.Reflect;

namespace Db4objects.Db4o.Internal.Handlers
{
	/// <summary>n-dimensional array</summary>
	/// <exclude></exclude>
	public class MultidimensionalArrayHandler : ArrayHandler
	{
		public MultidimensionalArrayHandler(ObjectContainerBase stream, ITypeHandler4 a_handler
			, bool a_isPrimitive) : base(stream, a_handler, a_isPrimitive)
		{
		}

		protected MultidimensionalArrayHandler(ITypeHandler4 template) : base(template)
		{
		}

		public sealed override IEnumerator AllElements(object array)
		{
			return AllElements(ArrayReflector(), array);
		}

		public static IEnumerator AllElements(IReflectArray reflectArray, object array)
		{
			int[] dim = reflectArray.Dimensions(array);
			object[] flat = new object[ElementCount(dim)];
			reflectArray.Flatten(array, dim, 0, flat, 0);
			return new ArrayIterator4(flat);
		}

		public sealed override int ElementCount(Transaction trans, IReadBuffer buffer)
		{
			return ElementCount(ReadDimensions(trans, buffer, ReflectClassByRef.IGNORED));
		}

		protected static int ElementCount(int[] a_dim)
		{
			int elements = a_dim[0];
			for (int i = 1; i < a_dim.Length; i++)
			{
				elements = elements * a_dim[i];
			}
			return elements;
		}

		public sealed override byte Identifier()
		{
			return Const4.YAPARRAYN;
		}

		public override int OwnLength(object obj)
		{
			int[] dim = ArrayReflector().Dimensions(obj);
			return Const4.OBJECT_LENGTH + (Const4.INT_LENGTH * (2 + dim.Length));
		}

		protected override int ReadElementsDefrag(BufferPair readers)
		{
			int numDimensions = base.ReadElementsDefrag(readers);
			int[] dimensions = new int[numDimensions];
			for (int i = 0; i < numDimensions; i++)
			{
				dimensions[i] = readers.ReadInt();
			}
			return ElementCount(dimensions);
		}

		public override void ReadSubCandidates(int handlerVersion, Db4objects.Db4o.Internal.Buffer
			 reader, QCandidates candidates)
		{
			IntArrayByRef dimensions = new IntArrayByRef();
			object arr = ReadCreate(candidates.i_trans, reader, dimensions);
			if (arr == null)
			{
				return;
			}
			ReadSubCandidates(handlerVersion, reader, candidates, ElementCount(dimensions.value
				));
		}

		protected virtual object ReadCreate(Transaction trans, IReadBuffer buffer, IntArrayByRef
			 dimensions)
		{
			ReflectClassByRef classByRef = new ReflectClassByRef();
			dimensions.value = ReadDimensions(trans, buffer, classByRef);
			IReflectClass clazz = NewInstanceReflectClass(classByRef);
			if (clazz == null)
			{
				return null;
			}
			return ArrayReflector().NewInstance(clazz, dimensions.value);
		}

		private int[] ReadDimensions(Transaction trans, IReadBuffer buffer, ReflectClassByRef
			 clazz)
		{
			int[] dim = new int[ReadElementsAndClass(trans, buffer, clazz)];
			for (int i = 0; i < dim.Length; i++)
			{
				dim[i] = buffer.ReadInt();
			}
			return dim;
		}

		public override object Read(IReadContext context)
		{
			IntArrayByRef dimensions = new IntArrayByRef();
			object array = ReadCreate(context.Transaction(), context, dimensions);
			if (array != null)
			{
				object[] objects = new object[ElementCount(dimensions.value)];
				for (int i = 0; i < objects.Length; i++)
				{
					objects[i] = context.ReadObject(_handler);
				}
				ArrayReflector().Shape(objects, 0, array, dimensions.value, 0);
			}
			return array;
		}

		public override void Write(IWriteContext context, object obj)
		{
			int classID = ClassID(obj);
			context.WriteInt(classID);
			int[] dim = ArrayReflector().Dimensions(obj);
			context.WriteInt(dim.Length);
			for (int i = 0; i < dim.Length; i++)
			{
				context.WriteInt(dim[i]);
			}
			IEnumerator objects = AllElements(obj);
			while (objects.MoveNext())
			{
				context.WriteObject(_handler, objects.Current);
			}
		}
	}
}

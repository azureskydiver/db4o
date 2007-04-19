using System;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Internal.Marshall;
using Db4objects.Db4o.Internal.Query.Processor;
using Db4objects.Db4o.Reflect;

namespace Db4objects.Db4o.Internal.Handlers
{
	/// <summary>n-dimensional array</summary>
	/// <exclude></exclude>
	public sealed class MultidimensionalArrayHandler : ArrayHandler
	{
		public MultidimensionalArrayHandler(ObjectContainerBase stream, ITypeHandler4 a_handler
			, bool a_isPrimitive) : base(stream, a_handler, a_isPrimitive)
		{
		}

		public sealed override object[] AllElements(object a_array)
		{
			int[] dim = _reflectArray.Dimensions(a_array);
			object[] flat = new object[ElementCount(dim)];
			_reflectArray.Flatten(a_array, dim, 0, flat, 0);
			return flat;
		}

		public int ElementCount(Transaction a_trans, Db4objects.Db4o.Internal.Buffer a_bytes
			)
		{
			return ElementCount(ReadDimensions(a_trans, a_bytes, new IReflectClass[1]));
		}

		private int ElementCount(int[] a_dim)
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

		public sealed override int ObjectLength(object a_object)
		{
			int[] dim = _reflectArray.Dimensions(a_object);
			return Const4.OBJECT_LENGTH + (Const4.INT_LENGTH * (2 + dim.Length)) + (ElementCount
				(dim) * i_handler.LinkLength());
		}

		public override int OwnLength(object obj)
		{
			int[] dim = _reflectArray.Dimensions(obj);
			return Const4.OBJECT_LENGTH + (Const4.INT_LENGTH * (2 + dim.Length));
		}

		public sealed override object Read1(MarshallerFamily mf, StatefulBuffer reader)
		{
			object[] ret = new object[1];
			int[] dim = Read1Create(reader.GetTransaction(), reader, ret);
			if (ret[0] != null)
			{
				object[] objects = new object[ElementCount(dim)];
				for (int i = 0; i < objects.Length; i++)
				{
					objects[i] = i_handler.Read(mf, reader, true);
				}
				_reflectArray.Shape(objects, 0, ret[0], dim, 0);
			}
			return ret[0];
		}

		protected override int ReadElementsDefrag(ReaderPair readers)
		{
			int numDimensions = base.ReadElementsDefrag(readers);
			int[] dimensions = new int[numDimensions];
			for (int i = 0; i < numDimensions; i++)
			{
				dimensions[i] = readers.ReadInt();
			}
			return ElementCount(dimensions);
		}

		public sealed override void Read1Candidates(MarshallerFamily mf, Db4objects.Db4o.Internal.Buffer
			 reader, QCandidates candidates)
		{
			object[] ret = new object[1];
			int[] dim = Read1Create(candidates.i_trans, reader, ret);
			if (ret[0] != null)
			{
				int count = ElementCount(dim);
				for (int i = 0; i < count; i++)
				{
					QCandidate qc = i_handler.ReadSubCandidate(mf, reader, candidates, true);
					if (qc != null)
					{
						candidates.AddByIdentity(qc);
					}
				}
			}
		}

		public sealed override object Read1Query(Transaction a_trans, MarshallerFamily mf
			, Db4objects.Db4o.Internal.Buffer a_bytes)
		{
			object[] ret = new object[1];
			int[] dim = Read1Create(a_trans, a_bytes, ret);
			if (ret[0] != null)
			{
				object[] objects = new object[ElementCount(dim)];
				for (int i = 0; i < objects.Length; i++)
				{
					objects[i] = i_handler.ReadQuery(a_trans, mf, true, a_bytes, true);
				}
				_reflectArray.Shape(objects, 0, ret[0], dim, 0);
			}
			return ret[0];
		}

		private int[] Read1Create(Transaction a_trans, Db4objects.Db4o.Internal.Buffer a_bytes
			, object[] obj)
		{
			IReflectClass[] clazz = new IReflectClass[1];
			int[] dim = ReadDimensions(a_trans, a_bytes, clazz);
			if (i_isPrimitive)
			{
				obj[0] = a_trans.Reflector().Array().NewInstance(i_handler.PrimitiveClassReflector
					(), dim);
			}
			else
			{
				if (clazz[0] != null)
				{
					obj[0] = a_trans.Reflector().Array().NewInstance(clazz[0], dim);
				}
			}
			return dim;
		}

		private int[] ReadDimensions(Transaction a_trans, Db4objects.Db4o.Internal.Buffer
			 a_bytes, IReflectClass[] clazz)
		{
			int[] dim = new int[ReadElementsAndClass(a_trans, a_bytes, clazz)];
			for (int i = 0; i < dim.Length; i++)
			{
				dim[i] = a_bytes.ReadInt();
			}
			return dim;
		}

		public sealed override void WriteNew1(object obj, StatefulBuffer writer)
		{
			int[] dim = _reflectArray.Dimensions(obj);
			WriteClass(obj, writer);
			writer.WriteInt(dim.Length);
			for (int i = 0; i < dim.Length; i++)
			{
				writer.WriteInt(dim[i]);
			}
			object[] objects = AllElements(obj);
			MarshallerFamily mf = MarshallerFamily.Current();
			for (int i = 0; i < objects.Length; i++)
			{
				i_handler.WriteNew(mf, Element(objects, i), false, writer, true, true);
			}
		}

		private object Element(object a_array, int a_position)
		{
			try
			{
				return _reflectArray.Get(a_array, a_position);
			}
			catch (Exception)
			{
				return null;
			}
		}
	}
}

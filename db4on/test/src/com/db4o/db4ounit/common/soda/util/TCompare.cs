namespace com.db4o.db4ounit.common.soda.util
{
	public class TCompare
	{
		public static bool IsEqual(object a_compare, object a_with)
		{
			return IsEqual(a_compare, a_with, null, new System.Collections.ArrayList());
		}

		private static bool IsEqual(object a_compare, object a_with, string a_path, System.Collections.ArrayList
			 a_list)
		{
			if (a_compare == null)
			{
				return a_with == null;
			}
			if (a_with == null)
			{
				return false;
			}
			System.Type clazz = a_compare.GetType();
			if (clazz != a_with.GetType())
			{
				return false;
			}
			if (com.db4o.@internal.Platform4.IsSimple(clazz))
			{
				return a_compare.Equals(a_with);
			}
			if (a_list.Contains(a_compare))
			{
				return true;
			}
			a_list.Add(a_compare);
			if (a_compare.GetType().IsArray)
			{
				return AreArraysEqual(NormalizeNArray(a_compare), NormalizeNArray(a_with), a_path
					, a_list);
			}
			if (HasPublicConstructor(a_compare.GetType()))
			{
				return AreFieldsEqual(a_compare, a_with, a_path, a_list);
			}
			return a_compare.Equals(a_with);
		}

		private static bool AreFieldsEqual(object a_compare, object a_with, string a_path
			, System.Collections.ArrayList a_list)
		{
			string path = GetPath(a_compare, a_with, a_path);
			System.Reflection.FieldInfo[] fields = j4o.lang.JavaSystem.GetDeclaredFields(a_compare
				.GetType());
			for (int i = 0; i < fields.Length; i++)
			{
				System.Reflection.FieldInfo field = fields[i];
                if (Db4oUnit.Extensions.Db4oUnitPlatform.IsStoreableField(field))
				{
					com.db4o.@internal.Platform4.SetAccessible(field);
					try
					{
						if (!IsFieldEqual(field, a_compare, a_with, path, a_list))
						{
							return false;
						}
					}
					catch (System.Exception e)
					{
						j4o.lang.JavaSystem.Err.WriteLine("TCompare failure executing path:" + path);
						j4o.lang.JavaSystem.PrintStackTrace(e);
						return false;
					}
				}
			}
			return true;
		}

		private static bool IsFieldEqual(System.Reflection.FieldInfo field, object a_compare
			, object a_with, string path, System.Collections.ArrayList a_list)
		{
			object compare = GetFieldValue(field, a_compare);
			object with = GetFieldValue(field, a_with);
			return IsEqual(compare, with, path + field.Name + ":", a_list);
		}

		private static object GetFieldValue(System.Reflection.FieldInfo field, object obj
			)
		{
			try
			{
				return field.GetValue(obj);
			}
			catch (System.MemberAccessException)
			{
				return null;
			}
		}

		private static bool AreArraysEqual(object compare, object with, string path, System.Collections.ArrayList
			 a_list)
		{
			int len = j4o.lang.JavaSystem.GetArrayLength(compare);
			if (len != j4o.lang.JavaSystem.GetArrayLength(with))
			{
				return false;
			}
			else
			{
				for (int j = 0; j < len; j++)
				{
					object elementCompare = j4o.lang.JavaSystem.GetArrayValue(compare, j);
					object elementWith = j4o.lang.JavaSystem.GetArrayValue(with, j);
					if (!IsEqual(elementCompare, elementWith, path, a_list))
					{
						return false;
					}
				}
			}
			return true;
		}

		private static string GetPath(object a_compare, object a_with, string a_path)
		{
			if (a_path != null && a_path.Length > 0)
			{
				return a_path;
			}
			if (a_compare != null)
			{
				return a_compare.GetType().FullName + ":";
			}
			if (a_with != null)
			{
				return a_with.GetType().FullName + ":";
			}
			return a_path;
		}

		internal static bool HasPublicConstructor(System.Type a_class)
		{
			if (a_class != typeof(string))
			{
				try
				{
					return System.Activator.CreateInstance(a_class) != null;
				}
				catch
				{
				}
			}
			return false;
		}

		internal static object NormalizeNArray(object a_object)
		{
			if (j4o.lang.JavaSystem.GetArrayLength(a_object) > 0)
			{
				object first = j4o.lang.JavaSystem.GetArrayValue(a_object, 0);
				if (first != null && first.GetType().IsArray)
				{
					int[] dim = ArrayDimensions(a_object);
					object all = new object[ArrayElementCount(dim)];
					NormalizeNArray1(a_object, all, 0, dim, 0);
					return all;
				}
			}
			return a_object;
		}

		internal static int NormalizeNArray1(object a_object, object a_all, int a_next, int[]
			 a_dim, int a_index)
		{
			if (a_index == a_dim.Length - 1)
			{
				for (int i = 0; i < a_dim[a_index]; i++)
				{
					j4o.lang.JavaSystem.SetArrayValue(a_all, a_next++, j4o.lang.JavaSystem.GetArrayValue
						(a_object, i));
				}
			}
			else
			{
				for (int i = 0; i < a_dim[a_index]; i++)
				{
					a_next = NormalizeNArray1(j4o.lang.JavaSystem.GetArrayValue(a_object, i), a_all, 
						a_next, a_dim, a_index + 1);
				}
			}
			return a_next;
		}

		internal static int[] ArrayDimensions(object a_object)
		{
			int count = 0;
			for (System.Type clazz = a_object.GetType(); clazz.IsArray; clazz = clazz.GetElementType
				())
			{
				count++;
			}
			int[] dim = new int[count];
			for (int i = 0; i < count; i++)
			{
				dim[i] = j4o.lang.JavaSystem.GetArrayLength(a_object);
				a_object = j4o.lang.JavaSystem.GetArrayValue(a_object, 0);
			}
			return dim;
		}

		internal static int ArrayElementCount(int[] a_dim)
		{
			int elements = a_dim[0];
			for (int i = 1; i < a_dim.Length; i++)
			{
				elements *= a_dim[i];
			}
			return elements;
		}

		private TCompare()
		{
		}
	}
}

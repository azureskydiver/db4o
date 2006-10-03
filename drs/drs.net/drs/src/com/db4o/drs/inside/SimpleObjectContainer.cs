namespace com.db4o.drs.inside
{
	public interface SimpleObjectContainer
	{
		void Activate(object @object);

		void Commit();

		void Delete(object obj);

		void DeleteAllInstances(System.Type clazz);

		/// <summary>Will cascade to save the whole graph of objects</summary>
		/// <param name="o"></param>
		void StoreNew(object o);

		/// <summary>It won't cascade.</summary>
		/// <remarks>It won't cascade. Use it with caution.</remarks>
		/// <param name="o"></param>
		void Update(object o);
	}
}

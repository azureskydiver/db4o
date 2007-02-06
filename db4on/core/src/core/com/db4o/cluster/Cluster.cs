namespace com.db4o.cluster
{
	/// <summary>allows running Queries against multiple ObjectContainers.</summary>
	/// <remarks>allows running Queries against multiple ObjectContainers.</remarks>
	/// <exclude></exclude>
	public class Cluster
	{
		public readonly com.db4o.ObjectContainer[] _objectContainers;

		public Cluster(com.db4o.ObjectContainer[] objectContainers)
		{
			if (objectContainers == null)
			{
				throw new System.ArgumentNullException();
			}
			if (objectContainers.Length < 1)
			{
				throw new System.ArgumentException();
			}
			for (int i = 0; i < objectContainers.Length; i++)
			{
				if (objectContainers[i] == null)
				{
					throw new System.ArgumentException();
				}
			}
			_objectContainers = objectContainers;
		}

		/// <summary>
		/// starts a query against all ObjectContainers in
		/// this Cluster.
		/// </summary>
		/// <remarks>
		/// starts a query against all ObjectContainers in
		/// this Cluster.
		/// </remarks>
		/// <returns>the Query</returns>
		public virtual com.db4o.query.Query Query()
		{
			lock (this)
			{
				com.db4o.query.Query[] queries = new com.db4o.query.Query[_objectContainers.Length
					];
				for (int i = 0; i < _objectContainers.Length; i++)
				{
					queries[i] = _objectContainers[i].Query();
				}
				return new com.db4o.@internal.cluster.ClusterQuery(this, queries);
			}
		}

		/// <summary>
		/// returns the ObjectContainer in this cluster where the passed object
		/// is stored or null, if the object is not stored to any ObjectContainer
		/// in this cluster
		/// </summary>
		/// <param name="obj">the object</param>
		/// <returns>the ObjectContainer</returns>
		public virtual com.db4o.ObjectContainer ObjectContainerFor(object obj)
		{
			lock (this)
			{
				for (int i = 0; i < _objectContainers.Length; i++)
				{
					if (_objectContainers[i].Ext().IsStored(obj))
					{
						return _objectContainers[i];
					}
				}
			}
			return null;
		}
	}
}

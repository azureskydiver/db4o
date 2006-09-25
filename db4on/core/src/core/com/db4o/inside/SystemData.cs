namespace com.db4o.inside
{
	/// <exclude></exclude>
	public class SystemData
	{
		private readonly com.db4o.header.FileHeader _fileHeader;

		private int _uuidIndexId;

		private int _classCollectionID;

		private int _freeSpaceID;

		public SystemData(com.db4o.header.FileHeader fileHeader)
		{
			_fileHeader = fileHeader;
		}

		public virtual int UuidIndexId()
		{
			return _uuidIndexId;
		}

		public virtual void UuidIndexId(int id)
		{
			_uuidIndexId = id;
		}

		public virtual void UuidIndexCreated(int id)
		{
			_uuidIndexId = id;
			_fileHeader.VariablePartChanged();
		}

		public virtual int ClassCollectionID()
		{
			return _classCollectionID;
		}

		public virtual int FreeSpaceID()
		{
			return _freeSpaceID;
		}

		public virtual void ClassCollectionID(int id)
		{
			_classCollectionID = id;
		}

		public virtual void FreeSpaceID(int id)
		{
			_freeSpaceID = id;
		}
	}
}

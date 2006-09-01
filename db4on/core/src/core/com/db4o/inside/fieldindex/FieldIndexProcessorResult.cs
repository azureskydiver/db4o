namespace com.db4o.inside.fieldindex
{
	public class FieldIndexProcessorResult
	{
		public static readonly com.db4o.inside.fieldindex.FieldIndexProcessorResult NO_INDEX_FOUND
			 = new com.db4o.inside.fieldindex.FieldIndexProcessorResult(null);

		public static readonly com.db4o.inside.fieldindex.FieldIndexProcessorResult FOUND_INDEX_BUT_NO_MATCH
			 = new com.db4o.inside.fieldindex.FieldIndexProcessorResult(null);

		public readonly com.db4o.TreeInt found;

		public FieldIndexProcessorResult(com.db4o.TreeInt found_)
		{
			found = found_;
		}
	}
}

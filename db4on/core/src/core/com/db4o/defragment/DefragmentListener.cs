namespace com.db4o.defragment
{
	/// <summary>Listener for defragmentation process messages.</summary>
	/// <remarks>Listener for defragmentation process messages.</remarks>
	/// <seealso cref="com.db4o.defragment.Defragment">com.db4o.defragment.Defragment</seealso>
	public interface DefragmentListener
	{
		/// <summary>
		/// This method will be called when the defragment process encounters
		/// file layout anomalies during the defragmentation process.
		/// </summary>
		/// <remarks>
		/// This method will be called when the defragment process encounters
		/// file layout anomalies during the defragmentation process.
		/// </remarks>
		/// <param name="info">The message from the defragmentation process.</param>
		void NotifyDefragmentInfo(com.db4o.defragment.DefragmentInfo info);
	}
}

namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public interface SlotReader
	{
		int Offset();

		void Offset(int offset);

		void IncrementOffset(int numBytes);

		void IncrementIntSize();

		void ReadBegin(byte identifier);

		void ReadEnd();

		byte ReadByte();

		void Append(byte value);

		int ReadInt();

		void WriteInt(int value);

		long ReadLong();

		void WriteLong(long value);

		com.db4o.foundation.BitMap4 ReadBitMap(int bitCount);

		void CopyBytes(byte[] target, int sourceOffset, int targetOffset, int length);
	}
}

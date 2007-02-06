namespace com.db4o.foundation.network
{
	public interface Socket4
	{
		void Close();

		void Flush();

		bool IsConnected();

		int Read();

		int Read(byte[] a_bytes, int a_offset, int a_length);

		void SetSoTimeout(int timeout);

		void Write(byte[] bytes);

		void Write(byte[] bytes, int off, int len);

		void Write(int i);

		com.db4o.foundation.network.Socket4 OpenParalellSocket();
	}
}

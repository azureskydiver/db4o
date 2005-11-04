namespace com.db4o.foundation.network
{
	public interface YapSocket
	{
		void close();

		void flush();

		int read();

		int read(byte[] a_bytes, int a_offset, int a_length);

		void setSoTimeout(int timeout);

		void write(byte[] bytes);

		void write(byte[] bytes, int off, int len);

		void write(int i);

		com.db4o.foundation.network.YapSocket openParalellSocket();
	}
}

namespace com.db4o.io.crypt
{
	/// <exclude></exclude>
	public class XTEA
	{
		/// <exclude></exclude>
		public sealed class IterationSpec
		{
			internal int _iterations;

			internal int _deltaSumInitial;

			internal IterationSpec(int iterations, int deltaSumInitial)
			{
				_iterations = iterations;
				_deltaSumInitial = deltaSumInitial;
			}
		}

		public static readonly com.db4o.io.crypt.XTEA.IterationSpec ITERATIONS8 = new com.db4o.io.crypt.XTEA.IterationSpec
			(8, unchecked((int)(0xF1BBCDC8)));

		public static readonly com.db4o.io.crypt.XTEA.IterationSpec ITERATIONS16 = new com.db4o.io.crypt.XTEA.IterationSpec
			(16, unchecked((int)(0xE3779B90)));

		public static readonly com.db4o.io.crypt.XTEA.IterationSpec ITERATIONS32 = new com.db4o.io.crypt.XTEA.IterationSpec
			(32, unchecked((int)(0xC6EF3720)));

		public static readonly com.db4o.io.crypt.XTEA.IterationSpec ITERATIONS64 = new com.db4o.io.crypt.XTEA.IterationSpec
			(64, unchecked((int)(0x8DDE6E40)));

		private readonly com.db4o.io.crypt.XTEA.IterationSpec _iterationSpec;

		private const int DELTA = unchecked((int)(0x9E3779B9));

		private int[] _key;

		public XTEA(string key, com.db4o.io.crypt.XTEA.IterationSpec iterationSpec) : this
			(new com.db4o.io.crypt.KeyGenerator().core(key), iterationSpec)
		{
		}

		public XTEA(string key) : this(new com.db4o.io.crypt.KeyGenerator().core(key), ITERATIONS32
			)
		{
		}

		private XTEA(int[] key, com.db4o.io.crypt.XTEA.IterationSpec iterationSpec)
		{
			if (key.Length != 4)
			{
				throw new System.ArgumentException();
			}
			_key = key;
			_iterationSpec = iterationSpec;
		}

		/// <summary>
		/// converts incoming array of eight bytes from offset to array of two
		/// integer values.<br />
		/// (An Integer is represented in memory as four bytes.)
		/// </summary>
		/// <param name="bytes">Incoming byte array of length eight to be converted<br /></param>
		/// <param name="offset">Offset from which to start converting bytes<br /></param>
		/// <param name="res">Int array of length two which contains converted array bytes.</param>
		private void byte2int(byte[] bytes, int offset, int[] res)
		{
			res[0] = (int)((((int)bytes[offset] & unchecked((int)(0xff))) << 24) | (((int)bytes
				[offset + 1] & unchecked((int)(0xff))) << 16) | (((int)bytes[offset + 2] & unchecked(
				(int)(0xff))) << 8) | ((int)bytes[offset + 3] & unchecked((int)(0xff))));
			res[1] = (int)((((int)bytes[offset + 4] & unchecked((int)(0xff))) << 24) | (((int
				)bytes[offset + 5] & unchecked((int)(0xff))) << 16) | (((int)bytes[offset + 6] &
				 unchecked((int)(0xff))) << 8) | ((int)bytes[offset + 7] & unchecked((int)(0xff)
				)));
		}

		/// <summary>
		/// converts incoming array of two integers from offset to array of eight
		/// bytes.<br />
		/// (An Integer is represented in memory as four bytes.)
		/// </summary>
		/// <param name="i">Incoming integer array of two to be converted<br /></param>
		/// <param name="offset">Offset from which to start converting integer values<br /></param>
		/// <param name="res">
		/// byte array of length eight which contains converted integer
		/// array i.
		/// </param>
		private void int2byte(int[] i, int offset, byte[] res)
		{
			res[offset] = (byte)(unchecked((int)(unchecked((uint)((i[0] & unchecked((int)(0xff000000
				))))) >> 24)));
			res[offset + 1] = (byte)(unchecked((int)(unchecked((uint)((i[0] & unchecked((int)
				(0x00ff0000))))) >> 16)));
			res[offset + 2] = (byte)(unchecked((int)(unchecked((uint)((i[0] & unchecked((int)
				(0x0000ff00))))) >> 8)));
			res[offset + 3] = (byte)(i[0] & unchecked((int)(0x000000ff)));
			res[offset + 4] = (byte)(unchecked((int)(unchecked((uint)((i[1] & unchecked((int)
				(0xff000000))))) >> 24)));
			res[offset + 5] = (byte)(unchecked((int)(unchecked((uint)((i[1] & unchecked((int)
				(0x00ff0000))))) >> 16)));
			res[offset + 6] = (byte)(unchecked((int)(unchecked((uint)((i[1] & unchecked((int)
				(0x0000ff00))))) >> 8)));
			res[offset + 7] = (byte)(i[1] & unchecked((int)(0x000000ff)));
		}

		/// <summary>enciphers two int values</summary>
		/// <param name="block">
		/// 
		/// int array to be encipher according to the XTEA encryption
		/// algorithm<br />
		/// </param>
		private void encipher(int[] block)
		{
			int n = _iterationSpec._iterations;
			int delta_sum = 0;
			while (n-- > 0)
			{
				block[0] += ((block[1] << 4 ^ block[1] >> 5) + block[1]) ^ (delta_sum + _key[delta_sum
					 & 3]);
				delta_sum += DELTA;
				block[1] += ((block[0] << 4 ^ block[0] >> 5) + block[0]) ^ (delta_sum + _key[delta_sum
					 >> 11 & 3]);
			}
		}

		/// <summary>deciphers two int values</summary>
		/// <param name="e_block">
		/// int array to be decipher according to the XTEA encryption
		/// algorithm<br />
		/// </param>
		private void decipher(int[] e_block)
		{
			int delta_sum = _iterationSpec._deltaSumInitial;
			int n = _iterationSpec._iterations;
			while (n-- > 0)
			{
				e_block[1] -= ((e_block[0] << 4 ^ e_block[0] >> 5) + e_block[0]) ^ (delta_sum + _key
					[delta_sum >> 11 & 3]);
				delta_sum -= DELTA;
				e_block[0] -= ((e_block[1] << 4 ^ e_block[1] >> 5) + e_block[1]) ^ (delta_sum + _key
					[delta_sum & 3]);
			}
		}

		/// <summary>encrypts incoming byte array according XTEA</summary>
		/// <param name="buffer">incoming byte array to be encrypted</param>
		public virtual void encrypt(byte[] buffer)
		{
			int[] asInt = new int[2];
			for (int i = 0; i < buffer.Length; i += 8)
			{
				byte2int(buffer, i, asInt);
				encipher(asInt);
				int2byte(asInt, i, buffer);
			}
		}

		/// <summary>decrypts incoming byte array according XTEA</summary>
		/// <param name="buffer">incoming byte array to be decrypted</param>
		public virtual void decrypt(byte[] buffer)
		{
			int[] asInt = new int[2];
			for (int i = 0; i < buffer.Length; i += 8)
			{
				byte2int(buffer, i, asInt);
				decipher(asInt);
				int2byte(asInt, i, buffer);
			}
		}
	}
}

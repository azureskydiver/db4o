namespace com.db4o.io.crypt
{
	/// <exclude></exclude>
	public class KeyGenerator
	{
		public KeyGenerator()
		{
		}

		/// <summary>
		/// generates an int[] array which has length four and produced according
		/// MD5 Message Digest Algorithm, as defined in RFC 1321.
		/// </summary>
		/// <remarks>
		/// generates an int[] array which has length four and produced according
		/// MD5 Message Digest Algorithm, as defined in RFC 1321.
		/// </remarks>
		/// <param name="message">
		/// -
		/// represents key to be converted as integer 128-bits<br />
		/// (will be used in XTEA encryption algorithm as key.)
		/// </param>
		/// <returns>int array of size 128-bit.</returns>
		public virtual int[] core(string message)
		{
			int[] messageAsInt = string2integer_padding(message);
			int a = unchecked((int)(0x01234567));
			int b = unchecked((int)(0x89abcdef));
			int c = unchecked((int)(0xfedcba98));
			int d = unchecked((int)(0x76543210));
			for (int i = 0; i < messageAsInt.Length; i += 16)
			{
				int variable_a = a;
				int variable_b = b;
				int variable_c = c;
				int variable_d = d;
				a = ff(a, b, c, d, messageAsInt[i + 0], 7, unchecked((int)(0xD76AA478)));
				d = ff(d, a, b, c, messageAsInt[i + 1], 12, unchecked((int)(0xE8C7B756)));
				c = ff(c, d, a, b, messageAsInt[i + 2], 17, unchecked((int)(0x242070DB)));
				b = ff(b, c, d, a, messageAsInt[i + 3], 22, unchecked((int)(0xC1BDCEEE)));
				a = ff(a, b, c, d, messageAsInt[i + 4], 7, unchecked((int)(0xF57C0FAF)));
				d = ff(d, a, b, c, messageAsInt[i + 5], 12, unchecked((int)(0x4787C62A)));
				c = ff(c, d, a, b, messageAsInt[i + 6], 17, unchecked((int)(0xA8304613)));
				b = ff(b, c, d, a, messageAsInt[i + 7], 22, unchecked((int)(0xFD469501)));
				a = ff(a, b, c, d, messageAsInt[i + 8], 7, unchecked((int)(0x698098D8)));
				d = ff(d, a, b, c, messageAsInt[i + 9], 12, unchecked((int)(0x8B44F7AF)));
				c = ff(c, d, a, b, messageAsInt[i + 10], 17, unchecked((int)(0xFFFF5BB1)));
				b = ff(b, c, d, a, messageAsInt[i + 11], 22, unchecked((int)(0x895CD7BE)));
				a = ff(a, b, c, d, messageAsInt[i + 12], 7, unchecked((int)(0x6B901122)));
				d = ff(d, a, b, c, messageAsInt[i + 13], 12, unchecked((int)(0xFD987193)));
				c = ff(c, d, a, b, messageAsInt[i + 14], 17, unchecked((int)(0xA679438E)));
				b = ff(b, c, d, a, messageAsInt[i + 15], 22, unchecked((int)(0x49B40821)));
				a = gg(a, b, c, d, messageAsInt[i + 1], 5, unchecked((int)(0xF61E2562)));
				d = gg(d, a, b, c, messageAsInt[i + 6], 9, unchecked((int)(0xC040B340)));
				c = gg(c, d, a, b, messageAsInt[i + 11], 14, unchecked((int)(0x265E5A51)));
				b = gg(b, c, d, a, messageAsInt[i + 0], 20, unchecked((int)(0xE9B6C7AA)));
				a = gg(a, b, c, d, messageAsInt[i + 5], 5, unchecked((int)(0xD62F105D)));
				d = gg(d, a, b, c, messageAsInt[i + 10], 9, unchecked((int)(0x02441453)));
				c = gg(c, d, a, b, messageAsInt[i + 15], 14, unchecked((int)(0xD8A1E681)));
				b = gg(b, c, d, a, messageAsInt[i + 4], 20, unchecked((int)(0xE7D3FBC8)));
				a = gg(a, b, c, d, messageAsInt[i + 9], 5, unchecked((int)(0x21E1CDE6)));
				d = gg(d, a, b, c, messageAsInt[i + 14], 9, unchecked((int)(0xC33707D6)));
				c = gg(c, d, a, b, messageAsInt[i + 3], 14, unchecked((int)(0xF4D50D87)));
				b = gg(b, c, d, a, messageAsInt[i + 8], 20, unchecked((int)(0x455A14ED)));
				a = gg(a, b, c, d, messageAsInt[i + 13], 5, unchecked((int)(0xA9E3E905)));
				d = gg(d, a, b, c, messageAsInt[i + 2], 9, unchecked((int)(0xFCEFA3F8)));
				c = gg(c, d, a, b, messageAsInt[i + 7], 14, unchecked((int)(0x676F02D9)));
				b = gg(b, c, d, a, messageAsInt[i + 12], 20, unchecked((int)(0x8D2A4C8A)));
				a = hh(a, b, c, d, messageAsInt[i + 5], 4, unchecked((int)(0xFFFA3942)));
				d = hh(d, a, b, c, messageAsInt[i + 8], 11, unchecked((int)(0x8771F681)));
				c = hh(c, d, a, b, messageAsInt[i + 11], 16, unchecked((int)(0x6D9D6122)));
				b = hh(b, c, d, a, messageAsInt[i + 14], 23, unchecked((int)(0xFDE5380C)));
				a = hh(a, b, c, d, messageAsInt[i + 1], 4, unchecked((int)(0xA4BEEA44)));
				d = hh(d, a, b, c, messageAsInt[i + 4], 11, unchecked((int)(0x4BDECFA9)));
				c = hh(c, d, a, b, messageAsInt[i + 7], 16, unchecked((int)(0xF6BB4B60)));
				b = hh(b, c, d, a, messageAsInt[i + 10], 23, unchecked((int)(0xBEBFBC70)));
				a = hh(a, b, c, d, messageAsInt[i + 13], 4, unchecked((int)(0x289B7EC6)));
				d = hh(d, a, b, c, messageAsInt[i + 0], 11, unchecked((int)(0xEAA127FA)));
				c = hh(c, d, a, b, messageAsInt[i + 3], 16, unchecked((int)(0xD4EF3085)));
				b = hh(b, c, d, a, messageAsInt[i + 6], 23, unchecked((int)(0x04881D05)));
				a = hh(a, b, c, d, messageAsInt[i + 9], 4, unchecked((int)(0xD9D4D039)));
				d = hh(d, a, b, c, messageAsInt[i + 12], 11, unchecked((int)(0xE6DB99E5)));
				c = hh(c, d, a, b, messageAsInt[i + 15], 16, unchecked((int)(0x1FA27CF8)));
				b = hh(b, c, d, a, messageAsInt[i + 2], 23, unchecked((int)(0xC4AC5665)));
				a = ii(a, b, c, d, messageAsInt[i + 0], 6, unchecked((int)(0xF4292244)));
				d = ii(d, a, b, c, messageAsInt[i + 7], 10, unchecked((int)(0x432AFF97)));
				c = ii(c, d, a, b, messageAsInt[i + 14], 15, unchecked((int)(0xAB9423A7)));
				b = ii(b, c, d, a, messageAsInt[i + 5], 21, unchecked((int)(0xFC93A039)));
				a = ii(a, b, c, d, messageAsInt[i + 12], 6, unchecked((int)(0x655B59C3)));
				d = ii(d, a, b, c, messageAsInt[i + 3], 10, unchecked((int)(0x8F0CCC92)));
				c = ii(c, d, a, b, messageAsInt[i + 10], 15, unchecked((int)(0xFFEFF47D)));
				b = ii(b, c, d, a, messageAsInt[i + 1], 21, unchecked((int)(0x85845DD1)));
				a = ii(a, b, c, d, messageAsInt[i + 8], 6, unchecked((int)(0x6FA87E4F)));
				d = ii(d, a, b, c, messageAsInt[i + 15], 10, unchecked((int)(0xFE2CE6E0)));
				c = ii(c, d, a, b, messageAsInt[i + 6], 15, unchecked((int)(0xA3014314)));
				b = ii(b, c, d, a, messageAsInt[i + 13], 21, unchecked((int)(0x4E0811A1)));
				a = ii(a, b, c, d, messageAsInt[i + 4], 6, unchecked((int)(0xF7537E82)));
				d = ii(d, a, b, c, messageAsInt[i + 11], 10, unchecked((int)(0xBD3AF235)));
				c = ii(c, d, a, b, messageAsInt[i + 2], 15, unchecked((int)(0x2AD7D2BB)));
				b = ii(b, c, d, a, messageAsInt[i + 9], 21, unchecked((int)(0xEB86D391)));
				a = addInteger_wrappingAt32(a, variable_a);
				b = addInteger_wrappingAt32(b, variable_b);
				c = addInteger_wrappingAt32(c, variable_c);
				d = addInteger_wrappingAt32(d, variable_d);
			}
			return new int[] { a, b, c, d };
		}

		/// <summary>core operation of MD5 Message Digest Algorithm</summary>
		private int core_operation(int q, int a, int b, int x, int s, int t)
		{
			return addInteger_wrappingAt32(bitwiseRotate32BitNumberLeft(addInteger_wrappingAt32
				(addInteger_wrappingAt32(a, q), addInteger_wrappingAt32(x, t)), s), b);
		}

		private int ff(int a, int b, int c, int d, int x, int s, int t)
		{
			return core_operation(f(b, c, d), a, b, x, s, t);
		}

		/// <summary>nonlinear function, used in each round</summary>
		private int f(int b, int c, int d)
		{
			return (b & c) | ((~b) & d);
		}

		private int gg(int a, int b, int c, int d, int x, int s, int t)
		{
			return core_operation(g(b, c, d), a, b, x, s, t);
		}

		/// <summary>nonlinear function, used in each round</summary>
		private int g(int b, int c, int d)
		{
			return (b & d) | (c & (~d));
		}

		private int hh(int a, int b, int c, int d, int x, int s, int t)
		{
			return core_operation(h(b, c, d), a, b, x, s, t);
		}

		/// <summary>nonlinear function, used in each round</summary>
		private int h(int b, int c, int d)
		{
			return b ^ c ^ d;
		}

		private int ii(int a, int b, int c, int d, int x, int s, int t)
		{
			return core_operation(i(b, c, d), a, b, x, s, t);
		}

		/// <summary>nonlinear function, used in each round</summary>
		private int i(int b, int c, int d)
		{
			return c ^ (b | (~d));
		}

		/// <summary>converts incoming padded String to an int array of size 128-bit.</summary>
		/// <remarks>converts incoming padded String to an int array of size 128-bit.</remarks>
		/// <param name="incomingString">
		/// -
		/// String to be converted to an int array of size 128-bit.
		/// </param>
		/// <returns>128-bit int array</returns>
		private int[] string2integer_padding(string incomingString)
		{
			int i = 0;
			int block = ((j4o.lang.JavaSystem.getLengthOf(incomingString) + 8) >> 6) + 1;
			int[] padded = new int[block * 16];
			for (i = 0; i < block * 16; i++)
			{
				padded[i] = 0;
			}
			for (i = 0; i < j4o.lang.JavaSystem.getLengthOf(incomingString); i++)
			{
				padded[i >> 2] |= j4o.lang.JavaSystem.getCharAt(incomingString, i) << ((i % 4) * 
					8);
			}
			padded[i >> 2] |= unchecked((int)(0x80)) << ((i % 4) * 8);
			padded[block * 16 - 2] = j4o.lang.JavaSystem.getLengthOf(incomingString) * 8;
			return padded;
		}

		private int addInteger_wrappingAt32(int x, int y)
		{
			return ((x & unchecked((int)(0x7FFFFFFF))) + (y & unchecked((int)(0x7FFFFFFF)))) 
				^ (x & unchecked((int)(0x80000000))) ^ (y & unchecked((int)(0x80000000)));
		}

		private int bitwiseRotate32BitNumberLeft(int number, int p)
		{
			return (number << p) | (unchecked((int)(unchecked((uint)(number)) >> (32 - p))));
		}
	}
}

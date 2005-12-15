package com.db4o.io.crypt;

public class XTEA {

	private static final int DELTA = 0x9E3779B9;

	private int DELTA_SUM_INITIAL = 0xC6EF3720;

	private int ITERATIONS = 32;

	private int[] key;

	/**
	 * creates an XTEA object from the given String key and iterations count.
	 * 
	 * @param key
	 *            the key, used in ecryption/decryption routine.
	 * @param iterations
	 *            iterations count. Possible values are 8, 16, 32 and 64.
	 * 
	 */
	public XTEA(String key, int iterations) {
		this(new KeyGenerator().core(key), iterations);
	}

	/**
	 * creates an XTEA object from the given String key. The default value of
	 * rounds is 32;
	 * 
	 * @param key
	 *            the key, used in ecryption/decryption routine.
	 * 
	 * 
	 */
	public XTEA(String key) {
		this(new KeyGenerator().core(key), 32);
	}

	/**
	 * creates an XTEA object from an int array of four
	 * @throws IllegalArgumentException
	 * 
	 */
	private XTEA(int[] key, int iterations) throws IllegalArgumentException {
		if (key.length != 4) {
			throw new IllegalArgumentException();
		}
		this.key = key;
		valueInt(iterations);
		this.ITERATIONS = iterations;
	}

	/**
	 * values the parameter of constructor
	 * 
	 * @param iterations
	 *            must be 8, 16, 32 or 64;
	 * @throws IllegalArgumentException
	 */
	private void valueInt(int iterations) throws IllegalArgumentException {
		switch (iterations) {
		case 8:
			DELTA_SUM_INITIAL = 0xF1BBCDC8;
			break;
		case 16:
			DELTA_SUM_INITIAL = 0xE3779B90;
			break;
		case 32:
			DELTA_SUM_INITIAL = 0xC6EF3720;
			break;
		case 64:
			DELTA_SUM_INITIAL = 0x8DDE6E40;
			break;
		default:
			throw new IllegalArgumentException(
					"illegal argument: Iterations must be set 8, 16, 32 or 64");
		}

	}

	/**
	 * converts incoming array of eight bytes from offset to array of two
	 * integer values.<br>
	 * (An Integer is represented in memory as four bytes.)
	 * 
	 * @param bytes-
	 *            Incoming byte array of length eight to be converted<br>
	 * @param offset-
	 *            Offset from which to start converting bytes<br>
	 * @param res-
	 *            Int array of length two which contains converted array bytes.
	 * 
	 */
	private void byte2int(byte[] bytes, int offset, int[] res) {
		res[0] = (int) ((((int) bytes[offset] & 0xff) << 24)
				| (((int) bytes[offset + 1] & 0xff) << 16)
				| (((int) bytes[offset + 2] & 0xff) << 8) | ((int) bytes[offset + 3] & 0xff));
		res[1] = (int) ((((int) bytes[offset + 4] & 0xff) << 24)
				| (((int) bytes[offset + 5] & 0xff) << 16)
				| (((int) bytes[offset + 6] & 0xff) << 8) | ((int) bytes[offset + 7] & 0xff));
	}

	/**
	 * converts incoming array of two integers from offset to array of eight
	 * bytes.<br>
	 * (An Integer is represented in memory as four bytes.)
	 * 
	 * @param i-
	 *            Incoming integer array of two to be converted<br>
	 * @param offset-
	 *            Offset from which to start converting integer values<br>
	 * @param res-
	 *            byte array of length eight which contains converted integer
	 *            array i.
	 */
	private void int2byte(int[] i, int offset, byte[] res) {
		res[offset] = (byte) ((i[0] & 0xff000000) >>> 24);
		res[offset + 1] = (byte) ((i[0] & 0x00ff0000) >>> 16);
		res[offset + 2] = (byte) ((i[0] & 0x0000ff00) >>> 8);
		res[offset + 3] = (byte) (i[0] & 0x000000ff);
		res[offset + 4] = (byte) ((i[1] & 0xff000000) >>> 24);
		res[offset + 5] = (byte) ((i[1] & 0x00ff0000) >>> 16);
		res[offset + 6] = (byte) ((i[1] & 0x0000ff00) >>> 8);
		res[offset + 7] = (byte) (i[1] & 0x000000ff);

	}

	/**
	 * enciphers two int values
	 * 
	 * @param block -
	 *            int array to be encipher according to the XTEA encryption
	 *            algorithm<br>
	 *            <br>
	 *            block[0] += ((block[1] << 4 ^ block[1] >> 5) + block[1]) ^
	 *            (delta_sum + key[delta_sum & 3]);<br>
	 *            delta_sum += DELTA;<br>
	 *            block[1] += ((block[0] << 4 ^ block[0] >> 5) + block[0]) ^
	 *            (delta_sum + key[delta_sum >> 11 & 3]);
	 */
	private void encipher(int[] block) {
		int n = ITERATIONS;
		int delta_sum = 0;
		while (n-- > 0) {
			block[0] += ((block[1] << 4 ^ block[1] >> 5) + block[1])
					^ (delta_sum + key[delta_sum & 3]);
			delta_sum += DELTA;
			block[1] += ((block[0] << 4 ^ block[0] >> 5) + block[0])
					^ (delta_sum + key[delta_sum >> 11 & 3]);

		}
	}

	/**
	 * deciphers two int values
	 * 
	 * @param e_block -
	 *            int array to be decipher according to the XTEA encryption
	 *            algorithm<br>
	 *            <br>
	 *            e_block[1] -= ((e_block[0] << 4 ^ e_block[0] >> 5) +
	 *            e_block[0]) ^ (delta_sum + key[delta_sum >> 11 & 3]);<br>
	 *            delta_sum -= DELTA;<br>
	 *            e_block[0] -= ((e_block[1] << 4 ^ e_block[1] >> 5) +
	 *            e_block[1]) ^ (delta_sum + key[delta_sum & 3]);
	 */
	private void decipher(int[] e_block) {
		int delta_sum = DELTA_SUM_INITIAL;
		int n = ITERATIONS;
		while (n-- > 0) {
			e_block[1] -= ((e_block[0] << 4 ^ e_block[0] >> 5) + e_block[0])
					^ (delta_sum + key[delta_sum >> 11 & 3]);
			delta_sum -= DELTA;
			e_block[0] -= ((e_block[1] << 4 ^ e_block[1] >> 5) + e_block[1])
					^ (delta_sum + key[delta_sum & 3]);
		}
	}

	/**
	 * encrypts incoming byte array according XTEA
	 * 
	 * @param buffer -
	 *            incoming byte array to be encrypted
	 * 
	 */
	public void encrypt(byte[] buffer) {
		int[] asInt = new int[2];
		for (int i = 0; i < buffer.length; i += 8) {
			byte2int(buffer, i, asInt);
			encipher(asInt);
			int2byte(asInt, i, buffer);
		}
	}

	/**
	 * decrypts incoming byte array according XTEA
	 * 
	 * @param buffer -
	 *            incoming byte array to be decrypted
	 * 
	 */
	public void decrypt(byte[] buffer) {
		int[] asInt = new int[2];
		for (int i = 0; i < buffer.length; i += 8) {
			byte2int(buffer, i, asInt);
			decipher(asInt);
			int2byte(asInt, i, buffer);
		}
	}

	public int iterations() {
		return ITERATIONS;
	}

	public void iterations(int iterations) {
		ITERATIONS = iterations;
	}

}

package com.db4o.foundation;
// This software is in the public domain.
//

/**
 * <P> Calculates the CRC32 - 32 bit Cyclical Redundancy Check
 * <P> This check is used in numerous systems to verify the integrity
 * of information.  It's also used as a hashing function.  Unlike a regular
 * checksum, it's sensitive to the order of the characters.
 * It produces a 32 bit (Java <CODE>int</CODE>.
 * <P>
 * This Java programme was translated from a C version I had written.
 * <P> This software is in the public domain.
 *
 * @author Michael Lecuyer
 *
 * @version 1.1 August 11, 1998
 *
 * @sharpen.ignore
 */
public class CRC32
{
   private static int crcTable[];   // CRC Lookup table

   static {
      buildCRCTable();     
   }

   private static void buildCRCTable()
   {
      final int CRC32_POLYNOMIAL = 0xEDB88320;

      int i, j;
      int crc;

      crcTable = new int[256];

      for (i = 0; i <= 255; i++)
      {
         crc = i;
         for (j = 8; j > 0; j--)
            if ((crc & 1) == 1)
               crc = (crc >>> 1) ^ CRC32_POLYNOMIAL;
            else
               crc >>>= 1;
         crcTable[i] = crc;
      }
   }

   public static long checkSum(byte buffer[], int start, int count)
   {
      int temp1, temp2;
      int i = start;

      int crc = 0xFFFFFFFF;

      while (count-- != 0)
      {
         temp1 = crc >>> 8;
         temp2 = crcTable[(crc ^ buffer[i++]) & 0xFF];
         crc = temp1 ^ temp2;
      }

      return (long) ~crc & 0xFFFFFFFFL;
   }
}

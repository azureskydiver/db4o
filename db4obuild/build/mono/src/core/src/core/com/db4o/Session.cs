/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
namespace com.db4o {

   internal class Session {
      internal String i_fileName;
      internal YapStream i_stream;
      private int i_openCount;
      
      internal Session(String xstring) : base() {
         i_fileName = xstring;
      }
      
      static internal void checkHackedVersion() {
      }
      
      internal bool closeInstance() {
         i_openCount--;
         return i_openCount < 0;
      }
      
      public override bool Equals(Object obj) {
         return i_fileName.Equals(((Session)obj).i_fileName);
      }
      
      internal String fileName() {
         return i_fileName;
      }
      
      internal YapStream subSequentOpen() {
         if (i_stream.isClosed()) return null;
         i_openCount++;
         return i_stream;
      }
   }
}
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
namespace com.db4o.ext {

   public class Status {
      
      public Status() : base() {
      }
      public static double UNUSED = -1.0;
      public static double AVAILABLE = -2.0;
      public static double QUEUED = -3.0;
      public static double COMPLETED = -4.0;
      public static double PROCESSING = -5.0;
      public static double ERROR = -99.0;
   }
}
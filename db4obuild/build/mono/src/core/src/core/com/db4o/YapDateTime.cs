/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
using System;

namespace com.db4o {

    internal class YapDateTime : YapTypeStruct {

        public YapDateTime(com.db4o.YapStream stream) : base(stream) {
        }

        public override int compare(object o1, object o2){
            return ((DateTime)o2 > (DateTime)o1) ? 1 : -1;
        }

        public override Object defaultValue(){
            return new DateTime(0);
        }

        public override bool isEqual(Object o1, Object o2){
            return o1.Equals(o2);
        }

        public override Object read(byte[] bytes, int offset){
            long ticks = 0;
            for (int i = 0; i < 8; i++) {
                ticks = (ticks << 8) + (long)(bytes[offset++] & 255);
            }
            return new DateTime(ticks);
        }

        public override int typeID(){
            return 25;
        }

        public override void write(object obj, byte[] bytes, int offset){
            long ticks = ((DateTime)obj).Ticks;
            for (int i = 0; i < 8; i++){
                bytes[offset++] = (byte)(int)(ticks >> (7 - i) * 8);
            }
        }
    }
}

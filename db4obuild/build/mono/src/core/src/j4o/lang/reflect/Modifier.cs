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

namespace j4o.lang.reflect {

    public class Modifier {

        public const int PUBLIC = 0x00000001;
        public const int PRIVATE = 0x00000002;
        public const int PROTECTED = 0x00000004;
        public const int STATIC = 0x00000008;
        public const int FINAL = 0x00000010;
        public const int TRANSIENT = 0x00000080;
        public const int INTERFACE = 0x00000200;
        public const int ABSTRACT = 0x00000400;

        public static bool isAbstract(int mod) {
            return (mod & ABSTRACT) != 0;
        }

        public static bool isFinal(int mod) {
            return (mod & FINAL) != 0;
        }

        public static bool isInterface(int mod) {
            return (mod & INTERFACE) != 0;
        }

        public static bool isPrivate(int mod) {
            return (mod & PRIVATE) != 0;
        }

        public static bool isProtected(int mod) {
            return (mod & PROTECTED) != 0;
        }

        public static bool isPublic(int mod) {
            return (mod & PUBLIC) != 0;
        }

        public static bool isStatic(int mod) {
            return (mod & STATIC) != 0;
        }

        public static bool isTransient(int mod) {
            return (mod & TRANSIENT) != 0;
        }
    }
}

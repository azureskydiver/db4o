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
using System.Collections;
using com.db4o.ext;

namespace com.db4o.wrap
{

    /// <summary>
    /// ObjectSet wrapper to allow using an ObjectSet as an IEnumerator
    /// </summary>
    public class ObjectSetEnumerator : IEnumerator{

        public int activationDepth = 5;
        private ExtObjectContainer i_objectContainer;
        private int i_current = -1;
        private long[] i_ids;

        public ObjectSetEnumerator(ObjectContainer objectContainer, ObjectSet objectSet){
            i_objectContainer = objectContainer.ext();
            i_ids = objectSet.ext().getIDs();
        }

        private Object activatedObject(int a_index) {
            Object obj = i_objectContainer.getByID(i_ids[a_index]);
            i_objectContainer.activate(obj, activationDepth);
            return obj;
        }

        public Object Current {
            get{
                return activatedObject(i_current);
            }
        }

        public bool MoveNext(){
            i_current++;
            return i_current >= 0 && i_current < i_ids.Length;
        }

        public void Reset(){
            i_current = -1;
        }
    }
}

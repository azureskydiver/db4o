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
using com.db4o.ext;
namespace com.db4o {

   internal class Sessions : Collection4 {
      
      internal Sessions() : base() {
      }
      
      internal void forEach(Visitor4 visitor4) {
         lock (Db4o.Lock) {
            Iterator4 iterator41 = this.iterator();
            while (iterator41.hasNext()) visitor4.visit(iterator41.next());
         }
      }
      
      internal ObjectContainer open(String xstring) {
         lock (Db4o.Lock) {
            YapRandomAccessFile yaprandomaccessfile1 = null;
            Session session1 = new Session(xstring);
            Session.checkHackedVersion();
            Session session_0_1 = (Session)this.get(session1);
            if (session_0_1 != null) {
               YapStream yapstream1 = session_0_1.subSequentOpen();
               if (yapstream1 == null) remove(session_0_1);
               return yapstream1;
            }
            try {
               {
                  yaprandomaccessfile1 = new YapRandomAccessFile(session1);
               }
            }  catch (ExpirationException expirationexception) {
               {
                  throw expirationexception;
               }
            } catch (LongJumpOutException longjumpoutexception) {
               {
                  throw longjumpoutexception;
               }
            } catch (DatabaseFileLockedException databasefilelockedexception) {
               {
                  throw databasefilelockedexception;
               }
            } catch (ObjectNotStorableException objectnotstorableexception) {
               {
                  throw objectnotstorableexception;
               }
            } catch (UserException userexception) {
               {
                  Db4o.throwRuntimeException(userexception.errCode, userexception.errMsg);
               }
            } catch (Exception throwable) {
               {
                  Db4o.logErr(Db4o.i_config, 4, xstring, throwable);
                  return null;
               }
            }
            if (yaprandomaccessfile1 != null) {
               session1.i_stream = yaprandomaccessfile1;
               this.add(session1);
               Platform.postOpen(yaprandomaccessfile1);
               Db4o.logMsg(Db4o.i_config, 5, xstring);
            }
            return yaprandomaccessfile1;
         }
      }
      
      internal override Object remove(Object obj) {
         lock (Db4o.Lock) {
            return base.remove(obj);
         }
      }
   }
}
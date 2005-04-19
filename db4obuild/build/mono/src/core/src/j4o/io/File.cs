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
using System.IO;
using com.db4o;

namespace j4o.io {

    public class File {

        private String path;

        public static char separator = Path.DirectorySeparatorChar;

        public File(String path) {
            this.path = path;
        }

        public File(String dir, String file) {
            if(dir == null) {
                path = file;
            } else {
                if(dir.LastIndexOf(separator) != dir.Length) {
                    dir += separator;
                }
                path = dir + file;
            }
        }

        public virtual bool delete() {
            if(exists()) {
                System.IO.File.Delete(path);
                return !exists();
            }
            return false;
        }

        public bool exists() {
            return System.IO.File.Exists(path) || System.IO.Directory.Exists(path);
        }

        public String getAbsolutePath() {
            return path;
        }

        public String getName() {
            int index = path.LastIndexOf(separator);
            return path.Substring(index + 1);
        }

        public String getPath() {
            return path;
        }

        public bool isDirectory() {
            return Compat.isDirectory(path);
        }

        public long length() {
            return new System.IO.FileInfo(path).Length;
        }

        public String[] list() {
            return Directory.GetFiles(path);
        }

        public bool mkdir() {
            if(exists()) {
                return false;
            }
            System.IO.Directory.CreateDirectory(path);
            return exists();
        }

        public bool mkdirs() {
            if(exists()) {
                return false;
            }
            int pos = path.LastIndexOf(separator);
            if(pos > 0) {
                new File(path.Substring(0, pos)).mkdirs();
            }
            return mkdir();
        }

        public void renameTo(File file) {
            new FileInfo(path).MoveTo(file.getPath());
        }
    }
}


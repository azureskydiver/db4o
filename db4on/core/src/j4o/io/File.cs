/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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


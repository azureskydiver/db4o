/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Text;

namespace j4o.lang {

    public class StringBuffer {

        private System.Text.StringBuilder stringBuilder;

        public StringBuffer() {
            stringBuilder = new StringBuilder();
        }
        
        public StringBuffer(string str) {
        	stringBuilder = new StringBuilder(str);
        }

        public StringBuffer append(char c) {
            stringBuilder.Append(c);
            return this;
        }

        public StringBuffer append(String str) {
            stringBuilder.Append(str);
            return this;
        }

        public StringBuffer append(Object obj) {
            stringBuilder.Append(obj);
            return this;
        }

        public override String ToString() {
            return stringBuilder.ToString();
        }
    }
}

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;
using System.Diagnostics;
using System.Reflection;
using System.Threading;

namespace com.db4o {

	/// <exclude />
    public class Compat {

        public static void addShutDownHook(EventHandler handler) {
            AppDomain.CurrentDomain.ProcessExit += handler;
            AppDomain.CurrentDomain.DomainUnload += handler;
        }

        public static Stream buffer(Stream stream){
            return new BufferedStream(stream);
        }

        public static Stream buffer(Stream stream, int bufferSize){
            return new BufferedStream(stream, bufferSize);
        }

        public static bool compact(){
            return false;
        }

        public static long doubleToLong(double a_double) {
            return BitConverter.DoubleToInt64Bits(a_double);
        }

        public static int getArrayRank(Type type) {
            return type.GetArrayRank();
        }

        public static bool isDirectory(string path) {
            return (File.GetAttributes(path) &  FileAttributes.Directory) != 0;
        }

        public static void lockFileStream(FileStream fs) {
            fs.Lock(0, 1);
        }

        public static double longToDouble(long a_long) {
            return BitConverter.Int64BitsToDouble(a_long);
        }
    
        public static void notify(object obj) {
            Monitor.Pulse(obj);
        }

        public static void notifyAll(object obj){
            Monitor.PulseAll(obj);
        }

        public static com.db4o.reflect.ReflectConstructor serializationConstructor(Type type){
            return new com.db4o.reflect.net.SerializationConstructor(type);
        }

        public static string stackTrace() {
            return new StackTrace(true).ToString();
        }

        public static void threadSetName(Thread thread, string name) {
            thread.Name = name;
        }

        public static string threadGetName(Thread thread) {
            return thread.Name;
        }

        public static void wait(object obj, long timeout) {
            Monitor.Wait(obj, (int)timeout);
        }
        
        static internal object wrapEvaluation(object evaluation) {
        	return (evaluation is com.db4o.query.EvaluationDelegate)
	        	? new EvaluationDelegateWrapper((com.db4o.query.EvaluationDelegate)evaluation)
	        	: evaluation;
        }
        
    }
}

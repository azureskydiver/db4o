/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using System.Runtime.Serialization.Formatters.Binary;
using System.IO;
using j4o.lang;
using j4o.lang.reflect;
using j4o.io;
using j4o.util;
using com.db4o.config;
using com.db4o.ext;
using com.db4o.query;
using com.db4o.reflect;
using com.db4o.types;

namespace com.db4o {


    public class Platform {

        private static String[] oldAssemblyNames;

        private static byte[] assembly;

        private static ArrayList shutDownStreams;

        private static byte[][] oldAssemblies;

        private static Object initMe = init();

        private static Object init(){
            oldAssemblyNames = new String[] {"db4o", "db4o-4.0-net1", "db4o-4.0-compact1"};
            String fullAssemblyName = typeof(Platform).Assembly.GetName().ToString();
            String shortAssemblyName = fullAssemblyName;
            int pos = fullAssemblyName.IndexOf(",");
            if(pos > 0) {
                shortAssemblyName = fullAssemblyName.Substring(0, pos);
            }
            YapStringIO stringIO = new YapStringIOUnicode();
            assembly = stringIO.write(shortAssemblyName);
            oldAssemblies = new byte[oldAssemblyNames.Length][];
            for(int i = 0; i < oldAssemblyNames.Length; i++){
                oldAssemblies[i] = stringIO.write(oldAssemblyNames[i]);
            }
            return null;
        }

        static internal void addShutDownHook(Object stream, Object streamLock) {
            lock(typeof(Platform)) {
                if (shutDownStreams == null) {
                    shutDownStreams = new ArrayList();
                    Compat.addShutDownHook(new EventHandler(shutDown));
                }
                shutDownStreams.Add(stream);
            }
        }

      
        static internal bool canSetAccessible() {
            return true;
        }

        static internal Db4oCollections collections(Object a_object){
            return new P2Collections(a_object);
        }

        static internal int collectionUpdateDepth(Class var_class) {
            return typeof(System.Collections.IDictionary).IsAssignableFrom(var_class.getNetType()) ? 3 : 2 ;
        }

        static internal Object createReferenceQueue() {
            return new YapReferenceQueue();
        }
      
        static internal Object createYapRef(Object referenceQueue, Object yapObject, Object obj) {
            return new YapRef(referenceQueue, yapObject, obj);
        }
      
        static internal long doubleToLong(double a_double) {
            return Compat.doubleToLong(a_double);
        }

        static internal QEvaluation evaluationCreate(Transaction a_trans, Object example){
            Evaluation eval = example as Evaluation;
            if(eval != null){
                return new QEvaluation(a_trans, example, false);
            }else{
                EvaluationDelegate ed = example as EvaluationDelegate;
                if(ed != null){
                    return new QEvaluation(a_trans,ed, true);
                }
            }
            return null;
        }
    
        static internal void evaluationEvaluate(Object a_evaluation, Candidate a_candidate){
            Evaluation eval = a_evaluation as Evaluation;
            if(eval != null){
                ((Evaluation)a_evaluation).evaluate(a_candidate);
            }else{
                EvaluationDelegate ed = a_evaluation as EvaluationDelegate;
                if(ed != null){
                    ed(a_candidate);
                }
            }
        }
      
        static internal Collection4 flattenCollection(Object obj) {
            Collection4 collection41 = new Collection4();
            flattenCollection1(obj, collection41);
            return collection41;
        }
      
        static internal void flattenCollection1(Object obj, Collection4 collection4) {

            Array arr = obj as Array;
            if(arr != null){
                Object[] flat = new Object[arr.Length];
                Array4.flatten(obj, Array4.dimensions(obj),0, flat, 0);
                for(int i = 0; i < flat.Length; i ++){
                    flattenCollection1(flat[i], collection4);
                }
            }else{

                // If obj implements IEnumerable, add all elements to collection4
                IEnumerator enumerator = getCollectionEnumerator(obj, true);

                // Add elements to collection if conversion was succesful
                if (enumerator != null) {
                    if(enumerator is IDictionaryEnumerator){
                        IDictionaryEnumerator dictEnumerator = enumerator as IDictionaryEnumerator;
                        while (enumerator.MoveNext()) {
                            flattenCollection1(dictEnumerator.Key, collection4);
                        }
                    }else{
                        while (enumerator.MoveNext()) {
                            // recursive call to flatten Collections in Collections
                            flattenCollection1(enumerator.Current, collection4);
                        }
                    }
                }else{

                    // If obj is not a Collection, it still needs to be collected.
                    collection4.add(obj);

                }
            }
        }
      
        static internal void forEachCollectionElement(Object obj, Visitor4 visitor) {
            
            IEnumerator enumerator = getCollectionEnumerator(obj, false);
            if (enumerator != null) {

                // If obj is a map (IDictionary in .NET speak) call visit() with the key
                // otherwise use the element itself
                if(enumerator is IDictionaryEnumerator){
                    IDictionaryEnumerator dictEnumerator = enumerator as IDictionaryEnumerator;
                    while (enumerator.MoveNext()) {
                        visitor.visit(dictEnumerator.Key);
                    }
                }else{
                    while (enumerator.MoveNext()) {
                        visitor.visit(enumerator.Current);
                    }
                }
            }
        }

        static internal String format(Date date, bool showSeconds) {
            String fmt = "yyyy-MM-dd";
            if (showSeconds){
                fmt += " HH:mm:ss";
            }
            return new DateTime(date.getTicks()).ToString(fmt);
        }

        public static Object getClassForType(Object obj) {
            Type t = obj as Type;
            if(t != null){
                return Class.getClassForType(t);
            }
            return obj;
        }

        static internal IEnumerator getCollectionEnumerator(object obj, bool allowArray){
            IEnumerable enumerable = obj as IEnumerable;
            if(enumerable != null){
                if(obj as String == null){
                    if(allowArray || obj as Array == null){
                        return enumerable.GetEnumerator();
                    }
                }
            }
            return null;
        }

        static internal void getDefaultConfiguration(Config4Impl config) {
            if(Compat.compact()){
                config.singleThreadedClient(true);
                config.weakReferenceCollectionInterval(0);
            }

            translate(config, Class.getClassForType(typeof(Class)).getName(), new TClass());
            
            translate(config, new ArrayList(), new TList());
            translate(config, new Hashtable(), new TDictionary());
            translate(config, new Queue(), new TQueue());
            translate(config, new Stack(), new TStack());

            if(! Compat.compact()){
                translate(config, "System.Collections.SortedList, mscorlib", new TDictionary());
            }
        }

        public static Object getTypeForClass(Object obj){
            Class clazz = obj as Class;
            if(clazz != null){
                return clazz.getNetType();
            }
            return obj;
        }

        static internal Object getYapRefObject(Object obj) {
            YapRef yapRef = obj as YapRef;
            if(yapRef != null){
                return yapRef.get();
            }
            return obj;
        }
      
        static internal bool hasCollections() {
            return true;
        }
      
        static internal bool hasLockFileThread() {
            return false;
        }
      
        static internal bool hasNio() {
            return false;
        }
      
        static internal bool hasWeakReferences() {
            return true;
        }

        static internal bool ignoreAsConstraint(Object obj){
            Type t = obj.GetType();
            if(t.IsEnum){
                if(System.Convert.ToInt32(obj) == 0){
                    return true;
                }
            }
            return false;
        }
      
        static internal bool isCollection(Class clazz) {
            if(clazz.isArray()){
                return false;
            }
            return typeof(ICollection).IsAssignableFrom(clazz.getNetType());
        }
      
        static internal bool isCollectionTranslator(Config4Class config4class) {
            if (config4class != null) {
                ObjectTranslator ot = config4class.getTranslator();
                if(ot != null){
                    return ot is TList || ot is TDictionary || ot is TQueue || ot is TStack;
                }
            }
            return false;
        }

        static internal bool isSecondClass(Class clazz) {
            return isValueType(clazz);
        }

        public static bool isSimple(Class a_class) {
            for (int i1 = 0; i1 < SIMPLE_CLASSES.Length; i1++) {
                if (a_class == SIMPLE_CLASSES[i1]) {
                    return true;
                }
            }
            return false;
        }

        static internal bool isValueType(Class clazz) {
            if(clazz != null){
                return clazz.getNetType().IsValueType;
            }
            return false;
        }

        static internal void killYapRef(Object obj){
            YapRef yr = obj as YapRef;
            if(yr != null){
                yr.yapObject = null;
            }
        }
      
        static internal double longToDouble(long l) {
            return Compat.longToDouble(l);
        }

        static internal void Lock(RandomAccessFile raf) {
            // do nothing. C# RAF is locked automatically upon opening
        }

        static internal void markTransient(String marker){
            Field.markTransient(marker);
        }
      
        static internal void pollReferenceQueue(Object stream, Object referenceQueue) {
            ((YapReferenceQueue)referenceQueue).poll((ExtObjectContainer)stream);
        }

        static internal void postOpen(ObjectContainer objectContainer) {
            Query q = objectContainer.query();
            q.constrain(typeof(AssemblyNameHint));
            ObjectSet objectSet = q.execute();
            while(objectSet.hasNext()){
                AssemblyNameHint anh = (AssemblyNameHint)objectSet.next();
                AssemblyNameHint existing = (AssemblyNameHint)Class.assemblies[anh.shortName];
                if(existing != null){
                    objectContainer.ext().bind(existing, objectContainer.ext().getID(anh));
                    objectContainer.set(existing);
                }else{
                    Class.assemblies[anh.shortName] = anh;
                }
            }
        }

        static internal void preClose(ObjectContainer objectContainer) {
            IEnumerator i = Class.assemblies.Keys.GetEnumerator();
            while(i.MoveNext()){
                objectContainer.set(Class.assemblies[i.Current]);
            }
        }

        static internal void removeShutDownHook(Object yapStream, Object streamLock) {
            lock (typeof(Platform)) {
                if (shutDownStreams != null && shutDownStreams.Contains(yapStream)) {
                    shutDownStreams.Remove(yapStream);
                }
            }
        }
      
        public static void setAccessible(Object obj) {
            // do nothing
        }
      
        static internal void shutDown(object sender, EventArgs args) {
            lock (typeof(Platform)) {
                foreach (object stream in shutDownStreams) {
                    Unobfuscated.shutDownHookCallback(stream);
                }
            }
        }

        private static void translate(Config4Impl config, object obj, ObjectTranslator translator) {
            try {
                config.objectClass(obj).translate(translator);
            } catch (Exception ex) {
                Unobfuscated.logErr(config, 48, obj.ToString(), null);
            }
        }
      
        static internal void unlock(RandomAccessFile randomaccessfile) {
            // do nothing. C# RAF is unlocked automatically upon closing
        }

        internal static byte[] updateClassName(byte[] bytes) {
            for(int i = 0; i < oldAssemblyNames.Length; i++){
                int j = oldAssemblies[i].Length -1;
                for(int k = bytes.Length -1 ; k >= 0; k--){
                    if(bytes[k] != oldAssemblies[i][j]){
                        break;
                    }
                    j--;
                    if(j < 0){
                        int keep =  bytes.Length - oldAssemblies[i].Length;
                        byte[] result = new byte[keep + assembly.Length];
                        Array.Copy(bytes, 0, result, 0,keep);
                        Array.Copy(assembly, 0, result, keep, assembly.Length);
                        return result;
                    }
                }
            }
            return bytes;
        }

        static internal YapTypeAbstract[] types() {
            return new YapTypeAbstract[]{
                                            new YapDouble(),
                                            new YapSByte(),
                                            new YapDecimal(),
                                            new YapUInt(),
                                            new YapULong(),
                                            new YapUShort(),
                                            new YapDateTime(),
            };
        }

        private static Class[] SIMPLE_CLASSES = {
                                                    Class.getClassForType(typeof(Int32)),
                                                    Class.getClassForType(typeof(Int64)),
                                                    Class.getClassForType(typeof(Single)),
                                                    Class.getClassForType(typeof(Boolean)),
                                                    Class.getClassForType(typeof(Double)),
                                                    Class.getClassForType(typeof(Byte)),
                                                    Class.getClassForType(typeof(Char)),
                                                    Class.getClassForType(typeof(Int16)),
                                                    Class.getClassForType(typeof(String)),
                                                    Class.getClassForType(typeof(j4o.util.Date))      };
    }






    
}
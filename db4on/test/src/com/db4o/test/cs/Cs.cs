/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using com.db4o.query;
using j4o.lang;
using j4o.lang.reflect;

namespace com.db4o.test.cs {

    /// <summary>
    /// Testing all CS value types.
    /// </summary>
    public class Cs {

        static decimal DECIMAL_PREC =  9999999999999999999999999999m;
        static float FLOAT_PREC = 1.123456E+38f;
        static double DOUBLE_PREC = 1.12345678901234E-300;

        bool boolMin;
        bool boolMax;

        byte byteMin;
        byte byteOne;
        byte byteFuzzy;
        byte byteMax;

        sbyte sbyteMin;
        sbyte sbyteNegOne;
        sbyte sbyteOne;
        sbyte sbyteFuzzy;
        sbyte sbyteMax;

        char charMin;
        char charOne;
        char charFuzzy;
        char charMax;

        decimal decimalMin;
        decimal decimalNegOne;
        decimal decimalOne;
        decimal decimalPrec;
        decimal decimalMax;

        double doubleMin;
        double doubleNegOne;
        double doubleOne;
        double doublePrec;
        double doubleMax;

        float floatMin;
        float floatNegOne;
        float floatOne;
        float floatPrec;
        float floatMax;

        int intMin;
        int intNegOne;
        int intOne;
        int intFuzzy;
        int intMax;

        uint uintMin; 
        uint uintOne;
        uint uintFuzzy;
        uint uintMax; 

        long longMin;
        long longNegOne;
        long longOne;
        long longFuzzy;
        long longMax;

        ulong ulongMin;
        ulong ulongOne;
        ulong ulongFuzzy;
        ulong ulongMax;

        short shortMin;
        short shortNegOne;    
        short shortOne;    
        short shortFuzzy;
        short shortMax;

        ushort ushortMin;
        ushort ushortOne;
        ushort ushortFuzzy;
        ushort ushortMax;

        DateTime dateTimeMin;
        DateTime dateTimeOne;
        DateTime dateTimeFuzzy;
        DateTime dateTimeMax;

        String name;

        public Cs() {
        }

        public void configure(){
            // printRange();
        }

        public void store(){
            Test.deleteAllInstances(this);
            Cs cs = new Cs();
            cs.name = "AllNull";
            Test.store(cs);
            cs = new Cs();
            cs.setValues();
            Test.store(cs);
        }

        public void setValues(){

            boolMin = false;
            boolMax = true;

            byteMin = byte.MinValue;
            byteOne = 1;
            byteFuzzy = 123;
            byteMax = byte.MaxValue;

            sbyteMin = sbyte.MinValue;
            sbyteNegOne = -1;
            sbyteOne = 1;
            sbyteFuzzy = 123;
            sbyteMax = sbyte.MaxValue;

            charMin = char.MinValue;
            charOne = (char)1;
            charFuzzy = (char)123;
            charMax = char.MaxValue;

            decimalMin = decimal.MinValue;
            decimalNegOne = -1;
            decimalOne = 1;
            decimalPrec = DECIMAL_PREC;
            decimalMax = decimal.MaxValue;

            doubleMin = double.MinValue;
            doubleNegOne = -1;
            doubleOne = 1;
            doublePrec = DOUBLE_PREC;
            doubleMax = double.MaxValue;

            floatMin = float.MinValue;
            floatNegOne = -1;
            floatOne = 1;
            floatPrec = FLOAT_PREC;
            floatMax = float.MaxValue;

            intMin = int.MinValue;
            intNegOne = -1;
            intOne = 1;
            intFuzzy = 1234567;
            intMax = int.MaxValue;

            uintMin = uint.MinValue;
            uintOne = 1;
            uintFuzzy = 1234567;
            uintMax = uint.MaxValue;

            longMin = long.MinValue;
            longNegOne = -1;
            longOne = 1;
            longFuzzy = 1234567891;
            longMax = long.MaxValue;

            ulongMin = ulong.MinValue;
            ulongOne = 1;
            ulongFuzzy = (ulong)87638635562;
            ulongMax = ulong.MaxValue;

            shortMin = short.MinValue;
            shortNegOne = -1;
            shortOne = 1;
            shortFuzzy = 12345;
            shortMax = short.MaxValue;

            ushortMin = ushort.MinValue;
            ushortOne = 1;
            ushortFuzzy = 12345;
            ushortMax = ushort.MaxValue;

            dateTimeMin = DateTime.MinValue;
            dateTimeOne = new DateTime(1);
            dateTimeFuzzy = new DateTime(2000,3,4,2,3,4,5);
            dateTimeMax = DateTime.MaxValue;

        }

        public void test(){
            Query q = Test.query();
            q.constrain(Class.getClassForType(typeof(Cs)));
            ObjectSet os = q.execute();
            Test.ensure(os.size() == 2);
            Cs cs1 = (Cs)os.next();
            Cs cs2 = (Cs)os.next();
            if(cs1.name != null){
                cs1.checkAllNull();
                cs2.checkAllPresent();
            }else{
                cs1.checkAllPresent();
                cs2.checkAllNull();
            }
        }

        public void checkAllPresent(){


            Test.ensure(! boolMin);
            Test.ensure(boolMax);

            Test.ensure(byteMin == byte.MinValue);
            Test.ensure(byteOne == 1);
            Test.ensure(byteFuzzy == 123);
            Test.ensure(byteMax == byte.MaxValue);

            Test.ensure(sbyteMin == sbyte.MinValue);
            Test.ensure(sbyteNegOne == -1);
            Test.ensure(sbyteOne == 1);
            Test.ensure(sbyteFuzzy == 123);
            Test.ensure(sbyteMax == sbyte.MaxValue);

            Test.ensure(charMin == char.MinValue);
            Test.ensure(charOne == 1);
            Test.ensure(charFuzzy == (char)123);
            Test.ensure(charMax == char.MaxValue);

            Test.ensure(decimalMin == decimal.MinValue);
            Test.ensure(decimalNegOne == -1);
            Test.ensure(decimalOne == 1);
            Test.ensure(decimalPrec == DECIMAL_PREC);
            Test.ensure(decimalMax == decimal.MaxValue);

            Test.ensure(doubleMin == double.MinValue);
            Test.ensure(doubleNegOne == -1);
            Test.ensure(doubleOne == 1);
            Test.ensure(doublePrec == DOUBLE_PREC);
            Test.ensure(doubleMax == double.MaxValue);

            Test.ensure(floatMin == float.MinValue);
            Test.ensure(floatNegOne == -1);
            Test.ensure(floatOne == 1);
            Test.ensure(floatPrec == FLOAT_PREC);
            Test.ensure(floatMax == float.MaxValue);

            Test.ensure(intMin == int.MinValue);
            Test.ensure(intNegOne == -1);
            Test.ensure(intOne == 1);
            Test.ensure(intFuzzy == 1234567);
            Test.ensure(intMax == int.MaxValue);

            Test.ensure(uintMin == uint.MinValue);
            Test.ensure(uintOne == 1);
            Test.ensure(uintFuzzy == 1234567);
            Test.ensure(uintMax == uint.MaxValue);

            Test.ensure(longMin == long.MinValue);
            Test.ensure(longNegOne == -1);
            Test.ensure(longOne == 1);
            Test.ensure(longFuzzy == 1234567891);
            Test.ensure(longMax == long.MaxValue);

            Test.ensure(ulongMin == ulong.MinValue);
            Test.ensure(ulongOne == 1);
            Test.ensure(ulongFuzzy == 87638635562);
            Test.ensure(ulongMax == ulong.MaxValue);

            Test.ensure(shortMin == short.MinValue);
            Test.ensure(shortNegOne == -1);
            Test.ensure(shortOne == 1);
            Test.ensure(shortFuzzy == 12345);
            Test.ensure(shortMax == short.MaxValue);

            Test.ensure(ushortMin == ushort.MinValue);
            Test.ensure(ushortOne == 1);
            Test.ensure(ushortFuzzy == 12345);
            Test.ensure(ushortMax == ushort.MaxValue);

            Test.ensure(dateTimeMin == DateTime.MinValue);
            Test.ensure(dateTimeOne == new DateTime(1));
            Test.ensure(dateTimeFuzzy == new DateTime(2000,3,4,2,3,4,5));
            Test.ensure(dateTimeMax == DateTime.MaxValue);

        }

        public void checkAllNull(){
            Test.ensure(name.Equals("AllNull"));
        }

        private void printRange(){
            Class clazz = Class.getClassForObject(this);
            Field[] fields = clazz.getDeclaredFields();
            for(int i = 0; i < fields.Length; i ++){
                clazz = fields[i].getType();
                String name = clazz.getName();
                if(! (name.IndexOf("System.Char") >= 0)){
                    Console.WriteLine(name);
                    Type t = Type.GetType(clazz.getName());
                    FieldInfo fi = t.GetField("MaxValue", BindingFlags.Public | BindingFlags.Static );
                    if(fi != null){
                        Console.WriteLine(fi.GetValue(t));
                    }
                    fi = t.GetField("MinValue", BindingFlags.Public | BindingFlags.Static );
                    if(fi != null){
                        Console.WriteLine(fi.GetValue(t));
                    }
                }
            }
        }

    }
}

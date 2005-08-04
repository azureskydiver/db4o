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
            Tester.deleteAllInstances(this);
            Cs cs = new Cs();
            cs.name = "AllNull";
            Tester.store(cs);
            cs = new Cs();
            cs.setValues();
            Tester.store(cs);
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
            Query q = Tester.query();
            q.constrain(Class.getClassForType(typeof(Cs)));
            ObjectSet os = q.execute();
            Tester.ensure(os.size() == 2);
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


            Tester.ensure(! boolMin);
            Tester.ensure(boolMax);

            Tester.ensure(byteMin == byte.MinValue);
            Tester.ensure(byteOne == 1);
            Tester.ensure(byteFuzzy == 123);
            Tester.ensure(byteMax == byte.MaxValue);

            Tester.ensure(sbyteMin == sbyte.MinValue);
            Tester.ensure(sbyteNegOne == -1);
            Tester.ensure(sbyteOne == 1);
            Tester.ensure(sbyteFuzzy == 123);
            Tester.ensure(sbyteMax == sbyte.MaxValue);

            Tester.ensure(charMin == char.MinValue);
            Tester.ensure(charOne == 1);
            Tester.ensure(charFuzzy == (char)123);
            Tester.ensure(charMax == char.MaxValue);

            Tester.ensure(decimalMin == decimal.MinValue);
            Tester.ensure(decimalNegOne == -1);
            Tester.ensure(decimalOne == 1);
            Tester.ensure(decimalPrec == DECIMAL_PREC);
            Tester.ensure(decimalMax == decimal.MaxValue);

            Tester.ensure(doubleMin == double.MinValue);
            Tester.ensure(doubleNegOne == -1);
            Tester.ensure(doubleOne == 1);
            Tester.ensure(doublePrec == DOUBLE_PREC);
            Tester.ensure(doubleMax == double.MaxValue);

            Tester.ensure(floatMin == float.MinValue);
            Tester.ensure(floatNegOne == -1);
            Tester.ensure(floatOne == 1);
            Tester.ensure(floatPrec == FLOAT_PREC);
            Tester.ensure(floatMax == float.MaxValue);

            Tester.ensure(intMin == int.MinValue);
            Tester.ensure(intNegOne == -1);
            Tester.ensure(intOne == 1);
            Tester.ensure(intFuzzy == 1234567);
            Tester.ensure(intMax == int.MaxValue);

            Tester.ensure(uintMin == uint.MinValue);
            Tester.ensure(uintOne == 1);
            Tester.ensure(uintFuzzy == 1234567);
            Tester.ensure(uintMax == uint.MaxValue);

            Tester.ensure(longMin == long.MinValue);
            Tester.ensure(longNegOne == -1);
            Tester.ensure(longOne == 1);
            Tester.ensure(longFuzzy == 1234567891);
            Tester.ensure(longMax == long.MaxValue);

            Tester.ensure(ulongMin == ulong.MinValue);
            Tester.ensure(ulongOne == 1);
            Tester.ensure(ulongFuzzy == 87638635562);
            Tester.ensure(ulongMax == ulong.MaxValue);

            Tester.ensure(shortMin == short.MinValue);
            Tester.ensure(shortNegOne == -1);
            Tester.ensure(shortOne == 1);
            Tester.ensure(shortFuzzy == 12345);
            Tester.ensure(shortMax == short.MaxValue);

            Tester.ensure(ushortMin == ushort.MinValue);
            Tester.ensure(ushortOne == 1);
            Tester.ensure(ushortFuzzy == 12345);
            Tester.ensure(ushortMax == ushort.MaxValue);

            Tester.ensure(dateTimeMin == DateTime.MinValue);
            Tester.ensure(dateTimeOne == new DateTime(1));
            Tester.ensure(dateTimeFuzzy == new DateTime(2000,3,4,2,3,4,5));
            Tester.ensure(dateTimeMax == DateTime.MaxValue);

        }

        public void checkAllNull(){
            Tester.ensure(name.Equals("AllNull"));
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

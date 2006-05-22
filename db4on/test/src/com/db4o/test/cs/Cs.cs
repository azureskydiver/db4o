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

        public void Configure(){
            // PrintRange();
        }

        public void Store(){
            Tester.DeleteAllInstances(this);
            Cs cs = new Cs();
            cs.name = "AllNull";
            Tester.Store(cs);
            cs = new Cs();
            cs.SetValues();
            Tester.Store(cs);
        }

        public void SetValues(){

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

        public void Test(){
            Query q = Tester.Query();
            q.Constrain(Class.GetClassForType(typeof(Cs)));
            ObjectSet os = q.Execute();
            Tester.Ensure(os.Size() == 2);
            Cs cs1 = (Cs)os.Next();
            Cs cs2 = (Cs)os.Next();
            if(cs1.name != null){
                cs1.CheckAllNull();
                cs2.CheckAllPresent();
            }else{
                cs1.CheckAllPresent();
                cs2.CheckAllNull();
            }
        }

        public void CheckAllPresent(){


            Tester.Ensure(! boolMin);
            Tester.Ensure(boolMax);

            Tester.Ensure(byteMin == byte.MinValue);
            Tester.Ensure(byteOne == 1);
            Tester.Ensure(byteFuzzy == 123);
            Tester.Ensure(byteMax == byte.MaxValue);

            Tester.Ensure(sbyteMin == sbyte.MinValue);
            Tester.Ensure(sbyteNegOne == -1);
            Tester.Ensure(sbyteOne == 1);
            Tester.Ensure(sbyteFuzzy == 123);
            Tester.Ensure(sbyteMax == sbyte.MaxValue);

            Tester.Ensure(charMin == char.MinValue);
            Tester.Ensure(charOne == 1);
            Tester.Ensure(charFuzzy == (char)123);
            Tester.Ensure(charMax == char.MaxValue);

            Tester.Ensure(decimalMin == decimal.MinValue);
            Tester.Ensure(decimalNegOne == -1);
            Tester.Ensure(decimalOne == 1);
            Tester.Ensure(decimalPrec == DECIMAL_PREC);
            Tester.Ensure(decimalMax == decimal.MaxValue);

            Tester.Ensure(doubleMin == double.MinValue);
            Tester.Ensure(doubleNegOne == -1);
            Tester.Ensure(doubleOne == 1);
            Tester.Ensure(doublePrec == DOUBLE_PREC);
            Tester.Ensure(doubleMax == double.MaxValue);

            Tester.Ensure(floatMin == float.MinValue);
            Tester.Ensure(floatNegOne == -1);
            Tester.Ensure(floatOne == 1);
            Tester.Ensure(floatPrec == FLOAT_PREC);
            Tester.Ensure(floatMax == float.MaxValue);

            Tester.Ensure(intMin == int.MinValue);
            Tester.Ensure(intNegOne == -1);
            Tester.Ensure(intOne == 1);
            Tester.Ensure(intFuzzy == 1234567);
            Tester.Ensure(intMax == int.MaxValue);

            Tester.Ensure(uintMin == uint.MinValue);
            Tester.Ensure(uintOne == 1);
            Tester.Ensure(uintFuzzy == 1234567);
            Tester.Ensure(uintMax == uint.MaxValue);

            Tester.Ensure(longMin == long.MinValue);
            Tester.Ensure(longNegOne == -1);
            Tester.Ensure(longOne == 1);
            Tester.Ensure(longFuzzy == 1234567891);
            Tester.Ensure(longMax == long.MaxValue);

            Tester.Ensure(ulongMin == ulong.MinValue);
            Tester.Ensure(ulongOne == 1);
            Tester.Ensure(ulongFuzzy == 87638635562);
            Tester.Ensure(ulongMax == ulong.MaxValue);

            Tester.Ensure(shortMin == short.MinValue);
            Tester.Ensure(shortNegOne == -1);
            Tester.Ensure(shortOne == 1);
            Tester.Ensure(shortFuzzy == 12345);
            Tester.Ensure(shortMax == short.MaxValue);

            Tester.Ensure(ushortMin == ushort.MinValue);
            Tester.Ensure(ushortOne == 1);
            Tester.Ensure(ushortFuzzy == 12345);
            Tester.Ensure(ushortMax == ushort.MaxValue);

            Tester.Ensure(dateTimeMin == DateTime.MinValue);
            Tester.Ensure(dateTimeOne == new DateTime(1));
            Tester.Ensure(dateTimeFuzzy == new DateTime(2000,3,4,2,3,4,5));
            Tester.Ensure(dateTimeMax == DateTime.MaxValue);

        }

        public void CheckAllNull(){
            Tester.Ensure(name.Equals("AllNull"));
        }

        private void PrintRange(){
            Class clazz = Class.GetClassForObject(this);
            Field[] fields = clazz.GetDeclaredFields();
            for(int i = 0; i < fields.Length; i ++){
                clazz = fields[i].GetType();
                String name = clazz.GetName();
                if(! (name.IndexOf("System.Char") >= 0)){
                    Console.WriteLine(name);
                    Type t = Type.GetType(clazz.GetName());
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

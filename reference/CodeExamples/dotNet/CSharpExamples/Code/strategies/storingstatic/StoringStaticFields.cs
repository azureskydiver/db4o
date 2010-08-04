using System;
using System.Collections.Generic;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4oDoc.Code.Strategies.StoringStatic
{
    public class StoringStaticFields
    {
        private const string DatabaseFile = "database.db4o";

        public static void Main(string[] args)
        {
            StoreCars();
            LoadCars();
        }

        private static void LoadCars()
        {
            using (IObjectContainer container = OpenDatabase())
            {
                IList<Car> cars = container.Query<Car>();

                foreach (Car car in cars)
                {
                    // #example: Compare by reference
                    // When you enable persist static field values, you can compare by reference
                    // because db4o stores the static field
                    if (car.Color == Color.Black)
                    {
                        Console.WriteLine("Black cars are boring");
                    }
                    else if (car.Color == Color.Red)
                    {
                        Console.WriteLine("Fire engine?");
                    }
                    // #end example
                }
            }
        }

        private static void StoreCars()
        {
            using (IObjectContainer container = OpenDatabase())
            {
                container.Store(new Car(Color.Black));
                container.Store(new Car(Color.White));
                container.Store(new Car(Color.Green));
                container.Store(new Car(Color.Red));
            }
        }

        private static IObjectContainer OpenDatabase()
        {
            //#example: Enable storing static fields for our color class
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.ObjectClass(typeof (Color)).PersistStaticFieldValues();
            // #end example
            return Db4oEmbedded.OpenFile(configuration, DatabaseFile);
        }
    }


    // #example: Class as enumeration
    public sealed class Color
    {
        public static readonly Color Black = new Color(0, 0, 0);
        public static readonly Color White = new Color(255, 255, 255);
        public static readonly Color Red = new Color(255, 0, 0);
        public static readonly Color Green = new Color(0, 255, 0);
        public static readonly Color Blue = new Color(0, 0, 255);

        private readonly int red;
        private readonly int green;
        private readonly int blue;

        private Color(int red, int green, int blue)
        {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int RedValue
        {
            get { return red; }
        }

        public int GreenValue
        {
            get { return green; }
        }

        public int BlueValue
        {
            get { return blue; }
        }

        public bool Equals(Color other)
        {
            if (ReferenceEquals(null, other)) return false;
            if (ReferenceEquals(this, other)) return true;
            return other.red == red && other.green == green && other.blue == blue;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != typeof (Color)) return false;
            return Equals((Color) obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int result = red;
                result = (result*397) ^ green;
                result = (result*397) ^ blue;
                return result;
            }
        }

        public override string ToString()
        {
            return string.Format("Red: {0}, Green: {1}, Blue: {2}", red, green, blue);
        }
    }
    // #end example


    public class Car
    {
        private Color color = Color.Black;

        public Car()
        {
        }

        public Car(Color color)
        {
            this.color = color;
        }

        public Color Color
        {
            get { return color; }
            set { color = value; }
        }
    }
}
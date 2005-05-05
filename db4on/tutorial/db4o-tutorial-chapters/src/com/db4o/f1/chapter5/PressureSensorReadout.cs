namespace com.db4o.f1.chapter5
{
    using System;

    public class PressureSensorReadout : SensorReadout
    {
        double _pressure;
        
        public PressureSensorReadout(DateTime time, Car car, string description, double pressure)
            : base(time, car, description)
        {
            _pressure = pressure;
        }
        
        public double Pressure
        {
            get
            {
                return _pressure;
            }
        }
        
        override public string ToString()
        {
            return base.ToString() + " pressure : " + _pressure;
        }
    }
}

namespace com.db4o.f1.chapter5
{
    using System;
    
    public class TemperatureSensorReadout : SensorReadout
    {
        double _temperature;

        public TemperatureSensorReadout(DateTime time, Car car, string description, double temperature)
            : base(time, car, description)
        {
            _temperature = temperature;
        }
        
        public double Temperature
        {
            get
            {
                return _temperature;
            }
        }
        
        override public string ToString()
        {
            return base.ToString() + " temp: " + _temperature;
        }
    }
}

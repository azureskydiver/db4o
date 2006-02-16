using System;

namespace com.db4o.f1.chapter4
{   
    public class SensorReadout
    {
        DateTime _time;
        Car _car;
        string _description;
        
        public SensorReadout(DateTime time, Car car, string description)
        {
            _time = time;
            _car = car;
            _description = description;
        }
        
        public Car Car
        {
            get
            {
                return _car;
            }
        }
        
        public DateTime Time
        {
            get
            {
                return _time;
            }
        }
        
        public string Description
        {
            get
            {
                return _description;
            }
        }
        
        override public string ToString()
        {
            return string.Format("{0}:{1}:{2}", _car, _time, _description);
        }
    }
}

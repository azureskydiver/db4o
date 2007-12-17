using System;

namespace Spikes {

    public abstract class CowEvent {
        Cow _cow;
        DateTime _date;

        public CowEvent(Cow cow, DateTime date) {
            _cow = cow;
            _date = date;
        }

        public Cow Cow {
            get { return _cow; }
        }

        public DateTime Date {
            get { return _date; }
        }
    }
}

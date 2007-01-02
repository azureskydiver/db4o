/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.NoClasses.Client
{

public class RecordBook {
	private string[,] _notes;
	private int _recordCounter;
	
	
	public RecordBook()
    {
		_notes = new string[20,3];
		_recordCounter = 0;
	}
	
	public void AddRecord(string period, string pilotName, string note)
    {
		_notes[_recordCounter, 0] = period;
        _notes[_recordCounter, 1] = pilotName;
        _notes[_recordCounter, 2] = note;
		_recordCounter ++;
	}
	
	public override string ToString()
    {
		string temp;
		temp = "Record book: \n";
		for (int i=0; i<_recordCounter;i++ ){
			temp = temp + _notes[i,0] + "/" + _notes[i,1] + "/" + _notes[i,2] + "\n";
		}
		return temp;
	}
}
}
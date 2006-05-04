package com.db4o.test;

import java.io.*;
import java.util.*;

import javax.microedition.rms.*;

import com.db4o.io.*;

public class RecordStoreIoAdapter extends IoAdapter {
	
	private RecordStore _recordStore;
	private int _recordSize;
	private Vector _recordIDs;
	private long _length;
	
	private long _curPos;
	private byte[] _page;

	public RecordStoreIoAdapter(int recordSize) {
		_recordSize=recordSize;
	}
	
	private RecordStoreIoAdapter(String path,int recordSize) throws RecordStoreException {
		_recordStore=RecordStore.openRecordStore(path,true);
		_recordSize=recordSize;
		_recordIDs=new Vector();
		_length=0;
		_curPos=0;
		_page=new byte[_recordSize];
	}
	
	public void close() throws IOException {
		try {
			_recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
	}

	public boolean exists(String path) {
		String[] recordStores=RecordStore.listRecordStores();
		if(recordStores==null) {
			return false;
		}
		for (int idx = 0; idx < recordStores.length; idx++) {
			if(path.equals(recordStores[idx])) {
				return true;
			}
		}
		return false;
	}

	public long getLength() throws IOException {
		return _length;
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException {
		try {
			return new RecordStoreIoAdapter(path,_recordSize);
		} catch (RecordStoreException exc) {
			throw new IOException(exc.getMessage());
		}
	}

	public int read(byte[] bytes, int length) throws IOException {
		//System.out.println("READ "+_curPos+","+length);
		try {
			int stillToRead=length;
			int recIdx=recordIndexForPosition(_curPos);
			int curOffset=recordOffsetForPosition(_curPos);
			while(stillToRead>0) {
				int curRead=loadRecordByIndex(recIdx);
				int nowToRead=min(stillToRead,curRead-curOffset);
				System.arraycopy(_page, curOffset, bytes, length-stillToRead, nowToRead);
				//System.out.println(" readPage "+recIdx+","+curOffset+","+nowToRead);
				stillToRead-=nowToRead;
				curOffset=0;
				recIdx++;
			}
			return length;
		} catch (RecordStoreException exc) {
			throw new IOException(exc.getMessage());
		}
	}

	public void seek(long pos) throws IOException {
		_curPos=pos;
	}

	public void sync() throws IOException {
	}

	public void write(byte[] buffer, int length) throws IOException {
		//System.out.println("WRITE "+_curPos+","+length);
		try {
			int stillToWrite=length;
			int recIdx=recordIndexForPosition(_curPos);
			while(_recordIDs.size()<=recIdx) {
				addPage();
			}
			int curOffset=recordOffsetForPosition(_curPos);
			while(stillToWrite>0) {
				if(_recordIDs.size()<=recIdx) {
					addPage();
				}
				int curRead=loadRecordByIndex(recIdx);
				int nowToWrite=min(stillToWrite,curRead-curOffset);
				System.arraycopy(buffer, length-stillToWrite, _page, curOffset, nowToWrite);
				_recordStore.setRecord(recordIDByIndex(recIdx), _page, 0, _recordSize);
				//System.out.println(" writePage "+recIdx+","+curOffset+","+nowToWrite);
				stillToWrite-=nowToWrite;
				curOffset=0;
				recIdx++;
			}
		} catch (RecordStoreException exc) {
			throw new IOException(exc.getMessage());
		}
	}

	private void addPage() throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
		int recID=_recordStore.addRecord(_page, 0, _recordSize);
		_recordIDs.addElement(new Integer(recID));
		System.out.println(" addPage "+(_recordIDs.size()-1)+":"+recID);
	}
	
	private int recordIndexForPosition(long pos) {
		return (int)(pos/_recordSize);
	}

	private int recordOffsetForPosition(long pos) {
		return (int)(pos%_recordSize);
	}

	private int loadRecordByIndex(int recIdx) throws RecordStoreException {
		int read=_recordStore.getRecord(recordIDByIndex(recIdx),_page,0);
		//System.out.println(" read "+read+" for "+recIdx);
		return read;
	}

	private int recordIDByIndex(int recIdx) {
		return ((Integer)_recordIDs.elementAt(recIdx)).intValue();
	}
	
	private int min(int a,int b) {
		return (a<b ? a : b);
	}
	
	public void log() throws RecordStoreException {
		for(int i=0;i<_recordIDs.size();i++) {
			loadRecordByIndex(i);
			System.out.println(i);
			for(int j=0;j<_page.length;j++) {
				System.out.print(_page[j]+" ");
			}
			System.out.println();
		}
	}

	public void delete(String path) {
		// TODO Auto-generated method stub
		
	}
}

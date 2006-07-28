/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.lib;

public class String4
{
	private static final char DATESEPARATOR = '-';
	private static final char TIMESEPARATOR = ':';
	private static final char DATETIMESEPARATOR = ' ';
	
	private String i_string;
	
	private String4(){}
	
	public String4(String a_String){
		i_string = a_String;
	}
	
	public String4(char a_char, int a_count){
		char[] l_array = new char[a_count];
		for (int i = 0; i < a_count;l_array[i++] = a_char);
		i_string = new String(l_array);
	}
	
	public String4(int a_int){
		i_string = "" + a_int;
	}
	
	public String4(long a_long){
		i_string = "" + a_long;
	}
	
	public String4(char a_char){
		i_string = "" + a_char;
	}
	
	public void clear(){
		i_string = "";
	}
	
	public String getString(){
		return i_string;
	}
	
	protected String joinDate(String a_Year, String a_Month, String a_Day ){
		i_string = a_Year + DATESEPARATOR + a_Month + DATESEPARATOR + a_Day;
		return i_string;
	}
	
	protected String joinTime(String a_Hours, String a_Minutes,String a_Seconds){
		i_string = a_Hours + TIMESEPARATOR + a_Minutes + TIMESEPARATOR + a_Seconds;
		return i_string;
	}

	protected String joinDateTime(String a_Date, String a_Time){
		i_string = a_Date + DATETIMESEPARATOR + a_Time;
		return i_string;
	}
	
	public static String _left(String a_String, int a_chars){
		return new String4(a_String).left(a_chars);
	}
	
	public String left(int a_chars){
		if(a_chars > i_string.length()){
			a_chars = i_string.length();
		}else{
			if (a_chars < 0){
				a_chars = 0;
			}
		}
		return i_string.substring(0,a_chars);
	}
	
	public static boolean _left(String ofString, String isString){
		return new String4(ofString).left(isString);
	}
	
	public boolean left(String compareString){
		return left(compareString.length()).toUpperCase().equals(compareString.toUpperCase());
	}
	
	public String PadLeft(char a_char, int a_length){
		return new String4(new String4(a_char,a_length).getString() + i_string).right(a_length);
	}
	
	public String PadRight(char a_char, int a_length){
		return (i_string + new String4(a_char,a_length).getString()).substring(0,a_length);
	}
	
	public void replace(String a_Replace, String a_With){
		replace(a_Replace, a_With,0);
	}
	
	public static String _replace(String a_in, String a_replace, String a_with){
		String4 s = new String4(a_in);
		s.replace(a_replace, a_with);
		return s.getString();
	}
	
	public void replace(String a_Replace, String a_With, int a_start){
		int l_pos = 0;
		while((l_pos = i_string.indexOf(a_Replace,a_start)) > -1){
			i_string = i_string.substring(0,l_pos) + a_With + i_string.substring(l_pos + a_Replace.length());
		}
	}
	
	public void replace (String a_ReplaceBegin, String a_ReplaceEnd, String a_With){
		replace (a_ReplaceBegin, a_ReplaceEnd, a_With, 0);
	}
	
	public void replace (String a_ReplaceBegin, String a_ReplaceEnd, String a_With, int a_start){
		int l_pos_from = i_string.indexOf(a_ReplaceBegin, a_start);
		if (l_pos_from > -1){
			int l_pos_to = i_string.indexOf(a_ReplaceEnd,l_pos_from + 1);
			if(l_pos_to > - 1){
				i_string = i_string.substring(0,l_pos_from) + a_With + i_string.substring(l_pos_to + a_ReplaceEnd.length());
				replace(a_ReplaceBegin, a_ReplaceEnd, a_With, l_pos_to);
			}
		}
	}
	
	public static String _right(String ofString, int isChar){
		return new String4(ofString).right(isChar);
	}
	
	public String right(int a_chars){
		int l_take = i_string.length() - a_chars;
		if(l_take < 0){
			l_take = 0;
		}
		return i_string.substring(l_take);
	}
	
	public static boolean _right(String ofString, String compareString){
		return new String4(ofString).right(compareString);
	}
	
	public boolean right(String compareString){
		int l_take = i_string.length() - compareString.length();
		if(l_take < 0){
			l_take = 0;
		}
		String right = i_string.substring(l_take).toUpperCase();
		return right.equals(compareString.toUpperCase());
	}
	
	public static String _splitRight(String a_String, String a_Splitter){
		return new String4(a_String).splitRight(a_Splitter);
	}
	
	public String splitRight(String a_Splitter){
		String l_Return = "";
		int l_pos = i_string.lastIndexOf(a_Splitter);
		if(l_pos > 0){
			l_Return = i_string.substring(l_pos + a_Splitter.length());
			i_string = i_string.substring(0,l_pos);
		}
		return l_Return;
	}
	
	public String toString(){
		return i_string;
	}
	
	private String toYear(){
		switch (i_string.length()){
			case 1:
				i_string = "200" + i_string;
				break;
			case 2:
				i_string = "20" + i_string;		   
				break;
			case 3:
				i_string = "2" + i_string;		   
				break;
		}
		return i_string;
	}
	
	private String toZeroTwoDigit(){
		if (i_string.length() == 1)
			i_string = "0" + i_string;
		return i_string;
	}
}

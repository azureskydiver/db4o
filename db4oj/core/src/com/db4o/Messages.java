/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
public final class Messages
{
    
    private static String[] i_messages;
	
	public static String get(int a_code){
		return get(a_code, null);
	}

	public static String get(int a_code, String param){
		if(a_code < 0){
			return param;
		}
		load();
		if(i_messages == null || a_code > i_messages.length - 1){
			return "msg[" + a_code + "]";
		}
		String msg = i_messages[a_code];
		if(param != null){
			int pos = msg.indexOf("%",0);
			if(pos > -1){
				msg = msg.substring(0, pos)
					  + "'"
					  + param
					  + "'"
					  + msg.substring(pos + 1);
			}
		}
		return msg;
	}
	
	private static void load(){
	    if(i_messages == null) {
			 if(Tuning.readableMessages){
			            
		        i_messages = new String[] {
						"", // unused
						"blocksize should be between 1 and 127", // unused 
						"% close request",
						"% closed",
						"Exception opening %",
						"% opened O.K.", // 5
						"Class %: Instantiation failed. \n Check custom ObjectConstructor code.",
						"Class %: Instantiation failed.\n Add a constructor for use with db4o, ideally with zero arguments.",
						"renaming %",
						"rename not possible. % already exists",
						"rename failed", // 10
						"File close failed.",
						"File % not available for readwrite access.",
						"File read access failed.",
						"File not found: % Creating new file",
						"Creation of file failed: %", // 15
						"File write failed.",
						"File format incompatible.",
						"Uncaught Exception. Engine closed.",
						"writing log for %",
						"% is closed. close() was called or open() failed.", // 20
						"Filename not specified.",
						"The database file is locked by another process.",
						"Class not available: %. Check CLASSPATH settings.",
						"finalized while performing a task.\n DO NOT USE CTRL + C OR System.exit() TO STOP THE ENGINE.",
						"Please mail the following to info@db4o.com:\n <db4o stacktrace>", // 25
						"</db4o stacktrace>",
						"Creation of lock file failed: %",
						"Previous session was not shut down correctly",
						"This feature is not available in this version.",
						"Could not open port: %", // 30
						"Server listening on port: %",
						"Client % connected.",
						"Client % timed out and closed.",
						"Connection closed by client %.",
						"Connection closed by server. %.",// 35
						"% connected to server.",
						"The directory % can neither be found nor created.",
						"This blob was never stored.",
						"Blob file % not available.",
						"Failure finding blob filename.", // 40
						"File does not exist %.",
						"Failed to connect to server.",
						"No blob data stored.",
						"Uncaught Exception. db4o engine closed.",
						"Add a public zero-parameter constructor to class %.", // 45
						"This method can only be called before opening the database file.",
						"AccessibleObject#setAccessible() is not available. Private fields can not be stored.",
						"ObjectTranslator could not be installed: %.",
						"",  // replacement for expiration message below
						// "This trial version will expire on " +  Platform.format(new Date(Lic.expirationDate), false) + ".",
						"% closed by ShutdownHook.", // 50
						"This database file has already been used with a different trial version.\n"
						+ " Please use an unrestricted version for productive use.\n"
						+ " Unrestricted versions are available for members of the db4o developer network.\n\n", // 51
						"", // empty: No memory security message
						"Call Db4o.licensedTo(key) to initialize this licensed db4o database engine.",
						"This is a db4o trial version. Licensed versions can be obtained from www.db4o.com.", 
						"Thread interrupted.", // 55
						"Password can not be null.",
						"Classes does not match.", 
						"rename() needs to be executed on the server.",
						"Primitive types like % can not be stored directly. Store and retrieve them in wrapper objects.",
						"Backups can not be run from clients and memory files.", // 60
						"Backup in progress.", 
						"Only use persisted first class objects as keys for IdentityHashMap." // 62
						
									};
		        }else{
		            i_messages = new String[0];
		        }
            }

    }
}


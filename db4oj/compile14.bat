
SET SJAVAC="C:\Program Files\Java\jdk1.5.0_01\bin\javac.exe"
SET SROOT=C:\_db4o\development\db4oj\

del c:\javacdirfile
dir *.java /B /S >> c:\javacdirfile

%SJAVAC% -d %SROOT%bin14 -target jsr14 @c:\javacdirfile 

del c:\javacdirfile


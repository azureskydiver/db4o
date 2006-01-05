REM is this script needed at all?
SET SJAVAC="C:\Program Files\Java\jdk1.5.0_01\bin\javac.exe"
SET SROOT=C:\_db4o\HEAD\db4oj\

md %SROOT%bin14

del c:\javacdirfile

dir %SROOT%\core\src\*.java /B /S >> c:\javacdirfile
REM dir %SROOT%\test\src\*.java /B /S >> c:\javacdirfile

%SJAVAC% -d %SROOT%bin14 -target jsr14 @c:\javacdirfile 

del c:\javacdirfile


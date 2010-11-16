
REM SET JAVA="C:\Program Files (x86)\Java\jdk1.6.0_17\bin\java.exe"
SET JAVA=java

SET EXAMPLE_HOME=D:\Workspaces\trunk\drsVodExample

SET DATABASE_NAME=dRSVodExample

SET SERVER_PORT=4000
SET CLIENT_PORT=4100
SET EVENTPROCESSOR_PORT=4200

SET VERSANT_BIN=%VERSANT_ROOT%\bin
SET VERSANT_LIB=%VERSANT_ROOT%\lib

SET JVI=%VERSANT_LIB%\jvi80.jar
SET VOD_JDO=%VERSANT_LIB%\vodjdo.jar
SET JDO_JAR=%VERSANT_LIB%\jdo2-api-2.1.jar
SET PIZZA=%VERSANT_LIB%\pizza.jar
SET ASM=%VERSANT_LIB%\asm-all-3.1.jar

SET VED_CONFIGFILE=config.ved.win
SET LOG_FILE="drsLogFile.log"

SET DB4O_VERSION=8.0.173.15095

SET CLI=%EXAMPLE_HOME%\lib\commons-cli-1.2.jar
SET DB4O_JAR=%EXAMPLE_HOME%\lib\db4o-%DB4O_VERSION%-all-java5.jar
SET DRS_PATH=%EXAMPLE_HOME%\lib\dRS-%DB4O_VERSION%-core.jar
REM for debugging:
REM SET DRS_PATH=D:/workspaces/trunk/drs/bin

REM delete the following before releasing
SET PATCH_JAR=%EXAMPLE_HOME%\lib\vod8.drs.patch.jar

SET LOCAL_CLASSPATH=%PATCH_JAR%;%VOD_JDO%;%JVI%;%DRS_PATH%;%CLI%;%PIZZA%;%JDO_JAR%;%ASM%;%DB4O_JAR%;


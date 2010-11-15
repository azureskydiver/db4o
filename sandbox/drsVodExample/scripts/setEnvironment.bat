
SET DATABASE_NAME=dRSVodExample

SET DB4O_VERSION=8.0.173.15095

SET DRS_HOME=C:/drs-8.0
SET DRS_LIB=%DRS_HOME%/lib

rem SET DRS_PATH=%DRS_LIB%/dRS-%DB4O_VERSION%-core.jar

SET DRS_PATH=D:/workspaces/trunk/drs/bin

SET JAVA="C:\Program Files (x86)\Java\jdk1.6.0_17\bin\java.exe"

SET SERVER_PORT=4000
SET CLIENT_PORT=4100
SET EVENTPROCESSOR_PORT=4200

SET VERSANT_BIN=%VERSANT_ROOT%\bin
SET VERSANT_LIB=%VERSANT_ROOT%\lib

SET VED_CONFIGFILE=config.ved.win
SET LOG_FILE="drsLogFile.log"

SET JVI=%VERSANT_LIB%\jvi80.jar
SET VOD_JDO=%VERSANT_LIB%\vodjdo.jar
SET JDO_JAR=%VERSANT_LIB%\jdo2-api-2.1.jar
SET PIZZA=%VERSANT_LIB%\pizza.jar
SET ASM=%VERSANT_LIB%\asm-all-3.1.jar

rem delete the following 4 before releasing
SET PMC=C:\TEMP
SET COBRA=%PMC%\cobra\bin
SET ENGINE=%PMC%\oa4engine/bin
SET PRODUCT=%PMC%\oa4product\bin
SET VDS=%PMC%\vds\bin


SET CLI=%DRS_LIB%\commons-cli-1.2.jar
SET DB4O_JAR=%DRS_LIB%\db4o-%DB4O_VERSION%-core-java5.jar

SET LOCAL_CLASSPATH=%COBRA%;%ENGINE%;%PRODUCT%;%VDS%;%VOD_JDO%;%JVI%;%DRS_PATH%;%CLI%;%PIZZA%;%JDO_JAR%;%ASM%;%DB4O_JAR%;


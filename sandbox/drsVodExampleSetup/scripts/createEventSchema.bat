call setEnvironment.bat
rem sch2db -d %DATABASE_NAME% -y  %VERSANT_ROOT%/lib/channel.sch  %VERSANT_ROOT%/lib/vedsechn.sch

%JAVA% -cp %LOCAL_CLASSPATH% com.db4o.drs.versant.eventprocessor.CreateEventSchema %DATABASE_NAME%
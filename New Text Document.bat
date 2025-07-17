@echo off
REM Set memory and encoding options
set JAVA_OPTS=-server -Xms1000M -Xmx1000M -Dfile.encoding=UTF-8

REM Run server with full classpath
java %JAVA_OPTS% -cp "dist/NGOC_RONG_ONLINE.jar;lib/*" server.ServerManager

pause

@ECHO OFF

java -server -Xms1000M -Xmx1000M -Dfile.encoding=UTF-8 -cp "dist/NGOC_RONG_ONLINE.jar;lib/*" server.ServerManager


PAUSE
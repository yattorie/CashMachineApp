@echo off
setlocal

set JAVA_HOME=C:\Users\andre\.jdks\corretto-17.0.11

set CLASSPATH=C:\Users\andre\IdeaProjects\CashMachineApp\out\production\CashMachineApp

%JAVA_HOME%\bin\java -classpath %CLASSPATH% atm.Main

endlocal
pause

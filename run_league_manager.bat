@echo off
set JAVA_HOME=C:\Program Files\Java\openjdk-24.0.1_windows-x64
set PATH=%JAVA_HOME%\bin;%PATH%
set JAVAFX_HOME=C:\Program Files\Java\javafx-sdk-24.0.1

REM Create output directory if it doesn't exist
mkdir out 2>nul

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

REM Download SQLite JDBC driver if not present
if not exist "lib\sqlite-jdbc-3.45.1.0.jar" (
    echo Downloading SQLite JDBC driver...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar' -OutFile 'lib\sqlite-jdbc-3.45.1.0.jar'"
)

REM Download SLF4J API if not present
if not exist "lib\slf4j-api-2.0.12.jar" (
    echo Downloading SLF4J API...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.12/slf4j-api-2.0.12.jar' -OutFile 'lib\slf4j-api-2.0.12.jar'"
)

REM Download SLF4J Simple binding if not present
if not exist "lib\slf4j-simple-2.0.12.jar" (
    echo Downloading SLF4J Simple binding...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.12/slf4j-simple-2.0.12.jar' -OutFile 'lib\slf4j-simple-2.0.12.jar'"
)

echo Compiling...
"%JAVA_HOME%\bin\javac.exe" --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls -cp "lib\*" -d out src/tournament/scheduler/masterdata/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Running...
"%JAVA_HOME%\bin\java.exe" --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls --add-opens javafx.graphics/com.sun.glass.utils=ALL-UNNAMED --add-opens javafx.graphics/com.sun.marlin=ALL-UNNAMED -cp "out;lib\*" tournament.scheduler.masterdata.LeagueManagerGUI

pause 
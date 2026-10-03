@ECHO OFF
SETLOCAL
SET WRAPPER_JAR=%~dp0.mvn\wrapper\maven-wrapper.jar
SET PROJECT_ROOT=%~dp0
SET PROJECT_ROOT=%PROJECT_ROOT:~0,-1%
IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Downloading Maven Wrapper...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "New-Item -ItemType Directory -Force -Path '%~dp0.mvn\wrapper' | Out-Null; Invoke-WebRequest -UseBasicParsing 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar' -OutFile '%WRAPPER_JAR%'"
  IF ERRORLEVEL 1 EXIT /B 1
)
java -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%PROJECT_ROOT%" org.apache.maven.wrapper.MavenWrapperMain %*
ENDLOCAL

@echo off
SET rootpath=%~dp0
SET configHome=%rootpath%gateway-configuration/gateway-configuration-distribution
SET logbackLocation=%rootpath%gateway-configuration/gateway-configuration-distribution
SET /p userConfig="Provide the configuration home (press enter to use the default path '%configHome%'): "
SET /p userLogback="Provide the logback location (press enter to use the default path '%logbackLocation%':"
IF NOT "%userConfig%"=="" (
    SET configHome=%userConfig%
)
IF NOT "%userLogback%"=="" (
    SET logbackLocation=%userLogback%
)
echo "java -Xmx2048m -Xms256m -jar %rootpath%swiftwallet-gateway.jar"
java -Xmx2048m -Xms256m -Dodin.zookeeper.connectString=localhost:2181 -jar %rootpath%swiftwallet-gateway.jar
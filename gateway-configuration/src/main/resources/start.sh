#!/bin/bash
rootpath=$(pwd)
configHome=$rootpath/gateway-configuration/gateway-configuration-distribution
logbackLocation=$rootpath/gateway-configuration/gateway-configuration-distribution
printf "Provide the configuration home (press enter to use the default path '$configHome'): "
read userConfig
if [ ! -z "$userConfig" ]
    then
        configHome=userConfig
fi
printf "Provide the logback location (press enter to use the default path '$logbackLocation'): "
read userLogback
if [ ! -z "$userLogback" ]
    then
        logbackLocation=userLogback
fi
printf "java -Xmx2048m -Xms256m -jar $rootpath/swiftwallet-gateway.jar"
java -Xmx2048m -Xms256m -Dodin.zookeeper.connectString=localhost:2181 -jar $rootpath/swiftwallet-gateway.jar
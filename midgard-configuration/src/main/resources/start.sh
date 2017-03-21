#!/bin/bash
rootpath=$(pwd)
configHome=$rootpath/midgard-configuration/midgard-configuration-distribution
logbackLocation=$rootpath/midgard-configuration/midgard-configuration-distribution
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
printf "java -Xmx2048m -Xms256m -jar $rootpath/swiftwallet-midgard.jar"
java -Xmx2048m -Xms256m -Dodin.zookeeper.connectString=localhost:2181 -Dmicro-service.id=midgard-node1 -jar $rootpath/swiftwallet-midgard.jar
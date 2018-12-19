#!/bin/bash
export JAVA_HOME=/jdk1.8.0_151
###启动urc-service-provider服务
/opt/urc-service-provider/bin/service-provider-shutdown.sh
sleep 2
cd /opt
rm -rf /opt/urc-service-provider/
tar -xvf urc-service-provider-assembly.tar.gz
sleep 2
cd urc-service-provider/bin
./start.sh

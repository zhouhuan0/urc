export JAVA_HOME=/jdk1.8.0_151
ps -ef | grep urc-service-provider|egrep -v "color"|awk '{print $2}'|xargs kill -9
sleep 3
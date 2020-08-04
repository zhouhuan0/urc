#!/bin/bash
# 生产jdbc文件替换到本地，方便调试生产环境

cd ../../
rm source/urc-mgr/src/main/resources/git.txt
rm source/urc-taskScheduler/src/main/resources/git.txt
rm source/urc-taskScheduler/src/main/resources/kafka-producer.properties
rm source/urc-taskScheduler/src/main/resources/kafka-consumer.properties
rm source/urc-taskScheduler/src/main/resources/motan/motan-service-config.xml

git checkout -- ./source/conf/*
git checkout -- ./source/urc-service-provider/src/main/resources/*
git checkout -- ./source/urc-mgr/src/main/resources/*
git checkout -- ./source/urc-taskScheduler/src/main/resources/*

#!/bin/bash

git name-rev --name-only HEAD >>  ./source/urc-mgr/src/main/resources/git.txt
git log --pretty=format:"%an %ad %h %s" -n 1  --date=format:'%Y-%m-%d %H:%M:%S' >>  ./source/urc-mgr/src/main/resources/git.txt

git name-rev --name-only HEAD >>  ./source/urc-taskScheduler/src/main/resources/git.txt
git log --pretty=format:"%an %ad %h %s" -n 1  --date=format:'%Y-%m-%d %H:%M:%S' >>  ./source/urc-taskScheduler/src/main/resources/git.txt

cp -rf ./source/conf/urc-service-provider/src/main/resources/* ./source/urc-service-provider/src/main/resources
cp -rf ./source/conf/urc-service-provider/src/main/resources/* ./source/urc-taskScheduler/src/main/resources
cp -rf ./source/conf/urc-service-provider/src/main/resources/* ./source/urc-mgr/src/main/resources
cp -rf ./source/conf/urc-mgr/src/main/resources/* ./source/urc-mgr/src/main/resources

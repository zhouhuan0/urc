#!/bin/bash
# 生产jdbc文件替换到本地，方便调试生产环境

cd ../../
git checkout -- ./source/urc-service-provider/src/main/resources/*
git checkout -- ./source/urc-mgr/src/main/resources/*
git checkout -- ./source/urc-taskScheduler/src/main/resources/*


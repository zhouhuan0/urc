package com.yks.urc.lock.bp.impl;

import java.util.List;

public class RedissonLockConfigVO {
    public String serverType;
    public String address;
    public Integer database;
    public List<String> clusterAddress;
}

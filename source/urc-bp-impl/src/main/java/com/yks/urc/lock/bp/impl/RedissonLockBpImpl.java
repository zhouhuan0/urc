package com.yks.urc.lock.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.lock.bp.api.ILockBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockBpImpl implements ILockBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String getSeparateWarehouseLockName(String yksOrderId) {
        return String.format("lk_od_%s", yksOrderId);
    }

    private Redisson redisson = null;
    private Config config = new Config();

    @Autowired
    private IConfigBp configBp;

    @Autowired
    private ISerializeBp serializeBp;

    @PostConstruct
    private void init() {
        RedissonLockConfigVO configVO = serializeBp.json2ObjNew(configBp.getString("redisson_lock_config"),
                new TypeReference<RedissonLockConfigVO>() {
                });
        if ("redis-single".equalsIgnoreCase(configVO.serverType)) {
            SingleServerConfig sc = config.useSingleServer().setAddress(configVO.address).
                    setDatabase(configVO.database).
                    setConnectionPoolSize(1000).setConnectionMinimumIdleSize(5).setConnectTimeout(3000).setTimeout(3000).setPingConnectionInterval(6000).setPingTimeout(6000);
            redisson = (Redisson) Redisson.create(config);
        } else {
            ClusterServersConfig sc = config.useClusterServers().setScanInterval(2000).setMasterConnectionPoolSize(1000).
                    setSlaveConnectionPoolSize(2000).setPingConnectionInterval(6000).setPingTimeout(6000);
            for (String address : configVO.clusterAddress) {
                sc.addNodeAddress(address);
            }
            redisson = (Redisson) Redisson.create(config);
        }

    }

    @Override
    public Boolean tryLockSeparateWarehouse(String yksOrderId) {
        return tryLock(getSeparateWarehouseLockName(yksOrderId));
    }

    private String getLockName(String lockName) {
        return String.format("lk_%s", lockName);
    }

    @Override
    public Boolean tryLock(String lockName) {
        try {
            RLock mylock = redisson.getLock(getLockName(lockName));
            // 1s拿不到锁则退出
            return mylock.tryLock(1, TimeUnit.SECONDS);
        } catch (Exception ex) {
            logger.error(lockName, ex);
        }
        return false;
    }

    @Override
    public void unlockSeparateWarehouse(String yksOrderId) {
        unlock(getSeparateWarehouseLockName(yksOrderId));
    }

    @Override
    public void unlock(String lockName) {
        try {
            RLock mylock = redisson.getLock(getLockName(lockName));
            if(mylock != null && mylock.isHeldByCurrentThread()){
                mylock.unlock();
            }
        } catch (Exception ex) {
            logger.error(String.format("unlock error:%s", lockName), ex);
        }
    }
}

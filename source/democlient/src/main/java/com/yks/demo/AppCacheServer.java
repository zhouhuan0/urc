package com.yks.demo;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import com.yks.distributed.cache.constant.Constant;
import com.yks.distributed.cache.core.Cache;
import com.yks.distributed.cache.core.DistributedCache;
import com.yks.distributed.cache.utils.SpringUtils;

/**
 * Hello world!
 *
 */
public class AppCacheServer {
	public static void main(String[] args) {
		testCache();
	}

	private static void testServer() {
		IgniteConfiguration igniteConfig = SpringUtils.getBean(Constant.ConfigFile.DEFAULT_CONFIG, Constant.Cache.IGNITE_CONFIGURATION, IgniteConfiguration.class);
		igniteConfig.setClientMode(false);
		Ignition.start("ignite-server-config.xml");
		testCache();
	}

	private static void testCache() {
		Cache<String, String> sysFuncJsonCache = new DistributedCache<>("URC-test");// , 100, TimeUnit.DAYS);
		sysFuncJsonCache.put("py", "abc");
		System.out.println("---------------" + sysFuncJsonCache.get("py"));
	}
}

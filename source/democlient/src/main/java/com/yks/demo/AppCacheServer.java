package com.yks.demo;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import com.yks.distributed.cache.constant.Constant;
import com.yks.distributed.cache.core.Cache;
import com.yks.distributed.cache.core.DistributedCache;
import com.yks.distributed.cache.utils.SpringUtils;
import com.yks.urc.fw.StringUtility;

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
		Cache<String, String> sysFuncJsonCache = new DistributedCache<>("URC-test-1", 3, TimeUnit.SECONDS);// , 100, TimeUnit.DAYS);
		sysFuncJsonCache.put("py", "abc");
		for (int i = 0; i < 5000; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(String.format("---------------%s %s", StringUtility.getDateTime_yyyyMMddHHmmssSSS(new Date()), sysFuncJsonCache.get("py")));
		}
	}
}

package com.yks.demo;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import com.yks.distributed.cache.constant.Constant;
import com.yks.distributed.cache.utils.SpringUtils;

/**
 * Hello world!
 *
 */
public class AppCacheServer {
	public static void main(String[] args) {

		IgniteConfiguration igniteConfig = SpringUtils.getBean(Constant.ConfigFile.DEFAULT_CONFIG, Constant.Cache.IGNITE_CONFIGURATION, IgniteConfiguration.class);
		igniteConfig.setClientMode(false);
		Ignition.start("ignite-server-config.xml");

	}
}

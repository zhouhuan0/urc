package com.yks.urc.mq;

import com.yks.mq.client.MQConsumerClient;
import com.yks.mq.client.MQConsumerClient.MessageCallBack;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class TestKafkaConsumerClient
{
	public static void main(String[] args)
	{
		MQConsumerClient client = new MQConsumerClient("default-group");
		
		final AtomicInteger counter = new AtomicInteger(1);
		
		Set<Long> orderIdSet = new HashSet<Long>();

		client.subscribe("test-topic", new MessageCallBack()
		{
			@Override
			public void call(String topic, String message)
			{
					System.out.println("consumer message ---->" + message);
			}
		});
		
		System.out.println("end !");
	}
}

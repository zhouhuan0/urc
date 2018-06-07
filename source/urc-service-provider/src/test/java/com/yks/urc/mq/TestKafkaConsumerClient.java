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
			/*	if(message.contains("90747021842255") || message.contains("701333936082512") ||
						message.contains("701349151012512") || message.contains("90630921506406") ||
						message.contains("505596812159227") ||
						message.contains("701405017436331") || message.contains("90865022521353"))
				{*/
					System.out.println("---->" + message);
					//orderIdSet.add(Long.parseLong(PatternUtils.getValueByPattern("order_id_pattern", message)));
					//System.out.println("records: " + counter.incrementAndGet() + ", orderId: " + orderIdSet.size());
//				}
			}
		});
		
		System.out.println("end !");
	}
}

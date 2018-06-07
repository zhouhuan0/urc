package com.yks.urc.mq;

import com.yks.mq.client.MQProducerClient;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.ArrayList;
import java.util.List;

public class TestKafkaProducerClient
{
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();

		MQProducerClient mqProducerClient = new MQProducerClient();

		//正常订单
		//File file = new File("E:\\source_order1.txt");
		//负利润
		//File file = new File("D:\\source_order_profit.txt");
		//File file = new File("E:\\detail.json");
		//String response = FileUtils.readBytesToString(file);

		List<String> messages = new ArrayList<String>();

		messages.add("hello, kafka");

		mqProducerClient.pubish("test-topic", messages, new MQProducerClient.MessageCallBack()
		{
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception)
			{
				if(exception == null)
				{
					System.out.println("publish msg succeed!");
				}
				else
				{
					exception.printStackTrace();
				}
			}
		});

		long end = System.currentTimeMillis();

		System.out.println("Cost: [" + String.valueOf(end - start) + "]");
	}
}

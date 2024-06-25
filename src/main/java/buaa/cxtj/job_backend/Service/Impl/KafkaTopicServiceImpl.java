package buaa.cxtj.job_backend.Service.Impl;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewPartitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaTopicServiceImpl {

    @Autowired
    private AdminClient adminClient;

    public void addPartitions(String topicName, int numPartitions) throws ExecutionException, InterruptedException {
        NewPartitions newPartitions = NewPartitions.increaseTo(numPartitions);
        adminClient.createPartitions(Collections.singletonMap(topicName, newPartitions)).all().get();
    }
}


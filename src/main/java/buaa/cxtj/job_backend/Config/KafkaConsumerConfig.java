package buaa.cxtj.job_backend.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;


@Configuration
@EnableKafka
public class KafkaConsumerConfig {

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaManualAckListenerContainerFactory(ConsumerFactory<Integer, String> consumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        // 设置提交偏移量的方式 当Acknowledgment.acknowledge()侦听器调用该方法时，立即提交偏移量
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        return factory;
//    }
}


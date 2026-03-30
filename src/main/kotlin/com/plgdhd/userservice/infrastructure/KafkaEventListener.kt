package com.plgdhd.userservice.infrastructure

import com.google.protobuf.InvalidProtocolBufferException
import com.plgdhd.auth.event.proto.UserRegisteredEvent
import com.plgdhd.userservice.config.AppProperties
import com.plgdhd.userservice.exception.UserNotFoundException
import com.plgdhd.userservice.model.User
import com.plgdhd.userservice.repository.UserRepository
import com.plgdhd.userservice.service.UserService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class KafkaEventListener(
    private val appProperties: AppProperties,
    private val userService: UserService
) {

    private val log = LoggerFactory.getLogger(KafkaEventListener::class.java)

    @KafkaListener(
        topics = ["\${app.kafka.topics.user-registered}"],
        groupId = "user-service",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun onUserRegisteredEvent(record: ConsumerRecord<String, ByteArray>, ack: Acknowledgment) {

        val userId = record.key();

        try{

            val event = UserRegisteredEvent.parseFrom(record.value())

            log.info(
                "Получен ивент user.registered: userId={}, username={}, partition={}, offset={}",
                event.userId, event.username, record.partition(), record.offset()
            )

            userService.createUser(event, appProperties.kafka.topics.userRegistered)

            ack.acknowledge()
        }
        catch(ex: Exception){

            log.error(
                "Ошибка обработки user.registered: userId={}, error={}",
                userId, ex.message, ex)
        }
    }

    //TODO дописать в будущем другие ивенты для бана и смены роли

}
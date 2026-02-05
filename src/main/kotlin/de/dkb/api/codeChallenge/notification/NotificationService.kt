package de.dkb.api.codeChallenge.notification

import de.dkb.api.codeChallenge.notification.model.*
import org.springframework.stereotype.Service

@Service
class NotificationService(private val userRepository: UserRepository) {

    fun registerUser(user: User) = userRepository.save(user)

    fun sendNotification(notificationDto: NotificationDto): Boolean {
        val categoryNotification = NotificationCategory.fromNotificationType(notificationDto.notificationType)
        val userOptional = userRepository.findById(notificationDto.userId)
            .filter { it.category.contains(categoryNotification?.name) }

        userOptional.ifPresent { // Logic to send notification to user
            println(
                "Sending notification of type ${notificationDto.notificationType}" +
                        " to user ``````${it.id}: ${notificationDto.message}"
            )
        }
        return userOptional.isPresent
    }
}
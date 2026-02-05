package de.dkb.api.codeChallenge

import de.dkb.api.codeChallenge.notification.NotificationService
import de.dkb.api.codeChallenge.notification.model.NotificationDto
import de.dkb.api.codeChallenge.notification.model.NotificationType
import de.dkb.api.codeChallenge.notification.model.User
import de.dkb.api.codeChallenge.notification.model.UserRepository
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@Testcontainers
@SpringBootTest
class CodeChallengeApplicationTests {

	@Autowired
	private lateinit var userRepository: UserRepository

	@Autowired
	lateinit var notificationService: NotificationService

	@Test
	fun newUserWithNotificationType6GetsNotificationType6(){
		val newUser = User(UUID.randomUUID(),mutableSetOf(NotificationType.type6))
		userRepository.save(newUser)
		val notificationDto = NotificationDto(
			userId = newUser.id,
			notificationType = NotificationType.type6,
			message = "Send test notification"
		)
		assertTrue(notificationService.sendNotification(notificationDto))
	}

	@Test
	fun userSubscribedClassAGetsNewNotificationType6() {
		val userClassA = user(NotificationType.type1,NotificationType.type4,NotificationType.type5)

		userClassA?.let {
			val notificationDto = NotificationDto(
				userId = userClassA.id,
				notificationType = NotificationType.type1,
				message = "Send test notification"
			)
			assertTrue(notificationService.sendNotification(notificationDto))
		}
	}

	@Test
	fun userSubscribedClassADoesntGetNewNotificationType5() {
		val userClassA = user(NotificationType.type1)

		userClassA?.let {
			val notificationDto = NotificationDto(
				userId = userClassA.id,
				notificationType = NotificationType.type5,
				message = "Send test notification"
			)
			assertFalse(notificationService.sendNotification(notificationDto))
		}
	}

	@Test
	fun userSubscribedClassADoesntGetNewNotificationType6() {
		val userClassB = user(NotificationType.type5,NotificationType.type1,NotificationType.type2,NotificationType.type3,NotificationType.type6)

		userClassB?.let {
			val notificationDto = NotificationDto(
				userId = userClassB.id,
				notificationType = NotificationType.type6,
				message = "Send test notification"
			)
			assertFalse(notificationService.sendNotification(notificationDto))
		}
	}

	@Test
	fun userSubscribedClassABGetsNewNotificationType6() {
		val userClassAB = userAll(NotificationType.type1,NotificationType.type5)

		userClassAB?.let {
			val notificationDtoA = NotificationDto(
				userId = userClassAB.id,
				notificationType = NotificationType.type6,
				message = "Send test notification"
			)

			val notificationDtoB = NotificationDto(
				userId = userClassAB.id,
				notificationType = NotificationType.type6,
				message = "Send test notification"
			)

			assertTrue(notificationService.sendNotification(notificationDtoA))
			assertTrue(notificationService.sendNotification(notificationDtoB))
		}
	}

	private fun userAll(vararg notificationTypes:NotificationType): User? {
		val user = userRepository.findAll().firstOrNull {
				userAux ->
			notificationTypes.all { it in userAux.notifications }
		}
		return user
	}

	private fun user(notificationType:NotificationType,vararg notNotificationTypes: NotificationType): User? {
		val user = userRepository.findAll().firstOrNull {
				userAux ->
			userAux.notifications.contains(notificationType) &&
					notNotificationTypes.none { it in userAux.notifications }
		}
		return user
	}
}

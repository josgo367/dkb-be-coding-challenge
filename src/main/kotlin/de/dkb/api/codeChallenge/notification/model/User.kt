package de.dkb.api.codeChallenge.notification.model

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID,
    @field:NotNull
    @Convert(converter = NotificationTypeSetConverter::class)
    var notifications: MutableSet<NotificationType> = mutableSetOf(),
) {
    @Transient
    var category: MutableSet<String> = mutableSetOf()
        get() = calculateCategory()
        private set

    private fun calculateCategory(): MutableSet<String> {
        return notifications
            .mapNotNull { NotificationCategory.fromNotificationType(it) }
            .mapTo(mutableSetOf()) { it.name }
    }

    // Default constructor for Hibernate
    constructor() : this(UUID.randomUUID(), mutableSetOf())
}
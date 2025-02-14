package social.friendship.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import social.common.ddd.Entity
import social.common.ddd.Factory
import social.common.ddd.ID

/**
 * Class to represent a user.
 * @param userId the user ID
 */
class User private constructor(
    @JsonProperty("userId") val userId: UserID
) : Entity<User.UserID>(userId) {

    /**
     * Data class to represent the user ID.
     * @param value the value of the user ID
     */
    data class UserID @JsonCreator constructor(
        @JsonProperty("value") val value: String
    ) : ID<String>(value)

    /**
     * Factory companion object to create a user.
     */
    companion object : Factory<User> {
        /**
         * Creates a user with the given user ID.
         * @param userID the user ID
         * @return the user
         */
        fun of(userID: String): User = User(UserID(userID))

        /**
         * Creates a user with the given user ID.
         * @param userID the user ID
         * @return the user
         */
        fun of(userID: UserID): User = User(userID)
    }
}

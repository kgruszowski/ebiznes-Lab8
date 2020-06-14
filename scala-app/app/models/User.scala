package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import play.api.libs.json.Json

case class User(id: Option[Int],
                userId: UUID,
                name: String,
                email: String) extends Identity

object User {
  implicit val userFormat = Json.format[Cart]

  def mapperTo(
                id: Option[Int],
                userId: UUID,
                name: String,
                email: String) = apply(
    id,
    userId,
    name,
    email)

  def mapperFrom(user: User) = Some((
    user.id,
    user.userId,
    user.name,
    user.email))

  def apply(profile: CommonSocialProfile): User = User(
    id = None,
    userId = UUID.randomUUID(),
    name = profile.fullName.getOrElse(profile.firstName.get + " " + profile.lastName.get),
    email = profile.email.get
  )
}


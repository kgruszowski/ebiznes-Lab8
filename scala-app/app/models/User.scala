package models

import com.mohiva.play.silhouette.api.Identity
import play.api.libs.json.Json

case class User(id: Int,
                firstName: Option[String],
                lastName: Option[String],
                email: Option[String],
                providerID: String,
                providerKey: String) extends Identity

object User {
  implicit val userFormat = Json.format[Cart]
}

package models

import play.api.libs.json.Json

case class Cart(id: Long, customer: Long, enabled: Boolean)

object Cart {
  implicit val cartFormat = Json.format[Cart]
}
package models

import play.api.libs.json.Json

case class Order(id: Long, cart: Long, discount: Option[Long], shippingMethod: Long)

object Order {
  implicit val orderFormat = Json.format[Order]
}

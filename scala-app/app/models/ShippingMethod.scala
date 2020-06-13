package models

import play.api.libs.json.Json

case class ShippingMethod(id: Long, name: String, deliveryTime: String, price: Float)

object ShippingMethod {
  implicit val shippingMethodFormat = Json.format[ShippingMethod]
}

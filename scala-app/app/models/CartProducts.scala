package models

import play.api.libs.json.Json

case class CartProducts(cart: Long, product: Long, quantity: Int)

object CartProducts {
  implicit val cartProductsFormat = Json.format[CartProducts]
}

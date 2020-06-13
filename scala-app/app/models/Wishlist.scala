package models

import play.api.libs.json.Json

case class Wishlist(id: Long, customer: Long, product: Long)

object Wishlist {
  implicit val wishlistFormat = Json.format[Wishlist]
}

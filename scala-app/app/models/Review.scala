package models

import play.api.libs.json.Json

case class Review(id: Long, rate: Int, comment: String, product: Long)

object Review {
  implicit val reviewFormat = Json.format[Review]
}

package models

import play.api.libs.json.Json

case class Customer(id: Long, firstname: String, surname: String, address: String)

object Customer {
  implicit val customerFormat = Json.format[Customer]
}



package models

import play.api.libs.json.Json

case class Inventory(id: Long, quantity: Int, available: Int, product: Long)

object Inventory{
  implicit val inventoryFormat = Json.format[Inventory]
}


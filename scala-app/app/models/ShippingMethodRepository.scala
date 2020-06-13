package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ShippingMethodRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ShippingMethodTable(tag: Tag) extends Table[ShippingMethod](tag, "shippingMethod") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def deliveryTime = column[String]("deliveryTime")

    def price = column[Float]("price")

    def * = (id, name, deliveryTime, price) <> ((ShippingMethod.apply _).tupled, ShippingMethod.unapply)
  }

  val shippingMethodTable = TableQuery[ShippingMethodTable]

  def list(): Future[Seq[ShippingMethod]] = db.run {
    shippingMethodTable.result
  }

  def getById(id: Long): Future[Option[ShippingMethod]] = db.run {
    shippingMethodTable.filter(_.id === id).result.headOption
  }

  def create(name: String, deliveryTime: String, price: Float): Future[ShippingMethod] = db.run {
    (shippingMethodTable.map(sm => (sm.name, sm.deliveryTime, sm.price))
      returning shippingMethodTable.map(_.id)
      into { case ((name, deliveryTime, price), id) => ShippingMethod(id, name, deliveryTime, price) }
      ) += (name, deliveryTime, price)
  }

  def update(id: Long, newShippingMethod: ShippingMethod): Future[Option[ShippingMethod]] = db.run {
    val shippingMethodToUpdate: ShippingMethod = newShippingMethod.copy(id)
    shippingMethodTable.filter(_.id === id).update(shippingMethodToUpdate).map({
      case 0 => None
      case _ => Some(shippingMethodToUpdate)
    })
  }

  def delete(id: Long): Future[Unit] = db.run(shippingMethodTable.filter(_.id === id).delete).map(_ => ())
}


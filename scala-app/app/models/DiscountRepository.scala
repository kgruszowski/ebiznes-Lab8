package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DiscountRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class DiscountTable(tag: Tag) extends Table[Discount](tag, "discount") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def code = column[String]("code")

    def value = column[Int]("value")

    def * = (id, code, value) <> ((Discount.apply _).tupled, Discount.unapply)
  }

  val discountTable = TableQuery[DiscountTable]

  def list(): Future[Seq[Discount]] = db.run {
    discountTable.result
  }

  def getById(id: Long): Future[Option[Discount]] = db.run {
    discountTable.filter(_.id === id).result.headOption
  }

  def create(code: String, value: Int): Future[Discount] = db.run {
    (discountTable.map(c => (c.code, c.value))
      returning discountTable.map(_.id)
      into { case ((code, value), id) => Discount(id, code, value) }
      ) += (code, value)
  }

  def update(id: Long, newDiscount: Discount): Future[Option[Discount]] = db.run {
    val discountToUpdate: Discount = newDiscount.copy(id)
    discountTable.filter(_.id === id).update(discountToUpdate).map({
      case 0 => None
      case _ => Some(discountToUpdate)
    })
  }

  def delete(id: Long): Future[Unit] = db.run(discountTable.filter(_.id === id).delete).map(_ => ())
}


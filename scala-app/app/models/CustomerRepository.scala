package models

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CustomerRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CustomerTable(tag: Tag) extends Table[Customer](tag, "customer") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def firstname = column[String]("firstname")

    def surname = column[String]("surname")

    def address = column[String]("address")

    def * = (id, firstname, surname, address) <> ((Customer.apply _).tupled, Customer.unapply)
  }

  val customerTable = TableQuery[CustomerTable]

  def list(): Future[Seq[Customer]] = db.run {
    customerTable.result
  }

  def getById(id: Long): Future[Option[Customer]] = db.run {
    customerTable.filter(_.id === id).result.headOption
  }

  def create(firstname: String, surname: String, address: String): Future[Customer] = db.run {
    (customerTable.map(c => (c.firstname, c.surname, c.address))
      returning customerTable.map(_.id)
      into { case ((firstname, surname, address), id) => Customer(id, firstname, surname, address) }
      ) += (firstname, surname, address)
  }

  def update(id: Long, newCustomer: Customer): Future[Option[Customer]] = db.run {
    val customerToUpdate: Customer = newCustomer.copy(id)
    customerTable.filter(_.id === id).update(customerToUpdate).map({
      case 0 => None
      case _ => Some(customerToUpdate)
    })
  }

  def delete(id: Long): Future[Unit] = db.run(customerTable.filter(_.id === id).delete).map(_ => ())
}


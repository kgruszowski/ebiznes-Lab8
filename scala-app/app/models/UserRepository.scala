package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[Option[String]]("firstname")

    def lastName = column[Option[String]]("surname")

    def email = column[Option[String]]("address")

    def providerID = column[String]("address")

    def providerKey = column[String]("address")

    def * = (id, firstName, lastName, email, providerID, providerKey) <> ((User.apply _).tupled, User.unapply)
  }

  val userTable = TableQuery[UserTable]
}
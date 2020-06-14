package models

import java.util.UUID

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
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[UUID]("user_id")

    def name = column[String]("name")

    def email = column[String]("email")

    def * = (id, userId, name, email) <> ((User.mapperTo _).tupled, User.mapperFrom)
  }

  val userTable = TableQuery[UserTable]
}
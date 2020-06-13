package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  val categoryTable = TableQuery[CategoryTable]

  def list(): Future[Seq[Category]] = db.run {
    categoryTable.result
  }

  def getById(id: Long): Future[Option[Category]] = db.run {
    categoryTable.filter(_.id === id).result.headOption
  }

  def create(name: String): Future[Category] = db.run {
    (categoryTable.map(c => (c.name))
      returning categoryTable.map(_.id)
      into { case ((name), id) => Category(id, name) }
      ) += (name)
  }

  def update(id: Long, newCategory: Category): Future[Option[Category]] = db.run {
    val categoryToUpdate: Category = newCategory.copy(id)
    categoryTable.filter(_.id === id).update(categoryToUpdate).map({
      case 0 => None
      case _ => Some(categoryToUpdate)
    })
  }

  def delete(id: Long): Future[Unit] = db.run(categoryTable.filter(_.id === id).delete).map(_ => ())
}


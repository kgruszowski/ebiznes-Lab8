package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, protected val categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[String]("description")

    def photo = column[String]("photo")

    def price = column[Float]("price")

    def category = column[Long]("category")

    def categoryFk = foreignKey("cat_fk", category, categoryTable)(_.id)

    def * = (id, name, description, photo, price, category) <> ((Product.apply _).tupled, Product.unapply)

  }

  import categoryRepository.CategoryTable

  val productTable = TableQuery[ProductTable]
  val categoryTable = TableQuery[CategoryTable]

  def list(): Future[Seq[(Product, Category)]] = db.run {
    productTable.join(categoryTable).on(_.category === _.id).result
  }

  def getById(id: Long): Future[Option[Product]] = db.run {
    productTable.filter(_.id === id).result.headOption
  }

  def create(name: String, description: String, photo: String, price: Float, category: Long): Future[Product] = db.run {
    (productTable.map(p => (p.name, p.description, p.photo, p.price, p.category))
      returning productTable.map(_.id)
      into { case ((name, description, photo, price, category), id) => Product(id, name, description, photo, price, category) }
      ) += (name, description, photo, price, category)
  }

  def update(id: Long, newProduct: Product): Future[Option[Product]] = db.run {
    val productToUpdate: Product = newProduct.copy(id)
    productTable.filter(_.id === id).update(productToUpdate).map({
      case 0 => None
      case _ => Some(productToUpdate)
    })
  }

  def delete(id: Long): Future[Unit] = db.run(productTable.filter(_.id === id).delete).map(_ => ())
}


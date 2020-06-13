package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartProductsRepository @Inject()
(dbConfigProvider: DatabaseConfigProvider, protected val cartRepository: CartRepository, protected val productRepository: ProductRepository)
(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CartProductsTable(tag: Tag) extends Table[CartProducts](tag, "cart_products") {
    def cart = column[Long]("cart")

    def product = column[Long]("product")

    def quantity = column[Int]("quantity")

    def cartFk = foreignKey("cart_fk", cart, cartTable)(_.id)

    def prodFk = foreignKey("prod_fk", product, productTable)(_.id)

    def * = (cart, product, quantity) <> ((CartProducts.apply _).tupled, CartProducts.unapply)
  }

  import cartRepository.CartTable
  import productRepository.ProductTable

  val cartProductsTable = TableQuery[CartProductsTable]
  val cartTable = TableQuery[CartTable]
  val productTable = TableQuery[ProductTable]
}


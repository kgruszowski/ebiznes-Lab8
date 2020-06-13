package models

import scala.util.{Failure, Success}
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartRepository @Inject()
(
  dbConfigProvider: DatabaseConfigProvider,
  protected val customerRepository: CustomerRepository,
  protected val productRepository: ProductRepository
)
(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CartTable(tag: Tag) extends Table[Cart](tag, "cart") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def customer = column[Long]("customer")

    def enabled = column[Boolean]("enabled")

    def customerFk = foreignKey("cust_fk", customer, customerTable)(_.id)

    def * = (id, customer, enabled) <> ((Cart.apply _).tupled, Cart.unapply)
  }

  class CartProductsTable(tag: Tag) extends Table[CartProducts](tag, "cart_products") {
    def cart = column[Long]("cart")

    def product = column[Long]("product")

    def quantity = column[Int]("quantity")

    def cartFk = foreignKey("cart_fk", cart, cartTable)(_.id)

    def prodFk = foreignKey("prod_fk", product, productTable)(_.id)

    def * = (cart, product, quantity) <> ((CartProducts.apply _).tupled, CartProducts.unapply)
  }

  import customerRepository.CustomerTable
  import productRepository.ProductTable

  val cartTable = TableQuery[CartTable]
  val customerTable = TableQuery[CustomerTable]
  lazy val cartProductsTable = TableQuery[CartProductsTable]
  lazy val productTable = TableQuery[ProductTable]

  def list(): Future[Seq[Cart]] = db.run {
    cartTable.result
  }

  def getById(id: Long): Future[Option[Cart]] = db.run {
    cartTable.filter(_.id === id).result.headOption
  }

  def getByCustomerId(id: Long): Future[Option[Cart]] = db.run {
    val query = cartTable filter { c => c.customer === id && c.enabled === true}
    query.result.headOption
  }

  def getCartProducts(cartId: Long): Future[Seq[Long]] =
    db.run {
      cartProductsTable.filter(_.cart === cartId).sortBy(_.product).map(_.product).result
    }

  def create(customer: Long): Future[Cart] = db.run{
    (cartTable.map(c => (c.customer, c.enabled))
      returning cartTable.map(_.id)
      into { case ((customer, enabled), id) => Cart(id, customer, enabled) }
      ) += (customer, true)
  }

  def createCartProducts(id: Long, products: Seq[String]): Unit = {
      for (product <- products) {
        db.run(cartProductsTable.map(cp => (cp.cart, cp.product, cp.quantity)) += (id, product.toLong, 1))
      }
  }

  def update(id: Long, newProducts: Seq[String]): Future[Unit] = {
    db.run(cartProductsTable.filter(_.cart === id).delete).onComplete {
      case Success(_) => {
        for (product <- newProducts) {
          db.run(cartProductsTable.map(cp => (cp.cart, cp.product, cp.quantity)) += (id, product.toLong, 1))
        }
      }
    }

    Future{Unit}
  }

  def disableCart(id: Long): Future[Option[Long]] = db.run{
    cartTable.filter(_.id === id).map(c => (c.enabled)).update(false).map({
      case 0 => None
      case _ => Some(id)
    })
  }

  def deleteCartProducts(id: Long ): Future[Int] = db.run{
    cartProductsTable.filter(_.cart === id).delete
  }

  def delete(id: Long ): Future[Int] = db.run{
    cartTable.filter(_.id === id).delete
  }

}


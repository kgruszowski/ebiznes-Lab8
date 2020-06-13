package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WishlistRepository @Inject()
(dbConfigProvider: DatabaseConfigProvider, protected val productRepository: ProductRepository, protected val customerRepository: CustomerRepository)
(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class WishlistTable(tag: Tag) extends Table[Wishlist](tag, "wishlist") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def customer = column[Long]("customer")

    def product = column[Long]("product")

    def customerFk = foreignKey("cust_fk", customer, customerTable)(_.id)

    def productFk = foreignKey("prod_fk", product, productTable)(_.id)

    def * = (id, customer, product) <> ((Wishlist.apply _).tupled, Wishlist.unapply)
  }

  import productRepository.ProductTable
  import customerRepository.CustomerTable

  val wishlistTable = TableQuery[WishlistTable]
  val productTable = TableQuery[ProductTable]
  val customerTable = TableQuery[CustomerTable]

  def list(): Future[Seq[Wishlist]] = db.run {
    wishlistTable.result
  }

  def getByCustomer(id: Long): Future[Seq[(Wishlist, Product)]] = db.run {
    val joinQuery = for {
      (wishlist, product) <- wishlistTable join productTable on (_.product === _.id)
    } yield (wishlist, product)

    joinQuery.result
//    wishlistTable.join(productTable).on(_.product === _.id).filter(_.1.customer === id).result
  }

  def getById(id: Long): Future[Option[Wishlist]] = db.run {
    wishlistTable.filter(_.id === id).result.headOption
  }

  def create(product: Long, customer: Long): Future[Wishlist] = db.run {
    (wishlistTable.map(r => (r.customer, r.product))
      returning wishlistTable.map(_.id)
      into { case ((customer, product), id) => Wishlist(id, customer, product) }
      ) += (product, customer)
  }

  def update(id: Long, newWishlist: Wishlist): Future[Option[Wishlist]] = db.run {
    val wishlistToUpdate: Wishlist = newWishlist.copy(id)
    wishlistTable.filter(_.id === id).update(wishlistToUpdate).map({
      case 0 => None
      case _ => Some(wishlistToUpdate)
    })
  }

  def delete(id: Long): Future[Unit] = db.run(wishlistTable.filter(_.id === id).delete).map(_ => ())
}


package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()
(
  dbConfigProvider: DatabaseConfigProvider,
  protected val cartRepository: CartRepository,
  protected val shippingMethodRepository: ShippingMethodRepository,
  protected val discountRepository: DiscountRepository
)
(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def cart = column[Long]("cart")

    def shippingMethod = column[Long]("shippingMethod")

    def discount = column[Option[Long]]("discount")

    def cartFk = foreignKey("cart_fk", cart, cartTable)(_.id)

    def discountFk = foreignKey("disc_fk", discount, discountTable)(_.id)

    def shippingMethodFk = foreignKey("sm_fk", shippingMethod, shippingMethodTable)(_.id)

    def * = (id, cart, discount, shippingMethod) <> ((Order.apply _).tupled, Order.unapply)

  }

  import cartRepository.CartTable
  import shippingMethodRepository.ShippingMethodTable
  import discountRepository.DiscountTable

  val orderTable = TableQuery[OrderTable]
  val cartTable = TableQuery[CartTable]
  val shippingMethodTable = TableQuery[ShippingMethodTable]
  val discountTable = TableQuery[DiscountTable]

  def list(): Future[Seq[(Order, Cart)]] = db.run {
    orderTable.join(cartTable).on(_.cart === _.id).result
  }

  def listByCustomer(customerId: Long): Future[Seq[(Order, Cart)]] = db.run {
    val joinQuery = for {
      (order, cart) <- orderTable join cartTable.filter(_.customer === customerId) on (_.cart === _.id)
    } yield (order, cart)

    joinQuery.result
  }

  def getById(id: Long): Future[Option[(Order, ShippingMethod)]] = db.run {

    val joinQuery = for {
      (order, shippingMethod) <- orderTable.filter(_.id === id) join shippingMethodTable on (_.shippingMethod === _.id)
    } yield (order, shippingMethod)

    joinQuery.result.headOption
  }

  def getByIdForApi(id: Long): Future[Option[(Order, ShippingMethod, Option[Discount])]] = db.run {

    val joinQuery = for {
      (order, shippingMethod) <- orderTable.filter(_.id === id) join shippingMethodTable on (_.shippingMethod === _.id)
      (_, discount) <- orderTable.filter(_.id === id) joinLeft  discountTable on (_.discount === _.id)
    } yield (order, shippingMethod, discount)

    joinQuery.result.headOption
  }

  def create(cart: Long, discount: Option[Long], shipping: Long): Future[Order] = db.run {
    (orderTable.map(o => (o.cart, o.discount, o.shippingMethod))
      returning orderTable.map(_.id)
      into { case ((cart, discount, shippingMethod), id) => Order(id, cart, discount, shippingMethod) }
      ) += (cart, discount, shipping)
  }

  def delete(id: Long): Future[Unit] = db.run(orderTable.filter(_.id === id).delete).map(_ => ())
}


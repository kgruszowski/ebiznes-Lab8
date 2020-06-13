package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class InventoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, protected val productRepository: ProductRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class InventoryTable(tag: Tag) extends Table[Inventory](tag, "inventory") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def quantity = column[Int]("quantity")

    def available = column[Int]("available")

    def product = column[Long]("product")

    def productFk = foreignKey("prod_fk", product, productTable)(_.id)

    def * = (id, quantity, available, product) <> ((Inventory.apply _).tupled, Inventory.unapply)

  }

  import productRepository.ProductTable

  private val inventoryTable = TableQuery[InventoryTable]
  private val productTable = TableQuery[ProductTable]

  def list(): Future[Seq[(Inventory, Product)]] = db.run {
    inventoryTable.join(productTable).on(_.product === _.id).result
  }

  def getById(id: Long): Future[Option[Inventory]] = db.run {
    inventoryTable.filter(_.id === id).result.headOption
  }

  def getByProductId(id: Long): Future[Option[Inventory]] = db.run {
    inventoryTable.filter(_.product === id).result.headOption
  }

  def create(quantity: Int, available: Int, product: Long): Future[Inventory] = db.run {
    (inventoryTable.map(i => (i.quantity, i.available, i.product))
      returning inventoryTable.map(_.id)
      into { case ((quantity, available, product), id) => Inventory(id, quantity, available, product) }
      ) += (quantity, available, product)
  }

  def update(id: Long, newInventory: Inventory): Future[Option[Inventory]] = db.run {
    val inventoryToUpdate: Inventory = newInventory.copy(id)
    inventoryTable.filter(_.id === id).update(inventoryToUpdate).map({
      case 0 => None
      case _ => Some(inventoryToUpdate)
    })
  }

  def delete(id: Long): Future[Unit] = db.run(inventoryTable.filter(_.id === id).delete).map(_ => ())
}


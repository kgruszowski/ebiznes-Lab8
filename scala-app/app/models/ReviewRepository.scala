package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, protected val productRepository: ProductRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ReviewTable(tag: Tag) extends Table[Review](tag, "review") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def rate = column[Int]("rate")

    def comment = column[String]("comment")

    def product = column[Long]("product")

    def productFk = foreignKey("prod_fk", product, productTable)(_.id)

    def * = (id, rate, comment, product) <> ((Review.apply _).tupled, Review.unapply)

  }

  import productRepository.ProductTable

  val reviewTable = TableQuery[ReviewTable]
  val productTable = TableQuery[ProductTable]

  def list(): Future[Seq[Review]] = db.run {
    reviewTable.result
  }

  def listByProductId(productId: Long): Future[Seq[Review]] = db.run {
    reviewTable.filter(_.product === productId).result
  }

  def getById(id: Long): Future[Option[Review]] = db.run {
    reviewTable.filter(_.id === id).result.headOption
  }

  def create(rate: Int, comment: String, product: Long): Future[Review] = db.run {
    (reviewTable.map(r => (r.rate, r.comment, r.product))
      returning reviewTable.map(_.id)
      into { case ((rate, comment, product), id) => Review(id, rate, comment, product) }
      ) += (rate, comment, product)
  }

  def update(id: Long, newReview: Review): Future[Option[Review]] = db.run {
    val reviewToUpdate: Review = newReview.copy(id)
    reviewTable.filter(_.id === id).update(reviewToUpdate).map({
      case 0 => None
      case _ => Some(reviewToUpdate)
    })
  }

  def delete(id: Long): Future[Unit] = db.run(reviewTable.filter(_.id === id).delete).map(_ => ())
}


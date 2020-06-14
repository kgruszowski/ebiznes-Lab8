package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import models.{Category, Product, ProductRepository, Review, ReviewRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import utils.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's review page.
 */
@Singleton
class ReviewController @Inject()(reviewRepository: ReviewRepository, productRepository: ProductRepository, silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)
                                (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val reviewForm: Form[CreateReviewForm] = Form {
    mapping(
      "rate" -> number,
      "comment" -> nonEmptyText,
      "product" -> longNumber

    )(CreateReviewForm.apply)(CreateReviewForm.unapply)
  }

  val updateReviewForm: Form[UpdateReviewForm] = Form {
    mapping(
      "id" -> longNumber,
      "rate" -> number,
      "comment" -> nonEmptyText,
      "product" -> longNumber
    )(UpdateReviewForm.apply)(UpdateReviewForm.unapply)
  }

  def list = Action.async { implicit request =>
    val reviewList = reviewRepository.list()
    reviewList.map(reviews => Ok(views.html.review.list(reviews)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    if (request.method == "POST") {
      var products: Seq[(Product, Category)] = Seq[(Product, Category)]()
      productRepository.list().onComplete {
        case Success(prod) => products = prod
        case Failure(_) => print("fail")
      }

      reviewForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.review.create(errorForm, products))
          )
        },
        r => {
          reviewRepository.create(r.rate, r.comment, r.product).map { _ =>
            Redirect(routes.ReviewController.create()).flashing("success" -> "review.created")
          }
        }
      )

    } else {
      val productList = productRepository.list()
      productList.map(products => Ok(views.html.review.create(reviewForm, products)))
    }
  }

  def details(id: Long) = Action.async { implicit request =>
    val review = reviewRepository.getById(id)

    review.map(review => review match {
      case Some(r) => {
        val form = updateReviewForm.fill(UpdateReviewForm(r.id, r.rate, r.comment, r.product))
        Ok(views.html.review.details(r, form))
      }
      case None => Redirect(routes.HomeController.index())
    })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    updateReviewForm.bindFromRequest.fold(
      errorForm => {
        val review = reviewRepository.getById(id)
        review.map(review => review match {
          case Some(r) => {
            BadRequest(views.html.review.details(r, errorForm))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      r => {
        reviewRepository.update(r.id, Review(r.id, r.rate, r.comment, r.product)).map { _ =>
          Redirect(routes.ReviewController.details(r.id)).flashing("success" -> "review updated")
        }
      }
    )
  }

  def apiList = silhouette.SecuredAction.async { implicit request =>
    val reviewList = reviewRepository.list()
    reviewList.map(list => Ok(Json.toJson(list)))
  }

  def apiListByProduct(productId: Long) = silhouette.SecuredAction.async { implicit request =>
    val reviewList = reviewRepository.listByProductId(productId)
    reviewList.map(list => Ok(Json.toJson(list)))
  }

  def apiGet(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val review = reviewRepository.getById(id)
    review.map(review => review match {
      case Some(i) => Ok(Json.obj("review" -> Json.toJson(i)))
      case None => NotFound
    })
  }

  def apiAdd = silhouette.SecuredAction.async { implicit request =>
    val review = request.body.asJson.get.as[Review]
    reviewRepository.create(review.rate, review.comment, review.product).map(newEntity => {
      Ok(Json.obj("review" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val review = request.body.asJson.get.as[Review]
    reviewRepository.update(id, review).map(updatedEntity => updatedEntity match{
      case Some(c) => Ok(Json.obj("review" -> Json.toJson(c)))
      case None => NotFound
    })
  }

  def apiDelete(id: Long)= silhouette.SecuredAction {
    reviewRepository.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }
}

case class CreateReviewForm(rate: Int, comment: String, product: Long)
case class UpdateReviewForm(id: Long, rate: Int, comment: String, product: Long)

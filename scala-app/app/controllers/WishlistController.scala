package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import models.{Category, Customer, CustomerRepository, Product, ProductRepository, Wishlist, WishlistRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import utils.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's whishlist page.
 */
@Singleton
class WishlistController @Inject()
  (wishlistRepository: WishlistRepository, productRepository: ProductRepository, customerRepository: CustomerRepository,
   silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)
  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val wishlistForm: Form[CreateWishlistForm] = Form {
    mapping(
      "customer" -> longNumber,
      "product" -> longNumber

    )(CreateWishlistForm.apply)(CreateWishlistForm.unapply)
  }

  val updateWishlistForm: Form[UpdateWishlistForm] = Form {
    mapping(
      "id" -> longNumber,
      "customer" -> longNumber,
      "product" -> longNumber
    )(UpdateWishlistForm.apply)(UpdateWishlistForm.unapply)
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    val wishlistList = wishlistRepository.list()
    wishlistList.map(wishlists => Ok(views.html.wishlist.list(wishlists)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var products: Seq[(Product, Category)] = Seq[(Product, Category)]()
    productRepository.list().onComplete {
      case Success(prod) => products = prod
      case Failure(_) => print("fail")
    }
    var customers: Seq[Customer] = Seq[Customer]()
    customerRepository.list().onComplete {
      case Success(c) => customers = c
      case Failure(_) => print("fail")
    }

    if (request.method == "POST") {
      wishlistForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.wishlist.create(errorForm, products, customers))
          )
        },
        w => {
          wishlistRepository.create(w.product, w.customer).map { _ =>
            Redirect(routes.WishlistController.create()).flashing("success" -> "wishlist.created")
          }
        }
      )

    } else {
      val productList = productRepository.list()
      productList.map(products => Ok(views.html.wishlist.create(wishlistForm, products, customers)))
    }
  }

  def details(id: Long) = Action.async { implicit request =>
    var products: Seq[(Product, Category)] = Seq[(Product, Category)]()
    productRepository.list().onComplete {
      case Success(prod) => products = prod
      case Failure(_) => print("fail")
    }

    val wishlist = wishlistRepository.getById(id)
    wishlist.map(wishlist => wishlist match {
      case Some(w) => {
        val form = updateWishlistForm.fill(UpdateWishlistForm(w.id, w.product, w.customer))
        Ok(views.html.wishlist.details(w, form, products))
      }
      case None => Redirect(routes.HomeController.index())
    })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var products: Seq[(Product, Category)] = Seq[(Product, Category)]()
    productRepository.list().onComplete {
      case Success(prod) => products = prod
      case Failure(_) => print("fail")
    }

    updateWishlistForm.bindFromRequest.fold(
      errorForm => {
        val wishlist = wishlistRepository.getById(id)
        wishlist.map(wishlist => wishlist match {
          case Some(r) => {
            BadRequest(views.html.wishlist.details(r, errorForm, products))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      w => {
        wishlistRepository.update(w.id, Wishlist(w.id, w.product, w.customer)).map { _ =>
          Redirect(routes.WishlistController.details(w.id)).flashing("success" -> "wishlist updated")
        }
      }
    )
  }

  def apiList = silhouette.SecuredAction.async { implicit request =>
    val wishlistList = wishlistRepository.list()
    wishlistList.map(list => Ok(Json.toJson(list)))
  }

  def apiListForCustomer(customerId: Long) = silhouette.SecuredAction.async { implicit request =>
    val wishlistProducts = wishlistRepository.getByCustomer(customerId)
    wishlistProducts.map(list => Ok(Json.toJson(list)))
  }

  def apiGet(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val wishlist = wishlistRepository.getById(id)
    wishlist.map(wishlist => wishlist match {
      case Some(i) => Ok(Json.obj("wishlist" -> Json.toJson(i)))
      case None => NotFound
    })
  }

  def apiAdd = silhouette.SecuredAction.async { implicit request =>
    val wishlist = request.body.asJson.get.as[Wishlist]
    wishlistRepository.create(wishlist.customer, wishlist.product).map(newEntity => {
      Ok(Json.obj("wishlist" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val wishlist = request.body.asJson.get.as[Wishlist]
    wishlistRepository.update(id, wishlist).map(updatedEntity => updatedEntity match{
      case Some(e) => Ok(Json.obj("wishlist" -> Json.toJson(e)))
      case None => NotFound
    })
  }

  def apiDelete(id: Long)= silhouette.SecuredAction {
    wishlistRepository.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }
}

case class CreateWishlistForm(customer: Long, product: Long)
case class UpdateWishlistForm(id: Long, customer: Long, product: Long)

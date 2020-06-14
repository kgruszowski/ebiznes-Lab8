package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import models.{Cart, CartRepository, Category, Customer, CustomerRepository, Product, ProductRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import utils.DefaultEnv

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's cart page.
 */
@Singleton
class CartController @Inject()(cartRepository: CartRepository,
                               customerRepository: CustomerRepository,
                               productRepository: ProductRepository,
                               silhouette: Silhouette[DefaultEnv],
                               cc: MessagesControllerComponents)
                              (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val cartForm: Form[CreateCartForm] = Form {
    mapping(
      "customer" -> longNumber,
      "products" -> list(longNumber)
    )(CreateCartForm.apply)(CreateCartForm.unapply)
  }

  val updateCartForm: Form[UpdateCartForm] = Form {
    mapping(
      "id" -> longNumber,
      "customer" -> longNumber,
      "products" -> list(longNumber)
    )(UpdateCartForm.apply)(UpdateCartForm.unapply)
  }

  def listAction = Action.async { implicit request =>
    val carts = cartRepository.list()
    carts.map(carts => Ok(views.html.cart.list(carts)))
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
      cartForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.cart.create(errorForm, products, customers))
          )
        },
        c => {
          val productIdsList: Seq[String] = request.body.asFormUrlEncoded.get("products")
          cartRepository.create(c.customer).map(newEntity => {
            cartRepository.createCartProducts(newEntity.id, productIdsList)
            Redirect(routes.CartController.create()).flashing("success" -> "cart.created")
          })
        }
      )

    } else {
      val productList = productRepository.list()
      productList.map(products => Ok(views.html.cart.create(cartForm, products, customers)))
    }
  }

  def details(id: Long) = Action.async { implicit request =>
    var products: Seq[(Product, Category)] = Seq[(Product, Category)]()
    productRepository.list().onComplete {
      case Success(prod) => products = prod
      case Failure(_) => print("fail")
    }

    val cart = cartRepository.getById(id)
    cart.map(cart => cart match {
      case Some(c) => {
        var cartProducts: List[Long] = List[Long]()
        val f = cartRepository.getCartProducts(id)

        Try(Await.result(f, Duration.Inf)) match {
          case Success(productList) => cartProducts = productList.toList
        }

        val form = updateCartForm.fill(UpdateCartForm(c.id, c.customer, cartProducts))
        Ok(views.html.cart.details(c, form, products, cartProducts))
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

    var cartProducts: List[Long] = List[Long]()
    val f = cartRepository.getCartProducts(id)

    Try(Await.result(f, Duration.Inf)) match {
      case Success(productList) => cartProducts = productList.toList
    }

    updateCartForm.bindFromRequest.fold(
      errorForm => {
        val cart = cartRepository.getById(id)
        cart.map(cart => cart match {
          case Some(c) => {
            BadRequest(views.html.cart.details(c, errorForm, products, cartProducts))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      sm => {
        val productIdsList = request.body.asFormUrlEncoded.get("products")
        cartRepository.update(sm.id, productIdsList).map { _ =>
          Redirect(routes.CartController.details(sm.id)).flashing("success" -> "cart updated")
        }
      }
    )
  }

  def apiList = silhouette.SecuredAction.async { implicit request =>
    val cartList = cartRepository.list()
    cartList.map(list => Ok(Json.toJson(list)))
  }

  def apiGet(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val cart = cartRepository.getById(id)
    cart.map(cart => cart match {
      case Some(i) => {
        var cartProducts: List[Long] = List[Long]()
        val f = cartRepository.getCartProducts(id)

        Try(Await.result(f, Duration.Inf)) match {
          case Success(productList) => cartProducts = productList.toList
        }

        Ok(Json.obj("cart" -> Json.toJson(i), "products" -> Json.toJson(cartProducts)))
      }
      case None => NotFound
    })
  }

  def apiGetForCustomer(customerId: Long) = silhouette.SecuredAction.async { implicit request =>
    val cart = cartRepository.getByCustomerId(customerId)
    cart.map(cart => cart match {
      case Some(c) => {
        var cartProducts: List[Long] = List[Long]()
        val f = cartRepository.getCartProducts(c.id)

        Try(Await.result(f, Duration.Inf)) match {
          case Success(productList) => cartProducts = productList.toList
        }

        Ok(Json.obj("cart" -> Json.toJson(c), "products" -> Json.toJson(cartProducts)))
      }
      case None => NotFound
    })
  }

    def apiAdd = silhouette.SecuredAction.async { implicit request =>
      val cart = request.body.asJson.get("cart").as[Cart]
      val productIdsList = request.body.asJson.get("products").as[Seq[Long]]

      cartRepository.create(cart.customer).map(newEntity => {
        cartRepository.createCartProducts(newEntity.id, productIdsList.map(_.toString))
        Ok(Json.obj("cart" -> Json.toJson(newEntity)))
      })
    }

    def apiUpdate(id: Long) = silhouette.SecuredAction.async { implicit request =>
      val productIdsList = request.body.asJson.get("products").as[Seq[Long]]
      cartRepository.update(id, productIdsList.map(_.toString))

      val cart = cartRepository.getById(id)
      cart.map(cart => cart match {
        case Some(i) => Ok(Json.obj("cart" -> Json.toJson(i)))
        case None => NotFound
      })
    }

  def apiDelete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    cartRepository.deleteCartProducts(id).map {_ =>
      cartRepository.delete(id)
      Ok(Json.obj("result" -> "ok"))
    }
  }
}

case class CreateCartForm(customer: Long, products: List[Long])

case class UpdateCartForm(id: Long, customer: Long, products: List[Long])
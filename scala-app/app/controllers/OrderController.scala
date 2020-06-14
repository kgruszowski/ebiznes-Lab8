package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import models.{Cart, CartRepository, Discount, DiscountRepository, Order, OrderRepository, Product, ShippingMethod, ShippingMethodRepository}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import play.api.libs.json.Json
import utils.DefaultEnv


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's order page.
 */
@Singleton
class OrderController @Inject()
(
  orderRepository: OrderRepository,
  cartRepository: CartRepository,
  shippingMethodRepository: ShippingMethodRepository,
  discountRepository: DiscountRepository,
  silhouette: Silhouette[DefaultEnv],
  cc: MessagesControllerComponents
)
(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "cart" -> longNumber,
      "discount" -> optional(longNumber),
      "shippingMethod" -> longNumber,
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  val updateProductForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> longNumber,
      "cart" -> longNumber,
      "discount" -> optional(longNumber),
      "shippingMethod" -> longNumber,
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    val orderList = orderRepository.list()
    orderList.map(orders => Ok(views.html.order.list(orders)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val carts = getCarts()
    val discounts = getDiscounts()
    val shippingMethods = getShippingMethods()

    if (request.method == "POST") {
      orderForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.order.create(errorForm, carts, discounts, shippingMethods))
          )
        },
        o => {
          orderRepository.create(o.cart, o.discount, o.shippingMethod).map { _ =>
            Redirect(routes.OrderController.create()).flashing("success" -> "order.created")
          }
        }
      )

    } else {
      Future{Ok(views.html.order.create(orderForm, carts, discounts, shippingMethods))}
    }
  }

  def details(id: Long) = Action.async { implicit request =>
    val order = orderRepository.getById(id)
    order.map(order => order match {
      case Some(o) => Ok(views.html.order.details(o))
      case None => NotFound
    })
  }

  def update(id: Long)= Action {
    Ok(views.html.index("PUT: /order/" + id))
  }

  def delete(id: Long)= Action {
    Ok(views.html.index("DELETE: /order/" + id))
  }

  def apiList = silhouette.SecuredAction.async { implicit request =>
    val orderList = orderRepository.list()
    orderList.map(orders => Ok(Json.toJson(orders)))
  }

  def apiListByCustomer(customerId: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val orderList = orderRepository.listByCustomer(customerId)
    orderList.map(orders => Ok(Json.toJson(orders)))
  }

  def apiGet(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val orders = orderRepository.getByIdForApi(id)
    orders.map(order => order match {
      case Some(c) => Ok(Json.obj("order" -> Json.toJson(c)))
      case None => NotFound
    })
  }

  def apiAdd = silhouette.SecuredAction.async { implicit request =>
    val order = request.body.asJson.get.as[Order]
    orderRepository.create(order.cart, order.discount, order.shippingMethod).map(newEntity => {
      val disableCartAction = cartRepository.disableCart(newEntity.cart)

      Await.result(disableCartAction, Duration.Inf)

      Ok(Json.obj("order" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = silhouette.SecuredAction { implicit request =>
    MethodNotAllowed
  }

  def apiDelete(id: Long) = silhouette.SecuredAction {
    orderRepository.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }

  def getCarts(): Seq[Cart] = {
    var carts: Seq[Cart] = Seq[Cart]()
    Try(Await.result(cartRepository.list(), Duration.Inf)) match {
      case Success(c) => carts = c
    }

    carts
  }

  def getDiscounts(): Seq[Discount] = {
    var discounts: Seq[Discount] = Seq[Discount]()
    Try(Await.result(discountRepository.list(), Duration.Inf)) match {
      case Success(d) => discounts = d
    }

    discounts
  }

  def getShippingMethods(): Seq[ShippingMethod] = {
    var shipping: Seq[ShippingMethod] = Seq[ShippingMethod]()
    Try(Await.result(shippingMethodRepository.list(), Duration.Inf)) match {
      case Success(s) => shipping = s
    }

    shipping
  }
}

case class CreateOrderForm(cart: Long, discount: Option[Long], shippingMethod: Long)
case class UpdateOrderForm(id: Long, cart: Long, discount: Option[Long], shippingMethod: Long)

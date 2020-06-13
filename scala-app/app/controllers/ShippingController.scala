package controllers

import javax.inject._
import models.{ShippingMethod, ShippingMethodRepository}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.data.format.Formats._
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's shipping method page.
 */
@Singleton
class ShippingController @Inject()(shippingMethodRepo: ShippingMethodRepository, cc: MessagesControllerComponents)
                                  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val shippingForm: Form[CreateShippingForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "deliveryTime" -> nonEmptyText,
      "price" -> of[Float],
    )(CreateShippingForm.apply)(CreateShippingForm.unapply)
  }

  val updateShippingForm: Form[UpdateShippingForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "deliveryTime" -> nonEmptyText,
      "price" -> of[Float],
    )(UpdateShippingForm.apply)(UpdateShippingForm.unapply)
  }

  def list = Action.async { implicit request =>
    val shippingMethodList = shippingMethodRepo.list()
    shippingMethodList.map(shippingMethods => Ok(views.html.shippingMethod.list(shippingMethods)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    if (request.method == "POST") {
      shippingForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.shippingMethod.create(errorForm))
          )
        },
        sp => {
          shippingMethodRepo.create(sp.name, sp.deliveryTime, sp.price).map { _ =>
            Redirect(routes.ShippingController.create()).flashing("success" -> "shipping.created")
          }
        }
      )

    } else {
      Future(Ok(views.html.shippingMethod.create(shippingForm)))
    }
  }

  def details(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val shippingMethod = shippingMethodRepo.getById(id)
    shippingMethod.map(shippingMethod => shippingMethod match {
      case Some(sm) => {
        val form = updateShippingForm.fill(UpdateShippingForm(sm.id, sm.name, sm.deliveryTime, sm.price))
        Ok(views.html.shippingMethod.details(sm, form))
      }
      case None => Redirect(routes.HomeController.index())
    })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    updateShippingForm.bindFromRequest.fold(
      errorForm => {
        val shippingMethod = shippingMethodRepo.getById(id)
        shippingMethod.map(shippingMethod => shippingMethod match {
          case Some(c) => {
            BadRequest(views.html.shippingMethod.details(c, errorForm))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      sm => {
        shippingMethodRepo.update(sm.id, ShippingMethod(sm.id, sm.name, sm.deliveryTime, sm.price)).map { _ =>
          Redirect(routes.ShippingController.details(sm.id)).flashing("success" -> "shippingMethod updated")
        }
      }
    )
  }

  def apiList = Action.async { implicit request =>
    val shippingMethodList = shippingMethodRepo.list()
    shippingMethodList.map(list => Ok(Json.toJson(list)))
  }

  def apiGet(id: Long) = Action.async { implicit request =>
    val shippingMethod = shippingMethodRepo.getById(id)
    shippingMethod.map(shippingMethod => shippingMethod match {
      case Some(i) => Ok(Json.obj("shippingMethod" -> Json.toJson(i)))
      case None => NotFound
    })
  }

  def apiAdd = Action.async { implicit request =>
    val shippingMethod = request.body.asJson.get.as[ShippingMethod]
    shippingMethodRepo.create(shippingMethod.name, shippingMethod.deliveryTime, shippingMethod.price).map(newEntity => {
      Ok(Json.obj("shippingMethod" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = Action.async { implicit request =>
    val shippingMethod = request.body.asJson.get.as[ShippingMethod]
    shippingMethodRepo.update(id, shippingMethod).map(updatedEntity => updatedEntity match{
      case Some(e) => Ok(Json.obj("shippingMethod" -> Json.toJson(e)))
      case None => NotFound
    })
  }

  def apiDelete(id: Long)= Action {
    shippingMethodRepo.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }
}

case class CreateShippingForm(name: String, deliveryTime: String, price: Float)
case class UpdateShippingForm(id: Long, name: String, deliveryTime: String, price: Float)

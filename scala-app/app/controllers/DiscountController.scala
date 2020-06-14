package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import models.{Discount, DiscountRepository}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import utils.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's discount page.
 */
@Singleton
class DiscountController @Inject()(discountRepo: DiscountRepository, silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)
                                  (implicit ec:ExecutionContext) extends MessagesAbstractController(cc) {

  val discountForm: Form[CreateDiscuntForm] = Form {
    mapping(
      "code" -> nonEmptyText,
      "value" -> number,
    )(CreateDiscuntForm.apply)(CreateDiscuntForm.unapply)
  }

  val updateDiscountForm: Form[UpdateDiscuntForm] = Form {
    mapping(
      "id" -> longNumber,
      "code" -> nonEmptyText,
      "value" -> number,
    )(UpdateDiscuntForm.apply)(UpdateDiscuntForm.unapply)
  }

  def list = Action.async { implicit request =>
    val discountList = discountRepo.list()
    discountList.map(discounts => Ok(views.html.discount.list(discounts)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    if (request.method == "POST") {
      discountForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.discount.create(errorForm))
          )
        },
        discount => {
          discountRepo.create(discount.code, discount.value).map { _ =>
            Redirect(routes.DiscountController.create()).flashing("success" -> "discount.created")
          }
        }
      )

    } else {
      Future(Ok(views.html.discount.create(discountForm)))
    }
  }

  def details(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val discount = discountRepo.getById(id)
    discount.map(discount => discount match {
      case Some(d) => {
        val form = updateDiscountForm.fill(UpdateDiscuntForm(d.id, d.code, d.value))
        Ok(views.html.discount.details(d, form))
      }
      case None => Redirect(routes.HomeController.index())
    })
  }
  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    updateDiscountForm.bindFromRequest.fold(
      errorForm => {
        val discount = discountRepo.getById(id)
        discount.map(discount => discount match {
          case Some(c) => {
            BadRequest(views.html.discount.details(c, errorForm))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      d => {
        discountRepo.update(d.id, Discount(d.id, d.code, d.value)).map { _ =>
          Redirect(routes.DiscountController.details(d.id)).flashing("success" -> "discount updated")
        }
      }
    )
  }

  def apiList = silhouette.SecuredAction.async { implicit request =>
    val discountList = discountRepo.list()
    discountList.map(list => Ok(Json.toJson(list)))
  }

  def apiGet(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val discount = discountRepo.getById(id)
    discount.map(discount => discount match {
      case Some(i) => Ok(Json.obj("discount" -> Json.toJson(i)))
      case None => NotFound
    })
  }

  def apiAdd = silhouette.SecuredAction.async { implicit request =>
    val discount = request.body.asJson.get.as[Discount]
    discountRepo.create(discount.code, discount.value).map(newEntity => {
      Ok(Json.obj("discount" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val discount = request.body.asJson.get.as[Discount]
    discountRepo.update(id, discount).map(updatedEntity => updatedEntity match{
      case Some(e) => Ok(Json.obj("discount" -> Json.toJson(e)))
      case None => NotFound
    })
  }

  def apiDelete(id: Long)= silhouette.SecuredAction {
    discountRepo.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }
}

case class CreateDiscuntForm(code: String, value: Int)
case class UpdateDiscuntForm(id: Long, code: String, value: Int)

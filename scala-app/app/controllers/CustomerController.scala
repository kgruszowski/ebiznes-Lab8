package controllers

import javax.inject._
import models.{Customer, CustomerRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json.Json
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's customer page.
 */
@Singleton
class CustomerController @Inject()(customerRepository: CustomerRepository, cc: MessagesControllerComponents)
                                  (implicit ec:ExecutionContext) extends MessagesAbstractController(cc) {

  val customerForm: Form[CreateCustomerForm] = Form {
    mapping(
      "firstname" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "address" -> nonEmptyText,
    )(CreateCustomerForm.apply)(CreateCustomerForm.unapply)
  }

  val updateCustomerForm: Form[UpdateCustomerForm] = Form {
    mapping(
      "id" -> longNumber,
      "firstname" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "address" -> nonEmptyText,
    )(UpdateCustomerForm.apply)(UpdateCustomerForm.unapply)
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    val customerList = customerRepository.list()
    customerList.map(customers => Ok(views.html.customer.list(customers)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    if (request.method == "POST") {
      customerForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.customer.create(errorForm))
          )
        },
        customer => {
          customerRepository.create(customer.firstname, customer.surname, customer.address).map { _ =>
            Redirect(routes.CustomerController.create()).flashing("success" -> "customer.created")
          }
        }
      )

    } else {
      Future(Ok(views.html.customer.create(customerForm)))
    }
  }

  def details(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val customer = customerRepository.getById(id)
    customer.map(customer => customer match {
      case Some(c) => {
        val form = updateCustomerForm.fill(UpdateCustomerForm(c.id, c.firstname, c.surname, c.address))
        Ok(views.html.customer.details(c, form))
      }
      case None => Redirect(routes.HomeController.index())
    })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    updateCustomerForm.bindFromRequest.fold(
      errorForm => {
        val customer = customerRepository.getById(id)
        customer.map(customer => customer match {
          case Some(c) => {
            BadRequest(views.html.customer.details(c, errorForm))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      c => {
        customerRepository.update(c.id, Customer(c.id, c.firstname, c.surname, c.address)).map { _ =>
          Redirect(routes.CustomerController.details(c.id)).flashing("success" -> "customer updated")
        }
      }
    )
  }

  def apiList = Action.async { implicit request =>
    val customerList = customerRepository.list()
    customerList.map(customers => Ok(Json.toJson(customers)))
  }

  def apiGet(id: Long) = Action.async { implicit request =>
    val customer = customerRepository.getById(id)
    customer.map(customer => customer match {
      case Some(c) => Ok(Json.obj("customer" -> Json.toJson(c)))
      case None => NotFound
    })
  }

  def apiAdd = Action.async { implicit request =>
    val customer = request.body.asJson.get.as[Customer]
    customerRepository.create(customer.firstname, customer.surname, customer.address).map(newEntity => {
      Ok(Json.obj("customer" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = Action.async { implicit request =>
    val customer = request.body.asJson.get.as[Customer]
    customerRepository.update(id, customer).map(updatedEntity => updatedEntity match{
      case Some(e) => Ok(Json.obj("customer" -> Json.toJson(e)))
      case None => NotFound
    })
  }

  def apiDelete(id: Long)= Action {
    customerRepository.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }

}

case class CreateCustomerForm(firstname: String, surname: String, address: String)
case class UpdateCustomerForm(id: Long, firstname: String, surname: String, address: String)
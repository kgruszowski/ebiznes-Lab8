package controllers

import javax.inject._
import models.{Category, Inventory, InventoryRepository, Product, ProductRepository}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's inventory page.
 */
@Singleton
class InventoryController @Inject()(inventoryRepo: InventoryRepository, productRepository: ProductRepository, cc: MessagesControllerComponents)
                                   (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val inventoryForm: Form[CreateInventoryForm] = Form {
    mapping(
      "quantity" -> number,
      "available" -> number,
      "product" -> longNumber

    )(CreateInventoryForm.apply)(CreateInventoryForm.unapply)
  }

  val updateInventoryForm: Form[UpdateInventoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "quantity" -> number,
      "available" -> number,
      "product" -> longNumber
    )(UpdateInventoryForm.apply)(UpdateInventoryForm.unapply)
  }

  def list = Action.async { implicit request =>
    val inventoryList = inventoryRepo.list()
    inventoryList.map(inventories => Ok(views.html.inventory.list(inventories)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    if (request.method == "POST") {
      var products: Seq[(Product, Category)] = Seq[(Product, Category)]()
      productRepository.list().onComplete {
        case Success(prod) => products = prod
        case Failure(_) => print("fail")
      }

      inventoryForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.inventory.create(errorForm, products))
          )
        },
        inventory => {
          inventoryRepo.create(inventory.quantity, inventory.available, inventory.product).map { _ =>
            Redirect(routes.InventoryController.create()).flashing("success" -> "inventory.created")
          }
        }
      )

    } else {
      val productList = productRepository.list()
      productList.map(products => Ok(views.html.inventory.create(inventoryForm, products)))
    }
  }

  def details(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val inventory = inventoryRepo.getById(id)

    inventory.map(inventory => inventory match {
      case Some(i) => {
        val form = updateInventoryForm.fill(UpdateInventoryForm(i.id, i.quantity, i.available, i.product))
        Ok(views.html.inventory.details(i, form))
      }
      case None => Redirect(routes.HomeController.index())
    })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    updateInventoryForm.bindFromRequest.fold(
      errorForm => {
        val inventory = inventoryRepo.getById(id)
        inventory.map(inventory => inventory match {
          case Some(p) => {
            BadRequest(views.html.inventory.details(p, errorForm))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      i => {
        inventoryRepo.update(i.id, Inventory(i.id, i.quantity, i.available, i.product)).map { _ =>
          Redirect(routes.InventoryController.details(i.id)).flashing("success" -> "inventory updated")
        }
      }
    )
  }

  def apiList = Action.async { implicit request =>
    val inventoryList = inventoryRepo.list()
    inventoryList.map(list => Ok(Json.toJson(list)))
  }

  def apiGet(id: Long) = Action.async { implicit request =>
    val inventory = inventoryRepo.getById(id)
    inventory.map(inventory => inventory match {
      case Some(i) => Ok(Json.obj("inventory" -> Json.toJson(i)))
      case None => NotFound
    })
  }

  def apiGetByProductId(id: Long) = Action.async { implicit request =>
    val inventory = inventoryRepo.getByProductId(id)
    inventory.map(inventory => inventory match {
      case Some(i) => Ok(Json.obj("inventory" -> Json.toJson(i)))
      case None => NotFound
    })
  }

  def apiAdd = Action.async { implicit request =>
    val inventory = request.body.asJson.get.as[Inventory]
    inventoryRepo.create(inventory.quantity,  inventory.available, inventory.product).map(newEntity => {
      Ok(Json.obj("inventory" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = Action.async { implicit request =>
    val inventory = request.body.asJson.get.as[Inventory]
    inventoryRepo.update(id, inventory).map(updatedEntity => updatedEntity match{
      case Some(e) => Ok(Json.obj("inventory" -> Json.toJson(e)))
      case None => NotFound
    })
  }

  def apiDelete(id: Long) = Action {
    inventoryRepo.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }
}

case class CreateInventoryForm(quantity: Int, available: Int, product: Long)
case class UpdateInventoryForm(id: Long, quantity: Int, available: Int, product: Long)
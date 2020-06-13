package controllers

import javax.inject._
import models.{Category, CategoryRepository, Product, ProductRepository}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}
import play.api.data.format.Formats._
import play.api.libs.json.Json


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's product page.
 */
@Singleton
class ProductController @Inject()(productRepo: ProductRepository, categoryRepo: CategoryRepository, cc: MessagesControllerComponents)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "photo" -> nonEmptyText,
      "price" -> of[Float],
      "category" -> longNumber,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "photo" -> nonEmptyText,
      "price" -> of[Float],
      "category" -> longNumber,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    val productsList = productRepo.list()
    productsList.map(products => Ok(views.html.product.list(products)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    if (request.method == "POST") {
      var categories: Seq[Category] = Seq[Category]()
      categoryRepo.list().onComplete {
        case Success(cat) => categories = cat
        case Failure(_) => print("fail")
      }

      productForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.product.create(errorForm, categories))
          )
        },
        p => {
          productRepo.create(p.name, p.description, p.photo, p.price, p.category).map { _ =>
            Redirect(routes.ProductController.create()).flashing("success" -> "product.created")
          }
        }
      )

    } else {
      val categoryList = categoryRepo.list()
      categoryList.map(categories => Ok(views.html.product.create(productForm, categories)))
    }
  }

  def details(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var categories: Seq[Category] = Seq[Category]()
    categoryRepo.list().onComplete {
      case Success(cat) => categories = cat
      case Failure(_) => print("fail")
    }

    val product = productRepo.getById(id)
    product.map(product => product match {
      case Some(p) => {
        val form = updateProductForm.fill(UpdateProductForm(p.id, p.name, p.description, p.photo, p.price, p.category))
        Ok(views.html.product.details(p, form, categories))
      }
      case None => Redirect(routes.HomeController.index())
    })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var categories: Seq[Category] = Seq[Category]()
    categoryRepo.list().onComplete {
      case Success(cat) => categories = cat
      case Failure(_) => print("fail")
    }

    updateProductForm.bindFromRequest.fold(
      errorForm => {
        val product = productRepo.getById(id)
        product.map(product => product match {
          case Some(p) => {
            BadRequest(views.html.product.details(p, errorForm, categories))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      p => {
        productRepo.update(p.id, Product(p.id, p.name, p.description, p.photo, p.price, p.category)).map { _ =>
          Redirect(routes.ProductController.details(p.id)).flashing("success" -> "product updated")
        }
      }
    )
  }

  def apiList = Action.async { implicit request =>
    val productList = productRepo.list()
    productList.map(products => Ok(Json.toJson(products)))
  }

  def apiGet(id: Long) = Action.async { implicit request =>
    val product = productRepo.getById(id)
    product.map(product => product match {
      case Some(c) => Ok(Json.obj("product" -> Json.toJson(c)))
      case None => NotFound
    })
  }

  def apiAdd = Action.async { implicit request =>
    val product = request.body.asJson.get.as[Product]
    productRepo.create(product.name, product.description, product.photo, product.price, product.category).map(newEntity => {
      Ok(Json.obj("product" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = Action.async { implicit request =>
    val product = request.body.asJson.get.as[Product]
    productRepo.update(id, product).map(updatedEntity => updatedEntity match{
      case Some(e) => Ok(Json.obj("product" -> Json.toJson(e)))
      case None => NotFound
    })
  }

  def apiDelete(id: Long) = Action {
    productRepo.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }
}

case class CreateProductForm(name: String, description: String, photo: String, price: Float, category: Long)
case class UpdateProductForm(id: Long, name: String, description: String, photo: String, price: Float, category: Long)
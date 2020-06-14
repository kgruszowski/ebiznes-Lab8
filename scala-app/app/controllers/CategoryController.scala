package controllers

import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import javax.inject._
import models.{Category, CategoryRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import utils.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's category page.
 */
@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepository, silhouette: Silhouette[DefaultEnv], cc: MessagesControllerComponents)
                                  (implicit ec:ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    val categoryList = categoryRepo.list()
    categoryList.map(categories => Ok(views.html.category.list(categories)))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    if (request.method == "POST") {
      categoryForm.bindFromRequest.fold(
        errorForm => {
          Future.successful(
            BadRequest(views.html.category.create(errorForm))
          )
        },
        category => {
          categoryRepo.create(category.name).map { _ =>
            Redirect(routes.CategoryController.create()).flashing("success" -> "category.created")
          }
        }
      )

    } else {
      Future(Ok(views.html.category.create(categoryForm)))
    }
  }

  def details(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val category = categoryRepo.getById(id)
    category.map(category => category match {
      case Some(c) => {
        val form = updateCategoryForm.fill(UpdateCategoryForm(c.id, c.name))
        Ok(views.html.category.details(c, form))
      }
      case None => Redirect(routes.HomeController.index())
    })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        val category = categoryRepo.getById(id)
        category.map(category => category match {
          case Some(c) => {
            BadRequest(views.html.category.details(c, errorForm))
          }
          case None => Redirect(routes.HomeController.index())
        })
      },
      c => {
        categoryRepo.update(c.id, Category(c.id, c.name)).map { _ =>
          Redirect(routes.CategoryController.details(c.id)).flashing("success" -> "category updated")
        }
      }
    )
  }

  def apiList = silhouette.SecuredAction.async { implicit request =>
    val categoryList = categoryRepo.list()
    categoryList.map(list => Ok(Json.toJson(list)))
  }

  def apiGet(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val category = categoryRepo.getById(id)
    category.map(category => category match {
      case Some(i) => Ok(Json.obj("category" -> Json.toJson(i)))
      case None => NotFound
    })
  }

  def apiAdd = silhouette.SecuredAction.async { implicit request =>
    val category = request.body.asJson.get.as[Category]
    categoryRepo.create(category.name).map(newEntity => {
      Ok(Json.obj("category" -> Json.toJson(newEntity)))
    })
  }

  def apiUpdate(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val category = request.body.asJson.get.as[Category]
    categoryRepo.update(id, category).map(updatedEntity => updatedEntity match{
      case Some(e) => Ok(Json.obj("category" -> Json.toJson(e)))
      case None => NotFound
    })
  }

  def apiDelete(id: Long)= silhouette.SecuredAction {
    categoryRepo.delete(id)
    Ok(Json.obj("result" -> "ok"))
  }
}

case class CreateCategoryForm(name: String)
case class UpdateCategoryForm(id: Long, name: String)
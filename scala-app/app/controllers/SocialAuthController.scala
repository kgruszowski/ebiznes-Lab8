package controllers

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.exceptions.OAuth2StateException
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.Inject
import models.{User, UserService}
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import utils.DefaultEnv

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

/**
 * The social auth controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info service implementation.
 * @param socialProviderRegistry The social provider registry.
 * @param actorSystem            The actor system.
 * @param executionContext       The execution context.
 */

class SocialAuthController @Inject() (
                                       components: ControllerComponents,
                                       silhouette: Silhouette[DefaultEnv],
                                       userService: UserService,
                                       authInfoRepository: AuthInfoRepository,
                                       socialProviderRegistry: SocialProviderRegistry,
                                       actorSystem: ActorSystem
                                     )(implicit executionContext: ExecutionContext) extends AbstractController(components) with I18nSupport with Logger {

  /**
   * Authenticates a user against a social provider.
   *
   * @param provider The ID of the provider to authenticate against.
   * @return The result to display.
   */
  def authenticate(provider: String) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.save(User(profile))
            authInfo <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)
            value <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(value, Ok)
          } yield {
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            Redirect("http://localhost:3000/products", Map("accessToken" -> Seq(value)))
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        BadRequest("UNEXPECTED_PROVIDER")
    }
  }

  def getUser() = silhouette.UserAwareAction { implicit request =>
    request.identity match {
      case Some(identity) => Ok(Json.obj("userId" -> Json.toJson(identity.id), "userName" -> Json.toJson(identity.name)))
      case None => NotFound
    }
  }
}

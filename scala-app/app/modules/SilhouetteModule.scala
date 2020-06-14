package modules

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto.{Crypter, CrypterAuthenticatorEncoder, Signer}
import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings, JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.{DefaultSocialStateHandler, OAuth2Info, OAuth2Settings, SocialProviderRegistry}
import com.mohiva.play.silhouette.impl.providers.oauth2.{GitHubProvider, GoogleProvider}
import com.mohiva.play.silhouette.impl.providers.state.{CsrfStateItemHandler, CsrfStateSettings}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import models.UserService
import models.daos.SocialAuthInfoDAOImpl
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.CookieHeaderEncoding
import utils.{CustomSecuredErrorHandler, CustomUnsecuredErrorHandler, DefaultEnv}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

class SilhouetteModule extends AbstractModule with ScalaModule {
  /**
   * @see https://www.playframework.com/documentation/2.6.x/ScalaDependencyInjection#programmatic-bindings
   */

  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[UnsecuredErrorHandler].to[CustomUnsecuredErrorHandler]
    bind[SecuredErrorHandler].to[CustomSecuredErrorHandler]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
    bind[DelegableAuthInfoDAO[OAuth2Info]].to[SocialAuthInfoDAOImpl]
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
  }

  /**
   * Provides the utils.auth info repository.
   *
   * @param oauth2InfoDAO The implementation of the delegable OAuth2 utils.auth info DAO.
   * @return The utils.auth info repository instance.
   */
  @Provides
  def provideAuthInfoRepository(oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info]): AuthInfoRepository = {
    new DelegableAuthInfoRepository(oauth2InfoDAO)
  }

  /**
   * Provides the HTTP layer implementation.
   *
   * @param client Play's WS client.
   * @return The HTTP layer implementation.
   */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /**
   * Provides the Silhouette environment.
   *
   * @param userService          The user service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus             The event bus instance.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(userService: UserService,
                         authenticatorService: AuthenticatorService[JWTAuthenticator],
                         eventBus: EventBus): Environment[DefaultEnv] =
    Environment[DefaultEnv](userService, authenticatorService, Seq(), eventBus)


  /**
   * Provides the crypter for the authenticator.
   *
   * @param configuration The Play configuration.
   * @return The crypter for the authenticator.
   */
  @Provides
  @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = JcaCrypterSettings(configuration.get[String]("silhouette.authenticator.crypter.key"))

    new JcaCrypter(config)
  }

  /**
   * Provides the authenticator service.
   *
   * @param crypter       The crypter implementation.
   * @param idGenerator   The ID generator implementation.
   * @param configuration The Play configuration.
   * @param clock         The clock instance.
   * @return The authenticator service.
   */
  @Provides
  def provideAuthenticatorService(@Named("authenticator-crypter") crypter: Crypter,
                                  idGenerator: IDGenerator,
                                  configuration: Configuration,
                                  clock: Clock): AuthenticatorService[JWTAuthenticator] = {
    val settings = JWTAuthenticatorSettings(sharedSecret = configuration.get[String]("silhouette.authenticator.jwt.key"))
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new JWTAuthenticatorService(settings, None, encoder, idGenerator, clock)
  }

  /**
   * Provides the social provider registry.
   *
   * @param googleProvider The Google provider implementation.
   * @return The Silhouette environment.
   */
  @Provides
  def provideSocialProviderRegistry(googleProvider: GoogleProvider, gitHubProvider: GitHubProvider): SocialProviderRegistry = {

    SocialProviderRegistry(Seq(
      googleProvider,
      gitHubProvider
    ))
  }

  /**
   * Provides the signer for the social state handler.
   *
   * @param configuration The Play configuration.
   * @return The signer for the social state handler.
   */
  @Provides
  @Named("social-state-signer")
  def provideSocialStateSigner(configuration: Configuration): Signer = {
    val config = JcaSignerSettings(
      key = configuration.get[String]("silhouette.socialStateHandler.signer.key")
    )

    new JcaSigner(config)
  }

  /**
   * Provides the signer for the CSRF state item handler.
   *
   * @param configuration The Play configuration.
   * @return The signer for the CSRF state item handler.
   */
  @Provides
  @Named("csrf-state-item-signer")
  def provideCSRFStateItemSigner(configuration: Configuration): Signer = {
    val config = JcaSignerSettings(configuration.get[String]("silhouette.csrfStateItemHandler.signer.key"))

    new JcaSigner(config)
  }

  /**
   * Provides the CSRF state item handler.
   *
   * @param idGenerator   The ID generator implementation.
   * @param signer        The signer implementation.
   * @param configuration The Play configuration.
   * @return The CSRF state item implementation.
   */
  @Provides
  def provideCsrfStateItemHandler(
                                   idGenerator: IDGenerator,
                                   @Named("csrf-state-item-signer") signer: Signer,
                                   configuration: Configuration): CsrfStateItemHandler = {
    val settings = CsrfStateSettings(
      cookieName = configuration.get[String]("silhouette.csrfStateItemHandler.cookieName"),
      cookiePath = configuration.get[String]("silhouette.csrfStateItemHandler.cookiePath"),
      secureCookie = configuration.get[Boolean]("silhouette.csrfStateItemHandler.secureCookie"),
      httpOnlyCookie = configuration.get[Boolean]("silhouette.csrfStateItemHandler.httpOnlyCookie"),
      expirationTime = configuration.get[FiniteDuration]("silhouette.csrfStateItemHandler.expirationTime"),
    )

    new CsrfStateItemHandler(settings, idGenerator, signer)
  }

  /**
   * Provides the social state handler.
   *
   * @param signer The signer implementation.
   * @return The social state handler implementation.
   */
  @Provides
  def provideSocialStateHandler(@Named("social-state-signer") signer: Signer,
                                 csrfStateItemHandler: CsrfStateItemHandler): DefaultSocialStateHandler = {
    new DefaultSocialStateHandler(Set(csrfStateItemHandler), signer)
  }

  /**
   * Provides the Google provider.
   *
   * @param httpLayer          The HTTP layer implementation.
   * @param socialStateHandler The social state handler implementation.
   * @param configuration      The Play configuration.
   * @return The Google provider.
   */
  @Provides
  def provideGoogleProvider(
                             httpLayer: HTTPLayer,
                             socialStateHandler: DefaultSocialStateHandler,
                             configuration: Configuration): GoogleProvider = {

    val config = OAuth2Settings(
      authorizationURL = configuration.get[Option[String]]("silhouette.google.authorizationURL"),
      accessTokenURL = configuration.get[String]("silhouette.google.accessTokenURL"),
      redirectURL = configuration.get[Option[String]]("silhouette.google.redirectURL"),
      clientID = configuration.get[String]("silhouette.google.clientID"),
      clientSecret = configuration.get[String]("silhouette.google.clientSecret"),
      scope = configuration.get[Option[String]]("silhouette.google.scope")
    )

    new GoogleProvider(httpLayer, socialStateHandler, config)
  }

  /**
   * Provides the Github provider.
   *
   * @param httpLayer          The HTTP layer implementation.
   * @param socialStateHandler The social state handler implementation.
   * @param configuration      The Play configuration.
   * @return The Github provider.
   */
  @Provides
  def provideGithubProvider(
                             httpLayer: HTTPLayer,
                             socialStateHandler: DefaultSocialStateHandler,
                             configuration: Configuration): GitHubProvider = {

    val config = OAuth2Settings(
      authorizationURL = configuration.get[Option[String]]("silhouette.github.authorizationURL"),
      accessTokenURL = configuration.get[String]("silhouette.github.accessTokenURL"),
      redirectURL = configuration.get[Option[String]]("silhouette.github.redirectURL"),
      clientID = configuration.get[String]("silhouette.github.clientID"),
      clientSecret = configuration.get[String]("silhouette.github.clientSecret"),
    )

    new GitHubProvider(httpLayer, socialStateHandler, config)
  }
}

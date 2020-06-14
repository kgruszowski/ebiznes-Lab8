package models

import javax.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, OAuth2Info}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                            protected val userRepository: UserRepository)
                           (implicit ec: ExecutionContext)
  extends IdentityService[User]
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import userRepository.UserTable
  val userTable = TableQuery[UserTable]

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run {
    play.Logger.debug {
      s"""UserServiceImpl.retrieve ----------
      		------------------ loginInfo: ${loginInfo}"""
    }
    userTable
      .filter(_.email === loginInfo.providerKey).result.headOption
  }

  def find(loginInfo: LoginInfo): Future[Option[User]] = db.run {
    userTable.filter(_.email === loginInfo.providerKey).result.headOption
  }

  def save(user: User): Future[User] = {
    play.Logger.debug {
      s"""UserServiceImpl.save ----------
      		------------------ loginInfo: ${user}"""
    }
    val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
    find(loginInfo).map {
      case Some(_user) => _user
      case None => db.run((userTable += user))
    }.map { _ => user }
  }
}


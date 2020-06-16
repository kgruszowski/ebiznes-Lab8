package models

import javax.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, OAuth2Info}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                            protected val userRepository: UserRepository,
                            protected val customerRepository: CustomerRepository)
                           (implicit ec: ExecutionContext)
  extends IdentityService[User]
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import userRepository.UserTable
  import customerRepository.CustomerTable
  val userTable = TableQuery[UserTable]
  val customerTable = TableQuery[CustomerTable]

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run {
    userTable.filter(_.email === loginInfo.providerKey).result.headOption
  }

  def find(loginInfo: LoginInfo): Future[Option[User]] = db.run {
    userTable.filter(_.email === loginInfo.providerKey).result.headOption
  }

  def save(user: User): Future[User] = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
    find(loginInfo).map {
      case Some(_user) => _user
      case None =>
        db.run((userTable += user))
        db.run((customerTable) += Customer(0, user.name, "", ""))
    }.map { _ => user }
  }
}


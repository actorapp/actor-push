package im.actor.push.repo

import im.actor.push.model.Token
import slick.driver.PostgresDriver.api._

final class TokenTable(tag: Tag) extends Table[Token](tag, "tokens") {
  def id = column[Int]("id", O.PrimaryKey)
  def token = column[String]("token")

  def * = (id, token) <> ((Token.apply _).tupled, Token.unapply)
}

object TokenRepo {
  val tokens = TableQuery[TokenTable]

  def create(token: Token) = tokens += token

  def find(id: Int) = tokens.filter(_.id === id).result.headOption
}
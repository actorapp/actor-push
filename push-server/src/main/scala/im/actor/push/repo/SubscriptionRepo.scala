package im.actor.push.repo

import im.actor.push.model.Subscription
import slick.driver.PostgresDriver.api._

final class SubscriptionTable(tag: Tag) extends Table[Subscription](tag, "subscriptions") {
  def appId = column[Int]("app_id", O.PrimaryKey)
  def id = column[String]("id", O.PrimaryKey)

  def * = (appId, id) <> ((Subscription.apply _).tupled, Subscription.unapply)
}

object SubscriptionRepo {
  val subscriptions = TableQuery[SubscriptionTable]

  def create(subscription: Subscription) = subscriptions += subscription

  def find(appId: Int, id: String) = subscriptions.filter(s ⇒ s.appId === appId && s.id === id).result.headOption
}
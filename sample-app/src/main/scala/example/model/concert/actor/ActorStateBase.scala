package example.model.concert.actor

import akka.actor.Actor.Receive

trait ActorStateBase[Event, State <: ActorStateBase[Event, State]] {
  type EventHandler = PartialFunction[Event, State]
  def updated: EventHandler
  def receiveCommand: Receive
}

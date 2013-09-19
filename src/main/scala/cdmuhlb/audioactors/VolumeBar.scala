package cdmuhlb.audioactors

import scala.collection.mutable.StringBuilder
import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}

class VolumeBar(analyzer: ActorRef) extends Actor with ActorLogging {
  var ticker: Cancellable = null
  val consoleWidth = context.system.settings.config.getInt(
      "audioactors.volume-bar.console-width")

  override def preStart = {
    implicit val ec = context.dispatcher
    val config = context.system.settings.config
    val updateInterval = FiniteDuration(config.getMilliseconds(
        "audioactors.volume-bar.update-interval"), MILLISECONDS)
    ticker = context.system.scheduler.schedule(
        updateInterval, updateInterval, analyzer, GetMax)
  }

  def receive = {
    case HaveMax(max) ⇒
      val length = (max*consoleWidth).toInt
      val sb = new StringBuilder(consoleWidth + 1)
      for (i ← 0 until length) sb += '|'
      for (i ← length until consoleWidth) sb += ' '
      sb += '\r'
      Console.print(sb.toString)
      Console.flush()
  }

  override def postStop = {
    if (ticker != null) ticker.cancel()
  }
}

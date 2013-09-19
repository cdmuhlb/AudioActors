package cdmuhlb.audioactors

import java.nio.{ByteBuffer, ByteOrder}
import javax.sound.sampled.AudioFormat
import akka.actor.{Actor, ActorLogging}

class MaxConsumer(format: AudioFormat) extends Actor with ActorLogging {
  var max: Double = 0.0
  require(format.getSampleSizeInBits == 16)
  val endianness = if (format.isBigEndian) ByteOrder.BIG_ENDIAN
                   else ByteOrder.LITTLE_ENDIAN

  def receive = {
    case AcceptSamples(samples) ⇒
      log.debug(s"Processing ${samples.length} bytes")
      val shortBuf = ByteBuffer.wrap(samples).order(
          endianness).asShortBuffer
      while (shortBuf.hasRemaining) {
        val a = math.abs(shortBuf.get().toDouble) / Short.MaxValue.toDouble
        if (a > max) max = a
      }
      sender ! ProvideSamples
    case GetMax ⇒
      sender ! HaveMax(max)
      max = 0.0
  }
}

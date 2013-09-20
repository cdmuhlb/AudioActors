package cdmuhlb.audioactors

import java.nio.{ByteBuffer, ByteOrder}
import javax.sound.sampled.AudioFormat
import akka.actor.{Actor, ActorLogging}

class Consumer(format: AudioFormat, producer: AnalyzerProducer) extends Actor
    with ActorLogging {
  val analyzer = producer.produce()
  require(format.getSampleSizeInBits == 16)
  val endianness = if (format.isBigEndian) ByteOrder.BIG_ENDIAN
                   else ByteOrder.LITTLE_ENDIAN

  def receive = {
    case AcceptSamples(samples) ⇒
      log.debug(s"Processing ${samples.length} bytes")
      val shortBuf = ByteBuffer.wrap(samples).order(
          endianness).asShortBuffer
      analyzer.process(shortBuf)
      sender ! ProvideSamples
    case GetAnalysis ⇒
      sender ! HaveAnalysis(analyzer.report)
      analyzer.reset()
  }
}

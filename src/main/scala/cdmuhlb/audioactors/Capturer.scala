package cdmuhlb.audioactors

import java.util.Arrays
import javax.sound.sampled._
import akka.actor.{Actor, ActorLogging}

class Capturer(mixerInfo: Mixer.Info, lineInfo: DataLine.Info)
    extends Actor with ActorLogging {
  val mixer = AudioSystem.getMixer(mixerInfo)
  val line = mixer.getLine(lineInfo).asInstanceOf[TargetDataLine]
  val minByteLatency = {
    val format = line.getFormat
    val frameSize = format.getFrameSize
    MoreMath.genCeil(frameSize, (frameSize*format.getFrameRate *
        0.001*context.system.settings.config.getMilliseconds(
        "audioactors.app-latency")).toInt)
  }
  val buffer = Array.ofDim[Byte](line.getBufferSize)

  override def preStart = {
    log.debug("Opening and starting target line")
    line.open()
    line.start()
  }

  def receive = {
    case ProvideSamples ⇒
      val nToRead = minByteLatency.max(line.available)
      log.debug(s"Capturing $nToRead bytes")
      val nRead = line.read(buffer, 0, nToRead)
      sender ! AcceptSamples(Arrays.copyOf(buffer, nRead))
    case FlushSamples ⇒
      log.debug("Flushing target samples")
      line.flush()
  }

  override def postStop = {
    log.debug("Stopping and closing target line")
    line.stop()
    line.close()
  }
}

package cdmuhlb.audioactors

import javax.sound.sampled._
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()

  val mixerIndex = config.getInt("audioactors.mixer-index")
  val mixerInfo = AudioSystem.getMixerInfo()(mixerIndex)
  println(s"Using mixer ${mixerInfo.getName} (${mixerInfo.getDescription})")

  val format = new AudioFormat(
    config.getInt("audioactors.capture-rate").toFloat,
    config.getInt("audioactors.capture-bits"),
    config.getInt("audioactors.capture-channels"),
    config.getBoolean("audioactors.capture-signed"),
    config.getBoolean("audioactors.capture-big-endian"))
  println(s"Capturing with format $format")

  val bufferSize = {
    val frameSize = format.getFrameSize
    MoreMath.genCeil(frameSize, (frameSize*format.getFrameRate *
      0.001*config.getMilliseconds("audioactors.javasound-latency")).toInt)
  }
  val targetLineInfo = new DataLine.Info(classOf[TargetDataLine], format,
      bufferSize)
  println(s"Using buffer size of $bufferSize bytes")

  val system = ActorSystem("AudioActors", config)
  val capturer = system.actorOf(Props(classOf[Capturer], mixerInfo,
      targetLineInfo), "captuer")
  val analyzer = system.actorOf(Props(classOf[Consumer], format,
      MaxAnalyzerProducer), "analyzer")
  val volumeBar = system.actorOf(Props(classOf[VolumeBar], analyzer),
      "volumeBar")

  capturer ! FlushSamples
  capturer.tell(ProvideSamples, analyzer)

  // Exit on user input
  println("Press [Enter] to exit")
  readLine()
  system.shutdown()
}

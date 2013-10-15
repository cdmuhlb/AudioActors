package cdmuhlb.audioactors

import javax.sound.sampled._

object MixerHelper {
  def mixerHelp(index: Int): String = {
    val info = AudioSystem.getMixerInfo()(index)
    raw"""
      |[$index] ${info.getName}: ${info.getDescription}
      |    ${info.getVendor}, ${info.getVersion}
      """.trim.stripMargin
  }

  def mixersHelp(): String = {
    val nMixers = AudioSystem.getMixerInfo.length
    (for (index ← 0 until nMixers) yield mixerHelp(index)).mkString("\n")
  }
}

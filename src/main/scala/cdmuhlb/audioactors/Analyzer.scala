package cdmuhlb.audioactors

import java.nio.ShortBuffer

trait Analyzer {
  def process(samples: ShortBuffer): Unit
  def reset(): Unit
  def report: Double
}

class MaxAnalyzer extends Analyzer {
  var max: Double = 0

  def process(samples: ShortBuffer): Unit = {
    while (samples.hasRemaining) {
      val a = math.abs(samples.get().toDouble) / Short.MaxValue.toDouble
      if (a > max) max = a
    }
  }

  def reset(): Unit = {
    max = 0
  }

  def report: Double = max
}

trait AnalyzerProducer {
  def produce(): Analyzer
}
object MaxAnalyzerProducer extends AnalyzerProducer {
  def produce(): Analyzer = new MaxAnalyzer
}

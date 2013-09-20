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
class RmsAnalyzer extends Analyzer {
  var sqrSum: Double = 0.0
  var nSamples: Int = 0

  def process(samples: ShortBuffer): Unit = {
    while (samples.hasRemaining) {
      val a = samples.get().toDouble / Short.MaxValue.toDouble
      sqrSum += a*a
      nSamples += 1
    }
  }

  def reset(): Unit = {
    sqrSum = 0.0
    nSamples = 0
  }

  def report: Double = math.sqrt(sqrSum/nSamples)
}

trait AnalyzerProducer {
  def produce(): Analyzer
}
object MaxAnalyzerProducer extends AnalyzerProducer {
  def produce(): Analyzer = new MaxAnalyzer
}
object RmsAnalyzerProducer extends AnalyzerProducer {
  def produce(): Analyzer = new RmsAnalyzer
}

package cdmuhlb.audioactors

case class AcceptSamples(samples: Array[Byte])
case object ProvideSamples
case object FlushSamples

case class HaveMax(max: Double)
case object GetMax

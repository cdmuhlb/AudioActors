package cdmuhlb.audioactors

object MoreMath {
  def genCeil(base: Int, x: Int): Int = {
    base * ((x - 1)/base + 1)
  }
}

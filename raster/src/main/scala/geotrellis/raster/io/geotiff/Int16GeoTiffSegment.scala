package geotrellis.raster.io.geotiff

import geotrellis.raster._
import geotrellis.raster.io.geotiff.util._

import java.nio.ByteBuffer
import java.util.BitSet

import spire.syntax.cfor._

abstract class Int16GeoTiffSegment(val bytes: Array[Byte]) extends GeoTiffSegment {
  protected val buffer = ByteBuffer.wrap(bytes).asShortBuffer

  val size: Int = bytes.size / 2

  def get(i: Int): Short = buffer.get(i)

  def getInt(i: Int): Int
  def getDouble(i: Int): Double

  protected def intToShortOut(v: Int): Short
  protected def doubleToShortOut(v: Double): Short

  def map(f: Int => Int): Array[Byte] = {
    val arr = Array.ofDim[Short](size)
    cfor(0)(_ < size, _ + 1) { i =>
      arr(i) = intToShortOut(f(getInt(i)))
    }
    val result = new Array[Byte](size * ShortConstantNoDataCellType.bytes)
    val bytebuff = ByteBuffer.wrap(result)
    bytebuff.asShortBuffer.put(arr)
    result
  }

  def mapDouble(f: Double => Double): Array[Byte] = {
    map(z => d2i(f(i2d(z))))
  }

  def mapWithIndex(f: (Int, Int) => Int): Array[Byte] = {
    val arr = Array.ofDim[Short](size)
    cfor(0)(_ < size, _ + 1) { i =>
      arr(i) = intToShortOut(f(i, getInt(i)))
    }
    val result = new Array[Byte](size * ShortConstantNoDataCellType.bytes)
    val bytebuff = ByteBuffer.wrap(result)
    bytebuff.asShortBuffer.put(arr)
    result
  }

  def mapDoubleWithIndex(f: (Int, Double) => Double): Array[Byte] = {
    val arr = Array.ofDim[Short](size)
    cfor(0)(_ < size, _ + 1) { i =>
      arr(i) = doubleToShortOut(f(i, getDouble(i)))
    }
    val result = new Array[Byte](size * ShortConstantNoDataCellType.bytes)
    val bytebuff = ByteBuffer.wrap(result)
    bytebuff.asShortBuffer.put(arr)
    result
  }
}

package org.gdal.scala

import org.gdal.gdal.Dataset
import org.gdal.gdal.Band
import org.gdal.gdal.Driver
import org.gdal.gdal.GCP
import org.gdal.gdal.gdal
import org.gdal.osr.SpatialReference

import java.nio.ByteBuffer

import scala.collection.JavaConversions._
import scala.reflect.ClassTag

class Raster(val ds: Dataset) {
  lazy val driver: Driver = ds.GetDriver()
  lazy val files: Seq[String] = {
    val v = ds.GetFileList()
    if(v == null) Seq()
    else v.toSeq.map { _.asInstanceOf[String] }
  }

  lazy val cols: Long = ds.getRasterXSize
  lazy val rows: Long = ds.getRasterYSize

  lazy val xmin: Double =
    geoTransform(0)

  lazy val ymin: Double =
    geoTransform(3) + geoTransform(5) * rows

  lazy val xmax: Double =
    geoTransform(0) +  geoTransform(1) * cols

  lazy val ymax: Double =
    geoTransform(3)

  lazy val projection: Option[String] = {
    val proj = ds.GetProjectionRef
    if(proj == null || proj.isEmpty) None
    else Some(proj)
  }

  lazy val geoTransform: Array[Double] =
    ds.GetGeoTransform

  lazy val groundControlPointCount: Long =
    ds.GetGCPCount

  lazy val groundControlPoints: Seq[GroundControlPoint] = {
    val gcps = new java.util.Vector[GCP]()
    ds.GetGCPs(gcps)
    gcps.map(GroundControlPoint(_)).toSeq
  }

  def metadata: List[String] =
    ds.GetMetadata_List("").toList.map(_.asInstanceOf[String])

  def metadata(id: String): List[String] =
    ds.GetMetadata_List(id).toList.map(_.asInstanceOf[String])

  lazy val bands: Vector[RasterBand] =
    (1 to ds.getRasterCount)
      .map { i => new RasterBand(ds.GetRasterBand(i), cols.toInt, rows.toInt) }
      .toVector
}

class RasterBand(band: Band, cols: Int, rows: Int) {
  lazy val noDataValue: Option[Double] = {
    val arr = Array.ofDim[java.lang.Double](1)
    band.GetNoDataValue(arr)
    if(arr(0) != null) {
      Some(arr(0))
    } else {
      None
    }
  }

  lazy val rasterType: GdalDataType =
    band.getDataType()

  lazy val blockWidth: Int =
    band.GetBlockXSize

  lazy val blockHeight: Int =
    band.GetBlockYSize

  lazy val rasterColorCode: Int =
    band.GetRasterColorInterpretation

  lazy val rasterColorName: String =
    gdal.GetColorInterpretationName(rasterColorCode)

  lazy val description: Option[String] = {
    val desc = band.GetDescription
    if(desc == null || desc.isEmpty) None
    else Some(desc)
  }

  def dataShort(): Array[Short] = {
    val arr = Array.ofDim[Short](cols*rows)
    band.ReadRaster(0,0,cols,rows,TypeInt16,arr)
    arr
  }

  def dataInt(): Array[Int] = {
    val arr = Array.ofDim[Int](cols*rows)
    band.ReadRaster(0,0,cols,rows,TypeInt32,arr)
    arr
  }

  def dataFloat(): Array[Float] = {
    val arr = Array.ofDim[Float](cols*rows)
    band.ReadRaster(0,0,cols,rows,TypeFloat32,arr)
    arr
  }

  def dataDouble(): Array[Double] = {
    val arr = Array.ofDim[Double](cols*rows)
    band.ReadRaster(0,0,cols,rows,TypeFloat64,arr)
    arr
}

object GdalDataType {
  val types =
    List(TypeUnknown,TypeByte, TypeUInt16,TypeInt16,TypeUInt32,TypeInt32,
         TypeFloat32,TypeFloat64,TypeCInt16,TypeCInt32,TypeCFloat32,
         TypeCFloat64)

  implicit def intToGdalDataType(i: Int): GdalDataType =
    types.find(_.code == i) match {
      case Some(dt) => dt
      case None => sys.error(s"Invalid GDAL data type code: $i")
    }

  implicit def GdalDataTypeToInt(typ: GdalDataType): Int =
    typ.code
}

abstract sealed class GdalDataType(val code: Int) {
  override
  def toString: String = gdal.GetDataTypeName(code)
}

case object TypeUnknown extends GdalDataType(0)
case object TypeByte extends GdalDataType(1)
case object TypeUInt16 extends GdalDataType(2)
case object TypeInt16 extends GdalDataType(3)
case object TypeUInt32 extends GdalDataType(4)
case object TypeInt32 extends GdalDataType(5)
case object TypeFloat32 extends GdalDataType(6)
case object TypeFloat64 extends GdalDataType(7)
case object TypeCInt16 extends GdalDataType(8)
case object TypeCInt32 extends GdalDataType(9)
case object TypeCFloat32 extends GdalDataType(10)
case object TypeCFloat64 extends GdalDataType(11)

case class GroundControlPoint(id: String,
                              info: String,
                              col: Double,
                              row: Double,
                              x: Double,
                              y: Double,
                              z: Double)

object GroundControlPoint {
  def apply(gcp: GCP): GroundControlPoint =
    GroundControlPoint(gcp.getId,
                       gcp.getInfo,
                       gcp.getGCPPixel,
                       gcp.getGCPLine,
                       gcp.getGCPX,
                       gcp.getGCPY,
                       gcp.getGCPZ)
}

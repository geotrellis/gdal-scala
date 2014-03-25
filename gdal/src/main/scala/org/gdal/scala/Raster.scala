package org.gdal.scala

import org.gdal.gdal.Dataset
import org.gdal.gdal.Band;
import org.gdal.gdal.Driver
import org.gdal.gdal.GCP
import org.gdal.osr.SpatialReference

import scala.collection.JavaConversions._

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
      .map { i => new RasterBand(ds.GetRasterBand(i)) }
      .toVector
}

class RasterBand(band: Band) {
  lazy val noDataValue: Double = {
    val arr = Array.ofDim[java.lang.Double](1)
    band.GetNoDataValue(arr)
    arr(0)
  }

  lazy val rasterType: GdalDataType =
    band.getDataType()
}

object GdalDataType {
  implicit def intToGdalDataType(i: Int): GdalDataType =
    i match {
      case  0 => TypeUnknown
      case  1 => TypeByte
      case  2 => TypeUInt16
      case  3 => TypeInt16
      case  4 => TypeUInt32
      case  5 => TypeInt32
      case  6 => TypeFloat32
      case  7 => TypeFloat64
      case  8 => TypeCInt16
      case  9 => TypeCInt32
      case 10 => TypeCFloat32
      case 11 => TypeCFloat64
    }
}

abstract sealed class GdalDataType
case object TypeUnknown extends GdalDataType
case object TypeByte extends GdalDataType
case object TypeUInt16 extends GdalDataType
case object TypeInt16 extends GdalDataType
case object TypeUInt32 extends GdalDataType
case object TypeInt32 extends GdalDataType
case object TypeFloat32 extends GdalDataType
case object TypeFloat64 extends GdalDataType
case object TypeCInt16 extends GdalDataType
case object TypeCInt32 extends GdalDataType
case object TypeCFloat32 extends GdalDataType
case object TypeCFloat64 extends GdalDataType

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

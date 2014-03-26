package org.gdal.scala

import org.gdal.gdal.Dataset
import org.gdal.gdal.Band
import org.gdal.gdal.ColorTable
import org.gdal.gdal.Driver
import org.gdal.gdal.GCP
import org.gdal.gdal.gdal
import org.gdal.osr.SpatialReference

import java.awt.Color
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

class RasterColor(color: Color) {
  override
  def toString: String = s"${color.getRed},${color.getGreen},${color.getBlue},${color.getAlpha}"
}

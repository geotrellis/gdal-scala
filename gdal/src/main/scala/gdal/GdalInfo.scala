package gdal

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.gdal.gdal.Band
import org.gdal.gdal.ColorTable
import org.gdal.gdal.Dataset
import org.gdal.gdal.Driver
import org.gdal.gdal.GCP
import org.gdal.gdal.gdal
import org.gdal.gdal.TermProgressCallback
import org.gdal.gdal.RasterAttributeTable
import org.gdal.gdalconst.gdalconstConstants
import org.gdal.osr.CoordinateTransformation
import org.gdal.osr.SpatialReference

import org.gdal.ogr.Geometry

import scala.collection.JavaConversions._

object GdalInfo {
  def main(args:Array[String]): Unit =
    GdalInfoOptions.parse(args) match {
      case Some(options) =>
        apply(options)
      case None =>
        // Argument errored, should have printed usage.
    }

  def apply(options: GdalInfoOptions): Unit = {
    gdal.AllRegister()

    val raster = Gdal.open(options.file.getAbsolutePath)

    val driver = raster.driver

    println(s"Driver: ${driver.getShortName}/${driver.getLongName}")

    val fileList = raster.files
    if(fileList.size == 0) {
      println("Files: none associated")
    } else {
      println("Files:")
      for(f <- fileList) { println(s"       $f") }
    }

    println(s"Size is ${raster.cols}, ${raster.rows}")

    raster.projection match {
      case Some(projection) =>
        val srs = new SpatialReference(projection)
        if(srs != null && projection.length != 0) {
          val arr = Array.ofDim[String](1)
          srs.ExportToPrettyWkt(arr)
          println("Coordinate System is:")
          println(arr(0))
        } else {
          println(s"Coordinate Sytem is ${projection}")
        }
        if(srs != null) { srs.delete() }
      case None =>
    }

    val geoTransform = raster.geoTransform

    if (geoTransform(2) == 0.0 && geoTransform(4) == 0.0) {
      println(s"Origin = (${geoTransform(0)},${geoTransform(3)})")
      println(s"Pixel Size = (${geoTransform(1)},${geoTransform(5)})")
    } else {
      println("GeoTransform =")
      println(s"  ${geoTransform(0)}, ${geoTransform(1)}, ${geoTransform(2)}")
      println(s"  ${geoTransform(3)}, ${geoTransform(4)}, ${geoTransform(5)}")
    }

    if(options.showGcps && raster.groundControlPointCount > 0) {
      for((gcp, i) <- raster.groundControlPoints.zipWithIndex) {
        println(s"GCP[$i]: Id=${gcp.id}, Info=${gcp.info}")
        println(s"    (${gcp.col},${gcp.row}) (${gcp.x},${gcp.y},${gcp.z})")
      }
    }

    if(options.showMetadata) {
      def printMetadata(header: String, id: String = "") = {
        val md = raster.metadata(id)
        if(!md.isEmpty) {
          println(header)
          for(key <- md) {
            println(s"  $key")
          }
        }
      }

      val metadataPairs = List(
        ("Image Structure Metadata:", "IMAGE_STRUCTURE"),
        ("Subdatasets:", "SUBDATASETS"),
        ("Geolocation:", "GEOLOCATION"),
        ("RPC Metadata:", "RPC")
      )

      printMetadata("Metadata:")

      for(domain <- options.mdds) {
        printMetadata("Metadata ($domain):", domain)
      }

      for((header, id) <- metadataPairs) {
        printMetadata(header, id)
      }
    }

    println(s"BOUNDING BOX: (${raster.xmin}, ${raster.ymin}, ${raster.xmax}, ${raster.ymax}")
  }
}

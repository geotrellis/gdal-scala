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

    val dataset =
      gdal.Open(options.file.getAbsolutePath,
        gdalconstConstants.GA_ReadOnly)

    if(dataset == null) {
      System.err.println("GDALOpen failed - " + gdal.GetLastErrorNo)
      System.err.println(gdal.GetLastErrorMsg)
      System.exit(-1)
    }

    val driver = dataset.GetDriver()

    println(s"Driver: ${driver.getShortName}/${driver.getLongName}")

    val fileList = dataset.GetFileList
    if(fileList.size == 0) {
      println("Files: none associated")
    } else {
      println("Files:")
      for(f <- fileList) { println(s"       $f") }
    }

    println(s"Size is ${dataset.getRasterXSize}, ${dataset.getRasterYSize}")

    val projection = dataset.GetProjectionRef
    if(projection != null) {
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
    }

    val geoTransform = Array.ofDim[Double](6)
    dataset.GetGeoTransform(geoTransform)
    if (geoTransform(2) == 0.0 && geoTransform(4) == 0.0) {
      println(s"Origin = (${geoTransform(0)},${geoTransform(3)})")
      println(s"Pixel Size = (${geoTransform(1)},${geoTransform(5)})")
    } else {
      println("GeoTransform =")
      println(s"  ${geoTransform(0)}, ${geoTransform(1)}, ${geoTransform(2)}")
      println(s"  ${geoTransform(3)}, ${geoTransform(4)}, ${geoTransform(5)}")

    }

    if(options.showGcps && dataset.GetGCPCount > 0) {
      val gcps = new Vector[GCP]()
      dataset.GetGCPs(gcps)

      for((gcp, i) <- gcps.elements.zipWithIndex) {
        println(s"GCP[$i]: Id=${gcp.getId}, Info=${gcp.getInfo}")
        println(s"    (${gcp.getGCPPixel},${gcp.getGCPLine}) (${gcp.getGCPX},${gcp.getGCPY},${gcp.getGCPZ})")
      }
    }

    if(options.showMetadata) {
      def printMetadata(header: String, id: String) = {
        val md = dataset.GetMetadata_List(id).toList
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

      printMetadata("Metadata:", "")

      for(domain <- options.mdds) {
        printMetadata("Metadata ($domain):", domain)
      }

      for(pair <- metadataPairs) {
        printMetadata(pair._1, pair._2)
      }
    }
  }
}

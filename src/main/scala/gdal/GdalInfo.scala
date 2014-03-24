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

object GdalInfo {
  val usage = "Usage: gdalinfo [--help-general] [-mm] [-stats] [-hist] [-nogcp] [-nomd]\n               [-norat] [-noct] [-mdd domain]* [-checksum] datasetname"

  def main(args:Array[String]): Unit =
    GdalInfoOptions.parse(args) match {
      case Some(options) =>
        apply(options)
      case None =>
        // Argument errored, should have printed usage.
    }

  def apply(options: GdalInfoOptions): Unit = {
    gdal.AllRegister()

    val geom = Geometry.CreateFromWkt("POINT (1 2)")
    val wkt = geom.ExportToWkt
    println(wkt)
  }
}

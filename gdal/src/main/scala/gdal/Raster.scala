package gdal

import org.gdal.gdal.Dataset
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

  lazy val projection: Option[String] = {
    val proj = ds.GetProjectionRef
    if(proj == null || proj.isEmpty) None
    else Some(proj)
  }

  lazy val geoTransform: Array[Double] = {
    val gt = Array.ofDim[Double](6)
    ds.GetGeoTransform(gt)
    gt
  }

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
}

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

package geotrellis.gdal

import gdal.Gdal
import gdal.{Raster => GdalRaster}

import geotrellis._

object GdalReader {
  def read(path: String): Raster = {
    val gdalRaster: GdalRaster = Gdal.open(path)

//    val extent = 
    ???
  }
}

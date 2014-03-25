package geotrellis.gdal

import org.gdal.{scala => GDAL}
import org.gdal.scala.Gdal

import geotrellis._

object GdalReader {
  def read(path: String, band: Int = 1): Raster = {
    val gdalRaster: GDAL.Raster = Gdal.open(path)

    val extent = Extent(gdalRaster.xmin, 
                        gdalRaster.ymin,
                        gdalRaster.xmax,
                        gdalRaster.ymax)
    val rasterExtent = RasterExtent(extent, gdalRaster.cols, gdalRaster.rows)

    val rasterBand = gdalRaster.bands(band)
    val rasterType = rasterBand.rasterType match {
      case GDAL.TypeUnknown => geotrellis.TypeDouble
      case GDAL.TypeByte => geotrellis.TypeByte
      case GDAL.TypeUInt16 => geotrellis.TypeInt
      case GDAL.TypeInt16 => geotrellis.TypeShort
      case GDAL.TypeUInt32 => geotrellis.TypeFloat
      case GDAL.TypeInt32 => geotrellis.TypeInt
      case GDAL.TypeFloat32 => geotrellis.TypeFloat
      case GDAL.TypeFloat64 => geotrellis.TypeDouble
      case GDAL.TypeCInt16 => ???
      case GDAL.TypeCInt32 => ???
      case GDAL.TypeCFloat32 => ???
      case GDAL.TypeCFloat64 => ???
    }

    ???
  }
}

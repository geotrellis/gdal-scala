package geotrellis.gdal

import gdal.Gdal
import _root_.{gdal => GDAL}
import gdal.{Raster => GdalRaster}

import geotrellis._

object GdalReader {
  def read(path: String, band: Int = 1): Raster = {
    val gdalRaster: GdalRaster = Gdal.open(path)

    val extent = Extent(gdalRaster.xmin, 
                        gdalRaster.ymin,
                        gdalRaster.xmax,
                        gdalRaster.ymax)
    val rasterExtent = RasterExtent(extent, gdalRaster.cols, gdalRaster.rows)

    val rasterBand = gdalRaster.bands(band)
    val rasterType = rasterBand.rasterType match {
      case TypeUnknown => geotrellis.TypeDouble
      case TypeByte => geotrellis.TypeByte
      case TypeUInt16 => geotrellis.TypeInt
      case TypeInt16 => geotrellis.TypeShort
      case TypeUInt32 => geotrellis.TypeFloat
      case TypeInt32 => geotrellis.TypeInt
      case TypeFloat32 => geotrellis.TypeFloat
      case TypeFloat64 => geotrellis.TypeDouble
      case TypeCInt16 => ???
      case TypeCInt32 => ???
      case TypeCFloat32 => ???
      case TypeCFloat64 => ???
    }

    ???
  }
}

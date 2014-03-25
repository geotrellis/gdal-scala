package geotrellis.gdal

import geotrellis._
import geotrellis.data.GeoTiff

import org.scalatest.FunSpec
import org.scalatest.matchers._

class GdalReaderSpec extends FunSpec with ShouldMatchers {
  val path = "geotrellis/data/slope.tif"

  describe("reading a GeoTiff") {
    it("should match one read with GeoTools") {
      val gdalRaster = GdalReader.read(path)
      val geotoolsRaster = GeoTiff.readRaster(path)

      val gdRe = gdalRaster.rasterExtent
      val gtRe = geotoolsRaster.rasterExtent

      val gdExt = gdRe.extent
      val gtExt = gtRe.extent

      gdExt.xmin should be (gtExt.xmin plusOrMinus 0.00001)
      gdExt.xmax should be (gtExt.xmax plusOrMinus 0.00001)
      gdExt.ymin should be (gtExt.ymin plusOrMinus 0.00001)
      gdExt.ymax should be (gtExt.ymax plusOrMinus 0.00001)

      gdRe.cols should be (gtRe.cols)
      gdRe.rows should be (gtRe.rows)

      gdalRaster.rasterType should be (geotoolsRaster.rasterType)

      for(col <- 0 until gdRe.cols) {
        for(row <- 0 until gdRe.rows) {
          val actual = gdalRaster.getDouble(col, row)
          val expected = geotoolsRaster.getDouble(col, row)
          withClue(s"At ($col, $row): GDAL - $actual  GeoTools - $expected") {
            isNoData(actual) should be (isNoData(expected))
            if(isData(actual))
              actual should be (expected)
          }
        }
      }
    }
  }
}

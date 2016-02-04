package loamstream.vcf

import org.scalatest.FunSuite
import scala.util.Success
import java.time.LocalDate

/**
 * @author clint
 * date: Feb 3, 2016
 */
final class VcfFileSuite extends FunSuite {

  import VcfFileSuite._

  test("Parsing a valid file") {
    val attempt = VcfFile.fromString(validSampleFile)

    val vcf = attempt.get

    assert(vcf.format == fileFormat)
    assert(vcf.date == LocalDate.of(2009, 8, 5))
    assert(vcf.source == source)
    assert(vcf.reference == reference)
    assert(vcf.phasing == phasing)

    val expectedInfos = Seq(
      Info(Id("NS"), Some(1), Type.Integer, "Number of Samples With Data"),
      Info(Id("DP"), Some(1), Type.Integer, "Total Depth"),
      Info(Id("AF"), None, Type.Float, "Allele Frequency"),
      Info(Id("AA"), Some(1), Type.String, "Ancestral Allele"),
      Info(Id("DB"), Some(0), Type.Flag, "dbSNP membership, build 129"),
      Info(Id("H2"), Some(0), Type.Flag, "HapMap2 membership"))
      
    assert(vcf.infos == expectedInfos)
  }
}

object VcfFileSuite {
  private val fileFormat = "VCFv4.0"
  private val fileDate = "20090805"
  private val source = "myImputationProgramV3.1"
  private val reference = "1000GenomesPilot-NCBI36"
  private val phasing = "partial"

  //NB: From http://www.1000genomes.org/node/101
  private val validSampleFile = s"""
    ##fileformat=$fileFormat
##fileDate=$fileDate
##source=$source
##reference=$reference
##phasing=$phasing
##INFO=<ID=NS,Number=1,Type=Integer,Description="Number of Samples With Data">
##INFO=<ID=DP,Number=1,Type=Integer,Description="Total Depth">
##INFO=<ID=AF,Number=.,Type=Float,Description="Allele Frequency">
##INFO=<ID=AA,Number=1,Type=String,Description="Ancestral Allele">
##INFO=<ID=DB,Number=0,Type=Flag,Description="dbSNP membership, build 129">
##INFO=<ID=H2,Number=0,Type=Flag,Description="HapMap2 membership">
##FILTER=<ID=q10,Description="Quality below 10">
##FILTER=<ID=s50,Description="Less than 50% of samples have data">
##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
##FORMAT=<ID=GQ,Number=1,Type=Integer,Description="Genotype Quality">
##FORMAT=<ID=DP,Number=1,Type=Integer,Description="Read Depth">
##FORMAT=<ID=HQ,Number=2,Type=Integer,Description="Haplotype Quality">
#CHROM POS     ID        REF ALT    QUAL FILTER INFO                              FORMAT      NA00001        NA00002        NA00003
20     14370   rs6054257 G      A       29   PASS   NS=3;DP=14;AF=0.5;DB;H2           GT:GQ:DP:HQ 0|0:48:1:51,51 1|0:48:8:51,51 1/1:43:5:.,.
20     17330   .         T      A       3    q10    NS=3;DP=11;AF=0.017               GT:GQ:DP:HQ 0|0:49:3:58,50 0|1:3:5:65,3   0/0:41:3
20     1110696 rs6040355 A      G,T     67   PASS   NS=2;DP=10;AF=0.333,0.667;AA=T;DB GT:GQ:DP:HQ 1|2:21:6:23,27 2|1:2:0:18,2   2/2:35:4
20     1230237 .         T      .       47   PASS   NS=3;DP=13;AA=T                   GT:GQ:DP:HQ 0|0:54:7:56,60 0|0:48:4:51,51 0/0:61:2
20     1234567 microsat1 GTCT   G,GTACT 50   PASS   NS=3;DP=9;AA=G                    GT:GQ:DP    0/1:35:4       0/2:17:2       1/1:40:3
"""
}
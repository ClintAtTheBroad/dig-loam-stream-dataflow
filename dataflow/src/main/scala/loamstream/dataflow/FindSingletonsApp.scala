package loamstream.dataflow

import com.google.cloud.dataflow.sdk.Pipeline
import com.google.cloud.dataflow.sdk.io.TextIO
import com.google.cloud.dataflow.sdk.options.DirectPipelineOptions
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory
import com.google.cloud.dataflow.sdk.runners.DirectPipelineRunner
import com.google.cloud.dataflow.sdk.transforms.Create
import loamstream.dataflow.Conversions.Implicits
import loamstream.vcf.VcfFile
import com.google.cloud.dataflow.sdk.transforms.Filter
import com.google.cloud.dataflow.sdk.transforms.DoFn
import loamstream.vcf.Row
import com.google.cloud.dataflow.sdk.transforms.SerializableFunction
import com.google.cloud.dataflow.sdk.transforms.ParDo

object FindSingletonsApp extends App {
  println("Creating pipeline...")

  val options = PipelineOptionsFactory.create.as(classOf[DirectPipelineOptions])
  options.setProject("Scala Dataflow")
  options.setRunner(classOf[DirectPipelineRunner])
  options.setTestSerializability(true)
  options.setTestEncodability(true)
  options.setTestUnorderedness(true)

  val vcf = VcfFile.fromFile("example.vcf").get

  import Conversions.Implicits._

  val rows = Create.of(vcf.rows.toStream.asJavaIterable)

  val pipeline: Pipeline = Pipeline.create(options)

  pipeline.
    apply(rows).
    apply(ParDo.of(isSingletonRow)).
    apply(ParDo.of(extractRowId)).
    apply(TextIO.Write.to("singleton-variant-ids.txt"))

  pipeline.run()

  println("Pipeline run!")

  //A side-effecting DoFn feels wrong, but I couldn't get a Filter to work :\
  lazy val isSingletonRow: DoFn[Row, Row] = new DoFn[Row, Row] {
    //TODO: Dumb approach, almost certainly wrong, but illustrates the sort of thing we might do
    override def processElement(c: DoFn[Row, Row]#ProcessContext): Unit = {
      val row = c.element
      
      val numSamples = row.samples.size
      
      val numOnes = row.samples.count(_.startsWith("1"))
      
      val numZeroes = row.samples.count(_.startsWith("0"))
      
      val onlyOne1 = numOnes == 1
      
      val everythingElseIs0 = numZeroes == (numSamples - 1)
      
      if(onlyOne1 && everythingElseIs0) {
        c.output(row)
      }
    }
  }
  
  lazy val extractRowId: DoFn[Row, String] = new DoFn[Row, String] {
    override def processElement(c: DoFn[Row, String]#ProcessContext): Unit = {
      for {
        id <- c.element.id
      } {
        c.output(id.value)
      }
    }
  }
}

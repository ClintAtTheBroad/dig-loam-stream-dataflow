package loamstream.dataflow

import com.google.cloud.dataflow.sdk.Pipeline
import com.google.cloud.dataflow.sdk.io.TextIO
import com.google.cloud.dataflow.sdk.options.DirectPipelineOptions
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory
import com.google.cloud.dataflow.sdk.runners.DirectPipelineRunner
import com.google.cloud.dataflow.sdk.transforms.Create

import loamstream.vcf.VcfFile

object ExtractSampleIdsApp extends App {
  println("Creating pipeline...")

  val options = PipelineOptionsFactory.create.as(classOf[DirectPipelineOptions])
  options.setProject("Scala Dataflow")
  options.setRunner(classOf[DirectPipelineRunner])
  options.setTestSerializability(true)
  options.setTestEncodability(true)
  options.setTestUnorderedness(true)

  val vcf = VcfFile.fromFile("example.vcf").get
  
  import Conversions.Implicits._
  
  //Make an unbounded PCollection from the VCF's sample ids 
  //(not really needed, just a proof-of-concept)
  val sampleIds = Create.of(vcf.sampleIds.toStream.map(_.value).asJavaIterable)
  
  val pipeline: Pipeline = Pipeline.create(options)
  
  pipeline.
    apply(sampleIds).
    apply(TextIO.Write.to("sampleIds.txt"))

  pipeline.run()

  println("Pipeline run!")
}
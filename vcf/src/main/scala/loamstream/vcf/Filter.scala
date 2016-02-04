package loamstream.vcf

//##FILTER=<ID=ID,Description=”description”>
final case class Filter(id: Id, description: String)
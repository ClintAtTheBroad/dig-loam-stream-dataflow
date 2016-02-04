package loamstream.vcf

//##FORMAT=<ID=ID,Number=number,Type=type,Description=”description”>
final case class Format(id: Id, number: Int, tpe: Type, description: String)

object Format {
  val allowedTypes: Set[Type] = Set(Type.Integer, Type.Float, Type.Character, Type.String)
}

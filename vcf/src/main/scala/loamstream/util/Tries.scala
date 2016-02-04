package loamstream.util

import scala.collection.generic.CanBuildFrom
import scala.util.Try
import scala.util.Failure

/**
 * @author clint
 * date: Feb 3, 2016
 * 
 * TODO: extract this somewhere; it may be generally useful
 */
object Tries {
  import scala.language.higherKinds

  private def unwrapFailFast[T, C[T] <: Traversable[T]](attempts: C[Try[T]])(implicit cbf: CanBuildFrom[C[T], T, C[T]]): C[T] = {
    val builder = cbf()

    builder ++= attempts.map(_.get)

    builder.result
  }
  
  private def unwrapIgnoreFailures[T, C[T] <: Traversable[T]](attempts: C[Try[T]])(implicit cbf: CanBuildFrom[C[T], T, C[T]]): C[T] = {
    val builder = cbf()

    builder ++= attempts.flatMap(_.toOption)

    builder.result
  }

  def sequenceIgnoringFailures[T, C[T] <: Traversable[T]](attempts: C[Try[T]])(implicit cbf: CanBuildFrom[C[T], T, C[T]]): Try[C[T]] = {
    Try(unwrapIgnoreFailures(attempts))
  }

  def sequence[T, C[T] <: Traversable[T]](attempts: C[Try[T]])(implicit cbf: CanBuildFrom[C[T], T, C[T]]): Try[C[T]] = {

    val firstFailure: Option[Failure[T]] = attempts.find(_.isFailure).collect { case f: Failure[T] => f }

    firstFailure match {
      //re-package first failure, to avoid casting
      case Some(Failure(e)) => Failure(e)
      //Otherwise, there are no failures
      case _                => Try(unwrapFailFast(attempts))
    }
  }
  
  def failure[T](message: String): Failure[T] = Failure(new Exception(message))
}

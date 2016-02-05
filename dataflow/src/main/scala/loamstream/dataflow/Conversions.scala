package loamstream.dataflow

import com.google.cloud.dataflow.sdk.transforms.DoFn
import com.google.cloud.dataflow.sdk.coders.Coder
import com.google.cloud.dataflow.sdk.coders.CoderRegistry
import com.google.cloud.dataflow.sdk.values.TypeDescriptor
import com.google.cloud.dataflow.sdk.values.KV
import scala.reflect.ClassTag
import scala.collection.JavaConverters

object Conversions {

  import scala.language.implicitConversions

  object Implicits {
    import scala.language.implicitConversions

    implicit final class StreamsAreJavaIterables[T](val s: Stream[T]) extends AnyVal {
      import JavaConverters._
      
      def asJavaIterable: java.lang.Iterable[T] = new java.lang.Iterable[T] {
        override def iterator = s.iterator.asJava
      }
    }

    //NB: These generic conversions work, but fail when used in a pipeline, possibly because
    //The DataFlow runtime expects to be able to make something like a Type Token, a la 
    //http://gafter.blogspot.com/2006/12/super-type-tokens.html
    //but can't when type info is inferred. :( :( :(
    
    /*implicit def function1sAreDoFns[A, B](f: A => B): DoFn[A, B] = new DoFn[A, B] {
      override def processElement(c: DoFn[A, B]#ProcessContext): Unit = {
        c.output(f(c.element))

      }
    }

    implicit def function1sToIterablesAreDoFns[A, B](f: A => Iterable[B]): DoFn[A, B] = new DoFn[A, B] {
      override def processElement(c: DoFn[A, B]#ProcessContext): Unit = {
        f(c.element).foreach(c.output)
      }
    }*/
  }
}
package com.komlan.lab
package scala12steps

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import java.util.concurrent.ForkJoinPool
import scala.collection.BuildFrom

object ConconrentFuturesSequence  {
    def print(message: String): Unit = println(s"[${getClass.getSimpleName()}]: ${message}") 
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool(10))
    private def producer() = {
      val list = Seq (
        Future { print("startFirst"); Thread.sleep(3000); print("stopFirst")},
        Future { print("startSecond"); Thread.sleep(3000); print("stopSecond")},
      )
      Future.sequence(list)
    }
    def run(): Unit = 
      Await.result(producer(), 10.seconds)
      match {case _ => ()}
    

}

object SerialFuturesFlatMap {
    def print(message: String): Unit = println(s"[${getClass.getSimpleName()}]: ${message}") 
    private def producer()(implicit ec: ExecutionContext) = {
      
      Future { 
        print("startFirst")
        Thread.sleep(3000)
        print("stopFirst")
      }.flatMap{
          _ => Future { 
            print("startSecond")
            Thread.sleep(3000)
            print("stopSecond")
          }
      }
      
    }
    def run(implicit ec: ExecutionContext): Unit = {
      Await.result(producer(), 10.seconds)
    }
     
    
}
object FutureExt {
  def seq[A, M[X] <: IterableOnce[X]](in: M[() => Future[A]])(implicit cbf: BuildFrom[M[() => Future[A]], A, M[A]], executor: ExecutionContext): Future[M[A]] = {
    in.iterator.foldLeft(Future.successful(cbf.newBuilder(in))) {
      (fr, ffa) => for (r <- fr; a <- ffa()) yield (r += a)
    } map (_.result())
  }
}
object TestFuturesSequence2  {
    def print(message: String): Unit = println(s"[${getClass.getSimpleName()}]: ${message}") 
    //implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool(10))
    

    private def producer()(implicit ec: ExecutionContext) = {
      val list = Seq (
        {() => Future { print("startFirst"); Thread.sleep(3000); print("stopFirst")}},
        {() => Future { print("startSecond"); Thread.sleep(3000); print("stopSecond")}},
      )
      FutureExt.seq(list)
    }

    def run(implicit ec: ExecutionContext): Unit = 
      Await.result(producer(), 10.seconds)
      match {case _ => ()}
    
}




object Main extends App {
  println("─" * 50)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool(10))

  TestFuturesSequence2 run

  println("─" * 50)
}

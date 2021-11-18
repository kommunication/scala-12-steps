package com.komlan.lab
package utils

import scala.concurrent.{Future, ExecutionContext}
import scala.collection.BuildFrom



object FutureExt {
  def seq[A, M[X] <: IterableOnce[X]](in: M[() => Future[A]])(implicit cbf: BuildFrom[M[() => Future[A]], A, M[A]], executor: ExecutionContext): Future[M[A]] = {
    in.iterator.foldLeft(Future.successful(cbf.newBuilder(in))) {
      (fr, ffa) => for (r <- fr; a <- ffa()) yield (r += a)
    } map (_.result())
  }
}
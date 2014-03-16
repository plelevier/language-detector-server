package com.github.plelevier

import com.github.plelevier.filter.{LangDetectorHttpFilter, HandleExceptionFilter}
import com.twitter.finagle._
import com.twitter.conversions.time._
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import com.twitter.finagle.util.DefaultTimer
import com.twitter.finagle.service.TimeoutFilter
import com.twitter.finagle.http.{RichHttp, Response, Request}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.github.plelevier.service.LangDetectorService
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.path._
import com.twitter.finagle.builder.ServerBuilder
import java.net.InetSocketAddress

object LangDetectServer extends TwitterServer {

  implicit val timer = DefaultTimer.twitter

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.setSerializationInclusion(Include.NON_NULL)

  val timeoutOption = flag("timeout", "3", "Response timeout (in seconds)")
  val httpPort = flag("http.port", new InetSocketAddress(8080), "http server port")

  def main() {
    val timeout = java.lang.Integer.parseInt(timeoutOption()).second
    val timeoutFilter = new TimeoutFilter[Request, Response](timeout, new IndividualRequestTimeoutException(timeout), timer)

    val handleExceptions = new HandleExceptionFilter(mapper)
    val serviceFilter = handleExceptions andThen timeoutFilter
    val httpFilter = new LangDetectorHttpFilter(mapper)
    val langDetectorService = httpFilter andThen new LangDetectorService

    val routingService = RoutingService.byPathObject {
      case Root / "lang-detector" => langDetectorService
    }

    val langDetectorServer = ServerBuilder()
      .codec(RichHttp[Request](http.Http()))
      .bindTo(httpPort())
      .name("langDetectorServer")
      .build(serviceFilter andThen routingService)

    onExit {
      langDetectorServer.close()
    }
  }
}

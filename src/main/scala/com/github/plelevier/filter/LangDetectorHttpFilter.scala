package com.github.plelevier.filter

import com.twitter.finagle.{Service, Filter}
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import com.twitter.util.Future
import org.jboss.netty.util.CharsetUtil._
import org.jboss.netty.buffer.ChannelBuffers._
import com.twitter.finagle.http.{Request, Response}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.plelevier.request.LangRequest
import com.github.plelevier.response.LangResponse

class LangDetectorHttpFilter(mapper: ScalaObjectMapper) extends Filter[Request, Response, List[LangRequest], List[LangResponse]] {
  def apply(request: Request, service: Service[List[LangRequest], List[LangResponse]]) = {

    Future(mapper.readValue[List[LangRequest]](request.getContent.toString(UTF_8))) flatMap { serviceRequest =>

      service(serviceRequest) map { response =>
        val httpResponse = Response(request.getProtocolVersion, HttpResponseStatus.OK)
        httpResponse.setContent(copiedBuffer(mapper.writerWithType[List[LangResponse]].writeValueAsString(response), UTF_8))
        httpResponse
      }
    } rescue {
      case e: Exception =>
        Future.exception(e)
    }
  }
}
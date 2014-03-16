package com.github.plelevier.filter

import com.twitter.finagle._
import org.jboss.netty.buffer.ChannelBuffers
import com.fasterxml.jackson.databind.ObjectMapper

import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.finagle.http.{Request, Response}

case class ErrorData(val error: String, val stackTrace: Option[List[String]])

class HandleExceptionFilter(val mapper: ObjectMapper) extends SimpleFilter[Request, Response] {

  def apply(request: Request, service: Service[Request, Response]) = {
    service(request) handle { case error =>
      val statusCode = error match {
        case _: IllegalArgumentException =>
          HttpResponseStatus.FORBIDDEN
        case _ =>
          HttpResponseStatus.INTERNAL_SERVER_ERROR
      }

      val errorResponse = Response(request.getProtocolVersion, statusCode)
      errorResponse.setContent(ChannelBuffers.copiedBuffer(
        mapper.writeValueAsString(
          ErrorData(
            error.getClass.getSimpleName + " : " + error.getMessage,
            Some(error.getStackTraceString.split("\n").toList))
        ), UTF_8))

      errorResponse
    }
  }
}
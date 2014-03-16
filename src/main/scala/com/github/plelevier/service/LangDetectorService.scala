package com.github.plelevier.service

import scala.language.implicitConversions
import scala.collection.JavaConversions._

import com.twitter.finagle.Service
import com.github.plelevier.langdetect.LangDetectorHelper
import com.github.plelevier.request.LangRequest
import com.github.plelevier.response.{LangResponse, LangProbability}
import com.twitter.util.Future
import com.cybozu.labs.langdetect.DetectorFactory


class LangDetectorService extends Service[List[LangRequest], List[LangResponse]] {
  LangDetectorHelper.loadProfileFromResources("profiles/")

  def getLangProbabilities(text: String): List[LangProbability] = {
    val detector = DetectorFactory.create()
    detector.append(text)
    detector.getProbabilities.toList.map { lang =>
      LangProbability(lang.lang, lang.prob)
    }
  }

  def apply(request: List[LangRequest]) = {
    val response = request.map { langRequest =>
      LangResponse(langRequest.id, getLangProbabilities(langRequest.text))
    }
    Future.value(response)
  }
}
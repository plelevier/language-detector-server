package com.github.plelevier.response

case class LangProbability(lang: String, prob: Double)
case class LangResponse(id: String, result: List[LangProbability])

/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils.countryOptions

import com.typesafe.config.ConfigException
import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import play.api.Environment
import play.api.i18n.Messages
import play.api.libs.json.Json
import utils.InputOption


@Singleton
class CountryOptions @Inject()(environment: Environment, config: FrontendAppConfig) {

  def options()(implicit messages: Messages): Seq[InputOption] = {
    CountryOptions.getCountries(environment, getFileName)
  }

  def getFileName()(implicit messages: Messages) = {
    val isWelsh = messages.lang.code == config.WELSH
    if (isWelsh) config.locationCanonicalListCY else config.locationCanonicalList
  }

}

object CountryOptions {

  def getCountries(environment: Environment, fileName: String): Seq[InputOption] = {
    environment.resourceAsStream(fileName).flatMap {
      in =>
        val locationJsValue = Json.parse(in)
        Json.fromJson[Seq[Seq[String]]](locationJsValue).asOpt.map {
          _.map { countryList =>
            InputOption(countryList(1).replaceAll("country:", ""), countryList.head)
          }.sortBy(x => x.label.toLowerCase)
        }
    }.getOrElse {
      throw new ConfigException.BadValue(fileName, "country json does not exist")
    }
  }

}

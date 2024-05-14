/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
import com.typesafe.config.ConfigException
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.{Lang, MessagesApi, MessagesImpl}
import utils.InputOption

class CountryOptionsSpec extends SpecBase with MockitoSugar {

  "Country Options" must {

    "build correctly the English InputOptions with all country list and country code" in {

      val application = applicationBuilder()
        .configure(Map(
          "location.canonical.list.all" -> "countries-canonical-list-test.json"
        ))
        .build()

      val messagesApi = app.injector.instanceOf[MessagesApi]
      implicit val messages: MessagesImpl = MessagesImpl(lang = Lang(ENGLISH), messagesApi = messagesApi)

      val countryOption: CountryOptions = application.injector.instanceOf[CountryOptions]
      countryOption.options() mustEqual Seq(InputOption("ES", "Spain"), InputOption("GB", "United Kingdom"))

      application.stop()
    }

    "build correctly the Welsh InputOptions with all country list and country code" in {

      val application = applicationBuilder()
        .configure(Map(
          "location.canonical.list.allCY" -> "countries-canonical-list-test-cy.json"
        ))
        .build()

      val messagesApi = app.injector.instanceOf[MessagesApi]
      implicit val messages: MessagesImpl = MessagesImpl(lang = Lang(WELSH), messagesApi = messagesApi)

      val countryOption: CountryOptions = application.injector.instanceOf[CountryOptions]
      countryOption.options() mustEqual Seq(InputOption("ES", "Sbaen"), InputOption("GB", "Y Deyrnas Unedig"))

      application.stop()
    }

    "throw the error if the country json does not exist" in {

      val application = applicationBuilder()
        .configure(Map(
          "location.canonical.list.all" -> "countries-canonical-test.json"
        ))
        .build()

      an[ConfigException.BadValue] shouldBe thrownBy {
        application.injector.instanceOf[CountryOptions].options()
      }

      application.stop()
    }
  }
}

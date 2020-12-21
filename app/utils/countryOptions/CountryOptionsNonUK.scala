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

import com.google.inject.Inject
import config.FrontendAppConfig
import javax.inject.Singleton
import play.api.Environment
import play.api.i18n.Messages
import utils.InputOption

@Singleton
class CountryOptionsNonUK @Inject()(
                                     environment: Environment,
                                     config: FrontendAppConfig
                                   ) extends CountryOptions(environment, config) {
  override def options()(implicit messages: Messages): Seq[InputOption] = {
    CountryOptions.getCountries(environment, getFileName).filterNot(x => x.value == config.UK_COUNTRY_CODE)
  }
}

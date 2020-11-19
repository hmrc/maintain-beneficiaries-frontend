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

package utils.print

import java.time.LocalDate

import com.google.inject.Inject
import models.beneficiaries.RoleInCompany
import models.beneficiaries.RoleInCompany.NA
import models.{Address, CombinedPassportOrIdCard, Description, HowManyBeneficiaries, IdCard, IdentificationDetailOptions, Name, Passport, UserAnswers}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.HtmlFormat
import queries.Gettable
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

class AnswerRowConverter @Inject()(checkAnswersFormatters: CheckAnswersFormatters) {

  def bind(userAnswers: UserAnswers, name: String, countryOptions: CountryOptions)
          (implicit messages: Messages): Bound = new Bound(userAnswers, name, countryOptions)

  class Bound(userAnswers: UserAnswers, name: String, countryOptions: CountryOptions)(implicit messages: Messages) {

    def nameQuestion(query: Gettable[Name],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel")),
          HtmlFormat.escape(x.displayFullName),
          changeUrl
        )
      }
    }

    def roleInCompanyQuestion(query: Gettable[RoleInCompany],
                              labelKey: String,
                              changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          x match {
            case NA => HtmlFormat.escape(messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel.na"))
            case _ => HtmlFormat.escape(messages(s"individualBeneficiary.roleInCompany.$x"))
          },
          changeUrl
        )
      }
    }

    def stringQuestion(query: Gettable[String],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          HtmlFormat.escape(x),
          changeUrl
        )
      }
    }

    def percentageQuestion(query: Gettable[Int],
                           labelKey: String,
                           changeUrl: String)
                          (implicit messages:Messages): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.percentage(x),
          changeUrl
        )
      }
    }

    def intQuestion(query: Gettable[Int],
                       labelKey: String,
                       changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          HtmlFormat.escape(x.toString),
          changeUrl
        )
      }
    }

    def yesNoQuestion(query: Gettable[Boolean],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.yesOrNo(x),
          changeUrl
        )
      }
    }

    def dateQuestion(query: Gettable[LocalDate],
                     labelKey: String,
                     changeUrl: String)
                    (implicit messages: Messages): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          HtmlFormat.escape(checkAnswersFormatters.formatDate(x)),
          changeUrl
        )
      }
    }

    def ninoQuestion(query: Gettable[String],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.formatNino(x),
          changeUrl
        )
      }
    }

    def addressQuestion[T <: Address](query: Gettable[T],
                                      labelKey: String,
                                      changeUrl: String)
                                     (implicit messages: Messages, reads: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.formatAddress(x, countryOptions),
          changeUrl
        )
      }
    }

    def identificationOptionsQuestion(query: Gettable[IdentificationDetailOptions],
                                      labelKey: String,
                                      changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.formatIdentificationDetails(x),
          changeUrl
        )
      }
    }

    def passportOrIdCardDetailsQuestion(query: Gettable[CombinedPassportOrIdCard],
                                        labelKey: String,
                                        changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.formatPassportOrIdCardDetails(x, countryOptions),
          changeUrl
        )
      }
    }

    def passportDetailsQuestion(query: Gettable[Passport],
                                labelKey: String,
                                changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.formatPassportDetails(x, countryOptions),
          changeUrl
        )
      }
    }

    def idCardDetailsQuestion(query: Gettable[IdCard],
                              labelKey: String,
                              changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.formatIdCardDetails(x, countryOptions),
          changeUrl
        )
      }
    }

    def descriptionQuestion(query: Gettable[Description],
                                    labelKey: String,
                                    changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel")),
          checkAnswersFormatters.formatDescription(x),
          changeUrl
        )
      }
    }

    def numberOfBeneficiariesQuestion(query: Gettable[HowManyBeneficiaries],
                                      labelKey: String,
                                      changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", name)),
          checkAnswersFormatters.formatNumberOfBeneficiaries(x),
          changeUrl
        )
      }
    }
  }
}

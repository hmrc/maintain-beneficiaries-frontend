/*
 * Copyright 2022 HM Revenue & Customs
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

import models._
import models.beneficiaries.RoleInCompany
import models.beneficiaries.RoleInCompany.NA
import play.api.i18n.Messages
import play.twirl.api.Html
import play.twirl.api.HtmlFormat.escape
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.language.LanguageUtils
import utils.countryOptions.CountryOptions

import java.time.{LocalDate => JavaDate}
import javax.inject.Inject
import scala.util.Try

class CheckAnswersFormatters @Inject()(languageUtils: LanguageUtils,
                                       countryOptions: CountryOptions) {

  def formatDate(date: JavaDate)(implicit messages: Messages): Html = {
    escape(languageUtils.Dates.formatDate(date))
  }

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      escape(messages("site.yes"))
    } else {
      escape(messages("site.no"))
    }
  }

  def formatNino(nino: String): Html = {
    val formatted = Try(Nino(nino).formatted).getOrElse(nino)
    escape(formatted)
  }

  def formatAddress(address: Address)(implicit messages: Messages): Html = {
    address match {
      case a: UkAddress => formatUkAddress(a)
      case a: NonUkAddress => formatNonUkAddress(a)
    }
  }

  private def formatUkAddress(address: UkAddress): Html = {
    val lines =
      Seq(
        Some(escape(address.line1)),
        Some(escape(address.line2)),
        address.line3.map(escape),
        address.line4.map(escape),
        Some(escape(address.postcode))
      ).flatten

    breakLines(lines)
  }

  private def formatNonUkAddress(address: NonUkAddress)(implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(escape(address.line1)),
        Some(escape(address.line2)),
        address.line3.map(escape),
        Some(country(address.country))
      ).flatten

    breakLines(lines)
  }

  def country(code: String)(implicit messages: Messages): Html =
    escape(countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse(""))

  def formatPassportOrIdCardDetails(passportOrIdCard: CombinedPassportOrIdCard)
                                   (implicit messages: Messages): Html = {

    def formatNumber(number: String): String = if (passportOrIdCard.detailsType.isProvisional) {
      number
    } else {
      messages("site.number-ending", number.takeRight(4))
    }

    val lines =
      Seq(
        Some(country(passportOrIdCard.countryOfIssue)),
        Some(escape(formatNumber(passportOrIdCard.number))),
        Some(formatDate(passportOrIdCard.expirationDate))
      ).flatten

    breakLines(lines)
  }


  def formatPassportDetails(passport: Passport)
                           (implicit messages: Messages): Html = {
    formatPassportOrIdCardDetails(passport.asCombined)
  }

  def formatIdCardDetails(idCard: IdCard)
                         (implicit messages: Messages): Html = {
    formatPassportOrIdCardDetails(idCard.asCombined)
  }

  def percentage(value: Int): Html = escape(s"$value%")

  def formatDescription(description: Description): Html = {
    val lines =
      Seq(
        Some(escape(description.description)),
        description.description1.map(escape),
        description.description2.map(escape),
        description.description3.map(escape),
        description.description4.map(escape)
      ).flatten

    breakLines(lines)
  }

  def formatNumberOfBeneficiaries(answer: HowManyBeneficiaries)(implicit messages: Messages): Html = {
    formatEnum("numberOfBeneficiaries", answer)
  }

  def formatRoleInCompany(answer: RoleInCompany)(implicit messages: Messages): Html = {
    answer match {
      case NA => escape(messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel.na"))
      case _ => formatEnum("individualBeneficiary.roleInCompany", answer)
    }
  }

  def formatEnum[T](key: String, answer: T)(implicit messages: Messages): Html = {
    escape(messages(s"$key.$answer"))
  }

  private def breakLines(lines: Seq[Html]): Html = {
    Html(lines.mkString("<br />"))
  }

}

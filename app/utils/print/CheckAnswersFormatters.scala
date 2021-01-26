/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time.{LocalDate => JavaDate}

import javax.inject.Inject
import models.{Address, CombinedPassportOrIdCard, Description, HowManyBeneficiaries, IdCard, IdentificationDetailOptions, NonUkAddress, Passport, UkAddress}
import org.joda.time.{LocalDate => JodaDate}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.language.LanguageUtils
import utils.countryOptions.CountryOptions

import scala.util.Try

class CheckAnswersFormatters @Inject()(languageUtils: LanguageUtils) {

  def formatDate(date: JavaDate)(implicit messages: Messages): String = {
    val convertedDate: JodaDate = new JodaDate(date.getYear, date.getMonthValue, date.getDayOfMonth)
    languageUtils.Dates.formatDate(convertedDate)
  }

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }
  }

  def formatNino(nino: String): Html = {
    val formatted = Try(Nino(nino).formatted).getOrElse(nino)
    HtmlFormat.escape(formatted)
  }

  def formatAddress(address: Address, countryOptions: CountryOptions)(implicit messages: Messages): Html = {
    address match {
      case a: UkAddress => formatUkAddress(a)
      case a: NonUkAddress => formatNonUkAddress(a, countryOptions)
    }
  }

  private def formatUkAddress(address: UkAddress): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        address.line4.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(address.postcode))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  private def formatNonUkAddress(address: NonUkAddress, countryOptions: CountryOptions)(implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        Some(country(address.country, countryOptions))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  private def country(code: String, countryOptions: CountryOptions)(implicit messages: Messages): String =
    countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse("")

  def formatIdentificationDetails(identificationDetailOptions: IdentificationDetailOptions): Html = {
    identificationDetailOptions match {
      case IdentificationDetailOptions.IdCard => HtmlFormat.escape("ID card")
      case IdentificationDetailOptions.Passport => HtmlFormat.escape("Passport")
    }
  }

  def formatPassportOrIdCardDetails(id: CombinedPassportOrIdCard, countryOptions: CountryOptions)
                                   (implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(country(id.countryOfIssue, countryOptions)),
        Some(HtmlFormat.escape(id.number)),
        Some(HtmlFormat.escape(formatDate(id.expirationDate)))
      ).flatten

    Html(lines.mkString("<br />"))
  }


  def formatPassportDetails(passport: Passport, countryOptions: CountryOptions)
                           (implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(country(passport.countryOfIssue, countryOptions)),
        Some(HtmlFormat.escape(passport.number)),
        Some(HtmlFormat.escape(formatDate(passport.expirationDate)))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def formatIdCardDetails(idCard: IdCard, countryOptions: CountryOptions)
                         (implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(country(idCard.countryOfIssue, countryOptions)),
        Some(HtmlFormat.escape(idCard.number)),
        Some(HtmlFormat.escape(formatDate(idCard.expirationDate)))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def percentage(value: Int): Html = HtmlFormat.escape(s"$value%")

  def formatDescription(description: Description): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(description.description)),
        description.description1.map(HtmlFormat.escape),
        description.description2.map(HtmlFormat.escape),
        description.description3.map(HtmlFormat.escape),
        description.description4.map(HtmlFormat.escape)
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def formatNumberOfBeneficiaries(answer: HowManyBeneficiaries)(implicit messages: Messages): Html = {
    HtmlFormat.escape(messages(s"numberOfBeneficiaries.$answer"))
  }

}

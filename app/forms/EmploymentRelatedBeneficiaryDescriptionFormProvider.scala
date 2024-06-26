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

package forms

import forms.helpers.WhitespaceHelper._
import forms.mappings.Mappings

import javax.inject.Inject
import models.Description
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

class EmploymentRelatedBeneficiaryDescriptionFormProvider @Inject() extends Mappings {

  def withPrefix(prefix: String): Form[Description] = Form(
    mapping(
      "description" -> text(s"$prefix.error.required")
        .verifying(
          firstError(
            maxLength(70, s"$prefix.error.length"),
            nonEmptyString("description", s"$prefix.error.required"),
            regexp(Validation.descriptionRegex, s"$prefix.error.invalid")
          )
        ),
      "description1" -> optional(text()
        .transform(trimWhitespace, identity[String])
        .verifying(
          firstError(
            maxLength(70, s"$prefix.error.length1"),
            regexp(Validation.descriptionRegex, s"$prefix.error.invalid1")
          )
        )
      ).transform(emptyToNone, identity[Option[String]]),
      "description2" -> optional(text()
        .transform(trimWhitespace, identity[String])
        .verifying(
          firstError(
            maxLength(70, s"$prefix.error.length2"),
            regexp(Validation.descriptionRegex, s"$prefix.error.invalid2")
          )
        )
      ).transform(emptyToNone, identity[Option[String]]),
      "description3" -> optional(text()
        .transform(trimWhitespace, identity[String])
        .verifying(
          firstError(
            maxLength(70, s"$prefix.error.length3"),
            regexp(Validation.descriptionRegex, s"$prefix.error.invalid3")
          )
        )
      ).transform(emptyToNone, identity[Option[String]]),
      "description4" -> optional(text()
        .transform(trimWhitespace, identity[String])
        .verifying(
          firstError(
            maxLength(70, s"$prefix.error.length4"),
            regexp(Validation.descriptionRegex, s"$prefix.error.invalid4")
          )
        )
      ).transform(emptyToNone, identity[Option[String]])
    )(Description.apply)(Description.unapply)
  )
}

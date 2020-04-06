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

package utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

class DateUtils {

  private val format = "d MMMM yyyy"

  def formatDate(dateTime: LocalDateTime): String = {
    val dateFormatter = DateTimeFormatter.ofPattern(format)
    dateTime.format(dateFormatter)
  }

  def maxDate(dates: List[LocalDate]): LocalDate = {
    def max(d1: LocalDate, d2: LocalDate): LocalDate = if (d1.compareTo(d2) > 0) d1 else d2
    dates.reduceLeft(max)
  }

}

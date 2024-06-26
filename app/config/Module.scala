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

package config

import com.google.inject.AbstractModule
import config.annotations._
import controllers.actions._
import navigation._
import services.{AuthenticationService, AuthenticationServiceImpl}

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[DataRetrievalAction]).to(classOf[DataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()

    // For session based storage instead of cred based, change to SessionIdentifierAction
    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction]).asEagerSingleton()
    bind(classOf[AuthenticationService]).to(classOf[AuthenticationServiceImpl]).asEagerSingleton()

    bind(classOf[Navigator]).annotatedWith(classOf[ClassOfBeneficiary]).to(classOf[ClassOfBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[IndividualBeneficiary]).to(classOf[IndividualBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[CharityBeneficiary]).to(classOf[CharityBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[OtherBeneficiary]).to(classOf[OtherBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[CompanyBeneficiary]).to(classOf[CompanyBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[TrustBeneficiary]).to(classOf[TrustBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[EmploymentRelatedBeneficiary]).to(classOf[EmploymentRelatedBeneficiaryNavigator]).asEagerSingleton()

  }
}

/*
 * Copyright 2018 HM Revenue & Customs
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

package services

import connectors.{AtedConnector, DataCacheConnector}
import models._
import org.joda.time.DateTime
import play.api.Logger
import play.api.http.Status._
import utils.AtedConstants._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

trait DisposeLiabilityReturnService {

  def atedConnector: AtedConnector

  def dataCacheConnector: DataCacheConnector

  def retrieveLiabilityReturn(oldFormBundleNo: String)
                             (implicit atedContext: AtedContext, hc: HeaderCarrier): Future[Option[DisposeLiabilityReturn]] = {
    atedConnector.retrieveAndCacheDisposeLiability(oldFormBundleNo) map {
      response => response.status match {
        case OK => response.json.asOpt[DisposeLiabilityReturn]
        case status =>
          Logger.warn(s"[DisposeLiabilityReturnService][retrieveLiabilityReturn] - status : $status, body = ${response.body}")
          None
      }
    }
  }

  def cacheDisposeLiabilityReturnDate(oldFormBundleNo: String, updatedDate: DisposeLiability)
                                     (implicit atedContext: AtedContext, hc: HeaderCarrier): Future[Option[DisposeLiabilityReturn]] = {
    atedConnector.cacheDraftDisposeLiabilityReturnDate(oldFormBundleNo, updatedDate) map {
      response => response.status match {
        case OK => response.json.asOpt[DisposeLiabilityReturn]
        case status => None
      }
    }
  }

  def cacheDisposeLiabilityReturnHasBankDetails(oldFormBundleNo: String, hasBankDetails: Boolean)
                                     (implicit atedContext: AtedContext, hc: HeaderCarrier): Future[Option[DisposeLiabilityReturn]] = {
    atedConnector.cacheDraftDisposeLiabilityReturnHasBank(oldFormBundleNo, hasBankDetails) map {
      response => response.status match {
        case OK => response.json.asOpt[DisposeLiabilityReturn]
        case status => None
      }
    }
  }

  def cacheDisposeLiabilityReturnBank(oldFormBundleNo: String, updatedValue: BankDetails)
                                     (implicit atedContext: AtedContext, hc: HeaderCarrier): Future[Option[DisposeLiabilityReturn]] = {
    atedConnector.cacheDraftDisposeLiabilityReturnBank(oldFormBundleNo, updatedValue) map {
      response => response.status match {
        case OK => response.json.asOpt[DisposeLiabilityReturn]
        case status => None
      }
    }
  }

  def calculateDraftDisposal(oldFormBundleNo: String)
                                     (implicit atedContext: AtedContext, hc: HeaderCarrier): Future[Option[DisposeLiabilityReturn]] = {
    atedConnector.calculateDraftDisposal(oldFormBundleNo) map {
      response => response.status match {
        case OK => response.json.asOpt[DisposeLiabilityReturn]
        case status => None
      }
    }
  }

  def submitDraftDisposeLiability(oldFormBundleNo: String)(implicit atedContext: AtedContext, hc: HeaderCarrier): Future[EditLiabilityReturnsResponseModel] = {
    atedConnector.submitDraftDisposeLiabilityReturn(oldFormBundleNo) flatMap {
      disposeLiabilityResponse => disposeLiabilityResponse.status match {
        case OK =>
          dataCacheConnector.clearCache() flatMap { response =>
            dataCacheConnector.saveFormData[EditLiabilityReturnsResponseModel](formId = SubmitEditedLiabilityReturnsResponseFormId,
              data = disposeLiabilityResponse.json.as[EditLiabilityReturnsResponseModel])
          }
        case status => Future.successful(EditLiabilityReturnsResponseModel(DateTime.now(), Nil, BigDecimal(0.00)))
      }
    }
  }

}

object DisposeLiabilityReturnService extends DisposeLiabilityReturnService {
  val atedConnector = AtedConnector
  val dataCacheConnector = DataCacheConnector
}

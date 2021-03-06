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

package views.propertyDetails

import java.util.UUID

import builders.AuthBuilder._
import builders.{PropertyDetailsBuilder, TitleBuilder}
import forms.PropertyDetailsForms._
import org.jsoup.Jsoup
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FeatureSpec, GivenWhenThen}
import org.scalatestplus.play.OneServerPerSuite
import play.api.test.FakeRequest
import utils.AtedUtils

class PropertyDetailsAddressSpec extends FeatureSpec with OneServerPerSuite with MockitoSugar with BeforeAndAfterEach with GivenWhenThen{

  val userId = s"user-${UUID.randomUUID}"
  implicit val user = createAtedContext(createUserAuthContext(userId, "name"))
  implicit val messages : play.api.i18n.Messages = play.api.i18n.Messages.Implicits.applicationMessages

  feature("The user can view an empty property details page") {

    info("as a user I want to view the correct page content")

    scenario("user has visited the page for the first time") {

      Given("A user visits the page")
      When("The user views the page")
      implicit val request = FakeRequest()

      val html = views.html.propertyDetails.propertyDetailsAddress(None, 2015, propertyDetailsAddressForm, None, None)

      val document = Jsoup.parse(html.toString())
      Then("Enter your property details")
      assert(document.title() === TitleBuilder.buildTitle("Enter the address of the property manually"))

      And("The pre-header text is - Manage your ATED service")
      assert(document.getElementById("property-details-header").text() === "Enter the address of the property manually")

      And("The the link to the lookup pages text is - Lookup address")
      assert(document.getElementById("lookup-address-link").text() === "Lookup address")
      assert(document.getElementById("lookup-address-link").attr("href") === "/ated/liability/create/address/lookup/2015")

      assert(document.getElementById("line_1_field").text() === "Address line 1")
      assert(document.getElementById("line_1").attr("value") === "")
      assert(document.getElementById("line_2_field").text() === "Address line 2")
      assert(document.getElementById("line_2").attr("value") === "")
      assert(document.getElementById("line_3_field").text() === "Address line 3 (optional)")
      assert(document.getElementById("line_3").attr("value") === "")
      assert(document.getElementById("line_4_field").text() === "Address line 4 (optional)")
      assert(document.getElementById("line_4").attr("value") === "")
      assert(document.getElementById("postcode_field").text() === "Postcode (optional)")
      assert(document.getElementById("postcode").attr("value") === "")
      assert(document.getElementById("submit").text() === "Save and continue")

      Then("The back link is correct")
      assert(document.getElementById("backLinkHref") === null)

    }

    scenario("user has visited the page to edit data") {

      Given("A user visits the page to edit data")
      When("The user views the page to edit data")
      implicit val request = FakeRequest()

      val propertyDetails = PropertyDetailsBuilder.getPropertyDetailsAddress(Some("postCode"))
      val html = views.html.propertyDetails.propertyDetailsAddress(Some("1"), 2015, propertyDetailsAddressForm.fill(propertyDetails), Some(AtedUtils.EDIT_SUBMITTED), Some("http://backLink"))

      val document = Jsoup.parse(html.toString())
      Then("Enter your property details")
      assert(document.title() === TitleBuilder.buildTitle("Enter the address of the property manually"))

      And("The pre-header text is - Manage your ATED service")
      assert(document.getElementById("property-details-header").text() === "Enter the address of the property manually")

      And("The the link to the lookup pages text is - Lookup address")
      assert(document.getElementById("lookup-address-link").text() === "Lookup address")
      assert(document.getElementById("lookup-address-link").attr("href") === "/ated/liability/create/address/lookup/2015?propertyKey=1&mode=editSubmitted")

      assert(document.getElementById("line_1").attr("value") === "addr1")
      assert(document.getElementById("line_2").attr("value") === "addr2")
      assert(document.getElementById("line_3").attr("value") === "addr3")
      assert(document.getElementById("line_4").attr("value") === "addr4")
      assert(document.getElementById("postcode").attr("value") === "postCode")
      assert(document.getElementById("submit").text() === "Save and continue")

      Then("The back link is correct")
      assert(document.getElementById("backLinkHref").text === "Back")
      assert(document.getElementById("backLinkHref").attr("href") === "http://backLink")

    }


  }
}

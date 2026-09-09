import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser('')

WebUI.maximizeWindow()

WebUI.navigateToUrl('https://hebronmart.com/مراية-عروس-170')

WebUI.waitForPageLoad(30)

WebUI.click(findTestObject('Object Repository/Wishlist/cookie_Close'), FailureHandling.OPTIONAL)

WebUI.verifyElementPresent(findTestObject('Object Repository/Wishlist/i_HeartDetail'), 10)

WebUI.verifyElementAttributeValue(findTestObject('Object Repository/Wishlist/i_HeartDetail'), 'class', 'icon-heart-o', 10)

capturedTitle = WebUI.getText(findTestObject('Object Repository/Wishlist/lbl_DetailTitle'))

capturedPrice = WebUI.getText(findTestObject('Object Repository/Wishlist/lbl_DetailPrice'))

WebUI.click(findTestObject('Object Repository/Wishlist/btn_Favorite'))

WebUI.verifyElementAttributeValue(findTestObject('Object Repository/Wishlist/i_HeartDetail'), 'class', 'icon-heart', 10)

WebUI.navigateToUrl('https://hebronmart.com/wishlist')

WebUI.waitForPageLoad(30)

WebUI.verifyElementPresent(findTestObject('Object Repository/Wishlist/lbl_WishlistTitle'), 20)

WebUI.verifyElementText(findTestObject('Object Repository/Wishlist/lbl_WishlistTitle'), capturedTitle)

WebUI.verifyElementText(findTestObject('Object Repository/Wishlist/lbl_WishlistPrice'), capturedPrice)

WebUI.navigateToUrl('https://hebronmart.com/مراية-عروس-170')

WebUI.waitForPageLoad(30)

WebUI.verifyElementAttributeValue(findTestObject('Object Repository/Wishlist/i_HeartDetail'), 'class', 'icon-heart', 10)

WebUI.click(findTestObject('Object Repository/Wishlist/btn_Favorite'))

WebUI.verifyElementAttributeValue(findTestObject('Object Repository/Wishlist/i_HeartDetail'), 'class', 'icon-heart-o', 10)

WebUI.navigateToUrl('https://hebronmart.com/wishlist')

WebUI.waitForPageLoad(30)

WebUI.verifyElementPresent(findTestObject('Object Repository/Wishlist/lbl_EmptyWishlist'), 20)

WebUI.closeBrowser()


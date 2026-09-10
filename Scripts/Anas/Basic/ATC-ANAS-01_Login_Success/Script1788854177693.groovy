import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.openBrowser('')
WebUI.navigateToUrl('https://hebronmart.com')
WebUI.click(findTestObject('Object Repository/Login/btn_login_open'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Login/inp_email'), 10)
WebUI.setText(findTestObject('Object Repository/Login/inp_email'), 'anasshalabi429@gmail.com')
WebUI.setText(findTestObject('Object Repository/Login/inp_password'), password)
WebUI.click(findTestObject('Object Repository/Login/btn_login_submit'))
WebUI.waitForPageLoad(10)

WebUI.verifyTextPresent('Anas Shalabi', false)

WebUI.closeBrowser()
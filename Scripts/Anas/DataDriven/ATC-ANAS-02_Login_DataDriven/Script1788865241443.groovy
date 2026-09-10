import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

String rowCaseId = caseId?.toString()?.trim()
String rowEmail = email?.toString()?.trim()
String rowPassword = password == null ? '' : password.toString()
String rowExpectedResult = expectedResult?.toString()?.trim()?.toUpperCase()

assert rowCaseId : 'The bound caseId must not be empty.'
assert rowEmail : "The bound email must not be empty for ${rowCaseId}."
assert rowExpectedResult in ['SUCCESS', 'FAIL'] : "Unsupported expectedResult '${rowExpectedResult}' for ${rowCaseId}."

try {
    WebUI.openBrowser('')
    WebUI.navigateToUrl('https://hebronmart.com')
    WebUI.maximizeWindow()

    TestObject loginOpen = findTestObject('Object Repository/Login/btn_login_open')
    TestObject emailInput = findTestObject('Object Repository/Login/inp_email')
    TestObject passwordInput = findTestObject('Object Repository/Login/inp_password')
    TestObject loginSubmit = findTestObject('Object Repository/Login/btn_login_submit')

    assert WebUI.waitForElementVisible(loginOpen, 20) : "Login link was not visible for ${rowCaseId}."
    WebUI.click(loginOpen)

    assert WebUI.waitForElementVisible(emailInput, 20) : "Email field was not visible for ${rowCaseId}."
    assert WebUI.waitForElementVisible(passwordInput, 20) : "Password field was not visible for ${rowCaseId}."
    WebUI.setText(emailInput, rowEmail)
    WebUI.setText(passwordInput, rowPassword)

    assert WebUI.waitForElementClickable(loginSubmit, 20) : "Login submit button was not clickable for ${rowCaseId}."
    WebUI.click(loginSubmit)
    WebUI.waitForPageLoad(20)

    if (rowExpectedResult == 'SUCCESS') {
        WebUI.verifyTextPresent('Anas Shalabi', false)
    } else {
        WebUI.verifyTextNotPresent('Anas Shalabi', false)
    }
} finally {
    WebUI.closeBrowser(FailureHandling.OPTIONAL)
}

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.openBrowser('')

try {
    WebUI.navigateToUrl('https://hebronmart.com')

    WebUI.maximizeWindow()

    WebUI.delay(2)

    // =====================================================
    // POSITIVE TEST - NAME DATA-DRIVEN
    // name comes from Test Data
    // abdalah
    // hani
    // =====================================================
    WebUI.click(findTestObject('Object Repository/Contact/Page_contact_negative/a_'))

    WebUI.delay(2)

    WebUI.setText(findTestObject('Object Repository/Contact/Page_contact_positive/input__name'), name)

    WebUI.setText(findTestObject('Object Repository/Contact/Page_contact_positive/input__email'), 'jaghama3@gmail.com')

    WebUI.setText(findTestObject('Object Repository/Contact/Page_contact_positive/textarea__message'), 'Positive name test.')

    // Check the checkbox
    WebUI.click(findTestObject('Object Repository/Contact/Page_contact_positive/label_'))

    WebUI.click(findTestObject('Object Repository/Contact/Page_contact_positive/button_'))

    WebUI.delay(2)

    // Verify positive submission
    assert WebUI.waitForElementVisible(findTestObject('Object Repository/Contact/Page_contact_positive/div_'), 15)

    // =====================================================
    // NEGATIVE TEST - CHECKBOX NOT CHECKED
    // =====================================================
    WebUI.click(findTestObject('Object Repository/Contact/Page_contact_negative/a_'))

    WebUI.delay(2)

    WebUI.setText(findTestObject('Object Repository/Contact/Page_contact_positive/input__name'), 'abdallah')

    WebUI.setText(findTestObject('Object Repository/Contact/Page_contact_positive/input__email'), 'jaghama3@gmail.com')

    WebUI.setText(findTestObject('Object Repository/Contact/Page_contact_positive/textarea__message'), 'Testing unchecked checkbox.')

    // IMPORTANT:
    // Do NOT click the checkbox
    WebUI.click(findTestObject('Object Repository/Contact/Page_contact_positive/button_'))

    WebUI.delay(2)

    // Verify the website displayed:
    // "عليك أن تقبل الشروط!"
    assert WebUI.getText(findTestObject('Object Repository/Contact/Page_contact_negative/p_')).trim() == 'عليك أن تقبل الشروط!' : 'Expected checkbox validation message was not displayed.'
}
finally { 
    WebUI.closeBrowser()
}
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.llm.keyword.LlmKeywords as LLM
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser('')

try {
    WebUI.navigateToUrl('https://hebronmart.com/register')

    WebUI.maximizeWindow()

    WebUI.delay(2)

    WebUI.setText(findTestObject('Object Repository/Merchant/page_hebronMarttwo/input__first_name'), firstName)

    WebUI.setText(findTestObject('Object Repository/Merchant/page_hebronMarttwo/input__last_name'), 'jaghama')

    WebUI.setText(findTestObject('Object Repository/Merchant/page_hebronMarttwo/input__email'), ('jaghama' + System.currentTimeMillis()) + 
        '@gmail.com')

    WebUI.setText(findTestObject('Object Repository/Merchant/page_hebronMarttwo/input__password'), '63664383')

    WebUI.setText(findTestObject('Object Repository/Merchant/page_hebronMarttwo/input__confirm_password'), '63664383')

    WebUI.click(findTestObject('Object Repository/Merchant/page_hebronMarttwo/Label_'))

    WebUI.delay(2)

    WebUI.click(findTestObject('Object Repository/Merchant/page_hebronMarttwo/button_'))

    WebUI.delay(2)

    if (firstName.toString().trim().isEmpty()) {
        WebUI.verifyElementPresent(findTestObject('Object Repository/Merchant/page_hebronMarttwo/input__first_name'), 5)
    } else {
        WebUI.verifyElementText(findTestObject('Object Repository/Merchant/page_hebronMarttwo/h1_'), 'تم إنشاء حسابك بنجاح!')
    }
}
finally { 
    WebUI.closeBrowser()
}
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.SelectorMethod
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.Keys

TestObject runtimeXpath(String name, String xpath) {
    TestObject object = new TestObject(name)
    object.setSelectorMethod(SelectorMethod.XPATH)
    object.setSelectorValue(SelectorMethod.XPATH, xpath)
    return object
}

TestObject firstResult = runtimeXpath('firstSearchResult', "(//div[@id='productListResultContainer']//div[contains(@class,'product-item')]//a)[1]")
TestObject checkoutButton = runtimeXpath('proceedToCheckout', "//*[self::a or self::button][contains(normalize-space(.),'الاستمرار في الخروج') or contains(normalize-space(.),'متابعة الدفع') or contains(normalize-space(.),'Checkout')][1]")
TestObject rejectionMessage = runtimeXpath('deliveryRejection', "//*[contains(normalize-space(.),'لا يتم التسليم إلى العنوان الذي اخترته')][1]")
TestObject cookieClose = runtimeXpath('optionalCookieClose', "//*[self::button or self::a][contains(@class,'close') or contains(normalize-space(.),'رفض') or contains(normalize-space(.),'لا أوافق')][1]")

try {
    WebUI.openBrowser('')
    WebUI.navigateToUrl('https://hebronmart.com')
    WebUI.waitForPageLoad(20, FailureHandling.OPTIONAL)
    if (WebUI.waitForElementVisible(cookieClose, 3, FailureHandling.OPTIONAL)) {
        WebUI.click(cookieClose, FailureHandling.OPTIONAL)
    }

    TestObject search = findTestObject('Object Repository/Common/Header/inp_Search')
    WebUI.waitForElementVisible(search, 20)
    WebUI.setText(search, 'كتاب')
    WebUI.sendKeys(search, Keys.chord(Keys.ENTER))
    WebUI.waitForElementPresent(firstResult, 20)
    WebUI.click(firstResult)

    TestObject addToCart = findTestObject('Object Repository/Product/btn_AddToCart')
    WebUI.waitForElementClickable(addToCart, 20)
    WebUI.click(addToCart)
    WebUI.navigateToUrl('https://hebronmart.com/cart')
    WebUI.waitForPageLoad(20, FailureHandling.OPTIONAL)
    WebUI.waitForElementClickable(checkoutButton, 20)
    WebUI.click(checkoutButton)

    // Define all shipping form objects
    TestObject inp_firstName    = runtimeXpath('inp_FirstName', "//input[@name='first_name']")
    TestObject inp_lastName     = runtimeXpath('inp_LastName', "//input[@name='last_name']")
    TestObject inp_email        = runtimeXpath('inp_ShippingEmail', "//input[@type='email' and @placeholder='بريد الالكتروني']")
    TestObject inp_phone        = runtimeXpath('inp_Phone', "//input[@name='phone_number']")
    TestObject inp_city         = runtimeXpath('inp_City', "//input[@name='city']")
    TestObject inp_zip          = runtimeXpath('inp_Zip', "//input[@name='zip_code']")
    TestObject inp_address      = runtimeXpath('inp_Address', "//input[@name='address']")
    TestObject inp_addressTitle = runtimeXpath('inp_AddressTitle', "//input[@name='title']")


    WebUI.waitForElementVisible(inp_firstName, 15)
    WebUI.setText(inp_firstName, 'Anas')
    WebUI.setText(inp_lastName, 'Shalabi')

    WebUI.waitForElementPresent(inp_email, 15)
    WebUI.scrollToElement(inp_email, 10)
    WebUI.waitForElementClickable(inp_email, 15)
    WebUI.click(inp_email)
    WebUI.setText(inp_email, 'anasshalabi429@gmail.com')
    WebUI.setText(inp_phone, '0594386953')

    WebUI.setText(inp_city, 'Ramallah')
    WebUI.setText(inp_zip, 'P6270466')
    WebUI.setText(inp_address, 'Ramallah-Birzeit-University Junction')

    TestObject ddl_country = runtimeXpath('ddl_Country', "//span[@id='select2-select_countries_new_address-container']")
    WebUI.waitForElementClickable(ddl_country, 15)
    WebUI.click(ddl_country)
    TestObject palestineOption = runtimeXpath('palestineOption', "//li[contains(@class,'select2-results__option') and contains(normalize-space(.),'Palestine')]")
    WebUI.waitForElementClickable(palestineOption, 10)
    WebUI.click(palestineOption)


    TestObject ddl_state = runtimeXpath('ddl_State', "//span[@id='select2-select_states_new_address-container']")
    WebUI.waitForElementClickable(ddl_state, 15)
    WebUI.click(ddl_state)
    TestObject westBankOption = runtimeXpath('westBankOption', "//li[contains(@class,'select2-results__option') and contains(normalize-space(.),'West Bank')]")
    WebUI.waitForElementClickable(westBankOption, 10)
    WebUI.click(westBankOption)

	TestObject submitShippingBtn = runtimeXpath('submitShippingBtn', "//button[@id='btnShowCartShippingError']")
WebUI.waitForElementPresent(submitShippingBtn, 15)
WebUI.scrollToElement(submitShippingBtn, 10)
WebUI.waitForElementClickable(submitShippingBtn, 15)
WebUI.click(submitShippingBtn)
    WebUI.waitForElementClickable(submitShippingBtn, 15)
    WebUI.click(submitShippingBtn)

    WebUI.waitForElementVisible(rejectionMessage, 20)

  
    assert WebUI.verifyTextPresent('لا يتم التسليم إلى العنوان الذي اخترته', false)
    WebUI.takeScreenshot(FailureHandling.OPTIONAL)
} finally {
    WebUI.closeBrowser(FailureHandling.OPTIONAL)
}
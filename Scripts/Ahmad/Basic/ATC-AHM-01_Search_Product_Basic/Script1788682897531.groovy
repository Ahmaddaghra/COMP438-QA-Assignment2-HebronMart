import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.SelectorMethod
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import java.net.URL
import java.net.URLDecoder

import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

TestObject searchInput = findTestObject('Object Repository/Common/Header/inp_Search')
TestObject searchResultCards = findTestObject('Object Repository/Search/card_SearchResult')
TestObject searchResultTitle = findTestObject('Object Repository/Search/lbl_SearchResultTitle')
TestObject productTitle = findTestObject('Object Repository/Product/lbl_ProductTitle')
TestObject productPrice = findTestObject('Object Repository/Product/lbl_ProductPrice')
TestObject productSeller = findTestObject('Object Repository/Product/lbl_Seller')

TestObject cookieClose = new TestObject('optionalCookieClose')
cookieClose.setSelectorMethod(SelectorMethod.CSS)
cookieClose.setSelectorValue(SelectorMethod.CSS, '.cookies-warning button.close')

def normalizeWhitespace = { String value ->
    value == null ? '' : value.replace('\u00A0', ' ').replaceAll('\\s+', ' ').trim()
}

def canonicalProductLocation = { String value ->
    URL url = new URL(value.trim())
    String path = URLDecoder.decode(url.path ?: '/', 'UTF-8').replaceFirst('/+$', '')

    [
        scheme: url.protocol.toLowerCase(),
        host: url.host.toLowerCase(),
        path: path ?: '/'
    ]
}

try {
    WebUI.openBrowser('')
    WebUI.navigateToUrl('https://hebronmart.com')
    WebUI.maximizeWindow()

    if (WebUI.waitForElementVisible(cookieClose, 2, FailureHandling.OPTIONAL)) {
        WebUI.click(cookieClose, FailureHandling.OPTIONAL)
    }

    assert WebUI.waitForElementVisible(searchInput, 20) : 'The HebronMart header search input was not visible.'
    assert WebUI.waitForElementClickable(searchInput, 15) : 'The HebronMart header search input was not clickable.'

    WebUI.setText(searchInput, 'كتاب')
    WebUI.sendKeys(searchInput, Keys.chord(Keys.ENTER))

    assert WebUI.waitForElementPresent(searchResultCards, 30) : 'Search for "كتاب" produced no product result cards.'
    assert WebUI.waitForElementVisible(searchResultTitle, 20) : 'No visible product title was available in the search results.'

    List<WebElement> resultCards = WebUI.findWebElements(searchResultCards, 15)
    assert resultCards.size() > 0 :
        "Expected at least one result for 'كتاب'; actual result count was ${resultCards.size()}."

    String clickedTitle = normalizeWhitespace(WebUI.getText(searchResultTitle))
    String clickedProductUrl = WebUI.getAttribute(searchResultTitle, 'href')?.trim() ?: ''

    assert !clickedTitle.isEmpty() :
        "Expected the selected search-result title to be non-empty; actual title was '${clickedTitle}'."
    assert !clickedProductUrl.isEmpty() :
        "Expected the selected search result to have a product URL; actual URL was '${clickedProductUrl}'."

    Map expectedProductLocation = canonicalProductLocation(clickedProductUrl)

    WebUI.click(searchResultTitle)

    assert WebUI.waitForElementVisible(productTitle, 30) : 'The product-details title was not visible.'
    assert WebUI.waitForElementVisible(productPrice, 20) : 'The product price was not visible.'
    assert WebUI.waitForElementVisible(productSeller, 20) : 'The seller/store link was not visible.'

    String detailsTitle = normalizeWhitespace(WebUI.getText(productTitle))
    String priceText = normalizeWhitespace(WebUI.getText(productPrice))
    String sellerText = normalizeWhitespace(WebUI.getText(productSeller))
    String currentUrl = WebUI.getUrl()?.trim() ?: ''
    String windowTitle = normalizeWhitespace(WebUI.getWindowTitle()).toLowerCase()
    Map actualProductLocation = canonicalProductLocation(currentUrl)

    assert !detailsTitle.isEmpty() :
        "Expected the product-details title to be non-empty; actual title was '${detailsTitle}'."
    assert detailsTitle == clickedTitle :
        "Expected product title '${clickedTitle}' from the clicked result; actual detail title was '${detailsTitle}'."
    assert actualProductLocation == expectedProductLocation :
        "Expected clicked product location ${expectedProductLocation}; actual opened location was ${actualProductLocation}. " +
        "Raw expected URL: '${clickedProductUrl}', raw actual URL: '${currentUrl}'."
    assert !priceText.isEmpty() :
        "Expected a non-empty product price; actual price text was '${priceText}'."
    assert priceText.find(/[0-9٠-٩]/) :
        "Expected the visible price to contain a numeric monetary value; actual price was '${priceText}'."
    assert !sellerText.isEmpty() :
        "Expected non-empty seller/store information; actual seller text was '${sellerText}'."
    assert !currentUrl.toLowerCase().contains('/404') :
        "Expected a product URL without '/404'; actual URL was '${currentUrl}'."
    assert !windowTitle.contains('404') && !windowTitle.contains('not found') :
        "Expected a valid product page title; actual window title was '${WebUI.getWindowTitle()}'."

    KeywordUtil.logInfo("Verified search result '${clickedTitle}', price '${priceText}', seller '${sellerText}', URL '${currentUrl}'.")
} finally {
    WebUI.closeBrowser(FailureHandling.OPTIONAL)
}

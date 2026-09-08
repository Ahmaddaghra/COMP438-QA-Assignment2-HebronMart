import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.SelectorMethod
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.text.Normalizer
import java.time.Duration

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait

TestObject searchInput = findTestObject('Object Repository/Common/Header/inp_Search')
TestObject searchResultCards = findTestObject('Object Repository/Search/card_SearchResult')
TestObject searchResultTitles = findTestObject('Object Repository/Search/lbl_SearchResultTitle')
TestObject productTitleObject = findTestObject('Object Repository/Product/lbl_ProductTitle')
TestObject productPriceObject = findTestObject('Object Repository/Product/lbl_ProductPrice')
TestObject stockStatusObject = findTestObject('Object Repository/Product/lbl_StockStatus')
TestObject productQuantityObject = findTestObject('Object Repository/Product/inp_Quantity')
TestObject productQuantityPlusObject = findTestObject('Object Repository/Product/btn_QuantityPlus')
TestObject addToCartObject = findTestObject('Object Repository/Product/btn_AddToCart')
TestObject productIdObject = findTestObject('Object Repository/Product/inp_ProductId')
TestObject addToCartModalObject = findTestObject('Object Repository/Cart/modal_AddToCart')
TestObject modalProductTitleObject = findTestObject('Object Repository/Cart/lbl_ModalProductTitle')
TestObject modalUnitPriceObject = findTestObject('Object Repository/Cart/lbl_ModalUnitPrice')
TestObject modalQuantityObject = findTestObject('Object Repository/Cart/lbl_ModalQuantity')
TestObject modalSubtotalObject = findTestObject('Object Repository/Cart/lbl_ModalSubtotal')
TestObject cartTotalObject = findTestObject('Object Repository/Cart/lbl_CartTotal')
TestObject emptyCartObject = findTestObject('Object Repository/Cart/lbl_EmptyCart')

def normalizeText = { Object value ->
    String rawValue = value == null ? '' : value.toString()
    Normalizer.normalize(rawValue.replace('\u00A0', ' ').replace('\u202F', ' '), Normalizer.Form.NFC)
        .replaceAll('\\s+', ' ')
        .trim()
}

def canonicalProductLocation = { String value ->
    URI uri = new URI(value.trim())
    String path = URLDecoder.decode(uri.rawPath ?: '/', 'UTF-8').replaceFirst('/+$', '')
    [
        scheme: uri.scheme?.toLowerCase(),
        host: uri.host?.toLowerCase(),
        path: path ?: '/'
    ]
}

def runtimeCss = { String name, String selector ->
    TestObject object = new TestObject(name)
    object.setSelectorMethod(SelectorMethod.CSS)
    object.setSelectorValue(SelectorMethod.CSS, selector)
    object
}

def runtimeXpath = { String name, String selector ->
    TestObject object = new TestObject(name)
    object.setSelectorMethod(SelectorMethod.XPATH)
    object.setSelectorValue(SelectorMethod.XPATH, selector)
    object
}

TestObject cookieClose = runtimeCss('optionalCookieClose', '.cookies-warning button.close')
TestObject allCartRows = runtimeCss('allCartRows', '.shopping-cart .item')

String homeUrl = 'https://hebronmart.com'
String cartUrl = 'https://hebronmart.com/cart'
String searchQuery = 'كتاب'
String productTitle = ''
String productUrl = ''
String productId = ''
String productPriceText = ''
TestObject cartRowObject = null
TestObject cartTitleObject = null
TestObject cartUnitPriceObject = null
TestObject cartQuantityObject = null
TestObject cartSubtotalObject = null
TestObject cartQuantityPlusObject = null
TestObject cartRemoveObject = null
boolean cleanupVerified = false

try {
    WebUI.openBrowser('')
    WebUI.navigateToUrl(homeUrl)
    WebUI.maximizeWindow()

    if (WebUI.waitForElementVisible(cookieClose, 2, FailureHandling.OPTIONAL)) {
        WebUI.click(cookieClose, FailureHandling.OPTIONAL)
    }

    assert WebUI.waitForElementVisible(searchInput, 20) :
        "Search input was not visible for query '${searchQuery}'."
    assert WebUI.waitForElementClickable(searchInput, 15) :
        "Search input was not clickable for query '${searchQuery}'."

    WebUI.setText(searchInput, searchQuery)
    WebUI.sendKeys(searchInput, Keys.chord(Keys.ENTER))

    assert WebUI.waitForElementPresent(searchResultCards, 30) :
        "Search for '${searchQuery}' produced no product cards; URL='${WebUI.getUrl()}'."
    assert WebUI.waitForElementVisible(searchResultTitles, 20) :
        "Search for '${searchQuery}' produced no visible product titles; URL='${WebUI.getUrl()}'."

    List<WebElement> resultTitleElements = WebUI.findWebElements(searchResultTitles, 15)
    List<Map<String, String>> candidates = resultTitleElements.collect { WebElement titleElement ->
        String href = titleElement.getAttribute('href')?.trim() ?: ''
        String absoluteUrl = href ? new URL(new URL(homeUrl), href).toExternalForm() : ''
        [title: normalizeText(titleElement.getText()), url: absoluteUrl]
    }.findAll { Map<String, String> candidate ->
        !candidate.title.isEmpty() && !candidate.url.isEmpty()
    }

    assert !candidates.isEmpty() :
        "Search for '${searchQuery}' returned no candidates with both title and URL."

    List<String> rejectionReasons = []

    for (Map<String, String> candidate : candidates) {
        WebUI.navigateToUrl(candidate.url)
        WebUI.waitForPageLoad(20, FailureHandling.OPTIONAL)

        boolean detailReady = WebUI.waitForElementVisible(productTitleObject, 8, FailureHandling.OPTIONAL)
        boolean priceReady = WebUI.waitForElementVisible(productPriceObject, 5, FailureHandling.OPTIONAL)
        boolean inStock = WebUI.waitForElementVisible(stockStatusObject, 3, FailureHandling.OPTIONAL)
        boolean quantityReady = WebUI.waitForElementVisible(productQuantityObject, 3, FailureHandling.OPTIONAL)
        boolean plusReady = WebUI.waitForElementClickable(productQuantityPlusObject, 3, FailureHandling.OPTIONAL)
        boolean addReady = WebUI.waitForElementClickable(addToCartObject, 3, FailureHandling.OPTIONAL)
        boolean idReady = WebUI.waitForElementPresent(productIdObject, 3, FailureHandling.OPTIONAL)

        String actualTitle = detailReady ? normalizeText(WebUI.getText(productTitleObject)) : ''
        String actualPrice = priceReady ? normalizeText(WebUI.getText(productPriceObject)) : ''
        String actualId = idReady ? (WebUI.getAttribute(productIdObject, 'value')?.trim() ?: '') : ''
        String actualQuantity = quantityReady ? (WebUI.getAttribute(productQuantityObject, 'value')?.trim() ?: '') : ''
        String actualUrl = WebUI.getUrl()?.trim() ?: ''
        boolean identityMatches = detailReady && actualTitle == candidate.title &&
            canonicalProductLocation(actualUrl) == canonicalProductLocation(candidate.url)
        boolean numericPrice = actualPrice.find(/[0-9٠-٩]/) != null
        boolean validId = actualId ==~ /[0-9]+/
        boolean validInitialQuantity = actualQuantity == '1'

        if (identityMatches && numericPrice && validId && validInitialQuantity &&
            inStock && plusReady && addReady) {
            productTitle = actualTitle
            productUrl = candidate.url
            productId = actualId
            productPriceText = actualPrice
            break
        }

        rejectionReasons.add(
            "candidateTitle='${candidate.title}', candidateUrl='${candidate.url}', " +
            "actualTitle='${actualTitle}', actualUrl='${actualUrl}', price='${actualPrice}', " +
            "productId='${actualId}', quantity='${actualQuantity}', inStock=${inStock}, " +
            "plusReady=${plusReady}, addReady=${addReady}, identityMatches=${identityMatches}")
    }

    assert !productId.isEmpty() :
        "No suitable in-stock product was discovered for search '${searchQuery}'. Rejections: ${rejectionReasons}."

    String openedTitle = normalizeText(WebUI.getText(productTitleObject))
    String openedUrl = WebUI.getUrl()?.trim() ?: ''
    assert openedTitle == productTitle && canonicalProductLocation(openedUrl) == canonicalProductLocation(productUrl) :
        "Product identity mismatch. expectedTitle='${productTitle}', actualTitle='${openedTitle}', " +
        "expectedUrl='${productUrl}', actualUrl='${openedUrl}'."
    assert normalizeText(WebUI.getText(stockStatusObject)) == 'في المخزن' :
        "Selected product was not reported in stock. productId='${productId}', productTitle='${productTitle}', " +
        "actualStockStatus='${normalizeText(WebUI.getText(stockStatusObject))}'."

    String initialQuantity = WebUI.getAttribute(productQuantityObject, 'value')?.trim() ?: ''
    assert initialQuantity == '1' :
        "Unexpected initial quantity. productId='${productId}', productTitle='${productTitle}', " +
        "expectedQuantity=1, actualQuantity='${initialQuantity}'."

    WebUI.click(productQuantityPlusObject)
    assert WebUI.waitForElementAttributeValue(productQuantityObject, 'value', '2', 15) :
        "Product quantity did not become 2. productId='${productId}', productTitle='${productTitle}', " +
        "actualQuantity='${WebUI.getAttribute(productQuantityObject, 'value')}'."

    String productQuantityAtAdd = WebUI.getAttribute(productQuantityObject, 'value')?.trim() ?: ''
    assert productQuantityAtAdd == '2' :
        "Product quantity update failed. productId='${productId}', productTitle='${productTitle}', " +
        "expectedQuantity=2, actualQuantity='${productQuantityAtAdd}'."

    WebUI.click(addToCartObject)
    assert WebUI.waitForElementVisible(addToCartModalObject, 20) :
        "Add-to-cart AJAX modal did not appear. productId='${productId}', productTitle='${productTitle}'."
    assert WebUI.waitForElementVisible(modalProductTitleObject, 10) :
        "Add-to-cart modal product title was not visible. productId='${productId}', productTitle='${productTitle}'."
    assert WebUI.waitForElementVisible(modalQuantityObject, 10) :
        "Add-to-cart modal quantity was not visible. productId='${productId}', productTitle='${productTitle}'."
    assert WebUI.waitForElementVisible(modalSubtotalObject, 10) :
        "Add-to-cart modal subtotal was not visible. productId='${productId}', productTitle='${productTitle}'."

    String modalTitle = normalizeText(WebUI.getText(modalProductTitleObject))
    String modalQuantity = normalizeText(WebUI.getText(modalQuantityObject))
    String modalUnitPrice = normalizeText(WebUI.getText(modalUnitPriceObject))
    String modalSubtotal = normalizeText(WebUI.getText(modalSubtotalObject))

    assert modalTitle == productTitle :
        "Modal product identity mismatch. expectedTitle='${productTitle}', actualTitle='${modalTitle}', productId='${productId}'."
    assert modalQuantity == '2' :
        "Unexpected modal quantity. productId='${productId}', productTitle='${productTitle}', " +
        "expectedQuantity=2, actualQuantity='${modalQuantity}'."

    CustomKeywords.'qa.cart.CartAssertions.assertLineSubtotal'(
        modalUnitPrice, 2, modalSubtotal, productTitle + ' | add-to-cart modal quantity 2')

    WebUI.navigateToUrl(cartUrl)
    WebUI.waitForPageLoad(30)

    assert productId ==~ /[0-9]+/ :
        "Unsafe product ID for runtime selector construction: '${productId}'."

    String rowXpath = "//div[contains(concat(' ',normalize-space(@class),' '),' shopping-cart ')]" +
        "//div[contains(concat(' ',normalize-space(@class),' '),' item ')]" +
        "[.//input[@data-product-id='${productId}']]"
    String titleXpath = rowXpath +
        "//div[contains(concat(' ',normalize-space(@class),' '),' cart-item-details ')]" +
        "/div[contains(concat(' ',normalize-space(@class),' '),' list-item ')][1]/a[1]"
    String unitPriceXpath = rowXpath +
        "//label[normalize-space(.)='سعر الوحدة:']/following-sibling::strong[contains(@class,'lbl-price')][1]"
    String quantityXpath = rowXpath +
        "//div[contains(concat(' ',normalize-space(@class),' '),' cart-item-quantity ')]//input[@data-product-id='${productId}']"
    String subtotalXpath = rowXpath +
        "//label[normalize-space(.)='مجموع:']/following-sibling::strong[contains(@class,'lbl-price')][1]"
    String plusXpath = quantityXpath +
        "/following-sibling::span//button[contains(concat(' ',normalize-space(@class),' '),' btn-spinner-plus ')]"
    String removeXpath = rowXpath +
        "//a[contains(concat(' ',normalize-space(@class),' '),' btn-cart-remove ')]"

    cartRowObject = runtimeXpath('cartRowForProduct-' + productId, rowXpath)
    cartTitleObject = runtimeXpath('cartTitleForProduct-' + productId, titleXpath)
    cartUnitPriceObject = runtimeXpath('cartUnitPriceForProduct-' + productId, unitPriceXpath)
    cartQuantityObject = runtimeXpath('cartQuantityForProduct-' + productId, quantityXpath)
    cartSubtotalObject = runtimeXpath('cartSubtotalForProduct-' + productId, subtotalXpath)
    cartQuantityPlusObject = runtimeXpath('cartQuantityPlusForProduct-' + productId, plusXpath)
    cartRemoveObject = runtimeXpath('cartRemoveForProduct-' + productId, removeXpath)

    assert WebUI.waitForElementPresent(cartRowObject, 30) :
        "Selected product row was not present in cart. productId='${productId}', productTitle='${productTitle}'."
    assert WebUI.waitForElementVisible(cartTitleObject, 15) :
        "Selected product title was not visible in cart. productId='${productId}', productTitle='${productTitle}'."

    List<WebElement> cartRows = WebUI.findWebElements(allCartRows, 10)
    assert cartRows.size() == 1 :
        "Cart precondition failed: expected exactly one row created by this test; " +
        "actualRowCount=${cartRows.size()}, selectedProductId='${productId}', selectedProductTitle='${productTitle}'."

    String cartTitleAtTwo = normalizeText(WebUI.getText(cartTitleObject))
    String cartUnitPriceAtTwo = normalizeText(WebUI.getText(cartUnitPriceObject))
    String cartQuantityAtTwo = WebUI.getAttribute(cartQuantityObject, 'value')?.trim() ?: ''
    String cartSubtotalAtTwo = normalizeText(WebUI.getText(cartSubtotalObject))

    assert cartTitleAtTwo == productTitle :
        "Cart product identity mismatch. expectedTitle='${productTitle}', actualTitle='${cartTitleAtTwo}', productId='${productId}'."
    assert cartQuantityAtTwo == '2' :
        "Unexpected cart quantity. productId='${productId}', productTitle='${productTitle}', " +
        "expectedQuantity=2, actualQuantity='${cartQuantityAtTwo}'."

    CustomKeywords.'qa.cart.CartAssertions.assertMoneyEquals'(
        productPriceText, cartUnitPriceAtTwo, productTitle + ' | product-page/cart unit-price consistency')
    CustomKeywords.'qa.cart.CartAssertions.assertLineSubtotal'(
        cartUnitPriceAtTwo, 2, cartSubtotalAtTwo, productTitle + ' | cart quantity 2')

    WebUI.click(cartQuantityPlusObject)

    WebDriver driver = DriverFactory.getWebDriver()
    boolean updateCompleted = false

    try {
        updateCompleted = new WebDriverWait(driver, Duration.ofSeconds(30)).until { WebDriver currentDriver ->
            List<WebElement> quantityElements = currentDriver.findElements(By.xpath(quantityXpath))
            List<WebElement> subtotalElements = currentDriver.findElements(By.xpath(subtotalXpath))

            if (quantityElements.size() != 1 || subtotalElements.size() != 1) {
                return false
            }

            String observedQuantity = quantityElements[0].getAttribute('value')?.trim() ?: ''
            String observedSubtotal = normalizeText(subtotalElements[0].getText())
            observedQuantity == '3' && !observedSubtotal.isEmpty() && observedSubtotal != cartSubtotalAtTwo
        }
    } catch (Exception waitFailure) {
        KeywordUtil.logWarning(
            "Timed out waiting for AJAX cart update. productId='${productId}', productTitle='${productTitle}', " +
            "reason='${waitFailure.message}'.")
    }

    String observedQuantityAfterWait = WebUI.getAttribute(
        cartQuantityObject, 'value', FailureHandling.OPTIONAL)?.trim() ?: ''
    String observedSubtotalAfterWait = normalizeText(
        WebUI.getText(cartSubtotalObject, FailureHandling.OPTIONAL))

    assert updateCompleted :
        "Quantity update failed after AJAX/reload. productId='${productId}', productTitle='${productTitle}', " +
        "expectedQuantity=3, actualQuantity='${observedQuantityAfterWait}', " +
        "previousSubtotal='${cartSubtotalAtTwo}', actualSubtotal='${observedSubtotalAfterWait}'."
    assert WebUI.waitForElementPresent(cartRowObject, 15) :
        "Selected product row was not reacquired after cart reload. productId='${productId}', productTitle='${productTitle}'."
    assert WebUI.waitForElementAttributeValue(cartQuantityObject, 'value', '3', 15) :
        "Reacquired cart quantity did not equal 3. productId='${productId}', productTitle='${productTitle}', " +
        "actualQuantity='${WebUI.getAttribute(cartQuantityObject, 'value', FailureHandling.OPTIONAL)}'."

    String cartUnitPriceAtThree = normalizeText(WebUI.getText(cartUnitPriceObject))
    String cartQuantityAtThree = WebUI.getAttribute(cartQuantityObject, 'value')?.trim() ?: ''
    String cartSubtotalAtThree = normalizeText(WebUI.getText(cartSubtotalObject))

    assert cartQuantityAtThree == '3' :
        "Unexpected updated cart quantity. productId='${productId}', productTitle='${productTitle}', " +
        "expectedQuantity=3, actualQuantity='${cartQuantityAtThree}'."
    assert cartSubtotalAtThree != cartSubtotalAtTwo :
        "Cart subtotal did not change after quantity update. productId='${productId}', productTitle='${productTitle}', " +
        "quantity2Subtotal='${cartSubtotalAtTwo}', quantity3Subtotal='${cartSubtotalAtThree}'."

    CustomKeywords.'qa.cart.CartAssertions.assertLineSubtotal'(
        cartUnitPriceAtThree, 3, cartSubtotalAtThree, productTitle + ' | cart quantity 3')

    List<WebElement> updatedCartRows = WebUI.findWebElements(allCartRows, 10)
    assert updatedCartRows.size() == 1 :
        "Cart total precondition failed: expected one cart row; actualRowCount=${updatedCartRows.size()}, " +
        "selectedProductId='${productId}', selectedProductTitle='${productTitle}'."

    String cartTotalText = normalizeText(WebUI.getText(cartTotalObject))
    CustomKeywords.'qa.cart.CartAssertions.assertMoneyEquals'(
        cartSubtotalAtThree, cartTotalText, productTitle + ' | single-row cart total')

    WebUI.click(cartRemoveObject)
    assert WebUI.waitForElementNotPresent(cartRowObject, 20) :
        "Selected product row remained after removal. productId='${productId}', productTitle='${productTitle}'."
    assert WebUI.waitForElementVisible(emptyCartObject, 20) :
        "Empty-cart state did not appear after removal. productId='${productId}', productTitle='${productTitle}'."

    String emptyCartText = normalizeText(WebUI.getText(emptyCartObject))
    assert emptyCartText == 'عربة التسوق فارغة!' :
        "Unexpected empty-cart message. expected='عربة التسوق فارغة!', actual='${emptyCartText}', " +
        "productId='${productId}', productTitle='${productTitle}'."

    cleanupVerified = true
    KeywordUtil.logInfo(
        "Advanced cart test passed. productId='${productId}', productTitle='${productTitle}', " +
        "productUrl='${productUrl}', productPrice='${productPriceText}', quantity2Subtotal='${cartSubtotalAtTwo}', " +
        "quantity3Subtotal='${cartSubtotalAtThree}', cartTotal='${cartTotalText}', cleanup='${emptyCartText}'.")
} finally {
    if (!cleanupVerified && !productId.isEmpty()) {
        try {
            WebUI.navigateToUrl(cartUrl, FailureHandling.OPTIONAL)
            WebUI.waitForPageLoad(20, FailureHandling.OPTIONAL)

            if (cartRowObject == null) {
                String cleanupRowXpath = "//div[contains(concat(' ',normalize-space(@class),' '),' shopping-cart ')]" +
                    "//div[contains(concat(' ',normalize-space(@class),' '),' item ')]" +
                    "[.//input[@data-product-id='${productId}']]"
                String cleanupRemoveXpath = cleanupRowXpath +
                    "//a[contains(concat(' ',normalize-space(@class),' '),' btn-cart-remove ')]"
                cartRowObject = runtimeXpath('cleanupCartRowForProduct-' + productId, cleanupRowXpath)
                cartRemoveObject = runtimeXpath('cleanupCartRemoveForProduct-' + productId, cleanupRemoveXpath)
            }

            if (WebUI.waitForElementPresent(cartRowObject, 5, FailureHandling.OPTIONAL)) {
                WebUI.click(cartRemoveObject, FailureHandling.OPTIONAL)
                boolean removed = WebUI.waitForElementNotPresent(cartRowObject, 15, FailureHandling.OPTIONAL)
                if (!removed) {
                    KeywordUtil.logWarning(
                        "Cleanup could not confirm product removal. productId='${productId}', productTitle='${productTitle}'.")
                }
            }

            if (WebUI.waitForElementVisible(emptyCartObject, 5, FailureHandling.OPTIONAL)) {
                KeywordUtil.logInfo(
                    "Cleanup left the anonymous cart empty for productId='${productId}', productTitle='${productTitle}'.")
            }
        } catch (Exception cleanupFailure) {
            KeywordUtil.logWarning(
                "Cleanup failed without replacing the primary test result. productId='${productId}', " +
                "productTitle='${productTitle}', reason='${cleanupFailure.message}'.")
        }
    }

    WebUI.closeBrowser(FailureHandling.OPTIONAL)
}

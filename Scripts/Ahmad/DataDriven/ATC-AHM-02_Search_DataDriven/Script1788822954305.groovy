import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.SelectorMethod
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import java.net.URI
import java.net.URLDecoder
import java.text.Normalizer
import java.util.Locale

import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

TestObject searchInput = findTestObject('Object Repository/Common/Header/inp_Search')
TestObject searchResultCards = findTestObject('Object Repository/Search/card_SearchResult')
TestObject searchResultTitles = findTestObject('Object Repository/Search/lbl_SearchResultTitle')
TestObject noProductsMessage = findTestObject('Object Repository/Search/lbl_NoProducts')

TestObject cookieClose = new TestObject('optionalCookieClose')
cookieClose.setSelectorMethod(SelectorMethod.CSS)
cookieClose.setSelectorValue(SelectorMethod.CSS, '.cookies-warning button.close')

def normalizeText = { Object value ->
    String rawValue = value == null ? '' : value.toString()
    Normalizer.normalize(rawValue.replace('\u00A0', ' '), Normalizer.Form.NFC)
        .replaceAll('\\s+', ' ')
        .trim()
}

String rowCaseId = normalizeText(caseId)
String rowQuery = normalizeText(query)
String rowExpectedState = normalizeText(expectedState).toUpperCase(Locale.ROOT)
String rowExpectedText = normalizeText(expectedText)
String context = "caseId='${rowCaseId}', query='${rowQuery}', expectedState='${rowExpectedState}', expectedText='${rowExpectedText}'"

assert !rowCaseId.isEmpty() : "Dataset caseId must not be empty; ${context}."
assert !rowQuery.isEmpty() : "Dataset query must not be empty; ${context}."
assert !rowExpectedText.isEmpty() : "Dataset expectedText must not be empty; ${context}."
assert rowExpectedState in ['RESULTS', 'EMPTY'] :
    "Unsupported expectedState '${rowExpectedState}'; caseId='${rowCaseId}', query='${rowQuery}'."

try {
    WebUI.openBrowser('')
    WebUI.navigateToUrl('https://hebronmart.com')
    WebUI.maximizeWindow()

    if (WebUI.waitForElementVisible(cookieClose, 2, FailureHandling.OPTIONAL)) {
        WebUI.click(cookieClose, FailureHandling.OPTIONAL)
    }

    assert WebUI.waitForElementVisible(searchInput, 20) :
        "Search input was not visible; ${context}."
    assert WebUI.waitForElementClickable(searchInput, 15) :
        "Search input was not clickable; ${context}."

    WebUI.setText(searchInput, rowQuery)
    WebUI.sendKeys(searchInput, Keys.chord(Keys.ENTER))

    TestObject expectedPageState = rowExpectedState == 'RESULTS' ?
        searchResultCards : noProductsMessage
    assert WebUI.waitForElementPresent(expectedPageState, 30) :
        "Expected search state '${rowExpectedState}' did not appear within 30 seconds; actual URL was '${WebUI.getUrl()}'; ${context}."

    String currentUrl = WebUI.getUrl()?.trim() ?: ''
    URI currentUri = new URI(currentUrl)
    String decodedSearchValue = ''
    String rawQueryString = currentUri.rawQuery ?: ''

    rawQueryString.split('&').each { String parameter ->
        String[] keyValue = parameter.split('=', 2)
        String decodedName = URLDecoder.decode(keyValue[0], 'UTF-8')

        if (decodedName == 'search') {
            decodedSearchValue = URLDecoder.decode(keyValue.length > 1 ? keyValue[1] : '', 'UTF-8')
        }
    }

    assert currentUri.path == '/products' :
        "Expected search results path '/products'; actual URL was '${currentUrl}'; ${context}."
    assert decodedSearchValue == rowQuery :
        "Expected decoded URL search value '${rowQuery}'; actual value was '${decodedSearchValue}'; URL='${currentUrl}'; ${context}."

    if (rowExpectedState == 'RESULTS') {
        assert WebUI.waitForElementVisible(searchResultTitles, 20) :
            "Expected at least one visible result title; ${context}."

        List<WebElement> resultElements = WebUI.findWebElements(searchResultCards, 15)
        List<WebElement> titleElements = WebUI.findWebElements(searchResultTitles, 15)
        List<String> observedTitles = titleElements.collect { WebElement titleElement ->
            normalizeText(titleElement.getText())
        }

        assert resultElements.size() > 0 :
            "Expected at least one result; actualResultCount=${resultElements.size()}; ${context}."
        assert !observedTitles.isEmpty() :
            "Expected loaded result titles; actualResultCount=${resultElements.size()}, observedTitles=${observedTitles}; ${context}."

        boolean latinExpectedText = rowExpectedText.find(/[A-Za-z]/) != null
        String comparableExpectedText = latinExpectedText ?
            rowExpectedText.toLowerCase(Locale.ROOT) : rowExpectedText
        boolean matchingTitleFound = observedTitles.any { String observedTitle ->
            String comparableTitle = latinExpectedText ?
                observedTitle.toLowerCase(Locale.ROOT) : observedTitle
            comparableTitle.contains(comparableExpectedText)
        }

        assert matchingTitleFound :
            "No result title contained expectedText='${rowExpectedText}'; actualResultCount=${resultElements.size()}, observedTitles=${observedTitles}; ${context}."

        List<WebElement> emptyMessages = WebUI.findWebElements(noProductsMessage, 1)
        assert emptyMessages.isEmpty() :
            "Expected results without an empty-state message; actualResultCount=${resultElements.size()}, actualEmptyStateCount=${emptyMessages.size()}; ${context}."

        KeywordUtil.logInfo("DDT row passed: ${context}, actualResultCount=${resultElements.size()}, observedTitles=${observedTitles}.")
    } else {
        assert WebUI.waitForElementVisible(noProductsMessage, 30) :
            "Expected the empty-state message to be visible; ${context}."

        List<WebElement> resultElements = WebUI.findWebElements(searchResultCards, 2)
        String actualEmptyText = normalizeText(WebUI.getText(noProductsMessage))
        String windowTitle = normalizeText(WebUI.getWindowTitle()).toLowerCase(Locale.ROOT)

        assert resultElements.size() == 0 :
            "Expected zero results; actualResultCount=${resultElements.size()}; ${context}."
        assert actualEmptyText == rowExpectedText :
            "Expected empty-state text '${rowExpectedText}'; actual text was '${actualEmptyText}'; ${context}."
        assert !currentUrl.toLowerCase(Locale.ROOT).contains('/404') :
            "Expected a valid empty-results page, not a 404 URL; actual URL was '${currentUrl}'; ${context}."
        assert !windowTitle.contains('404') && !windowTitle.contains('not found') :
            "Expected a valid empty-results page title; actual title was '${WebUI.getWindowTitle()}'; ${context}."

        KeywordUtil.logInfo("DDT row passed: ${context}, actualResultCount=0, actualEmptyText='${actualEmptyText}'.")
    }
} finally {
    WebUI.closeBrowser(FailureHandling.OPTIONAL)
}

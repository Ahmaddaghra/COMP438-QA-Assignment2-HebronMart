package qa.cart

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil

import java.math.BigDecimal
import java.text.Normalizer

class CartAssertions {

    @Keyword
    void assertLineSubtotal(String unitPriceText, int quantity, String subtotalText, String context) {
        String safeContext = normalizeContext(context)

        if (quantity <= 0) {
            KeywordUtil.markFailedAndStop(
                "Cart subtotal assertion requires quantity > 0. context: ${safeContext}; quantity: ${quantity}")
            return
        }

        BigDecimal unitPrice = null
        BigDecimal actualSubtotal = null

        try {
            unitPrice = parseMoney(unitPriceText)
            actualSubtotal = parseMoney(subtotalText)
        } catch (IllegalArgumentException parsingFailure) {
            KeywordUtil.markFailedAndStop(
                "Unable to parse cart monetary text. context: ${safeContext}; " +
                "raw unit price: '${unitPriceText}'; parsed unit price: ${display(unitPrice)}; " +
                "quantity: ${quantity}; raw subtotal: '${subtotalText}'; " +
                "parsed subtotal: ${display(actualSubtotal)}; reason: ${parsingFailure.message}")
            return
        }

        BigDecimal expectedSubtotal = unitPrice.multiply(BigDecimal.valueOf(quantity))

        if (expectedSubtotal.compareTo(actualSubtotal) != 0) {
            KeywordUtil.markFailedAndStop(
                "Cart subtotal mismatch. context: ${safeContext}; " +
                "raw unit price: '${unitPriceText}'; parsed unit price: ${display(unitPrice)}; " +
                "quantity: ${quantity}; expected subtotal: ${display(expectedSubtotal)}; " +
                "raw subtotal: '${subtotalText}'; parsed subtotal: ${display(actualSubtotal)}")
            return
        }

        KeywordUtil.markPassed(
            "Cart subtotal correct. context: ${safeContext}; unit price: ${display(unitPrice)}; " +
            "quantity: ${quantity}; expected: ${display(expectedSubtotal)}; actual: ${display(actualSubtotal)}")
    }

    @Keyword
    void assertMoneyEquals(String expectedText, String actualText, String context) {
        String safeContext = normalizeContext(context)
        BigDecimal expectedValue = null
        BigDecimal actualValue = null

        try {
            expectedValue = parseMoney(expectedText)
            actualValue = parseMoney(actualText)
        } catch (IllegalArgumentException parsingFailure) {
            KeywordUtil.markFailedAndStop(
                "Unable to compare monetary text. context: ${safeContext}; " +
                "raw expected text: '${expectedText}'; parsed expected value: ${display(expectedValue)}; " +
                "raw actual text: '${actualText}'; parsed actual value: ${display(actualValue)}; " +
                "reason: ${parsingFailure.message}")
            return
        }

        if (expectedValue.compareTo(actualValue) != 0) {
            KeywordUtil.markFailedAndStop(
                "Monetary values differ. context: ${safeContext}; " +
                "raw expected text: '${expectedText}'; parsed expected value: ${display(expectedValue)}; " +
                "raw actual text: '${actualText}'; parsed actual value: ${display(actualValue)}")
            return
        }

        KeywordUtil.markPassed(
            "Monetary values match. context: ${safeContext}; " +
            "expected: ${display(expectedValue)}; actual: ${display(actualValue)}")
    }

    private static BigDecimal parseMoney(String rawText) {
        if (rawText == null) {
            throw new IllegalArgumentException('value is null')
        }

        String normalized = Normalizer.normalize(rawText, Normalizer.Form.NFC)
            .replace('\u00A0', ' ')
            .replace('\u202F', ' ')
            .tr('٠١٢٣٤٥٦٧٨٩۰۱۲۳۴۵۶۷۸۹', '01234567890123456789')
            .replace('٫', '.')
            .replace('٬', ',')
            .replace('₪', '')
            .replaceAll('[\\s\\p{Z}]', '')

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException('value is empty after removing whitespace and the ₪ marker')
        }
        if (!(normalized ==~ /[-+]?\d+(?:[.,]\d+)*/)) {
            throw new IllegalArgumentException("unsupported monetary format '${normalized}'")
        }

        String canonical = canonicalizeSeparators(normalized)

        try {
            return new BigDecimal(canonical)
        } catch (NumberFormatException invalidNumber) {
            throw new IllegalArgumentException("invalid numeric value '${canonical}'", invalidNumber)
        }
    }

    private static String canonicalizeSeparators(String value) {
        boolean hasDot = value.contains('.')
        boolean hasComma = value.contains(',')

        if (hasDot && hasComma) {
            if (value.lastIndexOf('.') > value.lastIndexOf(',')) {
                return value.replace(',', '')
            }
            return value.replace('.', '').replace(',', '.')
        }

        if (hasComma) {
            return canonicalizeSingleSeparator(value, ',')
        }
        if (hasDot) {
            return canonicalizeSingleSeparator(value, '.')
        }
        return value
    }

    private static String canonicalizeSingleSeparator(String value, char separator) {
        String escapedSeparator = separator == '.' ? '\\.' : ','
        String[] parts = value.split(escapedSeparator, -1)

        if (parts.length == 2) {
            int fractionalDigits = parts[1].length()
            if (fractionalDigits == 3 && parts[0].replaceFirst('^[-+]', '').length() <= 3) {
                return value.replace(String.valueOf(separator), '')
            }
            if (fractionalDigits in [1, 2]) {
                return separator == ',' ? value.replace(',', '.') : value
            }
            throw new IllegalArgumentException("ambiguous separator usage in '${value}'")
        }

        boolean groupingOnly = parts.length > 2 &&
            parts[0].replaceFirst('^[-+]', '').length() in 1..3 &&
            parts.tail().every { String group -> group.length() == 3 }

        if (groupingOnly) {
            return value.replace(String.valueOf(separator), '')
        }

        throw new IllegalArgumentException("unsupported separator usage in '${value}'")
    }

    private static String normalizeContext(String context) {
        String value = context == null ? '' : context
        String normalized = Normalizer.normalize(value.replace('\u00A0', ' '), Normalizer.Form.NFC)
            .replaceAll('\\s+', ' ')
            .trim()
        return normalized ?: '(no context)'
    }

    private static String display(BigDecimal value) {
        return value == null ? '(not parsed)' : value.stripTrailingZeros().toPlainString()
    }
}

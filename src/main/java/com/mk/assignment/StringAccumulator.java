package com.mk.assignment;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringAccumulator {

    private static final String MINUS = "-";
    private static final String DELIMITER_PREFIX = "//";
    private static final String DELIMITER_SEPERATOR = "|";
    private static final String NEXT_LINE = "\n";
    private static final String COMMA = ",";
    private static final int MAX_ALLOWED_NUMBER = 1000;

    private final Pattern getOnlyNumbersMatcher = Pattern.compile("\\D+");
    private final Pattern firstNumberMatcher = Pattern.compile("\\d+");

    private Set<String> negativeIntegers;

    /**
     * Main method which accumulates the numbers in the provided string.
     * If a empty string or null is sent, 0 is returned.
     * If only 1 number is sent as an input, there is no need to do any calculation. The number itself will be returned.
     * In cases other than above,
     * If delimiter is suffixed {@link #sumOfNumbersWithDelimiter(String)}
     * If delimiter is not suffixed {@link #sumOfNumbersWithoutDelimiter(String)}
     * <p>
     * After the accumulation is complete,
     * if there are any negative numbers present in the provided string {@link IllegalArgumentException} is thrown with comma separated negative numbers.
     * else, the sum is returned.
     *
     * @param numbers
     * @return
     */
    public int add(String numbers) {
        if (StringUtils.isBlank(numbers)) {
            return 0;
        }
        if (StringUtils.isNumeric(numbers)) {
            return Integer.parseInt(numbers);
        }
        negativeIntegers = new HashSet<>();
        int finalSum = numbers.startsWith(DELIMITER_PREFIX) ? sumOfNumbersWithDelimiter(numbers) : sumOfNumbersWithoutDelimiter(numbers);
        if (negativeIntegers.size() > 0) {
            throw new IllegalArgumentException("negatives not allowed : " + negativeIntegers.stream().map(String::valueOf).collect(Collectors.joining(COMMA)));
        }
        return finalSum;
    }

    /**
     * If the numbers string does not contains delimiters, the delimiters need to be extracted from the string.
     * The extraction of delimiters is done using already compiled regex {@link #getOnlyNumbersMatcher}.
     * The extracted delimiters are put in a sorted set which compares based on length of delimiter.
     * If the above is not done, there will be issues.
     * Eg: If the number is 1,*2,1,*3,7 there are two delimiters "," and ",*".
     * If the splitting is done for "," before ",*", then incorrect result is returned.
     * <p>
     * Assumption:
     * If there is trailing "-" in the delimiters, then there is a negative number in the numbers string.
     *
     * @param numbers
     * @return
     */
    private int sumOfNumbersWithoutDelimiter(String numbers) {
        List<String> delimitersSet = new ArrayList<>();
        Matcher matcher = getOnlyNumbersMatcher.matcher(numbers);
        while (matcher.find()) {
            String delimiter = matcher.group();
            delimitersSet.add(delimiter.length() > 1 && StringUtils.endsWith(delimiter, MINUS) ? StringUtils.substringBeforeLast(delimiter, MINUS) : delimiter);
        }
        List<String> sortedDelimiters = delimitersSet.size() == 1 ? delimitersSet : reverseSortBasedOnLength.apply(delimitersSet.stream());
        return sumOfNumbers(sortedDelimiters, numbers);
    }

    /**
     * If the string starts with {@link #DELIMITER_PREFIX} then there are delimiters mentioned in the string.
     * So, the list of delimiters need to be extracted by splitting it by {@link #DELIMITER_SEPERATOR}.
     * Once extracted, if there are more than 1 delimiter, they need to be sorted based on its length.
     * Eg: If the number is 1,*2,1,*3,7 there are two delimiters "," and ",*".
     * If the splitting is done for "," before ",*", then incorrect result is returned.
     *
     * @param numbers
     * @return
     */
    private int sumOfNumbersWithDelimiter(String numbers) {
        String delimitersString = firstNumberMatcher.split(numbers)[0];
        String numbersToSplit = StringUtils.substringAfter(numbers, delimitersString);
        String delimitersStrWithoutSuffixPrefix = delimitersString.substring(DELIMITER_PREFIX.length(), delimitersString.lastIndexOf(NEXT_LINE));
        String[] delimitersArray = StringUtils.split(delimitersStrWithoutSuffixPrefix, DELIMITER_SEPERATOR);
        List<String> sortedDelimiters = delimitersArray.length == 1 ? Arrays.asList(delimitersArray) : reverseSortBasedOnLength.apply(Stream.of(delimitersArray));
        return sumOfNumbers(sortedDelimiters, numbersToSplit);
    }

    /**
     * Takes a stream of strings and sort them based on length.
     * The sorting is done in reversed ordered i.e. first element will be the string with biggest length.
     */
    private static Function<Stream<String>, List<String>> reverseSortBasedOnLength = stringStream ->
            stringStream.sorted(Comparator.comparingInt(String::length).reversed()).collect(Collectors.toList());

    /**
     * Sorted delimiters and the numbers string sent to this method to carry out the accumulating task.
     * All the delimiters are replaced with {@link #DELIMITER_SEPERATOR} and numbers are extracted accordingly.
     * However, If there is only one delimiter, there is no need to replace it with any other delimiter.
     * Once extracted, checks are done for negative numbers and numbers greater than 1000 whilst sum is being calculated.
     *
     * @param sortedDelimiters
     * @param numbersToSplit
     * @return
     */
    private int sumOfNumbers(List<String> sortedDelimiters, String numbersToSplit) {
        String tempDelimiter = sortedDelimiters.get(0);
        if (sortedDelimiters.size() > 1) {
            tempDelimiter = DELIMITER_SEPERATOR;
            for (String delimiter : sortedDelimiters) {
                numbersToSplit = StringUtils.replace(numbersToSplit, delimiter, tempDelimiter);
            }
        }
        String[] numbers = StringUtils.split(numbersToSplit, tempDelimiter);
        int sum = 0;
        for (String numberStr : numbers) {
            if (numberStr.startsWith(MINUS)) {
                negativeIntegers.add(numberStr);
            } else {
                int number = Integer.parseInt(numberStr);
                if (number <= MAX_ALLOWED_NUMBER) {
                    sum += number;
                }
            }
        }
        return sum;
    }

}

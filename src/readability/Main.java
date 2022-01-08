package readability;

import com.sun.source.tree.Scope;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static String text;
    private static List<String> listOfWords;
    private static List<String> listOfSentences;
    private static int wordCount;
    private static int sentenceCount;
    private static long characterCount;
    private static long syllableCount;
    private static int polysyllableCount;
    private static double averageNumberOfCharactersPerHundredWords;
    private static double averageNumberOfSentencesPerHundredWords;
    private static double score;

    private static Map<Integer, Integer> scoreToAgeMap = new HashMap<>();

    public static void main(String[] args) {

        scoreToAgeMap.put(1, 5);
        scoreToAgeMap.put(2, 6);
        scoreToAgeMap.put(3, 7);
        scoreToAgeMap.put(4, 9);
        scoreToAgeMap.put(5, 10);
        scoreToAgeMap.put(6, 11);
        scoreToAgeMap.put(7, 12);
        scoreToAgeMap.put(8, 13);
        scoreToAgeMap.put(9, 14);
        scoreToAgeMap.put(10, 15);
        scoreToAgeMap.put(11, 16);
        scoreToAgeMap.put(12, 17);
        scoreToAgeMap.put(13, 18);
        scoreToAgeMap.put(14, 24);

        String filename = args[0];
        StringBuilder textBuilder = new StringBuilder();

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                textBuilder.append(scanner.nextLine());
            }
        } catch (FileNotFoundException ignored) {}

        text = textBuilder.toString();
        listOfWords = Arrays.stream(text.split("\\s")).collect(Collectors.toList());
        listOfSentences = Arrays.stream(text.split("[.!?]")).collect(Collectors.toList());
        wordCount = listOfWords.size();
        sentenceCount = listOfSentences.size();
        characterCount = text.replaceAll("[\\s\\t]", "").length();
        averageNumberOfCharactersPerHundredWords = characterCount / ((double) listOfWords.size() / 100);
        averageNumberOfSentencesPerHundredWords = sentenceCount / ((double) listOfWords.size() / 100);
        countSyllablesAndPolysyllables();

        System.out.println("The text is: ");
        System.out.println(text);
        System.out.println();
        System.out.println("Words: " + listOfWords.size());
        System.out.println("Sentences: " + listOfSentences.size());
        System.out.println("Characters: " + characterCount);
        System.out.println("Syllables: " + syllableCount);
        System.out.println("Polysyllables: " + polysyllableCount);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        String mode = scanner.nextLine();
        System.out.println();

        switch (mode) {
            case "ARI":
                int age = ARI();
                break;
            case "FK":
                age = FK();
                break;
            case "SMOG":
                age = SMOG();
                break;
            case "CL":
                age = CL();
                break;
            case "all":
                all();
                break;
        }
    }

    public static void all() {
        int age = ARI() + FK() + SMOG() + CL();
        double ageAverage = (double) age / 4;
        System.out.println("\nThis text should be understood in average by " +
                String.format("%.2f", ageAverage) + "-year-olds.");

    }

    public static int ARI() {
        score = 4.71 * ((double) characterCount / wordCount) + 0.5 * ((double) wordCount / sentenceCount) - 21.43;
        int age = (int) Math.ceil(score) > 14 ? scoreToAgeMap.get(14) : scoreToAgeMap.get((int) Math.ceil(score));
        System.out.println("Automated Readability Index: " + String.format("%.2f", score) +
                " (about " + age + "-year-olds).");
        return age;
    }

    public static int FK() {
        score = 0.39 * ((double) wordCount / sentenceCount) + 11.8 * ((double) syllableCount / wordCount) - 15.59;
        int age = (int) Math.ceil(score) > 14 ? scoreToAgeMap.get(14) : scoreToAgeMap.get((int) Math.ceil(score));
        System.out.println("Flesch–Kincaid readability tests: " + String.format("%.2f", score) +
                " (about " + age + "-year-olds).");
        return age;
    }

    public static int SMOG() {
        score = 1.043 * Math.sqrt(polysyllableCount * ((double) 30 / sentenceCount)) + 3.1291;
        int age = (int) Math.ceil(score) > 14 ? scoreToAgeMap.get(14) : scoreToAgeMap.get((int) Math.ceil(score));
        System.out.println("Simple Measure of Gobbledygook: " + String.format("%.2f", score) +
                " (about " + age + "-year-olds).");
        return age;
    }

    public static int CL() {
        score = 0.0588 * averageNumberOfCharactersPerHundredWords -
                0.296 * averageNumberOfSentencesPerHundredWords - 15.8;
        int age = (int) Math.ceil(score) > 14 ? scoreToAgeMap.get(14) : scoreToAgeMap.get((int) Math.ceil(score));
        System.out.println("Coleman–Liau index: " + String.format("%.2f", score) +
                " (about " + age + "-year-olds).");
        return age;
    }

    public static void countSyllablesAndPolysyllables() {
        Pattern doubleVowelPattern = Pattern.compile("[aeiouAEIOU][aeiou]");
        for (String word : listOfWords) {
            long syl = word.chars().mapToObj(Character::toString).filter(x -> x.matches("[aeiouAEIOU]")).count();
            Matcher doubleVowelMatcher = doubleVowelPattern.matcher(word);
            long doubleVowels = doubleVowelMatcher.results().count();
            syl -= doubleVowels;
            if (word.endsWith("e")) {
                syl -= 1;
            }
            if (syl == 0) {
                syl = 1;
            }
            syllableCount += syl;
            if (syl > 2) {
                polysyllableCount += 1;
            }
        }
    }
}

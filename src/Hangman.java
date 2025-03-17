import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Hangman {
    private static final String[] STAGES = {
        """
        #########
        #  +---+ #
        #  |   | #
        #      | #
        #      | #
        #      | #
        #      | #
        #########
        """,
        """
        #########
        #  +---+ #
        #  |   | #
        #  O   | #
        #      | #
        #      | #
        #      | #
        #########
        """,
        """
        #########
        #  +---+ #
        #  |   | #
        #  O   | #
        #  |   | #
        #      | #
        #      | #
        #########
        """,
        """
        #########
        #  +---+ #
        #  |   | #
        #  O   | #
        # /|   | #
        #      | #
        #      | #
        #########
        """,
        """
        #########
        #  +---+ #
        #  |   | #
        #  O   | #
        # /|\\  | #
        #      | #
        #      | #
        #########
        """,
        """
        #########
        #  +---+ #
        #  |   | #
        #  O   | #
        # /|\\  | #
        # /    | #
        #      | #
        #########
        """,
        """
        #########
        #  +---+ #
        #  |   | #
        #  O   | #
        # /|\\  | #
        # / \\  | #
        #      | #
        #########
        """
    };

    private static String getRandomWord(String fileName) {
        List<String> defaultWords = Arrays.asList("абордаж", "кинопрокат", "подкачка", "транзит");
        Random random = new Random();
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
            long fileLength = file.length();
            if (fileLength == 0) return defaultWords.get(random.nextInt(defaultWords.size()));
            long randomPos = random.nextLong(fileLength);
            file.seek(randomPos);
            file.readLine(); //скип до полной строки
            String word = new String(file.readLine().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);//Считываем случайное слово из файла
            return (word != null) ? word.trim() : defaultWords.get(random.nextInt(defaultWords.size()));
        } catch (IOException e) {
            //System.out.println("Словарь не найден, используется запасной список."); если логер подключать то можно в дебаг убрать
            return defaultWords.get(random.nextInt(defaultWords.size()));
        }
    }

    private static void hangmanPicture(int fallCount) {
        System.out.println(STAGES[fallCount]);
    }
    private static boolean checkGuess( char guess,Set<Character> guessedLetters){
        if (Pattern.matches("[а-яё]", String.valueOf(guess))){
            System.out.println("Некорректный символ");
            return true;
        }
        if (guessedLetters.contains(guess)) {
            System.out.println("Вы уже вводили эту букву.");
            return true;
        }
        return false;
    }

    private static void game(Scanner scanner) {
        String word = getRandomWord("hangman_words.txt");
        char[] guessedWord = new char[word.length()];
        Arrays.fill(guessedWord, '_');
        Set<Character> guessedLetters = new HashSet<>();
        int maxAttempts = 6;
        int attempts = 6;

        System.out.println("Добро пожаловать в Виселицу!");

        while (attempts > 0 && new String(guessedWord).contains("_")) {
            hangmanPicture(maxAttempts-attempts);
            System.out.println("Слово: " + String.valueOf(guessedWord));
            System.out.println("Оставшиеся попытки: " + attempts);
            System.out.print("Введите букву: ");

            char guess = scanner.next().toLowerCase().charAt(0);
            if (!checkGuess(guess,guessedLetters)){
                continue;
            }
            guessedLetters.add(guess);
            boolean correct = false;
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    guessedWord[i] = guess;
                    correct = true;
                }
            }
            if (!correct) {
                attempts--;
                System.out.println("Неверно! -1 попытка.");
            }
        }

        if (new String(guessedWord).equals(word)) {
            System.out.println("Поздравляем! Вы угадали слово: " + word);
        } else {
            hangmanPicture(6);
            System.out.println("Вы проиграли! Загаданное слово: " + word);
        }

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in,"UTF-8");
        System.out.print("Хотите сыграть в висилицу? (да/нет): ");
        while (scanner.next().equalsIgnoreCase("да")) {
            game(scanner);
            System.out.print("Хотите сыграть в висилицу еще раз? (да/нет): ");
        }
        scanner.close();
        System.out.println("Спасибо за игру!");
    }
}

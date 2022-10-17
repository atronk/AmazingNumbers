package numbers;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ExceptionEmptyInput extends Exception {
    public ExceptionEmptyInput() {
        super();
    }
}

public class NumberChecker {
    private final String ERR_FIRST_PARAM = "The first parameter should be a natural number or zero.";
    private final String ERR_SECOND_PARAM = "The second parameter should be a natural number.";
    private final String MSG_INSTRUCTIONS = """
            Supported requests:
            - enter a natural number to know its properties;
            - enter two natural numbers to obtain the properties of the list:
              * the first parameter represents a starting number;
              * the second parameter shows how many consecutive numbers are to be printed;
            - two natural numbers and properties to search for;
            - a property preceded by minus must not be present in numbers;
            - separate the parameters with one space;
            - enter 0 to exit.""";
    private Long num = null;
    private Long range = null;
    private Set<String> selectedProperties = new HashSet<>();
    private boolean stop = false;
    private String error = null;
    private final Map<String, Boolean> PROPS = new HashMap<>();
    private final Set<String> PROPS_NAMES = new HashSet<>(Set.of(
            "buzz", "duck", "palindromic", "gapful", "spy", "even", "odd", "square", "sunny", "jumping",
            "happy", "sad"
    ));
    private final Set<Set<String>> MUT_EX_PROPS = new HashSet<>(Set.of(
            new HashSet<>(Set.of("odd", "even")),
            new HashSet<>(Set.of("-odd", "-even")),
            new HashSet<>(Set.of("duck", "spy")),
            new HashSet<>(Set.of("sunny", "square")),
            new HashSet<>(Set.of("happy", "sad"))
    ));

    private void printError() {
        System.out.println(error);
        System.out.println();
    }

    private void setErr(String errMsg) {
        error = errMsg;
    }

    private void parseLine(String readInput) {

        String[] nums = readInput.strip().split(" ");

        try {
            this.num = Long.parseLong(nums[0]);
        } catch (NumberFormatException exc) {
            this.num = -1L;
        }

        try {
            this.range = Long.parseLong(nums[1]);
        } catch (IndexOutOfBoundsException exc) {
            this.range = null;
        } catch (NumberFormatException exc) {
            this.range = -1L;
        }

        for (int i = 2; i < nums.length; i++) {
            selectedProperties.add(nums[i].toLowerCase());
        }
        if (selectedProperties.size() == 0) {
            selectedProperties = null;
        }
    }

    private void getAndSetInput() throws ExceptionEmptyInput {
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();

        if ("".equals(line)) {
            throw new ExceptionEmptyInput();
        }
        parseLine(line);
    }

    private boolean isNatural(Long n) {
        return n > 0;
    }

    private void setOddEven() {
        PROPS.put("even", this.num % 2 == 0);
        PROPS.put("odd", this.num % 2 != 0);
    }

    private static boolean endsWithSeven(Long number) {
        return (number % 10) == 7;
    }

    private static boolean isDivisibleBySeven(Long number) {
        return number % 7 == 0;
    }

    private void setIsBuzz() {
        boolean endsWith7 = endsWithSeven(this.num);
        boolean divisibleBySeven = isDivisibleBySeven(this.num);
        if (endsWith7 || divisibleBySeven) {
            PROPS.put("buzz", true);
        } else {
            PROPS.put("buzz", false);
        }
    }

    private void setIsDuck() {
        if (this.num.toString().contains("0")) {
            PROPS.put("duck", true);
        } else {
            PROPS.put("duck", false);
        }
    }

    private static boolean isPalindrome(Long n) {
        String nString = n.toString();
        int len = nString.length();
        int halfLen = len / 2;

        for (int i = 0; i < halfLen; i++) {
            if (nString.charAt(i) != nString.charAt(len - 1 - i)) {
                return false;
            }
        }
        return true;
    }

    private void setIsPalindrome() {
        PROPS.put("palindromic", isPalindrome(this.num));
    }

    private static boolean isGapful(Long n) {
        if (n >= 100) {
            // Find first+last digit
            Long firstLast = (n / ((long) Math.pow(10, (long) Math.log10(n)))) * 10 + (n % 10);
            return n % firstLast == 0;
        }
        return false;
    }

    private void setIsGapful() {
        PROPS.put("gapful", isGapful(this.num));
    }

    private static boolean isSpy(Long n) {
        long sum = 0L;
        long prod = 1L;
        while (n > 0) {
            int pop = (int) (n % 10);
            sum += pop;
            prod *= pop;
            n /= 10;
        }
        return sum == prod;
    }

    private void setIsSpy() {
        PROPS.put("spy", isSpy(this.num));
    }

    private static boolean isSquare(Long n) {
        int rooted = (int) Math.sqrt(n);
        return (long) rooted * rooted == n;
    }

    private void setIsSquare() {
        PROPS.put("square", isSquare(this.num));
    }

    private void setIsSunny() {
        PROPS.put("sunny", isSquare(this.num + 1));
    }

    private static boolean isJumping(Long n) {
        int last = (int) (n % 10);

        for (n /= 10; n > 0; n /= 10) {
            int current = (int) (n % 10);
            int diff = Math.abs(current - last);
            if (diff != 1) {
                return false;
            } else {
                last = current;
            }
        }
        return true;
    }

    private void setIsJumping() {
        PROPS.put("jumping", isJumping(num));
    }

    private static long getSumOfSquares(Long n) {
        long res = 0;
        while (n > 0) {
            res += Math.pow(n % 10, 2);
            n /= 10;
        }
        return res;
    }

    private static boolean isHappy(Long n) {
        long slow = n;
        long fast = getSumOfSquares(slow);

        while (slow != 1 || fast != 1) {
            if (slow == fast) {
                return false;
            }
            fast = getSumOfSquares(getSumOfSquares(fast));
            slow = getSumOfSquares(slow);
        }
        return true;
    }

    private void setIsHappySad() {
        boolean isHappy = isHappy(num);
        PROPS.put("happy", isHappy);
        PROPS.put("sad", !isHappy);
    }

    private void setProperties() {
        setOddEven();
        setIsBuzz();
        setIsDuck();
        setIsPalindrome();
        setIsGapful();
        setIsSpy();
        setIsSquare();
        setIsSunny();
        setIsJumping();
        setIsHappySad();
    }

    private void processSingleNumber() {
        setProperties();
        printSingleNumberProperties();
    }

    private static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate) {
        return map.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean numHasRightProps() {
        if (selectedProperties != null) {
            Set<String> trueProps = filterByValue(PROPS, val -> val).keySet();

            for (String s : selectedProperties) {
                if (s.startsWith("-")) {
                    if (trueProps.contains(s.substring(1)))
                        return false;
                } else {
                    if (!trueProps.contains(s))
                        return false;
                }
            }
        }
        return true;
    }


    private void processRangeOfNumbers() {
        for (; range > 0; num++) {
            setProperties();
            if (numHasRightProps()) {
                printRangeNumberProperties();
                range--;
            }
        }
    }

    private boolean isPropsNamesOk() {
        ArrayList<String> err = new ArrayList<>();
        for (String prop : selectedProperties) {
            if (prop.startsWith("-")) {
                if (!PROPS_NAMES.contains(prop.substring(1)))
                    err.add(prop);
            } else if (!PROPS_NAMES.contains(prop))
                err.add(prop);
        }

        if (err.size() > 0) {
            if (err.size() == 1) {
                setErr(String.format(
                        "The property [%s] is wrong.\n", err.get(0)));
            } else {
                setErr(String.format(
                        "The properties [%s] are wrong.\n", String.join(", ", err)));
            }
            setErr(String.format(
                    "%sAvailable properties: [%s]",
                    error, String.join(", ", PROPS_NAMES)));
            return false;
        }
        return true;
    }

    private void setMutExError(ArrayList<String> pairs) {
        setErr(String.format("""
                        The request contains mutually exclusive properties: [%s]
                        There are no numbers with these properties.""",
                String.join(", ", pairs)));
    }

    private boolean isPropsNonMutEx() {
        ArrayList<String> mutExPairs = new ArrayList<>();

        for (String p : selectedProperties) {
            String notP;
            if (p.startsWith("-"))
                notP = p.substring(1);
            else
                notP = "-" + p;
            if (selectedProperties.contains(notP))
                mutExPairs.add(p);
        }

        for (Set<String> pair : MUT_EX_PROPS) {
            if (selectedProperties.containsAll(pair))
                mutExPairs.addAll(pair);
        }

        if (mutExPairs.size() > 0) {
            setMutExError(mutExPairs);
            return false;
        }
        return true;
    }

    private boolean checkProperties() {
        return selectedProperties == null || isPropsNamesOk() && isPropsNonMutEx();
    }

    private boolean checkIsArgsValid() {
        if (error != null) {
            return false;
        } else if (!isNatural(num)) {
            setErr(ERR_FIRST_PARAM);
            return false;
        } else if (range != null && !isNatural(range)) {
            setErr(ERR_SECOND_PARAM);
            return false;
        }
        return checkProperties();
    }

    private void printSingleNumberProperties() {
        System.out.printf("Properties of %,d\n", this.num);
        for (Map.Entry<String, Boolean> e : PROPS.entrySet()) {
            System.out.printf("%12s: %b\n", e.getKey(), e.getValue());
        }
    }

    private void printRangeNumberProperties() {
        Set<String> keys = new HashSet<>();
        for (String key : PROPS.keySet()) {
            if (PROPS.get(key)) {
                keys.add(key);
            }
        }
        System.out.printf(
                "%,12d is %s\n",
                this.num,
                String.join(", ", keys));
    }

    private void greeting() {
        System.out.println("Welcome to Amazing Numbers!\n");
        System.out.println(MSG_INSTRUCTIONS);
        System.out.println();
    }

    private void goodbye() {
        System.out.println("Goodbye!");
    }

    private void unset() {
        this.num = null;
        this.range = null;
        this.selectedProperties = new HashSet<>();
        this.error = null;
    }

    private void requestInput() {
        System.out.print("Enter a request: ");

        try {
            getAndSetInput();
            if (num == 0) {
                stop = true;
            }
        } catch (NoSuchElementException exc) {
            stop = true;
        } catch (ExceptionEmptyInput exc) {
            setErr(MSG_INSTRUCTIONS);
        }
        System.out.print("\n");
    }

    private void process() {
        if (!stop) {
            if (!checkIsArgsValid()) {
                printError();
            } else {
                if (this.range == null) {
                    processSingleNumber();
                } else {
                    processRangeOfNumbers();
                }
                System.out.println();
            }
        }
    }

    public void run() {
        greeting();
        while (!this.stop) {
            requestInput();
            process();
            unset();
        }
        goodbye();
    }
}

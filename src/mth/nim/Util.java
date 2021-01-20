package mth.nim;

import javafx.scene.Node;
import javafx.scene.Scene;

public class Util {
    public static void style(Scene scene) {
        scene.getStylesheets().add("mth/nim/resources/style.css");
    }

    public static int[] toBin(int n) {
        Number maxPower = Math.floor(Math.log(n) / Math.log(2));
        int[] representation = new int[maxPower.intValue()];

        int q = n / 2;
        int r = n % 2;
        int index = maxPower.intValue() - 1;

        while (q != 0) {
            System.out.printf("%d %d%n", q, r);
            representation[index--] = r;
            q = q / 2;
            r = q % 2;

        }

        return representation;
    }

    public static String nimSum(String[] numbers) {
        StringBuilder b = new StringBuilder();

        if (numbers.length == 0)
            throw new RuntimeException();

        for (int i = 0; i < numbers[0].length(); i++) {
            int sum = 0;

            for (String number : numbers) {
                sum += Integer.valueOf(number.charAt(i));
            }

            b.append(sum % 2);
        }

        return b.toString();
    }

    public static void main(String[] args) {
        System.out.println(Util.nimSum(new String[]{"1010011", "0100110"}));
    }
}

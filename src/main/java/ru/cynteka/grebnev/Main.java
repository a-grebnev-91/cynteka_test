package ru.cynteka.grebnev;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    private final static String INPUT = "input.txt";
    private final static String OUTPUT = "output.txt";

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(INPUT));
        String[] firstBatch = getBatch(reader);
        String[] secondBatch = getBatch(reader);
        reader.close();

        Set<Integer> firstRemainIndxs = getRemainingIndxs(firstBatch);
        Set<Integer> secondRemainIndxs = getRemainingIndxs(secondBatch);

        TreeSet<Pair> topOfMostSimilar = getTopOfMostSimilar(firstBatch, secondBatch);

        String resultString = getResultString(
                firstBatch,
                secondBatch,
                firstRemainIndxs,
                secondRemainIndxs,
                topOfMostSimilar
        );

        BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT));
        writer.write(resultString);
        writer.close();
    }

    private static String[] getBatch(BufferedReader reader) throws IOException {
        int n = Integer.parseInt(reader.readLine());
        String[] batch = new String[n];
        for (int i = 0; i < n; i++) {
            batch[i] = reader.readLine();
        }
        return batch;
    }

    //return longest common subsequence of two strings
    private static int getLCS(String first, String second) {
        int len1 = first.length();
        int len2 = second.length();
        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 1; i < len1 + 1; i++) {
            for (int j = 1; j < len2 + 1; j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        int maxSubSeq = -1;
        for (int i = 0; i < len1 + 1; i++) {
            maxSubSeq = Math.max(maxSubSeq, dp[i][len2]);
        }
        return maxSubSeq;
    }

    private static Set<Integer> getRemainingIndxs(String[] batch) {
        Set<Integer> remainingIndxs = new HashSet<>();
        for (int i = 0; i < batch.length; i++) {
            remainingIndxs.add(i);
        }
        return remainingIndxs;
    }

    private static String getResultString(
            String[] firstBatch,
            String[] secondBatch,
            Set<Integer> firstRemainIndxs,
            Set<Integer> secondRemainIndxs,
            TreeSet<Pair> topOfMostSimilar
    ) {
        StringBuilder resultBuilder = new StringBuilder();
        while (!topOfMostSimilar.isEmpty()) {
            Pair currPair = topOfMostSimilar.pollLast();
            int firstIndx = currPair.firstIndx;
            int secondIndx = currPair.secondIndx;
            if (!firstRemainIndxs.contains(firstIndx) || !secondRemainIndxs.contains(secondIndx))
                continue;
            resultBuilder.append(firstBatch[firstIndx]);
            resultBuilder.append(':');
            resultBuilder.append(secondBatch[secondIndx]);
            resultBuilder.append(System.lineSeparator());
            firstRemainIndxs.remove(firstIndx);
            secondRemainIndxs.remove(secondIndx);
        }

        Set<Integer> notEmptySet = firstRemainIndxs.isEmpty() ? secondRemainIndxs : firstRemainIndxs;
        String[] remainingStrings = firstRemainIndxs.isEmpty() ? secondBatch : firstBatch;
        for (Integer i : notEmptySet) {
            resultBuilder.append(remainingStrings[i]);
            resultBuilder.append(":?");
            resultBuilder.append(System.lineSeparator());
        }
        return resultBuilder.toString();
    }

    private static TreeSet<Pair> getTopOfMostSimilar(String[] firstBatch, String[] secondBatch) {
        TreeSet<Pair> topOfMostSimilar = new TreeSet<>();
        for (int i = 0; i < firstBatch.length; i++) {
            for (int j = 0; j < secondBatch.length; j++) {
                int longestCommonSubsequence = getLCS(firstBatch[i], secondBatch[j]);
                Pair currPair = new Pair(i, j);
                currPair.similarity = longestCommonSubsequence;
                topOfMostSimilar.add(currPair);
            }
        }
        return topOfMostSimilar;
    }

    private static class Pair implements Comparable<Pair> {
        int similarity;
        final int firstIndx;
        final int secondIndx;

        private Pair(int firstIndx, int secondIndx) {
            this.firstIndx = firstIndx;
            this.secondIndx = secondIndx;
        }

        @Override
        public int compareTo(Pair o) {
            return Integer.compare(this.similarity, o.similarity);
        }
    }
}

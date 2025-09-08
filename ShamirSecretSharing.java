import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;
import java.math.BigInteger;

public class ShamirSecretSharing {

    public static BigInteger lagrangeInterpolation(List<int[]> xPoints, List<BigInteger> yPoints, int k, BigInteger prime) {
        BigInteger secret = BigInteger.ZERO;
        for (int i = 0; i < k; i++) {
            int xi = xPoints.get(i)[0];
            BigInteger yi = yPoints.get(i);
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int xj = xPoints.get(j)[0];
                    num = num.multiply(BigInteger.valueOf(-xj)).mod(prime);
                    den = den.multiply(BigInteger.valueOf(xi - xj)).mod(prime);
                }
            }

            BigInteger li0 = num.multiply(den.modInverse(prime)).mod(prime);
            BigInteger term = yi.multiply(li0).mod(prime);
            secret = secret.add(term).mod(prime);
        }
        return secret;
    }

    public static BigInteger findSuitablePrime(List<BigInteger> values) {
        BigInteger maxVal = values.stream().max(BigInteger::compareTo).orElse(BigInteger.ZERO);
        return maxVal.nextProbablePrime();
    }

    public static void main(String[] args) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("input1.json")));
            JSONObject root = new JSONObject(content);
            JSONArray testcases = root.getJSONArray("testcases");

            for (int t = 0; t < testcases.length(); t++) {
                JSONObject json = testcases.getJSONObject(t);

                int n = json.getJSONObject("keys").getInt("n");
                int k = json.getJSONObject("keys").getInt("k");

                List<int[]> xPoints = new ArrayList<>();
                List<BigInteger> yPoints = new ArrayList<>();

                for (String key : json.keySet()) {
                    if (!key.equals("keys")) {
                        JSONObject obj = json.getJSONObject(key);
                        int x = Integer.parseInt(key);
                        int base = obj.getInt("base");
                        String valueStr = obj.get("value").toString();

                        BigInteger decodedValue = new BigInteger(valueStr, base);
                        xPoints.add(new int[]{x});
                        yPoints.add(decodedValue);
                    }
                }

                if (xPoints.size() < k) {
                    System.out.println("Test case " + (t + 1) + ": Not enough shares to reconstruct.");
                    continue;
                }

                BigInteger prime = findSuitablePrime(yPoints);
                System.out.println("Test case " + (t + 1) + " using prime modulus: " + prime);

                List<int[]> chosenX = xPoints.subList(0, k);
                List<BigInteger> chosenY = yPoints.subList(0, k);

                BigInteger secret = lagrangeInterpolation(chosenX, chosenY, k, prime);
                System.out.println("Test case " + (t + 1) + " secret = " + secret + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

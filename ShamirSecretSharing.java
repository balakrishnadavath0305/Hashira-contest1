import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;
import java.math.BigInteger;   // ✅ add this


public class ShamirSecretSharing {

    // Function to calculate Lagrange Interpolation at x=0
    public static BigInteger lagrangeInterpolation(List<int[]> xPoints, List<BigInteger> yPoints, int k) {
    BigInteger secret = BigInteger.ZERO;

    for (int i = 0; i < k; i++) {
        int xi = xPoints.get(i)[0];
        BigInteger yi = yPoints.get(i);

        // Compute li(0) as fraction
        BigInteger num = BigInteger.ONE;
        BigInteger den = BigInteger.ONE;

        for (int j = 0; j < k; j++) {
            if (i != j) {
                int xj = xPoints.get(j)[0];
                num = num.multiply(BigInteger.valueOf(-xj));
                den = den.multiply(BigInteger.valueOf(xi - xj));
            }
        }

        // li(0) = num / den
        // yi * li(0) = yi * num / den
        // → Do division at the end
        BigInteger term = yi.multiply(num).divide(den);
        secret = secret.add(term);
    }

    return secret;
}


    public static void main(String[] args) {
    try {
        String content = new String(Files.readAllBytes(Paths.get("input1.json")));
        JSONObject json = new JSONObject(content);

        int n = json.getJSONObject("keys").getInt("n");
        int k = json.getJSONObject("keys").getInt("k");

        List<int[]> xPoints = new ArrayList<>();
        List<BigInteger> yPoints = new ArrayList<>();

        for (String key : json.keySet()) {
            if (!key.equals("keys")) {
                JSONObject obj = json.getJSONObject(key);

                int x = Integer.parseInt(key);
                int base = Integer.parseInt(obj.getString("base"));
                String valueStr = obj.getString("value");

                // decode huge value correctly
                BigInteger decodedValue = new BigInteger(valueStr, base);

                xPoints.add(new int[]{x});
                yPoints.add(decodedValue);
            }
        }

        // Pick first k shares (or shuffle for different subsets)
        List<int[]> chosenX = xPoints.subList(0, k);
        List<BigInteger> chosenY = yPoints.subList(0, k);

        BigInteger secret = lagrangeInterpolation(chosenX, chosenY, k);
        System.out.println("Secret (c) = " + secret);

    } catch (Exception e) {
        e.printStackTrace();
    }
}


}

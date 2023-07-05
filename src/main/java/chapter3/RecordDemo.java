package chapter3;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class RecordDemo {

    public static void main(String[] args) {
        point();
        System.out.println("======");
        System.out.println(findTopMerchants(
                List.of(
                        new Merchant("Jalen", Map.of(1, 100.0)),
                        new Merchant("Alperen", Map.of(1, 120.0)),
                        new Merchant("Jabari", Map.of(1, 80.0)),
                        new Merchant("Tari", Map.of(1, 90.0))
                ), 1
        ));
        System.out.println("=======");
        new Range(10, 5);
    }

    private static void point() {
        record Point(int x, int y) { }
        var point = new Point(3, 4);
        System.out.println("Point.x = " + point.x);
        System.out.println("Point.y = " + point.y);
        System.out.println(point.equals(new Point(3, 4)));
        System.out.println(point);
    }

    private static List<Merchant> findTopMerchants(List<Merchant> merchants, int month) {
        // Local record
        record MerchantSales(Merchant merchant, double sales) {}

        return merchants.stream()
                .map(merchant -> new MerchantSales(merchant, computeSales(merchant, month)))
                .sorted((m1, m2) -> Double.compare(m2.sales(), m1.sales()))
                .map(MerchantSales::merchant)
                .collect(toList());
    }

    private static double computeSales(Merchant merchant, int month) {
        return merchant.sales.get(month);
    }

    record Merchant(String name, Map<Integer, Double> sales){}

    record Range(int lo, int hi) {
        Range {
            if (lo > hi)
                throw new IllegalArgumentException(String.format("(%d,%d)", lo, hi));
        }
    }
}

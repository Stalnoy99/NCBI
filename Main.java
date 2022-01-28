import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String URL = "https://www.ncbi.nlm.nih.gov";
    private static final String URL_SEARCH = "https://www.ncbi.nlm.nih.gov/pmc/?term=";

    public static void main(String[] args) throws IOException {
        StringBuilder text = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите поисковой запрос:");
        String search = scanner.nextLine().trim().replaceAll("\\s", "+")
                .replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
        String newURL = URL_SEARCH + search;
        Document doc = Jsoup.connect(newURL).maxBodySize(0).get();
        Elements elements = doc.select("body").select("div:nth-child(1)").select("div:nth-child(1)")
                .select("form:nth-child(1)").select("div:nth-child(1)").select("div:nth-child(6)").select("a");
        elements.forEach(element -> {
            if (element.attr("class").equals("view") && element.attr("href").matches("/pmc/articles/PMC\\d*/")) {
                String resultURL = URL + element.attr("href");
                System.out.println(resultURL);
                text.append(resultURL).append("\n");
                try {
                    TreeSet<String> proteins = new TreeSet<>();
                    Document document = Jsoup.connect(resultURL).maxBodySize(0).get();
                    Elements resultElements = document.select("p");
                    resultElements.forEach(resultElement -> {
                        Pattern pattern = Pattern.compile("[A-Z]{2,4}(-[1-9][0-9]?|[1-9][0-9]?)");
                        Matcher matcher = pattern.matcher(resultElement.text());
                        while (matcher.find()) {
                            if (!matcher.group(0).matches("[ACGT]{3,4}") || matcher.group(0).equals("CC") || matcher.group(0).equals("DNA")
                                    || matcher.group(0).equals("RNA") || matcher.group(0).equals("NCBI") || matcher.group(0).equals("EUR"))
                                proteins.add(matcher.group(0));
                        }
                    });
                    System.out.println(proteins);
                    text.append(proteins).append("\n").append("__________________________________________").append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        try (FileWriter writer = new FileWriter("C:/Users/User/Desktop/proteins.doc", false)) {
            writer.write(String.valueOf(text));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
//((chondrocyte) OR (cartilage cell)) AND ((metabolism)) AND ((snp) OR (Single Nucleotide Polymorphism)) AND Homo sapiens[Organism]
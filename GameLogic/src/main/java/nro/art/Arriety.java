package nro.art;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class Arriety {


    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        for (int i = 1245234; i < 1257060; i++) {
            try {
                String url = "https://tienhiep.org/doc-truyen/hau-due-kiem-than/read/" + i + ".html";
                Document document = Jsoup.connect(url).get();
                String title = document.title();
                System.out.println("Title: " + title);

                Elements scriptTags = document.select("script[type='application/ld+json']");

                File file = new File("meo/page_" + i + ".txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Title: " + title);
                    writer.newLine();
                    for (Element script : scriptTags) {
                        String scriptContent = script.html();
                        try {
                            JSONObject json = new JSONObject(scriptContent);
                            if (json.has("@type") && (json.getString("@type").equals("Book") || json.getString("@type").equals("Article"))) {
                                writer.write("Name: " + json.optString("name", "N/A"));
                                writer.newLine();
                                writer.write("Author: " + json.optJSONObject("author").optString("name", "N/A"));
                                writer.newLine();
                                writer.write("Description: " + json.optString("description", "N/A"));
                                writer.newLine();
                                writer.write("Published Date: " + json.optString("datePublished", "N/A"));
                                writer.newLine();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Saved page " + i + " to file: " + file.getName());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

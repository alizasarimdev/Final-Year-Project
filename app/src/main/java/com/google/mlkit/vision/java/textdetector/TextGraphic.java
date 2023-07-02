package com.google.mlkit.vision.demo.java.textdetector;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;

import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.GraphicOverlay.Graphic;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.Text.Line;
import com.google.mlkit.vision.text.Text.TextBlock;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.Arrays;
import com.google.mlkit.vision.text.Text;

public class TextGraphic extends Graphic {
    private static final String TAG = "TextGraphic";
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int MARKER_COLOR = Color.WHITE;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;
    public static String first100Words;
    private final Paint rectPaint;
    private final Paint textPaint;
    private final Paint labelPaint;
    private Text text;
    private final boolean shouldGroupTextInBlocks;
    private final boolean showLanguageTag;
    //private final boolean showConfidence;
    private static boolean isTextDetectionEnabled;
    private String previousText; // Variable to store the previously detected text



    TextGraphic(
            GraphicOverlay overlay,
            Text text,
            boolean shouldGroupTextInBlocks,
            boolean showLanguageTag,
            boolean showConfidence,
            String previousText,
            boolean isTextDetectionEnabled)
    {
        super(overlay);

        this.text = text;
        this.shouldGroupTextInBlocks = shouldGroupTextInBlocks;
        this.showLanguageTag = showLanguageTag;
        //this.showConfidence = showConfidence;
        this.previousText = previousText;
        this.isTextDetectionEnabled=isTextDetectionEnabled;
        rectPaint = new Paint();
        rectPaint.setColor(MARKER_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(TEXT_SIZE);

        labelPaint = new Paint();
        labelPaint.setColor(MARKER_COLOR);
        labelPaint.setStyle(Paint.Style.FILL);
        postInvalidate();
    }
    @Override
    public void draw(Canvas canvas) {

        //textPaint.setColor(Color.BLACK);
        //rectPaint.setColor(Color.parseColor("#FFC0CB"));
        StringBuilder concatenatedText = new StringBuilder();

        if (text != null) {
            for (TextBlock textBlock : text.getTextBlocks()) {
                for (Line line : textBlock.getLines()) {
                    String lineText = line.getText();
                    RectF lineBoundingBox = new RectF(line.getBoundingBox());
                    drawText(lineText, lineBoundingBox, TEXT_SIZE + 2 * STROKE_WIDTH, canvas);
                    concatenatedText.append(line.getText()).append(" ");
                }
            }

            previousText = concatenatedText.toString();

            if (isTextDetectionEnabled && previousText != null && !previousText.isEmpty()) {
                Log.d(TAG, "Concatenated Texxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxt: " + previousText);
                Log.d(TAG, "HYyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy: " + isTextDetectionEnabled);
              GoogleScraper(previousText);

            }
        }
       if (isTextDetectionEnabled && first100Words != null) {
           final TextPaint textPaint = new TextPaint();
           textPaint.setTextSize(30.0f);
           labelPaint.setColor(Color.parseColor("#FFC0CB"));
            // Calculate the center coordinates of the screen
            float centerX = canvas.getWidth() / 2f;
            float centerY = canvas.getHeight() / 2f;

            // Set the maximum width for the text layout
            int maxWidth = (int) (canvas.getWidth() * 0.8); // Adjust the percentage as needed

            // Create the StaticLayout for the text
            StaticLayout textLayout = new StaticLayout(
                    first100Words, textPaint, maxWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

            // Calculate the starting position of the text
            float textStartX = centerX - (textLayout.getWidth() / 2f);
            float textStartY = centerY - (textLayout.getHeight() / 2f);

            // Set the background color
            int backgroundColor = Color.YELLOW; // Change the background color here

            // Calculate the rectangle dimensions based on the text layout
            int rectLeft = (int) textStartX;
            int rectTop = (int) textStartY;
            int rectRight = (int) (textStartX + textLayout.getWidth());
            int rectBottom = (int) (textStartY + textLayout.getHeight());

            // Create a paint object with the background color
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(backgroundColor);

            // Draw the yellow background rectangle
            canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, backgroundPaint);

            // Draw the text on the canvas
            canvas.save();
            canvas.translate(textStartX, textStartY);
            textLayout.draw(canvas);
            canvas.restore();

        }


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return false;
    }
    public static void GoogleScraper(String query) {
        try {
            // Perform a Google search
            String googleSearchUrl = "https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8");

            // Set a generic user agent
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Safari/537.36";

            Connection connection = Jsoup.connect(googleSearchUrl)
                    .userAgent(userAgent)
                    .timeout(5000); // Set a reasonable timeout

            // Add additional headers to mimic a real web browser
            connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.header("Accept-Language", "en-US,en;q=0.5");
            // Execute the request and retrieve the response
            Connection.Response response = connection.execute();

            // Check the response status code
            if (response.statusCode() == 200) {
                Document searchResults = response.parse();
                Elements divs = searchResults.select("div.g"); // Select only the search result divs
                Element firstResult = divs.first(); // Get the first search result
                Element urlElement = firstResult.selectFirst("a[href]");
                String url = urlElement.attr("href");

                // Visit the URL and extract the complete description from the webpage
                Document webpage = Jsoup.connect(url)
                        .userAgent(userAgent)
                        .timeout(5000) // Set a reasonable timeout
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "en-US,en;q=0.5")
                        .get();

                Elements paragraphElements = webpage.select("p");
                StringBuilder combinedText = new StringBuilder();

                System.out.println("Paragraphs:");
                for (Element paragraph : paragraphElements) {
                    String paragraphText = paragraph.text();

                    if (paragraphText.length() > 60 &&
                            paragraph.select("a, img").isEmpty() &&
                            paragraph.select("*").size() == 1) {
                        combinedText.append(paragraphText).append(" ");
                    }
                }
                String combinedParagraphs = combinedText.toString();
                String[] words = combinedParagraphs.split("\\s+");
                int wordCount = Math.min(words.length, 150); // Limit to the first 100 words
                first100Words = String.join(" ", Arrays.copyOfRange(words, 0, wordCount));

                Log.d(TAG, "Linkkkkkkkkkkkkkkkkk " + first100Words);
            } else {
                Log.d(TAG, "Error: Response code " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawText(String text, RectF rect, float textHeight, Canvas canvas) {
        if (isTextDetectionEnabled && previousText != null && !previousText.isEmpty())
        {

        }
            float x0 = translateX(rect.left);
        float x1 = translateX(rect.right);
        rect.left = min(x0, x1);
        rect.right = max(x0, x1);
        rect.top = translateY(rect.top);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, rectPaint);
        float textWidth = textPaint.measureText(text);
        canvas.drawRect(
                rect.left - STROKE_WIDTH,
                rect.top - textHeight,
                rect.left + textWidth + 2 * STROKE_WIDTH,
                rect.top,
                labelPaint);
        canvas.drawText(text, rect.left, rect.top - STROKE_WIDTH, textPaint);
    }
}
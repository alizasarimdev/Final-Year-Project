package com.google.mlkit.vision.demo.java.textdetector;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.demo.preference.PreferenceUtils;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.Text.Element;
import com.google.mlkit.vision.text.Text.Line;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import java.util.List;

/** Processor for the text detector demo. */
public class TextRecognitionProcessor extends VisionProcessorBase<Text> {


  private static final String TAG = "TextRecProcessor";

  private final TextRecognizer textRecognizer;
  private final Boolean shouldGroupRecognizedTextInBlocks;
  private final Boolean showLanguageTag;
  private final boolean showConfidence;
  public String previousText;

  private static boolean isTextDetectionEnabled = false;





  public TextRecognitionProcessor(
      Context context, TextRecognizerOptionsInterface textRecognizerOptions) {
    super(context);
    shouldGroupRecognizedTextInBlocks = PreferenceUtils.shouldGroupRecognizedTextInBlocks(context);
    showLanguageTag = PreferenceUtils.showLanguageTag(context);
    showConfidence = PreferenceUtils.shouldShowTextConfidence(context);
    textRecognizer = TextRecognition.getClient(textRecognizerOptions);
  }
  @Override
  public void stop() {
    super.stop();
    textRecognizer.close();
  }

  public void setTextDetectionEnabled(boolean enabled) {
    isTextDetectionEnabled = enabled;
  }

  public boolean isTextDetectionEnabled() {
    return isTextDetectionEnabled;
  }
  @Override
  protected Task<Text> detectInImage(InputImage image) {

    if (!isTextDetectionEnabled) {
      return textRecognizer.process(image);
    } else {
      // Create a completed Task with a null result
      return Tasks.forResult(null);
    }
  }


  @Override
  protected void onSuccess(@NonNull Text text, @NonNull GraphicOverlay graphicOverlay) {
    Log.d(TAG, "On-device Text detection successful");
    graphicOverlay.add(
        new TextGraphic(
            graphicOverlay,
            text,
            shouldGroupRecognizedTextInBlocks,
            showLanguageTag,
            showConfidence,previousText,isTextDetectionEnabled));
  }



  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.w(TAG, "Text detection failed." + e);
  }
}

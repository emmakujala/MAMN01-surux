package com.example.geobird;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class CanvasView extends View {
   private Paint paint;
   private Bitmap bitmap;
   private Bitmap birdMap;
   private static final Random random = new Random();
   private float topDelta = 0;
   private float leftDelta = 0;
   private Bitmap oriBridMap;

   public CanvasView(Context context) {
      super(context);
      init();
   }

   public CanvasView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public void updateDeltas(float top, float left) {
      topDelta += top;
      leftDelta += left;
   }

   public void rotate(float degrees) {
      Matrix matrix = new Matrix();
      matrix.postRotate(degrees);
      Bitmap scaledBitmap = Bitmap.createScaledBitmap(oriBridMap, oriBridMap.getWidth(), oriBridMap.getHeight(), true);
      birdMap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
   }

   private void init() {
      paint = new Paint();

      // could replace this by storing or handling the previous degrees passed in
      oriBridMap = BitmapFactory.decodeResource(getResources(), R.raw.bird);
      birdMap = BitmapFactory.decodeResource(getResources(), R.raw.bird);
      bitmap = BitmapFactory.decodeResource(getResources(), R.raw.img);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      canvas.drawBitmap(bitmap, leftDelta, topDelta, paint);

      int centerX = (getWidth() / 2) - (birdMap.getWidth() / 2); // Center horizontally
      int centerY = (getHeight() / 2) - (birdMap.getHeight() / 2); // Center vertically
      canvas.drawBitmap(birdMap, centerX, centerY, paint);
   }

   public static int getRandomInRange(int min, int max) {
      if (min > max) {
         throw new IllegalArgumentException("min cannot be greater than max");
      }
      return random.nextInt(max - min + 1) + min;
   }
}

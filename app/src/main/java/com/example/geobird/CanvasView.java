package com.example.geobird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class CanvasView extends View {
   private Paint paint;
   private Bitmap bitmap;
   private static final Random random = new Random();

   public CanvasView(Context context) {
      super(context);
      init();
   }

   public CanvasView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   private void init() {
      paint = new Paint();

//      Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.raw.img);
//
//      // Get the dimensions of the screen
//      int screenWidth = getResources().getDisplayMetrics().widthPixels;
//      int screenHeight = getResources().getDisplayMetrics().heightPixels;
//
//      // Calculate the desired aspect ratio
//      float desiredAspectRatio = (float) screenWidth / screenHeight;
//
//      // Calculate the new dimensions while preserving the aspect ratio
//      int newWidth, newHeight;
//      float bitmapAspectRatio = (float) originalBitmap.getWidth() / originalBitmap.getHeight();
//
//      if (bitmapAspectRatio > desiredAspectRatio) {
//         // Landscape image
//         newWidth = screenWidth;
//         newHeight = (int) (newWidth / bitmapAspectRatio);
//      } else {
//         // Portrait or square image
//         newHeight = screenHeight;
//         newWidth = (int) (newHeight * bitmapAspectRatio);
//      }
//
//      // Create the scaled Bitmap
//      bitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
      //originalBitmap.recycle();
      bitmap = BitmapFactory.decodeResource(getResources(), R.raw.img);
      // Recycle the original Bitmap to free up memory

   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);

      // Set the paint color to red
      paint.setColor(Color.RED);

      // Create a rectangle object
      // Rect rectangle = new Circle(100, 100, 300, 300);
      // Draw the rectangle on the canvas
      // canvas.drawRect(rectangle, paint);

      int leftDelta = 0;
      int topDelta = 0;

      if (random.nextBoolean()) {
         if (random.nextBoolean()) {
            leftDelta += 100;
         } else {
            leftDelta += -100;
         }
      } else {
         if (random.nextBoolean()) {
            topDelta += 100;
         } else {
            topDelta += -100;
         }
      }


      canvas.drawBitmap(bitmap, leftDelta, topDelta, paint);

      int centerX = getWidth() / 2; // Center horizontally
      int centerY = getHeight() / 2; // Center vertically
      int radius = 100; // Radius of the circle (adjust as needed)
      canvas.drawCircle(centerX, centerY, radius, paint);
   }

   public static int getRandomInRange(int min, int max) {
      if (min > max) {
         throw new IllegalArgumentException("min cannot be greater than max");
      }
      return random.nextInt(max - min + 1) + min;
   }
}

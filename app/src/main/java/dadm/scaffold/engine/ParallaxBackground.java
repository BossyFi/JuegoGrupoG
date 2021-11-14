package dadm.scaffold.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ParallaxBackground extends ScreenGameObject {
    private final Bitmap bitmap;
    protected double pixelFactor;
    private double speedY;
    private double imageHeight;
    private double imageWidth;
    private int screenHeight;
    private int screenWidth;
    private int targetWidth;
    private Rect srcRect = new Rect();
    private Rect dstRect = new Rect();
    private final Matrix matrix = new Matrix();

    public ParallaxBackground(GameEngine gameEngine, int speed,
                              int drawableResId) {
        Drawable spriteDrawable = gameEngine.getContext().getResources()
                .getDrawable(drawableResId);
        bitmap = ((BitmapDrawable) spriteDrawable).getBitmap();
        pixelFactor = gameEngine.pixelFactor;
        speedY = speed * pixelFactor / 1000d;
        imageHeight = spriteDrawable.getIntrinsicHeight() * pixelFactor;
        imageWidth = spriteDrawable.getIntrinsicWidth() * pixelFactor;
        screenHeight = gameEngine.height;
        screenWidth = gameEngine.width;
        targetWidth = (int) Math.min(imageWidth, screenWidth);
    }

    @Override
    public void startGame() {

    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        positionY += speedY * elapsedMillis;
    }

    @Override
    public void onDraw(Canvas canvas) {
//        if (positionY > 0) {
//            matrix.reset();
//            matrix.postScale((float) (pixelFactor),
//                    (float) (pixelFactor));
//            matrix.postTranslate(0, (float) (positionY - imageHeight));
//            canvas.drawBitmap(bitmap, matrix, null);
//        }
//        matrix.reset();
//        matrix.postScale((float) (pixelFactor),
//                (float) (pixelFactor));
//        matrix.postTranslate(0, (float) positionY);
//        canvas.drawBitmap(bitmap, matrix, null);
//        if (positionY > screenHeight) {
//            positionY -= imageHeight;
//        }
        efficientDraw(canvas);
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {

    }

    private void efficientDraw(Canvas canvas) {
        if (positionY < 0) {
            srcRect.set(0,
                    (int) (-positionY / pixelFactor),
                    (int) (targetWidth / pixelFactor),
                    (int) ((screenHeight - positionY) / pixelFactor));
            dstRect.set(0,
                    0,
                    (int) targetWidth,
                    (int) screenHeight);
            canvas.drawBitmap(bitmap, srcRect, dstRect, null);
        } else {
            srcRect.set(0,
                    0,
                    (int) (targetWidth / pixelFactor),
                    (int) ((screenHeight - positionY) / pixelFactor));
            dstRect.set(0,
                    (int) positionY,
                    (int) targetWidth,
                    (int) screenHeight);
            canvas.drawBitmap(bitmap, srcRect, dstRect, null);
            // We need to draw the previous block
            srcRect.set(0,
                    (int) ((imageHeight - positionY) / pixelFactor),
                    (int) (targetWidth / pixelFactor),
                    (int) (imageHeight / pixelFactor));
            dstRect.set(0,
                    0,
                    (int) targetWidth,
                    (int) positionY);
            canvas.drawBitmap(bitmap, srcRect, dstRect, null);
        }
        if (positionY > screenHeight) {
            positionY -= imageHeight;
        }
    }
}

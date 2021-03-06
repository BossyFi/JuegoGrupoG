package dadm.scaffold.engine;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public abstract class Sprite extends ScreenGameObject {

    public double rotation;

    protected double pixelFactor;

    public double scale = 1f;

    public int alpha = 255;

    private Bitmap bitmap;

    private final Matrix matrix = new Matrix();

    private Paint paint;

    private GameEngine gameEngine;

    protected Sprite(GameEngine gameEngine, int drawableRes) {

        this.gameEngine = gameEngine;
        Resources r = gameEngine.getContext().getResources();
        Drawable spriteDrawable = r.getDrawable(drawableRes);
        paint = new Paint();
        this.pixelFactor = gameEngine.pixelFactor;

        this.width = (int) (spriteDrawable.getIntrinsicWidth() * this.pixelFactor * scale);
        this.height = (int) (spriteDrawable.getIntrinsicHeight() * this.pixelFactor * scale);

        this.bitmap = ((BitmapDrawable) spriteDrawable).getBitmap();

        radius = Math.max(height, width) / 2;
    }

    protected Sprite(GameEngine gameEngine, int drawableRes, Context context) {

        this.gameEngine = gameEngine;
        Resources r = gameEngine.getContext().getResources();
        Drawable spriteDrawable = r.getDrawable(drawableRes);
        paint = new Paint();
        this.pixelFactor = gameEngine.pixelFactor;

        this.width = (int) (spriteDrawable.getIntrinsicWidth() * this.pixelFactor * scale);
        this.height = (int) (spriteDrawable.getIntrinsicHeight() * this.pixelFactor * scale);

        this.bitmap = ((BitmapDrawable) spriteDrawable).getBitmap();

        radius = Math.max(height, width) / 2;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (positionX > canvas.getWidth()
                || positionY > canvas.getHeight()
                || positionX < -width
                || positionY < -height) {
            return;
        }
        //Testeo colisiones
//        paint.setColor(Color.YELLOW);
//        //Bounding Box Rectangular
////        canvas.drawRect(mBoundingRect, paint);
//        canvas.drawCircle(
//                (int) (positionX + width / 2),
//                (int) (positionY + height / 2),
//                (int) radius,
//                paint);
//        paint.setColor(Color.YELLOW);
//        if (mBodyType == BodyType.Circular) {
//            canvas.drawCircle(
//                    (int) (positionX + width / 2),
//                    (int) (positionY + height / 2),
//                    (int) radius,
//                    paint);
//        } else if (mBodyType == BodyType.Rectangular) {
//            canvas.drawRect(mBoundingRect, paint);
//        }
//        //Testeo colisiones
//        matrix.reset();
//        matrix.postScale((float) pixelFactor, (float) pixelFactor);
//        matrix.postTranslate((float) positionX, (float) positionY);
//        matrix.postRotate((float) rotation, (float) (positionX + width / 2), (float) (positionY + height / 2));
//        canvas.drawBitmap(bitmap, matrix, null);
        //Implementaci??n de lo mismo de arriba con escala y alpha
        float scaleFactor = (float) (pixelFactor * scale);
        matrix.reset();
        matrix.postScale(scaleFactor, scaleFactor);
        matrix.postTranslate((float) positionX, (float) positionY);
        matrix.postRotate((float) rotation,
                (float) (positionX + width / 2),
                (float) (positionY + height / 2));
        paint.setAlpha(alpha);
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    public void setBitmap(int drawableRes) {
        Resources r = gameEngine.getContext().getResources();
        Drawable spriteDrawable = r.getDrawable(drawableRes);
        this.bitmap = ((BitmapDrawable) spriteDrawable).getBitmap();
    }
}

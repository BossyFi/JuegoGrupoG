package dadm.scaffold.engine;

import android.graphics.Rect;

public abstract class ScreenGameObject extends GameObject {

    protected double positionX;
    protected double positionY;

    protected int width;
    protected int height;

    public double radius;

    public abstract void onCollision(GameEngine gameEngine, ScreenGameObject otherObject);

    public enum BodyType {
        None,
        Circular,
        Rectangular
    }

    protected BodyType mBodyType;
    public Rect mBoundingRect = new Rect(-1, -1, -1, -1);

    public void onPostUpdate(GameEngine gameEngine) {
        mBoundingRect.set(
                (int) positionX,
                (int) positionY,
                (int) positionX + width,
                (int) positionY + height);
    }

    public boolean checkCollision(ScreenGameObject otherObject) {
        //return checkCircularCollision(otherObject);
        if (mBodyType == BodyType.Circular
                && otherObject.mBodyType == BodyType.Circular) {
            return checkCircularCollision(otherObject);
        } else if (mBodyType == BodyType.Rectangular
                && otherObject.mBodyType == BodyType.Rectangular) {
            return checkRectangularCollision(otherObject);
        } else {
            return checkMixedCollision(otherObject);
        }
    }

    private boolean checkCircularCollision(ScreenGameObject other) {
        double distanceX = (positionX + width / 2) - (other.positionX + other.width / 2);
        double distanceY = (positionY + height / 2) - (other.positionY + other.height / 2);
        double squareDistance = distanceX * distanceX + distanceY * distanceY;
        double collisionDistance = (radius + other.radius);
        return squareDistance <= collisionDistance * collisionDistance;
    }


    private boolean checkRectangularCollision(ScreenGameObject other) {
        return Rect.intersects(mBoundingRect, other.mBoundingRect);
    }

    private boolean checkMixedCollision(ScreenGameObject other) {
        ScreenGameObject circularSprite;
        ScreenGameObject rectangularSprite;
        if (mBodyType == BodyType.Rectangular) {
            circularSprite = this;
            rectangularSprite = other;
        } else {
            circularSprite = other;
            rectangularSprite = this;
        }
        double circleCenterX = circularSprite.positionX +
                circularSprite.width / 2;
        double positionXToCheck = circleCenterX;
        if (circleCenterX < rectangularSprite.positionX) {
            positionXToCheck = rectangularSprite.positionX;
        } else if (circleCenterX > rectangularSprite.positionX +
                rectangularSprite.width) {
            positionXToCheck = rectangularSprite.positionX +
                    rectangularSprite.width;
        }
        double distanceX = circleCenterX - positionXToCheck;
        double circleCenterY = circularSprite.positionY +
                circularSprite.height / 2;
        double positionYToCheck = circleCenterY;
        if (circleCenterY < rectangularSprite.positionY) {
            positionYToCheck = rectangularSprite.positionY;
        } else if (circleCenterY > rectangularSprite.positionY +
                rectangularSprite.height) {
            positionYToCheck = rectangularSprite.positionY +
                    rectangularSprite.height;
        }
        double distanceY = circleCenterY - positionYToCheck;
        double squareDistance = distanceX * distanceX +
                distanceY * distanceY;
        // They are overlapping
        return squareDistance <=
                circularSprite.radius * circularSprite.radius;
    }
}

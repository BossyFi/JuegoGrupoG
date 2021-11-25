package dadm.scaffold.engine.particles;

/**
 * Created by Raul Portales on 06/04/15.
 */
public class ScaleModifier implements ParticleModifier {

    private double mInitialValue;
    private double mFinalValue;
    private long mEndTime;
    private long mStartTime;
    private long mDuration;
    private double mValueIncrement;

    public ScaleModifier (double initialValue, double finalValue, long startMilis, long endMilis) {
        mInitialValue = initialValue;
        mFinalValue = finalValue;
        mStartTime = startMilis;
        mEndTime = endMilis;
        mDuration = mEndTime - mStartTime;
        mValueIncrement = mFinalValue-mInitialValue;
    }

    @Override
    public void apply(Particle particle, long miliseconds) {
        if (miliseconds < mStartTime) {
            particle.scale = mInitialValue;
        }
        else if (miliseconds > mEndTime) {
            particle.scale = mFinalValue;
        }
        else {
            double percentageValue = (miliseconds- mStartTime)*1d/mDuration;
            double newScale = mInitialValue + mValueIncrement*percentageValue;
            particle.scale = newScale;
        }
    }
}
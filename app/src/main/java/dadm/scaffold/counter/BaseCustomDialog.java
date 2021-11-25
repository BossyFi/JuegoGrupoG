package dadm.scaffold.counter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;

public class BaseCustomDialog implements View.OnTouchListener, Animation.AnimationListener {
    private boolean isShowing;
    private boolean isHiding;
    protected final ScaffoldActivity parent;
    private ViewGroup rootLayout;
    private View rootView;

    public BaseCustomDialog(ScaffoldActivity activity) {
        parent = activity;
    }

    protected void onViewClicked() {
        // Ignore clicks on this view
    }

    protected void setContentView(int dialogResId) {
        ViewGroup activityRoot = (ViewGroup)
                parent.findViewById(android.R.id.content);
        rootView = LayoutInflater.from(parent).inflate(dialogResId,
                activityRoot, false);
        parent.applyTypeface(rootView);
    }

    public void show() {
        if (isShowing) {
            return;
        }
        isHiding = false;
        isShowing = true;
        ViewGroup activityRoot = (ViewGroup)
                parent.findViewById(android.R.id.content);
        rootLayout = (ViewGroup)
                LayoutInflater.from(parent).inflate(
                        R.layout.rootlayout, activityRoot, false);
        activityRoot.addView(rootLayout);
        rootLayout.setOnTouchListener(this);
        rootLayout.addView(rootView);
        startShowAnimation();
    }

    public void dismiss() {
        if (!isShowing) {
            return;
        }
        if (isHiding) {
            return;
        }
        isHiding = true;
        startHideAnimation();
//        hideViews();
    }

    private void hideViews() {
        rootLayout.removeView(rootView);
        ViewGroup activityRoot = (ViewGroup)
                parent.findViewById(android.R.id.content);
        activityRoot.removeView(rootLayout);
    }

    protected View findViewById(int id) {
        return rootView.findViewById(id);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Ignoring touch events on the gray outside
        return true;
    }

    public boolean isShowing() {
        return isShowing;
    }

    private void startShowAnimation() {
        Animation dialogIn = AnimationUtils.loadAnimation(parent,
                R.anim.dialog_in);
        rootView.startAnimation(dialogIn);
    }

    private void startHideAnimation() {
        Animation dialogOut = AnimationUtils.loadAnimation(parent,
                R.anim.dialog_out);
        dialogOut.setAnimationListener(this);
        rootView.startAnimation(dialogOut);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation paramAnimation) {
        hideViews();
        isShowing = false;
        onDismissed();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    protected void onDismissed() {
    }

}

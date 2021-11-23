package dadm.scaffold;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import dadm.scaffold.counter.BaseCustomDialog;


public class BaseFragment extends Fragment {
    BaseCustomDialog mCurrentDialog;

    public boolean onBackPressed() {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            mCurrentDialog.dismiss();
            return true;
        }
        return false;
    }

    protected ScaffoldActivity getScaffoldActivity() {
        return (ScaffoldActivity) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getScaffoldActivity().applyTypeface(view);
    }

    public void showDialog(BaseCustomDialog newDialog) {
        showDialog(newDialog, false);
    }

    public void showDialog(BaseCustomDialog newDialog,
                           boolean dismissOtherDialog) {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            if (dismissOtherDialog) {
                mCurrentDialog.dismiss();
            } else {
                return;
            }
        }
        mCurrentDialog = newDialog;
        mCurrentDialog.show();
    }
}

package dadm.scaffold;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;


public class BaseFragment extends Fragment {
    public boolean onBackPressed() {
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
}

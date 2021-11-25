package dadm.scaffold.counter;

import android.view.View;

import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;

public class QuitDialog extends BaseCustomDialog implements View.
        OnClickListener {
    private QuitDialogListener listener;
    private int selectedId;

    public QuitDialog(ScaffoldActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_quit);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
    }

    public void setListener(QuitDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        selectedId = v.getId();
        dismiss();
    }

    public interface QuitDialogListener {
        void exit();
    }

    @Override
    protected void onDismissed() {
        if (selectedId == R.id.btn_exit) {
            listener.exit();
        }
    }
}
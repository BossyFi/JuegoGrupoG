package dadm.scaffold.counter;

import android.view.View;

import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;

public class ChooseShipDialog extends BaseCustomDialog implements View.
        OnClickListener {

    private ChooseShipDialogListener listener;
    private int selectedId;

    public ChooseShipDialog(ScaffoldActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_choose_ship);
        findViewById(R.id.btn_ship1).setOnClickListener(this);
        findViewById(R.id.btn_ship2).setOnClickListener(this);
    }

    public void setListener(ChooseShipDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        selectedId = view.getId();
        dismiss();
    }


    public interface ChooseShipDialogListener {
        void chooseFirstShip();

        void chooseSecondShip();
    }

    @Override
    protected void onDismissed() {
        if (selectedId == R.id.btn_ship1) {
            listener.chooseFirstShip();
        } else if (selectedId == R.id.btn_ship2) {
            listener.chooseSecondShip();
        }
    }
}

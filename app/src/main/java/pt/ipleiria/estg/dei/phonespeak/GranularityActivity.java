package pt.ipleiria.estg.dei.phonespeak;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class GranularityActivity extends AppCompatActivity {

    private static final int REQUEST_SAVE = 1;
    private static final int REQUEST_SYNC = 2;
    private static final int REQUEST_SYNC_NO_CHANGES = 3;
    private Button btnNext;
    private boolean checked;
    private RadioButton rdBtnMonth;
    private RadioButton rdBtnYear;
    private String selected;

    private int selectedDate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_granularity);

        initialize();
    }

    private void initialize() {
        btnNext = (Button) findViewById(R.id.btnNextActivity);
        checked = false;
        rdBtnMonth = (RadioButton) findViewById(R.id.rdBtnMonth);
        rdBtnYear = (RadioButton) findViewById(R.id.rdBtnYear);
        selectedDate = 0;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SAVE && resultCode == RESULT_OK) {
            finish();
        } else if (resultCode == REQUEST_SYNC_NO_CHANGES || resultCode == REQUEST_SYNC) {
            setResult(resultCode);
            finish();
        }
    }

    public void onClickBtnNextActivity(View view) {
        if (checked) {
            Intent intent = new Intent(this, SyncDataActivity.class);/*
            intent.putExtra(getString(R.string.granularity), this.selected);
            intent.putExtra(getString(R.string.granularityDate), this.selectedDate);*/
            if (selectedDate == 0) {
                int interval = 60;
                switch (selected) {
                    case "minute":
                        interval = 60;
                        break;
                    case "hour":
                        interval = 60 * 60;
                        break;
                    case "day":
                        interval = 60 * 60 * 24;
                        break;
                    case "week":
                        interval = 60 * 60 * 24 * 7;
                        break;
                }
                intent.putExtra("selected", selected);
                intent.putExtra("interval", interval);

            } else {
                intent.putExtra("selected", selected);
                intent.putExtra("selectedDate", selectedDate);
            }

            startActivityForResult(intent, REQUEST_SAVE);
        } else {
            Toast.makeText(this, "Need to select a option", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSelectedDate(int selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void onRadioButtonClicked(View view) {
        checked = ((RadioButton) view).isChecked();


        switch (view.getId()) {
            case R.id.rdBtnMinute:
                if (checked) {
                    rdBtnMonth.setText(getString(R.string.month));
                    rdBtnYear.setText(getString(R.string.year));
                    this.selected = "minute";
                    this.selectedDate = 0;
                }
                break;
            case R.id.rdBtnHour:
                if (checked) {
                    rdBtnMonth.setText(getString(R.string.month));
                    rdBtnYear.setText(getString(R.string.year));
                    this.selected = "hour";
                    this.selectedDate = 0;
                }
                break;
            case R.id.rdBtnDay:
                if (checked) {
                    rdBtnMonth.setText(getString(R.string.month));
                    rdBtnYear.setText(getString(R.string.year));
                    this.selected = "day";
                    this.selectedDate = 0;
                }
                break;
            case R.id.rdBtnWeek:
                if (checked) {
                    rdBtnMonth.setText(getString(R.string.month));
                    rdBtnYear.setText(getString(R.string.year));
                    this.selected = "week";
                    this.selectedDate = 0;
                }
                break;
            case R.id.rdBtnMonth:
                if (checked) {
                    rdBtnYear.setText(getString(R.string.year));

                    final Dialog d = new Dialog(this);
                    d.setContentView(R.layout.dialog);
                    TextView label = (TextView) d.findViewById(R.id.lblDialogTitle);
                    Button b1 = (Button) d.findViewById(R.id.btnSetDialog);
                    Button b2 = (Button) d.findViewById(R.id.btnCancelDialog);
                    final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
                    np.setMinValue(1);
                    np.setMaxValue(31);
                    np.setWrapSelectorWheel(false);
                    label.setText(getString(R.string.choose_day));
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rdBtnMonth.setText(getString(R.string.month) + " (Day: " + String.valueOf(np.getValue()) + ")");
                            setSelectedDate(np.getValue());
                            d.dismiss();
                        }
                    });
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                            rdBtnMonth.setText(getString(R.string.month));
                        }
                    });
                    d.show();
                    this.selected = "month";
                }
                break;
            case R.id.rdBtnYear:
                if (checked) {
                    rdBtnMonth.setText(getString(R.string.month));

                    final Dialog d = new Dialog(this);
                    d.setContentView(R.layout.dialog);
                    TextView label = (TextView) d.findViewById(R.id.lblDialogTitle);
                    Button b1 = (Button) d.findViewById(R.id.btnSetDialog);
                    Button b2 = (Button) d.findViewById(R.id.btnCancelDialog);
                    final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
                    np.setMinValue(1);
                    np.setMaxValue(12);
                    np.setWrapSelectorWheel(false);
                    label.setText(getString(R.string.choose_month));
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rdBtnYear.setText(getString(R.string.year) + " (Month: " + String.valueOf(np.getValue()) + ")");
                            setSelectedDate(np.getValue());
                            d.dismiss();
                        }
                    });
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                            rdBtnYear.setText(getString(R.string.year));
                        }
                    });
                    d.show();
                    this.selected = "year";
                }
                break;
        }
    }
}

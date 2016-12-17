package project.pedestrianstime;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import static android.R.attr.data;

/**
 * Created by админ on 17.12.2016.
 */

public class SettingsActivity extends AppCompatActivity {

    private Spinner cascades = null;
    String[] data = {"first_cascade", "second_cascade", "third_cascade", "fourth_cascade"};
    public EditText scaleFactor = null;
    public int cascadeNum=1;
    public EditText minNeighbors = null;
    private ImageButton apply = null;
    private ImageButton defaultButton = null;
    private EditText FSC = null;
    private EditText min_width = null;
    private EditText min_height = null;
    private EditText max_width = null;
    private EditText max_height = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent main = getIntent();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cascades = (Spinner) findViewById(R.id.spinner);
        cascades.setAdapter(adapter);
        cascades.setSelection(Integer.parseInt(main.getStringExtra("cascade")));

        cascades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //intent.putExtra("cascade_num", position);
                cascadeNum = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        scaleFactor = (EditText) findViewById(R.id.editText);
        scaleFactor.setText(main.getStringExtra("scaleFactor"));

        minNeighbors = (EditText) findViewById(R.id.editText2);
        minNeighbors.setText(main.getStringExtra("minNeighbors"));

        FSC = (EditText) findViewById(R.id.editText3);
        FSC.setText(main.getStringExtra("FSC"));

        min_height = (EditText) findViewById(R.id.editText5);
        min_height.setText(main.getStringExtra("min_height"));

        min_width = (EditText) findViewById(R.id.editText4);
        min_width.setText(main.getStringExtra("min_width"));

        max_height = (EditText) findViewById(R.id.editText7);
        max_height.setText(main.getStringExtra("max_height"));

        max_width = (EditText) findViewById(R.id.editText6);
        max_width.setText(main.getStringExtra("max_width"));

        apply = (ImageButton) findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("exist", "success");
                intent.putExtra("cascade", String.valueOf(cascadeNum));
                intent.putExtra("scaleFactor",scaleFactor.getText().toString());
                intent.putExtra("minNeighbors",minNeighbors.getText().toString());
                intent.putExtra("FSC",FSC.getText().toString());
                intent.putExtra("min_width",min_width.getText().toString());
                intent.putExtra("min_height",min_height.getText().toString());
                intent.putExtra("max_width",max_width.getText().toString());
                intent.putExtra("max_height",max_height.getText().toString());
                startActivity(intent);
            }
        });

        defaultButton = (ImageButton) findViewById(R.id.defaultButton);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cascades.setSelection(2);
                scaleFactor.setText("1.1");
                minNeighbors.setText("3");
                FSC.setText("0");
                min_height.setText("80");
                min_width.setText("30");
                max_height.setText("480");
                max_width.setText("153");
            }
        });
    }
}

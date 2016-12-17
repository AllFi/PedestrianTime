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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cascades = (Spinner) findViewById(R.id.spinner);
        cascades.setAdapter(adapter);
        cascades.setSelection(2);

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
        scaleFactor.setText("1.1");

        minNeighbors = (EditText) findViewById(R.id.editText2);
        minNeighbors.setText("3");

        apply = (ImageButton) findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("exist", "success");
                intent.putExtra("cascade", String.valueOf(cascadeNum));
                intent.putExtra("scaleFactor",scaleFactor.getText().toString());
                intent.putExtra("minNeighbors",minNeighbors.getText().toString());
                startActivity(intent);
            }
        });
    }
}

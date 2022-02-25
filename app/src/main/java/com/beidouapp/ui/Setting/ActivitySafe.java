package com.beidouapp.ui.Setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beidouapp.R;

public class ActivitySafe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);

        Toolbar toolbar = findViewById(R.id.safe1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        EditText editText = findViewById(R.id.edit_text);
        AutoSeparateTextWatcher textWatcher = new AutoSeparateTextWatcher(editText);
        textWatcher.setRULES(new int[]{3,4,4});
        textWatcher.setSeparator('-');
        editText.addTextChangedListener(textWatcher);

        Button btn_mima = findViewById(R.id.btn_mima);
        btn_mima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mEdit = (EditText) findViewById(R.id.text1);
                mEdit.setEnabled(true);
            }
        });

        Button btn_num = findViewById(R.id.btn_shouji);
        btn_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mEdit = (EditText) findViewById(R.id.edit_text);
                mEdit.setEnabled(true);
            }
        });
        Button btn_yx = findViewById(R.id.btn_youxiang);
        btn_yx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mEdit = (EditText) findViewById(R.id.youxiang);
                mEdit.setEnabled(true);
            }
        });
    }


}

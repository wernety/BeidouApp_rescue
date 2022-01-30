package com.beidouapp.ui.Setting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beidouapp.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityUser extends AppCompatActivity {
    private Spinner yearspinner;
    private Spinner monthspinner;
    private Spinner dayspinner;
    private TextView textView;
    //声明布局变量
    private List<Integer> year;
    // 声明 用来储存年份的list    <Integer>表示数据类型
    private List<Integer> month;
    //同上
    private List<Integer> day;
    //同上
    private ArrayAdapter<Integer> yearadapter;
    private ArrayAdapter<Integer> monthadapter;
    private ArrayAdapter<Integer> datadapter;
    //定义年月日的适配器
    private DateData dateData;
    //声明 之前用来年份处理的DateData对象
    private int theday;
    //声明 int类型的theday

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.user1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        yearspinner = findViewById(R.id.add_year_spinner);
        monthspinner = findViewById(R.id.add_month_spinner);
        dayspinner = findViewById(R.id.add_day_spinner);
//给布局对象分配id

        year = new ArrayList<Integer>();//给列表分配空间
        for (int i = 1949; i <= 2050; i++) {//把年份的选项加入year列表中；
            year.add(i);
        }
        dateData = new DateData();//给年份处理的对象分配空间
        yearadapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, year);
        //把年份的列表添加到适配器中 还有他的下拉选项
        yearadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);//
        yearspinner.setAdapter(yearadapter);//把布局加入适配器 这边我们用默认布局
        yearspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override//
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dateData.Setyear(yearadapter.getItem(position));//用DateData对象来处理选中的年份
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //这是未选中的时候 这里我们不写
            }
        });
        month = new ArrayList<Integer>();//给month列表分配空间
        for (int i = 1; i <= 12; i++) {
            month.add(i);//给month添加数据 1-12个月
        }
        //接下来和上面年份的一样
        monthadapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, month);
        monthadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        monthspinner.setAdapter(monthadapter);
        monthspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //当选中月数后 才能给day的Spinner分配天数 毕竟有的月份天数不同 所以我们把整个对day Spinner对象的处理全部放在 month Spinner被选中后的时间监听处理函数中
                // 提取出当前月分的天数
                theday = dateData.getMonth(position);
                //这里和年份处理一样
                day = new ArrayList<Integer>();
                for (int i = 1; i <= theday; i++) {
                    day.add(i);
                }
                datadapter = new ArrayAdapter<Integer>(ActivityUser.this, android.R.layout.simple_spinner_item, day);
                datadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                dayspinner.setAdapter(datadapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

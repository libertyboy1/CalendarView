package com.view.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.view.calendar.view.CalendarCard;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener{
	private RadioButton rb_single,rb_multi;
	private RadioGroup rb_main;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		setListener();
	}

	private void setListener() {
		rb_main.setOnCheckedChangeListener(this);
	}

	private void initView() {
		rb_single= (RadioButton) findViewById(R.id.rb_single);
		rb_multi= (RadioButton) findViewById(R.id.rb_multi);
		rb_main= (RadioGroup) findViewById(R.id.rb_main);
	}


	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int i) {
		switch (i){
			case R.id.rb_single:
				CalendarCard.isDoubleChoose=false;
				break;
			case R.id.rb_multi:
				CalendarCard.isDoubleChoose=true;
				break;
		}
	}

}

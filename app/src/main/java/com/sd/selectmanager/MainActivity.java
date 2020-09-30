package com.sd.selectmanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_simple_demo:
                startActivity(new Intent(this, SimpleDemoActivity.class));
                break;
            case R.id.btn_list_demo:
                startActivity(new Intent(this, ListDemoActivity.class));
                break;
        }
    }
}

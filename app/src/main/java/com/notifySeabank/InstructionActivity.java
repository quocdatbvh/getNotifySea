package com.notifySeabank;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstructionActivity extends AppCompatActivity {
    @BindView(R.id.switch_permission)
    SwitchCompat switchPermission;
    @BindView(R.id.cl_instruction)
    ConstraintLayout clInstruction;
    @BindView(R.id.tv_instruction)
    TextView tvInstruction;
    @BindView(R.id.tv_setting)
    TextView tvSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        ButterKnife.bind(this);

        String setting = getIntent().getStringExtra(Constants.EXTRA_SETTING);
        tvSetting.setText(setting);
        String instruction = getIntent().getStringExtra(Constants.EXTRA_INSTRUCTION);
        if (instruction == null) {
            final boolean[] isOn = {false};
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!isOn[0]) {
                        handler.postDelayed(this, 2000);
                        isOn[0] = true;
                        switchPermission.setChecked(true);
                    } else {
                        handler.postDelayed(this, 1000);
                        isOn[0] = false;
                        switchPermission.setChecked(false);
                    }
                }
            };

            handler.postDelayed(runnable, 1000);
        } else {
            clInstruction.setVisibility(View.GONE);
            tvInstruction.setText(instruction);
        }
    }

    @OnClick(R.id.bt_next)
    void onClickNext() {
        onBackPressed();
    }
}
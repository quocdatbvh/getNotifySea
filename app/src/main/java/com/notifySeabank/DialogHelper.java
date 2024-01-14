package com.notifySeabank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.thelittlefireman.appkillermanager.managers.KillerManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;


public class DialogHelper {
    private Context context;
    private Activity activity;
    private AlertDialog dialogLoading;
    private AlertDialog fullScreenDialog;
    private TextView tvLoading;
    private boolean enableSendReport = false;
    private Runnable instructionRunnable;
    private Handler instructionHandler;
    private AlertDialog matchWordInstructionDialog;

    public DialogHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public static void setDialogWidth(Activity activity, AlertDialog alertDialog, float widthPercent) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = (int) (displayWidth * widthPercent);
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    public static void showRequestAutoStartDialog(Context context, Activity activity, RequestPermissionListener listener) {
        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_request_auto_start, null);
        AlertDialog permissionDialog = new AlertDialog.Builder(context).create();

        if (permissionDialog.getWindow() != null) {
            permissionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        SwitchCompat switchAutoStart = view.findViewById(R.id.switch_auto_start);
        TextView tvAllow = view.findViewById(R.id.tv_ok);
        TextView tvInstruction = view.findViewById(R.id.tv_instruction);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);

        final boolean[] isOn = {false};
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (!isOn[0]) {
                    handler.postDelayed(this, 2000);
                    isOn[0] = true;
                    switchAutoStart.setChecked(true);
                } else {
                    handler.postDelayed(this, 1000);
                    isOn[0] = false;
                    switchAutoStart.setChecked(false);
                }

            }
        };

        handler.postDelayed(runnable, 1000);

        tvAllow.setOnClickListener(v -> {
            KillerManager.doActionAutoStart(context);
            permissionDialog.dismiss();
            listener.onAllowAutoStart();
        });

        tvInstruction.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.link_auto_start_battery_optimization_instruction)));
            context.startActivity(browserIntent);
        });

        tvCancel.setOnClickListener(v -> {
            handler.removeCallbacks(runnable);
            permissionDialog.dismiss();
        });

        permissionDialog.setView(view);
        permissionDialog.setCancelable(false);
        permissionDialog.show();

        // Đặt lại chiều rộng dialog
        setDialogWidth(activity, permissionDialog, 0.9f);
    }

    public static void showBatteryOptimizationDialog(Context context, Activity activity, RequestPermissionListener listener) {
        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_request_battery_optimization, null);
        AlertDialog permissionDialog = new AlertDialog.Builder(context).create();

        if (permissionDialog.getWindow() != null) {
            permissionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvAllow = view.findViewById(R.id.tv_ok);
        TextView tvInstruction = view.findViewById(R.id.tv_instruction);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);

        tvAllow.setOnClickListener(v -> {
            KillerManager.doActionPowerSaving(context);
            permissionDialog.dismiss();
            listener.onAllowBatteryOptimization();
        });

        tvInstruction.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.link_auto_start_battery_optimization_instruction)));
            context.startActivity(browserIntent);
        });

        tvCancel.setOnClickListener(v -> {
            permissionDialog.dismiss();
        });

        permissionDialog.setView(view);
        permissionDialog.setCancelable(false);
        permissionDialog.show();

        // Đặt lại chiều rộng dialog
        setDialogWidth(activity, permissionDialog, 0.9f);
    }

    public interface RequestPermissionListener {
        void onAllowAutoStart();
        void onAllowBatteryOptimization();
    }
}

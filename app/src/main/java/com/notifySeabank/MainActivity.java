package com.notifySeabank;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.thelittlefireman.appkillermanager.managers.KillerManager;
import com.thelittlefireman.appkillermanager.utils.Manufacturer;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static com.notifySeabank.NotiCatchService.ACTION_RECEIVE_NOTIFICATION;

public class MainActivity extends AppCompatActivity implements DialogHelper.RequestPermissionListener,
NotificationAdapter.NotificationClickListener, NewNotificationReceiver.NotificationListener{
    @BindView(R.id.cl_request_permission)
    ConstraintLayout clRequestPermission;
    @BindView(R.id.iv_enable_access_notification)
    ImageView ivEnableAccessNotification;
    @BindView(R.id.cl_auto_start)
    ConstraintLayout clAutoStart;
    @BindView(R.id.iv_enable_auto_start)
    ImageView ivEnableAutoStart;
    @BindView(R.id.cl_battery_optimization)
    ConstraintLayout clBatteryOptimization;
    @BindView(R.id.iv_enable_battery_optimization)
    ImageView ivEnableBatteryOptimization;
    @BindView(R.id.bt_enable_permission)
    Button btEnablePermission;
    @BindView(R.id.cl_empty_list)
    ConstraintLayout clEmptyList;
    @BindView(R.id.rv_notification)
    RecyclerView rvNotification;

    private boolean isAutoStartAvailable = false;
    private boolean isBatteryOptimizationAvailable = false;
    private boolean isAutoStartEnable = false;
    private boolean isBatteryOptimizationEnable = false;
    private int countPermission = 1;
    private int countStep = 1;
    private NewNotificationReceiver newNotificationReceiver;
    private IntentFilter intentFilter;
    private NotificationAdapter adapter;
    private List<NotificationInfo> listNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        isAutoStartAvailable = KillerManager.isActionAvailable(this, KillerManager.Actions.ACTION_AUTOSTART);
        if (isAutoStartAvailable) {
            countPermission++;
        }

        isBatteryOptimizationAvailable = KillerManager.isActionAvailable(this, KillerManager.Actions.ACTION_POWERSAVING);
        if (isBatteryOptimizationAvailable) {
            countPermission++;
        }

        String buttonText = getString(R.string.enable_permission) + " " + countStep + "/" + countPermission;
        btEnablePermission.setText(buttonText);

        // Yêu cầu quyền
        if (!isNotificationAccessEnable()) {
            if (!isAutoStartAvailable) {
                clAutoStart.setVisibility(View.GONE);
            }

            if (!isBatteryOptimizationAvailable) {
                clBatteryOptimization.setVisibility(View.GONE);
            }

            clRequestPermission.setVisibility(View.VISIBLE);
        } else {
            openNotificationScreen();
            if (KillerManager.getDevice().getDeviceManufacturer().equals(Manufacturer.XIAOMI)) {
                LogUtil.d("receiveee", "xiaomi");
                startNotiCatchService();
            }
        }

        listNotification = new ArrayList<>();
        adapter = new NotificationAdapter(this, this, listNotification, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false);
        rvNotification.setLayoutManager(layoutManager);
        rvNotification.setAdapter(adapter);
        listNotification.addAll(DatabaseManager.getInstance(this).getAllNotification());
        adapter.setItems(listNotification);
        if (listNotification.size() > 0) {
            clEmptyList.setVisibility(View.INVISIBLE);
        } else {
            clEmptyList.setVisibility(View.VISIBLE);
        }

        newNotificationReceiver = new NewNotificationReceiver(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_RECEIVE_NOTIFICATION);
        registerReceiver(newNotificationReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newNotificationReceiver);
        super.onDestroy();
    }

    private boolean isNotificationAccessEnable() {
        ComponentName cn = new ComponentName(this, NotiCatchService.class);
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }

    private void startNotiCatchService() {
        try {
            ComponentName componentName = new ComponentName(this, NotiCatchService.class);
            PackageManager packageManager = this.getPackageManager();
            packageManager.setComponentEnabledSetting(componentName, COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(componentName, COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            LogUtil.d("exceptionnn", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_NOTIFICATION_PERMISSION) {
            LogUtil.d("skvnksnv", "result notification");
            if (isNotificationAccessEnable()) {
                ivEnableAccessNotification.setImageDrawable(Utils.getDrawable(this, R.drawable.ic_correct));
                if (countStep < countPermission) {
                    countStep++;
                }
                String buttonText = getString(R.string.enable_permission) + " " + countStep + "/" + countPermission;
                btEnablePermission.setText(buttonText);

                if (!isAutoStartAvailable && !isBatteryOptimizationAvailable) {
                    // Ẩn layout yêu cầu quyền
                    openNotificationScreen();
                }
            }
        }
    }

    private void openNotificationScreen() {
        clRequestPermission.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.bt_enable_permission)
    void openNotificationPermission() {
        if (!isNotificationAccessEnable()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivityForResult(intent, Constants.REQUEST_NOTIFICATION_PERMISSION);
            new Handler().postDelayed(() -> {
                Intent intent1 = new Intent(this, InstructionActivity.class);
                startActivity(intent1);
            }, 500);
        } else {
            if (isAutoStartAvailable) {
                if (!isAutoStartEnable) {
                    DialogHelper.showRequestAutoStartDialog(this, this, this);
                } else {
                    if (isBatteryOptimizationAvailable) {
                        if (!isBatteryOptimizationEnable) {
                            DialogHelper.showBatteryOptimizationDialog(this, this, this);
                        }
                    }
                }
            } else {
                if (isBatteryOptimizationAvailable) {
                    if (!isBatteryOptimizationEnable) {
                        DialogHelper.showBatteryOptimizationDialog(this, this, this);
                    }
                }
            }
        }
    }

    @Override
    public void onAllowAutoStart() {
        // Cập nhật trạng thái cấp quyền
        ivEnableAutoStart.setImageDrawable(Utils.getDrawable(this, R.drawable.ic_correct));
        isAutoStartEnable = true;
        if (countStep < countPermission) {
            countStep++;
        }
        String buttonText = getString(R.string.enable_permission) + " " + countStep + "/" + countPermission;
        btEnablePermission.setText(buttonText);

        if (!isBatteryOptimizationAvailable) {
            openNotificationScreen();
        }
    }

    @Override
    public void onAllowBatteryOptimization() {
        // Cập nhật trạng thái cấp quyền
        LogUtil.d("skvnksnv", "result battery");
        ivEnableBatteryOptimization.setImageDrawable(Utils.getDrawable(this, R.drawable.ic_correct));
        isBatteryOptimizationEnable = true;
        if (countStep < countPermission) {
            countStep++;
        }
        String buttonText = getString(R.string.enable_permission) + " " + countStep + "/" + countPermission;
        btEnablePermission.setText(buttonText);

        openNotificationScreen();
    }

    @Override
    public void onClickNotification(NotificationInfo notificationInfo, int itemPosition) {

    }

    @Override
    public void onNewNotificationAdded() {
        rvNotification.scrollToPosition(0);
    }

    @Override
    public void onNewNotificationPosted(NotificationInfo newNotification) {
        if (clEmptyList.getVisibility() == View.VISIBLE) {
            clEmptyList.setVisibility(View.INVISIBLE);
        }
        listNotification.add(newNotification);
        adapter.addItem(newNotification);

        //todo gọi api
    }
}
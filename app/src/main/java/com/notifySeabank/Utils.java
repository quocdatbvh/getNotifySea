package com.notifySeabank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.res.ResourcesCompat;


/**
 * Created by Duy on 05/21/2019.
 */

public class Utils {
    private Context context;
    public static int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    public static int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    public Utils(Context context) {
        this.context = context;
    }

    public int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getFormattedTime(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static List<String> getListSaveNotificationPackageName(Context context) {
        List<String> listPackageName = new ArrayList<>();
//        listPackageName.add(Constants.MESSENGER_PACKAGE_NAME);
//        listPackageName.add(Constants.ZALO_PACKAGE_NAME);
//        listPackageName.add("vn.com.techcombank.bb.app");
//        listPackageName.add("com.fastacash.tcb");
//        listPackageName.add("vn.com.seabank.mb1");
        listPackageName.add("mobile.acb.com.vn");
        listPackageName.add("vn.com.seabank.mb1");
        return listPackageName;
    }

    public static Drawable getAppIconFromPackageName(Context context, String packageName) {
        //todo Nếu app bị gỡ thì để icon mặc định, để tạm icon app mình
        Drawable icon;
        try {
            icon = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.d(LogUtil.TAG_EXCEPTION, e.getMessage());
            icon = getDrawable(context, R.mipmap.ic_launcher);
        }

        return icon;
    }

    public static String getAppNameFromPackageName(Context context, String packageName) {
        String appName = "";
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
            appName = pm.getApplicationLabel(ai).toString();
        } catch (Exception e) {
            LogUtil.d(LogUtil.TAG_EXCEPTION, e.getMessage());
            appName = packageName;
        }

        return appName;
    }

    public static Drawable getIconFromOtherApp(Context context, String packageName, int iconId) {
        Drawable icon = null;
        try {
            PackageManager manager = context.getPackageManager();
            Resources resources = manager.getResourcesForApplication(packageName);
            icon = resources.getDrawable(iconId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return icon;
    }

    /**
     * @param inputDate ngày tháng VD:13/10/2018
     * @return mảng String {ngày,tháng,năm,ngày trong tuần}
     */
    public static String[] dateToArray(String inputDate) {
        String[] s = inputDate.split("/");
        String day = s[0];
        String month = s[1];
        String year = s[2];
        Calendar c = Calendar.getInstance();
        Date date2 = new Date(month + "/" + day + "/" + year);
        c.setTime(date2);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int inputDay = c.get(Calendar.DAY_OF_MONTH);
        int inputMonth = c.get(Calendar.MONTH);
        int inputYear = c.get(Calendar.YEAR);
        Date currentDate = new Date();
        c.setTime(currentDate);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentMonth = c.get(Calendar.MONTH);
        int currentYear = c.get(Calendar.YEAR);
        String dow = "";
        if (inputDay == currentDay && inputMonth == currentMonth && inputYear == currentYear)
            dow = "Hôm nay";
        else if (inputDay == (currentDay - 1) && inputMonth == currentMonth && inputYear == currentYear)
            dow = "Hôm qua";
        else {
            if (dayOfWeek == 1)
                dow = "Chủ nhật";
            else if (dayOfWeek == 2)
                dow = "Thứ 2";
            else if (dayOfWeek == 3)
                dow = "Thứ 3";
            else if (dayOfWeek == 4)
                dow = "Thứ 4";
            else if (dayOfWeek == 5)
                dow = "Thứ 5";
            else if (dayOfWeek == 6)
                dow = "Thứ 6";
            else if (dayOfWeek == 7)
                dow = "Thứ 7";
        }
        String[] result = new String[]{day, month, year, dow};

        return result;
    }

    public static String getFormattedPostDate(Context context, long timeMillis) {
        String formattedTime;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int inputDay = calendar.get(Calendar.DAY_OF_MONTH);
        int inputMonth = calendar.get(Calendar.MONTH);
        int inputYear = calendar.get(Calendar.YEAR);
        Date currentDate = new Date();
        calendar.setTime(currentDate);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        if (inputDay == currentDay && inputMonth == currentMonth && inputYear == currentYear) {
            formattedTime = "Hôm nay";
        } else {
            String lang = Locale.getDefault().getLanguage();
            String[] arrDate = getDateInfo(context, timeMillis);
            LogUtil.d("languageee", lang);
            if ("vi".equals(lang)) {
                formattedTime = arrDate[0] + " " + arrDate[1] + ", " + arrDate[2];
            } else {
                formattedTime = arrDate[1] + " " + arrDate[0] + ", " + arrDate[2];
            }
        }

        return formattedTime;
    }

    /**
     * Nếu trong ngày thì hiện giờ, nếu từ hôm qua trở về trước thì hiện ngày tháng
     *
     * @param context
     * @param timeMillis thời gian tin nhắn đến
     * @return thời gian đã format
     */
    public static String getFormattedPostTime(Context context, long timeMillis) {
        String formattedTime;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        Date inputDate = calendar.getTime();
        int inputDay = calendar.get(Calendar.DAY_OF_MONTH);
        int inputMonth = calendar.get(Calendar.MONTH);
        int inputYear = calendar.get(Calendar.YEAR);
        Date currentDate = new Date();
        calendar.setTime(currentDate);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        if (inputDay == currentDay && inputMonth == currentMonth && inputYear == currentYear) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            formattedTime = sdf.format(inputDate);
        } else {
            String lang = Locale.getDefault().getLanguage();
            String[] arrDate = getDateInfo(context, timeMillis);
            LogUtil.d("languageee", lang);
            if ("vi".equals(lang)) {
                formattedTime = arrDate[0] + " " + arrDate[1] + ", " + arrDate[2];
            } else {
                formattedTime = arrDate[1] + " " + arrDate[0] + ", " + arrDate[2];
            }
        }

        return formattedTime;
    }

    public static String getFullFormattedPostTime(Context context, long timeMillis) {
        String formattedTime;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        Date inputDate = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = sdf.format(inputDate);

        String lang = Locale.getDefault().getLanguage();
        String[] arrDate = getDateInfo(context, timeMillis);
        LogUtil.d("languageee", lang);
        if ("vi".equals(lang)) {
            formattedTime = arrDate[0] + " " + arrDate[1] + ", " + arrDate[2] + " " + time;
        } else {
            formattedTime = arrDate[1] + " " + arrDate[0] + ", " + arrDate[2] + " " + time;
        }

        return formattedTime;
    }

    public static int getFormattedDateByTimeMillis(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        Date inputDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        return Integer.parseInt(sdf.format(inputDate));
    }

    public static String getTimeFromTimeMillis(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        Date inputDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        return sdf.format(inputDate);
    }

    /**
     * @return mảng String {ngày,tháng,năm}
     */
    private static String[] getDateInfo(Context context, long timeMillis) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);

        String day = String.valueOf(calendar.get(Calendar.DATE));

        String year = String.valueOf(calendar.get(Calendar.YEAR));
        // Tháng bắt đầu từ 0
        int month = calendar.get(Calendar.MONTH) + 1;
        String strMonth = getMonthString(context, month);

        return new String[]{day, strMonth, year};
    }

    private static String getMonthString(Context context, int month) {
        String strMonth = "";
        switch (month) {
            case 1:
                strMonth = context.getString(R.string.january);
                break;
            case 2:
                strMonth = context.getString(R.string.february);
                break;
            case 3:
                strMonth = context.getString(R.string.march);
                break;
            case 4:
                strMonth = context.getString(R.string.april);
                break;
            case 5:
                strMonth = context.getString(R.string.may);
                break;
            case 6:
                strMonth = context.getString(R.string.june);
                break;
            case 7:
                strMonth = context.getString(R.string.july);
                break;
            case 8:
                strMonth = context.getString(R.string.august);
                break;
            case 9:
                strMonth = context.getString(R.string.september);
                break;
            case 10:
                strMonth = context.getString(R.string.october);
                break;
            case 11:
                strMonth = context.getString(R.string.november);
                break;
            case 12:
                strMonth = context.getString(R.string.december);
                break;
        }
        return strMonth;
    }

    public static String getStringCapitalized(String text) {
        return "- " + text.substring(0, 1).toUpperCase() + text.substring(1) + ".";
    }

    public static int getTopicItemHeight(Context context) {
        return (Utils.screenHeight - Utils.getStatusBarHeight(context)) / 72 * 10;
    }

    public static int getTestItemHeight(Context context) {
        return (Utils.screenHeight - Utils.getStatusBarHeight(context)) / 8;
    }

    public static void openMessenger(Context context) {
        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://m.me/104058438354243")));
    }

    public static void setGrayscaleImage(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

    /**
     * @return chiều cao của status bar
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Chuyển từ dp sang px
     */
    public int dpToPx(float dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics())
        );
    }

    public static int dpToPx(Context context, float dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics())
        );
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (conMgr != null) {
            netInfo = conMgr.getActiveNetworkInfo();
        }
        return !(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable());
    }

    public void setDrawableBackground(View v, int id) {
        v.setBackground(Utils.getDrawable(context, id));
    }

    public Drawable getDrawableFromName(String name) {
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return Utils.getDrawable(context, resourceId);
    }

    public static int parseInt(String number) {
        // Bỏ kí tự BOM
        number = number.replaceAll("\\uFEFF", "");
        return Integer.parseInt(number);
    }

    public static String getStringResourceFromName(Context context, String name) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(name, "string", packageName);
        return context.getString(resId);
    }

    public static String getTimeNow() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
        return df.format(new Date());
    }

    /**
     * Trả về ngày hôm nay
     *
     * @return DateToday
     */
    public static String getDateToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return sdf.format(new Date());
    }

    public static Drawable getDrawable(Context context, int drawableId) {
        return ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
    }

    public static boolean checkSoundMute(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volume = 0;
        if (audio != null) {
            volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return volume == 0;
    }

    public static void changeLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration,
                context.getResources().getDisplayMetrics());
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            LogUtil.d(LogUtil.TAG_EXCEPTION, e.getMessage());
        }
    }
}

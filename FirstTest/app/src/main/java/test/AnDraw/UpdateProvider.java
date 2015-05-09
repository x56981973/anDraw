package test.AnDraw;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import test.AnDraw.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;

/**
 * Created by jason 15-4-1.
 * this class is called to check new release from www
 */
public class UpdateProvider {
    private static final String savePath = "/sdcard/updateapk/";
    private static final String saveFileName = savePath + "AnDraw.apk";
    private String TAG = "UpdateProvider";
    private Context mContext;
    private int versionp;
    private int versionoCode = 0;
    private String versionName = "0.0";

    private Boolean flag;

    public UpdateProvider(Context context) {
        this.mContext = context;
        this.versionp = getVersionCode(context);
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersion(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return context.getResources().getString(R.string.url_ver_update);
        }
    }

    public void checkUpdateInfo() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(mContext.getResources().getString(R.string.url_ver_update));
                    ;
                    Log.i(TAG, "ver url = " + mContext.getResources().getString(R.string.url_ver_update));
                    HttpResponse response = client.execute(httpget);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String temp = EntityUtils.toString(response.getEntity()).trim();
                        Log.i(TAG, "temp == " + temp);
                        String[] tempList = temp.split(" ");
                        versionoCode = Integer.valueOf(tempList[0]).intValue();
                        versionName = tempList[1];
                        Log.i("versionCode == ", versionoCode + "   versionName ==" + versionName);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.i(TAG, "versionp = " + versionp);
                if (versionoCode > versionp) {
                    Log.i(TAG, "version2" + "n" + versionoCode + "p" + versionp + "fin");
                    showNoticeDialog();
                    flag = true;
                } else {
                    flag = false;
                    Log.i(TAG, "flag = " + flag);
                }
                super.onPostExecute(aVoid);
            }

        }.execute();

    }

    public void checkUpdateInfoWithToast() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(mContext.getResources().getString(R.string.url_ver_update));
                    ;
                    Log.i(TAG, "ver url = " + mContext.getResources().getString(R.string.url_ver_update));
                    HttpResponse response = client.execute(httpget);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String temp = EntityUtils.toString(response.getEntity()).trim();
                        Log.i(TAG, "temp == " + temp);
                        String[] tempList = temp.split(" ");
                        versionoCode = Integer.valueOf(tempList[0]).intValue();
                        versionName = tempList[1];
                        Log.i("versionCode == ", versionoCode + "   versionName ==" + versionName);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.i(TAG, "versionp = " + versionp);
                if (versionoCode > versionp) {
                    Log.i(TAG, "version2" + "n" + versionoCode + "p" + versionp + "fin");
                    showNoticeDialog();

                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.string_unavailable_update), Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(aVoid);
            }

        }.execute();

    }

    private void showNoticeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.btn_update));
        builder.setMessage(mContext.getResources().getString(R.string.string_available_update) + "Version " + versionName);
        builder.setPositiveButton(mContext.getResources().getString(R.string.string_yes_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();
                dialog.cancel();
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.string_no_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void downloadApk() {
        String apkUrl;
        apkUrl = mContext.getResources().getString(R.string.url_file_update);

        Uri uri = Uri.parse(apkUrl);
        File apkFile = new File(savePath + saveFileName);
        if (apkFile.exists()) {
            apkFile.delete();
        }

        DownloadManager.Request download = new DownloadManager.Request(uri)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir("updateapk", "AnDraw.apk");

        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Service.DOWNLOAD_SERVICE);
        downloadManager.enqueue(download);
    }

    private void installApk() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists())
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }
}

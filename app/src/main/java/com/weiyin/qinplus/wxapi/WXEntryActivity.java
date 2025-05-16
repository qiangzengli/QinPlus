package com.weiyin.qinplus.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.bluetooth.BlueToothControl;

import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    public static final String STitle = "showmsg_title";
    public static final String SMessage = "showmsg_message";
    public static final String BAThumbData = "showmsg_thumb_data";
    String mAccess_token = "";
    String mOpenId = "";

    SharedPreferences.Editor editor;
    public static String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?"
            + "appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    // IWXAPI 是第三方app和微信通信的openapi接口

    BlueToothControl blueToothControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        blueToothControl = BlueToothControl.getBlueToothInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("wxuser", 0);
        editor = sharedPreferences.edit();
        WinYinPianoApplication.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WinYinPianoApplication.api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                String code = sendResp.code;
//                getParseAccessTokenJSON(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                finish();
                break;
            default:
                result = R.string.errcode_unknown;
                finish();
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

//    void getParseAccessTokenJSON(String code) {
//        String strReqUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + Constant.APP_ID +
//                "&secret=" + Constant.SECRET + "&code=" + code + "&grant_type=authorization_code";
//        HttpData datas = new HttpData(strReqUrl, false, Constant.INTERNET_WX);
//        NetUtils.sendRequest(datas, this, this);
//    }
//
//    void getParseUnionIdJson(String token, String open_id) {
//        String strReqUrl = "https://api.weixin.qq.com/sns/userinfo?"
//                + "access_token=" + token + "&openid=" + open_id;
//        HttpData datas = new HttpData(strReqUrl, false, Constant.INTERNET_WX_USER);
//        NetUtils.sendRequest(datas, this, this);
//    }

    private void goToGetMsg() {
        Log.i("goToGetMsg", "进入");
//		Intent intent = new Intent(this, GetFromWXActivity.class);
//		intent.putExtras(getIntent());
//		startActivity(intent);
//		finish();
    }

    private void goToShowMsg(ShowMessageFromWX.Req showReq) {
        WXMediaMessage wxMsg = showReq.message;
        WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

        StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
        msg.append("description: ");
        msg.append(wxMsg.description);
        msg.append("\n");
        msg.append("extInfo: ");
        msg.append(obj.extInfo);
        msg.append("\n");
        msg.append("filePath: ");
        msg.append(obj.filePath);
        Log.i("goToShowMsg", msg.toString());


//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(STitle, wxMsg.title);
//		intent.putExtra(SMessage, msg.toString());
//		intent.putExtra(BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
//		finish();
    }

//    @Override
//    public void beforeTask() {
//
//    }
//
//    @Override
//    public int excueHttpResponse(String respondsStr, int index) {
//        switch (index) {
//            case Constant.INTERNET_WX:
//                Log.i("INTERNET_WX", respondsStr);
//                parseAccessTokenJSON(respondsStr);
//                getParseUnionIdJson(mAccess_token, mOpenId);
//                break;
//            case Constant.INTERNET_WX_USER:
//                parseUnionIdJson(respondsStr);
//                blueToothControl.getInterfaceLogin().result("login");
//                finish();
//                break;
//        }
//        return 0;
//    }
//
//    @Override
//    public void afterTask(int result) {
//
//    }

    /**
     * * 解析access_token返回的JSON数据 * * @param response
     */
    private void parseAccessTokenJSON(String response) {
        // TODO Auto-generated method stubtry
        try {
            JSONObject jsonObject = new JSONObject(response);
            mAccess_token = jsonObject.getString("access_token");
            String expiresIn = jsonObject.getString("expires_in");
            String refreshToken = jsonObject.getString("refresh_token");
            mOpenId = jsonObject.getString("openid");
            String scope = jsonObject.getString("scope");
            //将获取到的数据写进SharedPreferences里
//			editor.putString("access_token",mAccess_token);
//			editor.putString("expires_in",expiresIn);
//			editor.putString("refresh_token",refreshToken);
//			editor.putString("openid",mOpenId);
//			editor.putString("scope", scope);
//			editor.commit();
            Log.i("WXActivity", "access_token is " + mAccess_token);
            Log.i("WXActivity", "expires_in is " + expiresIn);
            Log.i("WXActivity", "refresh_token is " + refreshToken);
            Log.i("WXActivity", "openid is " + mOpenId);
            Log.i("WXActivity", "scope is " + scope);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * * 解析unionid数据 * @param response
     */
    private void parseUnionIdJson(String response) {

        try {
            Log.i("WXActivity", "response=" + response);
            JSONObject jsonObject = new JSONObject(response);
            String openid = jsonObject.getString("openid");
            String nickname = jsonObject.getString("nickname");
            String sex = jsonObject.getString("sex");
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String country = jsonObject.getString("country");
            String headimgurl = jsonObject.getString("headimgurl");
            String unionid = jsonObject.getString("unionid");
            editor.putString("nickname", nickname);
            editor.putString("headimgurl", headimgurl);
            editor.putBoolean("haveSign", true);
            editor.putString("unionid", unionid);
            editor.commit();
            Log.i("WXActivity ", " openid is " + openid);
            Log.i("WXActivity", "nickname is " + nickname);
            Log.i("WXActivity", "sex is " + sex);
            Log.i("WXActivity", "province is " + province);
            Log.i("WXActivity", "city is " + city);
            Log.i("WXActivity", "country is " + country);
            Log.i("WXActivity", "headimgurl is " + headimgurl);
            Log.i("WXActivity", "unionid is " + unionid);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
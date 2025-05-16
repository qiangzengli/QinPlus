package com.weiyin.qinplus.rest;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.weiyin.qinplus.commontool.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final int SUCCESS_CODE = 0;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Gson mGson;
    private final TypeAdapter<T> adapter;

    public CustomGsonResponseBodyConverter(Gson mGson, TypeAdapter<T> adapter) {
        this.mGson = mGson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        LogUtil.i("body",response);
        BaseResponse re = mGson.fromJson(response, BaseResponse.class);
        //关注的重点，自定义响应码中非0的情况，一律抛出ApiException异常。
        //这样，我们就成功的将该异常交给onError()去处理了。
        if (re.getCode() != SUCCESS_CODE) {
            value.close();
            throw new BizException(re.getCode(), re.getMsg());
        }

        MediaType mediaType = value.contentType();
        Charset charset = mediaType != null ? mediaType.charset(UTF_8) : UTF_8;
        ByteArrayInputStream bis = new ByteArrayInputStream(response.getBytes());
        InputStreamReader reader = new InputStreamReader(bis, charset);
        JsonReader jsonReader = mGson.newJsonReader(reader);
        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}

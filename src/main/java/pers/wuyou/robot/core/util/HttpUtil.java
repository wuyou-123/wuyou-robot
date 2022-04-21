package pers.wuyou.robot.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SM;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import pers.wuyou.robot.core.RobotCore;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 网络请求Util, 有的话可以用别的代替
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
@Slf4j
public class HttpUtil {
    private static final CookieStore STORE;
    private static final CloseableHttpClient CLOSEABLE_HTTP_CLIENT;

    static {
        // 缓存Cookie
        STORE = new BasicCookieStore();
        // 忽略Invalid cookie header
        RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        CLOSEABLE_HTTP_CLIENT = HttpClients.custom()
                .setDefaultCookieStore(STORE)
                .setDefaultRequestConfig(defaultConfig)
//                .setProxy(new HttpHost("127.0.0.1", 5533))
                .build();
    }

    private HttpUtil() {
    }

    public static boolean downloadFile(String url, String path) {
        try (
                OutputStream output = Files.newOutputStream(Paths.get(path));
                CloseableHttpResponse closeableHttpResponse = CLOSEABLE_HTTP_CLIENT.execute(new HttpGet(new URIBuilder(url).build()))
        ) {
            closeableHttpResponse.getEntity().writeTo(output);
            return true;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get请求
     *
     * @param url 请求的URL
     */
    public static RequestEntity get(String url) {
        return get(url, null, null);
    }

    /**
     * get请求
     *
     * @param url     请求的URL
     * @param params  请求的参数
     * @param cookies 请求携带的cookie
     */
    public static RequestEntity get(String url, Map<String, String> params, Map<String, String> cookies) {
        return get(url, params, cookies, null);
    }

    /**
     * get请求
     *
     * @param url     请求的URL
     * @param params  请求的参数
     * @param cookies 请求携带的cookie
     * @param headers 请求头
     */
    public static RequestEntity get(String url, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers) {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                params.forEach(uriBuilder::addParameter);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            if (headers != null) {
                headers.forEach(httpGet::setHeader);
            }
            setCookies(httpGet, cookies);
            return request(httpGet);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new RequestEntity();
    }

    /**
     * post请求
     *
     * @param url 请求的URL
     */
    public static RequestEntity post(String url) {
        Map<String, String> cookies = new HashMap<>(0);
        Map<String, String> params = new HashMap<>(0);
        return post(url, params, cookies);
    }

    /**
     * post请求
     *
     * @param url     请求的URL
     * @param params  请求的参数
     * @param cookies 请求携带的cookie
     */
    public static RequestEntity post(String url, Map<String, String> params, Map<String, String> cookies) {
        try {
            HttpPost httpPost = new HttpPost(url);
            if (params != null) {
                List<NameValuePair> paramsList = new ArrayList<>();
                params.forEach((key, value) -> paramsList.add(new BasicNameValuePair(key, value)));
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsList, "utf-8");
                httpPost.setEntity(formEntity);
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            }
            setCookies(httpPost, cookies);
            return request(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new RequestEntity();
    }

    /**
     * post请求
     *
     * @param url     请求的URL
     * @param json    请求的json
     * @param cookies 请求携带的cookie
     */
    public static RequestEntity post(String url, String json, Map<String, String> cookies) {
        try {
            HttpPost httpPost = new HttpPost(url);
            if (json != null) {
                httpPost.setEntity(new StringEntity(json));
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            }
            setCookies(httpPost, cookies);
            return request(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new RequestEntity();
    }

    /**
     * 设置cookie
     */
    protected static void setCookies(HttpRequestBase httpRequestBase, Map<String, String> cookies) {
        if (cookies != null) {
            StringBuilder cookie = new StringBuilder();
            cookies.forEach((key, value) -> cookie.append(key).append("=").append(value).append(";"));
            httpRequestBase.setHeader(SM.COOKIE, cookie.toString());

        }
    }

    /**
     * 网络请求具体实现
     */
    private static RequestEntity request(HttpRequestBase httpRequestBase) {
        try {
            return RobotCore.THREAD_POOL.submit(() -> {
                RequestEntity requestEntity = new RequestEntity();
                httpRequestBase.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
                httpRequestBase.setHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                try (CloseableHttpResponse closeableHttpResponse = CLOSEABLE_HTTP_CLIENT.execute(httpRequestBase)) {
                    final HttpEntity entity = closeableHttpResponse.getEntity();
                    requestEntity.setEntity(EntityUtils.toByteArray(entity));
                    requestEntity.setResponse(new String(requestEntity.getEntity(), StandardCharsets.UTF_8));
                    requestEntity.setCookies(STORE.getCookies().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue, (k1, k2) -> k1)));
                    for (Header header : closeableHttpResponse.getHeaders(SM.SET_COOKIE)) {
                        final String[] s = header.getValue().split(";")[0].split("=");
                        if (s.length == 1) {
                            requestEntity.getCookies().put(s[0], null);
                        } else if (s.length == 2) {
                            requestEntity.getCookies().put(s[0], s[1]);
                        }
                    }
                    requestEntity.setHeaders(closeableHttpResponse.getAllHeaders().clone());
                    httpRequestBase.setHeader(SM.COOKIE, "");
                    STORE.clear();
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
                return requestEntity;
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new RequestEntity();
    }

    /**
     * 解析腾讯接口返回结果的一个方法
     */
    public static JSONObject getJson(String url, String separator) {
        String body = HttpUtil.get(url).getResponse().replace(" ", "");
        String jsonStr = body.substring(body.indexOf(separator) + separator.length() + 1);
        jsonStr = jsonStr.substring(0, jsonStr.indexOf("</script>"));
        return JSON.parseObject(jsonStr);
    }

    /**
     * 响应实体类
     *
     * @author wuyou
     */
    @Data
    public static class RequestEntity {
        Map<String, String> cookies;
        Header[] headers;
        String response;
        byte[] entity;

        @SuppressWarnings("unused")
        public String getCookie(String cookieName) {
            return cookies.get(cookieName);
        }

        public String getHeaders(String headerName) {
            for (Header header : headers) {
                if (header.getName().equals(headerName)) {
                    return header.getValue();
                }
            }
            return "";
        }

        @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
        public JSONObject getJSONResponse() {
            return JSON.parseObject(response);
        }
    }
}

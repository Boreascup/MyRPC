package transport;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import protocol.Peer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HTTPClient implements TransportClient {
    private String url;
    int timeoutMillis = 5000; // 超时时间为5秒
    private static final int MAX_RETRIES = 3;

    @Override
    public void connect(Peer peer) {
        if (peer.isIPv6()) {
            this.url = "http://[" + peer.getHost() + "]:" + peer.getPort();
        } else {
            this.url = "http://" + peer.getHost() + ":" + peer.getPort();
        }
    }

    @Override
    public InputStream write(InputStream data) throws SocketTimeoutException {
        boolean success = false;
        int attempt = 0;

        while (!success && attempt < MAX_RETRIES) {
            attempt++;
            try {
                //log.info("客户端正在向" + url + "发起连接请求");
                URL urlObj = new URL(url);
                HttpURLConnection httpConn = (HttpURLConnection) urlObj.openConnection();
                httpConn.setConnectTimeout(timeoutMillis);
                httpConn.setReadTimeout(timeoutMillis);
                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                httpConn.setUseCaches(false);
                httpConn.setRequestMethod("POST");
                httpConn.setRequestProperty("Content-Type", "application/json");

                try (OutputStream out = httpConn.getOutputStream()) {
                    IOUtils.copy(data, out);
                }

                //int contentLength = httpConn.getContentLength();
                //log.info("Content-Length: " + contentLength);

                int resultCode = httpConn.getResponseCode();
                //log.info("resultCode = {}", resultCode);
                if (resultCode == HttpURLConnection.HTTP_OK) {
                    return httpConn.getInputStream();
                } else {
                    InputStream errorStream = httpConn.getErrorStream();
                    if (errorStream != null) {
                        String errorMessage = IOUtils.toString(errorStream, StandardCharsets.UTF_8);
                        log.error("服务器发生错误: {}", errorMessage);
                    } else {
                        log.error("服务器发生未知错误");
                    }
                    return errorStream;
                }
            } catch (SocketTimeoutException e) {
                if(attempt != MAX_RETRIES)
                    log.warn("在第" + attempt + "次尝试时连接超时,即将重试");
                else log.warn("在第" + attempt + "次尝试时连接超时");
            } catch (ConnectException e) {
                log.error("连接被拒绝，目标主机不可达。请检查端口号是否正确");
                break;
            } catch (MalformedURLException e) {
                log.error("URL格式不正确: " + e.getMessage(), e);
                break;
            }
            catch (Exception e) {
                log.error("发生异常：" + e.getMessage());
                break;
            }

            if (!success && attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(1000 * attempt);
                } catch (InterruptedException e) {
                    log.error("线程被中断，具体异常信息: " + e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!success) {
            log.error("无法与服务器建立连接");
            throw new SocketTimeoutException("无法连接到"+ this.url);
        }

        return null;
    }

    @Override
    public void close() {
        //暂时没有资源需要释放但是先写了
    }
}

package transport;

import lombok.extern.slf4j.Slf4j;
import org.Peer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class HTTPTransportClient implements TransportClient{
    private String url;
    @Override
    public void connect(Peer peer) {
        this.url = "http://" + peer.getHost() + ":" + peer.getPort();
    }

    @Override
    public InputStream write(InputStream data) {
        try{
            log.info("url = {}", url);
            HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");

            // 设置Content-Type头
            httpConn.setRequestProperty("Content-Type", "application/json");

            OutputStream out = httpConn.getOutputStream();
            IOUtils.copy(data, out);
            out.close();

            int resultCode = httpConn.getResponseCode();
            log.info("resultCode = {}", resultCode);
            if(resultCode == HttpURLConnection.HTTP_OK){
                return httpConn.getInputStream();
            }else {
                return httpConn.getErrorStream();
            }
        }catch (IOException e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {

    }
}

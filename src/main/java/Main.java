import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {


        CloseableHttpResponse response = response("https://api.nasa.gov/planetary/apod?api_key=94dAsSZlQv2o3gV3WtdvSrbxVzMj74dDdcqbxHP1");
        String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        nasaAnswer nasaAnswer = gson.fromJson(body, nasaAnswer.class);

        String imageUrl = nasaAnswer.getUrl();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        CloseableHttpResponse responseNasaImage = response(imageUrl);

        try (FileOutputStream out = new FileOutputStream(fileName);
             BufferedOutputStream bos = new BufferedOutputStream(out)) {

            byte[] buffer = responseNasaImage.getEntity().getContent().readAllBytes();
            bos.write(buffer, 0, buffer.length);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }


    }


    private static CloseableHttpClient httpClient() {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();
        return httpClient;
    }

    public static CloseableHttpResponse response(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = httpClient().execute(request);
        return response;

    }
}
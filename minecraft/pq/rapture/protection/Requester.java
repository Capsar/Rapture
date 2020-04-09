package pq.rapture.protection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Haze
 * @since 9/9/2015
 */
public class Requester {
    private String username;
    private String authcode;
    private RequestType requestType;

    private Requester(Builder builder) {
        this.username = builder.username;
        this.authcode = builder.authcode;
        this.requestType = builder.requestType;
    }

    public static Builder newRequester() {
        return new Builder();
    }

    public JsonElement request() {
        try {
            String url;
            switch (this.requestType) {
                case AUTHENTICATION:
                default:
                    url = "http://www.rxdy.gq/gauth/check.php";
                    break;
                case GET_SECRET:
                    url = "http://www.rxdy.gq/gauth/getSecret.php";
                    break;
                case IS_ENABLED:
                    url = "http://www.rxdy.gq/gauth/isGAuthEnabled.php";
                    break;
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setDoOutput(true);
            DataOutputStream stream = new DataOutputStream(connection.getOutputStream());
            switch (this.requestType) {
                case AUTHENTICATION:
                    stream.writeBytes(String.format("username=%s&authCode=%s", username, authcode));
                    break;
                case GET_SECRET:
                case IS_ENABLED:
                    stream.writeBytes(String.format("username=%s", username));
                    break;
            }
            stream.flush();
            stream.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Gson obj = new GsonBuilder().setPrettyPrinting().create();
            return obj.toJsonTree(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum RequestType {
        GET_SECRET, AUTHENTICATION, IS_ENABLED
    }

    public static final class Builder {
        private String username;
        private String authcode;
        private RequestType requestType;

        private Builder() {
        }

        public Requester build() {
            return new Requester(this);
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder authcode(String authcode) {
            this.authcode = authcode;
            return this;
        }

        public Builder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }
    }
}

import com.thingworx.common.exceptions.GenericHTTPException;
import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleClient extends ConnectedThingClient {


    private static final Logger LOG = LoggerFactory.getLogger(SimpleClient.class);

    public SimpleClient(ClientConfigurator config) throws Exception {
        super(config);
    }

    public static void main(String[] args) throws Exception {
        // client configurator
        ClientConfigurator config = new ClientConfigurator();
        config.setUri("ws://localhost:8080/Thingworx/WS");
        config.setAppKey("e8d205f8-9fc5-4c02-b1a7-19074369a242");
//        config.setAppKey("83fbbcaa-2163-49e9-83e1-9fe4a45a8370");
        config.ignoreSSLErrors(true);
        SimpleClient client = new SimpleClient(config);
        client.start();
        Thread.sleep(10000);

        int i = 1;
        String baseName = "patient";
        while (true) {
            String thingName = baseName + i++;
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://uinames.com/api/?ext&region=Poland");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            JSONObject jsonObject = new JSONObject(result.toString());
            String name = (String) jsonObject.get("name");
            String surname = (String) jsonObject.get("surname");
            String photo = (String) jsonObject.get("photo");
            Integer age = (Integer) jsonObject.get("age");

            Integer pulse = ThreadLocalRandom.current().nextInt(60, 200);
            try {
                client.writeProperty(ThingworxEntityTypes.Things, thingName, "photo", new StringPrimitive(photo),
                        1000000);
                client.writeProperty(ThingworxEntityTypes.Things, thingName, "lastName", new StringPrimitive(surname),
                        1000000);
                client.writeProperty(ThingworxEntityTypes.Things, thingName, "firstName", new StringPrimitive(name),
                        1000000);
                client.writeProperty(ThingworxEntityTypes.Things, thingName, "age", new IntegerPrimitive(age), 1000000);
                client.writeProperty(ThingworxEntityTypes.Things, thingName, "pulse", new IntegerPrimitive(pulse), 1000000);
            } catch (GenericHTTPException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
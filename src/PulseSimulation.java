import com.thingworx.common.exceptions.GenericHTTPException;
import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.types.primitives.IntegerPrimitive;

import java.util.Random;


public class PulseSimulation extends ConnectedThingClient {

    public PulseSimulation(ClientConfigurator config) throws Exception {
        super(config);
    }

    public static void main(String[] args) throws Exception {
        ClientConfigurator config = new ClientConfigurator();
        config.setUri("ws://localhost:8080/Thingworx/WS");
        config.setAppKey("83fbbcaa-2163-49e9-83e1-9fe4a45a8370");
        config.ignoreSSLErrors(true);
        PulseSimulation client = new PulseSimulation(config);
        client.start();
        Thread.sleep(10000);

        Random rand = new Random();
        int i = 1;
        String baseName = "patient";
        while (true) {
            try {
                String thingName = baseName + i++;
                System.out.println("plop thingName: " + thingName);
                Object thingProperty = client.readProperty(RelationshipTypes.ThingworxEntityTypes.Things,
                        thingName, "pulse", 10000).getReturnValue();

                System.out.println("plop thingProperty: " + thingProperty.toString());

                int oldIntValue = (int) Float.parseFloat("" + thingProperty);
//                if (oldIntValue == 0) continue;
                int modifier = rand.nextInt(202);
                System.out.println("plop modifier: " + modifier);
                if (modifier < 101) {
                    modifier = 1;
                } else if (modifier < 201) {
                    modifier = -1;
                } else {
                    modifier = -oldIntValue;
                }
                System.out.println("plop1: " + modifier);
                int newValue = oldIntValue + modifier;
                System.out.println("plop2: " + newValue);
                if (newValue >= 0) {
                    System.out.println("plop: " + newValue);
                    client.writeProperty(ThingworxEntityTypes.Things, thingName, "pulse",
                            new IntegerPrimitive(newValue), 1000000);
                }
            } catch (GenericHTTPException ex) {
                System.out.println("plop2: " + ex);
                break;
            } catch (Exception ex) {
                System.out.println("plop2: " + ex);
                ex.printStackTrace();
                break;
            }
        }
    }
}
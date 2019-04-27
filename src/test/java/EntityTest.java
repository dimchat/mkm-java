import chat.dim.crypto.Utils;
import chat.dim.mkm.entity.Address;
import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EntityTest extends TestCase {

    private void log(String msg) {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String method = traces[2].getMethodName();
        int line = traces[2].getLineNumber();
        System.out.println("[" + method + ":" + line + "] " + msg);
    }

    @Test
    public void testAddress() {
        Address address;

        address = Address.getInstance("4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
        log("address: " + address);
        log("number: " + address.code);
        assertEquals(1840839527L, address.code);

        address = Address.getInstance("4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        log("address: " + address);
        log("number: " + address.code);
        assertEquals(4049699527L, address.code);
    }

    @Test
    public void testID() {
        ID identifier;

        identifier = ID.getInstance("moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
        log("ID: " + identifier);
        log("number: " + identifier.getNumber());
        assertEquals(1840839527L, identifier.getNumber());

        identifier = ID.getInstance("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        log("ID: " + identifier);
        log("number: " + identifier.getNumber());
        assertEquals(4049699527L, identifier.getNumber());

        List<ID> array = new ArrayList<>();
        array.add(identifier);
        array.add(identifier);
        log("list<ID>:" + array);
        String json = Utils.jsonEncode(array);
//        String json = JSONObject.toJSON(array).toString();
        log("json:" + json);
    }

    @Test
    public void testEntity() {
        ID identifier = ID.getInstance("moky@4DnqXWdTV8wuZgfqSCX9GjE2kNq7HJrUgQ");
        Entity entity = new Entity(identifier);
        log(entity.toString());
        assertEquals(4049699527L, entity.getNumber());
    }
}
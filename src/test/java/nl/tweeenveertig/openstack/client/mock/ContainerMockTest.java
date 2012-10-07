package nl.tweeenveertig.openstack.client.mock;

import nl.tweeenveertig.openstack.client.Container;
import nl.tweeenveertig.openstack.client.StoredObject;
import nl.tweeenveertig.openstack.exception.CommandException;
import nl.tweeenveertig.openstack.command.core.CommandExceptionError;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static junit.framework.Assert.*;

public class ContainerMockTest {

    private ContainerMock container;

    private StoredObject object;

    @Before
    public void setup() {
        this.container = new ContainerMock(new AccountMock(), "someContainer");
        this.object = this.container.getObject("someObject");
    }

    // TODO implement exists() method before reactivating these chaps
    @Test
    public void getOrCreateDoesNotExist() {
        assertFalse(container.getObject("somevalue").exists());
    }

    @Test
    public void getDoesNotExist() {
        try {
            container.getObject("somevalue").delete();
            fail("Should have thrown an exception");
        } catch (CommandException err) {
            assertEquals(CommandExceptionError.ENTITY_DOES_NOT_EXIST, err.getError());
        }
    }

    @Test
    public void publicPrivate() {
        assertFalse(container.isPublic());
        container.makePublic();
        assertTrue(container.isPublic());
        container.makePrivate();
        assertFalse(container.isPublic());
    }

    @Test
    public void numberOfObjects() throws IOException {
        addObjects(3);
        assertEquals(3, container.getObjectCount());
    }

    @Test
    public void listObjects() throws IOException {
        addObjects(3);
        assertEquals(3, container.listObjects().size());
    }

    @Test
    public void deleteObject() throws IOException {
        object.uploadObject(new byte[]{});
        assertEquals(1, container.getObjectCount());
        object.delete();
        assertEquals(0, container.getObjectCount());
    }

    @Test
    public void getInfo() throws IOException {
        addObject("object1", new byte[] { 0x01, 0x02, 0x03 } );
        addObject("object2", new byte[] { 0x01, 0x02 } );
        addObject("object3", new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 } );
        assertEquals(10, container.getBytesUsed());
        assertEquals(3, container.getObjectCount());
    }

    @Test
    public void existence() {
        assertFalse(container.exists());
        container.create();
        assertTrue(container.exists());
        Container newContainer = new ContainerMock(container.getAccount(), "test") {
            @Override
            protected void checkForInfo() {
                throw new CommandException(404, CommandExceptionError.ENTITY_DOES_NOT_EXIST);
            }
        };
        assertFalse(newContainer.exists());
    }

    @Test
    public void getObject() throws IOException {
        StoredObject object1 = container.getObject("some-object");
        assertFalse(object1.exists());
        object1.uploadObject(new byte[]{0x01});
        StoredObject object2 = container.getObject("some-object");
        assertEquals(object1, object2);
        assertTrue(object1.exists());
    }

    @Test
    public void addMetadata() {
        Map<String, Object> metadata = new TreeMap<String, Object>();
        metadata.put("name", "value");
        container.setMetadata(metadata);
        assertEquals(1, container.getMetadata().size());
    }

    protected void addObject(String name, byte[] bytes) throws IOException {
        StoredObject object = container.getObject(name);
        object.uploadObject(bytes);
    }

    protected void addObjects(int times) throws IOException {
        for (int i = 0; i < times; i++) {
            container.getObject("someobject"+i).uploadObject(new byte[] {});
        }
    }
}

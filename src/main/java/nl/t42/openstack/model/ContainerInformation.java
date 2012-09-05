package nl.t42.openstack.model;

public class ContainerInformation extends AbstractInformation {

    private int objectCount;

    private long bytesUsed;

    private boolean publicContainer;

    public int getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(int objectCount) {
        this.objectCount = objectCount;
    }

    public long getBytesUsed() {
        return bytesUsed;
    }

    public void setBytesUsed(long bytesUsed) {
        this.bytesUsed = bytesUsed;
    }

    public boolean isPublicContainer() {
        return publicContainer;
    }

    public void setPublicContainer(boolean publicContainer) {
        this.publicContainer = publicContainer;
    }
}

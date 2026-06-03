package ar.edu.utn.dds.k3003.dtos;

public class MisionIDRequest {
    private String misionID;

    public MisionIDRequest(String misionID) {
        this.misionID = misionID;
    }

    public String getMisionID() {
        return misionID;
    }

    public void setMisionID(String misionID) {
        this.misionID = misionID;
    }
}

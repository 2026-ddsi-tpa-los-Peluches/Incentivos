package ar.edu.utn.dds.k3003.dtos;

public class InsigniaIDRequest {
    private String insigniaID;

        public InsigniaIDRequest(String insigniaID) {
            this.insigniaID = insigniaID;
        }

    public String getInsigniaID() {
        return insigniaID;
    }

    public void setInsigniaID(String insigniaID) {
        this.insigniaID = insigniaID;
    }
}

package src;

public class Model {
    private Networking networking;
    private Controller controller;

    public Model(Networking networking) {
        this.networking = networking;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setNetworkAddress(String networkAddress) {
        // forward the network address to the networking class
        networking.setNetworkAddress(networkAddress);
    }
}
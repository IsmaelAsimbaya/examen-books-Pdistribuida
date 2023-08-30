package com.distribuida;
import io.helidon.microprofile.server.Server;

public class MainApplication {

    public static void main(String[] args) {
        Server server = Server.create().start();
    }

}

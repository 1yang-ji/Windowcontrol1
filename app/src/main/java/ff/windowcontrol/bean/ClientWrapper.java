package ff.windowcontrol.bean;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Feng on 2018/5/6.
 */

public class ClientWrapper {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static final String IP_ADDRESS = "39.108.177.42";
    public static final int PORT = 1234;

    private Client client;

    private static ClientWrapper instance = null;
    public static ClientWrapper getInstance(){
        synchronized (ClientWrapper.class){
            if(instance == null){
                instance = new ClientWrapper();
            }
            return instance;
        }
    }


    public ClientWrapper() {
    }


    public void connect(final String ip, final int port) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(ip, port);
                    client = new Client(s);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("连接服务器失败，请退出重新连接");
                }
            }
        });
    }

    public Client getClient() {
        return client;
    }

    public void turnSafe(byte[] a){
        client.writedata(a);
    }


}

package ff.windowcontrol.bean;

/**
 * Created by Feng on 2018/5/5.
 */

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private ExecutorService readExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService writeExecutor = Executors.newSingleThreadExecutor();
    boolean connect = false;//客户端是否连接标志位
    Socket mSocket;
    InputStream in;
    OutputStream out;
    public Client(Socket s){
        this.mSocket = s;
        try {
            in = mSocket.getInputStream();
            out = mSocket.getOutputStream();
            connect = true;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        StartThread();//处理线程开始
    }

    public boolean isConnect() {
        return connect;
    }

    /* 功能：发送字节数组
         * 参数：要发送的字节数组
         * */
    public void writedata(final byte[] data){
        writeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if(out != null){
                        out.write(data);
                        System.out.println("发送完毕");
                    }
                } catch (IOException e) {
                    connect = false;
                    e.printStackTrace();
                }
            }
        });
    }
    /*
     * 发送字符串
     * */
    public void writestr(final String data){
        writedata(data.getBytes());
    }


    public void readdata(){
        readExecutor.execute(new Runnable() {

            @Override
            public void run() {
                System.out.println("readdata start ...");
                while(connect){
                    int Length = 0;
                    byte[] bdata = new byte[32];
                    try {
                        if(in != null){
                            Length = in.read(bdata);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        connect = false;
                    }
                    if(Length == -1){//接收到的字节长度为-1，客户端断开
                        connect = false;
                        System.out.println("read -1");
                    }else {
                        //回显，显示接收到的数据
                        String dString = new String(bdata);
                        if (bdata[0]==0x28||bdata[31]==0x29){
                            ReceiveData receiveData=new ReceiveData(bdata);
                            EventBus.getDefault().post(new AllEvent(receiveData.getwindow(),receiveData.getair(),receiveData.getwarn(),receiveData.getrain(),receiveData.getairs()));
                            EventBus.getDefault().post(new TemperatureEvent(receiveData.gettemperature()));
                        }
                    }
                }
                close();
                System.out.println("readdata end");
            }
        });

    }
    //释放资源
    public void close() {
        try{
            connect = false;
            if(mSocket != null)
                mSocket.close();
            if(in != null)
                in.close();
            if(out != null)
                out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("与服务器连接断开,请退出重新连接");
    }

    private void StartThread(){
        readdata();
    }
}

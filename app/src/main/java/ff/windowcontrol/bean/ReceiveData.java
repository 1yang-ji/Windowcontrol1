package ff.windowcontrol.bean;

/**
 * Created by Feng on 2018/5/9.
 */

public class ReceiveData {

    private byte[] mMsg;
    private String temperature;
    private int warn;
    private int window;
    private int air;
    private int rain;
    private int airs;
    public ReceiveData(byte [] a){
        mMsg=a;

    }
    public String gettemperature(){
        temperature=mMsg[8]+"."+mMsg[9];
        return temperature;
    }

    public int getwarn(){
        if (mMsg[5]==1){
         warn=1;
        }else{
         warn=0;
        }
        return warn;
    }

    public int getwindow(){
        if (mMsg[2]==1){
            window=1;
        }else{
            window=0;
        }
        return window;
    }
    public int getair(){
        if (mMsg[3]==1){
            air=1;
        }else{
            air=0;
        }
        return air;
    }

    public int getrain(){
        if (mMsg[6]==1){
            rain=1;
        }else{
            rain=0;
        }
        return rain;
    }
    public int getairs(){
        if (mMsg[7]==1){
            airs=1;
        }else{
            airs=0;
        }
        return airs;
    }
}

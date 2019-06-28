package ff.windowcontrol.bean;

/**
 * Created by Feng on 2018/5/9.
 */

public class SendData {

    public static byte[] model(){
        byte[] model=new byte[32];
        model[0]=0x28;
        model[1]=1;
        model[2]=0;
        model[3]=0;
        model[4]=0;
        model[31]=0x29;
        return model;
    }

    public static byte[] model_people(){
        byte[] model=new byte[32];
        model[0]=0x28;
        model[1]=0;
        model[2]=0;
        model[3]=0;
        model[4]=0;
        model[31]=0x29;
        return model;
    }





    public static byte[] openwindow(){
        byte[] openwindow=new byte[32];
        openwindow[0]=0x28;
        openwindow[1]=0;
        openwindow[2]=1;
        openwindow[3]=0;
        openwindow[4]=0;
        openwindow[31]=0x29;
        return openwindow;
    }

    public static byte[] openwindow_airrun(){
        byte[] openwindow=new byte[32];
        openwindow[0]=0x28;
        openwindow[1]=0;
        openwindow[2]=1;
        openwindow[3]=1;
        openwindow[4]=0;
        openwindow[31]=0x29;
        return openwindow;
    }

    public static byte[] closewindow(){
        byte[] openwindow=new byte[32];
        openwindow[0]=0x28;
        openwindow[1]=0;
        openwindow[2]=0;
        openwindow[3]=0;
        openwindow[4]=0;
        openwindow[31]=0x29;
        return openwindow;
    }

    public static byte[] closewindow_airrun(){
        byte[] openwindow=new byte[32];
        openwindow[0]=0x28;
        openwindow[1]=0;
        openwindow[2]=0;
        openwindow[3]=1;
        openwindow[4]=0;
        openwindow[31]=0x29;
        return openwindow;
    }


    public static byte[] air(){
        byte[]  air=new byte[32];
        air[0]=0x28;
        air[1]=0;
        air[2]=0;
        air[3]=1;
        air[4]=0;
        air[31]=0x29;
        return  air;
    }

    public static byte[] air_windowopen(){
        byte[]  air=new byte[32];
        air[0]=0x28;
        air[1]=0;
        air[2]=1;
        air[3]=1;
        air[4]=0;
        air[31]=0x29;
        return  air;
    }

    public static byte[] closeair(){
        byte[]  air=new byte[32];
        air[0]=0x28;
        air[1]=0;
        air[2]=0;
        air[3]=0;
        air[4]=0;
        air[31]=0x29;
        return  air;
    }

    public static byte[] closeair_windowopen(){
        byte[]  air=new byte[32];
        air[0]=0x28;
        air[1]=0;
        air[2]=1;
        air[3]=0;
        air[4]=0;
        air[31]=0x29;
        return  air;
    }


    public static byte[] time(){

        byte[]  time=new byte[32];
        time[0]=0x28;
        time[1]=0;
        time[2]=1;
        time[3]=0;
        time[4]=1;
        time[31]=0x29;
        return  time;
    }
}

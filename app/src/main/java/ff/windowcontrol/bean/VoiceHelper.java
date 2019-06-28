package ff.windowcontrol.bean;

public class VoiceHelper {

    private String help;

    public VoiceHelper(String c) {
        // TODO Auto-generated constructor stub
        this.help=c;

    }

    public int OpenWindow(String c) {
      boolean i= c.equals("打开窗户");
        if (i)
            return 1;
        return 0;
    }

    public int OpenAir(String c) {
        boolean b= c.equals("打开风扇");
        if (b)
            return 2;
        return 0;
    }

    public int CloseWindow(String c) {
        boolean d= c.equals("关闭窗户");
        if (d)
            return 3;
        return 0;
    }

    public int CloseAir(String c) {
        boolean e= c.equals("关闭风扇");
        if (e)
            return 4;
        return 0;
    }

    public int CloseTime(String c) {
        boolean f= c.equals("关闭闹钟");
        if (f)
            return 5;
        return 0;
    }




}

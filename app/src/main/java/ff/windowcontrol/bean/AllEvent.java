package ff.windowcontrol.bean;

public class AllEvent {
    private int window;
    private int air;
    private int warm;
    private int rain;
    private int airs;
    private int model;
    public AllEvent(int window,int air,int warm,int rain,int airs) {
        // TODO Auto-generated constructor stub
        this.window=window;
        this.air=air;
        this.warm=warm;
        this.rain=rain;
        this.airs=airs;

    }

    public int Getwindow() {
        return window;
    }

    public int Getair() {
        return air;
    }

    public int Getwarm() {
        return warm;
    }

    public int Getrain() {
        return rain;
    }

    public int Getairs() {
        return airs;
    }


}

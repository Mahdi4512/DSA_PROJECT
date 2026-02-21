package structures;

public class Ride {
    private String name;
    private int capacity;
    private int Duration;//مدت زمان هر دور
    private queue q;
    private Visitor[]currentV;//افرادی که در حال سرویس دهی هستند
    private boolean isOperating;//ایا در حال کار هست
    private int serve;//تعداد کل کسانی که تا کنون از دستگاه استفاده کردند
    private int finishTime;
    public Ride(String name,int capacity,int Duration){
        this.capacity=capacity;
        this.Duration=Duration;
        this.name=name;
        this.q=new queue(name);
        this.isOperating=false;
        this.currentV=null;
        this.serve=0;
        this.finishTime=-1;
    }
    //شروع سرویس جدید
    public boolean startService(int time){
        if(isOperating || q.isEmpty()){
            return false;
        }
        int count=Math.min(capacity,q.getSize());
        currentV=q.takeVisitors(count);
        for(Visitor v:currentV){
            v.setCurrentRide("In Ride:"+ name);
        }
        if(currentV.length==0){
            return false;
        }
        isOperating=true;
        finishTime=time+Duration;
        serve+=currentV.length;
        System.out.println("Ride "+name+" start with "+currentV.length+" riders");
        return true;
    }
    public Visitor []finishService(){
        if(!isOperating)return null;
        Visitor []finished=currentV;
        for(Visitor v:finished){
            v.setCurrentRide(null);
        }
        currentV=null;
        isOperating=false;
        finishTime=-1;
        return finished;
    }
    //برای بررسی اینکه زمان پایان سرویس دهی فرا رسیده یا نه
    public boolean shouldfinish(int time){
      return isOperating && time>=finishTime;
    }
    public String getName(){
        return name;
    };

    public int getCapacity(){
        return capacity;
    }
    public int getDuration(){
        return Duration;
    }
    public queue getQ(){
        return q;
    }
    public boolean isOperating(){
        return isOperating;
    }
    public int getFinishTime(){
        return finishTime;
    }
    public int getServe(){
        return serve;
    }
    public Visitor[] getCurrentV(){
        return currentV;
    }
}

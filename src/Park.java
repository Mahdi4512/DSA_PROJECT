
import structures.*;
import java.util.*;
public class Park {
    private boolean lastCommandWasTick = false;
    private AVL visitorTree;
    private Ride[] rides;
    private MinHeap eventHeap;
    private UndoStack undoStack;
    private int currentTime;
    private int totalVisitors;
    private List<String> eventLog;
    public Park(int maxRides) {
        this.visitorTree = new AVL();
        this.rides = new Ride[maxRides];
        this.eventHeap = new MinHeap(100);
        this.undoStack = new UndoStack();
        this.currentTime = 0;
        this.totalVisitors = 0;
        this.eventLog = new ArrayList<>();
    }

    public void addRide(String name, int capacity, int duration) {
        for (int i = 0; i < rides.length; i++) {
            if (rides[i] == null) {
                rides[i] = new Ride(name, capacity, duration);
                logEvent("Ride " + name + " added (Capacity: " + capacity + ", Duration: " + duration + ")");
                System.out.println("Ride " + name + " added");
                return;
            }
        }
        System.out.println("Error: Cannot add more rides");
    }

    public Ride findRide(String name) {
        for (Ride ride : rides) {
            if (ride != null && ride.getName().equals(name)) {
                return ride;
            }
        }
        return null;
    }



    public void processCommand(String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0) return;

        String cmd = parts[0].toUpperCase();

        try {
            switch (cmd) {
                case "ADD_VISITOR":
                    int id = Integer.parseInt(parts[1]);
                    String name = parts[2];
                    addVisitor(id, name);
                    break;

                case "MAKE_VIP":
                    int vipId = Integer.parseInt(parts[1]);
                    makeVIP(vipId);
                    break;

                case "JOIN_QUEUE":
                    int visitorId = Integer.parseInt(parts[1]);
                    String rideName = parts[2];
                    joinQueue(visitorId, rideName);
                    break;

                case "LEAVE_QUEUE":
                    int leaveId = Integer.parseInt(parts[1]);
                    String leaveRide = parts[2];
                    leaveQueue(leaveId, leaveRide);
                    break;
                case "DELETE_VISITOR":
                    int deleteId=Integer.parseInt(parts[1]);
                    deleteVisitor(deleteId);
                    break;
                case "TICK":
                    int minutes = Integer.parseInt(parts[1]);
                    tick(minutes);
                    lastCommandWasTick = true;
                    break;

                case "UNDO":
                    undo();
                    break;

                case "STATUS":
                    printStatus();
                    break;

                case "REPORT":
                    printReport();
                    break;

                case "VISITOR_INFO":
                    int infoId = Integer.parseInt(parts[1]);
                    visitorInfo(infoId);
                    break;

                case "ADD_RIDE":
                    String rideNameToAdd = parts[1];
                    int capacity = Integer.parseInt(parts[2]);
                    int duration = Integer.parseInt(parts[3]);
                    addRide(rideNameToAdd, capacity, duration);
                    break;

                default:
                    System.out.println("Unknown command: " + cmd);
            }
        } catch (Exception e) {
            System.out.println("Error processing command: " + command);
        }
    }



    private void addVisitor(int id, String name) {
        if (visitorTree.search(id) != null) {
            System.out.println("Error: Visitor " + id + " already exists!");
            return;
        }

        Visitor visitor = new Visitor(id, name, currentTime);
        visitorTree.insert(visitor);
        totalVisitors++;

        // ذخیره برای Undo
        undoStack.push(new Undo("DELETE_VISITOR", id, null));

        logEvent("Visitor " + id + " (" + name + ") added");
        System.out.println("Visitor " + id + " added");
        lastCommandWasTick = false;
    }
    private void deleteVisitor(int id) {
        Visitor visitor = visitorTree.search(id);

        if (visitor == null) {
            System.out.println("Error: Visitor " + id + " not found!");
            return;
        }
        String currentRideName = visitor.getCurrentRide();
        if (currentRideName != null && currentRideName.startsWith("In Ride:")) {
                System.out.println("Error: visitor "+id +" is currently riding and cant be deleted");
                return;
        }
        if(currentRideName!=null){
            Ride ride=findRide(currentRideName);
            if(ride!=null){
                ride.getQ().removeVisitor(id);
            }
        }

        Undo undoAction = new Undo(
                "ADD_VISITOR_AGAIN",
                visitor.getId(),
                null,
                visitor.getName() + "|" + visitor.getType() + "|" + visitor.getArrivalTime(), // ذخیره نام، نوع و زمان ورود
                -1
        );
        undoStack.push(undoAction);
        visitorTree.delete(id);
        logEvent("Visitor " + id + " (" + visitor.getName() + ") deleted");
        System.out.println("Visitor " + id + " deleted");
        lastCommandWasTick = false;
    }

    private void makeVIP(int id) {
        Visitor visitor = visitorTree.search(id);
        if (visitor == null) {
            System.out.println("Error: Visitor " + id + " not found!");
            return;
        }

        String previousType = visitor.getType();
        int previousPosition = -1;
        String currentRideName = visitor.getCurrentRide();

        // ذخیره موقعیت در صف
        if (currentRideName != null) {
            Ride ride = findRide(currentRideName);
            if (ride != null) {
                previousPosition = ride.getQ().getVisitorPosition(id);
            }
        }

        if (!visitor.isVIP()) {
            visitor.setType("VIP");
            if(currentRideName!=null){
                Ride ride=findRide(currentRideName);
                if(ride!=null){
                    ride.getQ().repositionVip(id);
                }
            }

            undoStack.push(new Undo("UNMAKE_VIP", id, currentRideName, previousType, previousPosition));

            logEvent("Visitor " + id + " became VIP");
            System.out.println("Visitor " + id + " is now VIP");
            lastCommandWasTick = false;
        } else {
            System.out.println("Visitor " + id + " is already VIP");
        }
    }

    private void joinQueue(int visitorId, String rideName) {
        Visitor visitor = visitorTree.search(visitorId);
        Ride ride = findRide(rideName);

        if (visitor == null || ride == null) {
            System.out.println("Error: Visitor or ride not found!");
            return;
        }

        if (visitor.getCurrentRide() != null) {
            System.out.println("Error: Visitor " + visitorId + " is already in a queue/ride");
            return;
        }

        boolean success = ride.getQ().enqueue(visitor);
        if (success) {
            visitor.setCurrentRide(rideName);

            undoStack.push(new Undo("LEAVE_QUEUE", visitorId, rideName));

            logEvent("Visitor " + visitorId + " joined queue for " + rideName);
            System.out.println("Visitor " + visitorId + " joined queue for " + rideName);
            lastCommandWasTick = false;
            // اگر دستگاه آماده است، رویداد شروع ایجاد کن
            if (!ride.isOperating() && ride.getQ().getSize() > 0) {
                scheduleRideStart(ride);
            }
        }
    }

    private void scheduleRideStart(Ride ride) {
        Event startEvent = new Event(
                currentTime,
                "START_SERVICE",
                ride.getName()
        );
        eventHeap.insert(startEvent);
        logEvent("Scheduled " + ride.getName() + " to start");
    }

    private void startRideService(String rideName) {
        Ride ride = findRide(rideName);

        if (ride == null || ride.isOperating()) {
            return;
        }
        if(ride.getQ().getSize()>0){
        if (ride.startService(currentTime)) {
            // حذف عملیات JOIN_QUEUE از پشته Undo
            Visitor[] riders = ride.getCurrentV();
            for (Visitor rider : riders) {
                boolean removed = undoStack.removeJoinQueueAction(rider.getId(), rideName);
                if (removed) {
                    logEvent("JOIN_QUEUE for visitor " + rider.getId() + " removed from undo stack");
                }
                rider.setCurrentRide("In Ride:" + rideName);
            }

            // ایجاد رویداد پایان سرویس
            Event finishEvent = new Event(
                    ride.getFinishTime(),
                    "FINISH_SERVICE",
                    rideName
            );
            eventHeap.insert(finishEvent);

            logEvent("Ride " + rideName + " started with " + riders.length + " riders");
            System.out.println(rideName + " started with " + riders.length + " riders");
        }
    }
}
    private void tick(int minutes) {
        if (minutes <= 0) {
            System.out.println("Error: TICK minutes must be positive");
            return;
        }

        logEvent("TICK " + minutes + " minutes");
        System.out.println("Advancing time by " + minutes + " minutes...");

        for (int i = 0; i < minutes; i++) {
            currentTime++;
            processEvents();
        }

        System.out.println("Time is now: " + currentTime);
    }

    private void processEvents() {
        while (!eventHeap.isEmpty() && eventHeap.peekMin().getTime() <= currentTime) {
            Event event = eventHeap.deleteMin();
            handleEvent(event);
        }
    }

    private void handleEvent(Event event) {
        switch (event.getType()) {
            case "START_SERVICE":
                startRideService(event.getRideName());
                break;

            case "FINISH_SERVICE":
                finishRideService(event.getRideName());
                break;
        }
    }

    private void finishRideService(String rideName) {
        Ride ride = findRide(rideName);
        if (ride != null && ride.shouldfinish(currentTime)) {
            Visitor[] finishedVisitors = ride.finishService();

            if (finishedVisitors != null && finishedVisitors.length > 0) {
                StringBuilder riderIds = new StringBuilder();
                for (Visitor v : finishedVisitors) {
                    riderIds.append(v.getId()).append(" ");
                    v.setCurrentRide(null);
                }

                logEvent("Ride " + rideName + " finished (Riders: " + riderIds.toString().trim() + ")");
                System.out.println(rideName + " finished. Riders: " + riderIds.toString().trim());

                // دور بعدی
                if (ride.getQ().getSize() > 0) {
                    scheduleRideStart(ride);
                }
            }
        }
    }

    private void leaveQueue(int visitorId, String rideName) {
        Visitor visitor = visitorTree.search(visitorId);
        Ride ride = findRide(rideName);

        if (visitor == null || ride == null) return;

        boolean removed = ride.getQ().removeVisitor(visitorId);
        if (removed) {
            visitor.setCurrentRide(null);
            logEvent("Visitor " + visitorId + " left queue for " + rideName);
            System.out.println("Visitor " + visitorId + " left queue for " + rideName);
        }
    }
    private void undo() {
        if(lastCommandWasTick){
            System.out.println("cannot Undo Tick command");
            return;
        }

        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo!");
            return;
        }

        Undo action = undoStack.pop();
        logEvent("UNDO: " + action.command + " for visitor " + action.id);
        executeUndo(action);
    }

    private void executeUndo(Undo action) {
        if (action == null) return;

        switch (action.command) {
            case "DELETE_VISITOR":
                undoDeleteVisitor(action.id);
                break;
            case "LEAVE_QUEUE":
                undoLeaveQueue(action.id, action.rideName);
                break;
            case "ADD_VISITOR_AGAIN":
                undoAddVisitor(action.id,action.additionalInfo);
                break;
            case "UNMAKE_VIP":
                undoUnmakeVIP(action.id, action.additionalInfo, action.previousPosition, action.rideName);
                break;
        }
    }
    //undo for add_visitor
    private void undoDeleteVisitor(int id) {
        Visitor visitor = visitorTree.search(id);
        if (visitor != null) {
            visitorTree.delete(id);
            logEvent("Visitor " + id + " deleted (undo)");
            System.out.println("Undo: Visitor " + id + " deleted");
        }
    }
    //undo for delete_visitor
    private void undoAddVisitor(int id, String additionalInfo) {
        String[] parts = additionalInfo.split("\\|");//name|type|arrivalTime
        String name = parts[0];
        String type = parts.length > 1 ? parts[1] : "NORMAL";
        int arrivalTime = parts.length > 2 ? Integer.parseInt(parts[2]) : currentTime;

        if (visitorTree.search(id) != null) return;
        Visitor visitor = new Visitor(id, name, arrivalTime);
        visitor.setType(type);
        visitorTree.insert(visitor);
        logEvent("Visitor " + id + " restored (undo delete)");
        System.out.println("Undo: Visitor " + id + " restored");
    }

    private void undoLeaveQueue(int id, String rideName) {
        Visitor visitor = visitorTree.search(id);
        Ride ride = findRide(rideName);

        if (visitor != null && ride != null) {
            // بررسی آیا بازدیدکننده در حال حاضر در دستگاه است
            if (visitor.getCurrentRide() != null &&
                    visitor.getCurrentRide().startsWith("In Ride:")) {
                System.out.println("Cannot undo: Visitor " + id + " is currently riding");
                return;
            }

            // بررسی آیا در صف است
            if (ride.getQ().getVisitorPosition(id) >= 0) {
                ride.getQ().removeVisitor(id);
                visitor.setCurrentRide(null);
                logEvent("Visitor " + id + " left queue for " + rideName + " (undo)");
                System.out.println("Undo: Visitor " + id + " left queue for " + rideName);
            } else {
                System.out.println("Cannot undo: Visitor " + id + " is not in queue for " + rideName);
            }
        }
    }

    private void undoUnmakeVIP(int id, String previousType, int previousPosition, String rideName) {
        Visitor visitor = visitorTree.search(id);
        if (visitor == null) return;

        // برگرداندن نوع
        visitor.setType(previousType != null ? previousType : "NORMAL");

        // اگر قبلاً داخل صف بوده، دقیقاً به همان جای قبلی برگردد
        if (rideName != null) {
            Ride ride = findRide(rideName);
            if (ride != null) {
                // حذف از جای فعلی در صف (VIP شده بوده و جاش تغییر کرده)
                ride.getQ().removeVisitor(id);

                // برگرداندن به موقعیت قبلی
                if (previousPosition >= 0) {
                    int pos = Math.min(previousPosition, ride.getQ().getSize());
                    ride.getQ().insertAtPosition(visitor, pos);
                    visitor.setCurrentRide(rideName);
                }
            }
        }
        logEvent("Visitor " + id + " changed back to NORMAL (undo)");
        System.out.println("Undo: Visitor " + id + " is NORMAL again and returned to old position");
    }



    private void printStatus() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PARK STATUS at time: " + currentTime);
        System.out.println("=".repeat(60));

        System.out.println("\n[VISITORS SUMMARY]");
        System.out.println("Total visitors in system: " + visitorTree.size());
        System.out.println("Total visitors arrived: " + totalVisitors);

        System.out.println("\n[RIDES STATUS]");
        boolean anyRide = false;
        for (Ride ride : rides) {
            if (ride != null) {
                anyRide = true;
                System.out.println("\n" + ride.getName().toUpperCase() + ":");
                System.out.println("  State: " + (ride.isOperating() ? "OPERATING" : "IDLE"));

                if (ride.isOperating()) {
                    System.out.print("  Current riders: [");
                    Visitor[] current = ride.getCurrentV();
                    if (current != null) {
                        for (int i = 0; i < current.length; i++) {
                            System.out.print(current[i].getId());
                            if (current[i].isVIP()) System.out.print("(VIP)");
                            if (i < current.length - 1) System.out.print(", ");
                        }
                    }
                    System.out.println("]");
                    System.out.println("  Finishes at: " + ride.getFinishTime() +
                            " (" + (ride.getFinishTime() - currentTime) + " minutes remaining)");
                }

                System.out.println("  Queue: " + ride.getQ().getQueueStatus());
                System.out.println("  Queue size: " + ride.getQ().getSize());
                System.out.println("  Total served: " + ride.getServe());
            }
        }

        if (!anyRide) {
            System.out.println("No rides available.");
        }

        System.out.println("\n[SYSTEM INFO]");
        System.out.println("Pending events in heap: " + eventHeap.size());
        System.out.println("Undo stack size: " + undoStack.size());
        System.out.println("Event log size: " + eventLog.size());




        System.out.println("=".repeat(60) + "\n");
    }

    private void printReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("FINAL SIMULATION REPORT");
        System.out.println("=".repeat(60));

        System.out.println("\n[EXECUTION SUMMARY]");
        System.out.println("Simulation ended at time: " + currentTime);
        System.out.println("Total commands processed: " + eventLog.size());
        System.out.println("Total visitors entered park: " + totalVisitors);
        System.out.println("Visitors currently in system: " + visitorTree.size());

        System.out.println("\n[RIDES PERFORMANCE]");
        System.out.println("-".repeat(40));

        int totalServedAll = 0;
        int totalWaitingAll = 0;

        for (Ride ride : rides) {
            if (ride != null) {
                int served = ride.getServe();
                int waiting = ride.getQ().getSize();
                totalServedAll += served;
                totalWaitingAll += waiting;

                System.out.printf("%-15s: Served: %-4d | Waiting: %-3d | Queue: %s%n",
                        ride.getName(),
                        served,
                        waiting,
                        ride.getQ().getQueueStatus());
            }
        }

        System.out.println("\n[OVERALL STATISTICS]");
        System.out.println("Total visitors served: " + totalServedAll);
        System.out.println("Total visitors still waiting: " + totalWaitingAll);
        if (totalVisitors > 0) {
            double serviceRate = (totalServedAll * 100.0) / totalVisitors;
            System.out.printf("Service rate: %.1f%%%n", serviceRate);
        }

        System.out.println("\n[EVENT LOG]");
        System.out.println("-".repeat(40));
        if (eventLog.isEmpty()) {
            System.out.println("No events logged");
        } else {

            for (int i = 0; i < eventLog.size(); i++) {
                System.out.println(eventLog.get(i));
            }
            System.out.println("Total events: " + eventLog.size());
        }

        System.out.println("=".repeat(60) + "\n");
    }

    private void visitorInfo(int id) {
        Visitor visitor = visitorTree.search(id);
        if (visitor != null) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("VISITOR INFORMATION");
            System.out.println("=".repeat(40));
            System.out.println("ID: " + visitor.getId());
            System.out.println("Name: " + visitor.getName());
            System.out.println("Type: " + visitor.getType());
            System.out.println("Arrival time: " + visitor.getArrivalTime());
            System.out.println("Time in park: " + (currentTime - visitor.getArrivalTime()) + " minutes");
            System.out.println("Current status: " +
                    (visitor.getCurrentRide() != null ?
                            "In queue/ride: " + visitor.getCurrentRide() :
                            "Free (not in any queue)"));
            System.out.println("=".repeat(40));
        } else {
            System.out.println("Visitor " + id + " not found!");
        }
    }
    private void logEvent(String message) {
        String logEntry = "T=" + currentTime + ": " + message;
        eventLog.add(logEntry);
    }

}

package scheduler;

import java.util.*;

/**
 * @author Joshua Donelly-Higgins
 */
public class ClassObject {
    public String type;
    public boolean waitlist;
    public boolean auxiliary;
    public List<String> auxiliaryClassNumbers;
    public ArrayList<ClassObject> auxiliaryClasses = new ArrayList<>();
    public int begin;
    public int end;
    public HashMap<String, Boolean> days;
    public HashMap<ClassObject, Boolean> conflictMap;

    /**
     * used to create a ClassObject during initial file read
     */
    public ClassObject(String type, String waitlist, String auxiliary, String auxiliaryClassNumbers, String begin, String end, String days) {
        //conflictMap and days init
        this.days = new HashMap<>();
        this.conflictMap = new HashMap<>();
        //type
        this.type = type;
        //waitlist
        this.waitlist = Boolean.getBoolean(waitlist);
        //auxiliary
        this.auxiliary = Boolean.getBoolean(auxiliary);
        //auxiliaryClassNumbers
        this.auxiliaryClassNumbers = Arrays.asList(auxiliaryClassNumbers.split(","));
        //begin
        this.begin = Integer.parseInt(begin);
        //end
        this.end = Integer.parseInt(end);
        //days
        List<String> dayList = Arrays.asList(days.split(","));
        for (int i = 0; i < 10; i += 2) {
            this.days.put(dayList.get(i), Boolean.valueOf(dayList.get(i + 1)));
        }

    }


    public boolean conflicts(ClassObject compare) {
        for ( String key : days.keySet()) {
            if (compare.days.get(key) && this.days.get(key)) {
                if (this.begin <= compare.end && this.end >= compare.begin) return true;
            }
        }
        return false;
    }

    public void setConflictMap(Collection<ClassObject> input) {
        for (ClassObject element : input) {
            conflictMap.put(element, this.conflicts(element));
        }
    }
}

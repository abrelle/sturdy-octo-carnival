package Resources;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import Processes.ProcessInterface;
import Resources.ResourceEnum.Name;
import Resources.ResourceEnum.Type;

public  class Resource {

    private final ProcessInterface fatherProcess;
    private final Name name;
    private final Type type;
    private final Deque<ArrayList<Object>> elements;

    private boolean isFree = false;

    private final ArrayList<Object> elementList;
    private final ArrayList<ProcessInterface> waitingList;

    public Resource(ProcessInterface father, Name name, Type type){

        this.fatherProcess = father;
        this.name = name;
        this.type = type;

        System.out.println("Created recourse -> "+  name);
        fatherProcess.getResourceDistributor().addResource(this);
        fatherProcess.addResource(this);
        elements = new ArrayDeque<>(10);
        elementList = new ArrayList<>(100);
        waitingList = new ArrayList<>(100);
    }

    public void destroy(){
        fatherProcess.getCreatedResources().remove(this);
        elementList.clear();
    }

    public Object get(int i)
    {
        return elements.getFirst().get(i);
    }

    public void setAvailability(boolean free) {
        isFree = free;
    }

    public boolean isAvailable() {
        return isFree;
    }

    public Type getType() {
        return type;
    }

    public Name getName() {
        return name;
    }

    public ArrayList<Object> getElementList() {
        return elements.getFirst();
    }

    public Deque<ArrayList<Object>> getElements() {
        return elements;
    }

}

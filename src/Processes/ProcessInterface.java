package Processes;

import Processes.ProcessEnum.Name;
import Processes.ProcessEnum.State;
import Resources.Resource;
import Resources.ResourceDistributor;

import java.util.ArrayList;


public abstract class ProcessInterface {
    protected final ProcessInterface father;
    protected final ResourceDistributor resourceDistributor;
    protected final ProcessPlanner processPlanner;
    protected final ArrayList<Resource> createdResources;
    protected final ArrayList<ProcessInterface> createdProcesses;

    protected boolean active = false;
    protected boolean prepared = false;
    protected boolean stopped = false;
    protected State state;
    protected Name name;
    protected int priority;
    protected int IC = 0;

    public ProcessInterface(ProcessInterface father, State state, int priority,
                            Name name, ProcessPlanner planner, ResourceDistributor distributor) {
        this.father = father;
        this.state = state;
        this.name = name;
        this.priority = priority;

        System.out.println("Created process -> " + this.getName());


        this.processPlanner = planner;
        this.resourceDistributor = distributor;
        createdResources = new ArrayList<>(10);
        createdProcesses = new ArrayList<>(10);

        planner.getProcessList().add(this);
        if (this.father != null) {
            this.father.addProcess(this);
        }
    }

    public void destroy() {
        System.out.println("Destroying -> " + this.getName());
        createdResources.forEach(Resource::destroy);
        createdProcesses.forEach(ProcessInterface::destroy);

        if (this.father != null) {
            this.father.createdProcesses.remove(this);
        }
        processPlanner.getProcessList().remove(this);
    }

    public void stop() {
        stopped = true;
        if (active) {
            active = false;
            prepared = true;
        }
        System.out.println("Stopped ->" + this.getName());
    }

    public void activate() throws Exception {
        System.out.println("Activated -> " + this.getName());
        stopped = false;
        executeTask();
    }

    public void executeTask() throws Exception {
        System.out.println("Executed -> : " + this.getName() + " , step: " + IC );
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPrepared() {
        return prepared;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void addProcess(ProcessInterface process) {
        createdProcesses.add(process);
    }

    public void addResource(Resource resource) {
        createdResources.add(resource);
    }

    public ResourceDistributor getResourceDistributor() {
        return resourceDistributor;
    }

    public ArrayList<Resource> getCreatedResources() {
        return createdResources;
    }

    public ProcessPlanner getProcessPlanner() {
        return processPlanner;
    }

    public String getName() {
        return name.name();
    }

    @Override
    public String toString() {
        return "--------------------------------------------" + "\n" +
                name + " " + "\n" +
                "Active : " + active + "\n" +
                "Preapared : " + prepared + "\n" +
                "Stopped : " + stopped + "\n" +
                " Priority " + priority +
                "--------------------------------------------";
    }
}

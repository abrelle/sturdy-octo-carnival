import Processes.ProcessPlanner;
import Processes.RealMachine;
import Resources.ResourceDistributor;

public class Main {

    public static void main(String[] args) throws Exception {
        ProcessPlanner processPlanner = new ProcessPlanner();
        ResourceDistributor resourceDistributor = new ResourceDistributor(processPlanner);
        new RealMachine(processPlanner, resourceDistributor);
    }
}

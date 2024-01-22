import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;



public class MainContainer {
 public static void main(String[] args) {
     try {
         Runtime rt = Runtime.instance();
         Properties p = new ExtendedProperties();
         p.setProperty("gui", "true");
         ProfileImpl pc = new ProfileImpl(p);
         AgentContainer container = rt.createMainContainer(pc);
         container.start();

         // Créer et démarrer les instances d'EcomAgent
         for (int i = 1; i <= 10; i++) {
             AgentController ecomAgent = container.createNewAgent("EcomAgent" + i, "EcomAgent", null);
             ecomAgent.start();
         }

         // Créer et démarrer une instance de ClientAgent
         AgentController clientAgent = container.createNewAgent("ClientAgent", "ClientAgent", null);
         clientAgent.start();

         System.out.println("Main container started with n EcomAgents and 1 ClientAgent.");
     } catch (ControllerException e) {
         e.printStackTrace();
     }
 }
}


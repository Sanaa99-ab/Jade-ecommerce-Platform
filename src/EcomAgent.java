import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;


public class EcomAgent extends Agent {
    public String myOffer;
    private int availableQuantity;
    private int stockQuantity;

    protected void setup() {
        // Create the agent's service description
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ecommerce");
        sd.setName("Commerce en Ligne");

        // Create the agent's DFAgentDescription
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);
        stockQuantity = 50 + (int)(Math.random() * 100); // Par exemple entre 50 et 150
        System.out.println(getLocalName() + ": Stock initial = " + stockQuantity);
        availableQuantity = (int) (Math.random() * 100) + 20;
        myOffer = "Prix: " + (100 + (int)(Math.random() * 50)) + ", Délai: " + ((int)(Math.random() * 3) + 1) + 
        		" jours, Quantité: " + availableQuantity;
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Add behavior to handle CFP (Call for Proposals) from the client
        addBehaviour(new HandleCFPBehaviour(this));
    }
    public int getStockQuantity() {
        return stockQuantity;} 
    public void handleAcceptProposal(ACLMessage msg) {
        try {
            String quantityString = msg.getUserDefinedParameter("quantity");
            int quantityDemanded = Integer.parseInt(quantityString);
            if (quantityDemanded <= stockQuantity) {
                stockQuantity -= quantityDemanded;
                ACLMessage confirm = new ACLMessage(ACLMessage.CONFIRM);
                confirm.addReceiver(msg.getSender());
                confirm.setContent("Confirmation de l'acceptation de l'offre avec " + quantityDemanded + " unités.");
                send(confirm);
                System.out.println(getLocalName() + ": Stock mis à jour, nouveau stock = " + stockQuantity);
            } else {
                System.out.println(getLocalName() + ": Stock insuffisant pour compléter la demande.");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    protected void takeDown() {
        // Deregister from the DF
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
    
}
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleCFPBehaviour extends CyclicBehaviour {
    public EcomAgent myAgent;

    public HandleCFPBehaviour(EcomAgent agent) {
        super(agent);
        myAgent = agent;
    }
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.CFP) {
                String product = msg.getContent();
                int requestedQuantity = Integer.parseInt(msg.getUserDefinedParameter("quantity"));               
                int deadline = Integer.parseInt(msg.getUserDefinedParameter("deadline"));
                // Utiliser la quantité de stock de l'agent pour la proposition
                int stockQuantity = ((EcomAgent) myAgent).getStockQuantity();               
                String myOffer = "Prix: " + (100 + (int)(Math.random() * 50)) + ", Délai: " + 
                      ((int)(Math.random() * 3) + 1) + " jours, Quantité: " + stockQuantity;
                // Create a response message with the proposal
                ACLMessage propose = msg.createReply();
                propose.setPerformative(ACLMessage.PROPOSE);
                propose.setContent(myOffer);                             
                // Send the proposal
                myAgent.send(propose);
            } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                // Handle ACCEPT_PROPOSAL
                if(myAgent instanceof EcomAgent) {              	
                    ((EcomAgent) myAgent).handleAcceptProposal(msg);
                }
            }
        } else {
            block();
        }
    }

    
    }



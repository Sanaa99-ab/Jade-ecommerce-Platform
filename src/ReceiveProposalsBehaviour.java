

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveProposalsBehaviour extends CyclicBehaviour {
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            // Process proposals from e-commerce agents
            System.out.println("Proposition reçue de " + msg.getSender().getLocalName() + ": " + msg.getContent());
            ((ClientAgent) myAgent).updateGuiWithProposal(msg.getSender().getLocalName(), msg.getContent());
        } else {
            block();
        }
    }
}










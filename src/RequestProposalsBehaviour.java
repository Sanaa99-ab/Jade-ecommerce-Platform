import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class RequestProposalsBehaviour extends OneShotBehaviour {
    private String product;
    private int quantity;
    private String deadline;

    public RequestProposalsBehaviour(String product, int quantity, String deadline) {
        this.product = product;
        this.quantity = quantity;
        this.deadline = deadline;
    }

    public void action() {
        // Recherche des agents e-commerce
    	DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ecommerce");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            for (DFAgentDescription agent : result) {
                cfp.addReceiver(agent.getName());
            }
            cfp.setContent("Demande de prix pour ...");
            myAgent.send(cfp);
            System.out.println("CFP envoyé aux agents e-commerce.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
}}

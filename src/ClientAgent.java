
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import javax.swing.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientAgent extends Agent {
    private ClientGui gui;
    private List<AID> ecomAgents;
    private String selectedEcomAgent;  
    private List<String> ecomAgentNames = new ArrayList<>();  // Store names of e-commerce agents
    private AID chosenEcomAgent;
    private ArrayList<String> pendingOffers;


    protected void setup() {
        // Enregistrement avec l'agent DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("client");
        sd.setName("Client");
        dfd.addServices(sd);
        pendingOffers = new ArrayList<>();
        ecomAgents = new ArrayList<>();
        addBehaviour(new ReceiveProposalsBehaviour());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Initialisation de la GUI 
        gui = new ClientGui(this);
        gui.setVisible(true);
    }
    public void requestProposals(String product, int quantity, String deadline) {
        addBehaviour(new RequestProposalsBehaviour(product, quantity, deadline));
    }
    
    public void updateGuiWithProposal(String ecomAgentName, String offer) {
        // Update the GUI with the new proposal
    	pendingOffers.add(ecomAgentName + ": " + offer);
        SwingUtilities.invokeLater(() -> {
            gui.updateOffers(ecomAgentName + ": " + offer);
        });      
    }
    public void setSelectedEcomAgent(String agentName) {
        this.selectedEcomAgent = agentName;
    }
    private AID extractAIDFromOffer(String offer) {
        String agentName = offer.split(":")[0].trim();
        if (agentName.isEmpty()) {
            return null; // Handle cases where the offer format may vary
        }
        AID agentAID = new AID(agentName, AID.ISLOCALNAME);
        return agentAID;
    }
    public void acceptOffer(String offerDetails, int requestedQuantity) {
        AID agentAID = extractAIDFromOffer(offerDetails);
        if (agentAID != null) {
            sendAcceptProposal(agentAID, requestedQuantity);
			// Stop all other e-commerce agents
            for (AID ecomAgent : ecomAgents) {
                if (!ecomAgent.equals(agentAID)) {
                    sendRefuse(ecomAgent);
                }
            }

            // Display the accepted offer in the GUI
            SwingUtilities.invokeLater(() -> {
                OfferDetailsDialog offerDetailsDialog = new OfferDetailsDialog(this.gui, offerDetails);
                offerDetailsDialog.setVisible(true);
            });
            pendingOffers.remove(offerDetails);
            
            }
    }
    
    public int extractRequestedQuantityFromOffer(String offerDetails) {
        Pattern pattern = Pattern.compile("Quantité: (\\d+)"); // Motif spécifique pour la quantité
        Matcher matcher = pattern.matcher(offerDetails);
        
        if (matcher.find()) {
            String quantityString = matcher.group(1); // Extraire le nombre après "Quantité: "
            try {
            	
                return Integer.parseInt(quantityString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    private void sendAcceptProposal(AID agentAID, int requestedQuantity) {
        // Préparation de l'envoi d'un ACCEPT_PROPOSAL avec la quantité demandée
        if (agentAID != null) {
            ACLMessage acceptProposal = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            acceptProposal.addReceiver(agentAID);

            // Ajoutez la quantité en tant que paramètre utilisateur
            acceptProposal.addUserDefinedParameter("quantity", String.valueOf(requestedQuantity));

            // Contenu du message
            String content = "Offre acceptée client agent avec une quantité de " + requestedQuantity;
            acceptProposal.setContent(content);

            // Imprimez le message
            System.out.println("Envoi de l'offre acceptée avec une quantité de " + requestedQuantity);

            // Envoyez le message
            send(acceptProposal);
        } else {
            System.err.println("ERREUR : agentAID est null. Impossible d'envoyer le message ACCEPT_PROPOSAL.");
        }
    } 
    private void sendRefuse(AID agentAID) {
        ACLMessage refuse = new ACLMessage(ACLMessage.REFUSE);
        refuse.addReceiver(agentAID);
        send(refuse);
    }
    public void rejectOffers() {
        for (String agentName : ecomAgentNames) {
            if (!agentName.equals(selectedEcomAgent)) {
                ACLMessage rejection = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                rejection.addReceiver(new jade.core.AID(agentName, jade.core.AID.ISLOCALNAME));
                send(rejection);
            }
        }
    }
    
    public void sendRequest(String product, int quantity, int deadline) {
        // Create a CFP (Call for Proposals) message
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        cfp.setContent(product);
        cfp.addUserDefinedParameter("quantity", String.valueOf(quantity));
        cfp.addUserDefinedParameter("deadline", String.valueOf(deadline));

        // Search for e-commerce agents
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ecommerce");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription agent : result) {
                cfp.addReceiver(agent.getName());
            }
            send(cfp);
            
            System.out.println("CFP sent to e-commerce agents.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
    protected void takeDown() {
        // Désenregistrement avec l'agent DF
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Fermer la GUI
        gui.dispose();
    }

    }

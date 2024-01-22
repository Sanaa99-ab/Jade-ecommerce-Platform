import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientGui extends JFrame {
	private JTextField productField, quantityField, deadlineField;
    private JButton submitButton;
    private ClientAgent agent;
    private JTextArea offersArea;
    private JPanel offersPanel;
    private ArrayList<String> pendingOffers;
    private JButton acceptOfferButton;
    private boolean offerAccepted = false;

    ClientGui(ClientAgent agent) {
        super("Client Interface");
        this.agent = agent;
        setSize(500, 400);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialisation des composants
        productField = new JTextField(10);
        quantityField = new JTextField(10);
        deadlineField = new JTextField(10);
        submitButton = new JButton("Submit");

        // Configuration des composants dans le GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Deadline:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(deadlineField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(submitButton, gbc);

        // Configuration du bouton Submit
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String product = productField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                int deadline = Integer.parseInt(deadlineField.getText());
                agent.sendRequest(product, quantity, deadline);
            }
        });

        add(inputPanel, BorderLayout.NORTH);

        offersArea = new JTextArea(10, 30);
        offersArea.setEditable(false);
        add(new JScrollPane(offersArea), BorderLayout.CENTER);

        offersPanel = new JPanel();
        offersPanel.setLayout(new BoxLayout(offersPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(offersPanel), BorderLayout.CENTER);

        pendingOffers = new ArrayList<>();

        setVisible(true);
    }

    public void updateOffers(String offer) {
        offersArea.append(offer + "\n");
        pendingOffers.add(offer);
        addOffer(offer);
    }
    
    private void handleOfferAcceptance(String offer) {
        try {
            int requestedQuantity = Integer.parseInt(quantityField.getText());
            // Traitement de l'offre acceptée
            offerAccepted = true;
            agent.acceptOffer(offer, requestedQuantity);
            pendingOffers.clear();
            pendingOffers.add(offer);
            updateOffersPanel();
        } catch (NumberFormatException e) {
            // Gérer l'exception si la quantité n'est pas un nombre valide
            JOptionPane.showMessageDialog(this, "La quantité doit être un nombre.", "Erreur de Quantité", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JButton createAcceptButton(String offer) {
        JButton acceptButton = new JButton("Accepter l'offre");
        acceptButton.setBackground(Color.GREEN);
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                handleOfferAcceptance(offer);
            }
        });
        return acceptButton;
    }    
    public void addOffer(String offer) {
        String[] offerDetails = offer.split(", ");
        String price = offerDetails[0];
        String deadline = offerDetails[1];
        String quantity = offerDetails[2];

        // Création des JLabels pour chaque détail
        JLabel priceLabel = new JLabel(price);
        JLabel deadlineLabel = new JLabel(deadline);
        JLabel quantityLabel = new JLabel(quantity);

        // Centrage du texte et augmentation de la taille de police
        Font labelFont = new Font("SansSerif", Font.BOLD, 16); // Choisissez la police et la taille souhaitée
        priceLabel.setHorizontalAlignment(JLabel.CENTER);
        deadlineLabel.setHorizontalAlignment(JLabel.CENTER);
        quantityLabel.setHorizontalAlignment(JLabel.CENTER);

        priceLabel.setFont(labelFont);
        deadlineLabel.setFont(labelFont);
        quantityLabel.setFont(labelFont);

        // Alignement des JLabels au centre du JPanel
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        deadlineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Configuration du JPanel pour l'offre
        JPanel offerPanel = new JPanel();
        offerPanel.setLayout(new BoxLayout(offerPanel, BoxLayout.Y_AXIS)); 
        offerPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN)); 

        // Ajout des détails de l'offre au panel
        offerPanel.add(priceLabel);
        offerPanel.add(deadlineLabel);
        offerPanel.add(quantityLabel);

        // Création et ajout du bouton pour accepter l'offre
        JButton acceptButton = createAcceptButton(offer);
        offerPanel.add(acceptButton);

        // Ajout du panneau de l'offre, si aucune offre n'a été précédemment acceptée
        if (!offerAccepted) {
            offersPanel.add(offerPanel);
        }

        // Rafraîchir l'interface
        revalidate();
        repaint();
    }    
    public void updateOffersPanel() {
    	
    	for (Component comp : offersPanel.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JButton && !innerComp.equals(acceptOfferButton)) {
                        innerComp.setBackground(offerAccepted ? Color.RED : Color.GREEN);
                    }
                }
            }
        }
    	
        //offersPanel.removeAll();
        if (offerAccepted) {
            addOffer(pendingOffers.get(0)); // Ajoute seulement l'offre acceptée
        } else {
            for (String offer : pendingOffers) {
                addOffer(offer); // Ajoute toutes les offres en attente
            }
        }
        revalidate();
        repaint();
    }
}





import javax.swing.*;
import java.awt.*;

public class OfferDetailsDialog extends JDialog {
    public OfferDetailsDialog(JFrame parent, String offerDetails) {
        super(parent, "Détails de l'offre acceptée", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);

        // Configuration du JTextArea
        JTextArea offerTextArea = new JTextArea(offerDetails);
        offerTextArea.setEditable(false);
        offerTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); 
        offerTextArea.setMargin(new Insets(10, 10, 10, 10)); 

        // Ajout du JScrollPane avec JTextArea
        JScrollPane scrollPane = new JScrollPane(offerTextArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Configuration du bouton Fermer
        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> dispose());
        closeButton.setPreferredSize(new Dimension(100, 30)); 

        // Configuration du JPanel pour le bouton
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); 
        buttonPanel.add(closeButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(Color.LIGHT_GRAY); 
    }
}

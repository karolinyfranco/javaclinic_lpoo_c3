package br.faesa.javaclinic_lpoo_c3.principal;

import br.faesa.javaclinic_lpoo_c3.view.principal.MenuPrincipal;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Principal {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();

            // Customizações visuais
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);

        } catch (Exception e) {
            System.err.println("Erro ao configurar tema: " + e.getMessage());
        }

        // Iniciar aplicação
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}

package br.com.tavora.sigfatd.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PrazoTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Pega o componente padrão (um JLabel) para customização
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Centraliza o texto na célula
        setHorizontalAlignment(CENTER);

        // Reseta a cor de fundo para o padrão para evitar que cores antigas persistam
        if (isSelected) {
            cellComponent.setBackground(table.getSelectionBackground());
            cellComponent.setForeground(table.getSelectionForeground());
        } else {
            cellComponent.setBackground(table.getBackground());
            cellComponent.setForeground(table.getForeground());
        }

        // Verifica se o valor da célula é uma String (para evitar erros)
        if (value instanceof String) {
            String status = (String) value;

            switch (status) {
                case "VENCIDO":
                    cellComponent.setBackground(new Color(255, 102, 102)); // Vermelho claro
                    cellComponent.setForeground(Color.WHITE);
                    break;
                case "PRÓXIMO DO VENCIMENTO":
                    cellComponent.setBackground(new Color(255, 255, 153)); // Amarelo claro
                    cellComponent.setForeground(Color.BLACK);
                    break;
                default:
                    // Mantém as cores padrão para "EM DIA" e outros valores
                    break;
            }
        }

        return cellComponent;
    }
}
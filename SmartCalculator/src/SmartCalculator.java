import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class SmartCalculator extends JFrame implements ActionListener {
    private JTextField display;
    private StringBuilder expression = new StringBuilder();  // To build the expression string

    public SmartCalculator() {
        setTitle("Smart Calculator");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display field
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        add(display, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "←", "", ""
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(this);
            if (!text.isEmpty()) {
                buttonPanel.add(button);
            }
        }

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.matches("[0-9]")) {
            expression.append(command);
            display.setText(expression.toString());
        } else if (command.equals(".")) {
            // Allow decimal only if the last part doesn't already have one
            String[] parts = expression.toString().split("[+\\-*/]");
            if (parts.length > 0 && !parts[parts.length - 1].contains(".")) {
                expression.append(".");
                display.setText(expression.toString());
            }
        } else if (command.matches("[+\\-*/]")) {
            if (!expression.toString().isEmpty() && !expression.toString().endsWith(" ") && !isOperator(expression.toString().charAt(expression.length() - 1))) {
                expression.append(" ").append(command).append(" ");
                display.setText(expression.toString());
            }
        } else if (command.equals("=")) {
            if (!expression.toString().isEmpty()) {
                try {
                    double result = evaluateExpression(expression.toString());
                    expression.setLength(0);
                    expression.append(result);
                    display.setText(String.valueOf(result));
                } catch (Exception ex) {
                    display.setText("Error");
                    expression.setLength(0);
                }
            }
        } else if (command.equals("C")) {
            expression.setLength(0);
            display.setText("");
        } else if (command.equals("←")) {
            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
                display.setText(expression.toString());
            }
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private double evaluateExpression(String expr) {
        List<String> tokens = tokenize(expr);
        if (tokens.size() < 3 || tokens.size() % 2 == 0) {
            throw new IllegalArgumentException("Invalid expression");
        }

        // First, handle * and / with precedence
        List<String> processed = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("*") || tokens.get(i).equals("/")) {
                double left = Double.parseDouble(processed.remove(processed.size() - 1));
                double right = Double.parseDouble(tokens.get(++i));
                double result = tokens.get(i - 1).equals("*") ? left * right : left / right;
                if (tokens.get(i - 1).equals("/") && right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                processed.add(String.valueOf(result));
            } else {
                processed.add(tokens.get(i));
            }
        }

        // Then, handle + and -
        double result = Double.parseDouble(processed.get(0));
        for (int i = 1; i < processed.size(); i += 2) {
            double next = Double.parseDouble(processed.get(i + 1));
            if (processed.get(i).equals("+")) {
                result += next;
            } else if (processed.get(i).equals("-")) {
                result -= next;
            }
        }
        return result;
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder num = new StringBuilder();
        for (char c : expr.toCharArray()) {
            if (c == ' ') {
                continue;  // Skip spaces
            } else if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else if (isOperator(c)) {
                if (num.length() > 0) {
                    tokens.add(num.toString());
                    num.setLength(0);
                }
                tokens.add(String.valueOf(c));
            }
        }
        if (num.length() > 0) {
            tokens.add(num.toString());
        }
        return tokens;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartCalculator());
    }
}

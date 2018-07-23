package gui;

import data.Order;
import gui.order.OpenOrdersCell;
import gui.order.OpenOrdersTableModel;
import org.apache.commons.lang3.StringUtils;
import rest.BitmexRestMethods;
import utils.Broadcaster;
import utils.Formatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

public class MainWindow extends JFrame implements Broadcaster.BroadcastListener {

    private JScrollPane ordersScrollPane;
    private JTable ordersTable;
    private static ArrayList<Order> orders = new ArrayList<>();

    private static GridBagConstraints gbc = new GridBagConstraints();
    private static JPanel tradeprofitstopPanel = new JPanel(new GridBagLayout());

    private static JTextField amtField;
    private static JTextField priceField;
    private static JPanel pricePanel;

    private static JButton buy;
    private static JButton sell;

    private static JRadioButton forcemakerRadio;
    private static boolean forcemakerBool = true;

    private static JRadioButton limitCheckbox;
    private static boolean limitBool = false;

    private static JRadioButton marketCheckbox;
    private static boolean marketBool = false;

    public MainWindow(String title) {
        super(title);
        setLayout(new GridBagLayout());
        Broadcaster.register(this);

        orders.add(new Order("update", "Buy", 10000, "New", "7200.5", "dff14hj-8ecbff2-8591039a-348a"));
        orders.add(new Order("update", "Buy", 10000, "New", "7100.5", "678dshj-dgssdjf-sdfg39a-sgf64h"));
        orders.add(new Order("update", "Buy", 20000, "New", "7000.5", "ppooyhj-8ecbfnj-859jgfjjj-oyyi"));

        setupTradeProfitStopPanels();

        openOrdersPanel();

        setupBuySellButtons();

        setupTradeStuff();

        setupTradeRadios();

    }

    private void setupTradeRadios() {

        forcemakerRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                forcemakerBool = true;
                limitBool = false;
                marketBool = false;

                pricePanel.setVisible(false);
            }
        });

        limitCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                forcemakerBool = false;
                limitBool = true;
                marketBool = false;

                pricePanel.setVisible(true);
            }
        });

        marketCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                forcemakerBool = false;
                limitBool = false;
                marketBool = true;

                pricePanel.setVisible(false);
            }
        });
    }

    private void setupTradeStuff() {

        //price field hidden on start
        pricePanel.setVisible(false);

        //start with forcemaker radio on
        forcemakerRadio.setSelected(true);

    }

    private void setupBuySellButtons() {

        buy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println((forcemakerBool ? "forcemaker" : "") + (limitBool ? "limit" : "") + (marketBool ? "market" : "") + " buy " + amtField.getText());

                if (forcemakerBool) {

                } else if (limitBool) {
                    try { BitmexRestMethods.limit((double) Formatter.getNumber(amtField.getText()), Double.parseDouble(priceField.getText()), true);
                    } catch (InterruptedException e1) { e1.printStackTrace(); }
                } else if (marketBool) {
                    System.out.println("market");
                    try { BitmexRestMethods.market((double) Formatter.getNumber(amtField.getText()), true);
                    } catch (InterruptedException e1) { e1.printStackTrace(); }
                }

            }
        });

        sell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println((forcemakerBool ? "forcemaker" : "") + (limitBool ? "limit" : "") + (marketBool ? "market" : "") + " sell " + amtField.getText());

                if (forcemakerBool) {

                } else if (limitBool) {
                    try { BitmexRestMethods.limit((double) -Formatter.getNumber(amtField.getText()), Double.parseDouble(priceField.getText()), true);
                    } catch (InterruptedException e1) { e1.printStackTrace(); }
                } else if (marketBool) {
                    System.out.println("market");
                    try { BitmexRestMethods.market((double) -Formatter.getNumber(amtField.getText()), true);
                    } catch (InterruptedException e1) { e1.printStackTrace(); }
                }
            }
        });


    }

    private void setupTradeProfitStopPanels() {

        gbc(0, 0, 0, 0, GridBagConstraints.NORTHWEST);
        add(tradeprofitstopPanel, gbc);

        tradePanel();
        takeProfitPanel();
        stopsPanel();
    }

    private void tradePanel() {

        //make main trade panel
        JPanel tradePanel = new JPanel(new GridBagLayout());
        tradePanel.setBorder(BorderFactory.createTitledBorder("trade"));

        //amount: [ ]
        JPanel amountPanel = new JPanel();

        JLabel amountLabel = new JLabel("amount");
        amountPanel.add(amountLabel);

        amtField = new JTextField(7);
        amountPanel.add(amtField);

        //price: [ ]
        pricePanel = new JPanel();

        JLabel priceLabel = new JLabel("price");
        pricePanel.add(priceLabel);

        priceField = new JTextField(7);
        pricePanel.add(priceField);

        //panel for amount and price
        JPanel amountPricePanel = new JPanel(new GridBagLayout());
        gbc(0, 0, 0, 0, GridBagConstraints.EAST);
        amountPricePanel.add(amountPanel, gbc);
        gbc(0, 1, 0, 0, GridBagConstraints.EAST);
        amountPricePanel.add(pricePanel, gbc);

        gbc(0, 0, 0, 0, GridBagConstraints.CENTER);
        tradePanel.add(amountPricePanel, gbc);

        // [Buy] [Sell]
        JPanel buysellButtonsPanel = new JPanel();

        buy = new JButton("Buy");
        buysellButtonsPanel.add(buy);

        sell = new JButton("Sell");
        buysellButtonsPanel.add(sell);

        gbc(0, 1, 1, 1, GridBagConstraints.CENTER);
        tradePanel.add(buysellButtonsPanel, gbc);

        // * ordertype radios
        forcemakerRadio = new JRadioButton("force maker");
        limitCheckbox = new JRadioButton("limit");
        marketCheckbox = new JRadioButton("market");

        ButtonGroup radios1 = new ButtonGroup();
        radios1.add(forcemakerRadio);
        radios1.add(limitCheckbox);
        radios1.add(marketCheckbox);

        gbc(0, 2, 1, 1, GridBagConstraints.WEST);
        tradePanel.add(forcemakerRadio, gbc);
        gbc(0, 3, 1, 1, GridBagConstraints.WEST);
        tradePanel.add(limitCheckbox, gbc);
        gbc(0, 4, 1, 1, GridBagConstraints.WEST);
        tradePanel.add(marketCheckbox, gbc);

        //add tradepanel to main panelgroup
        gbc(0, 0, 0, 0, GridBagConstraints.NORTHWEST);
        tradeprofitstopPanel.add(tradePanel, gbc);

    }

    private void takeProfitPanel() {

        JPanel takeprofitPanel = new JPanel(new GridBagLayout());
        takeprofitPanel.setBorder(BorderFactory.createTitledBorder("take profits"));

        gbc(0, 0, 0, 0, GridBagConstraints.WEST);
        JCheckBox takeprofit1 = new JCheckBox("take profit 1");

        takeprofitPanel.add(takeprofit1, gbc);

        //add to main panel
        gbc(1, 0, 0, 0, GridBagConstraints.NORTHWEST);
        tradeprofitstopPanel.add(takeprofitPanel, gbc);

    }

    private void stopsPanel() {

        JPanel stopsPanel = new JPanel(new GridBagLayout());
        stopsPanel.setBorder(BorderFactory.createTitledBorder("stops"));

        gbc(0, 0, 0, 0, GridBagConstraints.WEST);
        JCheckBox stop1 = new JCheckBox("stop 1");

        stopsPanel.add(stop1, gbc);

        //add to main panel
        gbc(2, 0, 0, 0, GridBagConstraints.NORTHWEST);
        tradeprofitstopPanel.add(stopsPanel, gbc);

    }

    private void openOrdersPanel() {
//
        //panel
//        JPanel ordersPanel = new JPanel(new GridBagLayout());
//        ordersPanel.setBorder(BorderFactory.createTitledBorder("open orders"));
//
//        //order1
//        JLabel order1 = new JLabel("order 1: buy 5,000 (force maker) status: unfilled");
//        ordersPanel.add(order1);
//
//        //order2
//        JLabel order2 = new JLabel("order 2: sell 10,000 at 6503.5 status: unfilled ");
//        ordersPanel.add(order2);
//
//        gbc(0,1,1,1, GridBagConstraints.NORTHWEST);
//        add(ordersPanel, gbc);




        ordersTable = new JTable(new OpenOrdersTableModel(orders));
        ordersTable.setDefaultRenderer(Order.class, new OpenOrdersCell());
        ordersTable.setTableHeader(null);

        gbc(0,1,1,1,GridBagConstraints.NORTHWEST);
        gbc.fill = GridBagConstraints.BOTH;
        add(ordersTable, gbc);
        gbc.fill = GridBagConstraints.NONE;


    }

    private void gbc(int gridx, int gridy, double weightx, double weighty, int anchor) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.anchor = anchor;
    }


    @Override
    public void receiveBroadcast(String message) throws InterruptedException, IOException {

        System.out.println("order msg received: " + message);

        if (message.contains("table\":\"order")) {

            String id = StringUtils.substringBetween(message, "\"orderID\":\"", "\",");
            String action = StringUtils.substringBetween(message, "\"action\":\"", "\",");
            int currentAmt;
            try {
                currentAmt = Integer.parseInt(StringUtils.substringBetween(message, "\"leavesQty\":", ","));
            } catch (Exception e) {
                currentAmt = -1;
            }
            String side = StringUtils.substringBetween(message, "\"side\":\"", "\",");
            String status = StringUtils.substringBetween(message, "\"ordStatus\":\"", "\",");

            String price = StringUtils.substringBetween(message, "\"price\":\"", "\",");

            boolean existingOrder = false;
            for (Order o : orders) {
                if (o.getId().contains(id)) {
                    System.out.println("order already in list, updating");
                    if (status != null) {
                        o.setStatus(status);
                    }
                    existingOrder = true;
                }
            }

            if (!existingOrder && !id.contains("guid")) {
                System.out.println("order not in list, adding");
                orders.add(new Order(action, side, currentAmt, status, price, id));
            }

            SwingUtilities.invokeLater(() -> {

                revalidate();
                repaint();

                getContentPane().revalidate();
                getContentPane().revalidate();

                ordersTable.revalidate();
                ordersTable.repaint();

            });

        }

    }
}

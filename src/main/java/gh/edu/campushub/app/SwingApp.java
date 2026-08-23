package gh.edu.campushub.app;

import gh.edu.campushub.algorithms.dp.Knapsack;
import gh.edu.campushub.algorithms.dp.KnapsackItem;
import gh.edu.campushub.algorithms.graph.BFS;
import gh.edu.campushub.algorithms.graph.DFS;
import gh.edu.campushub.algorithms.graph.Dijkstra;
import gh.edu.campushub.algorithms.graph.Kruskal;
import gh.edu.campushub.algorithms.graph.MstEdge;
import gh.edu.campushub.algorithms.graph.MstResult;
import gh.edu.campushub.algorithms.graph.Prim;
import gh.edu.campushub.algorithms.greedy.GreedyKnapsack;
import gh.edu.campushub.algorithms.greedy.GreedyResourceAssignment;
import gh.edu.campushub.algorithms.search.BinarySearch;
import gh.edu.campushub.algorithms.search.LinearSearch;
import gh.edu.campushub.algorithms.sort.InsertionSort;
import gh.edu.campushub.algorithms.sort.MergeSort;
import gh.edu.campushub.algorithms.sort.QuickSort;
import gh.edu.campushub.algorithms.sort.SelectionSort;
import gh.edu.campushub.db.CsvDataLoader;
import gh.edu.campushub.engine.AuditLog;
import gh.edu.campushub.engine.CampusDataStore;
import gh.edu.campushub.experiments.PerformanceLab;
import gh.edu.campushub.model.*;
import gh.edu.campushub.structures.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class SwingApp extends JFrame {

    static final Color BG_DARK       = new Color(0x1A1B2E);
    static final Color BG_SIDEBAR    = new Color(0x16213E);
    static final Color BG_CARD       = new Color(0x0F3460);
    static final Color BG_CARD2      = new Color(0x1B2A4A);
    static final Color ACCENT        = new Color(0xF0A500);
    static final Color ACCENT_HOVER  = new Color(0xFFBB33);
    static final Color TEXT_PRIMARY  = new Color(0xECEFF4);
    static final Color TEXT_SECONDARY= new Color(0x8892A4);
    static final Color TEXT_SUCCESS  = new Color(0x4CAF50);
    static final Color TEXT_ERROR    = new Color(0xEF5350);
    static final Color TEXT_INFO     = new Color(0x42A5F5);
    static final Color BORDER_COLOR  = new Color(0x2D3A5C);
    static final Color TABLE_HEADER  = new Color(0x0A2647);
    static final Color TABLE_ROW_ALT = new Color(0x1C2D4A);

    static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  14);
    static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 12);
    static final Font FONT_NAV    = new Font("Segoe UI", Font.BOLD,  13);
    static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font FONT_STAT   = new Font("Segoe UI", Font.BOLD,  18);

    private final CampusDataStore store;
    private final AuditLog auditLog;
    private JLabel statusLabel;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    private final List<JButton> navButtons = new ArrayList<>();
    private int activeNavIndex = 0;

    public SwingApp(CampusDataStore store) {
        this.store = store;
        this.auditLog = new AuditLog(store.auditEventDao());
        applyGlobalUIDefaults();
        buildFrame();
    }

    private void applyGlobalUIDefaults() {
        UIManager.put("Panel.background",            BG_DARK);
        UIManager.put("ScrollPane.background",       BG_DARK);
        UIManager.put("Viewport.background",         BG_DARK);
        UIManager.put("TextArea.background",         new Color(0x0B1120));
        UIManager.put("TextArea.foreground",         TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground",    ACCENT);
        UIManager.put("TextField.background",        BG_CARD2);
        UIManager.put("TextField.foreground",        TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",   ACCENT);
        UIManager.put("ComboBox.background",         BG_CARD2);
        UIManager.put("ComboBox.foreground",         TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",BG_CARD);
        UIManager.put("ComboBox.selectionForeground",ACCENT);
        UIManager.put("Label.foreground",            TEXT_PRIMARY);
        UIManager.put("ProgressBar.background",      BG_CARD2);
        UIManager.put("ProgressBar.foreground",      ACCENT);
        UIManager.put("ScrollBar.background",        BG_SIDEBAR);
        UIManager.put("ScrollBar.thumb",             BG_CARD);
        UIManager.put("ScrollBar.track",             BG_SIDEBAR);
        UIManager.put("Table.background",            BG_DARK);
        UIManager.put("Table.foreground",            TEXT_PRIMARY);
        UIManager.put("Table.gridColor",             BORDER_COLOR);
        UIManager.put("Table.selectionBackground",   BG_CARD);
        UIManager.put("Table.selectionForeground",   ACCENT);
        UIManager.put("TableHeader.background",      TABLE_HEADER);
        UIManager.put("TableHeader.foreground",      TEXT_PRIMARY);
        UIManager.put("ToolTip.background",          BG_CARD);
        UIManager.put("ToolTip.foreground",          TEXT_PRIMARY);
        UIManager.put("OptionPane.background",       BG_DARK);
        UIManager.put("OptionPane.messageForeground",TEXT_PRIMARY);
    }

    private void buildFrame() {
        setTitle("Ghana Smart Service Operations Optimizer — Campus Hub");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 750));
        setPreferredSize(new Dimension(1400, 850));
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);
        add(buildContent(),   BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        updateStatus();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_SIDEBAR);
        header.setBorder(new MatteBorder(0, 0, 2, 0, ACCENT));
        header.setPreferredSize(new Dimension(0, 64));

        JLabel logo = new JLabel("  🎓  Ghana Smart Service Operations Optimizer");
        logo.setFont(FONT_TITLE);
        logo.setForeground(ACCENT);
        header.add(logo, BorderLayout.WEST);

        JLabel tagline = new JLabel("University of Ghana, Legon · DCIT 204/308 DSA Project  ");
        tagline.setFont(FONT_SMALL);
        tagline.setForeground(TEXT_SECONDARY);
        header.add(tagline, BorderLayout.EAST);

        return header;
    }

    private static final String[][] NAV_ITEMS = {
        {"🏠", "Home"},
        {"🗄", "Data & DB"},
        {"🧱", "Structures"},
        {"🔍", "Search & Sort"},
        {"🗺", "Graph Engine"},
        {"⚡", "Optimisation"},
        {"📋", "Audit Log"},
        {"📊", "Performance"}
    };

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COLOR));

        sidebar.add(Box.createVerticalStrut(20));

        for (int i = 0; i < NAV_ITEMS.length; i++) {
            final int idx = i;
            JButton btn = buildNavButton(NAV_ITEMS[i][0], NAV_ITEMS[i][1], i == 0);
            btn.addActionListener(e -> navigateTo(idx));
            navButtons.add(btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(4));
        }

        sidebar.add(Box.createVerticalGlue());

        JLabel version = new JLabel("  v1.0.0 · Campus Hub");
        version.setFont(FONT_SMALL);
        version.setForeground(TEXT_SECONDARY);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(version);
        sidebar.add(Box.createVerticalStrut(16));

        return sidebar;
    }

    private JButton buildNavButton(String icon, String label, boolean active) {
        JButton btn = new JButton(icon + "  " + label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(BG_CARD2);
                    g2.fillRoundRect(6, 2, getWidth()-12, getHeight()-4, 10, 10);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(FONT_NAV);
        btn.setForeground(active ? ACCENT : TEXT_SECONDARY);
        btn.setBackground(active ? BG_CARD2 : BG_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setPreferredSize(new Dimension(200, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (active) {
            btn.setBorder(new MatteBorder(0, 3, 0, 0, ACCENT));
        } else {
            btn.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        }
        btn.setOpaque(false);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.getForeground() != ACCENT) btn.setForeground(TEXT_PRIMARY);
            }
            public void mouseExited(MouseEvent e) {
                if (btn.getForeground() != ACCENT) btn.setForeground(TEXT_SECONDARY);
            }
        });
        return btn;
    }

    private void navigateTo(int idx) {
        activeNavIndex = idx;
        cardLayout.show(contentPanel, NAV_ITEMS[idx][1]);
        for (int i = 0; i < navButtons.size(); i++) {
            JButton b = navButtons.get(i);
            boolean active = (i == idx);
            b.setForeground(active ? ACCENT : TEXT_SECONDARY);
            b.setBackground(active ? BG_CARD2 : BG_SIDEBAR);
            b.setBorder(active
                ? new MatteBorder(0, 3, 0, 0, ACCENT)
                : BorderFactory.createEmptyBorder(0, 3, 0, 0));
        }
    }

    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);

        contentPanel.add(buildHomePanel(),         NAV_ITEMS[0][1]);
        contentPanel.add(buildDataPanel(),         NAV_ITEMS[1][1]);
        contentPanel.add(buildStructuresPanel(),   NAV_ITEMS[2][1]);
        contentPanel.add(buildSearchSortPanel(),   NAV_ITEMS[3][1]);
        contentPanel.add(buildGraphPanel(),        NAV_ITEMS[4][1]);
        contentPanel.add(buildOptimisationPanel(), NAV_ITEMS[5][1]);
        contentPanel.add(buildAuditPanel(),        NAV_ITEMS[6][1]);
        contentPanel.add(buildPerformancePanel(),  NAV_ITEMS[7][1]);

        return contentPanel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_SIDEBAR);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(0, 28));

        statusLabel = new JLabel("  Loading...");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_SECONDARY);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel hint = new JLabel("All algorithms run on background threads — UI stays responsive  ");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_SECONDARY);
        bar.add(hint, BorderLayout.EAST);

        return bar;
    }

    void updateStatus() {
        if (statusLabel != null) {
            statusLabel.setText(String.format(
                "  ✅  %d locations  •  %d roads  •  %d resources  •  %d requests",
                store.locations().size(), store.roads().size(),
                store.resources().size(), store.requests().size()
            ));
        }
    }

    private JPanel buildHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel banner = new JPanel(new GridLayout(1, 4, 16, 0));
        banner.setBackground(BG_DARK);
        banner.add(statCard("📍 Locations", () -> String.valueOf(store.locations().size()), TEXT_INFO));
        banner.add(statCard("🛣  Roads",     () -> String.valueOf(store.roads().size()),    ACCENT));
        banner.add(statCard("🚐 Resources",  () -> String.valueOf(store.resources().size()), TEXT_SUCCESS));
        banner.add(statCard("📋 Requests",   () -> String.valueOf(store.requests().size()), new Color(0xBE4BDB)));
        panel.add(banner, BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(2, 1, 0, 20));
        centre.setBackground(BG_DARK);

        JPanel info = createCard("About This System");
        info.setLayout(new BorderLayout());
        JTextArea desc = new JTextArea(
            "Ghana Smart Service Operations Optimizer — University Campus Service Hub\n\n" +
            "This system loads the University of Ghana, Legon campus data (locations, roads,\n" +
            "service requests, resources) into 13 custom-built data structures and runs search,\n" +
            "sort, graph, greedy and dynamic-programming algorithms over them:\n\n" +
            "  • Dispatch scheduling        • Shortest routes (Dijkstra)\n" +
            "  • Minimum-cost networks      • Budget-constrained request selection\n" +
            "  • Priority-based assignment  • Audit / Undo log\n" +
            "  • Comparative sorting        • Timed performance experiments\n\n" +
            "Navigate using the sidebar to explore each module."
        );
        desc.setFont(FONT_BODY);
        desc.setForeground(TEXT_SECONDARY);
        desc.setBackground(BG_CARD);
        desc.setEditable(false);
        desc.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        info.add(desc);
        centre.add(info);

        JPanel dsPanel = createCard("13 Custom Data Structures (all built from scratch — no java.util equivalents)");
        dsPanel.setLayout(new GridLayout(3, 5, 8, 6));
        String[] structures = {
            "Dynamic Array", "Linked List", "Stack", "FIFO Queue", "Circular Queue",
            "Deque", "Binary Heap", "BST", "AVL Tree", "B-Tree",
            "Hash Table", "Hash Set", "Disjoint Set", "Graph (Adj List)", "Graph (Adj Matrix)"
        };
        for (String s : structures) {
            JLabel lbl = new JLabel("✦ " + s);
            lbl.setFont(FONT_SMALL);
            lbl.setForeground(TEXT_INFO);
            lbl.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            dsPanel.add(lbl);
        }
        centre.add(dsPanel);

        panel.add(centre, BorderLayout.CENTER);
        return panel;
    }

    private JPanel statCard(String title, java.util.function.Supplier<String> valueSupplier, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(color.darker(), 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_BODY);
        titleLbl.setForeground(TEXT_SECONDARY);

        JLabel valLbl = new JLabel(valueSupplier.get());
        valLbl.setFont(FONT_STAT);
        valLbl.setForeground(color);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl,   BorderLayout.CENTER);
        return card;
    }

    private JPanel buildDataPanel() {
        JPanel panel = makeModulePanel("🗄  Data & Database");

        JTextArea out = makeOutputArea();
        JPanel controls = makeControlsCard("Operations");
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

        JButton reloadBtn   = makeAccentButton("↺  Reload from Database");
        JButton reimportBtn = makeSecondaryButton("📥  Re-import Seed CSVs");

        reloadBtn.addActionListener(e -> runAsync(out, () -> {
            appendLine(out, "[ Reloading structures from database... ]", ACCENT);
            store.loadFromDatabase();
            updateStatus();
            appendLine(out, String.format("✅  Reloaded: %d locations, %d roads, %d resources, %d requests.",
                store.locations().size(), store.roads().size(),
                store.resources().size(), store.requests().size()), TEXT_SUCCESS);
        }));

        reimportBtn.addActionListener(e -> runAsync(out, () -> {
            appendLine(out, "[ Re-importing seed CSVs from ./data ... ]", ACCENT);
            CsvDataLoader loader = new CsvDataLoader(store.connection());
            String report = loader.loadAll(Path.of("data")).toString();
            appendLine(out, report, TEXT_INFO);
            store.loadFromDatabase();
            updateStatus();
            appendLine(out, "✅  Import complete. Structures reloaded.", TEXT_SUCCESS);
        }));

        controls.add(reloadBtn);
        controls.add(reimportBtn);

        assembleModulePanel(panel, controls, out);
        return panel;
    }

    private JPanel buildStructuresPanel() {
        JPanel panel = makeModulePanel("🧱  Data Structures Demo");
        JTextArea out = makeOutputArea();
        JPanel controls = makeControlsCard("Operations");
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        String[] ops = {
            "1. Location lookup by ID (Hash Table)",
            "2. Location lookup by name (AVL Tree)",
            "3. Resource lookup by ID (B-Tree)",
            "4. Service request lookup by ID (BST)",
            "5. FIFO queue walk-through",
            "6. Circular queue wrap-around demo",
            "7. Deque urgent-insertion demo",
            "8. Priority queue / heap dispatch order"
        };
        JComboBox<String> opCombo = makeComboBox(ops);

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        inputRow.setBackground(BG_CARD2);
        JLabel idLabel = new JLabel("ID / Name:");
        idLabel.setFont(FONT_BODY);
        idLabel.setForeground(TEXT_SECONDARY);
        JTextField idField = makeTextField("e.g. L001, V001, Q001", 16);

        JButton runBtn = makeAccentButton("▶  Run");
        runBtn.addActionListener(e -> {
            int sel = opCombo.getSelectedIndex() + 1;
            String input = idField.getText().trim();
            runAsync(out, () -> structureOperation(out, sel, input));
        });

        inputRow.add(idLabel);
        inputRow.add(idField);
        inputRow.add(runBtn);

        JPanel row0 = makePaddedRow();
        JLabel comboLabel = new JLabel("Select operation:");
        comboLabel.setFont(FONT_BODY);
        comboLabel.setForeground(TEXT_SECONDARY);
        row0.add(comboLabel);
        row0.add(Box.createHorizontalStrut(8));
        row0.add(opCombo);

        controls.add(Box.createVerticalStrut(8));
        controls.add(row0);
        controls.add(Box.createVerticalStrut(8));
        JPanel inputWrap = makePaddedRow();
        inputWrap.add(inputRow);
        controls.add(inputWrap);
        controls.add(Box.createVerticalStrut(8));

        assembleModulePanel(panel, controls, out);
        return panel;
    }

    private void structureOperation(JTextArea out, int sel, String input) {
        switch (sel) {
            case 1 -> {
                appendLine(out, "[ Hash Table: Location by ID = " + input + " ]", ACCENT);
                Location loc = store.locationById().get(input);
                appendLine(out, loc != null ? "✅  " + loc : "❌  Not found.", loc != null ? TEXT_SUCCESS : TEXT_ERROR);
            }
            case 2 -> {
                appendLine(out, "[ AVL Tree: Location by name = " + input + " ]", ACCENT);
                Location loc = store.locationByName().search(input);
                appendLine(out, loc != null ? "✅  " + loc : "❌  Not found.", loc != null ? TEXT_SUCCESS : TEXT_ERROR);
                appendLine(out, String.format("   AVL height: %d  (n=%d, theoretical min ~%d)",
                    store.locationByName().height(), store.locationByName().size(),
                    (int) Math.ceil(Math.log(store.locationByName().size() + 1) / Math.log(2))), TEXT_INFO);
            }
            case 3 -> {
                appendLine(out, "[ B-Tree: Resource by ID = " + input + " ]", ACCENT);
                Resource r = store.resourceIndex().search(input);
                appendLine(out, r != null ? "✅  " + r : "❌  Not found.", r != null ? TEXT_SUCCESS : TEXT_ERROR);
                appendLine(out, "   B-tree height (pages): " + store.resourceIndex().height(), TEXT_INFO);
            }
            case 4 -> {
                appendLine(out, "[ BST: ServiceRequest by ID = " + input + " ]", ACCENT);
                ServiceRequest r = store.requestByIdBst().search(input);
                appendLine(out, r != null ? "✅  " + r : "❌  Not found.", r != null ? TEXT_SUCCESS : TEXT_ERROR);
                appendLine(out, String.format("   BST height: %d  (n=%d) — unbalanced",
                    store.requestByIdBst().height(), store.requestByIdBst().size()), TEXT_INFO);
            }
            case 5 -> {
                appendLine(out, "[ FIFO Queue walk-through ]", ACCENT);
                Queue<ServiceRequest> queue = store.buildFifoQueue();
                appendLine(out, "Queue size = " + queue.size() + ". First 5 in submission order:", TEXT_INFO);
                for (int i = 0; i < 5 && !queue.isEmpty(); i++) {
                    appendLine(out, "  " + queue.dequeue(), TEXT_PRIMARY);
                }
            }
            case 6 -> {
                appendLine(out, "[ Circular Queue wrap-around demo ]", ACCENT);
                CircularQueue<ServiceRequest> cq = store.buildCircularQueue();
                appendLine(out, String.format("capacity=%d, size=%d", cq.capacity(), cq.size()), TEXT_INFO);
                appendLine(out, "Dequeue 3:", TEXT_SECONDARY);
                for (int i = 0; i < 3; i++) appendLine(out, "  dequeued: " + cq.dequeue(), TEXT_PRIMARY);
                for (int i = 0; i < 3 && i < store.requests().size(); i++) cq.enqueue(store.requests().get(i));
                appendLine(out, "Re-enqueued 3. size=" + cq.size() + ", peek=" + cq.peek(), TEXT_SUCCESS);
            }
            case 7 -> {
                appendLine(out, "[ Deque urgent-insertion demo ]", ACCENT);
                Deque<ServiceRequest> deque = new Deque<>();
                int shown = 0;
                for (ServiceRequest r : store.requests()) {
                    if (shown++ >= 6) break;
                    if (r.getUrgency() >= 4) {
                        deque.addFront(r);
                        appendLine(out, "  URGENT → addFront: " + r.getRequestId() + " (urgency=" + r.getUrgency() + ")", new Color(0xEF9A9A));
                    } else {
                        deque.addRear(r);
                        appendLine(out, "  normal → addRear:  " + r.getRequestId() + " (urgency=" + r.getUrgency() + ")", TEXT_SECONDARY);
                    }
                }
                appendLine(out, "Front (urgent-first): " + deque.peekFront(), TEXT_SUCCESS);
            }
            case 8 -> {
                appendLine(out, "[ Priority Queue / Heap dispatch order ]", ACCENT);
                BinaryHeap<ServiceRequest> heap = store.buildDispatchHeap(store.demoReferenceTime());
                appendLine(out, "Heap size=" + heap.size() + ". Top 5 (most urgent first):", TEXT_INFO);
                for (int i = 0; i < 5 && !heap.isEmpty(); i++) {
                    ServiceRequest sr = heap.extractRoot();
                    appendLine(out, String.format("  #%d  %s  urgency=%d  %s", i+1, sr.getRequestId(), sr.getUrgency(), sr.getCategory()), TEXT_PRIMARY);
                }
            }
        }
    }

    private JPanel buildSearchSortPanel() {
        JPanel panel = makeModulePanel("🔍  Search & Sort Lab");
        JTextArea out = makeOutputArea();

        JPanel topRow = makeControlsCard("Search: Linear vs Binary");
        topRow.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        JTextField searchField = makeTextField("location name to search", 22);
        JButton searchBtn = makeAccentButton("🔍  Run Comparison");
        searchBtn.addActionListener(e -> {
            String target = searchField.getText().trim();
            if (target.isEmpty()) { appendLine(out, "⚠  Please enter a name.", TEXT_ERROR); return; }
            runAsync(out, () -> {
                appendLine(out, "[ Linear vs Binary Search: \"" + target + "\" ]", ACCENT);
                String[] names = namesOf();
                long t0 = System.nanoTime();
                int li = LinearSearch.search(names, target, Comparator.naturalOrder());
                long t1 = System.nanoTime();
                String[] sorted = names.clone();
                MergeSort.sort(sorted);
                long t2 = System.nanoTime();
                int bi = BinarySearch.search(sorted, target, Comparator.naturalOrder());
                long t3 = System.nanoTime();
                appendLine(out, String.format("  Linear search:  index=%-4d  time=%.3f µs", li, (t1-t0)/1000.0), TEXT_INFO);
                appendLine(out, String.format("  Binary search:  index=%-4d  time=%.3f µs  (on pre-sorted array)", bi, (t3-t2)/1000.0), TEXT_SUCCESS);
                if (li >= 0) appendLine(out, "  Found: " + names[li], TEXT_PRIMARY);
                else appendLine(out, "  Not found in location names.", TEXT_ERROR);
            });
        });
        topRow.add(new JLabel("Name:"));
        topRow.add(searchField);
        topRow.add(searchBtn);

        JPanel botRow = makeControlsCard("Sort: Service Requests by Urgency");
        botRow.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        String[] algNames = {"Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort"};
        JComboBox<String> algCombo = makeComboBox(algNames);
        JButton sortBtn = makeAccentButton("⬇  Sort Now");
        sortBtn.addActionListener(e -> {
            int alg = algCombo.getSelectedIndex() + 1;
            runAsync(out, () -> runSort(out, alg, algNames[alg-1]));
        });
        JButton allBtn = makeSecondaryButton("⬇⬇  Run All Sorts");
        allBtn.addActionListener(e -> runAsync(out, () -> {
            for (int i = 0; i < 4; i++) runSort(out, i+1, algNames[i]);
        }));
        botRow.add(algCombo);
        botRow.add(sortBtn);
        botRow.add(allBtn);

        JPanel controls = new JPanel(new GridLayout(2, 1, 0, 12));
        controls.setBackground(BG_DARK);
        controls.add(topRow);
        controls.add(botRow);

        assembleModulePanel(panel, controls, out);
        return panel;
    }

    private void runSort(JTextArea out, int alg, String name) {
        appendLine(out, "[ " + name + " on " + store.requests().size() + " requests ]", ACCENT);
        ServiceRequest[] arr = store.requests().toArray(ServiceRequest[]::new);
        Comparator<ServiceRequest> cmp = Comparator.comparingInt(ServiceRequest::getUrgency).reversed();
        long t0 = System.nanoTime();
        switch (alg) {
            case 1 -> SelectionSort.sort(arr, cmp);
            case 2 -> InsertionSort.sort(arr, cmp);
            case 3 -> MergeSort.sort(arr, cmp);
            case 4 -> QuickSort.sort(arr, cmp);
        }
        long t1 = System.nanoTime();
        appendLine(out, String.format("  ✅  Sorted %d requests in %.3f ms.", arr.length, (t1-t0)/1_000_000.0), TEXT_SUCCESS);
        appendLine(out, "  Top 5 by urgency:", TEXT_INFO);
        for (int i = 0; i < 5 && i < arr.length; i++) {
            appendLine(out, String.format("    #%d  %s  urgency=%d  %s", i+1, arr[i].getRequestId(), arr[i].getUrgency(), arr[i].getCategory()), TEXT_PRIMARY);
        }
    }

    private String[] namesOf() {
        String[] n = new String[store.locations().size()];
        for (int i = 0; i < n.length; i++) n[i] = store.locations().get(i).getName();
        return n;
    }

    private JPanel buildGraphPanel() {
        JPanel panel = makeModulePanel("🗺  Graph Engine");
        JTextArea out = makeOutputArea();
        JPanel controls = makeControlsCard("Graph Algorithm Controls");
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JPanel fieldRow = makePaddedRow();
        JLabel srcLabel = new JLabel("Source ID:");
        srcLabel.setFont(FONT_BODY); srcLabel.setForeground(TEXT_SECONDARY);
        JTextField srcField = makeTextField("e.g. L001", 10);
        JLabel dstLabel = new JLabel("Dest ID:");
        dstLabel.setFont(FONT_BODY); dstLabel.setForeground(TEXT_SECONDARY);
        JTextField dstField = makeTextField("e.g. L010 (Dijkstra only)", 20);
        fieldRow.add(srcLabel); fieldRow.add(srcField);
        fieldRow.add(Box.createHorizontalStrut(16));
        fieldRow.add(dstLabel); fieldRow.add(dstField);

        JPanel btnRow = makePaddedRow();
        JButton bfsBtn     = makeAccentButton("BFS");
        JButton dfsBtn     = makeAccentButton("DFS");
        JButton dijBtn     = makeAccentButton("Dijkstra");
        JButton primBtn    = makeSecondaryButton("Prim MST");
        JButton kruskalBtn = makeSecondaryButton("Kruskal MST");

        bfsBtn.addActionListener(e -> runAsync(out, () -> {
            String src = srcField.getText().trim();
            appendLine(out, "[ BFS from " + src + " ]", ACCENT);
            DynamicArray<String> order = BFS.traverse(store.roadNetwork(), src);
            appendLine(out, "Visited " + order.size() + " locations: " + truncate(order.toString(), 300), TEXT_SUCCESS);
        }));
        dfsBtn.addActionListener(e -> runAsync(out, () -> {
            String src = srcField.getText().trim();
            appendLine(out, "[ DFS from " + src + " ]", ACCENT);
            DynamicArray<String> order = DFS.traverse(store.roadNetwork(), src);
            appendLine(out, "Visited " + order.size() + " locations: " + truncate(order.toString(), 300), TEXT_SUCCESS);
        }));
        dijBtn.addActionListener(e -> runAsync(out, () -> {
            String src = srcField.getText().trim(), dst = dstField.getText().trim();
            appendLine(out, "[ Dijkstra: " + src + " → " + dst + " ]", ACCENT);
            Dijkstra.Result<String> res = Dijkstra.run(store.roadNetwork(), src);
            Double dist = res.distances.get(dst);
            if (dist == null) appendLine(out, "❌  No path found.", TEXT_ERROR);
            else {
                appendLine(out, String.format("✅  Shortest cost: %.3f", dist), TEXT_SUCCESS);
                appendLine(out, "   Path: " + res.pathTo(dst), TEXT_INFO);
            }
        }));
        primBtn.addActionListener(e -> runAsync(out, () -> {
            String src = srcField.getText().trim();
            appendLine(out, "[ Prim MST from " + src + " ]", ACCENT);
            MstResult<String> res = Prim.run(store.roadNetwork(), src);
            printMst(out, res);
        }));
        kruskalBtn.addActionListener(e -> runAsync(out, () -> {
            appendLine(out, "[ Kruskal MST ]", ACCENT);
            MstResult<String> res = Kruskal.run(store.roadNetwork());
            printMst(out, res);
        }));

        btnRow.add(bfsBtn); btnRow.add(dfsBtn); btnRow.add(dijBtn);
        btnRow.add(Box.createHorizontalStrut(16));
        btnRow.add(primBtn); btnRow.add(kruskalBtn);

        controls.add(Box.createVerticalStrut(8));
        controls.add(fieldRow);
        controls.add(Box.createVerticalStrut(8));
        controls.add(btnRow);
        controls.add(Box.createVerticalStrut(8));

        assembleModulePanel(panel, controls, out);
        return panel;
    }

    private void printMst(JTextArea out, MstResult<String> result) {
        appendLine(out, "MST edges (" + result.edges.size() + "):", TEXT_INFO);
        int shown = 0;
        for (MstEdge<String> edge : result.edges) {
            if (shown++ < 20) appendLine(out, "  " + edge, TEXT_PRIMARY);
        }
        if (result.edges.size() > 20) appendLine(out, "  ... (" + (result.edges.size()-20) + " more)", TEXT_SECONDARY);
        appendLine(out, String.format("✅  Total network cost: %.3f", result.totalCost), TEXT_SUCCESS);
    }

    private JPanel buildOptimisationPanel() {
        JPanel panel = makeModulePanel("⚡  Optimisation Engine");
        JTextArea out = makeOutputArea();
        JPanel controls = makeControlsCard("Operations");
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

        JButton greedyBtn   = makeAccentButton("🎯  Greedy Resource Assignment");
        JButton knapsackBtn = makeSecondaryButton("⚖  Greedy vs DP Knapsack");

        greedyBtn.addActionListener(e -> runAsync(out, () -> {
            appendLine(out, "[ Greedy Priority-Based Resource Assignment ]", ACCENT);
            DynamicArray<ServiceRequest> pending = new DynamicArray<>();
            for (ServiceRequest r : store.requests())
                if (r.getStatus() == RequestStatus.NEW) pending.add(r);
            DynamicArray<Resource> available = new DynamicArray<>();
            for (Resource r : store.resources())
                if (r.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE) available.add(r);

            appendLine(out, String.format("  Pending NEW requests: %d   |   Available resources: %d", pending.size(), available.size()), TEXT_INFO);
            GreedyResourceAssignment.Result result = GreedyResourceAssignment.assign(pending, available, store.demoReferenceTime());

            appendLine(out, String.format("✅  Assigned %d / %d requests. Unassigned: %d",
                result.assigned().size(), pending.size(), result.unassigned().size()), TEXT_SUCCESS);

            int show = Math.min(10, result.assigned().size());
            for (int i = 0; i < show; i++) {
                var a = result.assigned().get(i);
                store.resourceDao().updateAvailability(a.resource().getResourceId(), AvailabilityStatus.BUSY);
                auditLog.record("ASSIGN_RESOURCE", "resources", a.resource().getResourceId(), "AVAILABLE", "BUSY");
                store.serviceRequestDao().updateStatus(a.request().getRequestId(), RequestStatus.ASSIGNED);
                auditLog.record("ASSIGN_REQUEST", "service_requests", a.request().getRequestId(), "NEW", "ASSIGNED");
                appendLine(out, String.format("  %s → %s", a.request().getRequestId(), a.resource().getResourceId()), TEXT_PRIMARY);
            }
            if (result.assigned().size() > show)
                appendLine(out, "  ... (" + (result.assigned().size()-show) + " more assignments)", TEXT_SECONDARY);

            if (!result.assigned().isEmpty()) {
                store.loadFromDatabase();
                updateStatus();
                appendLine(out, "(" + result.assigned().size()*2 + " audit events recorded — check Audit Log panel)", TEXT_INFO);
            }
        }));

        knapsackBtn.addActionListener(e -> runAsync(out, () -> {
            appendLine(out, "[ Greedy vs DP Knapsack Counterexample ]", ACCENT);
            KnapsackItem[] items = GreedyKnapsack.counterexample();
            int cap = GreedyKnapsack.COUNTEREXAMPLE_CAPACITY;
            GreedyKnapsack.Result greedy = GreedyKnapsack.solve(items, cap);
            Knapsack.Result dp = Knapsack.solve(items, cap);
            appendLine(out, "Items: A(w=10, v=60)  B(w=20, v=100)  C(w=30, v=120)   Capacity=" + cap, TEXT_INFO);
            appendLine(out, "Greedy (by value/weight ratio) picks: " + greedy.selected() + "  total value=" + greedy.totalValue(), new Color(0xFFB74D));
            appendLine(out, "DP (0/1 optimal) picks:               " + dp.selected()     + "  total value=" + dp.totalValue(),     TEXT_SUCCESS);
            appendLine(out, "⚠  Greedy is SUBOPTIMAL here by " + (dp.totalValue()-greedy.totalValue()) + " value points.", TEXT_ERROR);
            appendLine(out, "   → This is the required counterexample proving Greedy ≠ Optimal for 0/1 Knapsack.", TEXT_SECONDARY);
        }));

        controls.add(greedyBtn);
        controls.add(knapsackBtn);

        assembleModulePanel(panel, controls, out);
        return panel;
    }

    private JPanel buildAuditPanel() {
        JPanel panel = makeModulePanel("📋  Audit / Undo Log");

        String[] cols = {"#", "Event Type", "Table", "Entity ID", "Before", "After", "Created At"};
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildStyledTable(tableModel);
        JScrollPane tableScroll = makeScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(0, 300));

        JTextArea out = makeOutputArea();
        out.setPreferredSize(new Dimension(0, 140));

        JPanel controls = makeControlsCard("Undo Log Controls");
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

        JButton refreshBtn = makeAccentButton("↺  Refresh Log");
        JButton undoBtn    = makeSecondaryButton("↩  Undo Last Action");
        JLabel stackSize   = new JLabel("Stack: 0");
        stackSize.setFont(FONT_BODY);
        stackSize.setForeground(TEXT_INFO);

        Runnable refresh = () -> SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            try {
                var events = store.auditEventDao().findAll();
                int row = 1;
                for (var e : events) {
                    tableModel.addRow(new Object[]{
                        row++, e.getEventType(), e.getEntityTable(),
                        e.getEntityId(), e.getBeforeState(), e.getAfterState(),
                        e.getCreatedAt()
                    });
                }
            } catch (Exception ex) {
                appendLine(out, "Error loading audit events: " + ex.getMessage(), TEXT_ERROR);
            }
            stackSize.setText("Stack: " + auditLog.size());
        });

        refreshBtn.addActionListener(e -> {
            appendLine(out, "[ Refreshing audit log... ]", ACCENT);
            refresh.run();
            appendLine(out, "✅  Loaded " + tableModel.getRowCount() + " events.", TEXT_SUCCESS);
        });

        undoBtn.addActionListener(e -> runAsync(out, () -> {
            if (!auditLog.canUndo()) {
                appendLine(out, "❌  Nothing to undo.", TEXT_ERROR);
            } else {
                var undone = auditLog.undoLast();
                revertEntityState(undone);
                store.loadFromDatabase();
                updateStatus();
                appendLine(out, "✅  Undid: " + undone, TEXT_SUCCESS);
                refresh.run();
            }
        }));

        controls.add(refreshBtn);
        controls.add(undoBtn);
        controls.add(Box.createHorizontalStrut(20));
        controls.add(stackSize);

        JPanel centre = new JPanel(new BorderLayout(0, 12));
        centre.setBackground(BG_DARK);
        centre.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        centre.add(controls,          BorderLayout.NORTH);
        centre.add(tableScroll,       BorderLayout.CENTER);
        centre.add(makeScrollPane(out), BorderLayout.SOUTH);

        panel.add(centre, BorderLayout.CENTER);
        return panel;
    }

    private void revertEntityState(gh.edu.campushub.model.AuditEvent event) {
        switch (event.getEntityTable()) {
            case "resources" -> store.resourceDao().updateAvailability(
                    event.getEntityId(), AvailabilityStatus.valueOf(event.getBeforeState()));
            case "service_requests" -> store.serviceRequestDao().updateStatus(
                    event.getEntityId(), RequestStatus.valueOf(event.getBeforeState()));
        }
    }

    private JPanel buildPerformancePanel() {
        JPanel panel = makeModulePanel("📊  Performance Experiment Lab");
        JTextArea out = makeOutputArea();

        JPanel controls = makeControlsCard("Experiment Controls");
        controls.setLayout(new BorderLayout(8, 8));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        btnRow.setBackground(BG_CARD2);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        progress.setString("Ready");
        progress.setForeground(ACCENT);
        progress.setBackground(BG_CARD2);
        progress.setFont(FONT_SMALL);

        JButton runAllBtn = makeAccentButton("▶▶  Run All Experiments (3× each)");
        runAllBtn.addActionListener(e -> {
            runAllBtn.setEnabled(false);
            progress.setString("Running...");
            progress.setValue(10);
            PrintStream oldOut = System.out;
            System.setOut(new PrintStream(new TextAreaOutputStream(out)));
            runAsync(out, () -> {
                appendLine(out, "[ Performance Experiment Lab — running all 6 experiments ]", ACCENT);
                appendLine(out, "Results saved to results/*.csv and algorithm_runs table.\n", TEXT_INFO);
                String[] names = {"Search", "Sort", "Hash", "BST vs AVL", "Heap", "Graph"};
                int total = names.length;
                try {
                    java.nio.file.Files.createDirectories(java.nio.file.Path.of("results"));
                    PerformanceLab.searchComparison(store);
                    updateProgressBar(progress, 1, total, names[0]);
                    PerformanceLab.sortingComparison(store);
                    updateProgressBar(progress, 2, total, names[1]);
                    PerformanceLab.hashLoadFactor(store);
                    updateProgressBar(progress, 3, total, names[2]);
                    PerformanceLab.treeComparison(store);
                    updateProgressBar(progress, 4, total, names[3]);
                    PerformanceLab.heapDispatch(store);
                    updateProgressBar(progress, 5, total, names[4]);
                    PerformanceLab.graphAlgorithms(store);
                    updateProgressBar(progress, 6, total, names[5]);
                    appendLine(out, "\n✅  All experiments complete!", TEXT_SUCCESS);
                } catch (Exception ex) {
                    appendLine(out, "Error: " + ex.getMessage(), TEXT_ERROR);
                } finally {
                    System.setOut(oldOut);
                }
                SwingUtilities.invokeLater(() -> {
                    progress.setValue(100);
                    progress.setString("All done!");
                    runAllBtn.setEnabled(true);
                });
            });
        });

        btnRow.add(runAllBtn);

        controls.add(btnRow,    BorderLayout.CENTER);
        controls.add(progress,  BorderLayout.SOUTH);

        assembleModulePanel(panel, controls, out);
        return panel;
    }

    private void updateProgressBar(JProgressBar bar, int done, int total, String label) {
        SwingUtilities.invokeLater(() -> {
            bar.setValue((int)(done * 100.0 / total));
            bar.setString(label + " ✓");
        });
    }

    private JPanel makeModulePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(BG_DARK);
        titleBar.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_HEADER);
        titleLbl.setForeground(ACCENT);
        titleBar.add(titleLbl, BorderLayout.WEST);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        titleBar.add(sep, BorderLayout.SOUTH);

        panel.add(titleBar, BorderLayout.NORTH);
        return panel;
    }

    private void assembleModulePanel(JPanel panel, JPanel controls, JTextArea out) {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        body.add(controls,          BorderLayout.NORTH);
        body.add(makeScrollPane(out), BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        if (title != null && !title.isEmpty()) {
            card.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(BORDER_COLOR, 1, true), " " + title + " ",
                    TitledBorder.LEFT, TitledBorder.TOP, FONT_SMALL, TEXT_SECONDARY),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        }
        return card;
    }

    private JPanel makeControlsCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD2);
        card.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(BORDER_COLOR, 1, true), " " + title + " ",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_SMALL, TEXT_SECONDARY),
            BorderFactory.createEmptyBorder(4, 8, 6, 8)
        ));
        return card;
    }

    private JPanel makePaddedRow() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(BG_CARD2);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return row;
    }

    private JTextArea makeOutputArea() {
        JTextArea area = new JTextArea();
        area.setFont(FONT_MONO);
        area.setBackground(new Color(0x0B1120));
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(ACCENT);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        area.setText("▌ Output console — results appear here.\n");
        return area;
    }

    private JScrollPane makeScrollPane(JComponent component) {
        JScrollPane sp = new JScrollPane(component);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(component.getBackground());
        sp.setBorder(new LineBorder(BORDER_COLOR, 1));
        sp.getVerticalScrollBar().setBackground(BG_SIDEBAR);
        sp.getHorizontalScrollBar().setBackground(BG_SIDEBAR);
        return sp;
    }

    private JButton makeAccentButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = getModel().isRollover()
                    ? new GradientPaint(0, 0, ACCENT_HOVER, 0, getHeight(), ACCENT)
                    : new GradientPaint(0, 0, ACCENT, 0, getHeight(), ACCENT.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_NAV);
        btn.setForeground(new Color(0x1A1B2E));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        btn.setOpaque(false);
        return btn;
    }

    private JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BG_CARD : BG_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_NAV);
        btn.setForeground(TEXT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        btn.setOpaque(false);
        return btn;
    }

    private JTextField makeTextField(String placeholder, int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(BG_CARD2);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        tf.setToolTipText(placeholder);
        tf.setText(placeholder);
        tf.setForeground(TEXT_SECONDARY);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(TEXT_SECONDARY);
                }
            }
        });
        return tf;
    }

    private <T> JComboBox<T> makeComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setBackground(BG_CARD2);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? BG_CARD : BG_CARD2);
                setForeground(isSelected ? ACCENT : TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return cb;
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(BG_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(BG_CARD);
        table.setSelectionForeground(ACCENT);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, val, isSel, hasFocus, row, col);
                setBackground(isSel ? BG_CARD : (row % 2 == 0 ? BG_DARK : TABLE_ROW_ALT));
                setForeground(isSel ? ACCENT : TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_PRIMARY);
        header.setFont(FONT_SMALL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        return table;
    }

    private void runAsync(JTextArea out, Runnable task) {
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() { task.run(); return null; }
            protected void done() {
                try { get(); } catch (Exception e) {
                    appendLine(out, "❌  Error: " + e.getCause().getMessage(), TEXT_ERROR);
                }
            }
        }.execute();
    }

    private void appendLine(JTextArea area, String text, Color color) {
        Runnable r = () -> {
            area.append(text + "\n");
            area.setCaretPosition(area.getDocument().getLength());
        };
        if (SwingUtilities.isEventDispatchThread()) r.run();
        else SwingUtilities.invokeLater(r);
    }

    private String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }

    static class TextAreaOutputStream extends OutputStream {
        private final JTextArea area;
        private final StringBuilder buf = new StringBuilder();

        TextAreaOutputStream(JTextArea area) { this.area = area; }

        public void write(int b) {
            buf.append((char) b);
            if (b == '\n') flush();
        }

        public void flush() {
            final String line = buf.toString();
            buf.setLength(0);
            SwingUtilities.invokeLater(() -> {
                area.append(line);
                area.setCaretPosition(area.getDocument().getLength());
            });
        }
    }
}

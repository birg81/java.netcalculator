package view;
import java.awt.*;
import javax.swing.*;
import controller.IPv4;
public class WinApp {
	private final JFrame win = new JFrame();
	private final Container ctx = win.getContentPane();
	private final IPv4 ip = new IPv4();
	private JPanel infoPane = new JPanel(), multiPane = new JPanel();
	public WinApp() {
		init();
	}
	private void redraw() {
		infoPane.removeAll();
		infoPane.revalidate();
		multiPane.removeAll();
		multiPane.revalidate();
		infoPane();
		multiPane();
	}
	private JPanel ipPane() {
		final JPanel ipPane = new JPanel();
		ipPane.setLayout(new GridBagLayout());
		final JSpinner cidr = new JSpinner(new SpinnerNumberModel(ip.cidr(), 2, 30, 1));
		final JSpinner[] fields = {
			new JSpinner(new SpinnerNumberModel(ip.field(0), 0, 255, 1)),
			new JSpinner(new SpinnerNumberModel(ip.field(1), 0, 255, 1)),
			new JSpinner(new SpinnerNumberModel(ip.field(2), 0, 255, 1)),
			new JSpinner(new SpinnerNumberModel(ip.field(3), 0, 255, 1))
		};
		for (int i = 0; i < fields.length; i++) {
			// fields[i].setPreferredSize();
			final int j = i;
			fields[i].addChangeListener(e -> {
				ip.field(j, (int) fields[j].getValue());
				if (j == 0) {
					ip.defaultMask();
					cidr.setValue(ip.cidr());
				}
				redraw();
			});
			ipPane.add(fields[i]);
			ipPane.add(new JLabel(i < 3 ? "." : "/"));
		}
		cidr.addChangeListener(e -> {
			ip.cidr((int) cidr.getValue());
			redraw();
		});
		ipPane.add(cidr);
		return ipPane;
	}
	private void infoPane() {
		infoPane.add(new JLabel("Networks(s): %d\t Host(s): %d".formatted(ip.networks(),ip.hosts())));
	}
	private JPanel addrPane(String name, int[] addr) {
		JPanel addrPane = new JPanel();
		for (int i = 0; i < 4; i++) {
			addrPane.add(new JLabel("%d".formatted(addr[i])));
			addrPane.add(new JLabel("%s".formatted(i < 3 ? "." : name)));
		}
		return addrPane;
	}
	private void multiPane() {
		multiPane.setLayout(new GridLayout(5, 1));
		multiPane.add(addrPane("Class: %s".formatted(ip.ipClass()), ip.mask()));
		multiPane.add(addrPane("Network", ip.networkAddr()));
		multiPane.add(addrPane("Broadcast", ip.broadcastAddr()));
		multiPane.add(addrPane("Gateway 0", ip.gateway0()));
		multiPane.add(addrPane("Gateway 1", ip.gateway1()));
	}
	public void init() {
		win.setTitle("IP Calculator");
		win.setResizable(false);
		win.setSize(300, 200);
		win.setLocationRelativeTo(null);
		win.setIconImage(Toolkit.getDefaultToolkit().getImage("img/favicon.png"));
		win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		ctx.setLayout(new BorderLayout());
		redraw();
		ctx.add(infoPane, BorderLayout.SOUTH);
		ctx.add(multiPane, BorderLayout.CENTER);
		ctx.add(ipPane(), BorderLayout.NORTH);
		win.setVisible(true);
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				new WinApp();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
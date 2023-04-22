package controller;
import model.IPV4classKind;
public class IPv4 {
	private int ip, cidr;
	public IPv4(int[] ip) {
		this.ip = getInt(ip);
		defaultMask();
	}
	public IPv4() {
		this(new int[]{192, 168, 1, 101});
	}
	public IPv4(int[] ip, int cidr) {
		this(ip);
		if (cidr > this.cidr && cidr < 31)
			this.cidr = cidr;
	}
	public int[] ip() {
		return fields(ip);
	}
	public void field(int i, int value) {
		if (i >= 0 && i < 4)
			ip = ip & ~(0xff << (3 - i) * 8) | (0xff & value) << (3 - i) * 8;
	}
	public int field(int i) {
		return 0xff & ip >> (3 - i) * 8;
	}
	public void ip(int[] addr) {
		if (addr.length == 4)
			ip = getInt(addr);
	}
	public int cidr() {
		return cidr;
	}
	public void cidr(int cidr) {
		if (cidr > 0 && cidr < 31)
			this.cidr = cidr;
	}
	public int[] mask() {
		return fields(snm());
	}
	public void defaultMask() {
		cidr = switch (ipClass()) {
			case A -> 8;
			case B -> 16;
			case C -> 24;
			default -> 30;
		};
	}
	public IPV4classKind ipClass() {
		return switch (ip >> 28 & 0b1111) {
			case 0b1111 -> IPV4classKind.E;
			case 0b1110 -> IPV4classKind.D;
			case 0b1101, 0b1100 -> IPV4classKind.C; // 0b110N
			case 0b1011, 0b1010, 0b1001, 0b1000 -> IPV4classKind.B; // 0b10NN
			default -> IPV4classKind.A; // 0b0NNN
		};
	}
	private static int[] fields(int addr) {
		return new int[]{addr >> 24 & 0xff, addr >> 16 & 0xff, addr >> 8 & 0xff, addr & 0xff};
	}
	private static int getInt(int[] addr) {
		return addr.length != 4
			? 0
			: (addr[0] & 0xff) << 24 |
				(addr[1] & 0xff) << 16 |
				(addr[2] & 0xff) << 8 |
				addr[3] & 0xff;
	}
	private int snm() {
		return -1 << 32 - cidr;
	}
	public int[] networkAddr() {
		return fields(ip & snm());
	}
	public int[] broadcastAddr() {
		return fields(ip | ~snm());
	}
	public int[] gateway0() {
		return fields((ip & snm()) + 1);
	}
	public int[] gateway1() {
		return fields((ip | ~snm()) - 1);
	}
	public int hosts() {
		return (1 << 32 - cidr) - 2;
	}
	public int networks() {
		int n = switch (ipClass()) {
			case A -> cidr - 1;
			case B -> cidr - 2;
			case C -> cidr - 3;
			default -> cidr - 4;
		};
		return n > 0 ? (1 << n) - 2 : 1;
	}
}
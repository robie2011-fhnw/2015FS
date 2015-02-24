package ch.fhnw.ds.networking.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

class UDPClient {
	public static void main(String args[]) throws Exception {
		String host = "localhost";

		try (DatagramSocket socket = new DatagramSocket()) {
			System.out.println("local addr: " + socket.getLocalAddress());
			System.out.println("local port: " + socket.getLocalPort());

			for (int i = 0; i < 5; i++) {
				InetAddress ia = InetAddress.getByName(host);
				String s = new Date().toString();

				DatagramPacket packet = new DatagramPacket(s.getBytes(),
						s.length(), ia, 4711);

				socket.send(packet);

				System.out.println("Weg ist es");

				Thread.sleep(1000);
			}
		}
	}
}

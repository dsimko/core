
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public final class Start {

	public static void main(final String[] args) {
//		System.setProperty("spring.security.strategy", "MODE_INHERITABLETHREADLOCAL");
		final Server server = new Server();
		final SocketConnector connector = new SocketConnector();

		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });

		final WebAppContext webAppContext = new WebAppContext();
		webAppContext.setServer(server);
		webAppContext.setContextPath("/");
		// webAppContext.setWar("src/main/webapp");
		webAppContext.setResourceBase("src/main/webapp");

		// START JMX SERVER
		// MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		// MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
		// server.getContainer().addEventListener(mBeanContainer);
		// mBeanContainer.start();

		server.addHandler(webAppContext);

		try {
			System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP"); // NOPMD by bednar on
			// 16.12.08 13:16
			server.start();

			while (System.in.available() == 0) {
				Thread.sleep(5000);
			}

			server.stop();
			server.join();
		} catch (final Exception e) {
			e.printStackTrace(); // NOPMD by bednar on 16.12.08 13:23
			System.exit(100); // NOPMD by bednar on 16.12.08 13:23
		}
	}

	private Start() {
	}
}
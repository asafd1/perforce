package perforce;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.perforce.p4java.PropertyDefs;
import com.perforce.p4java.client.IClient;
import com.perforce.p4java.client.IClientSummary;
import com.perforce.p4java.server.IServer;
import com.perforce.p4java.server.ServerFactory;

public class UpdateWorkspaces
{
	static HashMap<String, String> serversMapping = new HashMap<String, String>();

	public static void main(String[] args) throws Exception
	{
		prepareHashMap();
		doWork();
	}

	private static void prepareHashMap()
	{
		serversMapping.put("xxx", "xxx");
	}

	private static void doWork() throws Exception
	{

		IServer server = null;
		try
		{
			server = connect("1.1.1.1", 1666, "user");
			// updateClientHost(server, "TW", "asaf-test2");
			updateClients(server);
		}
		finally
		{
			disconnect(server);
		}

	}

	private static void updateClients(IServer server) throws Exception
	{
		List<IClientSummary> clients = server.getClients(null, null, 0);
		System.out.println("working on " + clients.size() + " workspaces\n");
		int i = 0;
		for (IClientSummary clientSummary : clients)
		{
			String host = clientSummary.getHostName();
			if (host != null && host.length() > 0)
			{
				String newHost = serversMapping.get(host.toLowerCase());

				if (newHost != null)
				{
					System.out.println("workspace " + clientSummary.getName() + " : setting host from " + host + " to "
						+ newHost);
					IClient client = server.getClient(clientSummary.getName());
					client.setHostName(newHost);
					client.update();

					i++;
				}
			}
		}
		System.out.println("\nUpdated " + i + " workspaces");
	}

	private static void disconnect(IServer server) throws Exception
	{
		if (server != null)
		{
			server.disconnect();
		}
	}

	private static IServer connect(String host, int port, String user) throws Exception
	{
		String serverUri = "p4java://" + host + ":" + port;

		Properties props = new Properties();
		props.setProperty(PropertyDefs.USER_NAME_KEY, user);
		props.setProperty(PropertyDefs.PASSWORD_KEY, user);

		IServer server = ServerFactory.getServer(serverUri, props);
		server.connect();

		return server;
	}
}

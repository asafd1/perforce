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
		serversMapping.put("dotnet-build", "ID1PREMBSD0173");
		serversMapping.put("i4w-build", "ID1PRENBSD0174");
		serversMapping.put("vm-collwin64-1", "ID1PRENBSD0178");
		serversMapping.put("vm-comp-build", "ID1PRENBSD0179");
		serversMapping.put("vm-comp-hudson", "ID1PRENBSD0180");
		serversMapping.put("vm-db-bso-build", "ID1PRENBSD0181");
		serversMapping.put("vm-db-db2-build", "ID1PRENBSD0182");
		serversMapping.put("vm-db-oracle-build", "ID1PRENBSD0183");
		serversMapping.put("vm-db-sds-build", "ID1PRENBSD0184");
		serversMapping.put("vm-db-sybase-build", "ID1PRENBSD0186");
		serversMapping.put("vm-infra-build3", "ID1PRENBSD0188");
		serversMapping.put("vm-insight-build", "ID1PRENBSD0189");
		serversMapping.put("buildwin1", "ID1PREKBSD0170");
		serversMapping.put("vm-tpm-build", "ID1PRENBSD0191");
	}

	private static void doWork() throws Exception
	{

		IServer server = null;
		try
		{
			server = connect("172.20.102.146", 1666, "adavid");
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

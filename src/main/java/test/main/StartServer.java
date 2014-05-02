package main;

import java.io.IOException;
import java.util.Properties;

import org.apache.thrift.transport.TTransportException;

import consistent.ConsistentTimeServer;
import consistent.ConsistentTimeServerWatcher;
import consistent.zookeeper.FailoverServer;
import consistent.zookeeper.HostPort;

public class StartServer {
    /**
     * �޸�hostPort�������StartServer,��һ�������Ļ���Ϊmaster��thrift client�ṩ<br/>
     * ��ȡ����������һ�µ�ʱ�����,����������StartServer��waitס��Ϊbackup-servers,<br/>
     * һ��master崻�/�˳������쳣ֹͣ����,backup-servers��ͨ��Zookeeper��Watcher<br/>
     * ͨ���ص�����(/GetConsistentTime/master��ʱ�ڵ㱻ɾ������NodeDataChanged�¼�)<br/>
     * notifyAll����waitס��StartServer���߳�����ѡ��(�����µ�/GetConsistentTime/master<br/>
     * ��ʱ�ڵ�,�������µ�thrift server��ip_port).������ʵ��master���˵������backup-servers��<br/>
     * �Զ�ѡ������һ���ٴ���Ϊmaster,�����ṩ���������Ч��.
     * 
     * @author thegodofwar
     * 
     */
	public static void main(String args[]) {
		//��������޸Ķ˿ں��������backup-servers
	    HostPort hostPort = new HostPort("127.0.0.1", 10086);//10086��10087��10088...
		Properties properties = new Properties();
		//FailoverServer����
		properties.setProperty(FailoverServer.BASE_ZNODE, "/GetConsistentTime");
		//Zookeeper����ip�Ͷ˿ں�
		properties.setProperty(FailoverServer.ZK_QUORUM, "127.0.0.1:2181");
		properties.setProperty(FailoverServer.SERVER_HOST, hostPort.getHost());
		properties.setProperty(FailoverServer.SERVER_PORT, String.valueOf(hostPort.getPort()));
		properties.setProperty(FailoverServer.SESSION_TIMEOUT, "5000");
		properties.setProperty(FailoverServer.CONNECT_RETRY_TIMES, String.valueOf(10));
		//ChronosServer����
		properties.setProperty(ConsistentTimeServer.MAX_THREAD, "1000");
		properties.setProperty(ConsistentTimeServer.ZK_ADVANCE_TIMESTAMP, "10000");
		ConsistentTimeServerWatcher consistentTimeServerWatcher = null;
		try {
			consistentTimeServerWatcher = new ConsistentTimeServerWatcher(properties, true);
		} catch (IOException e) {
		   e.printStackTrace();
		}
		ConsistentTimeServer consistentTimeServer = null;
		try {
			consistentTimeServer = new ConsistentTimeServer(consistentTimeServerWatcher);
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		consistentTimeServer.run();
//		
		
  }

}

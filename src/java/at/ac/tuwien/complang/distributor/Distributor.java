package at.ac.tuwien.complang.distributor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;

public interface Distributor extends Worker  {
	void register (Worker worker, String name) throws RemoteException;
}

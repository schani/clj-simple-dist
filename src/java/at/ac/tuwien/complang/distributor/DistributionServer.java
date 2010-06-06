package at.ac.tuwien.complang.distributor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;

public interface DistributionServer extends Remote {
	Serializable compute (Serializable function, Serializable args) throws RemoteException, NotImplementedException;
}

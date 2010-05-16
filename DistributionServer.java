package at.ac.tuwien.complang.distributer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;

public interface DistributionServer extends Remote {
	String compute (Serializable function, Serializable args) throws RemoteException;
	Serializable ask (String id) throws RemoteException;
}

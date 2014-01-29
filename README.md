ECE454Project2
==============

This is a Java project to implement a peer-to-peer file sharing network using the bitTorrent protocol

The peer to peer network file system is implemented using JAVA. This project specification attempts to cover all aspects of the network file system that is going to be developed. The bare minimum API that will be offered to application programmers will be of: 

int open(String filename, char operation);
int close(String filename);
int read(String filename, char buf[], int offset, int bufsize);
int write(String filename, char buf[], int offset, int bufsize);

To Run this *you need multiple projects as each project is one peer in the network*:

1) Please download the java project and open it in eclipse.

2) Open up PropertiesOfPeer class (ECE454Project2 -> src -> data -> PropertiesOfPeer)
  a) configure the ipAddress of this host (line 18)
  b) set the portNumber (must be a free port) (line 19)
  c) set the ip address and port numbers of other peers (other copies of this java project)  (line 35)
    (i) add additional entries to the ipaAddrPortNumMappingAll for additional peers

2) Open up the Peer class (ECE454Project2 -> src -> main -> Peer)

3) Run the project

4) Input
    join             (join the network)
    leave            (leave the network)
    addevice         (add another machine to the network)
    retiredevice     (remove the machine from the network)
    open             (open a file)
    close            (close a file)
    create           (create a file)
    logicalview      (all of the files on the network)
    remoteview       (physical files on other machines)
    delete           (remove file from the network)
    deleteall        (remove all of the files from the network)

For more information, concerns, and questions please e-mail charlieouyang@hotmail.com 

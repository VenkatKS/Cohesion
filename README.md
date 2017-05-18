# Cohesion
A distributed system with multiple independent nodes that uses Lamport's Distributed Algorithm to sustain coherence and fault tolerance.

Cohension is a toy distributed retail sales system that I wrote that utilizes Lamport's Distributed Algorithm to maintain synchronous fault tolerance and synchronous coherence between all the servers in play. The system itself is a transaction system, and consists of two groups of players: Servers and Clients.

## Servers
Represents the actual sales servers. They're the ones that handle the inventory. Servers, when booted, load up the inventory list from a text file (inventory.txt). Once it loads up the file, it establishes connections between itself and all the other Servers in the system using TCP connections. Once done, the servers are then open for business.

## Clients
Clients emulate the customer. Clients support three commands: "list", "purchase [username] [item] [quantity]", and "search [username]". Once you boot up the Client and give it a list of all servers, it establishes a connection with *one* of these servers. It does not change the Server it interacts with -- unless the server dies, at which point, the Client sends commands to the second server in the list (and so on). The Client closes when all the Servers are dead.

When you send a list command: the Client contacts a Server and gets back a list of inventory avaliable for purchase right now. The Server uses the Lamport Mutual Exclusion Algorithm to get the coherent inventory list and quantity and returns it (see next paragraph for more details). The Client then prints this list onto your screen.

When you send a purchase command: The Client contacts a Server and sends the information regarding the username, item purchased, and quantity purchased. If there's not enough of the item requested, the transaction is denied. If there is enough for the transaction to execute, the server first requests permission to change this distributed inventory data from the other Servers using Lamport's Distributed Mutual Exclusion algorithm. Once it gets the permission, the Server then records the username that purchased the item in its list and deducts the specified quantity from the avaliable quantity of that item. Once done, it informs the other servers of this change (to maintain coherency), and exits the critical section.

When you send a search command: The Client sends the specified username to a Server. Once again, the Server uses Lamport's algorithm to get the coherent data and returns it to the Client. The Client then prints out ALL the transactions performed under this undername, as well as the transaction ID (which is coherent across all distributed servers).

## Distributed Mutual Exclusion
The network is fault tolerant and does maintain cohension. At any time when there is no server in the critical section changing the data, ALL existing servers have the exact same data, with ZERO differences. Therefore, when a server crashes at any time, the data is still coherent and another server can step in. Any and all transactions done is reflected across all servers once executed. Simultaneous accesses also are ordered, so only one is executed and then propagated across all servers at a time.

## How To Run:
There are two different programs to reflect the symmetry specified above: clients and servers. Under Client/ and Server/, run javac and execute java Client and java Server. The programs then will each request IP information of other servers through stdin (exact instructions will be specified through stdout). After all information is provided, the Servers will give the "All good" message and start listening for client transactions. The clients, once all set up, will open a prompt and you can start entering your commands.

## Sample Run:
#### Server 1:
```
What's my server ID?  
1  
How many servers are there (including me)?  
3  
What's the path to the inventory file that I need to manage?  
inventory.txt  
Please enter the addresses of all servers (in order of server IDs -- i.e. Server 0's ID first, then Server 1's, etc.)!  
127.0.0.1:5000  
I'm assuming what you just entered is my IP info (due to my server ID)!  
127.0.0.1:5001  
External Server Registered!  
127.0.0.1:5002  
External Server Registered!  
Great! I'll set up connections now by sending Lamport Messages to the other servers to establish coherency between these servers.  
Awesome! I've talked to the other servers and we're on the same page. I'll update my inventory now.  
I'm all set! I can accept client connections now.  
```
#### Server 2:
```
What's my server ID?  
2  
How many servers are there (including me)?  
3  
What's the path to the inventory file that I need to manage?  
inventory.txt  
Please enter the addresses of all servers (in order of server IDs -- i.e. Server 0's ID first, then Server 1's, etc.)!  
127.0.0.1:5000  
External Server Registered!  
127.0.0.1:5001  
I'm assuming what you just entered is my IP info (due to my server ID)!  
127.0.0.1:5002  
External Server Registered!  
Great! I'll set up connections now by sending Lamport Messages to the other servers to establish coherency between these servers.  
Awesome! I've talked to the other servers and we're on the same page. I'll update my inventory now.  
I'm all set! I can accept client connections now.  
```
#### Server 3:
```
What's my server ID?  
3  
How many servers are there (including me)?  
3  
What's the path to the inventory file that I need to manage?  
inventory.txt  
Please enter the addresses of all servers (in order of server IDs -- i.e. Server 0's ID first, then Server 1's, etc.)!  
127.0.0.1:5000  
External Server Registered!  
127.0.0.1:5001  
External Server Registered!  
127.0.0.1:5002  
I'm assuming what you just entered is my IP info (due to my server ID)!  
Great! I'll set up connections now by sending Lamport Messages to the other servers to establish coherency between these servers.  
Awesome! I've talked to the other servers and we're on the same page. I'll update my inventory now.  
I'm all set! I can accept client connections now.  
```
#### Client
```
Please enter the number of servers that exist:  
3  
Please enter Server 0's IP:    
127.0.0.1:5000  
Please enter Server 1's IP:   
127.0.0.1:5001  
Please enter Server 2's IP:   
127.0.0.1:5002  

>> list  
ps4 17  
phone 100  
xbox 8  
laptop 15  
camera 10  

>> purchase venkat ps4 1  
You order has been placed, 1000000 venkat ps4 1  
  
>> purchase venkat ps4 100  
Not Available - Not enough items  
  
>> search venkat  
1000000, ps4, 1   
    
>> purchase venkat1 camera 5  
You order has been placed, 1000001 venkat1 camera 5  
  
>> list camera   
ps4 16  
phone 100  
xbox 8  
laptop 15  
camera 5  
  
>> search venkat1
1000001, camera, 5

>> purchase venkat1 phone 50
You order has been placed, 1000002 venkat1 phone 50

>> search venkat
1000000, ps4, 1

>> search venkat1
1000001, camera, 5
1000002, phone, 50

>> 
```
### Fault Tolerance Example

I'm still using the same run as above, but randomly kill servers 1 and 3.

#### Server 1:
```
What's my server ID?
1
How many servers are there (including me)?
3
What's the path to the inventory file that I need to manage?
inventory.txt
Please enter the addresses of all servers (in order of server IDs -- i.e. Server 0's ID first, then Server 1's, etc.)!
127.0.0.1:5000
I'm assuming what you just entered is my IP info (due to my server ID)!
127.0.0.1:5001
External Server Registered!
127.0.0.1:5002
External Server Registered!
Great! I'll set up connections now by sending Lamport Messages to the other servers to establish coherency between these servers.
Awesome! I've talked to the other servers and we're on the same page. I'll update my inventory now.
I'm all set! I can accept client connections now.

Process finished with exit code 130
```
#### Server 2:
```
What's my server ID?
2
How many servers are there (including me)?
3
What's the path to the inventory file that I need to manage?
inventory.txt
Please enter the addresses of all servers (in order of server IDs -- i.e. Server 0's ID first, then Server 1's, etc.)!
127.0.0.1:5000
External Server Registered!
127.0.0.1:5001
I'm assuming what you just entered is my IP info (due to my server ID)!
127.0.0.1:5002
External Server Registered!
Great! I'll set up connections now by sending Lamport Messages to the other servers to establish coherency between these servers.
Awesome! I've talked to the other servers and we're on the same page. I'll update my inventory now.
I'm all set! I can accept client connections now.
Oops! Looks like one of the other servers went offline. I'll update my list.
Oops! Looks like one of the other servers went offline. I'll update my list.
```
#### Server 3:
```
What's my server ID?
3
How many servers are there (including me)?
3
What's the path to the inventory file that I need to manage?
inventory.txt
Please enter the addresses of all servers (in order of server IDs -- i.e. Server 0's ID first, then Server 1's, etc.)!
127.0.0.1:5000
External Server Registered!
127.0.0.1:5001
External Server Registered!
127.0.0.1:5002
I'm assuming what you just entered is my IP info (due to my server ID)!
Great! I'll set up connections now by sending Lamport Messages to the other servers to establish coherency between these servers.
Awesome! I've talked to the other servers and we're on the same page. I'll update my inventory now.
I'm all set! I can accept client connections now.
Oops! Looks like one of the other servers went offline. I'll update my list.

Process finished with exit code 130
```

#### Client
```
... (same client as above, so history is truncated)
>> list
ps4 16
phone 50
xbox 8
laptop 15
camera 5

>> 
```
As shown, the ledger is coherent across all servers and the entire system is fault tolerant.

## Testing
Obviously, the run above is a very trivial and does not stress test the distributed algorithm's robustness at all. To that end, I've also added a testbench java file under Tests/ that actually creates a TON of Clients and spams the Servers to ensure that the cohesion and fault tolerance is maintained across all servers. Feel free to kill as many Servers as you want while running the stress test. It'll demonstrate the implementation's efficiacy. The default setting is to test 3 Servers running at localhost:5000, localhost:5001, and localhost:5002, but feel free to change Client.java to add more/remove some.

